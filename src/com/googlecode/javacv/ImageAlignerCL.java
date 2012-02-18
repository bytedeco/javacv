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

package com.googlecode.javacv;

import com.jogamp.opencl.CLImage2d;

/**
 *
 * @author Samuel Audet
 */
public interface ImageAlignerCL extends ImageAligner {

    CLImage2d getTemplateImageCL();
    void setTemplateImageCL(CLImage2d template0, double[] roiPts);

    CLImage2d getTargetImageCL();
    void setTargetImageCL(CLImage2d target0);

    CLImage2d getTransformedImageCL();
    CLImage2d getResidualImageCL();
    CLImage2d getMaskImageCL();

    CLImage2d[] getImagesCL();
}
