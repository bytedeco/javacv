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

#include <opencv2/core/core_c.h>
#include <opencv2/core/core.hpp>

#ifdef _WIN32
#include <windows.h>
#undef min
#undef max
#endif
static inline void SetLibraryPath(const char *path) {
#if _WIN32_WINNT >= 0x0502
    SetDllDirectory(path);
#endif
}

class MatAdapter {
public:
    MatAdapter(const CvArr* ptr, int size) : ptr((CvArr*)ptr), size(size),
            mat2(cv::cvarrToMat(ptr)), mat(mat2) { }
    MatAdapter(const cv::Mat& mat) : ptr(0), size(0), mat2(mat), mat(mat2) { }
    MatAdapter(      cv::Mat& mat) : ptr(0), size(0), mat(mat) { }
    static void deallocate(void* ptr) {
        if (CV_IS_MAT(ptr)) {
            cvReleaseMat  ((CvMat**)   &ptr);
        } else if (CV_IS_MATND(ptr)) {
            cvReleaseMatND((CvMatND**) &ptr);
        } else if (CV_IS_IMAGE(ptr)) {
            cvReleaseImage((IplImage**)&ptr);
        }
    }
    operator CvMat*()    { const CvMat&    m = mat; return CV_IS_MAT  (&m) ? cvCloneMat  (&m) : NULL; }
    operator CvMatND*()  { const CvMatND&  m = mat; return CV_IS_MATND(&m) ? cvCloneMatND(&m) : NULL; }
    operator IplImage*() { const IplImage& m = mat; return CV_IS_IMAGE(&m) ? cvCloneImage(&m) : NULL; }
    operator cv::Mat&()  { return mat; }
    operator cv::Mat*()  { return ptr ? &mat : 0; }

    CvArr* ptr;
    int size;
    cv::Mat mat2;
    cv::Mat& mat;
};

class ArrayAdapter {
public:
    template<class T> ArrayAdapter(const T* ptr, int size) : ptr((T*)ptr), size(size),
            mat(ptr ? std::vector<T>((T*)ptr, (T*)ptr + size) : std::vector<T>(), true), arr2(mat), arr(arr2) { }
    ArrayAdapter(const cv::_OutputArray& arr) : ptr(0), size(0), arr2(arr), arr(arr2) { }
    ArrayAdapter(      cv::_OutputArray& arr) : ptr(0), size(0), arr(arr) { }
    static void deallocate(void* ptr) {
        if (CV_IS_MAT(ptr)) {
            cvReleaseMat  ((CvMat**)   &ptr);
        } else if (CV_IS_MATND(ptr)) {
            cvReleaseMatND((CvMatND**) &ptr);
        } else if (CV_IS_IMAGE(ptr)) {
            cvReleaseImage((IplImage**)&ptr);
        } else {
            free(ptr);
        }
    }
    template<class T> operator T*() {
        if (mat.total() > size) {
            ptr = malloc(sizeof(T) * mat.total());
        }
        if (ptr) {
            std::copy(mat.begin<T>(), mat.end<T>(), (T*)ptr);
        }
        size = mat.total();
        return (T*)ptr;
    }
    operator CvMat*()    { const CvMat&    m = arr.getMatRef(); return CV_IS_MAT  (&m) ? cvCloneMat  (&m) : NULL; }
    operator CvMatND*()  { const CvMatND&  m = arr.getMatRef(); return CV_IS_MATND(&m) ? cvCloneMatND(&m) : NULL; }
    operator IplImage*() { const IplImage& m = arr.getMatRef(); return CV_IS_IMAGE(&m) ? cvCloneImage(&m) : NULL; }
    operator cv::_OutputArray&()  { return arr; }
    operator cv::_OutputArray*()  { return ptr ? &arr : 0; }

    CvArr* ptr;
    size_t size;
    cv::Mat mat;
    cv::_OutputArray arr2;
    cv::_OutputArray& arr;
};

template<> ArrayAdapter::ArrayAdapter(const CvArr* ptr, int size)    : ptr((CvArr*)ptr),
        size(size), mat(cv::cvarrToMat(ptr)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const CvMat* ptr, int size)    : ptr((CvMat*)ptr),
        size(size), mat(cv::cvarrToMat(ptr)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const CvMatND* ptr, int size)  : ptr((CvMatND*)ptr),
        size(size), mat(cv::cvarrToMat(ptr)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const IplImage* ptr, int size) : ptr((IplImage*)ptr),
        size(size), mat(cv::cvarrToMat(ptr)), arr2(mat), arr(arr2) { }

class RNGAdapter {
public:
    RNGAdapter(const CvRNG* ptr, int size) : ptr((CvRNG*)ptr),   size(size), rng2(*ptr), rng(rng2) { }
    RNGAdapter(const cv::RNG &rng) : ptr(new CvRNG(rng.state )), size(0), rng2( rng), rng(rng2) { }
    RNGAdapter(const cv::RNG *rng) : ptr(new CvRNG(rng->state)), size(0), rng2(*rng), rng(rng2) { }
    RNGAdapter(      cv::RNG &rng) : ptr(new CvRNG(rng.state )), size(0), rng( rng) { }
    RNGAdapter(      cv::RNG *rng) : ptr(new CvRNG(rng->state)), size(0), rng(*rng) { }
    static void deallocate(void* ptr) { delete (CvRNG*)ptr; }
    operator CvRNG*() { *ptr = rng.state; return ptr; }
    operator cv::RNG&() { return  rng; }
    operator cv::RNG*() { return &rng; }

    CvRNG* ptr;
    int size;
    cv::RNG rng2;
    cv::RNG& rng;
};

class FileStorageAdapter {
public:
    FileStorageAdapter(const CvFileStorage* ptr, int size) :
            size(size), fs2((CvFileStorage*)ptr), fs(fs2) { fs.fs.addref(); }
    FileStorageAdapter(const cv::FileStorage &fs) : size(0), fs2(fs), fs(fs2) { }
    FileStorageAdapter(      cv::FileStorage &fs) : size(0), fs(fs) { }
    static void deallocate(void* ptr) { }
    operator CvFileStorage*()   { return fs.fs; }
    operator cv::FileStorage&() { return fs; }
    operator cv::FileStorage*() { return &fs; }

    int size;
    cv::FileStorage fs2;
    cv::FileStorage& fs;
};

class FileNodeAdapter {
public:
    FileNodeAdapter(const CvFileStorage* ptr, int size, CvFileNode* ptr2, int size2) :
            size(size), size2(size2), fn2(ptr, ptr2), fn(fn2) { }
    FileNodeAdapter(const cv::FileNode& fn) : size(0), size2(0), fn2(fn), fn(fn2) { }
    FileNodeAdapter(      cv::FileNode& fn) : size(0), size2(0), fn(fn) { }
    static void deallocate(void* ptr) { }
    operator CvFileStorage*() { return (CvFileStorage*)fn.fs; }
    operator CvFileNode*()    { return (CvFileNode*)   fn.node; }
    operator cv::FileNode&()  { return  fn; }
    operator cv::FileNode*()  { return &fn; }

    int size, size2;
    cv::FileNode fn2;
    cv::FileNode& fn;
};

class RectAdapter {
public:
    RectAdapter(const CvRect* ptr, int size) : ptr((CvRect*)ptr), size(size),
            rect2(ptr ? cv::Rect(*ptr) : cv::Rect()), rect(rect2) { }
    RectAdapter(const cv::Rect& rect) : ptr(new CvRect), size(0), rect2(rect), rect(rect2) { }
    RectAdapter(      cv::Rect& rect) : ptr(new CvRect), size(0), rect(rect) { }
    static void deallocate(void* ptr) { delete (CvRect*)ptr; }
    operator CvRect*()   { if (ptr) { *ptr = rect; } return ptr; }
    operator cv::Rect&() { return rect; }
    operator cv::Rect*() { return ptr ? &rect : 0; }

    CvRect* ptr;
    int size;
    cv::Rect rect2;
    cv::Rect& rect;
};

class Point2dAdapter {
public:
    Point2dAdapter(const CvPoint2D64f* ptr, int size) : ptr((CvPoint2D64f*)ptr), size(size),
            point2d2(ptr ? cv::Point2d(ptr->x, ptr->y) : cv::Point2d()), point2d(point2d2) { }
    Point2dAdapter(const cv::Point2d& point2d) : ptr(new CvPoint2D64f), size(0), point2d2(point2d), point2d(point2d2) { }
    Point2dAdapter(      cv::Point2d& point2d) : ptr(new CvPoint2D64f), size(0), point2d(point2d) { }
    static void deallocate(void* ptr) { delete (CvPoint2D64f*)ptr; }
    operator CvPoint2D64f*(){ if (ptr) { ptr->x = point2d.x; ptr->y = point2d.y; } return ptr; }
    operator cv::Point2d&() { return point2d; }
    operator cv::Point2d*() { return ptr ? &point2d : 0; }

    CvPoint2D64f* ptr;
    int size;
    cv::Point2d point2d2;
    cv::Point2d& point2d;
};

template<class T> class PtrAdapter {
public:
    PtrAdapter(const T* ptr, int size)  : ptr((T*)ptr), size(size), cvPtr(cvPtr2) {
            cvPtr2.obj = (T*)ptr; cvPtr2.refcount = 0; }
    PtrAdapter(const cv::Ptr<T>& cvPtr) : ptr(0), size(0), cvPtr2(cvPtr), cvPtr(cvPtr2) { }
    PtrAdapter(      cv::Ptr<T>& cvPtr) : ptr(0), size(0), cvPtr(cvPtr) { }
    void assign(T* ptr, int size) {
        this->ptr = ptr;
        this->size = size;
        this->cvPtr = ptr;
    }
    static void deallocate(void* ptr) { cv::Ptr<T> deallocator((T*)ptr); }
    operator T*() {
        // take ownership
        ptr = cvPtr.obj;
        cvPtr.obj = 0;
        return ptr;
    }
    operator const T*()    { return (const T*)cvPtr; }
    operator cv::Ptr<T>&() { return cvPtr; }
    operator cv::Ptr<T>*() { return ptr ? &cvPtr : 0; }
    T* ptr;
    int size;
    cv::Ptr<T> cvPtr2;
    cv::Ptr<T>& cvPtr;
};
