import java.awt.event.KeyEvent;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacv.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

/**
 * I was unable to find the OpenCV squares.c translated into JavaCV, so this
 * is a line-by-line translation of the C source.
 * The squares.c source used, found here:
 *      https://code.ros.org/trac/opencv/browser/trunk/opencv/samples/c/squares.c?rev=1429
 *
 * This is a demo class for finding squares/rectangles in an image, using JavaCV.
 *
 * All individual imports are kept as is; if you are like me,
 * you probably dislike the catch all .* import when trying to understand stuff.
 *
 * The major headache of the C code was figuring out the
 * "drawLines" method, and what parameters "cvPolyLine" is supposed to use.
 *
 * @author geir.ruud@digitalinferno.com
 */
public class Square {

    int thresh = 50;
    IplImage img = null;
    IplImage img0 = null;
    CvMemStorage storage = null;
    String wndname = "Square Detection Demo";

    // Java spesific
    CanvasFrame canvas = null;
    OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    // helper function:
    // finds a cosine of angle between vectors
    // from pt0->pt1 and from pt0->pt2
    double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
        double dx1 = pt1.x() - pt0.x();
        double dy1 = pt1.y() - pt0.y();
        double dx2 = pt2.x() - pt0.x();
        double dy2 = pt2.y() - pt0.y();

        return (dx1*dx2 + dy1*dy2) / Math.sqrt((dx1*dx1 + dy1*dy1) * (dx2*dx2 + dy2*dy2) + 1e-10);
    }

    // returns sequence of squares detected on the image.
    // the sequence is stored in the specified memory storage
    CvSeq findSquares4(IplImage img, CvMemStorage storage) {
        // Java translation: moved into loop
        // CvSeq contours = new CvSeq();
        int i, c, l, N = 11;
        CvSize sz = cvSize(img.width() & -2, img.height() & -2);
        IplImage timg = cvCloneImage(img); // make a copy of input image
        IplImage gray = cvCreateImage(sz, 8, 1);
        IplImage pyr = cvCreateImage(cvSize(sz.width()/2, sz.height()/2), 8, 3);
        IplImage tgray = null;
        // Java translation: moved into loop
        // CvSeq result = null;
        // double s = 0.0, t = 0.0;

        // create empty sequence that will contain points -
        // 4 points per square (the square's vertices)
        CvSeq squares = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvPoint.class), storage);

        // select the maximum ROI in the image
        // with the width and height divisible by 2
        cvSetImageROI(timg, cvRect(0, 0, sz.width(), sz.height()));

        // down-scale and upscale the image to filter out the noise
        cvPyrDown(timg, pyr, 7);
        cvPyrUp(pyr, timg, 7);
        tgray = cvCreateImage(sz, 8, 1);

        // find squares in every color plane of the image
        for (c = 0; c < 3; c++) {
            // extract the c-th color plane
            cvSetImageCOI(timg, c+1);
            cvCopy(timg, tgray);

            // try several threshold levels
            for (l = 0; l < N; l++) {
                // hack: use Canny instead of zero threshold level.
                // Canny helps to catch squares with gradient shading
                if (l == 0) {
                    // apply Canny. Take the upper threshold from slider
                    // and set the lower to 0 (which forces edges merging)
                    cvCanny(tgray, gray, 0, thresh, 5);
                    // dilate canny output to remove potential
                    // holes between edge segments
                    cvDilate(gray, gray, null, 1);
                } else {
                    // apply threshold if l!=0:
                    //     tgray(x,y) = gray(x,y) < (l+1)*255/N ? 255 : 0
                    cvThreshold(tgray, gray, (l+1)*255/N, 255, CV_THRESH_BINARY);
                }

                // find contours and store them all as a list
                // Java translation: moved into the loop
                CvSeq contours = new CvSeq();
                cvFindContours(gray, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));

                // test each contour
                while (contours != null && !contours.isNull()) {
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    // Java translation: moved into the loop
                    CvSeq result = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours)*0.02, 0);
                    // square contours should have 4 vertices after approximation
                    // relatively large area (to filter out noisy contours)
                    // and be convex.
                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
                    if(result.total() == 4 && Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) > 1000 && cvCheckContourConvexity(result) != 0) {

                        // Java translation: moved into loop
                        double s = 0.0, t = 0.0;

                        for( i = 0; i < 5; i++ ) {
                            // find minimum angle between joint
                            // edges (maximum of cosine)
                            if( i >= 2 ) {
                                //      Java translation:
                                //          Comment from the HoughLines.java sample code:
                                //          "    Based on JavaCPP, the equivalent of the C code:
                                //                  CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
                                //                  CvPoint first=line[0];
                                //                  CvPoint second=line[1];
                                //          is:
                                //                  Pointer line = cvGetSeqElem(lines, i);
                                //                  CvPoint first = new CvPoint(line).position(0);
                                //                  CvPoint second = new CvPoint(line).position(1);
                                //          "
                                //          ... so after some trial and error this seem to work
//                                t = fabs(angle(
//                                        (CvPoint*)cvGetSeqElem( result, i ),
//                                        (CvPoint*)cvGetSeqElem( result, i-2 ),
//                                        (CvPoint*)cvGetSeqElem( result, i-1 )));
                                t = Math.abs(angle(new CvPoint(cvGetSeqElem(result, i)),
                                        new CvPoint(cvGetSeqElem(result, i-2)),
                                        new CvPoint(cvGetSeqElem(result, i-1))));
                                s = s > t ? s : t;
                            }
                        }

                        // if cosines of all angles are small
                        // (all angles are ~90 degree) then write quandrange
                        // vertices to resultant sequence
                        if (s < 0.3)
                            for( i = 0; i < 4; i++ ) {
                                cvSeqPush(squares, cvGetSeqElem(result, i));
                            }
                    }

                    // take the next contour
                    contours = contours.h_next();
                }
            }
        }

        // release all the temporary images
        cvReleaseImage(gray);
        cvReleaseImage(pyr);
        cvReleaseImage(tgray);
        cvReleaseImage(timg);

        return squares;
    }

    // the function draws all the squares in the image
    void drawSquares(IplImage img, CvSeq squares) {

        //      Java translation: Here the code is somewhat different from the C version.
        //      I was unable to get straight forward CvPoint[] arrays
        //      working with "reader" and the "CV_READ_SEQ_ELEM".

//        CvSeqReader reader = new CvSeqReader();

        IplImage cpy = cvCloneImage(img);
        int i = 0;

        // Used by attempt 3
        // Create a "super"-slice, consisting of the entire sequence of squares
        CvSlice slice = new CvSlice(squares);

        // initialize reader of the sequence
//        cvStartReadSeq(squares, reader, 0);

         // read 4 sequence elements at a time (all vertices of a square)
         for(i = 0; i < squares.total(); i += 4) {

//              // Attempt 1:
//              // This does not work, uses the "reader"
//              CvPoint pt[] = new CvPoint[]{new CvPoint(1), new CvPoint(1), new CvPoint(1), new CvPoint(1)};
//              PointerPointer rect = new PointerPointer(pt);
//              int count[] = new int[]{4};
//
//              CV_READ_SEQ_ELEM(pt[0], reader);
//              CV_READ_SEQ_ELEM(pt[1], reader);
//              CV_READ_SEQ_ELEM(pt[2], reader);
//              CV_READ_SEQ_ELEM(pt[3], reader);

//              // Attempt 2:
//              // This works, somewhat similar to the C code, somewhat messy, does not use the "reader"
//              CvPoint pt[] = new CvPoint[]{
//                      new CvPoint(cvGetSeqElem(squares, i)),
//                      new CvPoint(cvGetSeqElem(squares, i + 1)),
//                      new CvPoint(cvGetSeqElem(squares, i + 2)),
//                      new CvPoint(cvGetSeqElem(squares, i + 3))};
//              PointerPointer rect = new PointerPointer(pt);
//              int count[] = new int[]{4};

              // Attempt 3:
              // This works, may be the "cleanest" solution, does not use the "reader"
             CvPoint rect = new CvPoint(4);
             IntPointer count = new IntPointer(1).put(4);
             // get the 4 corner slice from the "super"-slice
             cvCvtSeqToArray(squares, rect, slice.start_index(i).end_index(i + 4));

//             // Attempt 4:
//             // This works, and look the most like the original C code, uses the "reader"
//             CvPoint rect = new CvPoint(4);
//             int count[] = new int[]{4};
//
//             // read 4 vertices
//             CV_READ_SEQ_ELEM(rect.position(0), reader);
//             CV_READ_SEQ_ELEM(rect.position(1), reader);
//             CV_READ_SEQ_ELEM(rect.position(2), reader);
//             CV_READ_SEQ_ELEM(rect.position(3), reader);

             // draw the square as a closed polyline
             // Java translation: gotcha (re-)setting the opening "position" of the CvPoint sequence thing
             cvPolyLine(cpy, rect.position(0), count, 1, 1, CV_RGB(0,255,0), 3, CV_AA, 0);
         }

        // show the resultant image
        // cvShowImage(wndname, cpy);
        canvas.showImage(converter.convert(cpy));
        cvReleaseImage(cpy);
    }

    String names[] = new String[]{ "pic1.png", "pic2.png", "pic3.png",
                      "pic4.png", "pic5.png", "pic6.png" };

    public static void main(String args[]) throws Exception {
        new Square().main();
    }

    public void main() throws InterruptedException {
        // Java translation: c not used
        int i; // , c;
        // create memory storage that will contain all the dynamic data
        storage = cvCreateMemStorage(0);

        for(i = 0; i < names.length; i++) {
            // load i-th image

            // Java translation
            String filePathAndName = Square.class.getClassLoader().getResource(names[i]).getPath();
            filePathAndName = filePathAndName == null || filePathAndName.isEmpty() ? names[i] : filePathAndName;
            // img0 = cvLoadImage(names[i], 1);
            img0 = cvLoadImage(filePathAndName, 1);
            if (img0 == null) {
                System.err.println("Couldn't load " + names[i]);
                continue;
            }
            img = cvCloneImage(img0);

            // create window and a trackbar (slider) with parent "image" and set callback
            // (the slider regulates upper threshold, passed to Canny edge detector)
            // Java translation
            canvas = new CanvasFrame(wndname, 1);
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            // cvNamedWindow( wndname, 1 );

            // find and draw the squares
            drawSquares(img, findSquares4(img, storage));

            // wait for key.
            // Also the function cvWaitKey takes care of event processing
            // Java translation
            KeyEvent key = canvas.waitKey(0);
            // c = cvWaitKey(0);

            // release both images
            cvReleaseImage(img);
            cvReleaseImage(img0);
            // clear memory storage - reset free space position
            cvClearMemStorage(storage);

            if (key.getKeyCode() == 27) {
                break;
            }
        }
        // cvDestroyWindow( wndname );
        if (canvas != null) {
            canvas.dispose();
        }
    }

}
