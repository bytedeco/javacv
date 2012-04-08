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
#define _WIN32_WINNT 0x0502
#include <windows.h>
#endif
static inline void SetLibraryPath(const char *path) {
#ifdef _WIN32
    SetDllDirectory(path);
#endif
}

class MatAdapter {
public:
    MatAdapter(CvArr* pointer, int capacity) : pointer(pointer), capacity(capacity),
            mat2(cv::cvarrToMat(pointer)), mat(mat2) { }
    MatAdapter(const cv::Mat& mat) : pointer(0), capacity(0), mat((cv::Mat&)mat) { }
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
    int capacity;
    cv::Mat mat2;
    cv::Mat& mat;
};

class ArrayAdapter {
public:
    template<class T> ArrayAdapter(T* pointer, int capacity) : pointer(pointer), capacity(capacity),
            mat(pointer ? std::vector<T>(pointer, pointer + capacity) : std::vector<T>(), true), arr2(mat), arr(arr2) { }
    ArrayAdapter(const cv::_OutputArray& arr) : pointer(0), capacity(0), arr((cv::_OutputArray&)arr) { }
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
        if (mat.total() > capacity) {
            pointer = malloc(sizeof(T) * mat.total());
            capacity = mat.total();
        }
        if (pointer) {
            std::copy(mat.begin<T>(), mat.end<T>(), (T*)pointer);
        }
        return (T*)pointer;
    }
    operator CvMat*()    { const CvMat&    m = arr.getMatRef(); return CV_IS_MAT  (&m) ? cvCloneMat  (&m) : NULL; }
    operator CvMatND*()  { const CvMatND&  m = arr.getMatRef(); return CV_IS_MATND(&m) ? cvCloneMatND(&m) : NULL; }
    operator IplImage*() { const IplImage& m = arr.getMatRef(); return CV_IS_IMAGE(&m) ? cvCloneImage(&m) : NULL; }
    operator cv::_OutputArray&()  { return arr; }
    operator cv::_OutputArray*()  { return pointer ? &arr : 0; }

    CvArr* pointer;
    size_t capacity;
    cv::Mat mat;
    cv::_OutputArray arr2;
    cv::_OutputArray& arr;
};

template<> ArrayAdapter::ArrayAdapter(CvArr* pointer, int capacity) : pointer(pointer),
        capacity(capacity), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(CvMat* pointer, int capacity) : pointer(pointer),
        capacity(capacity), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(CvMatND* pointer, int capacity) : pointer(pointer),
        capacity(capacity), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }
template<> ArrayAdapter::ArrayAdapter(IplImage* pointer, int capacity) : pointer(pointer),
        capacity(capacity), mat(cv::cvarrToMat(pointer)), arr2(mat), arr(arr2) { }

class RNGAdapter {
public:
    RNGAdapter(CvRNG* pointer, int capacity) :
        pointer(pointer), capacity(capacity), rng2(*pointer), rng(rng2) { }
    RNGAdapter(const cv::RNG &rng) :
        pointer(new CvRNG(rng.state)), capacity(0), rng((cv::RNG&)rng) { }
    RNGAdapter(const cv::RNG *rng) :
        pointer(new CvRNG(rng->state)), capacity(0), rng(*(cv::RNG*)rng) { }
    static void deallocate(CvRNG* pointer) { delete pointer; }
    operator CvRNG*() { *pointer = rng.state; return pointer; }
    operator cv::RNG&() { return  rng; }
    operator cv::RNG*() { return &rng; }

    CvRNG* pointer;
    int capacity;
    cv::RNG rng2;
    cv::RNG& rng;
};

class FileStorageAdapter {
public:
    FileStorageAdapter(CvFileStorage* pointer, int capacity) :
        capacity(capacity), fs2(pointer), fs(fs2) { fs.fs.addref(); }
    FileStorageAdapter(const cv::FileStorage &fs) :
        capacity(0), fs((cv::FileStorage&)fs) { }
    static void deallocate(CvFileStorage* pointer) { }
    operator CvFileStorage*()   { return fs.fs; }
    operator cv::FileStorage&() { return fs; }
    operator cv::FileStorage*() { return &fs; }

    int capacity;
    cv::FileStorage fs2;
    cv::FileStorage& fs;
};

class FileNodeAdapter {
public:
    FileNodeAdapter(CvFileStorage* pointer, int capacity, CvFileNode* pointer2, int capacity2) :
        capacity(capacity), capacity2(capacity2), fn2(pointer, pointer2), fn(fn2) { }
    FileNodeAdapter(const cv::FileNode& fn) :
        capacity(0), capacity2(0), fn((cv::FileNode&)fn) { }
    static void deallocate(CvFileStorage* pointer, CvFileNode* pointer2) { }
    operator CvFileStorage*() { return (CvFileStorage*)fn.fs; }
    operator CvFileNode*()    { return (CvFileNode*)   fn.node; }
    operator cv::FileNode&()  { return  fn; }
    operator cv::FileNode*()  { return &fn; }

    int capacity, capacity2;
    cv::FileNode fn2;
    cv::FileNode& fn;
};

class RectAdapter {
public:
    RectAdapter(CvRect* pointer, int capacity) : pointer(pointer), capacity(capacity),
            rect2(pointer ? cv::Rect(*pointer) : cv::Rect()), rect(rect2) { }
    RectAdapter(const cv::Rect& rect) :
        pointer(new CvRect), capacity(0), rect((cv::Rect&)rect) { }
    static void deallocate(CvRect* pointer) { delete pointer; }
    operator CvRect*()   { if (pointer) { *pointer = rect; } return pointer; }
    operator cv::Rect&() { return rect; }
    operator cv::Rect*() { return pointer ? &rect : 0; }

    CvRect* pointer;
    int capacity;
    cv::Rect rect2;
    cv::Rect& rect;
};

class Point2dAdapter {
public:
    Point2dAdapter(CvPoint2D64f* pointer, int capacity) : pointer(pointer), capacity(capacity),
            point2d2(pointer ? cv::Point2d(pointer->x, pointer->y) : cv::Point2d()), point2d(point2d2) { }
    Point2dAdapter(const cv::Point2d& point2d) :
        pointer(new CvPoint2D64f), capacity(0), point2d((cv::Point2d&)point2d) { }
    static void deallocate(CvPoint2D64f* pointer) { delete pointer; }
    operator CvPoint2D64f*(){ if (pointer) { pointer->x = point2d.x; pointer->y = point2d.y; } return pointer; }
    operator cv::Point2d&() { return point2d; }
    operator cv::Point2d*() { return pointer ? &point2d : 0; }

    CvPoint2D64f* pointer;
    int capacity;
    cv::Point2d point2d2;
    cv::Point2d& point2d;
};
