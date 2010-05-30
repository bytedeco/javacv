/*
 * Copyright (C) 2009,2010 Samuel Audet
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

package name.audet.samuel.javacv;

import java.util.Arrays;

import static name.audet.samuel.javacv.jna.cvkernels.*;
import static name.audet.samuel.javacv.jna.cxcore.*;

/**
 *
 * @author Samuel Audet
 */
public class ProjectiveGainBiasTransformer extends ProjectiveTransformer {
    public ProjectiveGainBiasTransformer(CvMat K1, CvMat K2, CvMat R, CvMat t,
            CvMat n, double[] referencePoints, CvMat X, int numGains, int numBiases) {
        super(K1, K2, R, t, n, referencePoints);

        this.X = X == null ? null : X.clone();

        this.numGains = numGains;
        this.numBiases = numBiases;
    }

    private CvMat X = null;
    private int numGains = 0, numBiases = 0;

    public CvMat getX() {
        return X;
    }
    public int getNumGains() {
        return numGains;
    }
    public int getNumBiases() {
        return numBiases;
    }

    public void transformGainBias(IplImage srcImage, IplImage dstImage, CvRect roi,
            int pyramidLevel, ImageTransformer.Parameters parameters, boolean inverse) {
        Parameters p = ((Parameters)parameters);

        if ((Arrays.equals(p.getGainBiasParameters(), p.getIdentityGainBiasParameters()) &&
                (X == null || p.fakeIdentity)) || (X == null && numGains == 0 && numBiases == 0)) {
            if (srcImage != dstImage) {
                cvCopy(srcImage, dstImage);
            }
            return;
        }

        CvMat X2 = CvMat.take(4, 4);
        prepareTransform(X2, pyramidLevel, p, inverse);
        X2.rows = 3;
        // do color transformation
        if (roi == null) {
            cvResetImageROI(dstImage);
        } else {
            cvSetImageROI(dstImage, roi.byValue());
        }
        X2.put(0, 3, X2.get(0, 3)*dstImage.getMaxIntensity());
        X2.put(1, 3, X2.get(1, 3)*dstImage.getMaxIntensity());
        X2.put(2, 3, X2.get(2, 3)*dstImage.getMaxIntensity());
        cvTransform(srcImage, dstImage, X2, null);
        X2.rows = 4;
        X2.pool();
    }

    protected void prepareTransform(CvMat X2, int pyramidLevel, Parameters p, boolean inverse) {
        CvMat A = p.getA(), b = p.getB();

        cvSetIdentity(X2);

        X2.rows = 3; X2.cols = 3;
        if (p.fakeIdentity && !inverse) {
            X2.put(A);
        } else if (A != null && X != null) {
            cvMatMul(X, A, X2);
        } else if (X == null) {
            X2.put(A);
        } else if (A == null) {
            X2.put(X);
        }

        X2.rows = 4; X2.cols = 4;
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

    @Override public void transform(Data[] data, IplImage mask, CvRect roi, double zeroThreshold,
            int pyramidLevel, ImageTransformer.Parameters[] parameters) {
        assert (data.length == parameters.length);
        boolean allOK = true;
        for (int i = 0; i < data.length; i++) {
            Data d = data[i];
            if (d.srcImg != null) {
                if ((d.transImg != null || d.dstImg != null) &&
                     d.subImg   == null && d.srcDotImg == null && d.dstDstDot == null) {
                        IplImage dstImage = d.transImg == null ? d.dstImg : d.transImg;
                        transform(d.srcImg, dstImage, roi, pyramidLevel, parameters[i], d.inverse);
                        transformGainBias(dstImage, dstImage, roi, pyramidLevel, parameters[i], d.inverse);
                } else {
                    allOK = false;
                }
            }
        }
        if (!allOK) {
            class Cache {
                MultiWarpColorTransformData[] kernelData;
                CvMat[] H, X;
            }
            Cache cache = data[0].cache instanceof Cache ?
                   (Cache)data[0].cache : new Cache();
            if (cache.kernelData == null || cache.kernelData.length != data.length ||
                         cache.H == null ||          cache.H.length != data.length ||
                         cache.X == null ||          cache.X.length != data.length) {
                data[0].cache = cache;
                cache.kernelData = (MultiWarpColorTransformData[])
                        new MultiWarpColorTransformData().toArray(data.length);
                cache.H = new CvMat[data.length];
                cache.X = new CvMat[data.length];
            }
            MultiWarpColorTransformData[] kd = cache.kernelData;

            for (int i = 0; i < kd.length; i++) {
                Data d = data[i];
                kd[i].srcImg    = d.srcImg    == null ? null : d.srcImg   .getPointer();
                kd[i].srcImg2   = null;
                kd[i].subImg    = d.subImg    == null ? null : d.subImg   .getPointer();
                kd[i].srcDotImg = d.srcDotImg == null ? null : d.srcDotImg.getPointer();

                CvMat H = cache.H[i] == null ? cache.H[i] = CvMat.create(3, 3) : cache.H[i];
                CvMat X = cache.X[i] == null ? cache.X[i] = CvMat.create(4, 4) : cache.X[i];

                prepareHomography(H, pyramidLevel, (Parameters)parameters[i], d.inverse);
                prepareTransform (X, pyramidLevel, (Parameters)parameters[i], d.inverse);

                kd[i].H1 = H.getPointer();
                kd[i].H2 = null;
                kd[i].X  = X.getPointer();

                kd[i].transImg  = d.transImg  == null ? null : d.transImg .getPointer();
                kd[i].dstImg    = d.dstImg    == null ? null : d.dstImg   .getPointer();
                kd[i].dstDstDot = d.dstDstDot;
            }

            multiWarpColorTransform(kd, mask, roi, zeroThreshold, getFillColor());

            for (int i = 0; i < data.length; i++) {
                data[i].dstCount     = kd[i].dstCount;
                data[i].dstCountZero = kd[i].dstCountZero;
                data[i].srcDstDot    = kd[i].srcDstDot;
            }
        }

    }

    @Override public Parameters createParameters() {
        return new Parameters();
    }

    public class Parameters extends ProjectiveTransformer.Parameters {
        protected Parameters() {
            identityGainBiasParameters = new double[numGains + numBiases];
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
                case 1: identityGainBiasParameters[0] =
                            (A.get(0) + A.get(4) + A.get(8))/3; break;
                case 3: identityGainBiasParameters[0] = A.get(0);
                        identityGainBiasParameters[1] = A.get(4);
                        identityGainBiasParameters[2] = A.get(8); break;
                case 9: A.get(0, identityGainBiasParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: identityGainBiasParameters[numGains] =
                            (b.get(0) + b.get(1) + b.get(2))/3;   break;
                case 3: b.get(0, identityGainBiasParameters, numGains, 3); break;
                default: assert (false);
            }

            reset(false);
        }

        protected double[] gainBiasParameters = null, identityGainBiasParameters = null;
        private CvMat A = null, b = null;

        public double[] getGainBiasParameters() {
            return gainBiasParameters;
        }
        public double[] getIdentityGainBiasParameters() {
            return identityGainBiasParameters;
        }

        @Override public int size() {
            return super.size() + numGains + numBiases;
        }
        @Override public double get(int i) {
            int s = super.size();
            if (i < s) {
                return super.get(i);
            } else {
                return gainBiasParameters[i-s];
            }
        }
        @Override public void set(int i, double p) {
            int s = super.size();
            if (i < s) {
                super.set(i, p);
            } else {
                if (gainBiasParameters[i-s] != p) {
                    gainBiasParameters[i-s] = p;
                    setUpdateNeeded(true);
                }
            }
        }
        @Override public void reset(boolean asIdentity) {
            super.reset(asIdentity);
            resetGainBias(asIdentity);
        }
        public void resetGainBias(boolean asIdentity) {
            if (identityGainBiasParameters != null) {
                if (!Arrays.equals(gainBiasParameters, identityGainBiasParameters) ||
                        fakeIdentity != asIdentity) {
                    fakeIdentity = asIdentity;
                    gainBiasParameters = identityGainBiasParameters.clone();
                    setUpdateNeeded(true);
                }
            }
        }
        @Override public boolean addDelta(int i) {
            int s = super.size();
            if (i < s) {
                return super.addDelta(i);
            } else {
                // gradient varies linearly with intensity, so
                // the increment value is not very important, but
                // referenceCameraImage is good only for the value 1,
                // so let's use that
                int channel = i-s;
                gainBiasParameters[channel] += 1;
                setUpdateNeeded(true);
                return false;
            }
        }
        @Override public void compose(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            super.compose(p1, inverse1, p2, inverse2);
            composeGainBias(p1, inverse1, p2, inverse2);
        }
        public void composeGainBias(ImageTransformer.Parameters p1, boolean inverse1,
                ImageTransformer.Parameters p2, boolean inverse2) {
            assert (!inverse1 && !inverse2);

            Parameters pp1 = (Parameters)p1, pp2 = (Parameters)p2;
            CvMat A1 = pp1.getA(), b1 = pp1.getB();
            CvMat A2 = pp2.getA(), b2 = pp2.getB();

            if (b != null) {
                CvMat temp = null;
                if (pp1.fakeIdentity && X != null) {
                    temp = CvMat.take(3, 1);
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

                if (temp != null) {
                    temp.pool();
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
                case 1: gainBiasParameters[0] =
                            (A.get(0) + A.get(4) + A.get(8))/3; break;
                case 3: gainBiasParameters[0] = A.get(0);
                        gainBiasParameters[1] = A.get(4);
                        gainBiasParameters[2] = A.get(8); break;
                case 9: A.get(0, gainBiasParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: gainBiasParameters[numGains] =
                            (b.get(0) + b.get(1) + b.get(2))/3;   break;
                case 3: b.get(0, gainBiasParameters, numGains, 3); break;
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
                case 1: A.put(0, gainBiasParameters[0]);
                        A.put(4, gainBiasParameters[0]);
                        A.put(8, gainBiasParameters[0]); break;
                case 3: A.put(0, gainBiasParameters[0]);
                        A.put(4, gainBiasParameters[1]);
                        A.put(8, gainBiasParameters[2]); break;
                case 9: A.put(0, gainBiasParameters, 0, 9); break;
                default: assert (false);
            }
            switch (numBiases) {
                case 0: assert (b == null); break;
                case 1: b.put(0, gainBiasParameters[numGains]);
                        b.put(1, gainBiasParameters[numGains]);
                        b.put(2, gainBiasParameters[numGains]);    break;
                case 3: b.put(0, gainBiasParameters, numGains, 3); break;
                default: assert (false);
            }

            super.update();
            setUpdateNeeded(false);
        }

    }

}

