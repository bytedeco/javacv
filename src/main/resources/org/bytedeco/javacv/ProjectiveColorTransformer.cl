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

/**
 * transform kernel (one transform, no mask)
 */
kernel void transformOne(read_only image2d_t srcImg, write_only image2d_t dstImg,
        constant float H[9], constant float4 X[4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(dstImg);
    int h = get_image_height(dstImg);
    multiWarpColorTransform(srcImg, 0, 0, 0, 0, dstImg, 0, w, h,
            H, 0, X, 1, inputData, outputData, false, false, false, true, false);
}

/**
 * transform kernel (one transform with an image to subtract and a mask)
 */
kernel void transformSub(read_only image2d_t srcImg, read_only image2d_t subImg,
        write_only image2d_t transImg, write_only image2d_t dstImg,
        read_only image2d_t maskImg, constant float H[9], constant float4 X[4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(maskImg);
    int h = get_image_height(maskImg);
    multiWarpColorTransform(srcImg, 0, subImg, 0, transImg, dstImg, maskImg, w, h,
            H, 0, X, 1, inputData, outputData, true, false, true, true, true);
}

/**
 * transform kernel (dot products only)
 */
kernel void transformDot(read_only image2d_t srcImg, read_only image2d_t subImg,
        read_only image2d_t dotImg, read_only image2d_t maskImg,
        constant float H[][9], constant float4 X[][4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(maskImg);
    int h = get_image_height(maskImg);
    multiWarpColorTransform(srcImg, 0, subImg, dotImg, 0, 0, maskImg, w, h,
            H, 0, X, DOT_SIZE, inputData, outputData, true, true, false, false, true);
}
