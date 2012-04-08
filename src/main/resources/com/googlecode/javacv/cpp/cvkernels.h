/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
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

#define CV_INLINE static inline
#include <opencv2/core/core_c.h>

/**
 *
 * @author Samuel Audet
 *
 * OpenCL implementation also available in the ImageTransformer.cl file.
 *
 */
#ifndef __JAVACV_CVKERNELS_H__
#define __JAVACV_CVKERNELS_H__

struct KernelData {
    // input
    IplImage *srcImg, *srcImg2, *subImg, *srcDotImg, *mask;
    double    zeroThreshold, outlierThreshold;
    CvMat    *H1, *H2, *X;

    // output
    IplImage *transImg,  *dstImg;
    int       dstCount,   dstCountZero, dstCountOutlier;
    double    srcDstDot, *dstDstDot;
};

#define PTYPE float
#define multiWarpColorTransform multiWarpColorTransform32F
#include "cvkernels.h"
#undef multiWarpColorTransform
#undef PTYPE

#define PTYPE unsigned char
#define multiWarpColorTransform multiWarpColorTransform8U
#include "cvkernels.h"
#undef multiWarpColorTransform
#undef PTYPE

#elif defined PTYPE //__JAVACV_CVKERNELS_H__

// transImg  = warp(srcImg, H1) * (X*warp(srcImg2, H2))
//   dstImg  = transImg - subImg
// srcDstDot =   dstImg · srcDotImg
// data[i].dstDstDot[j] = dstImg[i] · dstImg[j]
static inline void multiWarpColorTransform(KernelData data[], int size, CvRect* roi, CvScalar* fillColor) {
    assert (size <= MAX_SIZE);
    int srcStep [MAX_SIZE], srcWidth [MAX_SIZE], srcHeight [MAX_SIZE],
        srcStep2[MAX_SIZE], srcWidth2[MAX_SIZE], srcHeight2[MAX_SIZE];
    PTYPE *srcPixels[MAX_SIZE], *srcPixels2[MAX_SIZE], *transPixels [MAX_SIZE],
          *subPixels[MAX_SIZE], *dstPixels [MAX_SIZE], *srcDotPixels[MAX_SIZE];
    unsigned char* maskBytes[MAX_SIZE];
    double zeroThreshold2[MAX_SIZE], outlierThreshold2[MAX_SIZE];
    float h[MAX_SIZE][9], g[MAX_SIZE][9], Xa[MAX_SIZE][16];
    IplImage* modelImage = NULL, *modelMask = NULL;

    int i, j, allSrcEqual = 1;
    for (i = 0; i < size; i++) {
        srcStep  [i] = data[i].srcImg->widthStep/sizeof(PTYPE);
        srcWidth [i] = data[i].srcImg->width;
        srcHeight[i] = data[i].srcImg->height;
        if (data[i].srcImg2 != NULL) {
            srcStep2  [i] = data[i].srcImg2->widthStep/sizeof(PTYPE);
            srcWidth2 [i] = data[i].srcImg2->width;
            srcHeight2[i] = data[i].srcImg2->height;
        }

        srcPixels   [i] = (PTYPE*)data[i].srcImg ->imageData;
        srcPixels2  [i] = data[i].srcImg2   == NULL ? NULL : (PTYPE*)data[i].srcImg2  ->imageData;
        transPixels [i] = data[i].transImg  == NULL ? NULL : (PTYPE*)data[i].transImg ->imageData;
        subPixels   [i] = data[i].subImg    == NULL ? NULL : (PTYPE*)data[i].subImg   ->imageData;
        dstPixels   [i] = data[i].dstImg    == NULL ? NULL : (PTYPE*)data[i].dstImg   ->imageData;
        srcDotPixels[i] = data[i].srcDotImg == NULL ? NULL : (PTYPE*)data[i].srcDotImg->imageData;
        maskBytes   [i] = data[i].mask   == NULL ? NULL : (unsigned char*)data[i].mask->imageData;
        zeroThreshold2   [i] = data[i].zeroThreshold   *data[i].zeroThreshold;
        outlierThreshold2[i] = data[i].outlierThreshold*data[i].outlierThreshold;
        if (i > 0 && (data[i].srcImg != data[i-1].srcImg || data[i].srcImg2   != data[i-1].srcImg2   ||
                      data[i].subImg != data[i-1].subImg || data[i].srcDotImg != data[i-1].srcDotImg ||
                      data[i].mask   != data[i-1].mask   || zeroThreshold2[i] != zeroThreshold2[i-1] ||
                      outlierThreshold2[i] != outlierThreshold2[i-1])) {
            allSrcEqual = 0;
        }

        data[i].dstCount        = 0;
        data[i].dstCountZero    = 0;
        data[i].dstCountOutlier = 0;
        data[i].srcDstDot       = 0;
        if (data[i].dstDstDot != NULL) {
            for (j = 0; j < size; j++) {
                data[i].dstDstDot[j] = 0;
            }
        }

        for (j = 0; j < 9; j++) {
            h[i][j] = (float)data[i].H1->data.db[j];
        }
        if (data[i].H2 != NULL) {
            for (j = 0; j < 9; j++) {
                g[i][j] = (float)data[i].H2->data.db[j];
            }
        }
        if (data[i].X != NULL) {
            for (j = 0; j < 16; j++) {
                Xa[i][j] = (float)data[i].X->data.db[j];
            }
        } else {
            // identity matrix
            for (j = 0; j < 16; j++) {
                Xa[i][j] = j%4 == j/4 ? 1 : 0;
            }
        }

        if (data[i].transImg != NULL) {
            modelImage = data[i].transImg;
        } else if (data[i].dstImg != NULL) {
            modelImage = data[i].dstImg;
        } else if (data[i].subImg != NULL) {
            modelImage = data[i].subImg;
        } else if (data[i].srcDotImg != NULL) {
            modelImage = data[i].srcDotImg;
        }

        if (data[i].mask != NULL) {
            modelMask = data[i].mask;
        }
    }

    int startx   = 0;
    int starty   = 0;
    int step     = modelImage->widthStep/sizeof(PTYPE);
    int channels = modelImage->nChannels;
    int colors   = channels > 3 ? 3 : channels; // ignore alpha channel
    int endx     = modelImage->width;
    int endy     = modelImage->height;
    int maskStep = modelMask == NULL ? 0 : modelMask->widthStep;
    if (roi != NULL) {
        startx = roi->x;
        starty = roi->y;
        endx   = startx + roi->width;
        endy   = starty + roi->height;
    }
    PTYPE fill[4] = { 0.0 };
    if (fillColor != NULL) {
        fill[0] = (PTYPE)fillColor->val[0];
        fill[1] = (PTYPE)fillColor->val[1];
        fill[2] = (PTYPE)fillColor->val[2];
        fill[3] = (PTYPE)fillColor->val[3];
    }

    int x, y, z;
    int line     = starty*step;
    int maskLine = starty*maskStep;
    for (y = starty; y < endy; y++, line += step, maskLine += maskStep) {
        int pixel = line + startx*channels;
        float h3[MAX_SIZE], h1[MAX_SIZE], h2[MAX_SIZE], g3[MAX_SIZE], g1[MAX_SIZE], g2[MAX_SIZE];
        for (i = 0; i < size; i++) {
            h3[i] = startx*h[i][6] + y*h[i][7] + h[i][8];
            h1[i] = startx*h[i][0] + y*h[i][1] + h[i][2];
            h2[i] = startx*h[i][3] + y*h[i][4] + h[i][5];
            g3[i] = startx*g[i][6] + y*g[i][7] + g[i][8];
            g1[i] = startx*g[i][0] + y*g[i][1] + g[i][2];
            g2[i] = startx*g[i][3] + y*g[i][4] + g[i][5];
        }
        for (x = startx; x < endx; x++, pixel += channels) {
            if (x > startx) {
                for (i = 0; i < size; i++) {
                    h3[i] += h[i][6];
                    h1[i] += h[i][0];
                    h2[i] += h[i][3];
                    g3[i] += g[i][6];
                    g1[i] += g[i][0];
                    g2[i] += g[i][3];
                }
            }

            if (allSrcEqual) {
                if (maskBytes[0] != NULL && maskBytes[0][maskLine + x] == 0) {
                    continue;
                } else if (srcDotPixels[0] != NULL) {
                    double d, magnitude2 = 0;
                    switch (colors) {
                    //case 4: d = srcDotPixels[0][pixel+3]; magnitude2 += d*d;
                    case 3: d = srcDotPixels[0][pixel+2]; magnitude2 += d*d;
                    case 2: d = srcDotPixels[0][pixel+1]; magnitude2 += d*d;
                    case 1: d = srcDotPixels[0][pixel+0]; magnitude2 += d*d; break;
                    default: assert (0);
                    }
                    if (magnitude2 < zeroThreshold2[0]) {
                        for (i = 0; i < size; i++) {
                            data[i].dstCount++;
                            data[i].dstCountZero++;
                        }
                        continue;
                    } else if (outlierThreshold2[0] > 0 &&
                            magnitude2 > outlierThreshold2[0]) {
                        for (i = 0; i < size; i++) {
                            data[i].dstCount++;
                            data[i].dstCountOutlier++;
                        }
                        continue;
                    }
                }
            }

            float dst[MAX_SIZE][4];
            for (i = 0; i < size; i++) {
                if (!allSrcEqual) {
                    if (maskBytes[i] != NULL && maskBytes[i][maskLine + x] == 0) {
                        continue;
                    } else if (srcDotPixels[i] != NULL) {
                        double d, magnitude2 = 0;
                        switch (colors) {
                        //case 4: d = srcDotPixels[i][pixel+3]; magnitude2 += d*d;
                        case 3: d = srcDotPixels[i][pixel+2]; magnitude2 += d*d;
                        case 2: d = srcDotPixels[i][pixel+1]; magnitude2 += d*d;
                        case 1: d = srcDotPixels[i][pixel+0]; magnitude2 += d*d; break;
                        default: assert (0);
                        }
                        if (magnitude2 < zeroThreshold2[i]) {
                            dst[i][0] = dst[i][1] = dst[i][2] = dst[i][3] = 0;
                            data[i].dstCount++;
                            data[i].dstCountZero++;
                            continue;
                        } else if (outlierThreshold2[i] > 0 &&
                                magnitude2 > outlierThreshold2[i]) {
                            dst[i][0] = dst[i][1] = dst[i][2] = dst[i][3] = 0;
                            data[i].dstCount++;
                            data[i].dstCountOutlier++;
                            continue;
                        }
                    }
                }
                data[i].dstCount++;

                float w2 = 1/h3[i];
                float x2 = h1[i]*w2;
                float y2 = h2[i]*w2;
                int xi2 = cvFloor(x2);
                int yi2 = cvFloor(y2);

                PTYPE src[4] = { fill[0], fill[1], fill[2], fill[3] };
                PTYPE *src00 = fill;
                PTYPE *src10 = fill;
                PTYPE *src01 = fill;
                PTYPE *src11 = fill;
                int inside = 0;
                if (xi2 >= 0 && xi2 < srcWidth[i]-1 && yi2 >= 0 && yi2 < srcHeight[i]-1) {
                    inside = 1;
                    src00 = srcPixels[i] + yi2*srcStep[i] + xi2*channels;
                    src10 = src00 + channels;
                    src01 = src00 + srcStep[i];
                    src11 = src00 + srcStep[i] + channels;
                } else if (xi2 >= -1 && xi2 < srcWidth[i] && yi2 >= -1 && yi2 < srcHeight[i]) {
                    inside = 1;
                    if (xi2 >= 0 && yi2 >= 0) {
                        src00 = srcPixels[i] + yi2*srcStep[i] + xi2*channels;
                    }
                    if (xi2 < srcWidth[i]-1 && yi2 >= 0) {
                        src10 = srcPixels[i] + yi2*srcStep[i] + (xi2+1)*channels;
                    }
                    if (xi2 >= 0 && yi2 < srcHeight[i]-1) {
                        src01 = srcPixels[i] + (yi2+1)*srcStep[i] + xi2*channels;
                    }
                    if (xi2 < srcWidth[i]-1 && yi2 < srcHeight[i]-1) {
                        src11 = srcPixels[i] + (yi2+1)*srcStep[i] + (xi2+1)*channels;
                    }
                }

                if (inside) {
                    float xn = x2 - xi2;
                    float yn = y2 - yi2;

                    for (z = 0; z < colors; z++) {
                        float f00 = src00[z];
                        float f10 = src10[z];
                        float f01 = src01[z];
                        float f11 = src11[z];

                        float f0 = f00*(1-xn) + f10*xn;
                        float f1 = f01*(1-xn) + f11*xn;
                        src[z] =  f0*(1-yn) + f1*yn;
                    }
                }

                switch (colors) {
                //case 4: dst[i][3] = Xa[i][12]*src[0] + Xa[i][13]*src[1] + Xa[i][14]*src[2] + Xa[i][15]*src[3];
                case 3: dst[i][2] = Xa[i][8 ]*src[0] + Xa[i][9 ]*src[1] + Xa[i][10]*src[2] + Xa[i][11]*src[3];
                case 2: dst[i][1] = Xa[i][4 ]*src[0] + Xa[i][5 ]*src[1] + Xa[i][6 ]*src[2] + Xa[i][7 ]*src[3];
                case 1: dst[i][0] = Xa[i][0 ]*src[0] + Xa[i][1 ]*src[1] + Xa[i][2 ]*src[2] + Xa[i][3 ]*src[3]; break;
                default: assert (0);
                }

                if (srcPixels2[i] != NULL) {
                    float w3 = 1/g3[i];
                    float x3 = g1[i]*w3;
                    float y3 = g2[i]*w3;
                    int xi3 = cvFloor(x3);
                    int yi3 = cvFloor(y3);

                    src00 = fill;
                    src10 = fill;
                    src01 = fill;
                    src11 = fill;
                    inside = 0;
                    if (xi3 >= 0 && xi3 < srcWidth2[i]-1 && yi3 >= 0 && yi3 < srcHeight2[i]-1) {
                        inside = 1;
                        src00 = srcPixels2[i] + yi3*srcStep2[i] + xi3*channels;
                        src10 = src00 + channels;
                        src01 = src00 + srcStep2[i];
                        src11 = src00 + srcStep2[i] + channels;
                    } else if (xi3 >= -1 && xi3 < srcWidth2[i] && yi3 >= -1 && yi3 < srcHeight2[i]) {
                        inside = 1;
                        if (xi3 >= 0 && yi3 >= 0) {
                            src00 = srcPixels2[i] + yi3*srcStep2[i] + xi3*channels;
                        }
                        if (xi3 < srcWidth2[i]-1 && yi3 >= 0) {
                            src10 = srcPixels2[i] + yi3*srcStep2[i] + (xi3+1)*channels;
                        }
                        if (xi3 >= 0 && yi3 < srcHeight2[i]-1) {
                            src01 = srcPixels2[i] + (yi3+1)*srcStep2[i] + xi3*channels;
                        }
                        if (xi3 < srcWidth2[i]-1 && yi3 < srcHeight2[i]-1) {
                            src11 = srcPixels2[i] + (yi3+1)*srcStep2[i] + (xi3+1)*channels;
                        }
                    }

                    if (inside) {
                        float xn = x3 - xi3;
                        float yn = y3 - yi3;

                        for (z = 0; z < colors; z++) {
                            float f00 = src00[z];
                            float f10 = src10[z];
                            float f01 = src01[z];
                            float f11 = src11[z];

                            float f0 = f00*(1-xn) + f10*xn;
                            float f1 = f01*(1-xn) + f11*xn;
                            dst[i][z] *= f0*(1-yn) + f1*yn;
                        }
                    } else {
                        dst[i][0] *= fill[0];
                        dst[i][1] *= fill[1];
                        dst[i][2] *= fill[2];
                        dst[i][3] *= fill[3];
                    }
                }

                for (z = 0; z < channels; z++) {
                    if (transPixels[i] != NULL) {
                        transPixels[i][pixel+z] = dst[i][z];
                    }

                    if (subPixels[i] != NULL && z < colors) {
                        dst[i][z] -= subPixels[i][pixel+z];
                    }

                    if (dstPixels[i] != NULL) {
                        dstPixels[i][pixel+z] = dst[i][z];
                    }

                    if (srcDotPixels[i] != NULL && z < colors) {
                        data[i].srcDstDot += srcDotPixels[i][pixel+z]*dst[i][z];
                    }
                }
            }

            switch (colors) {
                case 1:
                    for (i = 0; i < size; i++) {
                        if (data[i].dstDstDot != NULL) {
                            for (j = i; j < size; j++) {
                                data[i].dstDstDot[j] += dst[i][0]*dst[j][0];
                            }
                        }
                    }
                    break;
                case 2:
                    for (i = 0; i < size; i++) {
                        if (data[i].dstDstDot != NULL) {
                            for (j = i; j < size; j++) {
                                data[i].dstDstDot[j] += dst[i][0]*dst[j][0] + dst[i][1]*dst[j][1];
                            }
                        }
                    }
                    break;
                case 3:
                    for (i = 0; i < size; i++) {
                        if (data[i].dstDstDot != NULL) {
                            for (j = i; j < size; j++) {
                                data[i].dstDstDot[j] += dst[i][0]*dst[j][0] + dst[i][1]*dst[j][1] +
                                                        dst[i][2]*dst[j][2];
                            }
                        }
                    }
                    break;
//                case 4:
//                    for (i = 0; i < size; i++) {
//                        if (data[i].dstDstDot != NULL) {
//                            for (j = i; j < size; j++) {
//                                data[i].dstDstDot[j] += dst[i][0]*dst[j][0] + dst[i][1]*dst[j][1] +
//                                                        dst[i][2]*dst[j][2] + dst[i][3]*dst[j][3];
//                            }
//                        }
//                    }
//                    break;
                default: assert (0);
            }

        }
    }

    // fill in other half of Hessian or whatever
    for (i = 0; i < size; i++) {
        if (data[i].dstDstDot != NULL) {
            for (j = 0; j < i; j++) {
                data[i].dstDstDot[j] = data[j].dstDstDot[i];
            }
        }
    }
}
#endif //__JAVACV_CVKERNELS_H__
