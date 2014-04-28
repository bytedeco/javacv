import org.bytedeco.javacv.Blobs;
import org.bytedeco.javacv.CanvasFrame;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

///////////////////////////////////////////////////////////////////
//*                                                             *//
//* As the author of this code, I place all of this code into   *//
//* the public domain. Users can use it for any legal purpose.  *//
//*                                                             *//
//*             - Dave Grossman                                 *//
//*                                                             *//
///////////////////////////////////////////////////////////////////
public class BlobDemo
{
    public static void main(String[] args)
    {
        System.out.println("STARTING...\n");
        demo();
        System.out.println("ALL DONE");
    }

    public static void demo()
    {
        int MinArea = 6;
        int ErodeCount =0;
        int DilateCount = 0;
        
        IplImage RawImage = null;

        // Read an image.
        for(int k = 0; k < 7; k++)
        {
            if(k == 0) { RawImage = cvLoadImage("BlackBalls.jpg"); MinArea = 250; ErodeCount = 0; DilateCount = 1; }
            else if(k == 1) { RawImage = cvLoadImage("Shapes1.jpg"); MinArea = 6; ErodeCount = 0; DilateCount = 1; }
            else if(k == 2) { RawImage = cvLoadImage("Shapes2.jpg"); MinArea = 250; ErodeCount = 0; DilateCount = 1; }
            else if(k == 3) { RawImage = cvLoadImage("Blob1.jpg"); MinArea = 2800; ErodeCount = 1; DilateCount = 1; }
            else if(k == 4) { RawImage = cvLoadImage("Blob2.jpg"); MinArea = 2800; ErodeCount = 1; DilateCount = 1; }
            else if(k == 5) { RawImage = cvLoadImage("Blob3.jpg"); MinArea = 2800; ErodeCount = 1; DilateCount = 1; }
            else if(k == 6) { RawImage = cvLoadImage("Rice.jpg"); MinArea = 30; ErodeCount = 2; DilateCount = 1; }
            //ShowImage(RawImage, "RawImage", 512);
        
            IplImage GrayImage = cvCreateImage(cvGetSize(RawImage), IPL_DEPTH_8U, 1);     
            cvCvtColor(RawImage, GrayImage, CV_BGR2GRAY);
            //ShowImage(GrayImage, "GrayImage", 512);

            IplImage BWImage = cvCreateImage(cvGetSize(GrayImage), IPL_DEPTH_8U, 1); 
            cvThreshold(GrayImage, BWImage, 127, 255, CV_THRESH_BINARY);
            //ShowImage(BWImage, "BWImage");
            
            IplImage WorkingImage = cvCreateImage(cvGetSize(BWImage), IPL_DEPTH_8U, 1);     
            cvErode(BWImage, WorkingImage, null, ErodeCount);    
            cvDilate(WorkingImage, WorkingImage, null, DilateCount);
            //ShowImage(WorkingImage, "WorkingImage", 512);
        
            //cvSaveImage("Working.jpg", WorkingImage);
            //PrintGrayImage(WorkingImage, "WorkingImage");
            //BinaryHistogram(WorkingImage);
        
            Blobs Regions = new Blobs();
            Regions.BlobAnalysis(
                    WorkingImage,               // image
                    -1, -1,                     // ROI start col, row
                    -1, -1,                     // ROI cols, rows
                    1,                          // border (0 = black; 1 = white)
                    MinArea);                   // minarea
            Regions.PrintRegionData();

            for(int i = 1; i <= Blobs.MaxLabel; i++)
            {
                double [] Region = Blobs.RegionData[i];
                int Parent = (int) Region[Blobs.BLOBPARENT];
                int Color = (int) Region[Blobs.BLOBCOLOR];
                int MinX = (int) Region[Blobs.BLOBMINX];
                int MaxX = (int) Region[Blobs.BLOBMAXX];
                int MinY = (int) Region[Blobs.BLOBMINY];
                int MaxY = (int) Region[Blobs.BLOBMAXY];
                Highlight(RawImage,  MinX, MinY, MaxX, MaxY, 1);
            }
            
            ShowImage(RawImage, "RawImage", 512);

            cvReleaseImage(GrayImage); GrayImage = null;
            cvReleaseImage(BWImage); BWImage = null;
            cvReleaseImage(WorkingImage); WorkingImage = null;
        }
        cvReleaseImage(RawImage); RawImage = null;
    }

    // Versions with 2, 3, and 4 parms respectively
    public static void ShowImage(IplImage image, String caption)
    {
        CvMat mat = image.asCvMat();
        int width = mat.cols(); if(width < 1) width = 1;
        int height = mat.rows(); if(height < 1) height = 1;
        double aspect = 1.0 * width / height;
        if(height < 128) { height = 128; width = (int) ( height * aspect ); }
        if(width < 128) width = 128;
        height = (int) ( width / aspect );
        ShowImage(image, caption, width, height);
    }
    public static void ShowImage(IplImage image, String caption, int size)
    {
        if(size < 128) size = 128;
        CvMat mat = image.asCvMat();
        int width = mat.cols(); if(width < 1) width = 1;
        int height = mat.rows(); if(height < 1) height = 1;
        double aspect = 1.0 * width / height;
        if(height != size) { height = size; width = (int) ( height * aspect ); }
        if(width != size) width = size;
        height = (int) ( width / aspect );
        ShowImage(image, caption, width, height);
    }
    public static void ShowImage(IplImage image, String caption, int width, int height)
    {
        CanvasFrame canvas = new CanvasFrame(caption, 1);   // gamma=1
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        canvas.setCanvasSize(width, height);
        canvas.showImage(image);
    }
    
    public static void Highlight(IplImage image, int [] inVec)
    {
        Highlight(image, inVec[0], inVec[1], inVec[2], inVec[3], 1);
    }
    public static void Highlight(IplImage image, int [] inVec, int Thick)
    {
        Highlight(image, inVec[0], inVec[1], inVec[2], inVec[3], Thick);
    }
    public static void Highlight(IplImage image, int xMin, int yMin, int xMax, int yMax)
    {
        Highlight(image, xMin, yMin, xMax, yMax, 1);
    }
    public static void Highlight(IplImage image, int xMin, int yMin, int xMax, int yMax, int Thick)
    {
        CvPoint pt1 = cvPoint(xMin,yMin);
        CvPoint pt2 = cvPoint(xMax,yMax);
        CvScalar color = cvScalar(255,0,0,0);       // blue [green] [red]
        cvRectangle(image, pt1, pt2, color, Thick, 4, 0);
    }
    
    public static void PrintGrayImage(IplImage image, String caption)
    {
        int size = 512; // impractical to print anything larger
        CvMat mat = image.asCvMat();
        int cols = mat.cols(); if(cols < 1) cols = 1;
        int rows = mat.rows(); if(rows < 1) rows = 1;
        double aspect = 1.0 * cols / rows;
        if(rows > size) { rows = size; cols = (int) ( rows * aspect ); }
        if(cols > size) cols = size;
        rows = (int) ( cols / aspect );
        PrintGrayImage(image, caption, 0, cols, 0, rows);
    }
    public static void PrintGrayImage(IplImage image, String caption, int MinX, int MaxX, int MinY, int MaxY)
    {
        int size = 512; // impractical to print anything larger
        CvMat mat = image.asCvMat();
        int cols = mat.cols(); if(cols < 1) cols = 1;
        int rows = mat.rows(); if(rows < 1) rows = 1;
        
        if(MinX < 0) MinX = 0; if(MinX > cols) MinX = cols; 
        if(MaxX < 0) MaxX = 0; if(MaxX > cols) MaxX = cols; 
        if(MinY < 0) MinY = 0; if(MinY > rows) MinY = rows; 
        if(MaxY < 0) MaxY = 0; if(MaxY > rows) MaxY = rows; 
        
        System.out.println("\n" + caption);
        System.out.print("   +");
        for(int icol = MinX; icol < MaxX; icol++) System.out.print("-");
        System.out.println("+");
        
        for(int irow = MinY; irow < MaxY; irow++)
        {
            if(irow<10) System.out.print(" ");
            if(irow<100) System.out.print(" ");
            System.out.print(irow);
            System.out.print("|");
            for(int icol = MinX; icol < MaxX; icol++)
            {
                int val = (int) mat.get(irow,icol);
                String C = " ";
                if(val == 0) C = "*";
                System.out.print(C);
            }
            System.out.println("|");
        }
        System.out.print("   +");
        for(int icol = MinX; icol < MaxX; icol++) System.out.print("-");
        System.out.println("+");
    }

    public static void PrintImageProperties(IplImage image)
    {
        CvMat mat = image.asCvMat();
        int cols = mat.cols();
        int rows = mat.rows();
        int depth = mat.depth();
        System.out.println("ImageProperties for " + image + " : cols=" + cols + " rows=" + rows + " depth=" + depth);
    }
    
    public static float BinaryHistogram(IplImage image)
    {
        CvScalar Sum = cvSum(image);
        float WhitePixels = (float) ( Sum.getVal(0) / 255 );
        CvMat mat = image.asCvMat();
        float TotalPixels = mat.cols() * mat.rows();
        //float BlackPixels = TotalPixels - WhitePixels;
        return WhitePixels / TotalPixels;
    }
  
    // Counterclockwise small angle rotation by skewing - Does not stretch border pixels
    public static IplImage SkewGrayImage(IplImage Src, double angle)    // angle is in radians
    {
        //double radians = - Math.PI * angle / 360.0;   // Half because skew is horizontal and vertical
        double sin = - Math.sin(angle);
        double AbsSin = Math.abs(sin);
        
        int nChannels = Src.nChannels();
        if(nChannels != 1) 
        {
            System.out.println("ERROR: SkewGrayImage: Require 1 channel: nChannels=" + nChannels);
            System.exit(1);
        }
        
        CvMat SrcMat = Src.asCvMat();
        int SrcCols = SrcMat.cols();
        int SrcRows = SrcMat.rows();

        double WidthSkew = AbsSin * SrcRows; 
        double HeightSkew = AbsSin * SrcCols;
        
        int DstCols = (int) ( SrcCols + WidthSkew ); 
        int DstRows = (int) ( SrcRows + HeightSkew );
    
        CvMat DstMat = cvCreateMat(DstRows, DstCols, CV_8UC1);  // Type matches IPL_DEPTH_8U
        cvSetZero(DstMat);
        cvNot(DstMat, DstMat);
        
        for(int irow = 0; irow < DstRows; irow++)
        {
            int dcol = (int) ( WidthSkew * irow / SrcRows );
            for(int icol = 0; icol < DstCols; icol++)
            {
                int drow = (int) ( HeightSkew - HeightSkew * icol / SrcCols );
                int jrow = irow - drow;
                int jcol = icol - dcol;
                if(jrow < 0 || jcol < 0 || jrow >= SrcRows || jcol >= SrcCols) DstMat.put(irow, icol, 255);
                else DstMat.put(irow, icol, (int) SrcMat.get(jrow,jcol));
            }
        }
        
        IplImage Dst = cvCreateImage(cvSize(DstCols, DstRows), IPL_DEPTH_8U, 1);
        Dst = DstMat.asIplImage();
        return Dst;
    }
    
    public static IplImage TransposeImage(IplImage SrcImage)
    {
        CvMat mat = SrcImage.asCvMat();
        int cols = mat.cols();
        int rows = mat.rows();
        IplImage DstImage = cvCreateImage(cvSize(rows, cols), IPL_DEPTH_8U, 1);
        cvTranspose(SrcImage, DstImage);
        cvFlip(DstImage,DstImage,1);
        return DstImage;
    }
}

