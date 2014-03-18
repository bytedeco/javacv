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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
public class ReflectanceInitializer {

    public ReflectanceInitializer(CameraDevice cameraDevice, ProjectorDevice projectorDevice,
            int channels, GNImageAligner.Settings alignerSettings) {
        this(cameraDevice, projectorDevice, channels, alignerSettings, 51, 0.01);
    }
    public ReflectanceInitializer(CameraDevice cameraDevice, ProjectorDevice projectorDevice,
            int channels, GNImageAligner.Settings alignerSettings, int smoothingSize, double reflectanceMin) {
        this.alignerSettings = alignerSettings;
        this.smoothingSize   = smoothingSize;
        this.reflectanceMin  = reflectanceMin;
        this.cameraDevice    = cameraDevice;
        this.projectorDevice = projectorDevice;
        this.projectorImages = new IplImage[3];

        for (int i = 0; i < projectorImages.length; i++) {
            projectorImages[i] = IplImage.create(projectorDevice.imageWidth,
                    projectorDevice.imageHeight, IPL_DEPTH_32F, channels);
        }

        // capture "black" image (illuminated by ambient light only)
        cvSetZero(projectorImages[0]);
        // capture "white" image (projector illumination + ambient light)
        cvSet(projectorImages[1], CvScalar.ONE);
        // capture image with some texture easy to register... ugh...
        CvMat H = mat3x3.get();
        projectorDevice.getRectifyingHomography(cameraDevice, H);
        JavaCV.fractalTriangleWave(projectorImages[2], H);
    }

    private static ThreadLocal<CvMat>
            mat3x1 = CvMat.createThreadLocal(3, 1),
            mat3x3 = CvMat.createThreadLocal(3, 3),
            mat4x4 = CvMat.createThreadLocal(4, 4);

    private GNImageAligner.Settings alignerSettings;
    private int smoothingSize;
    private double reflectanceMin;
    private CameraDevice cameraDevice;
    private ProjectorDevice projectorDevice;
    private IplImage[] projectorImages;

    public IplImage[] getProjectorImages() {
        return projectorImages;
    }

    public IplImage initializeReflectance(IplImage[] cameraImages, IplImage reflectance,
            double[] roiPts, double[] gainAmbientLight) {
        int w        = cameraImages[0].width();
        int h        = cameraImages[0].height();
        int channels = cameraImages[0].nChannels();

        IplImage mask = IplImage.create(w, h, IPL_DEPTH_8U, 1);
        cvSetZero(mask);
        cvFillConvexPoly(mask, new CvPoint(roiPts.length/2).put((byte)(16-cameraDevice.getMapsPyramidLevel()), roiPts),
                4, CvScalar.WHITE, 8, 16);

        // make the images very very smooth to compensate for small movements
        IplImage float1 = cameraImages[0];
        IplImage float2 = cameraImages[1];
        cvCopy(float2, reflectance);
        cvSmooth(float1, float1, CV_GAUSSIAN, smoothingSize, 0, 0, 0);
        cvSmooth(float2, float2, CV_GAUSSIAN, smoothingSize, 0, 0, 0);

        // remove ambient light of image1 from image2 -> image2
        cvSub(float2, float1, float2, null);

        // remove distortion caused by the mixing matrix of the projector light
        // and recover (very very smooth) reflectance map
        CvMat p = mat3x1.get();
        p.put(1.0, 1.0, 1.0); // white
        cvMatMul(projectorDevice.colorMixingMatrix, p, p);
        CvMat invp;
        if (float2.nChannels() == 4) {
            invp = mat4x4.get();
            invp.put(1/p.get(0), 0, 0, 0,
                     0, 1/p.get(1), 0, 0,
                     0, 0, 1/p.get(2), 0,
                     0, 0, 0, 1);
        } else {
            invp = mat3x3.get();
            invp.put(1/p.get(0), 0, 0,
                     0, 1/p.get(1), 0,
                     0, 0, 1/p.get(2));
        }
        cvTransform(float2, float2, invp, null);

        // recover (very very smooth) ambient light by removing distortions
        // caused by the reflectance map
        // cvDiv(image1, image3, image1, 1);
        // cvDiv() doesn't support division by zero...
        FloatBuffer fb1 = float1.getFloatBuffer();
        FloatBuffer fb2 = float2.getFloatBuffer();
        ByteBuffer  mb  = mask.getByteBuffer();
        assert fb1.capacity() == fb2.capacity()/3;
        assert fb1.capacity() == mb.capacity()/3;
        int[] nPixels = new int[channels];
        for (int i = 0, j = 0; j < fb1.capacity(); i++, j+=channels) {
            for (int z = 0; z < channels; z++) {
                float ra = fb1.get(j+z);
                float r  = fb2.get(j+z);
                float a  = r == 0 ? 0 : ra/r;
                fb1.put(j+z, a);
                if (mb.get(i) != 0) {
                    if (r > reflectanceMin) {
                        nPixels[z]++;
                        gainAmbientLight[z+1] += a;
                    }
                }
            }
        }
        gainAmbientLight[0] = 1.0; // assume projector gain = 1.0
        for (int z = 0; z < gainAmbientLight.length-1; z++) {
            gainAmbientLight[z+1] = nPixels[z] == 0 ? 0 : gainAmbientLight[z+1]/nPixels[z];
        }
//        System.out.println(ambientLight[0] + " " + ambientLight[1] + " " + ambientLight[2]);

        // recover sharp reflectance map by using the original image2 and smooth ambient light
        cvAddS(float1, cvScalar(p.get(0), p.get(1), p.get(2), 0.0), float1, null);
        cvDiv(reflectance, float1, reflectance, 1.0);

        cvNot(mask, mask);
        // increase region a bit so that the resulting image can be
        // interpolated or averaged properly within the region of interest...
        cvErode(mask, mask, null, 15);
        cvSet(reflectance, CvScalar.ZERO, mask);

        return reflectance;
    }

    public CvMat initializePlaneParameters(IplImage reflectance, IplImage cameraImage,
            double[] referencePoints, double[] roiPts, double[] gainAmbientLight) {
        ProCamTransformer transformer = new ProCamTransformer(referencePoints, cameraDevice, projectorDevice, null);
        transformer.setProjectorImage(projectorImages[2], 0, alignerSettings.pyramidLevelMax);

        ProCamTransformer.Parameters parameters = transformer.createParameters();
//        parameters.set(8,  0);
//        parameters.set(9,  0);
//        parameters.set(10, -1/cameraDevice.getSettings().nominalDistance);
        final int gainAmbientLightStart = parameters.size() - gainAmbientLight.length;
        final int gainAmbientLightEnd   = parameters.size();
        for (int i = gainAmbientLightStart; i < gainAmbientLightEnd; i++) {
            parameters.set(i, gainAmbientLight[i-gainAmbientLightStart]);
        }
        ImageAligner aligner = new GNImageAligner(transformer, parameters,
                reflectance, roiPts, cameraImage, alignerSettings);

        double[] delta = new double[parameters.size()+1];
        boolean converged = false;
        long iterationsStartTime = System.currentTimeMillis();
        int iterations = 0;
        while (!converged && iterations < 100) {
            converged = aligner.iterate(delta);
            iterations++;
        }
        parameters = (ProCamTransformer.Parameters)aligner.getParameters();
//        for (int i = 0; i < gainAmbientLight.length; i++) {
//            gainAmbientLight[i] = parameters.get(11+i);
//        }
        Logger.getLogger(ReflectanceInitializer.class.getName()).info(
            "iteratingTime = " + (System.currentTimeMillis()-iterationsStartTime) +
                "  iterations = " + iterations + "  objectiveRMSE = " + (float)aligner.getRMSE());
        return parameters.getN0();
    }
}
