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

package org.bytedeco.javacv;

import java.util.Arrays;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacv.cvkernels.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveColorTransformer extends ProjectiveTransformer {
    public ProjectiveColorTransformer(CvMat K1, CvMat K2, CvMat R, CvMat t,
            CvMat n, double[] referencePoints1, double[] referencePoints2,
            CvMat X, int numGains, int numBiases) {
        super(K1, K2, R, t, n, referencePoints1, referencePoints2);

        this.X = X == null ? null : X.clone();

        this.numGains = numGains;
        this.numBiases = numBiases;
    }

    protected static ThreadLocal<CvMat>
            X24x4   = CvMat.createThreadLocal(4, 4),
            temp3x1 = CvMat.createThreadLocal(3, 1);

    protected CvMat X = null;
    protected int numGains = 0, numBiases = 0;

    protected CvMat[] X2 = null;

    public CvMat getX() {
        return X;
    }
    public int getNumGains() {
        return numGains;
    }
    public int getNumBiases() {
        return numBiases;
    }

    public void transformColor(IplImage srcImage, IplImage dstImage, CvRect roi,
            int pyramidLevel, ImageTransformer.Parameters parameters, boolean inverse) {
        Parameters p = ((Parameters)parameters);

        if ((Arrays.equals(p.getColorParameters(), p.getIdentityColorParameters()) &&
                (X == null || p.fakeIdentity)) || (X == null && numGains == 0 && numBiases == 0)) {
            if (srcImage != dstImage) {
                cvCopy(srcImage, dstImage);
            }
            return;
        }

        CvMat X2 = X24x4.get();
        prepareColorTransform(X2, pyramidLevel, p, inverse);
        X2.rows(3);
        // do color transformation
        if (roi == null) {
            cvResetImageROI(dstImage);
        } else {
            cvSetImageROI(dstImage, roi);
        }
        X2.put(0, 3, X2.get(0, 3)*dstImage.highValue());
        X2.put(1, 3, X2.get(1, 3)*dstImage.highValue());
        X2.put(2, 3, X2.get(2, 3)*dstImage.highValue());
        cvTransform(srcImage, dstImage, X2, null);
        X2.rows(4);
    }

    protected void prepareColorTransform(CvMat X2, int pyramidLevel, Parameters p, boolean inverse) {
        CvMat A = p.getA(), b = p.getB();

        cvSetIdentity(X2);

        X2.rows(3); X2.cols(3);
        if (p.fakeIdentity && !inverse) {
            X2.put(A);
        } else if (A != null && X != null) {
            cvMatMul(X, A, X2);
        } else if (X == null) {
            X2.put(A);
        } else if (A == null) {
            X2.put(X);
        }

        X2.rows(4); X2.cols(4);
        if (b != null) {
            X2.put(0, 3, b.get(0));
            X2.put(1, 3, b.get(1));
            X2.put(2, 3, b.get(2));
        }

        if (inverse) {
            // CV_LU doesn't work on OpenCV 2.0 with rows > 3 ...
            cvInvert(X2, X2, CV_SVD);
        }
    }

    @Override public void transform(Data[] data, CvRect roi, ImageTransformer.Parameters[] parameters, boolean[] inverses) {
        assert data.length == parameters.length;
        if (kernelData == null || kernelData.capacity() < data.length) {
            kernelData = new KernelData(data.length);
        }
        if (H == null || H.length < data.length) {
            H = new CvMat[data.length];
            for (int i = 0; i < H.length; i++) {
                H[i] = CvMat.create(3, 3);
            }
        }
        if (X2 == null || X2.length < data.length) {
            X2 = new CvMat[data.length];
            for (int i = 0; i < X2.length; i++) {
                X2[i] = CvMat.create(4, 4);
            }
        }

        for (int i = 0; i < data.length; i++) {
            kernelData.position(i);

            kernelData.srcImg(data[i].srcImg);
            kernelData.srcImg2(null);
            kernelData.subImg(data[i].subImg);
            kernelData.srcDotImg(data[i].srcDotImg);
            kernelData.mask(data[i].mask);
            kernelData.zeroThreshold(data[i].zeroThreshold);
            kernelData.outlierThreshold(data[i].outlierThreshold);

            boolean inverse = inverses == null ? false : inverses[i];
            prepareHomography    (H[i], data[i].pyramidLevel, (Parameters)parameters[i], inverse);
            prepareColorTransform(X2[i], data[i].pyramidLevel, (Parameters)parameters[i], inverse);

            kernelData.H1(H[i]);
            kernelData.H2(null);
            kernelData.X(X2[i]);

            kernelData.transImg(data[i].transImg);
            kernelData.dstImg(data[i].dstImg);
            kernelData.dstDstDot(data[i].dstDstDot);
        }

        int fullCapacity = kernelData.capacity();
        kernelData.capacity(data.length);
        multiWarpColorTransform(kernelData, roi, getFillColor());
        kernelData.capacity(fullCapacity);

        for (int i = 0; i < data.length; i++) {
            kernelData.position(i);
            data[i].dstCount        = kernelData.dstCount();
            data[i].dstCountZero    = kernelData.dstCountZero();
            data[i].dstCountOutlier = kernelData.dstCountOutlier();
            data[i].srcDstDot       = kernelData.srcDstDot();
        }
    }

    @Override public Parameters createParameters() {
        return new Parameters();
    }

    public class Parameters extends ProjectiveTransformer.Parameters {
        protected Parameters() {
            identityColorParameters = new double[numGains + numBiases];
            if (numGains > 0) {
                A = CvMat.create(3, 3);
                cvSetIdentity(A);
            }
            if (numBiases > 0) {
                b = CvMat.create(3, 1);
                cvSetZero(b);
            }

            switch (numGains) {
                case 0: assert (A == null); break;
                case 1: identityColorParameters[0] =
                            (A.get(0) + A.get(4) + A.get(8))/3; break;
                case 3: identityColorParameters[0] = A.get(0);
                        identityColorParameters[1] = A.get(4);
                        identityColorParameters[2] = A.get(8); break;
                case 9: A.get(0, identityColorParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: identityColorParameters[numGains] =
                            (b.get(0) + b.get(1) + b.get(2))/3;   break;
                case 3: b.get(0, identityColorParameters, numGains, 3); break;
                default: assert (false);
            }

            reset(false);
        }

        protected double[] colorParameters = null, identityColorParameters = null;
        private CvMat A = null, b = null;

        public double[] getColorParameters() {
            return colorParameters;
        }
        public double[] getIdentityColorParameters() {
            return identityColorParameters;
        }

        @Override public int size() {
            return super.size() + numGains + numBiases;
        }
        @Override public double get(int i) {
            int s = super.size();
            if (i < s) {
                return super.get(i);
            } else {
                return colorParameters[i-s];
            }
        }
        @Override public void set(int i, double p) {
            int s = super.size();
            if (i < s) {
                super.set(i, p);
            } else {
                if (colorParameters[i-s] != p) {
                    colorParameters[i-s] = p;
                    setUpdateNeeded(true);
                }
            }
        }
        @Override public void reset(boolean asIdentity) {
            super.reset(asIdentity);
            resetColor(asIdentity);
        }
        public void resetColor(boolean asIdentity) {
            if (identityColorParameters != null) {
                if (!Arrays.equals(colorParameters, identityColorParameters) ||
                        fakeIdentity != asIdentity) {
                    fakeIdentity = asIdentity;
                    colorParameters = identityColorParameters.clone();
                    setUpdateNeeded(true);
                }
            }
        }
//        @Override public boolean addDelta(int i, double scale) {
//            int s = super.size();
//            if (i < s) {
//                return super.addDelta(i, scale);
//            } else {
//                // gradient varies linearly with intensity, so
//                // the increment value is not very important, but
//                // referenceCameraImage is good only for the value 1,
//                // so let's use that
//                int channel = i-s;
//                colorParameters[channel] += scale;//1;
//                setUpdateNeeded(true);
//                return false;
//            }
//        }
        @Override public void compose(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            super.compose(p1, inverse1, p2, inverse2);
            composeColor(p1, inverse1, p2, inverse2);
        }
        public void composeColor(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            assert (!inverse1 && !inverse2);

            Parameters pp1 = (Parameters)p1, pp2 = (Parameters)p2;
            CvMat A1 = pp1.getA(), b1 = pp1.getB();
            CvMat A2 = pp2.getA(), b2 = pp2.getB();

            if (b != null) {
                if (pp1.fakeIdentity && X != null) {
                    CvMat temp = temp3x1.get();
                    cvMatMul(X, b1, temp);
                    b1 = temp;
                }

                if (A2 == null && b2 == null) {
                    cvCopy(b1, b);
                } else if (b1 == null) {
                    cvCopy(b2, b);
                } else if (b2 == null) {
                    cvMatMul(A2, b1, b);
                } else {
                    cvGEMM(A2, b1, 1.0,  b2, 1.0,  b, 0);
                }
            }

            if (A != null) {
                if (A1 == null) {
                    cvCopy(A2, A);
                } else if (A2 == null) {
                    cvCopy(A1, A);
                } else {
                    cvMatMul(A2, A1, A);
                }
            }

            switch (numGains) {
                case 0: assert (A == null); break;
                case 1: colorParameters[0] =
                            (A.get(0) + A.get(4) + A.get(8))/3; break;
                case 3: colorParameters[0] = A.get(0);
                        colorParameters[1] = A.get(4);
                        colorParameters[2] = A.get(8); break;
                case 9: A.get(0, colorParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: colorParameters[numGains] =
                            (b.get(0) + b.get(1) + b.get(2))/3;   break;
                case 3: b.get(0, colorParameters, numGains, 3); break;
                default: assert (false);
            }
        }

        public CvMat getA() {
            update();
            return A;
        }
        public CvMat getB() {
            update();
            return b;
        }

        @Override protected void update() {
            if (!isUpdateNeeded()) {
                return;
            }

            switch (numGains) {
                case 0: assert (A == null); break;
                case 1: A.put(0, colorParameters[0]);
                        A.put(4, colorParameters[0]);
                        A.put(8, colorParameters[0]); break;
                case 3: A.put(0, colorParameters[0]);
                        A.put(4, colorParameters[1]);
                        A.put(8, colorParameters[2]); break;
                case 9: A.put(0, colorParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: b.put(0, colorParameters[numGains]);
                        b.put(1, colorParameters[numGains]);
                        b.put(2, colorParameters[numGains]);    break;
                case 3: b.put(0, colorParameters, numGains, 3); break;
                default: assert (false);
            }

            super.update();
            setUpdateNeeded(false);
        }

        @Override public Parameters clone() {
            Parameters p = new Parameters();
            p.set(this);
            return p;
        }
    }
}
