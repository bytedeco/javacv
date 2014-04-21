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
                          CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;

//const sampler_t linearSampler = CLK_NORMALIZED_COORDS_FALSE |
//                                CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_LINEAR;

#define SENSOR_PATTERN_RGGB (int2)(0,0)
#define SENSOR_PATTERN_GBRG (int2)(1,0)
#define SENSOR_PATTERN_GRBG (int2)(0,1)
#define SENSOR_PATTERN_BGGR (int2)(1,1)

inline float4 readBayer(read_only image2d_t img, int2 xy, int2 sensorPattern) {
    float p1 = read_imagef(img, sampler, xy).s0;
    float p2 = read_imagef(img, sampler, xy + (int2)(1, 0)).s0;
    float p3 = read_imagef(img, sampler, xy + (int2)(0, 1)).s0;
    float p4 = read_imagef(img, sampler, xy + (int2)(1, 1)).s0;

    xy = (xy + sensorPattern) % 2;
    if (all(xy == SENSOR_PATTERN_RGGB)) {
        return (float4)(p1, (p2 + p3)/2, p4, 1);
    } else if (all(xy == SENSOR_PATTERN_GBRG)) {
        return (float4)(p2, (p1 + p4)/2, p3, 1);
    } else if (all(xy == SENSOR_PATTERN_GRBG)) {
        return (float4)(p3, (p1 + p4)/2, p2, 1);
    } else {//(all(xy == SENSOR_PATTERN_BGGR))
        return (float4)(p4, (p2 + p3)/2, p1, 1);
    }
}

inline float4 readLinear(read_only image2d_t img, float2 xy) {
    int2 xy00 = convert_int2_rtn(xy);
    float4 img00 = read_imagef(img, sampler, xy00);
    float4 img10 = read_imagef(img, sampler, xy00 + (int2)(1, 0));
    float4 img01 = read_imagef(img, sampler, xy00 + (int2)(0, 1));
    float4 img11 = read_imagef(img, sampler, xy00 + (int2)(1, 1));
    float4 img0 = mix(img00, img10, xy.x - xy00.x);
    float4 img1 = mix(img01, img11, xy.x - xy00.x);
    return mix(img0, img1, xy.y - xy00.y);
}

inline float4 readBayerLinear(read_only image2d_t img, float2 xy, int2 sensorPattern) {
    int2 xy00 = convert_int2_rtn(xy);
    float4 img00 = readBayer(img, xy00, sensorPattern);
    float4 img10 = readBayer(img, xy00 + (int2)(1, 0), sensorPattern);
    float4 img01 = readBayer(img, xy00 + (int2)(0, 1), sensorPattern);
    float4 img11 = readBayer(img, xy00 + (int2)(1, 1), sensorPattern);
    float4 img0 = mix(img00, img10, xy.x - xy00.x);
    float4 img1 = mix(img01, img11, xy.x - xy00.x);
    return mix(img0, img1, xy.y - xy00.y);
}

kernel void pyrDown(read_only image2d_t srcImg, write_only image2d_t dstImg) {
    int x = get_global_id(0), y = get_global_id(1);
    int lx = get_local_id(0), ly = get_local_id(1);
    int x2 = x*2, y2 = y*2;
    int lx2 = lx*2, ly2 = ly*2;

    if (x >= get_image_width(dstImg) || y >= get_image_height(dstImg)) {
        return;
    }

#define S(x,y) read_imagef(srcImg, sampler, (int2)(x, y))
    float4 rgb = (1.0/256.0)*(S(x2-2, y2-2) + S(x2+2, y2-2) + S(x2-2, y2+2) + S(x2+2, y2+2)) +
                 (4.0/256.0)*(S(x2-1, y2-2) + S(x2+1, y2-2) + S(x2-2, y2-1) + S(x2+2, y2-1) +
                              S(x2-2, y2+1) + S(x2+2, y2+1) + S(x2-1, y2+2) + S(x2+1, y2+2)) +
                 (6.0/256.0)*(S(x2  , y2-2) + S(x2-2, y2  ) + S(x2+2, y2  ) + S(x2  , y2+2)) +
                (16.0/256.0)*(S(x2-1, y2-1) + S(x2+1, y2-1) + S(x2-1, y2+1) + S(x2+1, y2+1)) +
                (24.0/256.0)*(S(x2  , y2-1) + S(x2-1, y2  ) + S(x2+1, y2  ) + S(x2  , y2+1)) +
                (36.0/256.0)* S(x2  , y2  );
#undef S

    rgb.w = 1;
    write_imagef(dstImg, (int2)(x, y), rgb);
}

kernel void remap(read_only image2d_t srcImg, write_only image2d_t dstImg,
        read_only image2d_t mapxImg, read_only image2d_t mapyImg) {
    int x = get_global_id(0), y = get_global_id(1);
    int2 xy = (int2)(x, y);

    if (x >= get_image_width(dstImg) || y >= get_image_height(dstImg)) {
        return;
    }

    float x2 = read_imagef(mapxImg, sampler, xy).s0;
    float y2 = read_imagef(mapyImg, sampler, xy).s0;
    float2 xy2 = (float2)(x2, y2);
    float4 rgb = readLinear(srcImg, xy2);
    write_imagef(dstImg, xy, rgb);
}

kernel void remapBayer(read_only image2d_t srcImg, write_only image2d_t dstImg,
        read_only image2d_t mapxImg, read_only image2d_t mapyImg, int2 sensorPattern) {
    int x = get_global_id(0), y = get_global_id(1);
    int2 xy = (int2)(x, y);

    if (x >= get_image_width(dstImg) || y >= get_image_height(dstImg)) {
        return;
    }

    float x2 = read_imagef(mapxImg, sampler, xy).s0;
    float y2 = read_imagef(mapyImg, sampler, xy).s0;
    float2 xy2 = (float2)(x2, y2);
    float4 rgb = readBayerLinear(srcImg, xy2, sensorPattern);
    write_imagef(dstImg, xy, rgb);
}
