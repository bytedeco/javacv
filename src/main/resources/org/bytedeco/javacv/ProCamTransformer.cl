/*
 * Copyright (C) 2011-2012 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * transform kernel (one transform, no mask)
 */
kernel void transformOne(read_only image2d_t srcImg, read_only image2d_t srcImg2, write_only image2d_t dstImg,
        constant float H1[9], constant float H2[9], constant float4 X[4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(dstImg);
    int h = get_image_height(dstImg);
    multiWarpColorTransform(srcImg, srcImg2, 0, 0, 0, dstImg, 0, w, h,
            H1, H2, X, 1, inputData, outputData, false, false, false, true, false);
}

/**
 * transform kernel (one transform with an image to subtract and a mask)
 */
kernel void transformSub(read_only image2d_t srcImg, read_only image2d_t srcImg2, read_only image2d_t subImg,
        write_only image2d_t transImg, write_only image2d_t dstImg,
        read_only image2d_t maskImg, constant float H1[9], constant float H2[9], constant float4 X[4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(maskImg);
    int h = get_image_height(maskImg);
    multiWarpColorTransform(srcImg, srcImg2, subImg, 0, transImg, dstImg, maskImg, w, h,
            H1, H2, X, 1, inputData, outputData, true, false, true, true, true);
}

/**
 * transform kernel (dot products only)
 */
kernel void transformDot(read_only image2d_t srcImg, read_only image2d_t srcImg2, read_only image2d_t subImg,
        read_only image2d_t dotImg, read_only image2d_t maskImg,
        constant float H1[][9], constant float H2[][9], constant float4 X[][4],
        constant struct InputData *inputData, global struct OutputData *outputData) {
    int w = get_image_width(maskImg);
    int h = get_image_height(maskImg);
    multiWarpColorTransform(srcImg, srcImg2, subImg, dotImg, 0, 0, maskImg, w, h,
            H1, H2, X, DOT_SIZE, inputData, outputData, true, true, false, false, true);
}
