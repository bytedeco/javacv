/*
 * Copyright (C) 2011 Samuel Audet
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
 *
 *
 * This file is based on information found in compat.hpp, legacy.hpp, and
 * blobtrack.hpp of OpenCV 2.3.1, which are covered by the following copyright notice:
 *
 *                        Intel License Agreement
 *                For Open Source Computer Vision Library
 *
 * Copyright (C) 2000, Intel Corporation, all rights reserved.
 * Third party copyrights are property of their respective owners.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * Redistribution's of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistribution's in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   * The name of Intel Corporation may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * This software is provided by the copyright holders and contributors "as is" and
 * any express or implied warranties, including, but not limited to, the implied
 * warranties of merchantability and fitness for a particular purpose are disclaimed.
 * In no event shall the Intel Corporation or contributors be liable for any direct,
 * indirect, incidental, special, exemplary, or consequential damages
 * (including, but not limited to, procurement of substitute goods or services;
 * loss of use, data, or profits; or business interruption) however caused
 * and on any theory of liability, whether in contract, strict liability,
 * or tort (including negligence or otherwise) arising in any way out of
 * the use of this software, even if advised of the possibility of such damage.
 *
 */

package com.googlecode.javacv.cpp;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.annotation.ByPtrPtr;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Opaque;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(includepath=genericIncludepath, linkpath=genericLinkpath,
        include={"<opencv2/legacy/compat.hpp>", "<opencv2/legacy/legacy.hpp>", "<opencv2/legacy/blobtrack.hpp>"},
        link={"opencv_legacy", "opencv_video", "opencv_features2d", "opencv_flann", "opencv_calib3d", "opencv_highgui", "opencv_imgproc", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath,
        link={"opencv_legacy231", "opencv_video231", "opencv_features2d231", "opencv_flann231", "opencv_calib3d231", "opencv_highgui231", "opencv_imgproc231", "opencv_core231"}),
    @Platform(value="windows-x86",    linkpath=windowsx86Linkpath, preloadpath=windowsx86Preloadpath),
    @Platform(value="windows-x86_64", linkpath=windowsx64Linkpath, preloadpath=windowsx64Preloadpath),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_legacy {
    static { load(opencv_features2d.class); load(opencv_video.class); load(); }

    public static float cvQueryHistValue_1D(CvHistogram hist, int idx0) {
        return (float)cvGetReal1D(hist.bins(), idx0);
    }
    public static float cvQueryHistValue_2D(CvHistogram hist, int idx0, int idx1) {
        return (float)cvGetReal2D(hist.bins(), idx0, idx1);
    }
    public static float cvQueryHistValue_3D(CvHistogram hist, int idx0, int idx1, int idx2) {
        return (float)cvGetReal3D(hist.bins(), idx0, idx1, idx2);
    }
    public static float cvQueryHistValue_nD(CvHistogram hist, int idx0, int[] idx) {
        return (float)cvGetRealND(hist.bins(), idx);
    }

    public static Pointer cvGetHistValue_1D(CvHistogram hist, int idx0) {
        return cvPtr1D(hist.bins(), idx0, null);
    }
    public static Pointer cvGetHistValue_2D(CvHistogram hist, int idx0, int idx1) {
        return cvPtr2D(hist.bins(), idx0, idx1, null);
    }
    public static Pointer cvGetHistValue_3D(CvHistogram hist, int idx0, int idx1, int idx2) {
        return cvPtr3D(hist.bins(), idx0, idx1, idx2, null);
    }
    public static Pointer cvGetHistValue_nD(CvHistogram hist, int idx0, int[] idx) {
        return cvPtrND(hist.bins(), idx, null, 1, null);
    }


    public static native CvSeq cvSegmentImage(CvArr srcarr, CvArr dstarr,
            double canny_threshold, double ffill_threshold, CvMemStorage storage);


    public static class CvCallback extends FunctionPointer {
        static { load(); }
        public    CvCallback(Pointer p) { super(p); }
        protected CvCallback() { allocate(); }
        protected final native void allocate();
        public native int call(int index, Pointer buffer, Pointer user_data);
    }

    public static final int
            CV_EIGOBJ_NO_CALLBACK     = 0,
            CV_EIGOBJ_INPUT_CALLBACK  = 1,
            CV_EIGOBJ_OUTPUT_CALLBACK = 2,
            CV_EIGOBJ_BOTH_CALLBACK   = 3;

    public static native void cvCalcCovarMatrixEx(int nObjects, Pointer input, int ioFlags, int ioBufSize,
            @Cast("uchar*") BytePointer buffer, Pointer userData, IplImage avg, float[] covarMatrix);
    public static native void cvCalcEigenObjects(int nObjects, Pointer input, Pointer output, int ioFlags,
            int ioBufSize, Pointer userData, CvTermCriteria calcLimit, IplImage avg, float[] eigVals);
    public static native double cvCalcDecompCoeff(IplImage obj, IplImage eigObj, IplImage avg);
    public static native void cvEigenDecomposite(IplImage obj, int nEigObjs, Pointer eigInput,
            int ioFlags, Pointer userData, IplImage avg, float[] coeffs);
    public static native void cvEigenProjection(Pointer eigInput, int nEigObjs, int ioFlags,
            Pointer userData, float[] coeffs, IplImage avg, IplImage proj);

    public static native void cvCalcCovarMatrixEx(int nObjects, Pointer input, int ioFlags, int ioBufSize,
            @Cast("uchar*") BytePointer buffer, Pointer userData, IplImage avg, FloatPointer covarMatrix);
    public static native void cvCalcEigenObjects(int nObjects, Pointer input, Pointer output, int ioFlags,
            int ioBufSize, Pointer userData, CvTermCriteria calcLimit, IplImage avg, FloatPointer eigVals);
    public static native void cvEigenDecomposite(IplImage obj, int nEigObjs, Pointer eigInput,
            int ioFlags, Pointer userData, IplImage avg, FloatPointer coeffs);
    public static native void cvEigenProjection(Pointer eigInput, int nEigObjs, int ioFlags,
            Pointer userData, FloatPointer coeffs, IplImage avg, IplImage proj);


    public static class CvImgObsInfo extends Pointer {
        static { load(); }
        public CvImgObsInfo() { allocate(); }
        public CvImgObsInfo(int size) { allocateArray(size); }
        public CvImgObsInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvImgObsInfo position(int position) {
            return (CvImgObsInfo)super.position(position);
        }

        public static CvImgObsInfo create(CvSize numObs, int obsSize) {
            CvImgObsInfo p = cvCreateObsInfo(numObs, obsSize);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvImgObsInfo implements Deallocator {
            ReleaseDeallocator(CvImgObsInfo p) { super(p); }
            @Override public void deallocate() { cvReleaseObsInfo(this); }
        }

        public native int obs_x();        public native CvImgObsInfo obs_x(int obs_x);
        public native int obs_y();        public native CvImgObsInfo obs_y(int obs_y);
        public native int obs_size();     public native CvImgObsInfo obs_size(int obs_size);
        public native FloatPointer obs(); public native CvImgObsInfo obs(FloatPointer obs);

        public native IntPointer state(); public native CvImgObsInfo state(IntPointer state);
        public native IntPointer mix();   public native CvImgObsInfo mix(IntPointer mix);
    }

    @Opaque public static class Cv1DObsInfo extends CvImgObsInfo {
        static { load(); }
        public Cv1DObsInfo() { }
        public Cv1DObsInfo(Pointer p) { super(p); }
    }

    public static class CvEHMMState extends Pointer {
        static { load(); }
        public CvEHMMState() { allocate(); }
        public CvEHMMState(int size) { allocateArray(size); }
        public CvEHMMState(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvEHMMState position(int position) {
            return (CvEHMMState)super.position(position);
        }

        public native int num_mix();              public native CvEHMMState num_mix(int num_mix);
        public native FloatPointer mu();          public native CvEHMMState mu(FloatPointer mu);
        public native FloatPointer inv_var();     public native CvEHMMState inv_var(FloatPointer inv_var);
        public native FloatPointer log_var_val(); public native CvEHMMState log_var_val(FloatPointer log_var_val);
        public native FloatPointer weight();      public native CvEHMMState weight(FloatPointer weight);
    }

    public static class CvEHMM extends Pointer {
        static { load(); }
        public CvEHMM() { allocate(); }
        public CvEHMM(int size) { allocateArray(size); }
        public CvEHMM(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvEHMM position(int position) {
            return (CvEHMM)super.position(position);
        }

        public static CvEHMM create(int[] stateNumber, int[] numMix, int obsSize) {
            CvEHMM p = cvCreate2DHMM(stateNumber, numMix, obsSize);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvEHMM implements Deallocator {
            ReleaseDeallocator(CvEHMM p) { super(p); }
            @Override public void deallocate() { cvRelease2DHMM(this); }
        }

        public native int level();              public native CvEHMM level(int level);
        public native int num_states();         public native CvEHMM num_states(int num_states);
        public native FloatPointer transP();    public native CvEHMM transP(FloatPointer transP);
        @Cast("float**")
        public native PointerPointer obsProb(); public native CvEHMM obsProb(PointerPointer obsProb);
        
        @Name("u.state") public native CvEHMMState u_state(); public native CvEHMM u_state(CvEHMMState u_state);
        @Name("u.ehmm")  public native CvEHMM u_ehmm();       public native CvEHMM u_ehmm(CvEHMM u_ehmm);
    }

//    public static native int icvCreate1DHMM(@ByPtrPtr CvEHMM this_hmm,
//            int state_number, int[] num_mix, int obs_size);
//    public static native int icvRelease1DHMM(@ByPtrPtr CvEHMM phmm);
//    public static native int icvUniform1DSegm(Cv1DObsInfo obs_info, CvEHMM hmm);
//    public static native int icvInit1DMixSegm(@Cast("Cv1DObsInfo**") PointerPointer obs_info_array,
//            int num_img, CvEHMM hmm);
//    public static native int icvEstimate1DHMMStateParams(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
//            int num_img, CvEHMM hmm);
//    public static native int icvEstimate1DObsProb(CvImgObsInfo obs_info, CvEHMM hmm);
//    public static native int icvEstimate1DTransProb(@Cast("Cv1DObsInfo**") PointerPointer obs_info_array,
//            int num_seq, CvEHMM hmm);
//    public static native float icvViterbi(Cv1DObsInfo obs_info, CvEHMM hmm);
//    public static native int icv1DMixSegmL2(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
//            int num_img, CvEHMM hmm);

    public static native CvEHMM cvCreate2DHMM(int[] stateNumber, int[] numMix, int obsSize);
    public static native void cvRelease2DHMM(@ByPtrPtr CvEHMM  hmm);

    public static void CV_COUNT_OBS(CvSize roi, CvSize win, CvSize delta, CvSize numObs) {
        numObs.width((roi.width()  - win.width()  + delta.width())/delta.width());
        numObs.height((roi.height() - win.height() + delta.height())/delta.height());
    }

    public static native CvImgObsInfo cvCreateObsInfo(@ByVal CvSize numObs, int obsSize);
    public static native void cvReleaseObsInfo(@ByPtrPtr CvImgObsInfo obs_info);
    public static native void cvImgToObs_DCT(CvArr arr, float[] obs, @ByVal CvSize dctSize,
            @ByVal CvSize obsSize, @ByVal CvSize delta);
    public static native void cvImgToObs_DCT(CvArr arr, FloatPointer obs, @ByVal CvSize dctSize,
            @ByVal CvSize obsSize, @ByVal CvSize delta);
    public static native void cvUniformImgSegm(CvImgObsInfo obs_info, CvEHMM ehmm);
    public static native void cvInitMixSegm(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateHMMStateParams(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateTransProb(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
            int num_img, CvEHMM hmm);
    public static native void cvEstimateObsProb(CvImgObsInfo obs_info, CvEHMM hmm);
    public static native float cvEViterbi(CvImgObsInfo obs_info, CvEHMM hmm);
    public static native void cvMixSegmL2(@Cast("CvImgObsInfo**") PointerPointer obs_info_array,
            int num_img, CvEHMM hmm);


    public static native void cvCreateHandMask(CvSeq hand_points, IplImage img_mask, CvRect roi);
    public static native void cvFindHandRegion(CvPoint3D32f points, int count, CvSeq indexs,
            float[] line, @ByVal CvSize2D32f size, int flag, CvPoint3D32f center,
            CvMemStorage storage, @ByPtrPtr CvSeq numbers);
    public static native void cvFindHandRegionA(CvPoint3D32f points, int count, CvSeq indexs,
            float[] line, @ByVal CvSize2D32f size, int jc, CvPoint3D32f center,
            CvMemStorage storage, @ByPtrPtr CvSeq numbers);

    public static native void cvFindHandRegion(CvPoint3D32f points, int count, CvSeq indexs,
            FloatPointer line, @ByVal CvSize2D32f size, int flag, CvPoint3D32f center,
            CvMemStorage storage, @ByPtrPtr CvSeq numbers);
    public static native void cvFindHandRegionA(CvPoint3D32f points, int count, CvSeq indexs,
            FloatPointer line, @ByVal CvSize2D32f size, int jc, CvPoint3D32f center,
            CvMemStorage storage, @ByPtrPtr CvSeq numbers);

    public static native void cvCalcImageHomography(float[] line,
            CvPoint3D32f center, float[] intrinsic, float[] homography);
    public static native void cvCalcImageHomography(FloatPointer line,
            CvPoint3D32f center, FloatPointer intrinsic, FloatPointer homography);


    public static native void icvDrawMosaic(CvSubdiv2D subdiv, IplImage src, IplImage dst);
    public static native int icvSubdiv2DCheck(CvSubdiv2D subdiv);
    public static double icvSqDist2D32f(CvPoint2D32f pt1, CvPoint2D32f pt2) {
        double dx = pt1.x() - pt2.x();
        double dy = pt1.y() - pt2.y();

        return dx*dx + dy*dy;
    }


    public static int CV_CURRENT_INT(CvSeqReader reader) { return new IntPointer(reader.ptr()).get(); }
    public static int CV_PREV_INT(CvSeqReader reader) { return new IntPointer(reader.prev_elem()).get(); }

    public static class CvGraphWeightedVtx extends CvGraphVtx {
        static { load(); }
        public CvGraphWeightedVtx() { allocate(); }
        public CvGraphWeightedVtx(int size) { allocateArray(size); }
        public CvGraphWeightedVtx(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvGraphWeightedVtx position(int position) {
            return (CvGraphWeightedVtx)super.position(position);
        }

        public native float weight(); public native CvGraphWeightedVtx weight(float weight);
    }

    public static class CvGraphWeightedEdge extends CvGraphEdge { }

    // enum CvGraphWeightType
    public static final int
            CV_NOT_WEIGHTED = 0,
            CV_WEIGHTED_VTX = 1,
            CV_WEIGHTED_EDGE = 2,
            CV_WEIGHTED_ALL = 3;

    public static native void cvCalcPGH(CvSeq contour, CvHistogram hist);

    public static final int CV_DOMINANT_IPAN = 1;

    public static native CvSeq cvFindDominantPoints(CvSeq contour,
            CvMemStorage storage, int method/*=CV_DOMINANT_IPAN*/,
            double parameter1/*=0*/, double parameter2/*=0*/,
            double parameter3/*=0*/, double parameter4/*=0*/);


    public static class CvCliqueFinder extends Pointer {
        static { load(); }
        public CvCliqueFinder() { allocate(); }
        public CvCliqueFinder(int size) { allocateArray(size); }
        public CvCliqueFinder(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvCliqueFinder position(int position) {
            return (CvCliqueFinder)super.position(position);
        }

        public native CvGraph graph();               public native CvCliqueFinder graph(CvGraph graph);
        @Cast("int**")
        public native PointerPointer adj_matr();     public native CvCliqueFinder adj_matr(PointerPointer adj_matr);
        public native int N();                       public native CvCliqueFinder N(int N);

        public native int k();                       public native CvCliqueFinder k(int k);
        public native IntPointer current_comp();     public native CvCliqueFinder current_comp(IntPointer current_comp);
        @Cast("int**")
        public native PointerPointer All();          public native CvCliqueFinder All(PointerPointer All);

        public native IntPointer ne();               public native CvCliqueFinder ne(IntPointer ne);
        public native IntPointer ce();               public native CvCliqueFinder ce(IntPointer ce);
        public native IntPointer fixp();             public native CvCliqueFinder fixp(IntPointer fixp);
        public native IntPointer nod();              public native CvCliqueFinder nod(IntPointer nod);
        public native IntPointer s();                public native CvCliqueFinder s(IntPointer s);
        public native int status();                  public native CvCliqueFinder status(int status);
        public native int best_score();              public native CvCliqueFinder best_score(int best_score);
        public native int weighted();                public native CvCliqueFinder weighted(int weighted);
        public native int weighted_edges();          public native CvCliqueFinder weighted_edges(int weighted_edges);
        public native float best_weight();           public native CvCliqueFinder best_weight(float best_weight);
        public native FloatPointer edge_weights();   public native CvCliqueFinder edge_weights(FloatPointer edge_weights);
        public native FloatPointer vertex_weights(); public native CvCliqueFinder vertex_weights(FloatPointer vertex_weights);
        public native FloatPointer cur_weight();     public native CvCliqueFinder cur_weight(FloatPointer cur_weight);
        public native FloatPointer cand_weight();    public native CvCliqueFinder cand_weight(FloatPointer cand_weight);
    }

    public static final int
            CLIQUE_TIME_OFF = 2,
            CLIQUE_FOUND = 1,
            CLIQUE_END   = 0;

//    public static native void cvStartFindCliques(CvGraph graph, CvCliqueFinder finder,
//            int reverse, int weighted/*=0*/, int weighted_edges/*=0*/);
//    public static native int cvFindNextMaximalClique(CvCliqueFinder finder, int[] clock_rest/*=null*/);
//    public static native void cvEndFindCliques(CvCliqueFinder finder);
//    public static native void cvBronKerbosch(CvGraph graph);
//
//    public static native float cvSubgraphWeight(CvGraph graph, CvSeq subgraph,
//            int /* CvGraphWeightType */ weight_type/*=CV_NOT_WEIGHTED*/,
//            float[] weight_vtx/*=null*/, float[] weight_edge/*=null*/);
//
//    public static native  CvSeq cvFindCliqueEx(CvGraph graph, CvMemStorage storage,
//            int is_complementary/*=0*/, int /* CvGraphWeightType */ weight_type/*=CV_NOT_WEIGHTED*/,
//            float[] weight_vtx/*=null*/, float[] weight_edge/*=null*/,
//            CvSeq start_clique/*=null*/, CvSeq subgraph_of_ban/*=null*/,
//            float[] clique_weight_ptr/*=null*/, int num_generations/*=3*/, int quality/*=2*/);

    public static final int
        CV_UNDEF_SC_PARAM         = 12345,

        CV_IDP_BIRCHFIELD_PARAM1  = 25,
        CV_IDP_BIRCHFIELD_PARAM2  = 5,
        CV_IDP_BIRCHFIELD_PARAM3  = 12,
        CV_IDP_BIRCHFIELD_PARAM4  = 15,
        CV_IDP_BIRCHFIELD_PARAM5  = 25,

        CV_DISPARITY_BIRCHFIELD  = 0;

    public static native void cvFindStereoCorrespondence(CvArr leftImage, CvArr rightImage,
            int mode, CvArr dispImage, int maxDisparity, double param1/*=CV_UNDEF_SC_PARAM*/,
            double param2/*=CV_UNDEF_SC_PARAM*/,         double param3/*=CV_UNDEF_SC_PARAM*/,
            double param4/*=CV_UNDEF_SC_PARAM*/,         double param5/*=CV_UNDEF_SC_PARAM*/);


    public static class CvStereoLineCoeff extends Pointer {
        static { load(); }
        public CvStereoLineCoeff() { allocate(); }
        public CvStereoLineCoeff(int size) { allocateArray(size); }
        public CvStereoLineCoeff(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStereoLineCoeff position(int position) {
            return (CvStereoLineCoeff)super.position(position);
        }

        public native double Xcoef();   public native CvStereoLineCoeff Xcoef(double Xcoef);
        public native double XcoefA();  public native CvStereoLineCoeff XcoefA(double XcoefA);
        public native double XcoefB();  public native CvStereoLineCoeff XcoefB(double XcoefB);
        public native double XcoefAB(); public native CvStereoLineCoeff XcoefAB(double XcoefAB);

        public native double Ycoef();   public native CvStereoLineCoeff Ycoef(double Ycoef);
        public native double YcoefA();  public native CvStereoLineCoeff YcoefA(double YcoefA);
        public native double YcoefB();  public native CvStereoLineCoeff YcoefB(double YcoefB);
        public native double YcoefAB(); public native CvStereoLineCoeff YcoefAB(double YcoefAB);

        public native double Zcoef();   public native CvStereoLineCoeff Zcoef(double Zcoef);
        public native double ZcoefA();  public native CvStereoLineCoeff ZcoefA(double ZcoefA);
        public native double ZcoefB();  public native CvStereoLineCoeff ZcoefB(double ZcoefB);
        public native double ZcoefAB(); public native CvStereoLineCoeff ZcoefAB(double ZcoefAB);
    }

    public static class CvCamera extends Pointer {
        static { load(); }
        public CvCamera() { allocate(); }
        public CvCamera(int size) { allocateArray(size); }
        public CvCamera(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvCamera position(int position) {
            return (CvCamera)super.position(position);
        }

        @MemberGetter public native FloatPointer imgSize();    // float[2];
        @MemberGetter public native FloatPointer matrix();     // float[9];
        @MemberGetter public native FloatPointer distortion(); // float[4];
        @MemberGetter public native FloatPointer rotMatr();    // float[9];
        @MemberGetter public native FloatPointer transVect();  // float[3];
    }

    public static class CvStereoCamera extends Pointer {
        static { load(); }
        public CvStereoCamera() { allocate(); }
        public CvStereoCamera(int size) { allocateArray(size); }
        public CvStereoCamera(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvStereoCamera position(int position) {
            return (CvStereoCamera)super.position(position);
        }

        public native CvCamera/*[2]*/ camera(int i);
        public native CvStereoCamera camera(int i, CvCamera camera);
        @MemberGetter public native FloatPointer fundMatr(); // float[9];

        @ByRef public native CvPoint3D32f/*[2]*/ epipole(int i);
               public native CvStereoCamera epipole(int i, CvPoint3D32f epipole);
        @ByRef public native CvPoint2D32f/*[2][4]*/ quad(int i, int j);
               public native CvStereoCamera quad(int i, int j, CvPoint2D32f quad);
        public native double/*[2][3][3]*/ coeffs(int i, int j, int k);
               public native CvStereoCamera coeffs(int i, int j, int k, double coeffs);
        @ByRef public native CvPoint2D32f/*[2][4]*/ border(int i, int j);
               public native CvStereoCamera border(int i, int j, CvPoint2D32f epipole);
        @ByRef public native CvSize warpSize();
               public native CvStereoCamera warpSize(CvSize warpSize);
        public native CvStereoLineCoeff lineCoeffs();
               public native CvStereoCamera lineCoeffs(CvStereoLineCoeff lineCoeffs);
        public native int needSwapCameras();
               public native CvStereoCamera needSwapCameras(int needSwapCameras);
        @MemberGetter public native FloatPointer rotMatrix();   // float[9];
        @MemberGetter public native FloatPointer transVector(); // float[3];
    }

    public static class CvContourOrientation extends Pointer {
        static { load(); }
        public CvContourOrientation() { allocate(); }
        public CvContourOrientation(int size) { allocateArray(size); }
        public CvContourOrientation(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvContourOrientation position(int position) {
            return (CvContourOrientation)super.position(position);
        }

        public native float/*[2]*/ egvals(int i);  public native CvContourOrientation egvals(int i, float egvals);
        public native float/*[4]*/ egvects(int i); public native CvContourOrientation egvects(int i, float egvects);

        public native float max();                 public native CvContourOrientation max(float max);
        public native float min();                 public native CvContourOrientation min(float min);
        public native int imax();                  public native CvContourOrientation imax(int imax);
        public native int imin();                  public native CvContourOrientation imin(int imin);
    }

    public static final int
            CV_CAMERA_TO_WARP = 1,
            CV_WARP_TO_CAMERA = 2;

    public static native int icvConvertWarpCoordinates(@Cast("double(*)[3]") double[] coeffs/*[3][3]*/,
            CvPoint2D32f cameraPoint, CvPoint2D32f warpPoint, int direction);
    public static native int icvConvertWarpCoordinates(@Cast("double(*)[3]") DoublePointer coeffs/*[3][3]*/,
            CvPoint2D32f cameraPoint, CvPoint2D32f warpPoint, int direction);
    public static native int icvGetSymPoint3D(@ByVal CvPoint3D64f pointCorner,
            @ByVal CvPoint3D64f point1, @ByVal CvPoint3D64f point2, CvPoint3D64f pointSym2);
    public static native void icvGetPieceLength3D(@ByVal CvPoint3D64f point1,
            @ByVal CvPoint3D64f point2, double[] dist);
    public static native int icvCompute3DPoint(double alpha, double betta,
            CvStereoLineCoeff coeffs, CvPoint3D64f point);
    public static native int icvCreateConvertMatrVect(double[] rotMatr1,
            double[] transVect1,  double[] rotMatr2,  double[] transVect2,
            double[] convRotMatr, double[] convTransVect);
    public static native int icvConvertPointSystem(@ByVal CvPoint3D64f M2,
            CvPoint3D64f M1, double[] rotMatr, double[] transVect);
    public static native int icvCreateConvertMatrVect(DoublePointer rotMatr1,
            DoublePointer transVect1,  DoublePointer rotMatr2,  DoublePointer transVect2,
            DoublePointer convRotMatr, DoublePointer convTransVect);
    public static native int icvConvertPointSystem(@ByVal CvPoint3D64f M2,
            CvPoint3D64f M1, DoublePointer rotMatr, DoublePointer transVect);
    public static native int icvComputeCoeffForStereo(CvStereoCamera stereoCamera);

    public static native int icvGetCrossPieceVector(@ByVal CvPoint2D32f p1_start,
            @ByVal CvPoint2D32f p1_end, @ByVal CvPoint2D32f v2_start,
            @ByVal CvPoint2D32f v2_end, CvPoint2D32f cross);
    public static native int icvGetCrossLineDirect(@ByVal CvPoint2D32f p1,
            @ByVal CvPoint2D32f p2, float a, float b, float c, CvPoint2D32f cross);
    public static native float icvDefinePointPosition(@ByVal CvPoint2D32f point1,
            @ByVal CvPoint2D32f point2, @ByVal CvPoint2D32f point);
    public static native int icvStereoCalibration(int numImages, int[] nums, @ByVal CvSize imageSize,
            CvPoint2D32f imagePoints1, CvPoint2D32f imagePoints2,
            CvPoint3D32f objectPoints, CvStereoCamera stereoparams);

    public static native int icvComputeRestStereoParams(CvStereoCamera stereoparams);

    public static native void cvComputePerspectiveMap(@Cast("double(*)[3]") double[] coeffs/*[3][3]*/,
            CvArr rectMapX, CvArr rectMapY);
    public static native void cvComputePerspectiveMap(@Cast("double(*)[3]") DoublePointer coeffs/*[3][3]*/,
            CvArr rectMapX, CvArr rectMapY);

    public static native int icvComCoeffForLine(@ByVal CvPoint2D64f point1,
            @ByVal CvPoint2D64f point2, @ByVal CvPoint2D64f point3, @ByVal CvPoint2D64f point4,
            double[] camMatr1, double[] rotMatr1, double[] transVect1, double[] camMatr2,
            double[] rotMatr2, double[] transVect2, CvStereoLineCoeff coeffs, int[] needSwapCameras);
    public static native int icvComCoeffForLine(@ByVal CvPoint2D64f point1,
            @ByVal CvPoint2D64f point2, @ByVal CvPoint2D64f point3, @ByVal CvPoint2D64f point4,
            DoublePointer camMatr1, DoublePointer rotMatr1, DoublePointer transVect1, DoublePointer camMatr2,
            DoublePointer rotMatr2, DoublePointer transVect2, CvStereoLineCoeff coeffs, int[] needSwapCameras);
    public static native int icvGetDirectionForPoint(@ByVal CvPoint2D64f point,
            double[] camMatr, CvPoint3D64f direct);
    public static native int icvGetDirectionForPoint(@ByVal CvPoint2D64f point,
            DoublePointer camMatr, CvPoint3D64f direct);
    public static native int icvGetCrossLines(@ByVal CvPoint3D64f point11, @ByVal CvPoint3D64f point12,
            @ByVal CvPoint3D64f point21, @ByVal CvPoint3D64f point22, CvPoint3D64f midPoint);
    public static native int icvComputeStereoLineCoeffs(@ByVal CvPoint3D64f pointA, 
            @ByVal CvPoint3D64f pointB, @ByVal CvPoint3D64f pointCam1, double gamma, CvStereoLineCoeff coeffs);
//    public static native int icvComputeFundMatrEpipoles(double[] camMatr1, double[] rotMatr1,
//            double[] transVect1, double[] camMatr2, double[] rotMatr2, double[] transVect2,
//            CvPoint2D64f epipole1, CvPoint2D64f epipole2, double[] fundMatr);
    public static native int icvGetAngleLine(@ByVal CvPoint2D64f startPoint,
            @ByVal CvSize imageSize,  CvPoint2D64f point1, CvPoint2D64f point2);

    public static native void icvGetCoefForPiece(@ByVal CvPoint2D64f p_start, 
            @ByVal CvPoint2D64f p_end, double[] a, double[] b, double[] c, int[] result);
//    public static native void icvGetCommonArea(@ByVal CvSize imageSize, @ByVal CvPoint2D64f epipole1,
//            @ByVal CvPoint2D64f epipole2, double[] fundMatr, double[] coeff11, double[] coeff12,
//            double[] coeff21, double[] coeff22, int[] result);

    public static native void icvComputeeInfiniteProject1(double[] rotMatr, double[] camMatr1,
            double[] camMatr2, @ByVal CvPoint2D32f point1, CvPoint2D32f point2);
    public static native void icvComputeeInfiniteProject1(DoublePointer rotMatr, DoublePointer camMatr1,
            DoublePointer camMatr2, @ByVal CvPoint2D32f point1, CvPoint2D32f point2);
    public static native void icvComputeeInfiniteProject2(double[] rotMatr, double[] camMatr1,
            double[] camMatr2, CvPoint2D32f point1, @ByVal CvPoint2D32f point2);
    public static native void icvComputeeInfiniteProject2(DoublePointer rotMatr, DoublePointer camMatr1,
            DoublePointer camMatr2, CvPoint2D32f point1, @ByVal CvPoint2D32f point2);

    public static native void icvGetCrossDirectDirect(double[] direct1,
            double[] direct2, CvPoint2D64f cross, int[] result);
    public static native void icvGetCrossDirectDirect(DoublePointer direct1,
            DoublePointer direct2, CvPoint2D64f cross, int[] result);
    public static native void icvGetCrossPieceDirect(@ByVal CvPoint2D64f p_start,
            @ByVal CvPoint2D64f p_end, double a, double b, double c,
            CvPoint2D64f cross, int[] result);
    public static native void icvGetCrossPiecePiece(@ByVal CvPoint2D64f p1_start,
            @ByVal CvPoint2D64f p1_end, @ByVal CvPoint2D64f p2_start, 
            @ByVal CvPoint2D64f p2_end, CvPoint2D64f cross, int[] result);

    public static native void icvGetPieceLength(@ByVal CvPoint2D64f point1,
            @ByVal CvPoint2D64f point2, double[] dist);
    public static native void icvGetCrossRectDirect(@ByVal CvSize imageSize,
            double a, double b, double c, CvPoint2D64f start, CvPoint2D64f end,
            int[] result);
    public static native void icvProjectPointToImage(@ByVal CvPoint3D64f point,
            double[] camMatr, double[] rotMatr, double[] transVect, CvPoint2D64f projPoint);
    public static native void icvProjectPointToImage(@ByVal CvPoint3D64f point,
            DoublePointer camMatr, DoublePointer rotMatr, DoublePointer transVect, CvPoint2D64f projPoint);
    public static native void icvGetQuadsTransform(@ByVal CvSize imageSize,
            double[] camMatr1, double[] rotMatr1, double[] transVect1,
            double[] camMatr2, double[] rotMatr2, double[] transVect2,
            CvSize warpSize, @Cast("double(*)[2]") double[] quad1/*[4][2]*/,
            @Cast("double(*)[2]") double[] quad2/*[4][2]*/,
            double[] fundMatr, CvPoint3D64f epipole1, CvPoint3D64f epipole2);
    public static native void icvGetQuadsTransform(@ByVal CvSize imageSize,
            DoublePointer camMatr1, DoublePointer rotMatr1, DoublePointer transVect1,
            DoublePointer camMatr2, DoublePointer rotMatr2, DoublePointer transVect2,
            CvSize warpSize, @Cast("double(*)[2]") DoublePointer quad1/*[4][2]*/,
            @Cast("double(*)[2]") DoublePointer quad2/*[4][2]*/,
            DoublePointer fundMatr, CvPoint3D64f epipole1, CvPoint3D64f epipole2);

    public static native void icvGetQuadsTransformStruct(CvStereoCamera stereoCamera);
    public static native void icvComputeStereoParamsForCameras(CvStereoCamera stereoCamera);

    public static native void icvGetCutPiece(double[] areaLineCoef1,
            double[] areaLineCoef2, @ByVal CvPoint2D64f epipole, @ByVal CvSize imageSize,
            CvPoint2D64f point11, CvPoint2D64f point12,
            CvPoint2D64f point21, CvPoint2D64f point22, int[] result);
    public static native void icvGetCutPiece(DoublePointer areaLineCoef1,
            DoublePointer areaLineCoef2, @ByVal CvPoint2D64f epipole, @ByVal CvSize imageSize,
            CvPoint2D64f point11, CvPoint2D64f point12,
            CvPoint2D64f point21, CvPoint2D64f point22, int[] result);
    public static native void icvGetMiddleAnglePoint(@ByVal CvPoint2D64f basePoint,
            @ByVal CvPoint2D64f point1, @ByVal CvPoint2D64f point2, CvPoint2D64f midPoint);
    public static native void icvGetNormalDirect(double[] direct,
            @ByVal CvPoint2D64f point, double[] normDirect);
    public static native void icvGetNormalDirect(DoublePointer direct,
            @ByVal CvPoint2D64f point, DoublePointer normDirect);
    public static native double icvGetVect(@ByVal CvPoint2D64f basePoint,
            @ByVal CvPoint2D64f point1, @ByVal CvPoint2D64f point2);
    public static native void icvProjectPointToDirect(@ByVal CvPoint2D64f point,
            double[] lineCoeff, CvPoint2D64f projectPoint);
    public static native void icvProjectPointToDirect(@ByVal CvPoint2D64f point,
            DoublePointer lineCoeff, CvPoint2D64f projectPoint);
    public static native void icvGetDistanceFromPointToDirect(@ByVal CvPoint2D64f point,
            double[] lineCoef, double[] dist);
    public static native void icvGetDistanceFromPointToDirect(@ByVal CvPoint2D64f point,
            DoublePointer lineCoef, double[] dist);

    public static native IplImage icvCreateIsometricImage(IplImage src, IplImage dst,
            int desired_depth, int desired_num_channels);

    public static native void cvDeInterlace(CvArr frame, CvArr fieldEven, CvArr fieldOdd);

//    public static native int icvSelectBestRt(int numImages, int[] numPoints, @ByVal CvSize imageSize,
//            CvPoint2D32f imagePoints1, CvPoint2D32f imagePoints2, CvPoint3D32f objectPoints,
//            float[] cameraMatrix1, float[] distortion1, float[] rotMatrs1, float[] transVects1,
//            float[] cameraMatrix2, float[] distortion2, float[] rotMatrs2, float[] transVects2,
//            float[] bestRotMatr,   float[] bestTransVect);


    public static class CvContourTree extends CvSeq {
        static { load(); }
        public CvContourTree() { allocate(); }
        public CvContourTree(int size) { allocateArray(size); }
        public CvContourTree(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvContourTree position(int position) {
            return (CvContourTree)super.position(position);
        }

        @ByRef public native CvPoint p1(); public native CvContourTree p1(CvPoint p1);
        @ByRef public native CvPoint p2(); public native CvContourTree p2(CvPoint p2);
    }

    public static native CvContourTree cvCreateContourTree(CvSeq contour, CvMemStorage storage, double threshold);
    public static native CvSeq cvContourFromContourTree(CvContourTree tree,
            CvMemStorage storage, @ByVal CvTermCriteria criteria);
    public static final int CV_CONTOUR_TREES_MATCH_I1 = 1;
    public static native double cvMatchContourTrees(CvContourTree tree1, CvContourTree tree2,
            int method/*=CV_CONTOUR_TREES_MATCH_I1*/, double threshold);

//    public static native CvSeq cvCalcContoursCorrespondence(CvSeq contour1,
//            CvSeq contour2, CvMemStorage storage);
//    public static native CvSeq cvMorphContours(CvSeq contour1, CvSeq contour2,
//            CvSeq corr, double alpha, CvMemStorage storage);

    public static final int
            CV_VALUE = 1,
            CV_ARRAY = 2;
    public static native void cvSnakeImage(IplImage image, CvPoint points, int length,
            float[] alpha, float[] beta, float[] gamma, int coeff_usage,
            @ByVal CvSize win, @ByVal CvTermCriteria criteria, int calc_gradient/*=1*/);
    public static native void cvSnakeImage(IplImage image, CvPoint points, int length,
            FloatPointer alpha, FloatPointer beta, FloatPointer gamma, int coeff_usage,
            @ByVal CvSize win, @ByVal CvTermCriteria criteria, int calc_gradient/*=1*/);


    public static final int
            CV_GLCM_OPTIMIZATION_NONE                  = -2,
            CV_GLCM_OPTIMIZATION_LUT                   = -1,
            CV_GLCM_OPTIMIZATION_HISTOGRAM             = 0,

            CV_GLCMDESC_OPTIMIZATION_ALLOWDOUBLENEST   = 10,
            CV_GLCMDESC_OPTIMIZATION_ALLOWTRIPLENEST   = 11,
            CV_GLCMDESC_OPTIMIZATION_HISTOGRAM         = 4,

            CV_GLCMDESC_ENTROPY                        = 0,
            CV_GLCMDESC_ENERGY                         = 1,
            CV_GLCMDESC_HOMOGENITY                     = 2,
            CV_GLCMDESC_CONTRAST                       = 3,
            CV_GLCMDESC_CLUSTERTENDENCY                = 4,
            CV_GLCMDESC_CLUSTERSHADE                   = 5,
            CV_GLCMDESC_CORRELATION                    = 6,
            CV_GLCMDESC_CORRELATIONINFO1               = 7,
            CV_GLCMDESC_CORRELATIONINFO2               = 8,
            CV_GLCMDESC_MAXIMUMPROBABILITY             = 9,

            CV_GLCM_ALL                                = 0,
            CV_GLCM_GLCM                               = 1,
            CV_GLCM_DESC                               = 2;

    @Opaque public static class CvGLCM extends Pointer {
        static { load(); }
        public CvGLCM() { }
        public CvGLCM(Pointer p) { super(p); }

        public static CvGLCM create(IplImage srcImage, int stepMagnitude) {
            return create(srcImage, stepMagnitude, null, 0, CV_GLCM_OPTIMIZATION_NONE);
        }
        public static CvGLCM create(IplImage srcImage, int stepMagnitude,
                int[] stepDirections/*=null*/, int numStepDirections/*=0*/,
                int optimizationType/*=CV_GLCM_OPTIMIZATION_NONE*/) {
            CvGLCM p = cvCreateGLCM(srcImage, stepMagnitude, stepDirections,
                    numStepDirections, optimizationType);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvGLCM implements Deallocator {
            ReleaseDeallocator(CvGLCM p) { super(p); }
            @Override public void deallocate() { cvReleaseGLCM(this, CV_GLCM_ALL); }
        }
    }

    public static native CvGLCM cvCreateGLCM(IplImage srcImage, int stepMagnitude,
            int[] stepDirections/*=null*/, int numStepDirections/*=0*/,
            int optimizationType/*=CV_GLCM_OPTIMIZATION_NONE*/);
    public static native void cvReleaseGLCM(@ByPtrPtr CvGLCM GLCM, int flag/*=CV_GLCM_ALL*/);
    public static native void cvCreateGLCMDescriptors(CvGLCM destGLCM,
            int descriptorOptimizationType/*=CV_GLCMDESC_OPTIMIZATION_ALLOWDOUBLENEST*/);
    public static native double cvGetGLCMDescriptor(CvGLCM GLCM, int step, int descriptor);
    public static native void cvGetGLCMDescriptorStatistics(CvGLCM GLCM, int descriptor,
            double[] average, double[] standardDeviation);
    public static native IplImage cvCreateGLCMImage(CvGLCM GLCM, int step);


    @Opaque public static class CvFaceTracker extends Pointer {
        static { load(); }
        public CvFaceTracker() { }
        public CvFaceTracker(Pointer p) { super(p); }

        public static CvFaceTracker create(CvFaceTracker pFaceTracking,
                IplImage imgGray, CvRect pRects, int nRects) {
            CvFaceTracker p = cvInitFaceTracker(new CvFaceTracker(), imgGray, pRects, nRects);
            if (p != null) {
                p.deallocator(new ReleaseDeallocator(p));
            }
            return p;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvFaceTracker implements Deallocator {
            ReleaseDeallocator(CvFaceTracker p) { super(p); }
            @Override public void deallocate() { cvReleaseFaceTracker(this); }
        }
    }

    public static final int
            CV_NUM_FACE_ELEMENTS   = 3,
    // enum CV_FACE_ELEMENTS
            CV_FACE_MOUTH = 0,
            CV_FACE_LEFT_EYE = 1,
            CV_FACE_RIGHT_EYE = 2;

    public static native CvFaceTracker cvInitFaceTracker(CvFaceTracker pFaceTracking,
            IplImage imgGray, CvRect pRects, int nRects);
    public static native int cvTrackFace(CvFaceTracker pFaceTracker, IplImage imgGray,
            CvRect pRects, int nRects, CvPoint ptRotate, double[] dbAngleRotate);
    public static native void cvReleaseFaceTracker(@ByPtrPtr CvFaceTracker ppFaceTracker);

    public static class CvFace extends Pointer {
        static { load(); }
        public CvFace() { allocate(); }
        public CvFace(int size) { allocateArray(size); }
        public CvFace(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvFace position(int position) {
            return (CvFace)super.position(position);
        }

        @ByRef public native CvRect MouthRect();    public native CvFace MouthRect(CvRect MouthRect);
        @ByRef public native CvRect LeftEyeRect();  public native CvFace LeftEyeRect(CvRect LeftEyeRect);
        @ByRef public native CvRect RightEyeRect(); public native CvFace RightEyeRect(CvRect RightEyeRect);
    }

//    public static native CvSeq cvFindFace(IplImage Image, CvMemStorage storage);
//    public static native CvSeq cvPostBoostingFindFace(IplImage Image, CvMemStorage storage);


    // typedef unsigned char CvBool;

    public static class Cv3dTracker2dTrackedObject extends Pointer {
        static { load(); }
        public Cv3dTracker2dTrackedObject() { allocate(); }
        public Cv3dTracker2dTrackedObject(int size) { allocateArray(size); }
        public Cv3dTracker2dTrackedObject(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv3dTracker2dTrackedObject position(int position) {
            return (Cv3dTracker2dTrackedObject)super.position(position);
        }

        public native int id();         public native Cv3dTracker2dTrackedObject id(int id);
        @ByRef
        public native CvPoint2D32f p(); public native Cv3dTracker2dTrackedObject p(CvPoint2D32f p);
    }
    public static Cv3dTracker2dTrackedObject cv3dTracker2dTrackedObject(int id, CvPoint2D32f p) {
        Cv3dTracker2dTrackedObject r = new Cv3dTracker2dTrackedObject();
        r.id(id);
        r.p(p);
        return r;
    }

    public static class Cv3dTrackerTrackedObject extends Pointer {
        static { load(); }
        public Cv3dTrackerTrackedObject() { allocate(); }
        public Cv3dTrackerTrackedObject(int size) { allocateArray(size); }
        public Cv3dTrackerTrackedObject(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv3dTrackerTrackedObject position(int position) {
            return (Cv3dTrackerTrackedObject)super.position(position);
        }

        public native int id();         public native Cv3dTrackerTrackedObject id(int id);
        @ByRef
        public native CvPoint3D32f p(); public native Cv3dTrackerTrackedObject p(CvPoint3D32f p);
    }
    public static Cv3dTrackerTrackedObject cv3dTrackerTrackedObject(int id, CvPoint3D32f p) {
        Cv3dTrackerTrackedObject r = new Cv3dTrackerTrackedObject();
        r.id(id);
        r.p(p);
        return r;
    }

    public static class Cv3dTrackerCameraInfo extends Pointer {
        static { load(); }
        public Cv3dTrackerCameraInfo() { allocate(); }
        public Cv3dTrackerCameraInfo(int size) { allocateArray(size); }
        public Cv3dTrackerCameraInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv3dTrackerCameraInfo position(int position) {
            return (Cv3dTrackerCameraInfo)super.position(position);
        }

        public native boolean/*CvBool*/ valid();
               public native Cv3dTrackerCameraInfo valid(boolean valid);
        // float mat[4][4]
        @MemberGetter public native @Cast("float(*)[4]") FloatPointer mat(); 
        @ByRef public native CvPoint2D32f principal_point();
               public native Cv3dTrackerCameraInfo principal_point(CvPoint2D32f principal_point);
    }

    public static class Cv3dTrackerCameraIntrinsics extends Pointer {
        static { load(); }
        public Cv3dTrackerCameraIntrinsics() { allocate(); }
        public Cv3dTrackerCameraIntrinsics(int size) { allocateArray(size); }
        public Cv3dTrackerCameraIntrinsics(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public Cv3dTrackerCameraIntrinsics position(int position) {
            return (Cv3dTrackerCameraIntrinsics)super.position(position);
        }

        @ByRef public native CvPoint2D32f principal_point();
               public native Cv3dTrackerCameraIntrinsics principal_point(CvPoint2D32f principal_point);
        @MemberGetter public native FloatPointer focal_length(); // float[2]
        @MemberGetter public native FloatPointer distortion();   // float[4];
    }

    public static native boolean/*CvBool*/ cv3dTrackerCalibrateCameras(int num_cameras,
            Cv3dTrackerCameraIntrinsics camera_intrinsics, @ByVal CvSize etalon_size,
            float square_size, IplImageArray samples, Cv3dTrackerCameraInfo camera_info);
    public static native int cv3dTrackerLocateObjects(int num_cameras, int num_objects,
            Cv3dTrackerCameraInfo camera_info, Cv3dTracker2dTrackedObject tracking_info,
            Cv3dTrackerTrackedObject tracked_objects);


    // enum CvLeeParameters
    public static final int
            CV_LEE_INT = 0,
            CV_LEE_FLOAT = 1,
            CV_LEE_DOUBLE = 2,
            CV_LEE_AUTO = -1,
            CV_LEE_ERODE = 0,
            CV_LEE_ZOOM = 1,
            CV_LEE_NON = 2;

    public static CvVoronoiSite2D CV_NEXT_VORONOISITE2D(CvVoronoiSite2D SITE) {
        return SITE.edge(0).site((SITE.edge(0).site(0) == SITE) ? 1 : 0);
    }
    public static CvVoronoiSite2D CV_PREV_VORONOISITE2D(CvVoronoiSite2D SITE) {
        return SITE.edge(1).site((SITE.edge(1).site(0) == SITE) ? 1 : 0);
    }
    public static CvVoronoiEdge2D CV_FIRST_VORONOIEDGE2D(CvVoronoiSite2D SITE) {
        return SITE.edge(0);
    }
    public static CvVoronoiEdge2D CV_LAST_VORONOIEDGE2D(CvVoronoiSite2D SITE) {
        return SITE.edge(1);
    }
    public static CvVoronoiEdge2D CV_NEXT_VORONOIEDGE2D( CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.next((EDGE.site(0) != SITE) ? 1 : 0);
    }
    public static CvVoronoiEdge2D CV_PREV_VORONOIEDGE2D(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.next(2 + ((EDGE.site(0) != SITE) ? 1 : 0));
    }
    public static CvVoronoiNode2D CV_VORONOIEDGE2D_BEGINNODE(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.node((EDGE.site(0) != SITE) ? 1 : 0);
    }
    public static CvVoronoiNode2D CV_VORONOIEDGE2D_ENDNODE(CvVoronoiEdge2D EDGE, CvVoronoiSite2D SITE) {
        return EDGE.node((EDGE.site(0) == SITE) ? 1 : 0);
    }
    public static CvVoronoiSite2D CV_TWIN_VORONOISITE2D(CvVoronoiSite2D SITE, CvVoronoiEdge2D EDGE) {
        return EDGE.site((EDGE.site(0) == SITE) ? 1 : 0);
    }

    public static class CvVoronoiSite2D extends Pointer {
        static { load(); }
        public CvVoronoiSite2D() { allocate(); }
        public CvVoronoiSite2D(int size) { allocateArray(size); }
        public CvVoronoiSite2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvVoronoiSite2D position(int position) {
            return (CvVoronoiSite2D)super.position(position);
        }

        public native CvVoronoiNode2D/*[2]*/ node(int i); public native CvVoronoiSite2D node(int i, CvVoronoiNode2D node);
        public native CvVoronoiEdge2D/*[2]*/ edge(int i); public native CvVoronoiSite2D edge(int i, CvVoronoiEdge2D edge);

        public native CvVoronoiSite2D/*[2]*/ next(int i); public native CvVoronoiSite2D next(int i, CvVoronoiSite2D next);
    }

    public static class CvVoronoiEdge2D extends Pointer {
        static { load(); }
        public CvVoronoiEdge2D() { allocate(); }
        public CvVoronoiEdge2D(int size) { allocateArray(size); }
        public CvVoronoiEdge2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvVoronoiEdge2D position(int position) {
            return (CvVoronoiEdge2D)super.position(position);
        }

        public native CvVoronoiNode2D/*[2]*/ node(int i); public native CvVoronoiEdge2D node(int i, CvVoronoiNode2D node);
        public native CvVoronoiSite2D/*[2]*/ site(int i); public native CvVoronoiEdge2D site(int i, CvVoronoiSite2D site);
        public native CvVoronoiEdge2D/*[4]*/ next(int i); public native CvVoronoiEdge2D next(int i, CvVoronoiEdge2D next);
    }

    public static class CvVoronoiNode2D extends CvSetElem {
        static { load(); }
        public CvVoronoiNode2D() { allocate(); }
        public CvVoronoiNode2D(int size) { allocateArray(size); }
        public CvVoronoiNode2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvVoronoiNode2D position(int position) {
            return (CvVoronoiNode2D)super.position(position);
        }

        @ByRef
        public native CvPoint2D32f pt(); public native CvVoronoiNode2D pt(CvPoint2D32f pt);
        public native float radius();    public native CvVoronoiNode2D radius(float radius);
    }

    public static class CvVoronoiDiagram2D extends CvGraph {
        static { load(); }
        public CvVoronoiDiagram2D() { allocate(); }
        public CvVoronoiDiagram2D(int size) { allocateArray(size); }
        public CvVoronoiDiagram2D(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvVoronoiDiagram2D position(int position) {
            return (CvVoronoiDiagram2D)super.position(position);
        }

        public native CvSet sites(); public native CvVoronoiDiagram2D sites(CvSet sites);
    }

    public static native int cvVoronoiDiagramFromContour(CvSeq ContourSeq,
            @ByPtrPtr CvVoronoiDiagram2D VoronoiDiagram,
            CvMemStorage VoronoiStorage, @Cast("CvLeeParameters") int contour_type/*=CV_LEE_INT*/,
            int contour_orientation/*=-1*/, int attempt_number/*=10*/);
    public static native int cvVoronoiDiagramFromImage(IplImage pImage,
            @ByPtrPtr CvSeq ContourSeq, @ByPtrPtr CvVoronoiDiagram2D VoronoiDiagram,
            CvMemStorage VoronoiStorage, @Cast("CvLeeParameters") int regularization_method/*=CV_LEE_NON*/,
            float approx_precision/*=CV_LEE_AUTO*/);
    public static native void cvReleaseVoronoiStorage(CvVoronoiDiagram2D VoronoiDiagram,
            @ByPtrPtr CvMemStorage pVoronoiStorage);


    public static class CvLCMEdge extends CvGraphEdge {
        static { load(); }
        public CvLCMEdge() { allocate(); }
        public CvLCMEdge(int size) { allocateArray(size); }
        public CvLCMEdge(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLCMEdge position(int position) {
            return (CvLCMEdge)super.position(position);
        }

        public native CvSeq chain(); public native CvLCMEdge chain(CvSeq chain);
        public native float width(); public native CvLCMEdge width(float width);
        public native int index1();  public native CvLCMEdge index1(int index1);
        public native int index2();  public native CvLCMEdge index2(int index2);
    }

    public static class CvLCMNode extends CvGraphVtx {
        static { load(); }
        public CvLCMNode() { allocate(); }
        public CvLCMNode(int size) { allocateArray(size); }
        public CvLCMNode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvLCMNode position(int position) {
            return (CvLCMNode)super.position(position);
        }

        public native CvContour contour(); public native CvLCMNode contour(CvContour contour);
    }

    public static native CvGraph cvLinearContorModelFromVoronoiDiagram(
            CvVoronoiDiagram2D VoronoiDiagram, float maxWidth);
    public static native int cvReleaseLinearContorModelStorage(@ByPtrPtr CvGraph Graph);


    public static native void cvInitPerspectiveTransform(@ByVal CvSize size,
            CvPoint2D32f vertex/*[4]*/, @Cast("double(*)[3]") double[] matrix/*[3][3]*/, CvArr rectMap);
    public static native void cvInitPerspectiveTransform(@ByVal CvSize size,
            CvPoint2D32f vertex/*[4]*/, @Cast("double(*)[3]") DoublePointer matrix/*[3][3]*/, CvArr rectMap);

//    public static native void cvInitStereoRectification(CvStereoCamera params,
//            CvArr rectMap1, CvArr rectMap2, int do_undistortion);


    public static native void cvMakeScanlines(@Cast("CvMatrix3*") float[] matrix/*[3][3]*/, @ByVal CvSize img_size,
            int[] scanlines1, int[] scanlines2, int[] lengths1, int[] lengths2, int[] line_count);
    public static native void cvMakeScanlines(@Cast("CvMatrix3*") FloatPointer matrix/*[3][3]*/, @ByVal CvSize img_size,
            int[] scanlines1, int[] scanlines2, int[] lengths1, int[] lengths2, int[] line_count);
    public static native void cvPreWarpImage(int line_count, IplImage img,
            @Cast("uchar*") BytePointer dst, int[] dst_nums, int[] scanlines);
    public static native void cvFindRuns(int line_count, @Cast("uchar*") BytePointer prewarp1, 
            @Cast("uchar*") BytePointer prewarp2, int[] line_lengths1, int[] line_lengths2,
            int[] runs1, int[] runs2, int[] num_runs1, int[] num_runs2);
    public static native void cvDynamicCorrespondMulti(int  line_count, int[] first, int[] first_runs,
            int[] second, int[] second_runs, int[] first_corr, int[] second_corr);
    public static native void cvMakeAlphaScanlines(int[] scanlines1, int[] scanlines2, int[] scanlinesA,
            int[] lengths, int line_count, float alpha);
    public static native void cvMorphEpilinesMulti(int line_count, 
            @Cast("uchar*") BytePointer first_pix, int[] first_num,
            @Cast("uchar*") BytePointer second_pix, int[] second_num,
            @Cast("uchar*") BytePointer dst_pix, int[] dst_num, float alpha,
            int[] first, int[] first_runs, int[] second, int[] second_runs, int[] first_corr, int[] second_corr);
    public static native void cvPostWarpImage(int line_count,
            @Cast("uchar*") BytePointer src, int[] src_nums, IplImage img, int[] scanlines);

    public static native void cvDeleteMoire(IplImage img);


    public static class CvRandState extends Pointer {
        static { load(); }
        public CvRandState() { allocate(); }
        public CvRandState(int size) { allocateArray(size); }
        public CvRandState(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvRandState position(int position) {
            return (CvRandState)super.position(position);
        }

        public native long /* CvRNG */ state();     public native CvRandState state(long state);
        public native int disttype();               public native CvRandState disttype(int disttype);
        @ByRef
        public native CvScalar/*[2]*/ param(int i); public native CvRandState param(int i, CvScalar param);
    }

    public static class CvConDensation extends Pointer {
        static { load(); }
        public CvConDensation() { allocate(); }
        public CvConDensation(int size) { allocateArray(size); }
        public CvConDensation(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Override public CvConDensation position(int position) {
            return (CvConDensation)super.position(position);
        }

        public static CvConDensation create(int dynam_params, int measure_params,
                int sample_count) {
            CvConDensation c = cvCreateConDensation(dynam_params, measure_params, sample_count);
            if (c != null) {
                c.deallocator(new ReleaseDeallocator(c));
            }
            return c;
        }

        public void release() {
            deallocate();
        }
        static class ReleaseDeallocator extends CvConDensation implements Deallocator {
            ReleaseDeallocator(CvConDensation p) { super(p); }
            @Override public void deallocate() { cvReleaseConDensation(this); }
        }

        public native int MP();                      public native CvConDensation MP(int MP);
        public native int DP();                      public native CvConDensation DP(int DP);
        public native FloatPointer DynamMatr();      public native CvConDensation DynamMatr(FloatPointer DynamMatr);
        public native FloatPointer State();          public native CvConDensation State(FloatPointer State);
        public native int SamplesNum();              public native CvConDensation SamplesNum(int SamplesNum);
        @Cast("float**")
        public native PointerPointer flSamples();    public native CvConDensation flSamples(PointerPointer flSamples);
        @Cast("float**") 
        public native PointerPointer flNewSamples(); public native CvConDensation flNewSamples(PointerPointer flNewSamples);
        public native FloatPointer flConfidence();   public native CvConDensation flConfidence(FloatPointer flConfidence);
        public native FloatPointer flCumulative();   public native CvConDensation flCumulative(FloatPointer flCumulative);
        public native FloatPointer Temp();           public native CvConDensation Temp(FloatPointer Temp);
        public native FloatPointer RandomSample();   public native CvConDensation RandomSample(FloatPointer RandomSample);
        public native CvRandState RandS();           public native CvConDensation RandS(CvRandState RandS);
    }

    public static native CvConDensation cvCreateConDensation(int dynam_params, int measure_params, int sample_count);
    public static native void cvReleaseConDensation(@ByPtrPtr CvConDensation condens);
    public static native void cvConDensUpdateByTime(CvConDensation condens);
    public static native void cvConDensInitSampleSet(CvConDensation condens, CvMat lower_bound, CvMat upper_bound);

    public static int iplWidth(IplImage img) {
        return img == null ? 0 : img.roi() == null ? img.width() : img.roi().width();
    }

    public static int iplHeight(IplImage img) {
        return img == null ? 0 : img.roi() == null ? img.height() : img.roi().height();
    }


    public static final int
    // enum CvCalibEtalonType
        CV_CALIB_ETALON_USER = -1,
        CV_CALIB_ETALON_CHESSBOARD = 0,
        CV_CALIB_ETALON_CHECKERBOARD = CV_CALIB_ETALON_CHESSBOARD;

    public static class CvCalibFilter extends Pointer {
        static { load(); }
        public CvCalibFilter() { allocate(); }
        public CvCalibFilter(Pointer p) { super(p); }
        private native void allocate();

        public native boolean SetEtalon(@Cast("CvCalibEtalonType") int etalonType,
                double[] etalonParams, int pointCount/*=0*/, CvPoint2D32f points/*=null*/);
        public native @Cast("CvCalibEtalonType") int GetEtalon(int[] paramCount/*=0*/,
                @Cast("const double**") PointerPointer etalonParams/*=0*/, int[] pointCount/*=0*/,
                @Cast("const CvPoint2D32f**") PointerPointer etalonPoints/*=null*/);
        public native void SetCameraCount(int cameraCount);
        public native int GetCameraCount();
        public native boolean SetFrames(int totalFrames);
        public native void Stop(boolean calibrate/*=false*/);
        public native boolean IsCalibrated();
        public native boolean FindEtalon(IplImageArray imgs);
        public native boolean FindEtalon(CvMatArray imgs);
        public native boolean Push(@Cast("const CvPoint2D32f**") PointerPointer points/*=0*/);
        public native int GetFrameCount(int[] framesTotal/*=null*/);
        public native @Const CvCamera GetCameraParams(int idx/*=0*/);
        public native @Const CvStereoCamera GetStereoParams();
        public native boolean SetCameraParams(CvCamera params);
        public native boolean SaveCameraParams(String filename );
        public native boolean LoadCameraParams(String filename );
        public native boolean Undistort(IplImageArray src, IplImageArray dst);
        public native boolean Undistort(CvMatArray src, CvMatArray dst );
        public native boolean GetLatestPoints(int idx, @Cast("CvPoint2D32f**")
                PointerPointer pts, int[] count, @Cast("bool*") boolean[] found);
        public native void DrawPoints(IplImageArray dst);
        public native void DrawPoints(CvMatArray dst);
        public native boolean Rectify( IplImageArray srcarr, IplImageArray dstarr );
        public native boolean Rectify( CvMatArray srcarr, CvMatArray dstarr );

//        protected static final int MAX_CAMERAS = 3;
//
//        /* etalon data */
//        protected native @Cast("CvCalibEtalonType") int etalonType();
//        protected native int etalonParamCount();
//        protected native DoublePointer etalonParams();
//        protected native int etalonPointCount();
//        protected native CvPoint2D32f etalonPoints();
//        protected native @ByRef CvSize imgSize();
//        protected native CvMat grayImg();
//        protected native CvMat tempImg();
//        protected native CvMemStorage storage();
//
//        /* camera data */
//        protected native int cameraCount();
//        protected native @ByRef CvCamera/*[MAX_CAMERAS]*/ cameraParams(int i);
//        protected native @ByRef CvStereoCamera stereo();
//        protected native CvPoint2D32f/*[MAX_CAMERAS]*/ points(int i);
//        protected native CvMat/*[MAX_CAMERAS][2]*/ undistMap(int i, int j);
//        protected native CvMat undistImg();
//        protected native int/*[MAX_CAMERAS]*/ latestCounts(int i);
//        protected native CvPoint2D32f/*[MAX_CAMERAS]*/ latestPoints(int i);
//        protected native CvMat/*[MAX_CAMERAS][2]*/ rectMap(int i, int j);
//
//        /* Added by Valery */
//        //protected native @ByRef CvStereoCamera stereoParams();
//
//        protected native int maxPoints();
//        protected native int framesTotal();
//        protected native int framesAccepted();
//        protected native boolean isCalibrated();
    }

    public static class CvCamShiftTracker extends Pointer {
        static { load(); }
        public CvCamShiftTracker() { allocate(); }
        public CvCamShiftTracker(Pointer p) { super(p); }
        private native void allocate();

        public native float get_orientation();
        public native float get_length();
        public native float get_width();
        public native @ByVal CvPoint2D32f get_center();
        public native @ByVal CvRect get_window();

        public native int get_threshold();
        public native int get_hist_dims(int[] dims/*=null*/);
        public native int get_min_ch_val(int channel);
        public native int get_max_ch_val(int channel);

        public native boolean set_window(@ByVal CvRect window);
        public native boolean set_threshold(int threshold);
        public native boolean set_hist_bin_range(int dim, int min_val, int max_val);
        public native boolean set_hist_dims(int c_dims, int[] dims);
        public native boolean set_min_ch_val(int channel, int val);
        public native boolean set_max_ch_val(int channel, int val);

        public native boolean track_object(IplImage cur_frame);
        public native boolean update_histogram(IplImage cur_frame);
        public native void reset_histogram();
        public native IplImage get_back_project();

        public native float query(int[] bin);

//        protected native void color_transform(IplImage img);
//
//        protected native CvHistogram m_hist();
//
//        protected native @ByRef CvBox2D    m_box();
//        protected native @ByRef CvConnectedComp m_comp();
//
//        protected native float/*[CV_MAX_DIM][2]*/     m_hist_ranges_data(int i, int j);
//        protected native FloatPointer/*[CV_MAX_DIM]*/ m_hist_ranges(int i);
//
//        protected native int/*[CV_MAX_DIM]*/ m_min_ch_val(int i);
//        protected native int/*[CV_MAX_DIM]*/ m_max_ch_val(int i);
//        protected native int                 m_threshold();
//
//        protected native IplImage/*[CV_MAX_DIM]*/ m_color_planes(int i);
//        protected native IplImage  m_back_project();
//        protected native IplImage  m_temp();
//        protected native IplImage  m_mask();
    }


    // #include <blobtrack.hpp>
    public static class CvDefParam extends Pointer {
        static { load(); }
        public CvDefParam() { allocate(); }
        public CvDefParam(int size) { allocateArray(size); }
        public CvDefParam(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native CvDefParam     next();     public native CvDefParam next(CvDefParam next);
        @Cast("char*")
        public native BytePointer    pName();    public native CvDefParam pName(BytePointer pName);
        @Cast("char*")
        public native BytePointer    pComment(); public native CvDefParam pComment(BytePointer pComment);
        public native DoublePointer  pDouble();  public native CvDefParam pDouble(DoublePointer pDouble);
        public native double         Double();   public native CvDefParam Double(double Double);
        public native FloatPointer   pFloat();   public native CvDefParam pFloat(FloatPointer pFloat);
        public native float          Float();    public native CvDefParam Float(float Float);
        public native IntPointer     pInt();     public native CvDefParam pInt(IntPointer pInt);
        public native int            Int();      public native CvDefParam Int(int Int);
        @Cast("char**")
        public native PointerPointer pStr();     public native CvDefParam pStr(PointerPointer pStr);
        @Cast("char*")
        public native BytePointer    Str();      public native CvDefParam Str(BytePointer Str);
    }

    public static class CvVSModule extends Pointer {
        static { load(); }
        public CvVSModule() { }
        public CvVSModule(Pointer p) { super(p); }

        public native String GetParamName(int index);
        public native String GetParamComment(String name);
        public native double GetParam(String name);
        public native String GetParamStr(String name);
        public native void SetParam(String name, double val);
        public native void SetParamStr(String name, String str);
        public native void TransferParamsFromChild(CvVSModule pM, String prefix/*=null*/);
        public native void TransferParamsToChild(CvVSModule pM, @Cast("char*") String prefix/*=null*/);
        public native void ParamUpdate();
        public native String GetTypeName();
        public native int IsModuleTypeName(String name);
        public native String GetModuleName();
        public native int IsModuleName(String name);
        public native void SetNickName(String pStr);
        public native String GetNickName();
        public native void SaveState(CvFileStorage fs);
        public native void LoadState(CvFileStorage fs, CvFileNode fn);

        public /*abstract*/ native void Release();

//        protected native int m_Wnd();
//
//        protected native int IsParam(String name);
//        protected native void AddParam(String name, double[] pAddr);
//        protected native void AddParam(String name, float[] pAddr);
//        protected native void AddParam(String name, int[] pAddr);
//        protected native void AddParam(String name, @Cast("const char**") PointerPointer pAddr);
//        protected native void AddParam(String name);
//        protected native void CommentParam(String name, String pComment);
//        protected native void SetTypeName(String name);
//        protected native void SetModuleName(String name);
//        protected native void DelParam(String name);
    }

    public static native void cvWriteStruct(CvFileStorage fs, String name, Pointer addr, String desc, int num/*=1*/);
    public static native void cvReadStructByName(CvFileStorage fs, CvFileNode node, String name, Pointer addr, String desc);

    public static class CvFGDetector extends CvVSModule {
        static { load(); }
        public CvFGDetector() { }
        public CvFGDetector(Pointer p) { super(p); }

        public /*abstract*/ native IplImage GetMask();
        public /*abstract*/ native void Process(IplImage pImg);
    }

    public static native void cvReleaseFGDetector(@ByPtrPtr CvFGDetector ppT);
    public static native CvFGDetector cvCreateFGDetectorBase(int type, Pointer param);


    public static class CvBlob extends Pointer {
        static { load(); }
        public CvBlob() { allocate(); }
        public CvBlob(int size) { allocateArray(size); }
        public CvBlob(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native float x(); public native CvBlob x(float x);
        public native float y(); public native CvBlob y(float y);
        public native float w(); public native CvBlob w(float w);
        public native float h(); public native CvBlob h(float h);
        public native int  ID(); public native CvBlob ID(int ID);
    }

    public static CvBlob cvBlob(float x, float y, float w, float h) {
        return new CvBlob().x(x).y(y).w(w).h(h).ID(0);
    }

    public static final int
            CV_BLOB_MINW = 5,
            CV_BLOB_MINH = 5;
    public static int CV_BLOB_ID(CvBlob pB) { return pB.ID(); }
    public static CvPoint2D32f CV_BLOB_CENTER(CvBlob pB) { return cvPoint2D32f(pB.x(), pB.y()); }
    public static float CV_BLOB_X(CvBlob pB) { return pB.x(); }
    public static float CV_BLOB_Y(CvBlob pB) { return pB.y(); }
    public static float CV_BLOB_WX(CvBlob pB) { return pB.w(); }
    public static float CV_BLOB_WY(CvBlob pB) { return pB.h(); }
    public static float CV_BLOB_RX(CvBlob pB) { return 0.5f*CV_BLOB_WX(pB); }
    public static float CV_BLOB_RY(CvBlob pB) { return 0.5f*CV_BLOB_WY(pB); }
    public static CvRect CV_BLOB_RECT(CvBlob pB) { return cvRect(Math.round(pB.x()-CV_BLOB_RX(pB)), Math.round(pB.y()-CV_BLOB_RY(pB)),Math.round(CV_BLOB_WX(pB)),Math.round(CV_BLOB_WY(pB))); }

    public static class CvBlobSeq extends Pointer {
        static { load(); }
        public CvBlobSeq() { allocate(); }
        public CvBlobSeq(int BlobSize/*=sizeof(CvBlob)*/) { allocate(BlobSize); }
        public CvBlobSeq(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int BlobSize/*=sizeof(CvBlob)*/);

        public native CvBlob GetBlob(int BlobIndex);
        public native CvBlob GetBlobByID(int BlobID);
        public native void DelBlob(int BlobIndex);
        public native void DelBlobByID(int BlobID);
        public native void Clear();
        public native void AddBlob(CvBlob pB);
        public native int GetBlobNum();
        public native void Write(CvFileStorage fs, String name);
        public native void Load(CvFileStorage fs, CvFileNode node);
        public native void AddFormat(String str);

//        protected native CvMemStorage   m_pMem();
//        protected native CvSeq          m_pSeq();
//        protected native String/*char[1024]*/ m_pElemFormat();
    }

    public static class CvBlobTrack extends Pointer {
        static { load(); }
        public CvBlobTrack() { allocate(); }
        public CvBlobTrack(int size) { allocateArray(size); }
        public CvBlobTrack(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int        TrackID();    public native CvBlobTrack TrackID(int TrackID);
        public native int        StartFrame(); public native CvBlobTrack StartFrame(int StartFrame);
        public native CvBlobSeq  pBlobSeq();   public native CvBlobTrack pBlobSeq(CvBlobSeq pBlobSeq);
    }

    public static class CvBlobTrackSeq extends Pointer {
        static { load(); }
        public CvBlobTrackSeq() { allocate(); }
        public CvBlobTrackSeq(int TrackSize/*=sizeof(CvBlobTrack)*/) { allocate(TrackSize); }
        public CvBlobTrackSeq(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int TrackSize/*=sizeof(CvBlobTrack)*/);

        public native CvBlobTrack GetBlobTrack(int TrackIndex);
        public native CvBlobTrack GetBlobTrackByID(int TrackID);
        public native void DelBlobTrack(int TrackIndex);
        public native void DelBlobTrackByID(int TrackID);
        public native void Clear();
        public native void AddBlobTrack(int TrackID, int StartFrame/*=0*/);
        public native int GetBlobTrackNum();

//        protected native CvMemStorage   m_pMem();
//        protected native CvSeq          m_pSeq();
    }

    public static class CvBlobDetector extends CvVSModule {
        static { load(); }
        public CvBlobDetector() { }
        public CvBlobDetector(Pointer p) { super(p); }

        public /*abstract*/ native int DetectNewBlob(IplImage pImg, IplImage pImgFG, CvBlobSeq pNewBlobList, CvBlobSeq pOldBlobList);
    }

    public static native void cvReleaseBlobDetector(@ByPtrPtr CvBlobDetector ppBD);

    public static native CvBlobDetector cvCreateBlobDetectorSimple();
    public static native CvBlobDetector cvCreateBlobDetectorCC();

    @NoOffset public static class CvDetectedBlob extends CvBlob {
        static { load(); }
        public CvDetectedBlob() { allocate(); }
        public CvDetectedBlob(int size) { allocateArray(size); }
        public CvDetectedBlob(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native float response(); public native CvDetectedBlob response(float response);
    }

    public static CvDetectedBlob cvDetectedBlob(float x, float y, float w, float h, int ID/*=0*/, float response/*=0.0F*/) {
        CvDetectedBlob b = new CvDetectedBlob();
        b.x(x).y(y).w(w).h(h).ID(ID); b.response(response);
        return b;
    }

    public static class CvObjectDetector extends Pointer {
        static { load(); }
        public CvObjectDetector() { allocate(); }
        public CvObjectDetector(String detector_file_name/*=null*/) { allocate(detector_file_name); }
        public CvObjectDetector(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(String detector_file_name/*=null*/);

        public native boolean Load(String detector_file_name/*=null*/);
        public native @ByVal CvSize GetMinWindowSize();
        public native int GetMaxBorderSize();
        public native void Detect(CvArr img, CvBlobSeq detected_blob_seq/*=null*/);

//        protected native CvObjectDetectorImpl impl();
    }

    public static native @ByVal CvRect cvRectIntersection(@ByVal CvRect r1, @ByVal CvRect r2);


    public static class CvDrawShape extends Pointer {
        static { load(); }
        public CvDrawShape() { allocate(); }
        public CvDrawShape(int size) { allocateArray(size); }
        public CvDrawShape(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public static final int RECT=0, ELLIPSE=1; // enum shape;
        public native @ByRef CvScalar color(); public native CvDrawShape color(CvScalar color);
    }

    public static class CvImageDrawer extends Pointer {
        static { load(); }
        public CvImageDrawer() { allocate(); }
        public CvImageDrawer(Pointer p) { super(p); }
        private native void allocate();

//        public native void SetShapes(CvDrawShape shapes, int num);
//        public native IplImage Draw(CvArr src, CvBlobSeq blob_seq/*=null*/, CvSeq roi_seq/*=null*/);
        public native IplImage GetImage();

//        protected native IplImage m_image();
//        protected native CvDrawShape/*[16]*/ m_shape(int i);
    }


    public static class CvBlobTrackGen extends CvVSModule {
        static { load(); }
        public CvBlobTrackGen() { }
        public CvBlobTrackGen(Pointer p) { super(p); }

        public /*abstract*/ native void SetFileName(@Cast("char*") String pFileName);
        public /*abstract*/ native void AddBlob(CvBlob pBlob);
        public /*abstract*/ native void Process(IplImage pImg/*=null*/, IplImage pFG/*=null*/);
    }

    public static native void cvReleaseBlobTrackGen(@ByPtrPtr CvBlobTrackGen pBTGen);

    public static native CvBlobTrackGen cvCreateModuleBlobTrackGen1();
    public static native CvBlobTrackGen cvCreateModuleBlobTrackGenYML();


    public static class CvBlobTracker extends CvVSModule {
        static { load(); }
        public CvBlobTracker() { }
        public CvBlobTracker(Pointer p) { super(p); }

        public /*abstract*/ native CvBlob AddBlob(CvBlob pBlob, IplImage pImg, IplImage pImgFG/*=null*/);
        public /*abstract*/ native int    GetBlobNum();
        public /*abstract*/ native CvBlob GetBlob(int BlobIndex);
        public /*abstract*/ native void   DelBlob(int BlobIndex);
        public /*abstract*/ native void   Process(IplImage pImg, IplImage pImgFG/*=null*/);

        public native void ProcessBlob(int BlobIndex, CvBlob pBlob, IplImage pImg, IplImage pImgFG/*=null*/);
        public native double  GetConfidence(int BlobIndex, CvBlob pBlob, IplImage pImg, IplImage pImgFG/*=null*/);
        public native double GetConfidenceList(CvBlobSeq pBlobList, IplImage pImg, IplImage pImgFG/*=null*/);
        public native void UpdateBlob(int BlobIndex, CvBlob pBlob, IplImage pImg, IplImage pImgFG/*=null*/);
        public native void Update(IplImage pImg, IplImage pImgFG/*=null*/);
        public native int    GetBlobIndexByID(int BlobID);
        public native CvBlob GetBlobByID(int BlobID);
        public native void   DelBlobByID(int BlobID);
        public native void   SetBlob(int BlobIndex, CvBlob pBlob);
        public native void   SetBlobByID(int BlobID, CvBlob pBlob);

        public native int    GetBlobHypNum(int BlobIdx);
        public native CvBlob GetBlobHyp(int BlobIndex, int hypothesis);
        public native void   SetBlobHyp(int BlobIndex, CvBlob pBlob);
    }

    public static native void cvReleaseBlobTracker(@ByPtrPtr CvBlobTracker ppT);

    public static class CvBlobTrackerOne extends CvVSModule {
        static { load(); }
        public CvBlobTrackerOne() { }
        public CvBlobTrackerOne(Pointer p) { super(p); }

        public /*abstract*/ native void Init(CvBlob pBlobInit, IplImage pImg, IplImage pImgFG/*=null*/);
        public /*abstract*/ native CvBlob Process(CvBlob pBlobPrev, IplImage pImg, IplImage pImgFG/*=null*/);

        public native void SkipProcess(CvBlob pBlobPrev, IplImage pImg, IplImage pImgFG/*=null*/);
        public native void Update(CvBlob pBlob, IplImage pImg, IplImage pImgFG/*=null*/);
        public native void SetCollision(int CollisionFlag);
        public native double GetConfidence(CvBlob pBlob, IplImage pImg,
                IplImage pImgFG/*=null*/, IplImage pImgUnusedReg/*=null*/);
    }
    
    public static native void cvReleaseBlobTrackerOne(@ByPtrPtr CvBlobTrackerOne ppT);
    public static class CreateCvBlobTrackerOne extends FunctionPointer {
        static { load(); }
        public CreateCvBlobTrackerOne(Pointer p) { super(p); }
        public native CvBlobTrackerOne call();
    }
    public static native CvBlobTracker cvCreateBlobTrackerList(CreateCvBlobTrackerOne create);


    public static final int 
            PROFILE_EPANECHNIKOV   = 0,
            PROFILE_DOG            = 1;
    public static class CvBlobTrackerParamMS extends Pointer {
        static { load(); }
        public CvBlobTrackerParamMS() { allocate(); }
        public CvBlobTrackerParamMS(int size) { allocateArray(size); }
        public CvBlobTrackerParamMS(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int   noOfSigBits();        public native CvBlobTrackerParamMS noOfSigBits(int noOfSigBits);
        public native int   appearance_profile(); public native CvBlobTrackerParamMS appearance_profile(int appearance_profile);
        public native int   meanshift_profile();  public native CvBlobTrackerParamMS meanshift_profile(int meanshift_profile);
        public native float sigma();              public native CvBlobTrackerParamMS sigma(float sigma);
    }

//    public static native CvBlobTracker cvCreateBlobTrackerMS1(CvBlobTrackerParamMS param);
//    public static native CvBlobTracker cvCreateBlobTrackerMS2(CvBlobTrackerParamMS param);
//    public static native CvBlobTracker cvCreateBlobTrackerMS1ByList();

    public static class CvBlobTrackerParamLH extends Pointer {
        static { load(); }
        public CvBlobTrackerParamLH() { allocate(); }
        public CvBlobTrackerParamLH(int size) { allocateArray(size); }
        public CvBlobTrackerParamLH(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int HistType();   public native CvBlobTrackerParamLH HistType(int HistType);
        public native int ScaleAfter(); public native CvBlobTrackerParamLH ScaleAfter(int ScaleAfter);
    }

//    public static native CvBlobTracker cvCreateBlobTrackerLHR(CvBlobTrackerParamLH param/*=null*/);
//    public static native CvBlobTracker cvCreateBlobTrackerLHRS(CvBlobTrackerParamLH param/*=null*/);
    public static native CvBlobTracker cvCreateBlobTrackerCC();
    public static native CvBlobTracker cvCreateBlobTrackerCCMSPF();
    public static native CvBlobTracker cvCreateBlobTrackerMSFG();
    public static native CvBlobTracker cvCreateBlobTrackerMSFGS();
    public static native CvBlobTracker cvCreateBlobTrackerMS();
    public static native CvBlobTracker cvCreateBlobTrackerMSPF();
//    public static native  CvBlobTracker cvCreateBlobTrackerIPF();
//    public static native  CvBlobTracker cvCreateBlobTrackerIRB();
//    public static native  CvBlobTracker cvCreateBlobTrackerIPFDF();

    public static class CvBlobTrackPostProc extends CvVSModule {
        static { load(); }
        public CvBlobTrackPostProc() { }
        public CvBlobTrackPostProc(Pointer p) { super(p); }

        public /*abstract*/ native void   AddBlob(CvBlob pBlob);
        public /*abstract*/ native void   Process();
        public /*abstract*/ native int    GetBlobNum();
        public /*abstract*/ native CvBlob GetBlob(int index);

        public native CvBlob GetBlobByID(int BlobID);
    }

    public static native void cvReleaseBlobTrackPostProc(@ByPtrPtr CvBlobTrackPostProc pBTPP);

    public static class CvBlobTrackPostProcOne extends CvVSModule {
        static { load(); }
        public CvBlobTrackPostProcOne() { }
        public CvBlobTrackPostProcOne(Pointer p) { super(p); }

        public /*abstract*/ native CvBlob Process(CvBlob pBlob);
    }

    public static class CreateCvBlobTrackPostProcOne extends FunctionPointer {
        static { load(); }
        public CreateCvBlobTrackPostProcOne(Pointer p) { super(p); }
        public native CvBlobTrackPostProcOne call();
    }
    public static native CvBlobTrackPostProc cvCreateBlobTrackPostProcList(CreateCvBlobTrackPostProcOne create);
    public static native CvBlobTrackPostProc cvCreateModuleBlobTrackPostProcKalman();
    public static native CvBlobTrackPostProc cvCreateModuleBlobTrackPostProcTimeAverRect();
    public static native CvBlobTrackPostProc cvCreateModuleBlobTrackPostProcTimeAverExp();


    public static class CvBlobTrackPredictor extends CvVSModule {
        static { load(); }
        public CvBlobTrackPredictor() { }
        public CvBlobTrackPredictor(Pointer p) { super(p); }

        public /*abstract*/ native CvBlob Predict();
        public /*abstract*/ native void   Update(CvBlob pBlob);
    }
    public static native CvBlobTrackPredictor cvCreateModuleBlobTrackPredictKalman();


    public static class CvBlobTrackAnalysis extends CvVSModule {
        static { load(); }
        public CvBlobTrackAnalysis() { }
        public CvBlobTrackAnalysis(Pointer p) { super(p); }

        public /*abstract*/ native void    AddBlob(CvBlob pBlob);
        public /*abstract*/ native void    Process(IplImage pImg, IplImage pFG);
        public /*abstract*/ native float   GetState(int BlobID);

        public native String  GetStateDesc(int BlobID);
        public native void    SetFileName(@Cast("char*") String DataBaseName);
    }

    public static native void cvReleaseBlobTrackAnalysis(@ByPtrPtr CvBlobTrackAnalysis pBTPP);


    public static class CvBlobTrackFVGen extends CvVSModule {
        static { load(); }
        public CvBlobTrackFVGen() { }
        public CvBlobTrackFVGen(Pointer p) { super(p); }

        public /*abstract*/ native void    AddBlob(CvBlob pBlob);
        public /*abstract*/ native void    Process(IplImage pImg, IplImage pFG);
        public /*abstract*/ native int     GetFVSize();
        public /*abstract*/ native int     GetFVNum();
        public /*abstract*/ native FloatPointer  GetFV(int index, int[] pFVID);
        public native FloatPointer  GetFVVar();
        public /*abstract*/ native FloatPointer  GetFVMin();
        public /*abstract*/ native FloatPointer  GetFVMax();
    }


    public static class CvBlobTrackAnalysisOne extends Pointer {
        static { load(); }
        public CvBlobTrackAnalysisOne() { }
        public CvBlobTrackAnalysisOne(Pointer p) { super(p); }

        public /*abstract*/ native int  Process(CvBlob pBlob, IplImage pImg, IplImage pFG);
        public /*abstract*/ native void Release();
    }

    public static class CreateCvBlobTrackAnalysisOne extends FunctionPointer {
        static { load(); }
        public CreateCvBlobTrackAnalysisOne(Pointer p) { super(p); }
        public native CvBlobTrackAnalysisOne call();
    }
    public static native CvBlobTrackAnalysis cvCreateBlobTrackAnalysisList(CreateCvBlobTrackAnalysisOne create);
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisHistP();
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisHistPV();
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisHistPVS();
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisHistSS();
//    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisSVMP();
//    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisSVMPV();
//    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisSVMPVS();
//    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisSVMSS();
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisTrackDist();
//    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysis3DRoadMap();
    public static native CvBlobTrackAnalysis cvCreateModuleBlobTrackAnalysisIOR();


    public static class CvBlobTrackAnalysisHeight extends CvBlobTrackAnalysis {
        static { load(); }
        public CvBlobTrackAnalysisHeight() { }
        public CvBlobTrackAnalysisHeight(Pointer p) { super(p); }

        public /*abstract*/ native double GetHeight(CvBlob pB);
    }
//    public static native CvBlobTrackAnalysisHeight cvCreateModuleBlobTrackAnalysisHeightScale();


    public static class CvBlobTrackerAuto extends CvVSModule {
        static { load(); }
        public CvBlobTrackerAuto() { }
        public CvBlobTrackerAuto(Pointer p) { super(p); }

        public /*abstract*/ native void       Process(IplImage pImg, IplImage pMask/*=null*/);
        public /*abstract*/ native CvBlob     GetBlob(int index);
        public /*abstract*/ native CvBlob     GetBlobByID(int ID);
        public /*abstract*/ native int        GetBlobNum();
        public native IplImage   GetFGMask();
        public /*abstract*/ native float      GetState(int BlobID);
        public /*abstract*/ native String     GetStateDesc(int BlobID);
    }
    public static native void cvReleaseBlobTrackerAuto(@ByPtrPtr CvBlobTrackerAuto ppT);


    public static class CvBlobTrackerAutoParam1 extends Pointer {
        static { load(); }
        public CvBlobTrackerAutoParam1() { allocate(); }
        public CvBlobTrackerAutoParam1(int size) { allocateArray(size); }
        public CvBlobTrackerAutoParam1(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int                 FGTrainFrames(); public native CvBlobTrackerAutoParam1 FGTrainFrames(int FGTrainFrames);
        public native CvFGDetector        pFG();           public native CvBlobTrackerAutoParam1 pFG(CvFGDetector pFG);
        public native CvBlobDetector      pBD();           public native CvBlobTrackerAutoParam1 pBD(CvBlobDetector pBD);
        public native CvBlobTracker       pBT();           public native CvBlobTrackerAutoParam1 pBT(CvBlobTracker pBT);
        public native CvBlobTrackGen      pBTGen();        public native CvBlobTrackerAutoParam1 pBTGen(CvBlobTrackGen pBTGen);
        public native CvBlobTrackPostProc pBTPP();         public native CvBlobTrackerAutoParam1 pBTPP(CvBlobTrackPostProc pBTPP);
        public native int                 UsePPData();     public native CvBlobTrackerAutoParam1 UsePPData(int UsePPData);
        public native CvBlobTrackAnalysis pBTA();          public native CvBlobTrackerAutoParam1 pBTA(CvBlobTrackAnalysis pBTA);
    }

    public static native CvBlobTrackerAuto cvCreateBlobTrackerAuto1(CvBlobTrackerAutoParam1 param/*=null*/);
    public static native CvBlobTrackerAuto cvCreateBlobTrackerAuto(int type, Pointer param);


    public static class CvTracksTimePos extends Pointer {
        static { load(); }
        public CvTracksTimePos() { allocate(); }
        public CvTracksTimePos(int size) { allocateArray(size); }
        public CvTracksTimePos(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int len1();   public native CvTracksTimePos len1(int len1);
        public native int len2();   public native CvTracksTimePos len2(int len2);
        public native int beg1();   public native CvTracksTimePos beg1(int beg1);
        public native int beg2();   public native CvTracksTimePos beg2(int beg2);
        public native int end1();   public native CvTracksTimePos end1(int end1);
        public native int end2();   public native CvTracksTimePos end2(int end2);
        public native int comLen(); public native CvTracksTimePos comLen(int comLen);
        public native int shift1(); public native CvTracksTimePos shift1(int shift1);
        public native int shift2(); public native CvTracksTimePos shift2(int shift2);
    }

//    public static native int cvCompareTracks(CvBlobTrackSeq groundTruth, CvBlobTrackSeq result, @Cast("FILE*") Pointer file);
//
//    public static native void cvCreateTracks_One(CvBlobTrackSeq TS);
//    public static native void cvCreateTracks_Same(CvBlobTrackSeq TS1, CvBlobTrackSeq TS2);
//    public static native void cvCreateTracks_AreaErr(CvBlobTrackSeq TS1, CvBlobTrackSeq TS2, int addW, int addH);


    public static class CvProb extends Pointer {
        static { load(); }
        public CvProb() { }
        public CvProb(Pointer p) { super(p); }

        public native double Value(int[] comp, int x/*=0*/, int y/*=0*/);

        public /*abstract*/ native void AddFeature(float W, int[] comps, int x/*=0*/, int y/*=0*/);
        public /*abstract*/ native void Scale(float factor/*=0*/, int x/*=-1*/, int y/*=-1*/);
        public /*abstract*/ native void Release();
    }
    public static native void cvReleaseProb(@ByPtrPtr CvProb ppProb);

//    public static native CvProb cvCreateProbS(int dim, @ByVal CvSize size, int sample_num);
//    public static native CvProb cvCreateProbMG(int dim, @ByVal CvSize size, int sample_num);
//    public static native CvProb cvCreateProbMG2(int dim, @ByVal CvSize size, int sample_num);
//    public static native CvProb cvCreateProbHist(int dim, @ByVal CvSize size);
//
//    public static final int
//            CV_BT_HIST_TYPE_S    = 0,
//            CV_BT_HIST_TYPE_MG   = 1,
//            CV_BT_HIST_TYPE_MG2  = 2,
//            CV_BT_HIST_TYPE_H    = 3;
//    public static native CvProb cvCreateProb(int type, int dim, @ByVal CvSize size/*=cvSize(1,1)*/, Pointer param/*=null*/);


    public static final int
        CV_NOISE_NONE              = 0,
        CV_NOISE_GAUSSIAN          = 1,
        CV_NOISE_UNIFORM           = 2,
        CV_NOISE_SPECKLE           = 3,
        CV_NOISE_SALT_AND_PEPPER   = 4;
//    public static native void cvAddNoise(IplImage pImg, int noise_type, double Ampl, CvRNG rnd_state/*=null*/);

    @Opaque public static class CvTestSeq extends Pointer {
        static { load(); }
        public CvTestSeq() { }
        public CvTestSeq(Pointer p) { super(p); }
    }

    public static native CvTestSeq cvCreateTestSeq(@Cast("char*") String pConfigfile, @Cast("char**") PointerPointer videos, 
            int numvideo, float Scale/*=1*/, int noise_type/*=CV_NOISE_NONE*/, double noise_ampl/*=0*/);
    public static native void cvReleaseTestSeq(@ByPtrPtr CvTestSeq ppTestSeq);

    public static native IplImage cvTestSeqQueryFrame(CvTestSeq pTestSeq);
    public static native IplImage cvTestSeqGetFGMask(CvTestSeq pTestSeq);
    public static native IplImage cvTestSeqGetImage(CvTestSeq pTestSeq);
    public static native @ByVal CvSize cvTestSeqGetImageSize(CvTestSeq pTestSeq);
    public static native int cvTestSeqFrameNum(CvTestSeq pTestSeq);
    public static native int cvTestSeqGetObjectNum(CvTestSeq pTestSeq);

    public static native int cvTestSeqGetObjectPos(CvTestSeq pTestSeq, int ObjIndex, CvPoint2D32f pPos);
    public static native int cvTestSeqGetObjectSize(CvTestSeq pTestSeq, int ObjIndex, CvPoint2D32f pSize);

    public static native void cvTestSeqAddNoise(CvTestSeq pTestSeq, int noise_type/*=CV_NOISE_NONE*/, double noise_ampl/*=0*/);

    public static native void cvTestSeqAddIntensityVariation(CvTestSeq pTestSeq, float DI_per_frame, float MinI, float MaxI);
    public static native void cvTestSeqSetFrame(CvTestSeq pTestSeq, int n);
}
