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
#endif
static inline void SetLibraryPath(const char *path) {
#if _WIN32_WINNT >= 0x0502
    SetDllDirectory(path);
#endif
}

class MatAdapter {
public:
    MatAdapter(const CvArr* pointer, int size) : pointer((CvArr*)pointer), size(size),
            mat2(cv::cvarrToMat(pointer)), mat(mat2) { }
    MatAdapter(const cv::Mat& mat) : pointer(0), size(0), mat((cv::Mat&)mat) { }
    static void deallocate(CvArr* pointer) { 
        if (CV_IS_MAT(pointer)) {
            cvReleaseMat  ((CvMat**)   &pointer);
        } else if (CV_IS_MATND(pointer)) {
            cvReleaseMatND((CvMatND**) &pointer);
        } else if (CV_IS_IMAGE(pointer)) {
            cvReleaseImage((IplImage**)&pointer);
        }
    }
    operator CvMat*()    { const CvMat&    m = mat; return CV_IS_MAT  (&m) ? cvCloneMat  (&m) : NULL; }
    operator CvMatND*()  { const CvMatND&  m = mat; return CV_IS_MATND(&m) ? cvCloneMatND(&m) : NULL; }
    operator IplImage*() { const IplImage& m = mat; return CV_IS_IMAGE(&m) ? cvCloneImage(&m) : NULL; }
    operator cv::Mat&()  { return mat; }
    operator cv::Mat*()  { return pointer ? &mat : 0; }

    CvArr* pointer;
    int size;
    cv::Mat mat2;
    cv::Mat& mat;
};

class ArrayAdapter {
public:
    template<class T> ArrayAdapter(const T* pointer, int size) : pointer((T*)pointer), size(size),
            mat(pointer ? std::vector<T>((T*)pointer, (T*)pointer + size) : std::vector<T>(), true), arr2(mat), arr(arr2) { }
    ArrayAdapter(const cv::_OutputArray& arr) : pointer(0), size(0), arr((cv::_OutputArray&)arr) { }
    static void deallocate(CvArr* pointer) {
        if (CV_IS_MAT(pointer)) {
            cvReleaseMat  ((CvMat**)   &pointer);
        } else if (CV_IS_MATND(pointer)) {
            cvReleaseMatND((CvMatND**) &pointer);
        } else if (CV_IS_IMAGE(pointer)) {
            cvReleaseImage((IplImage**)&pointer);
        } else {
            free(pointer);
        }
    }
    template<class T> operator T*() {
        if (mat.total() > size) {
            pointer = malloc(sizeof(T) * mat.total());
        }
        if (pointer) {
            std::copy(mat.begin<T>(), mat.end<T>(), (T*)pointer);
        }
        size = mat.total();
        return (T*)pointer;
    }
    operator CvMat*()    { const CvMat&    m = arr.getMatRef(); return CV_IS_MAT  (&m) ? cvCloneMat  (&m) : NULL; }
    operator CvMatND*()  { const CvMatND&  m = arr.getMatRef(); return CV_IS_MATND(&m) ? cvCloneMatND(&m) : NULL; }
    operator IplImage*() { const IplImage& m = arr.getMatRef(); return CV_IS_IMAGE(&m) ? cvCloneImage(&m) : NULL; }
    operator cv::_OutputArray&()  { return arr; }
    operator cv::_OutputArray*()  { return pointer ? &arr : 0; }

    CvArr* pointer;
    size_t size;
    cv::Mat mat;
    cv::_OutputArray arr2;
    cv::_OutputArray& arr;
};

template<> ArrayAdapter::ArrayAdapter(const CvArr* pointer, int size) : pointer((CvArr*)pointer),
        size(size), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const CvMat* pointer, int size) : pointer((CvMat*)pointer),
        size(size), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const CvMatND* pointer, int size) : pointer((CvMatND*)pointer),
        size(size), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(const IplImage* pointer, int size) : pointer((IplImage*)pointer),
        size(size), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }

class RNGAdapter {
public:
    RNGAdapter(const CvRNG* pointer, int size) :
        pointer((CvRNG*)pointer), size(size), rng2(*pointer), rng(rng2) { }
    RNGAdapter(const cv::RNG &rng) :
        pointer(new CvRNG(rng.state)), size(0), rng((cv::RNG&)rng) { }
    RNGAdapter(const cv::RNG *rng) :
        pointer(new CvRNG(rng->state)), size(0), rng(*(cv::RNG*)rng) { }
    static void deallocate(CvRNG* pointer) { delete pointer; }
    operator CvRNG*() { *pointer = rng.state; return pointer; }
    operator cv::RNG&() { return  rng; }
    operator cv::RNG*() { return &rng; }

    CvRNG* pointer;
    int size;
    cv::RNG rng2;
    cv::RNG& rng;
};

class FileStorageAdapter {
public:
    FileStorageAdapter(const CvFileStorage* pointer, int size) :
        size(size), fs2((CvFileStorage*)pointer), fs(fs2) { fs.fs.addref(); }
    FileStorageAdapter(const cv::FileStorage &fs) :
        size(0), fs((cv::FileStorage&)fs) { }
    static void deallocate(CvFileStorage* pointer) { }
    operator CvFileStorage*()   { return fs.fs; }
    operator cv::FileStorage&() { return fs; }
    operator cv::FileStorage*() { return &fs; }

    int size;
    cv::FileStorage fs2;
    cv::FileStorage& fs;
};

class FileNodeAdapter {
public:
    FileNodeAdapter(const CvFileStorage* pointer, int size, CvFileNode* pointer2, int size2) :
        size(size), size2(size2), fn2(pointer, pointer2), fn(fn2) { }
    FileNodeAdapter(const cv::FileNode& fn) :
        size(0), size2(0), fn((cv::FileNode&)fn) { }
    static void deallocate(CvFileStorage* pointer, CvFileNode* pointer2) { }
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
    RectAdapter(const CvRect* pointer, int size) : pointer((CvRect*)pointer), size(size),
            rect2(pointer ? cv::Rect(*pointer) : cv::Rect()), rect(rect2) { }
    RectAdapter(const cv::Rect& rect) :
        pointer(new CvRect), size(0), rect((cv::Rect&)rect) { }
    static void deallocate(CvRect* pointer) { delete pointer; }
    operator CvRect*()   { if (pointer) { *pointer = rect; } return pointer; }
    operator cv::Rect&() { return rect; }
    operator cv::Rect*() { return pointer ? &rect : 0; }

    CvRect* pointer;
    int size;
    cv::Rect rect2;
    cv::Rect& rect;
};

class Point2dAdapter {
public:
    Point2dAdapter(const CvPoint2D64f* pointer, int size) : pointer((CvPoint2D64f*)pointer), size(size),
            point2d2(pointer ? cv::Point2d(pointer->x, pointer->y) : cv::Point2d()), point2d(point2d2) { }
    Point2dAdapter(const cv::Point2d& point2d) :
        pointer(new CvPoint2D64f), size(0), point2d((cv::Point2d&)point2d) { }
    static void deallocate(CvPoint2D64f* pointer) { delete pointer; }
    operator CvPoint2D64f*(){ if (pointer) { pointer->x = point2d.x; pointer->y = point2d.y; } return pointer; }
    operator cv::Point2d&() { return point2d; }
    operator cv::Point2d*() { return pointer ? &point2d : 0; }

    CvPoint2D64f* pointer;
    int size;
    cv::Point2d point2d2;
    cv::Point2d& point2d;
};
