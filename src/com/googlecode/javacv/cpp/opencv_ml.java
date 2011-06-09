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
 * This file is based on information found in ml.hpp of OpenCV 2.2,
 * which is covered by the following copyright notice:
 *
 *                        Intel License Agreement
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

import com.googlecode.javacpp.BoolPointer;
import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.DoublePointer;
import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.FunctionPointer;
import com.googlecode.javacpp.IntPointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacpp.PointerPointer;
import com.googlecode.javacpp.ShortPointer;
import com.googlecode.javacpp.annotation.Adapter;
import com.googlecode.javacpp.annotation.ByRef;
import com.googlecode.javacpp.annotation.ByVal;
import com.googlecode.javacpp.annotation.Cast;
import com.googlecode.javacpp.annotation.Const;
import com.googlecode.javacpp.annotation.MemberGetter;
import com.googlecode.javacpp.annotation.Name;
import com.googlecode.javacpp.annotation.Namespace;
import com.googlecode.javacpp.annotation.NoOffset;
import com.googlecode.javacpp.annotation.Platform;
import com.googlecode.javacpp.annotation.Properties;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 *
 * @author Samuel Audet
 */
@Properties({
    @Platform(include={"<opencv2/ml/ml.hpp>", "opencv_adapters.h"}, includepath=genericIncludepath,
        linkpath=genericLinkpath,       link={"opencv_ml", "opencv_core"}),
    @Platform(value="windows", includepath=windowsIncludepath, linkpath=windowsLinkpath,
        preloadpath=windowsPreloadpath, link={"opencv_ml220", "opencv_core220"}),
    @Platform(value="android", includepath=androidIncludepath, linkpath=androidLinkpath) })
public class opencv_ml {
    static { load(opencv_core.class); load(); }

    public static final double CV_LOG2PI = 1.8378770664093454835606594728112;

    public static final int
            CV_COL_SAMPLE = 0,
            CV_ROW_SAMPLE = 1;

    public static int CV_IS_ROW_SAMPLE(int flags) { return flags & CV_ROW_SAMPLE; }

    public static class CvVectors extends Pointer {
        static { load(); }
        public CvVectors() { allocate(); }
        public CvVectors(int size) { allocateArray(size); }
        public CvVectors(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int type();       public native CvVectors type(int type);
        public native int dims();       public native CvVectors dims(int dims);
        public native int count();      public native CvVectors count(int count);
        public native CvVectors next(); public native CvVectors next(CvVectors next);
        @Name("data.ptr") @Cast("uchar**") 
        public native PointerPointer data_ptr(); public native CvVectors data_ptr(PointerPointer data_ptr);
        @Name("data.fl") @Cast("float**")
        public native PointerPointer data_fl();  public native CvVectors data_fl(PointerPointer data_fl);
        @Name("data.db") @Cast("double**")
        public native PointerPointer data_db();  public native CvVectors data_db(PointerPointer data_db);
    }

//    public static class CvParamLattice extends Pointer {
//        static { load(); }
//        public CvParamLattice() { allocate(); }
//        public CvParamLattice(int size) { allocateArray(size); }
//        public CvParamLattice(Pointer p) { super(p); }
//
//        private native void allocate();
//        private native void allocateArray(int size);
//        public native double min_val();
//        public native double max_val();
//        public native double step();
//    }
//    public static native @ByVal CvParamLattice cvParamLattice(double min_val, double max_val, double log_step);
//    public static native @ByVal CvParamLattice cvDefaultParamLattice();

    public static final int
            CV_VAR_NUMERICAL   = 0,
            CV_VAR_ORDERED     = 0,
            CV_VAR_CATEGORICAL = 1;

    public static final String
            CV_TYPE_NAME_ML_SVM        = "opencv-ml-svm",
            CV_TYPE_NAME_ML_KNN        = "opencv-ml-knn",
            CV_TYPE_NAME_ML_NBAYES     = "opencv-ml-bayesian",
            CV_TYPE_NAME_ML_EM         = "opencv-ml-em",
            CV_TYPE_NAME_ML_BOOSTING   = "opencv-ml-boost-tree",
            CV_TYPE_NAME_ML_TREE       = "opencv-ml-tree",
            CV_TYPE_NAME_ML_ANN_MLP    = "opencv-ml-ann-mlp",
            CV_TYPE_NAME_ML_CNN        = "opencv-ml-cnn",
            CV_TYPE_NAME_ML_RTREES     = "opencv-ml-random-trees",
            CV_TYPE_NAME_ML_GBT        = "opencv-ml-gradient-boosting-trees";

    public static final int
            CV_TRAIN_ERROR = 0,
            CV_TEST_ERROR  = 1;

    public static class CvStatModel extends Pointer {
        static { Loader.load(); }
        public CvStatModel() { allocate(); }
        public CvStatModel(Pointer p) { super(p); }
        private native void allocate();

        public native void clear();

        public native void save(String filename, String name/*=null*/);
        public native void load(String filename, String name/*=null*/);

        public native void write(CvFileStorage storage, String name);
        public native void read(CvFileStorage storage, CvFileNode node);

//        protected native @Cast("const char*") BytePointer default_model_name();
    }

    @NoOffset public static class CvParamGrid extends Pointer {
        static { load(); }
        public CvParamGrid() { allocate(); }
        public CvParamGrid(double _min_val, double _max_val, double log_step) {
            allocate(_min_val, _max_val, log_step);
        }
        //public CvParamGrid(int param_id);
        public CvParamGrid(int size) { allocateArray(size); }
        public CvParamGrid(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(double _min_val, double _max_val, double log_step);
        private native void allocateArray(int size);

        public static final int SVM_C=0, SVM_GAMMA=1, SVM_P=2, SVM_NU=3, SVM_COEF=4, SVM_DEGREE=5;

        public native boolean check();

        public native double min_val(); public native CvParamGrid min_val(double min_val);
        public native double max_val(); public native CvParamGrid max_val(double max_val);
        public native double step();    public native CvParamGrid step(double step);
    }

    public static class CvNormalBayesClassifier extends CvStatModel {
        static { Loader.load(); }
        public CvNormalBayesClassifier() { allocate(); }
        public CvNormalBayesClassifier(CvMat trainData, CvMat responses,
                CvMat varIdx/*=null*/, CvMat sampleIdx/*=0*/) {
            allocate(trainData, responses, varIdx, sampleIdx);
        }
        public CvNormalBayesClassifier(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, CvMat responses,
                CvMat varIdx/*=null*/, CvMat sampleIdx/*=0*/);

        public native boolean train(CvMat trainData, CvMat responses,
                CvMat varIdx/*=null*/, CvMat sampleIdx/*=null*/, boolean update/*=false*/);
        public native float predict(CvMat samples, CvMat results/*=null*/);
//        public native void clear();
//
//        public native void write(CvFileStorage storage, String name);
//        public native void read(CvFileStorage storage, CvFileNode node );
//
//        protected native int   var_count();
//        protected native int   var_all();
//        protected native CvMat var_idx();
//        protected native CvMat cls_labels();
//        protected native CvMatArray count();
//        protected native CvMatArray sum();
//        protected native CvMatArray productsum();
//        protected native CvMatArray avg();
//        protected native CvMatArray inv_eigen_values();
//        protected native CvMatArray cov_rotate_mats();
//        protected native CvMat  c();
    }

    public static class CvKNearest extends CvStatModel {
        static { Loader.load(); }
        public CvKNearest() { allocate(); }
        public CvKNearest(CvMat trainData, CvMat responses, CvMat sampleIdx/*=null*/,
                boolean isRegression/*=false*/, int max_k/*=32*/) {
            allocate(trainData, responses, sampleIdx, isRegression, max_k);
        }
        public CvKNearest(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, CvMat responses, CvMat sampleIdx/*=null*/,
                boolean isRegression/*=false*/, int max_k/*=32*/);

        public native boolean train(CvMat trainData, CvMat responses, CvMat sampleIdx/*=null*/,
                boolean is_regression/*=false*/, int maxK/*=32*/,  boolean updateBase/*=false*/);
        public native float find_nearest(CvMat samples, int k, CvMat results/*=null*/, @Cast("const float**")
                PointerPointer neighbors/*=null*/, CvMat neighborResponses/*=null*/, CvMat dist/*=null*/);

//        public native void clear();
        public native int get_max_k();
        public native int get_var_count();
        public native int get_sample_count();
        public native boolean is_regression();

//        protected native float write_results(int k, int k1, int start, int end,
//                float[] neighbor_responses, float[] dist, CvMat _results,
//                CvMat _neighbor_responses, CvMat _dist, Cv32suf sort_buf);
//
//        protected native void find_neighbors_direct(CvMat _samples, int k, int start, int end,
//                float[] neighbor_responses, @Cast("float**") PointerPointer neighbors, float[] dist);
//
//        protected native int max_k();
//        protected native int var_count();
//        protected native int total();
//        protected native boolean regression();
//        protected native CvVectors samples();
    }


    @NoOffset public static class CvSVMParams extends Pointer {
        static { load(); }
        public CvSVMParams() { allocate(); }
        public CvSVMParams(int _svm_type, int _kernel_type, double _degree, double _gamma, double _coef0,
                 double Cvalue, double _nu, double _p, CvMat _class_weights, CvTermCriteria _term_crit) {
            allocate(_svm_type, _kernel_type, _degree, _gamma, _coef0, Cvalue, _nu, _p, _class_weights, _term_crit);
        }
        public CvSVMParams(int size) { allocateArray(size); }
        public CvSVMParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _svm_type, int _kernel_type, double _degree, double _gamma, double _coef0,
                 double Cvalue, double _nu, double _p, CvMat _class_weights, @ByVal CvTermCriteria _term_crit);
        private native void allocateArray(int size);

        public native int    svm_type();          public native CvSVMParams svm_type(int svm_type);
        public native int    kernel_type();       public native CvSVMParams kernel_type(int kernel_type);
        public native double degree();            public native CvSVMParams degree(double degree);
        public native double gamma();             public native CvSVMParams gamma(double gamma);
        public native double coef0();             public native CvSVMParams coef0(double coef0);

        public native double C();                 public native CvSVMParams C(double C);
        public native double nu();                public native CvSVMParams nu(double nu);
        public native double p();                 public native CvSVMParams p(double p);
        public native CvMat  class_weights();     public native CvSVMParams class_weights(CvMat class_weights);
        @ByRef
        public native CvTermCriteria term_crit(); public native CvSVMParams term_crit(CvTermCriteria term_crit);
    }

    @NoOffset public static class CvSVMKernel extends Pointer {
        static { load(); }
        @Namespace("CvSVMKernel") public static class Calc extends FunctionPointer {
            static { load(); }
            public Calc(Pointer p) { super(p); }
            public native void call(CvSVMKernel o, int vec_count, int vec_size, @Cast("const float**")
                    PointerPointer vecs, @Const FloatPointer another, FloatPointer results);
        }
        public CvSVMKernel() { allocate(); }
        public CvSVMKernel(CvSVMParams params, Calc _calc_func) { allocate(params, _calc_func); }
        public CvSVMKernel(int size) { allocateArray(size); }
        public CvSVMKernel(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvSVMParams params, Calc _calc_func);
        private native void allocateArray(int size);

        public native void clear();
        public native void calc(int vcount, int n,  @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results);
        @Const
        public native CvSVMParams params(); public native CvSVMKernel params(CvSVMParams params);
        public native Calc calc_func();     public native CvSVMKernel calc_func(Calc calc_func);

        public native void calc_non_rbf_base(int vec_count, int vec_size, @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results, double alpha, double beta);

        public native void calc_linear( int vec_count, int vec_size, @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results);
        public native void calc_rbf( int vec_count, int vec_size, @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results);
        public native void calc_poly( int vec_count, int vec_size, @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results);
        public native void calc_sigmoid( int vec_count, int vec_size, @Cast("const float**")
                PointerPointer vecs, float[] another, float[] results);

        @MemberGetter public static native @ByRef Calc calc_linear();
        @MemberGetter public static native @ByRef Calc calc_rbf();
        @MemberGetter public static native @ByRef Calc calc_poly();
        @MemberGetter public static native @ByRef Calc calc_sigmoid();
    }

    public static class CvSVMKernelRow extends Pointer {
        static { load(); }
        public CvSVMKernelRow() { allocate(); }
        public CvSVMKernelRow(int size) { allocateArray(size); }
        public CvSVMKernelRow(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native CvSVMKernelRow prev(); public native CvSVMKernelRow prev(CvSVMKernelRow prev);
        public native CvSVMKernelRow next(); public native CvSVMKernelRow next(CvSVMKernelRow next);
        public native FloatPointer data();   public native CvSVMKernelRow data(FloatPointer data);
    }

    public static class CvSVMSolutionInfo extends Pointer {
        static { load(); }
        public CvSVMSolutionInfo() { allocate(); }
        public CvSVMSolutionInfo(int size) { allocateArray(size); }
        public CvSVMSolutionInfo(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native double obj();           public native CvSVMSolutionInfo obj(double obj);
        public native double rho();           public native CvSVMSolutionInfo rho(double rho);
        public native double upper_bound_p(); public native CvSVMSolutionInfo upper_bound_p(double upper_bound_p);
        public native double upper_bound_n(); public native CvSVMSolutionInfo upper_bound_n(double upper_bound_n);
        public native double r();             public native CvSVMSolutionInfo r(double r);
    }

    @NoOffset public static class CvSVMSolver extends Pointer {
        static { load(); }
        @Namespace("CvSVMSolver") public static class SelectWorkingSet extends FunctionPointer {
            static { load(); }
            public SelectWorkingSet(Pointer p) { super(p); }
            public native @Cast("bool") boolean call(CvSVMSolver o, @ByRef IntPointer i, @ByRef IntPointer j);
        }
        @Namespace("CvSVMSolver") public static class GetRow extends FunctionPointer {
            static { load(); }
            public GetRow(Pointer p) { super(p); }
            public native FloatPointer call(CvSVMSolver o, int i, FloatPointer row, FloatPointer dst,
                    @Cast("bool") boolean existed);
        }
        @Namespace("CvSVMSolver") public static class CalcRho extends FunctionPointer {
            static { load(); }
            public CalcRho(Pointer p) { super(p); }
            public native void call(CvSVMSolver o, @ByRef DoublePointer rho, @ByRef DoublePointer r);
        }

        public CvSVMSolver() { allocate(); }
        public CvSVMSolver(int count, int var_count, @Cast("const float**") PointerPointer samples,
                byte[] y, int alpha_count, double[] alpha, double Cp, double Cn, CvMemStorage storage,
                CvSVMKernel kernel, GetRow get_row, SelectWorkingSet select_working_set, CalcRho calc_rho) {
            allocate(count, var_count, samples, y, alpha_count, alpha, Cp, Cn, storage, kernel, get_row, select_working_set, calc_rho);
        }
        public CvSVMSolver(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int count, int var_count, @Cast("const float**") PointerPointer samples,
                byte[] y, int alpha_count, double[] alpha, double Cp, double Cn, CvMemStorage storage,
                CvSVMKernel kernel, GetRow get_row, SelectWorkingSet select_working_set, CalcRho calc_rho);

        public native boolean create(int count, int var_count, @Cast("const float**") PointerPointer samples,
                byte[] y, int alpha_count, double[] alpha, double Cp, double Cn, CvMemStorage storage,
                CvSVMKernel kernel, GetRow get_row, SelectWorkingSet select_working_set, CalcRho calc_rho);

        public native void clear();
        public native boolean solve_generic(@ByRef CvSVMSolutionInfo si);

        public native boolean solve_c_svc(int count, int var_count, @Cast("const float**") PointerPointer samples, byte[] y,
                double Cp, double Cn, CvMemStorage storage, CvSVMKernel kernel, double[] alpha, @ByRef CvSVMSolutionInfo si);
        public native boolean solve_nu_svc(int count, int var_count, @Cast("const float**") PointerPointer samples, byte[] y,
                CvMemStorage storage, CvSVMKernel kernel, double[] alpha, @ByRef CvSVMSolutionInfo si);
        public native boolean solve_one_class(int count, int var_count, @Cast("const float**") PointerPointer samples,
                CvMemStorage storage, CvSVMKernel kernel, double[] alpha, @ByRef CvSVMSolutionInfo si);

        public native boolean solve_eps_svr( int count, int var_count, @Cast("const float**") PointerPointer samples, float[] y,
                CvMemStorage storage, CvSVMKernel kernel, double[] alpha, @ByRef CvSVMSolutionInfo si);

        public native boolean solve_nu_svr( int count, int var_count, @Cast("const float**") PointerPointer samples, float[] y,
                CvMemStorage storage, CvSVMKernel kernel, double[] alpha, @ByRef CvSVMSolutionInfo si);

        public native FloatPointer get_row_base(int i, BoolPointer _existed);
        public native FloatPointer get_row(int i, float[] dst);

        public native int sample_count();               public native CvSVMSolver sample_count(int sample_count);
        public native int var_count();                  public native CvSVMSolver var_count(int var_count);
        public native int cache_size();                 public native CvSVMSolver cache_size(int cache_size);
        public native int cache_line_size();            public native CvSVMSolver cache_line_size(int cache_line_size);
        @Cast("const float**")
        public native PointerPointer samples();         public native CvSVMSolver samples(PointerPointer samples);
        public native @Const CvSVMParams params();      public native CvSVMSolver params(CvSVMParams params);
        public native CvMemStorage storage();           public native CvSVMSolver storage(CvMemStorage storage);
        public native @ByRef CvSVMKernelRow lru_list(); public native CvSVMSolver lru_list(CvSVMKernelRow lru_list);
        public native CvSVMKernelRow rows();            public native CvSVMSolver rows(CvSVMKernelRow rows);

        public native int alpha_count();                public native CvSVMSolver alpha_count(int alpha_count);

        public native DoublePointer G();                public native CvSVMSolver G(DoublePointer G);
        public native DoublePointer alpha();            public native CvSVMSolver alpha(DoublePointer alpha);

        public native BytePointer alpha_status();       public native CvSVMSolver alpha_status(BytePointer alpha_status);

        public native BytePointer y();                  public native CvSVMSolver y(BytePointer y);
        public native DoublePointer b();                public native CvSVMSolver b(DoublePointer b);
        public native FloatPointer buf/*[2]*/(int i);   public native CvSVMSolver buf(int i, FloatPointer buf);
        public native double eps();                     public native CvSVMSolver eps(double eps);
        public native int max_iter();                   public native CvSVMSolver max_iter(int max_iter);
        public native double C/*[2]*/(int i);           public native CvSVMSolver C(int i, double C);
        public native CvSVMKernel kernel();             public native CvSVMSolver kernel(CvSVMKernel kernel);

        public native SelectWorkingSet select_working_set_func();
        public native CvSVMSolver select_working_set_func(SelectWorkingSet select_working_set_func);
        public native CalcRho calc_rho_func();          public native CvSVMSolver calc_rho_func(CalcRho calc_rho_func);
        public native GetRow get_row_func();            public native CvSVMSolver get_row_func(GetRow get_row_func);

        public native boolean select_working_set(@ByRef int[] i, @ByRef int[] j);
        public native boolean select_working_set_nu_svm(@ByRef int[] i, @ByRef int[] j);
        public native void calc_rho(@ByRef double[] rho, @ByRef double[] r);
        public native void calc_rho_nu_svm(@ByRef double[] rho, @ByRef double[] r);

        public native FloatPointer get_row_svc(int i, float[] row, float[] dst, boolean existed);
        public native FloatPointer get_row_one_class(int i, float[] row, float[] dst, boolean existed);
        public native FloatPointer get_row_svr(int i, float[] row, float[] dst, boolean existed);

        @MemberGetter public static native @ByRef SelectWorkingSet select_working_set();
        @MemberGetter public static native @ByRef SelectWorkingSet select_working_set_nu_svm();
        @MemberGetter public static native @ByRef CalcRho calc_rho();
        @MemberGetter public static native @ByRef CalcRho calc_rho_nu_svm();
        @MemberGetter public static native @ByRef GetRow get_row_svc();
        @MemberGetter public static native @ByRef GetRow get_row_one_class();
        @MemberGetter public static native @ByRef GetRow get_row_svr();
    }

    public static class CvSVMDecisionFunc extends Pointer {
        static { load(); }
        public CvSVMDecisionFunc() { allocate(); }
        public CvSVMDecisionFunc(int size) { allocateArray(size); }
        public CvSVMDecisionFunc(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native double rho();          public native CvSVMDecisionFunc rho(double rho);
        public native int sv_count();        public native CvSVMDecisionFunc sv_count(int sv_count);
        public native DoublePointer alpha(); public native CvSVMDecisionFunc alpha(DoublePointer alpha);
        public native IntPointer sv_index(); public native CvSVMDecisionFunc sv_index(IntPointer sv_index);
    }

    public static class CvSVM extends CvStatModel {
        static { Loader.load(); }
        public CvSVM() { allocate(); }
        public CvSVM(CvMat trainData, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, @ByVal CvSVMParams params/*=CvSVMParams()*/) {
            allocate(trainData, responses, varIdx, sampleIdx, params);
        }
        public CvSVM(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, @ByVal CvSVMParams params/*=CvSVMParams()*/);

        public static final int C_SVC=100, NU_SVC=101, ONE_CLASS=102, EPS_SVR=103, NU_SVR=104;

        public static final int LINEAR=0, POLY=1, RBF=2, SIGMOID=3;

        public static final int C=0, GAMMA=1, P=2, NU=3, COEF=4, DEGREE=5;

        public native boolean train(CvMat trainData, CvMat responses, CvMat varIdx/*=0*/,
                CvMat sampleIdx/*=null*/, @ByVal CvSVMParams params/*=CvSVMParams()*/);

        public native boolean train_auto(CvMat trainData, CvMat responses, CvMat varIdx,
                CvMat sampleIdx, @ByVal CvSVMParams params, int kfold/*=10*/,
                @ByVal CvParamGrid Cgrid     /* = get_default_grid(CvSVM::C)*/,
                @ByVal CvParamGrid gammaGrid /* = get_default_grid(CvSVM::GAMMA)*/,
                @ByVal CvParamGrid pGrid     /* = get_default_grid(CvSVM::P)*/,
                @ByVal CvParamGrid nuGrid    /* = get_default_grid(CvSVM::NU)*/,
                @ByVal CvParamGrid coeffGrid /* = get_default_grid(CvSVM::COEF)*/,
                @ByVal CvParamGrid degreeGrid/* = get_default_grid(CvSVM::DEGREE)*/,
                boolean balanced/*=false*/);

        public native float predict(CvMat sample, boolean returnDFVal/*=false*/);

        public native int get_support_vector_count();
        public native @Const FloatPointer get_support_vector(int i);
        public native @ByVal CvSVMParams get_params();
//        public native void clear();

        public static native @ByVal CvParamGrid get_default_grid(int param_id);

//        public native void write(CvFileStorage storage, String name);
//        public native void read(CvFileStorage storage, CvFileNode node );
        public native int get_var_count();

//        protected native boolean set_params(@ByRef CvSVMParams params);
//        protected native boolean train1(int sample_count, int var_count, @Cast("const float**") PointerPointer samples,
//                Pointer responses, double Cp, double Cn, CvMemStorage _storage, double[] alpha, @ByRef double[] rho);
//        protected native boolean do_train( int svm_type, int sample_count, int var_count, @Cast("const float**")
//                PointerPointer samples, CvMat responses, CvMemStorage _storage, double[] alpha);
//        protected native void create_kernel();
//        protected native void create_solver();
//
//        protected native float predict(float[] row_sample, int row_len, boolean returnDFVal/*=false*/);
//
//        protected native void write_params(CvFileStorage fs );
//        protected native void read_params(CvFileStorage fs, CvFileNode node);
//
//        protected native @ByRef CvSVMParams params();
//        protected native CvMat class_labels();
//        protected native int var_all();
//        protected native @Cast("float**") PointerPointer sv();
//        protected native int sv_total();
//        protected native CvMat var_idx();
//        protected native CvMat class_weights();
//        protected native CvSVMDecisionFunc decision_func();
//        protected native CvMemStorage storage();
//
//        protected native CvSVMSolver solver();
//        protected native CvSVMKernel kernel();
    }


    @NoOffset public static class CvEMParams extends Pointer {
        static { load(); }
        public CvEMParams() { allocate(); }
        public CvEMParams(int _nclusters, int _cov_mat_type/*=CvEM::COV_MAT_DIAGONAL*/, int _start_step/*=CvEM::START_AUTO_STEP*/,
                @ByVal CvTermCriteria _term_crit/*=cvTermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 100, FLT_EPSILON)*/,
                CvMat _probs/*=null*/, CvMat _weights/*=null*/, CvMat _means/*=null*/, @Const CvMatArray _covs/*=null*/) {
            allocate(_nclusters, _cov_mat_type, _start_step, _term_crit, _probs, _weights, _means, _covs);
        }
        public CvEMParams(int size) { allocateArray(size); }
        public CvEMParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _nclusters, int _cov_mat_type/*=CvEM::COV_MAT_DIAGONAL*/, int _start_step/*=CvEM::START_AUTO_STEP*/,
                @ByVal CvTermCriteria _term_crit/*=cvTermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 100, FLT_EPSILON)*/,
                CvMat _probs/*=null*/, CvMat _weights/*=null*/, CvMat _means/*=null*/, @Const CvMatArray _covs/*=null*/);
        private native void allocateArray(int size);

        public native int nclusters();            public native CvEMParams nclusters(int nclusters);
        public native int cov_mat_type();         public native CvEMParams cov_mat_type(int cov_mat_type);
        public native int start_step();           public native CvEMParams start_step(int start_step);
        public native @Const CvMat probs();       public native CvEMParams probs(CvMat probs);
        public native @Const CvMat weights();     public native CvEMParams weights(CvMat weights);
        public native @Const CvMat means();       public native CvEMParams means(CvMat means);
        public native @Const CvMatArray covs();   public native CvEMParams covs(CvMatArray covs);
        @ByRef
        public native CvTermCriteria term_crit(); public native CvEMParams term_crit(CvTermCriteria term_crit);
    }


    public static class CvEM extends CvStatModel {
        static { Loader.load(); }
        public CvEM() { allocate(); }
        public CvEM(CvMat samples, CvMat sampleIdx/*=null*/,
                CvEMParams params/*=CvEMParams()*/, CvMat labels/*=null*/ ) {
            allocate(samples, sampleIdx, params, labels);
        }
//        public CvEM(@ByVal CvEMParams params, CvMat means, CvMatArray covs, CvMat weights,
//                CvMat probs, CvMat log_weight_div_det, CvMat inv_eigen_values, CvMatArray cov_rotate_mats);
        public CvEM(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat samples, CvMat sampleIdx/*=null*/,
                @ByVal CvEMParams params/*=CvEMParams()*/, CvMat labels/*=null*/ );

        public static final int COV_MAT_SPHERICAL=0, COV_MAT_DIAGONAL=1, COV_MAT_GENERIC=2;

        public static final int START_E_STEP=1, START_M_STEP=2, START_AUTO_STEP=0;

        public native boolean train(CvMat samples, CvMat sampleIdx/*=null*/,
                @ByVal CvEMParams params/*=CvEMParams()*/, CvMat labels/*=null*/);
        public native float predict(CvMat sample, CvMat probs);
//        public native void clear();

        public native int           get_nclusters();
        public native @Const CvMat  get_means();
        public native @Const CvMatArray get_covs();
        public native @Const CvMat  get_weights();
        public native @Const CvMat  get_probs();

        public native double        get_log_likelihood();

//        public native @Const CvMat  get_log_weight_div_det();
//        public native @Const CvMat  get_inv_eigen_values();
//        public native @Const CvMatArray get_cov_rotate_mats();
//
//        protected native void set_params(@ByRef CvEMParams params, @ByRef CvVectors train_data);
//        protected native void init_em(@ByRef CvVectors train_data);
//        protected native double run_em(@ByRef CvVectors train_data);
//        protected native void init_auto(@ByRef CvVectors samples);
//        protected native void kmeans(@ByRef CvVectors train_data, int nclusters,
//                CvMat labels, @ByVal CvTermCriteria criteria, CvMat means);
//        @ByRef
//        protected native CvEMParams params();
//        protected native double log_likelihood();
//
//        protected native CvMat means();
//        protected native CvMatArray covs();
//        protected native CvMat weights();
//        protected native CvMat probs();
//
//        protected native CvMat log_weight_div_det();
//        protected native CvMat inv_eigen_values();
//        protected native CvMatArray cov_rotate_mats();
    }


    public static class CvPair16u32s extends Pointer {
        static { load(); }
        public CvPair16u32s() { allocate(); }
        public CvPair16u32s(int size) { allocateArray(size); }
        public CvPair16u32s(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        @Cast("unsigned short*")
        public native ShortPointer u(); public native CvPair16u32s u(ShortPointer u);
        public native IntPointer i();   public native CvPair16u32s i(IntPointer i);
    }

    public static native int CV_DTREE_CAT_DIR(int idx, int[] subset);

    public static class CvDTreeSplit extends Pointer {
        static { load(); }
        public CvDTreeSplit() { allocate(); }
        public CvDTreeSplit(int size) { allocateArray(size); }
        public CvDTreeSplit(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int var_idx();            public native CvDTreeSplit var_idx(int var_idx);
        public native int condensed_idx();      public native CvDTreeSplit condensed_idx(int condensed_idx);
        public native int inversed();           public native CvDTreeSplit inversed(int inversed);
        public native float quality();          public native CvDTreeSplit quality(float quality);
        public native CvDTreeSplit next();      public native CvDTreeSplit next(CvDTreeSplit next);
        public native int subset/*[2]*/(int i); public native CvDTreeSplit subset(int i, int subset);
        @Name("ord.c")
        public native float ord_c();            public native CvDTreeSplit ord_c(float ord_c);
        @Name("ord.split_point")
        public native int ord_split_point();    public native CvDTreeSplit ord_split_point(int ord_split_point);
    }

    public static class CvDTreeNode extends Pointer {
        static { load(); }
        public CvDTreeNode() { allocate(); }
        public CvDTreeNode(int size) { allocateArray(size); }
        public CvDTreeNode(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int class_idx();              public native CvDTreeNode class_idx(int class_idx);
        public native int Tn();                     public native CvDTreeNode Tn(int Tn);
        public native double value();               public native CvDTreeNode value(double value);

        public native CvDTreeNode parent();         public native CvDTreeNode parent(CvDTreeNode parent);
        public native CvDTreeNode left();           public native CvDTreeNode left(CvDTreeNode left);
        public native CvDTreeNode right();          public native CvDTreeNode right(CvDTreeNode right);

        public native CvDTreeSplit split();         public native CvDTreeNode split(CvDTreeSplit split);

        public native int sample_count();           public native CvDTreeNode sample_count(int sample_count);
        public native int depth();                  public native CvDTreeNode depth(int depth);
        public native IntPointer num_valid();       public native CvDTreeNode num_valid(IntPointer num_valid);
        public native int offset();                 public native CvDTreeNode offset(int offset);
        public native int buf_idx();                public native CvDTreeNode buf_idx(int buf_idx);
        public native double maxlr();               public native CvDTreeNode maxlr(double maxlr);

        public native int complexity();             public native CvDTreeNode complexity(int complexity);
        public native double alpha();               public native CvDTreeNode alpha(double alpha);
        public native double node_risk();           public native CvDTreeNode node_risk(double node_risk);
        public native double tree_risk();           public native CvDTreeNode tree_risk(double tree_risk);
        public native double tree_error();          public native CvDTreeNode tree_error(double tree_error);

        public native IntPointer cv_Tn();            public native CvDTreeNode cv_Tn(IntPointer cv_Tn);
        public native DoublePointer cv_node_risk();  public native CvDTreeNode cv_node_risk(DoublePointer cv_node_risk);
        public native DoublePointer cv_node_error(); public native CvDTreeNode cv_node_error(DoublePointer cv_node_error);

        public native int get_num_valid(int vi);
        public native void set_num_valid(int vi, int n);
    }

    @NoOffset public static class CvDTreeParams extends Pointer {
        static { load(); }
        public CvDTreeParams() { allocate(); }
        public CvDTreeParams(int _max_depth, int _min_sample_count, float _regression_accuracy, boolean _use_surrogates,
                int _max_categories, int _cv_folds, boolean _use_1se_rule, boolean _truncate_pruned_tree, float[] _priors) {
            allocate(_max_depth, _min_sample_count, _regression_accuracy, _use_surrogates,
                    _max_categories, _cv_folds, _use_1se_rule, _truncate_pruned_tree, _priors);
        }
        public CvDTreeParams(int size) { allocateArray(size); }
        public CvDTreeParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _max_depth, int _min_sample_count, float _regression_accuracy, boolean _use_surrogates,
                int _max_categories, int _cv_folds, boolean _use_1se_rule, boolean _truncate_pruned_tree, float[] _priors);
        private native void allocateArray(int size);

        public native int max_categories();           public native CvDTreeParams max_categories(int max_categories);
        public native int max_depth();                public native CvDTreeParams max_depth(int max_depth);
        public native int min_sample_count();         public native CvDTreeParams min_sample_count(int min_sample_count);
        public native int cv_folds();                 public native CvDTreeParams cv_folds(int cv_folds);
        public native boolean use_surrogates();       public native CvDTreeParams use_surrogates(boolean use_surrogates);
        public native boolean use_1se_rule();         public native CvDTreeParams use_1se_rule(boolean use_1se_rule);
        public native boolean truncate_pruned_tree(); public native CvDTreeParams truncate_pruned_tree(boolean truncate_pruned_tree);
        public native float regression_accuracy();    public native CvDTreeParams regression_accuracy(float regression_accuracy);
        public native @Const FloatPointer priors();   public native CvDTreeParams priors(FloatPointer priors);
    }

    @NoOffset public static class CvDTreeTrainData extends Pointer {
        static { load(); }
        public CvDTreeTrainData() { allocate(); }
        public CvDTreeTrainData(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                CvDTreeParams params/*=CvDTreeParams()*/, boolean _shared/*=false*/, boolean _add_labels/*=false*/) {
            allocate(trainData, tflag, responses, varIdx, sampleIdx, varType, missingDataMask, params, _shared, _add_labels);
        }
        public CvDTreeTrainData(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/, @ByRef
                CvDTreeParams params/*=CvDTreeParams()*/, boolean _shared/*=false*/, boolean _add_labels/*=false*/);

        public native void set_data(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/, @ByRef
                CvDTreeParams params/*=CvDTreeParams()*/, boolean _shared/*=false*/, boolean _add_labels/*=false*/,
                boolean _update_data/*=false*/);
        public native void do_responses_copy();

        public native void get_vectors(CvMat _subsample_idx, float[] values,
                @Cast("uchar*") byte[] missing, float[] responses, boolean get_class_idx/*=false*/);

        public native CvDTreeNode subsample_data(CvMat _subsample_idx);

        public native void write_params(CvFileStorage fs);
        public native void read_params(CvFileStorage fs, CvFileNode node);

        public native void clear();

        public native int get_num_classes();
        public native int get_var_type(int vi);
        public native int get_work_var_count();

        public native @Const FloatPointer get_ord_responses(CvDTreeNode n, float[] values_buf, int[] sample_indices_buf);
        public native @Const IntPointer get_class_labels(CvDTreeNode n, int[] labels_buf);
        public native @Const IntPointer get_cv_labels(CvDTreeNode n, int[] labels_buf);
        public native @Const IntPointer get_sample_indices(CvDTreeNode n, int[] indices_buf);
        public native @Const IntPointer get_cat_var_data(CvDTreeNode n, int vi, int[] cat_values_buf);
        public native void get_ord_var_data(CvDTreeNode n, int vi, float[] ord_values_buf, int[] sorted_indices_buf,
                @Cast("const float**") PointerPointer ord_values, @Cast("const int**") PointerPointer sorted_indices,
                int[] sample_indices_buf);
        public native int get_child_buf_idx(CvDTreeNode n);

        public native boolean set_params(@ByRef CvDTreeParams params);
        public native CvDTreeNode new_node(CvDTreeNode parent, int count, int storage_idx, int offset);

        public native CvDTreeSplit new_split_ord(int vi, float cmp_val, int split_point, int inversed, float quality);
        public native CvDTreeSplit new_split_cat(int vi, float quality);
        public native void free_node_data(CvDTreeNode node);
        public native void free_train_data();
        public native void free_node(CvDTreeNode node);

        public native int sample_count();      public native CvDTreeTrainData sample_count(int sample_count);
        public native int var_all();           public native CvDTreeTrainData var_all(int var_all);
        public native int var_count();         public native CvDTreeTrainData var_count(int var_count);
        public native int max_c_count();       public native CvDTreeTrainData max_c_count(int max_c_count);
        public native int ord_var_count();     public native CvDTreeTrainData ord_var_count(int ord_var_count);
        public native int cat_var_count();     public native CvDTreeTrainData cat_var_count(int cat_var_count);
        public native int work_var_count();    public native CvDTreeTrainData work_var_count(int work_var_count);
        public native boolean have_labels();   public native CvDTreeTrainData have_labels(boolean have_labels);
        public native boolean have_priors();   public native CvDTreeTrainData have_priors(boolean have_priors);
        public native boolean is_classifier(); public native CvDTreeTrainData is_classifier(boolean is_classifier);
        public native int tflag();             public native CvDTreeTrainData tflag(int tflag);

        public native @Const CvMat train_data(); public native CvDTreeTrainData train_data(CvMat train_data);
        public native @Const CvMat responses();  public native CvDTreeTrainData responses(CvMat responses);
        public native CvMat responses_copy();    public native CvDTreeTrainData responses_copy(CvMat responses_copy);

        public native int buf_count();         public native CvDTreeTrainData buf_count(int buf_count);
        public native int buf_size();          public native CvDTreeTrainData buf_size(int buf_size);
        public native boolean shared();        public native CvDTreeTrainData shared(boolean shared);
        public native int is_buf_16u();        public native CvDTreeTrainData is_buf_16u(int is_buf_16u);

        public native CvMat cat_count();       public native CvDTreeTrainData cat_count(CvMat cat_count);
        public native CvMat cat_ofs();         public native CvDTreeTrainData cat_ofs(CvMat cat_ofs);
        public native CvMat cat_map();         public native CvDTreeTrainData cat_map(CvMat cat_map);

        public native CvMat counts();          public native CvDTreeTrainData counts(CvMat counts);
        public native CvMat buf();             public native CvDTreeTrainData buf(CvMat buf);
        public native CvMat direction();       public native CvDTreeTrainData direction(CvMat direction);
        public native CvMat split_buf();       public native CvDTreeTrainData split_buf(CvMat split_buf);

        public native CvMat var_idx();         public native CvDTreeTrainData var_idx(CvMat var_idx);
        public native CvMat var_type();        public native CvDTreeTrainData var_type(CvMat var_type);
        public native CvMat priors();          public native CvDTreeTrainData priors(CvMat priors);
        public native CvMat priors_mult();     public native CvDTreeTrainData priors_mult(CvMat priors_mult);

        public native @ByRef CvDTreeParams params();  public native CvDTreeTrainData params(CvDTreeParams params);

        public native CvMemStorage tree_storage(); public native CvDTreeTrainData tree_storage(CvMemStorage tree_storage);
        public native CvMemStorage temp_storage(); public native CvDTreeTrainData temp_storage(CvMemStorage temp_storage);

        public native CvDTreeNode data_root(); public native CvDTreeTrainData data_root(CvDTreeNode data_root);

        public native CvSet node_heap();       public native CvDTreeTrainData node_heap(CvSet node_heap);
        public native CvSet split_heap();      public native CvDTreeTrainData split_heap(CvSet split_heap);
        public native CvSet cv_heap();         public native CvDTreeTrainData cv_heap(CvSet cv_heap);
        public native CvSet nv_heap();         public native CvDTreeTrainData nv_heap(CvSet nv_heap);

        public native @Adapter("RNGAdapter") CvRNG rng(); public native CvDTreeTrainData rng(CvRNG rng);
    }

    @NoOffset public static class CvDTree extends CvStatModel {
        static { Loader.load(); }
        public CvDTree() { allocate(); }
        public CvDTree(Pointer p) { super(p); }
        private native void allocate();

        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvDTreeParams params/*=CvDTreeParams()*/);
        public native boolean train(CvMLData trainData, @ByVal CvDTreeParams params/*=CvDTreeParams()*/);
        public native float calc_error(CvMLData trainData, int type,
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer resp/*=null*/);
        public native boolean train(CvDTreeTrainData trainData, CvMat subsampleIdx);
        public native CvDTreeNode predict(CvMat sample, CvMat missingDataMask/*=0*/, boolean preprocessedInput/*=false*/);
        public native @Const CvMat get_var_importance();
//        public native void clear();
//
//        public native void read(CvFileStorage fs, CvFileNode node);
//        public native void write(CvFileStorage fs, String name);

        public native void read( CvFileStorage fs, CvFileNode node, CvDTreeTrainData data);
        public native void write(CvFileStorage fs);

        public native @Const CvDTreeNode get_root();
        public native int get_pruned_tree_idx();
        public native CvDTreeTrainData get_data();

//        protected native boolean do_train(CvMat _subsample_idx );
//
//        protected native void try_split_node(CvDTreeNode n);
//        protected native void split_node_data(CvDTreeNode n);
//        protected native CvDTreeSplit find_best_split(CvDTreeNode n);
//        protected native CvDTreeSplit find_split_ord_class(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_class(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_ord_reg(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_reg(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_surrogate_split_ord(CvDTreeNode n, int vi, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_surrogate_split_cat(CvDTreeNode n, int vi, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native double calc_node_dir(CvDTreeNode node);
//        protected native void complete_node_dir(CvDTreeNode node);
//        protected native void cluster_categories(int[] vectors, int vector_count,
//                int var_count, int[] sums, int k, int[] cluster_labels);
//
//        protected native void calc_node_value(CvDTreeNode node);
//
//        protected native void prune_cv();
//        protected native double update_tree_rnc(int T, int fold);
//        protected native int cut_tree(int T, int fold, double min_alpha);
//        protected native void free_prune_data(boolean cut_tree);
//        protected native void free_tree();
//
//        protected native void write_node(CvFileStorage fs, CvDTreeNode node);
//        protected native void write_split(CvFileStorage fs, CvDTreeSplit split);
//        protected native CvDTreeNode read_node(CvFileStorage fs, CvFileNode node, CvDTreeNode parent);
//        protected native CvDTreeSplit read_split(CvFileStorage fs, CvFileNode node);
//        protected native void write_tree_nodes(CvFileStorage fs);
//        protected native void read_tree_nodes(CvFileStorage fs, CvFileNode node);
//
//        protected native CvDTreeNode root();
//        protected native CvMat var_importance();
//        protected native CvDTreeTrainData data();

        public native int pruned_tree_idx(); public native CvDTree pruned_tree_idx(int pruned_tree_idx);
    }


    public static class CvForestTree extends CvDTree {
        static { Loader.load(); }
        public CvForestTree() { allocate(); }
        public CvForestTree(Pointer p) { super(p); }
        private native void allocate();

        public native boolean train(CvDTreeTrainData trainData, CvMat _subsample_idx, CvRTrees forest);

        public native int get_var_count();
        public native void read(CvFileStorage fs, CvFileNode node, CvRTrees forest, CvDTreeTrainData _data);

//        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
//                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
//                @ByVal CvDTreeParams params/*=CvDTreeParams()*/);
//        public native boolean train(CvDTreeTrainData trainData, CvMat _subsample_idx);
//        public native void read(CvFileStorage fs, CvFileNode node);
//        public native void read(CvFileStorage fs, CvFileNode node, CvDTreeTrainData data);
//
//        protected native CvDTreeSplit find_best_split(CvDTreeNode n);
//        protected native CvRTrees forest();
    }


    @NoOffset public static class CvRTParams extends CvDTreeParams {
        static { load(); }
        public CvRTParams() { allocate(); }
        public CvRTParams(int _max_depth, int _min_sample_count, float _regression_accuracy, boolean _use_surrogates,
                int _max_categories, float[] _priors, boolean _calc_var_importance, int _nactive_vars,
                int max_num_of_trees_in_the_forest, float forest_accuracy, int termcrit_type) {
            allocate(_max_depth, _min_sample_count, _regression_accuracy, _use_surrogates,
                    _max_categories, _priors, _calc_var_importance, _nactive_vars,
                    max_num_of_trees_in_the_forest, forest_accuracy, termcrit_type);
        }
        public CvRTParams(int size) { allocateArray(size); }
        public CvRTParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _max_depth, int _min_sample_count, float _regression_accuracy, boolean _use_surrogates,
                int _max_categories, float[] _priors, boolean _calc_var_importance, int _nactive_vars,
                int max_num_of_trees_in_the_forest, float forest_accuracy, int termcrit_type);
        private native void allocateArray(int size);

        public native boolean calc_var_importance();     public native CvRTParams calc_var_importance(boolean calc_var_importance);
        public native int nactive_vars();                public native CvRTParams nactive_vars(int nactive_vars);
        public native @ByRef CvTermCriteria term_crit(); public native CvRTParams term_crit(CvTermCriteria term_crit);
    }

    public static class CvRTrees extends CvStatModel {
        static { Loader.load(); }
        public CvRTrees() { allocate(); }
        public CvRTrees(Pointer p) { super(p); }
        private native void allocate();

        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvRTParams params/*=CvRTParams()*/);
        public native boolean train(CvMLData data, @ByVal CvRTParams params/*=CvRTParams()*/);
        public native float predict(CvMat sample, CvMat missing/*=null*/);
        public native float predict_prob(CvMat sample, CvMat missing/*=0*/);

//        public native void clear();

        public native @Const CvMat get_var_importance();
        public native float get_proximity(CvMat sample1, CvMat sample2, CvMat missing1/*=null*/, CvMat missing2/*=null*/);

        public native float calc_error(CvMLData _data, int type, 
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer resp/*=null*/);

        public native float get_train_error();

//        public native void read(CvFileStorage fs, CvFileNode node);
//        public native void write(CvFileStorage fs, String name);

        public native CvMat get_active_var_mask();
        public native CvRNG get_rng();

        public native int get_tree_count();
        public native CvForestTree get_tree(int i);

//        protected native boolean grow_forest(@ByVal CvTermCriteria term_crit);
//
//        protected native @Cast("CvForestTree**") PointerPointer trees();
//        protected native CvDTreeTrainData data();
//        protected native int ntrees();
//        protected native int nclasses();
//        protected native double oob_error();
//        protected native CvMat var_importance();
//        protected native int nsamples();
//
//        protected native @Adapter("RNGAdapter") CvRNG rng();
//        protected native CvMat active_var_mask();
    }


    @NoOffset public static class CvERTreeTrainData extends CvDTreeTrainData {
        static { load(); }
        public CvERTreeTrainData() { allocate(); }
        public CvERTreeTrainData(Pointer p) { super(p); }
        private native void allocate();

//        public native void set_data(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
//                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
//                @ByRef CvDTreeParams params/*=CvDTreeParams()*/, boolean _shared/*=false*/,
//                boolean _add_labels/*=false*/, boolean _update_data/*=false*/);
//        public native void get_ord_var_data(CvDTreeNode n, int vi, float[] ord_values_buf, int[] missing_buf,
//                @Cast("const float**") PointerPointer ord_values, @Cast("const int**") PointerPointer missing,
//                int[] sample_buf/*=null*/);
//        public native @Const IntPointer get_sample_indices(CvDTreeNode n, int[] indices_buf );
//        public native @Const IntPointer get_cv_labels(CvDTreeNode n, int[] labels_buf );
//        public native @Const IntPointer get_cat_var_data(CvDTreeNode n, int vi, int[] cat_values_buf );
//        public native void get_vectors(CvMat _subsample_idx, float[] values,
//                @Cast("uchar*") byte[] missing, float[] responses, boolean get_class_idx/*=false*/);
//        public native CvDTreeNode subsample_data(CvMat _subsample_idx);

        public native @Const CvMat missing_mask(); public native CvERTreeTrainData missing_mask(CvMat missing_mask);
    }

    public static class CvForestERTree extends CvForestTree {
        static { Loader.load(); }
        public CvForestERTree() { allocate(); }
        public CvForestERTree(Pointer p) { super(p); }
        private native void allocate();

//        protected native double calc_node_dir(CvDTreeNode node);
//        protected native CvDTreeSplit find_split_ord_class( CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_class( CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_ord_reg( CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_reg( CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native void split_node_data(CvDTreeNode n);
    }

    public static class CvERTrees extends CvRTrees {
        static { Loader.load(); }
        public CvERTrees() { allocate(); }
        public CvERTrees(Pointer p) { super(p); }
        private native void allocate();

//        public native boolean train( CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
//                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
//                @ByVal CvRTParams params/*=CvRTParams()*/);
//        public native boolean train(CvMLData data, @ByVal CvRTParams params/*=CvRTParams()*/);
//
//        protected native boolean grow_forest(@ByVal CvTermCriteria term_crit);
    }


    @NoOffset public static class CvBoostParams extends CvDTreeParams {
        static { load(); }
        public CvBoostParams() { allocate(); }
        public CvBoostParams(int boost_type, int weak_count, double weight_trim_rate,
                int max_depth, boolean use_surrogates, float[] priors) {
            allocate(boost_type, weak_count, weight_trim_rate, max_depth, use_surrogates, priors);
        }
        public CvBoostParams(int size) { allocateArray(size); }
        public CvBoostParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int boost_type, int weak_count, double weight_trim_rate,
                int max_depth, boolean use_surrogates, float[] priors);
        private native void allocateArray(int size);

        public native int boost_type();          public native CvBoostParams boost_type(int boost_type);
        public native int weak_count();          public native CvBoostParams weak_count(int weak_count);
        public native int split_criteria();      public native CvBoostParams split_criteria(int split_criteria);
        public native double weight_trim_rate(); public native CvBoostParams weight_trim_rate(double weight_trim_rate);
    }

    public static class CvBoostTree extends CvDTree {
        static { Loader.load(); }
        public CvBoostTree() { allocate(); }
        public CvBoostTree(Pointer p) { super(p); }
        private native void allocate();

        public native boolean train(CvDTreeTrainData trainData, CvMat subsample_idx, CvBoost ensemble);

        public native void scale( double s );
        public native void read(CvFileStorage fs, CvFileNode node, CvBoost ensemble, CvDTreeTrainData _data);
//        public native void clear();
//
//        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
//                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
//                @ByVal CvDTreeParams params/*=CvDTreeParams()*/);
//        public native boolean train(CvDTreeTrainData trainData, CvMat subsampleIdx);
//
//        public native void read(CvFileStorage fs, CvFileNode node);
//        public native void read(CvFileStorage fs, CvFileNode node, CvDTreeTrainData data);
//
//        protected native void try_split_node(CvDTreeNode n);
//        protected native CvDTreeSplit find_surrogate_split_ord(CvDTreeNode n, int vi, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_surrogate_split_cat(CvDTreeNode n, int vi, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_ord_class(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_class(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_ord_reg(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native CvDTreeSplit find_split_cat_reg(CvDTreeNode n, int vi, float init_quality/*=0*/,
//                CvDTreeSplit _split/*=null*/, @Cast("uchar*") byte[] ext_buf/*=null*/);
//        protected native void calc_node_value(CvDTreeNode n);
//        protected native double calc_node_dir(CvDTreeNode n);
//
//        protected native CvBoost ensemble();
    }

    public static class CvBoost extends CvStatModel {
        static { Loader.load(); }
        public CvBoost() { allocate(); }
        public CvBoost(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvBoostParams params/*=CvBoostParams()*/) {
            allocate(trainData, tflag, responses, varIdx, sampleIdx, varType, missingDataMask, params);
        }
        public CvBoost(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvBoostParams params/*=CvBoostParams()*/);

        public static final int DISCRETE=0, REAL=1, LOGIT=2, GENTLE=3;

        public static final int DEFAULT=0, GINI=1, MISCLASS=3, SQERR=4;

        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvBoostParams params/*=CvBoostParams()*/, boolean update/*=false*/);
        public native boolean train(CvMLData data, @ByVal CvBoostParams params/*=CvBoostParams()*/, boolean update/*=false*/);
        public native float predict(CvMat sample, CvMat missing/*=null*/, CvMat weak_responses/*=null*/,
                @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/, boolean raw_mode/*=false*/, boolean return_sum/*=false*/);

        public native float calc_error(CvMLData _data, int type, 
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer resp/*=null*/);
        public native void prune(@ByVal CvSlice slice);
//        public native void clear();
//
//        public native void write(CvFileStorage storage, String name);
//        public native void read(CvFileStorage storage, CvFileNode node);
        public native @Const CvMat get_active_vars(boolean absolute_idx/*=true*/);

        public native CvSeq get_weak_predictors();

        public native CvMat get_weights();
        public native CvMat get_subtree_weights();
        public native CvMat get_weak_response();
        public native @Const @ByRef CvBoostParams get_params();
        public native @Const CvDTreeTrainData get_data();

//        protected native boolean set_params(@ByRef CvBoostParams params);
//        protected native void update_weights(CvBoostTree tree);
//        protected native void trim_weights();
//        protected native void write_params(CvFileStorage fs);
//        protected native void read_params(CvFileStorage fs, CvFileNode node);
//
//        protected native CvDTreeTrainData data();
//        protected native @ByRef CvBoostParams params();
//        protected native CvSeq weak();
//
//        protected native CvMat active_vars();
//        protected native CvMat active_vars_abs();
//        protected native boolean have_active_cat_vars();
//
//        protected native CvMat orig_response();
//        protected native CvMat sum_response();
//        protected native CvMat weak_eval();
//        protected native CvMat subsample_mask();
//        protected native CvMat weights();
//        protected native CvMat subtree_weights();
//        protected native boolean have_subsample();
    }


    @NoOffset public static class CvGBTreesParams extends CvDTreeParams {
        static { load(); }
        public CvGBTreesParams() { allocate(); }
        public CvGBTreesParams(int loss_function_type, int weak_count, float shrinkage,
        float subsample_portion, int max_depth, boolean use_surrogates) {
            allocate(loss_function_type, weak_count, shrinkage, subsample_portion, max_depth, use_surrogates);
        }
        public CvGBTreesParams(int size) { allocateArray(size); }
        public CvGBTreesParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int loss_function_type, int weak_count, float shrinkage,
        float subsample_portion, int max_depth, boolean use_surrogates);
        private native void allocateArray(int size);

        public native int weak_count();          public native CvGBTreesParams weak_count(int weak_count);
        public native int loss_function_type();  public native CvGBTreesParams loss_function_type(int loss_function_type);
        public native float subsample_portion(); public native CvGBTreesParams subsample_portion(float subsample_portion);
        public native float shrinkage();         public native CvGBTreesParams shrinkage(float shrinkage);
    }

    public static class CvGBTrees extends CvStatModel {
        static { Loader.load(); }
        public CvGBTrees() { allocate(); }
        public CvGBTrees(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvGBTreesParams params/*=CvGBTreesParams()*/) {
            allocate(trainData, tflag, responses, varIdx, sampleIdx, varType, missingDataMask, params);
        }
        public CvGBTrees(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvGBTreesParams params/*=CvGBTreesParams()*/);

        public static final int SQUARED_LOSS=0, ABSOLUTE_LOSS=1, HUBER_LOSS=3, DEVIANCE_LOSS=4;

        public native boolean train(CvMat trainData, int tflag, CvMat responses, CvMat varIdx/*=null*/,
                CvMat sampleIdx/*=null*/, CvMat varType/*=null*/, CvMat missingDataMask/*=null*/,
                @ByVal CvGBTreesParams params/*=CvGBTreesParams()*/, boolean update/*=false*/);
        public native boolean train(CvMLData data, @ByVal CvGBTreesParams params/*=CvGBTreesParams()*/, boolean update/*=false*/);
        public native float predict(CvMat sample, CvMat missing/*=null*/, CvMat weakResponses/*=null*/,
                @ByVal CvSlice slice/*=CV_WHOLE_SEQ*/, int k/*=-1*/);

//        public native void clear();
        public native float calc_error(CvMLData _data, int type,
                @Adapter(value="VectorAdapter<float>", out=true) FloatPointer resp/*=null*/);

//        public native void write(CvFileStorage fs, String name);
//        public native void read(CvFileStorage fs, CvFileNode node);
//
//        protected native void find_gradient(int k/*=0*/);
//        protected native void change_values(CvDTree tree, int k/*=0*/);
//        protected native float find_optimal_value(CvMat _Idx );
//        protected native void do_subsample();
//        protected native void leaves_get(@Cast("CvDTreeNode**") PointerPointer leaves, @ByRef int[] count, CvDTreeNode node);
//        protected native @Cast("CvDTreeNode**") PointerPointer GetLeaves(CvDTree dtree, @ByRef int[] len);
//        protected native boolean problem_type();
//        protected native void write_params(CvFileStorage fs);
//        protected native void read_params(CvFileStorage fs, CvFileNode fnode);
//
//        protected native CvDTreeTrainData data();
//        protected native @ByRef CvGBTreesParams params();
//
//        protected native @Cast("CvSeq**") PointerPointer weak();
//        protected native CvMat orig_response();
//        protected native CvMat sum_response();
//        protected native CvMat sum_response_tmp();
//        protected native CvMat weak_eval();
//        protected native CvMat sample_idx();
//        protected native CvMat subsample_train();
//        protected native CvMat subsample_test();
//        protected native CvMat missing();
//        protected native CvMat class_labels();
//
//        protected native @Adapter("RNGAdapter") CvRNG rng();
//
//        protected native int class_count();
//        protected native float delta();
//        protected native float base_value();
    }


    @NoOffset public static class CvANN_MLP_TrainParams extends Pointer {
        static { load(); }
        public CvANN_MLP_TrainParams() { allocate(); }
        public CvANN_MLP_TrainParams(CvTermCriteria term_crit, int train_method, double param1, double param2/*=0*/) {
            allocate(term_crit, train_method, param1, param2);
        }
        public CvANN_MLP_TrainParams(int size) { allocateArray(size); }
        public CvANN_MLP_TrainParams(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(@ByVal CvTermCriteria term_crit, int train_method, double param1, double param2/*=0*/);
        private native void allocateArray(int size);

        public static final int BACKPROP=0, RPROP=1;

        @ByRef
        public native CvTermCriteria term_crit(); public native CvANN_MLP_TrainParams term_crit(CvTermCriteria term_crit);
        public native int train_method();         public native CvANN_MLP_TrainParams train_method(int train_method);

        public native double bp_dw_scale();       public native CvANN_MLP_TrainParams bp_dw_scale(double bp_dw_scale);
        public native double bp_moment_scale();   public native CvANN_MLP_TrainParams bp_moment_scale(double bp_moment_scale);

        public native double rp_dw0();            public native CvANN_MLP_TrainParams rp_dw0(double rp_dw0);
        public native double rp_dw_plus();        public native CvANN_MLP_TrainParams rp_dw_plus(double rp_dw_plus);
        public native double rp_dw_minus();       public native CvANN_MLP_TrainParams rp_dw_minus(double rp_dw_minus);
        public native double rp_dw_min();         public native CvANN_MLP_TrainParams rp_dw_min(double rp_dw_min);
        public native double rp_dw_max();         public native CvANN_MLP_TrainParams rp_dw_max(double rp_dw_max);
    }


    public static class CvANN_MLP extends CvStatModel {
        static { Loader.load(); }
        public CvANN_MLP() { allocate(); }
        public CvANN_MLP(CvMat layerSizes, int activateFunc/*=CvANN_MLP::SIGMOID_SYM*/,
                double fparam1/*=0, double fparam2/*=0*/) {
            allocate(layerSizes, activateFunc, fparam1);
        }
        public CvANN_MLP(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(CvMat layerSizes, int activateFunc/*=CvANN_MLP::SIGMOID_SYM*/,
                double fparam1/*=0, double fparam2/*=0*/);

        public native void create(CvMat layerSizes, int activateFunc/*=CvANN_MLP::SIGMOID_SYM*/,
                double fparam1/*=0, double fparam2/*=0*/);

        public native int train(CvMat inputs, CvMat outputs, CvMat sampleWeights, CvMat sampleIdx/*=null*/,
                @ByVal CvANN_MLP_TrainParams params/*=CvANN_MLP_TrainParams()*/, int flags/*=0*/);
        public native float predict(CvMat inputs, CvMat outputs);

//        public native void clear();

        public static final int IDENTITY = 0, SIGMOID_SYM = 1, GAUSSIAN = 2;

        public static final int UPDATE_WEIGHTS = 1, NO_INPUT_SCALE = 2, NO_OUTPUT_SCALE = 4;

//        public native void read(CvFileStorage fs, CvFileNode node);
//        public native void write(CvFileStorage storage, String name);

        public native int get_layer_count();
        public native @Const CvMat get_layer_sizes();
        public native DoublePointer get_weights(int layer);

//        protected native boolean prepare_to_train(CvMat _inputs, CvMat _outputs, CvMat _sample_weights,
//                CvMat sampleIdx, CvVectors _ivecs, CvVectors _ovecs, @Cast("double**") PointerPointer _sw, int _flags);
//
//        protected native int train_backprop(CvVectors _ivecs, CvVectors _ovecs, double[] _sw);
//
//        protected native int train_rprop(CvVectors _ivecs, CvVectors _ovecs, double[] _sw);
//
//        protected native void calc_activ_func(CvMat xf, double[] bias);
//        protected native void calc_activ_func_deriv(CvMat xf, CvMat deriv, double[] bias);
//        protected native void set_activ_func(int _activ_func/*=SIGMOID_SYM*/,
//                double _f_param1/*=0*/, double _f_param2/*=0*/);
//        protected native void init_weights();
//        protected native void scale_input(CvMat _src, CvMat _dst);
//        protected native void scale_output(CvMat _src, CvMat _dst);
//        protected native void calc_input_scale(CvVectors vecs, int flags);
//        protected native void calc_output_scale(CvVectors vecs, int flags);
//
//        protected native void write_params(CvFileStorage fs);
//        protected native void read_params(CvFileStorage fs, CvFileNode node);
//
//        protected native CvMat layer_sizes();
//        protected native CvMat wbuf();
//        protected native CvMat sample_weights();
//        protected native @Cast("double**") PointerPointer weights();
//        protected native double f_param1();
//        protected native double f_param2();
//        protected native double min_val();
//        protected native double max_val();
//        protected native double min_val1();
//        protected native double max_val1();
//        protected native int activ_func();
//        protected native int max_count();
//        protected native int max_buf_sz();
//        protected native @ByRef CvANN_MLP_TrainParams params();
//        protected native @Adapter("RNGAdapter") CvRNG rng();
    }

    public static native void cvRandMVNormal(CvMat mean, CvMat cov, CvMat sample, CvRNG rng/*=null*/);
    public static native void cvRandGaussMixture(CvMatArray means, CvMatArray covs, float[] weights,
            int clsnum, CvMat sample, CvMat sampClasses/*=null*/);
    public static final int CV_TS_CONCENTRIC_SPHERES = 0;
    public static native void cvCreateTestSet(int type, CvMatArray samples, int num_samples,
            int num_features, CvMatArray responses, int num_classes, Pointer /* ...? */ p);


    public static final int
            CV_COUNT    = 0,
            CV_PORTION  = 1;

    @NoOffset public static class CvTrainTestSplit extends Pointer {
        static { load(); }
        public CvTrainTestSplit() { allocate(); }
        public CvTrainTestSplit(int _train_sample_count, boolean _mix/*=true*/) { allocate(_train_sample_count, _mix); }
        public CvTrainTestSplit(float _train_sample_portion, boolean _mix/*=true*/) { allocate(_train_sample_portion, _mix); }
        public CvTrainTestSplit(int size) { allocateArray(size); }
        public CvTrainTestSplit(Pointer p) { super(p); }
        private native void allocate();
        private native void allocate(int _train_sample_count, boolean _mix/*=true*/);
        private native void allocate(float _train_sample_portion, boolean _mix/*=true*/);
        private native void allocateArray(int size);

        @Name("train_sample_part.count")
        public native int train_sample_part_count();     public native CvTrainTestSplit train_sample_part_count(int train_sample_part_count);
        @Name("train_sample_part.portion")
        public native float train_sample_part_portion(); public native CvTrainTestSplit train_sample_part_portion(float train_sample_part_portion);
        public native int train_sample_part_mode();      public native CvTrainTestSplit train_sample_part_mode(int train_sample_part_mode);

        @Name("class_part->count") @NoOffset
        public native IntPointer class_part_count();     public native CvTrainTestSplit class_part_count(IntPointer class_part_count);
        @Name("class_part->portion") @NoOffset
        public native FloatPointer class_part_portion(); public native CvTrainTestSplit class_part_portion(FloatPointer class_part_portion);
        public native int class_part_mode();             public native CvTrainTestSplit class_part_mode(int class_part_mode);

        public native boolean mix();                     public native CvTrainTestSplit mix(boolean mix);
    }

    public static class CvMLData extends Pointer {
        static { load(); }
        public CvMLData() { allocate(); }
        public CvMLData(int size) { allocateArray(size); }
        public CvMLData(Pointer p) { super(p); }
        private native void allocate();
        private native void allocateArray(int size);

        public native int read_csv(String filename);

        public native @Const CvMat get_values();
        public native @Const CvMat get_responses();
        public native @Const CvMat get_missing();

        public native void set_response_idx(int idx);
        public native int get_response_idx();

        public native @Const CvMat get_train_sample_idx();
        public native @Const CvMat get_test_sample_idx();
        public native void mix_train_and_test_idx();
        public native void set_train_test_split(CvTrainTestSplit spl);

        public native @Const CvMat get_var_idx();
        public native void chahge_var_idx(int vi, boolean state);

        public native @Const CvMat get_var_types();
        public native int get_var_type(int var_idx);
        public native void set_var_types(String str);
        public native void change_var_type(int var_idx, int type);

        public native void set_delimiter(byte ch);
        public native byte get_delimiter();

        public native void set_miss_ch(byte ch);
        public native byte get_miss_ch();

//        protected native void clear();
//
//        protected native void str_to_flt_elem(String token, @ByRef float[] flt_elem, @ByRef int[] type);
//        protected native void free_train_test_idx();
//
//        protected native byte delimiter();
//        protected native byte miss_ch();
//        //protected native byte flt_separator();
//
//        protected native CvMat values();
//        protected native CvMat missing();
//        protected native CvMat var_types();
//        protected native CvMat var_idx_mask();
//
//        protected native CvMat response_out();
//        protected native CvMat var_idx_out();
//        protected native CvMat var_types_out();
//
//        protected native int response_idx();
//
//        protected native int train_sample_count();
//        protected native boolean mix();
//
//        protected native int total_class_count();
//        protected native StringIntMap /* std::map<std::string, int> * */ class_map();
//
//        protected native CvMat train_sample_idx();
//        protected native CvMat test_sample_idx();
//        protected native IntPointer sample_idx();
//
//        protected native @Adapter("RNGAdapter") CvRNG rng();
    }

}
