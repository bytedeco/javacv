/*
 * Copyright (C) 2011,2012 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE |
                          CLK_ADDRESS_CLAMP | CLK_FILTER_NEAREST;

//const sampler_t linearSampler = CLK_NORMALIZED_COORDS_FALSE |
//                                CLK_ADDRESS_CLAMP | CLK_FILTER_LINEAR;

inline float4 readLinear(read_only image2d_t img, float2 xy) {
    float2 xy00 = floor(xy);
    float dx = xy.x - xy00.x;
    float dy = xy.y - xy00.y;
    float4 rgba  = (1-dx)*(1-dy)*read_imagef(img, sampler, xy00);
           rgba +=    dx *(1-dy)*read_imagef(img, sampler, xy00 + (float2)(1, 0));
           rgba += (1-dx)*   dy *read_imagef(img, sampler, xy00 + (float2)(0, 1));
           rgba +=    dx *   dy *read_imagef(img, sampler, xy00 + (float2)(1, 1));
    return rgba;
}

#pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable

inline void atomicAddFloat(global float* address, float val) {
    global int* address_as_int = (global int*)address;
    while (val != 0.0f) {
        val += as_float(atom_xchg(address_as_int, as_int(0.0f)));
        val  = as_float(atom_xchg(address_as_int, as_int(val)));
    }
}

// Bit Twiddling Hacks
// http://graphics.stanford.edu/~seander/bithacks.html
inline int ceilPow2(int v) {
    v--;
    v |= v >> 1;
    v |= v >> 2;
    v |= v >> 4;
    v |= v >> 8;
    v |= v >> 16;
    return ++v;
}

inline int reduceSumInt(float value, int i, int size, local void* scratch) {
    local float *scratchi = (local float*)scratch;
    scratchi[i] = value;
    barrier(CLK_LOCAL_MEM_FENCE);
    for (int offset = ceilPow2(size)/2; offset > 0; offset >>= 1) {
        if (i < offset && i + offset < size) {
            scratchi[i] += scratchi[i + offset];
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    return scratchi[0];
}

inline float reduceSumFloat(float value, int i, int size, local void* scratch) {
    local float *scratchf = (local float*)scratch;
    scratchf[i] = value;
    barrier(CLK_LOCAL_MEM_FENCE);
    for (int offset = ceilPow2(size)/2; offset > 0; offset >>= 1) {
        if (i < offset && i + offset < size) {
            scratchf[i] += scratchf[i + offset];
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    return scratchf[0];
}

struct InputData {
    int roiY, roiHeight;
    float zeroThreshold, outlierThreshold;
};

struct OutputData {
    int groupsFinished, dstCount, dstCountZero, dstCountOutlier;
    float srcDstDot[DOT_SIZE], dstDstDot[DOT_SIZE][DOT_SIZE];
};

inline void multiWarpColorTransform(read_only image2d_t srcImg, read_only image2d_t srcImg2, read_only image2d_t subImg,
        read_only image2d_t dotImg, write_only image2d_t transImg, write_only image2d_t dstImg, read_only image2d_t maskImg,
        int width, int height, constant float _H[][9], constant float _H2[][9], constant float/*4*/ _X[][16/*4*/], int size,
        constant struct InputData *inputData, global struct OutputData *outputData,
        bool haveSubImg, bool haveDotImg, bool haveTransImg, bool haveDstImg, bool haveMaskImg) {
    const int x = get_global_id(0), gx = get_group_id(0), lx = get_local_id(0), lsx = get_local_size(0);
    const int y = get_global_id(1), gy = get_group_id(1), ly = get_local_id(1), lsy = get_local_size(1);
    const int z = get_global_id(2), gz = get_group_id(2), lz = get_local_id(2), lsz = get_local_size(2);

    int dstCount = 0, dstCountZero = 0, dstCountOutlier = 0;
    float srcDstDot = 0, dstDstDot = 0;
    local float scratch[DOT_SIZE + 1][DOT_SIZE | 1][3];

    local float H[DOT_SIZE][9], H2[DOT_SIZE][9], X[DOT_SIZE][12];
    if (lx < size) {
        for (int j = 0; j < 9; j++) {
            H[lx][j] = _H[lx][j];
            if (_H2) H2[lx][j] = _H2[lx][j];
        }
        for (int j = 0; j < 12; j++) {
            if (_X) X[lx][j] = _X[lx][j];
        }
    }
    barrier(CLK_LOCAL_MEM_FENCE);

    for (int y = inputData->roiY; y < inputData->roiY + inputData->roiHeight; y++) {
        const int2 xy = (int2)(x, y);
        float4 dotRGB = 0, dstRGB = 0;

        if (x >= width) {
            goto skipPixel;
        }

        if (haveMaskImg) {
            if (read_imagei(maskImg, sampler, xy).x == 0) {
                goto skipPixel;
            } else {
                dstCount++;
            }
        }

        if (haveDotImg) {
            float zeroThreshold2    = inputData->zeroThreshold    * inputData->zeroThreshold;
            float outlierThreshold2 = inputData->outlierThreshold * inputData->outlierThreshold;

            dotRGB = read_imagef(dotImg, sampler, xy);
            float norm2 = dot(dotRGB.xyz, dotRGB.xyz);
            if (norm2 < zeroThreshold2) {
                dstCountZero++;
                goto skipPixel;
            } else if (outlierThreshold2 > 0 && norm2 > outlierThreshold2) {
                dstCountOutlier++;
                goto skipPixel;
            }
        }

        float u = H[lz][0]*x + H[lz][1]*y + H[lz][2];
        float v = H[lz][3]*x + H[lz][4]*y + H[lz][5];
        float w = H[lz][6]*x + H[lz][7]*y + H[lz][8];
        float inv_w = native_recip(w);
        float2 uv = inv_w*(float2)(u, v);// + 0.5f;
//        float4 srcRGB = read_imagef(srcImg, linearSampler, uv);
        float4 srcRGB = readLinear(srcImg, uv);
        if (_X) {
//            srcRGB.w = 1;
//            dstRGB = (float4)(dot(X[lz][0], srcRGB), dot(X[lz][1], srcRGB),
//                              dot(X[lz][2], srcRGB), dot(X[lz][3], srcRGB));
            dstRGB.x = X[lz][0]*srcRGB.x + X[lz][1]*srcRGB.y + X[lz][2] *srcRGB.z + X[lz][3];
            dstRGB.y = X[lz][4]*srcRGB.x + X[lz][5]*srcRGB.y + X[lz][6] *srcRGB.z + X[lz][7];
            dstRGB.z = X[lz][8]*srcRGB.x + X[lz][9]*srcRGB.y + X[lz][10]*srcRGB.z + X[lz][11];
        } else {
            dstRGB = srcRGB;
        }
        if (_H2) {
            float u2 = H2[lz][0]*x + H2[lz][1]*y + H2[lz][2];
            float v2 = H2[lz][3]*x + H2[lz][4]*y + H2[lz][5];
            float w2 = H2[lz][6]*x + H2[lz][7]*y + H2[lz][8];
            float inv_w2 = native_recip(w2);
            float2 uv2 = inv_w2*(float2)(u2, v2);// + 0.5f;
//            dstRGB *= read_imagef(srcImg2, linearSampler, uv2);
            dstRGB *= readLinear(srcImg2, uv2);
        }
        dstRGB.w = 1;
        if (haveTransImg) {
            write_imagef(transImg, xy, dstRGB);
        }
        if (haveSubImg) {
            dstRGB.xyz -= read_imagef(subImg, sampler, xy).xyz;
        }
        if (haveDstImg) {
            write_imagef(dstImg, xy, dstRGB);
        }
        if (haveDotImg) {
            srcDstDot += dot(dotRGB.xyz, dstRGB.xyz);
        }

        if (size == 1) {
            float zeroThreshold2    = inputData->zeroThreshold    * inputData->zeroThreshold;
            float outlierThreshold2 = inputData->outlierThreshold * inputData->outlierThreshold;

            float norm2 = dot(dstRGB.xyz, dstRGB.xyz);
            if (norm2 < zeroThreshold2) {
                dstCountZero++;
            } else if (outlierThreshold2 > 0 && norm2 > outlierThreshold2) {
                dstCountOutlier++;
            } else {
                dstDstDot += norm2;
            }
        }
skipPixel:
        if (size > 1) {
            scratch[lz][lx][0] = dstRGB.x;
            scratch[lz][lx][1] = dstRGB.y;
            scratch[lz][lx][2] = dstRGB.z;
            barrier(CLK_LOCAL_MEM_FENCE);
#pragma unroll
            for (int i = 0; i < size; i++) {
                dstDstDot += scratch[lz][i][0]*scratch[lx][i][0] +
                             scratch[lz][i][1]*scratch[lx][i][1] +
                             scratch[lz][i][2]*scratch[lx][i][2];
            }
            barrier(CLK_LOCAL_MEM_FENCE);
        }
    }

    if (lz == 0) {
        dstCount = reduceSumInt(dstCount, lx, lsx, scratch);
        dstCountZero = reduceSumInt(dstCountZero, lx, lsx, scratch);
        dstCountOutlier = reduceSumInt(dstCountOutlier, lx, lsx, scratch);
        if (lx == 0) {
            outputData[gx].dstCount = dstCount;
            outputData[gx].dstCountZero = dstCountZero;
            outputData[gx].dstCountOutlier = dstCountOutlier;
        }
    }
    if (size == 1) {
        if (haveDotImg) srcDstDot = reduceSumFloat(srcDstDot, lx, lsx, scratch);
        dstDstDot = reduceSumFloat(dstDstDot, lx, lsx, scratch);
        if (lx == 0) {
            if (haveDotImg) outputData[gx].srcDstDot[0] = srcDstDot;
            outputData[gx].dstDstDot[0][0] = dstDstDot;
        }
    } else {
        if (haveDotImg) {
            srcDstDot = reduceSumFloat(srcDstDot, lx, lsx, scratch[lz]);
            if (lx == 0) {
                outputData[gx].srcDstDot[lz] = srcDstDot;
            }
        }
        outputData[gx].dstDstDot[lz][lx] = dstDstDot;
    }
}

kernel void reduceOutputData(global struct OutputData *outputData) {
    const int x = get_global_id(0), gx = get_group_id(0), lx = get_local_id(0), lsx = get_local_size(0);
    local int scratch[256];

    int dstCount = reduceSumInt(outputData[x].dstCount, lx, lsx, scratch);
    int dstCountZero = reduceSumInt(outputData[x].dstCountZero, lx, lsx, scratch);
    int dstCountOutlier = reduceSumInt(outputData[x].dstCountOutlier, lx, lsx, scratch);
    if (lx == 0) {
        outputData[0].dstCount = dstCount;
        outputData[0].dstCountZero = dstCountZero;
        outputData[0].dstCountOutlier = dstCountOutlier;
    }
    for (int i = 0; i < DOT_SIZE; i++) {
        float srcDstDot = reduceSumFloat(outputData[x].srcDstDot[i], lx, lsx, scratch);
        float dstDstDot = reduceSumFloat(outputData[x].dstDstDot[i][i], lx, lsx, scratch);
        if (lx == 0) {
            outputData[0].srcDstDot[i] = srcDstDot;
            outputData[0].dstDstDot[i][i] = dstDstDot;
        }
        for (int j = i+1; j < DOT_SIZE; j++) {
            float dstDstDot = reduceSumFloat(outputData[x].dstDstDot[i][j], lx, lsx, scratch);
            if (lx == 0) {
                outputData[0].dstDstDot[i][j] = dstDstDot;
                outputData[0].dstDstDot[j][i] = dstDstDot;
            }
        }
    }
}
