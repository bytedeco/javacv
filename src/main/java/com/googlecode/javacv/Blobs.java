package com.googlecode.javacv;

import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

//***************************************************************//
//* Blob analysis package  Version3.0 3 Oct 2012                *//
//* - Version 1.0:  8 Aug 2003                                  *//
//* - Version 1.2:  3 Jan 2008                                  *//
//* - Version 1.3:  5 Jan 2008 Add BLOBCOLOR                    *//
//* - Version 1.4: 13 January 2008 Add ROI function             *//
//* - Version 1.5: 13 April 2008 Fix perimeter on Region 0      *//
//* - Version 1.6:  1 May 2008 Reduce size of working storage   *//
//* - Version 1.7:  2 May 2008 Speed up run code initialization *//
//* - Version 1.8:  4 May 2008 Fix bugs in perimeter & Reg 0    *//
//* - Version 2.0:  3 Jan 2009 Add labeling functionality       *//
//* - Version 3.0:  3 Oct 2012 Convert to Java                  *//
//* -   Eliminate labeling functionality (but it's still there) *//
//* -   Simplify (at slight expense of performance)             *//
//* -   Reduce to 4 connectivity                                *//
//*                                                             *//
//* Input: IplImage binary image                                *//
//* Output: attributes of each connected region                 *//
//* Internal data: labeled array (could easily be externalized) *//
//* Author: Dave Grossman                                       *//
//* Email: dgrossman2@gmail.com                                 *//
//* Acknowledgement: my code is based on an algorithm that was  *//
//* to the best of my knowledge originally developed by Gerry   *//
//* Agin of SRI around the year 1973. I have not been able to   *//
//* find any published references to his earlier work. I posted *//
//* early versions of my program to OpenCV, where they morphed  *//
//* eventually into cvBlobsLib.                                 *//
//*                                                             *//
//* As the author of this code, I place all of this code into   *//
//* the public domain. Users can use it for any legal purpose.  *//
//*                                                             *//
//*             - Dave Grossman                                 *//
//*                                                             *//
//* Typical calling sequence:                                   *//
//*     Blobs Blob = new Blobs();                               *//
//*     Blob.BlobAnalysis(                                      *//
//*         image3, // image                                    *//
//*         -1, -1, // ROI start col, row (-1 means full image) *//
//*         -1, -1, // ROI cols, rows                           *//
//*         0,      // border (0 = black; 1 = white)            *//
//*         20);    // minarea                                  *//
//*     Blob.PrintRegionData();                                 *//
//*     int BlobLabel = Blob.NextRegion(                        *//
//*         -1,     // parentcolor (-1 = ignore)                *//
//*         0,      // color (0 = black; 1 = white; -1 = ignore *//
//*         100,    // minarea                                  *//
//*         500,    // maxarea                                  *//
//*         15);    // starting label (default 0)               *//
//*                                                             *//
//* Ellipse properties can be derived from moments:             *//
//*     h = (XX + YY) / 2                                       *//
//*     Major axis = h + sqrt ( h^2 - XX * YY + XY^2)           *//
//*     Minor axis = h - sqrt ( h^2 - XX * YY2 + XY^2)          *//
//*     Eccentricity = (sqrt(abs(XX - YY)) + 4 * XY)/AREA       *//
//***************************************************************//

public class Blobs
{
    // The following parameters should be configured by the user:
    // On ScanSnap Manager, "Best" setting = 300dpi gray level
    // jpg compression is set to minimum so that quality is highest
    // Each page jpg image is then a little under 1 MB 
    static int BLOBROWCOUNT = 3500; // 11 inches * 8.5 inches standard page
    static int BLOBCOLCOUNT = 2700; // with some added cushion to be safe
    
    // Allow for vast number of blobs so there is no memory overrun
    static int BLOBTOTALCOUNT = (BLOBROWCOUNT + BLOBCOLCOUNT) * 5;

    //--------------------------------------------------------------
    // Do not change anything below this line
    public static int BLOBLABEL = 0;
    public static int BLOBPARENT = 1;
    public static int BLOBCOLOR = 2;
    public static int BLOBAREA = 3;
    public static int BLOBPERIMETER = 4;
    public static int BLOBSUMX = 5;
    public static int BLOBSUMY = 6;
    public static int BLOBSUMXX = 7;
    public static int BLOBSUMYY = 8;
    public static int BLOBSUMXY = 9;
    public static int BLOBMINX = 10;
    public static int BLOBMAXX = 11;
    public static int BLOBMINY = 12;
    public static int BLOBMAXY = 13;
    public static int BLOBDATACOUNT = 14; 

    public static int [][] LabelMat = new int [BLOBROWCOUNT][BLOBCOLCOUNT];
    public static double [][] RegionData = new double [BLOBTOTALCOUNT][BLOBDATACOUNT];
    public static int MaxLabel; 
    
    public int LabelA, LabelB, LabelC, LabelD;
    public int ColorA, ColorB, ColorC, ColorD;
    public int jrow, jcol;  // index within ROI
    public static int [] SubsumedLabel = new int [BLOBTOTALCOUNT];
    public static int [] CondensationMap = new int [BLOBTOTALCOUNT];
    
    // Print out all the data for all the regions (blobs)
    public void PrintRegionData() { PrintRegionData(0, MaxLabel); }
    public void PrintRegionData(int Label0, int Label1)
    {
        if(Label0 < 0) Label0 = 0;
        if(Label1 > MaxLabel) Label1 = MaxLabel;
        if(Label1 < Label0) return;
        for(int Label = Label0; Label <= Label1; Label++)
        {
            double [] Property = RegionData[Label];
            
            int ThisLabel = (int)Property[BLOBLABEL];
            int ThisParent = (int)Property[BLOBPARENT];
            int ThisColor = (int)Property[BLOBCOLOR];
            double ThisArea = Property[BLOBAREA];
            double ThisPerimeter = Property[BLOBPERIMETER];
            double ThisSumX = Property[BLOBSUMX];
            double ThisSumY = Property[BLOBSUMY];
            double ThisSumXX = Property[BLOBSUMXX];
            double ThisSumYY = Property[BLOBSUMYY];
            double ThisSumXY = Property[BLOBSUMXY];
            int ThisMinX = (int)Property[BLOBMINX];
            int ThisMaxX = (int)Property[BLOBMAXX];
            int ThisMinY = (int)Property[BLOBMINY];
            int ThisMaxY = (int)Property[BLOBMAXY];
            
            String Str1 = " " + Label + ": L[" + ThisLabel + "] P[" + ThisParent + "] C[" + ThisColor + "]";
            String Str2 = " AP[" + ThisArea + ", " + ThisPerimeter + "]";
            String Str3 = " M1[" + ThisSumX + ", " + ThisSumY + "] M2[" + ThisSumXX + ", " + ThisSumYY + ", " + ThisSumXY + "]";
            String Str4 = " MINMAX[" + ThisMinX + ", " + ThisMaxX + ", " + ThisMinY + ", " + ThisMaxY + "]";
            
            String Str = Str1 + Str2 + Str3 + Str4;
            System.out.println(Str);
        }
        System.out.println();
    }

    // Determine the next (higher number) region that meets the desired conditions
    public static int NextRegion(int Parent, int Color, double MinArea, double MaxArea, int Label)
    {
        double DParent = (double) Parent; 
        double DColor = (double) Color; if(DColor > 0) DColor = 1;
        
        int i;
        for(i = Label; i <= MaxLabel; i++)
        {
            double [] Region = RegionData[i];
            double ThisParent = Region[BLOBPARENT];
            double ThisColor = Region[BLOBCOLOR];
            if(DParent >= 0 && DParent != ThisParent) continue;
            if(DColor >= 0 && DColor != ThisColor) continue;
            if(Region[BLOBAREA] < MinArea || Region[BLOBAREA] > MaxArea) continue;  
            break;      // We have a match!
        }
        if(i > MaxLabel) i = -1;    // Use -1 to flag that there was no match
        return i;
    }

    // Determine the prior (lower number) region that meets the desired conditions
    public static int PriorRegion(int Parent, int Color, double MinArea, double MaxArea, int Label)
    {
        double DParent = (double) Parent; 
        double DColor = (double) Color; if(DColor > 0) DColor = 1;
        
        int i;
        for(i = Label; i >= 0; i--)
        {
            double [] Region = RegionData[i];
            double ThisParent = Region[BLOBPARENT];
            double ThisColor = Region[BLOBCOLOR];
            if(DParent >= 0 && DParent != ThisParent) continue;
            if(DColor >= 0 && DColor != ThisColor) continue;
            if(Region[BLOBAREA] < MinArea || Region[BLOBAREA] > MaxArea) continue;  
            break;      // We have a match!
        }
        if(i < 0) i = -1;   // Use -1 to flag that there was no match
        return i;
    }
    
    public void ResetRegion(int Label)
    {
        double [] RegionD = RegionData[Label];
        RegionD[BLOBLABEL] = 
        RegionD[BLOBPARENT] = 
        RegionD[BLOBCOLOR] =
        RegionD[BLOBAREA] =
        RegionD[BLOBPERIMETER] =
        RegionD[BLOBSUMX] =
        RegionD[BLOBSUMY] = 
        RegionD[BLOBSUMXX] = 
        RegionD[BLOBSUMYY] = 
        RegionD[BLOBSUMXY] = 
        RegionD[BLOBMINX] = 
        RegionD[BLOBMAXX] = 
        RegionD[BLOBMINY] = 
        RegionD[BLOBMAXY] = 0.0;
        System.arraycopy(RegionD,0,RegionData[Label],0,BLOBDATACOUNT);  // RegionData[Label] <- RegionD;
    }
    
    public void OldRegion(
            int NewLabelD,  // 3rd update this (may be the same as Label1 or Label2)
            int Label1,     // 1st increment this by 1 
            int Label2)     // 2nd increment this by 1
    {
        int DeltaPerimeter = 0;
        
        if(Label1 >= 0 && Label1 != NewLabelD)
        {
            DeltaPerimeter++;
            double [] Region1 = RegionData[Label1];
            Region1[BLOBPERIMETER]++;
            System.arraycopy(Region1,0,RegionData[Label1],0,BLOBDATACOUNT); // RegionData[Label1] <- Region1;
        }
        
        if(Label2 >= 0 && Label2 != NewLabelD)
        {
            DeltaPerimeter++;
            double [] Region2 = RegionData[Label2];
            Region2[BLOBPERIMETER]++;
            System.arraycopy(Region2,0,RegionData[Label2],0,BLOBDATACOUNT); // RegionData[Label2] <- Region2;
        }
        
        LabelD = NewLabelD;
        double [] RegionD = RegionData[LabelD];
        RegionD[BLOBLABEL] = LabelD;
        RegionD[BLOBPARENT] += 0.0;     // no change
        RegionD[BLOBCOLOR] += 0.0;      // no change
        RegionD[BLOBAREA] += 1.0;
        RegionD[BLOBPERIMETER] += DeltaPerimeter;
        RegionD[BLOBSUMX] += jcol;
        RegionD[BLOBSUMY] += jrow;
        RegionD[BLOBSUMXX] += jcol*jcol;
        RegionD[BLOBSUMYY] += jrow*jrow;
        RegionD[BLOBSUMXY] += jcol*jrow;
        RegionD[BLOBMINX] = Math.min(RegionD[BLOBMINX], jcol);
        RegionD[BLOBMAXX] = Math.max(RegionD[BLOBMAXX], jcol);
        RegionD[BLOBMINY] = Math.min(RegionD[BLOBMINY], jrow);
        RegionD[BLOBMAXY] = Math.max(RegionD[BLOBMAXY], jrow);
        System.arraycopy(RegionD,0,RegionData[LabelD],0,BLOBDATACOUNT); // RegionData[LabelD] <- RegionD;
   }
    
    public void NewRegion(int ParentLabel)
    {
        LabelD = ++MaxLabel;
        double [] RegionD = RegionData[LabelD];
        RegionD[BLOBLABEL] = LabelD;
        RegionD[BLOBPARENT] = (double) ParentLabel;
        RegionD[BLOBCOLOR] = ColorD;
        RegionD[BLOBAREA] = 1.0;
        RegionD[BLOBPERIMETER] = 2.0;
        RegionD[BLOBSUMX] = jcol;
        RegionD[BLOBSUMY] = jrow;
        RegionD[BLOBSUMXX] = jcol*jcol;
        RegionD[BLOBSUMYY] = jrow*jrow;
        RegionD[BLOBSUMXY] = jcol*jrow;
        RegionD[BLOBMINX] = jcol;
        RegionD[BLOBMAXX] = jcol;
        RegionD[BLOBMINY] = jrow;
        RegionD[BLOBMAXY] = jrow;

        System.arraycopy(RegionD,0,RegionData[LabelD],0,BLOBDATACOUNT); // RegionData[LabelD] <- RegionD;
        SubsumedLabel[LabelD] = -1;     // Flag label as not subsumed

        double [] RegionB = RegionData[LabelB];
        RegionB[BLOBPERIMETER]++;
        System.arraycopy(RegionB,0,RegionData[LabelB],0,BLOBDATACOUNT); // RegionData[LabelB] <- RegionB;
        
        double [] RegionC = RegionData[LabelC];
        RegionC[BLOBPERIMETER]++;

        System.arraycopy(RegionC,0,RegionData[LabelC],0,BLOBDATACOUNT); // RegionData[LabelC] <- RegionC;
    }
    
    public void Subsume(int GoodLabel, int BadLabel, int PSign) // Combine data with parent
    {
        LabelD = GoodLabel;
        double [] GoodRegion = RegionData[GoodLabel];   
        double [] BadRegion = RegionData[BadLabel];
    
        GoodRegion[BLOBLABEL] = GoodRegion[BLOBLABEL];      // no change
        GoodRegion[BLOBPARENT] = GoodRegion[BLOBPARENT];    // no change
        GoodRegion[BLOBCOLOR] = GoodRegion[BLOBCOLOR];      // no change
        GoodRegion[BLOBAREA] += BadRegion[BLOBAREA];
        GoodRegion[BLOBPERIMETER] += BadRegion[BLOBPERIMETER] * PSign;  // + external or - internal perimeter
        GoodRegion[BLOBSUMX] += BadRegion[BLOBSUMX];
        GoodRegion[BLOBSUMY] += BadRegion[BLOBSUMY];
        GoodRegion[BLOBSUMXX] += BadRegion[BLOBSUMXX];
        GoodRegion[BLOBSUMYY] += BadRegion[BLOBSUMYY];
        GoodRegion[BLOBSUMXY] += BadRegion[BLOBSUMXY];
        GoodRegion[BLOBMINX] = Math.min(GoodRegion[BLOBMINX], BadRegion[BLOBMINX]);
        GoodRegion[BLOBMAXX] = Math.max(GoodRegion[BLOBMAXX], BadRegion[BLOBMAXX]);
        GoodRegion[BLOBMINY] = Math.min(GoodRegion[BLOBMINY], BadRegion[BLOBMINY]);
        GoodRegion[BLOBMAXY] = Math.max(GoodRegion[BLOBMAXY], BadRegion[BLOBMAXY]);
        
        System.arraycopy(GoodRegion,0,RegionData[GoodLabel],0,BLOBDATACOUNT);   // RegionData[GoodLabel] <- GoodRegion;
    }

    public static int SubsumptionChain(int x) { return SubsumptionChain(x, 0); }
    public static int SubsumptionChain(int x, int Print)
    {
        String Str = "";
        if(Print > 0) Str = "Subsumption chain for " + x + ": ";
        int Lastx = x;
        while(x > -1)
        {
            Lastx = x;
            if(Print > 0) Str += " " + x;
            if(x == 0) break;
            x = SubsumedLabel[x];
        }
        if(Print > 0) System.out.println(Str);
        return Lastx;
    }

    //---------------------------------------------------------------------------------------
    // Main blob analysis routine
    //---------------------------------------------------------------------------------------
    // RegionData[0] is the border. It has Property[BLOBPARENT] = 0. 

    public int BlobAnalysis(IplImage Src,           // input image
                int Col0, int Row0,                 // start of ROI
                int Cols, int Rows,                 // size of ROI
                int Border,                         // border color (0 = black; 1 = white)
                int MinArea)                        // minimum region area
    {
        CvMat SrcMat = Src.asCvMat();
        int SrcCols = SrcMat.cols();
        int SrcRows = SrcMat.rows();
        
        if(Col0 < 0) Col0 = 0;
        if(Row0 < 0) Row0 = 0;
        if(Cols < 0) Cols = SrcCols;
        if(Rows < 0) Rows = SrcRows;
        if(Col0 + Cols > SrcCols) Cols = SrcCols - Col0;
        if(Row0 + Rows > SrcRows) Rows = SrcRows - Row0;

        if(Cols > BLOBCOLCOUNT || Rows > BLOBROWCOUNT )
        {
            System.out.println("Error in Class Blobs: Image too large: Edit Blobs.java");
            System.exit(666);
            return 0;
        }
        
        // Initialization
        int FillLabel = 0;
        int FillColor = 0; if(Border > 0) { FillColor = 1; }
        LabelA = LabelB = LabelC = LabelD = 0;
        ColorA = ColorB = ColorC = ColorD = FillColor;
        for(int k = 0; k < BLOBTOTALCOUNT; k++) SubsumedLabel[k] = -1;
        
        // Initialize border region
        MaxLabel = 0;
        double [] BorderRegion = RegionData[0];
        BorderRegion[BLOBLABEL] = 0.0;
        BorderRegion[BLOBPARENT] = -1.0;
        BorderRegion[BLOBAREA] = Rows + Cols + 4;   // Top, left, and 4 corners
        BorderRegion[BLOBCOLOR] = FillColor;
        BorderRegion[BLOBSUMX] = 0.5 * ( (2.0 + Cols) * (Cols - 1.0) ) - Rows - 1 ;
        BorderRegion[BLOBSUMY] = 0.5 * ( (2.0 + Rows) * (Rows - 1.0) ) - Cols - 1 ;
        BorderRegion[BLOBMINX] = -1;
        BorderRegion[BLOBMINY] = -1;
        BorderRegion[BLOBMAXX] = Cols + 1.0;
        BorderRegion[BLOBMAXY] = Rows + 1.0;
        System.arraycopy(BorderRegion,0,RegionData[0],0,BLOBDATACOUNT); // RegionData[0] <- BorderRegion;
        
        //  The cells are identified this way
        //          Last |AB|
        //          This |CD|
        //
        // With 4 connectivity, there are 8 possibilities for the cells:
        //                      No color transition     Color transition
        //          Case              1  2  3  4          5  6  7  8 
        //          Last Row        |pp|pp|pq|pq|       |pp|pp|pq|pq|   
        //          This Row        |pP|qQ|pP|qQ|       |pQ|qP|pQ|qP|
        //
        // Region numbers are p, q, r, x; where p<>q
        // Upper case letter is the current element at column=x row=y
        // Color is 0 or 1      (1 stands for 255 in the actual image)
        // Note that Case 4 is complicated because it joins two regions
        //--------------------------
        // Case 1: Colors A=B; C=D; A=C     
        // Case 2: Colors A=B; C=D; A<>C    
        // Case 3: Colors A<>B;C=D; A=C     
        // Case 4: Colors A<>B;C=D; A<>C    
        // Case 5: Colors A=B; C<>D; A=C    
        // Case 6: Colors A=B; C<>D; A<>C   
        // Case 7: Colors A<>B;C<>D; A=C    
        // Case 8: Colors A<>B;C<>D; A<>C   
        //--------------------------
                    
        // Loop over rows of ROI. irow = Row0 is 1st row of image; irow = Row0+Row is last row of image.
        for(int irow = Row0; irow < Row0+Rows; irow++)  // index within Src
        {
            jrow = irow - Row0; // index within ROI. 0 is first row. Rows is last row.
            
            // Loop over columns of ROI.
            for(int icol = Col0; icol < Col0+Cols; icol++)  // index within Src
            {
                jcol = icol - Col0; // index within ROI
 
                // initialize
                ColorA = ColorB = ColorC = FillColor;
                LabelA = LabelB = LabelC = LabelD = 0;
                ColorD = (int) SrcMat.get(jrow,jcol);       // fetch color of cell
            
                if(jrow == 0 || jcol == 0)  // first column or row
                {
                    if(jcol > 0)
                    {
                        ColorC = (int) SrcMat.get(jrow,jcol-1);
                        LabelC = LabelMat[jrow][jcol-1];
                    }
                    if(jrow > 0)
                    {
                        ColorB = (int) SrcMat.get(jrow-1,jcol);
                        LabelB = LabelMat[jrow-1][jcol];
                    }
                }
                else
                {
                    ColorA = (int) SrcMat.get(jrow-1,jcol-1); if(ColorA > 0) ColorA = 1;
                    ColorB = (int) SrcMat.get(jrow-1,jcol); if(ColorB > 0) ColorB = 1;
                    ColorC = (int) SrcMat.get(jrow,jcol-1); if(ColorC > 0) ColorC = 1;
                    LabelA = LabelMat[jrow-1][jcol-1];
                    LabelB = LabelMat[jrow-1][jcol];
                    LabelC = LabelMat[jrow][jcol-1];
                }   
                if(ColorA > 0) ColorA = 1;
                if(ColorB > 0) ColorB = 1;
                if(ColorC > 0) ColorC = 1;
                if(ColorD > 0) ColorD = 1;
                    
                // Determine Case
                int Case = 0;
                if(ColorA == ColorB)
                {
                    if(ColorC == ColorD) { if(ColorA == ColorC) Case = 1; else Case = 2; }
                    else { if(ColorA == ColorC) Case = 5; else Case = 6; }
                }
                else
                {
                    if(ColorC == ColorD) { if(ColorA == ColorC) Case = 3; else Case = 4; }
                    else { if(ColorA == ColorC) Case = 7; else Case = 8; }
                }

                // Take appropriate action
                if(Case == 1) { OldRegion(LabelC, -1, -1); }
                else if(Case == 2 || Case == 3) { OldRegion(LabelC, LabelB, LabelC); }
                else if(Case == 5 || Case == 8) // Isolated
                {
                    if((jrow == Rows || jcol == Cols) && ColorD == FillColor) { OldRegion(0, -1, -1); } // attached to border region 0
                    else NewRegion(LabelB);
                }
                else if(Case == 6 || Case == 7) { OldRegion(LabelB, LabelB, LabelC); }
                else            // Case 4 - The complicated situation
                {
                    int LabelBRoot = SubsumptionChain(LabelB); 
                    int LabelCRoot = SubsumptionChain(LabelC);
                    int LabelRoot = Math.min(LabelBRoot, LabelCRoot);
                    int LabelX;
                    if(LabelBRoot < LabelCRoot) { OldRegion(LabelB, -1, -1); LabelX = LabelC; }
                    else { OldRegion(LabelC, -1, -1); LabelX = LabelB; }
                    int NextLabelX = LabelX;
                    while(LabelRoot < LabelX)
                    {
                        NextLabelX = SubsumedLabel[LabelX];
                        SubsumedLabel[LabelX] = LabelRoot;
                        LabelX = NextLabelX;
                    }
                }
                    
                // Last column or row. Final corner was handled earlier in Cases 5 and 8.
                if((jrow == Rows || jcol == Cols) && ColorD == FillColor)
                {
                    if(jcol < Cols)         // bottom row   
                    {
                        if(ColorC != FillColor)     // Subsume B chain to border region 0
                        {
                            int LabelRoot = SubsumptionChain(LabelB);
                            SubsumedLabel[LabelRoot] = 0;
                        }
                    }
                    else if(jrow < Rows)    // right column
                    {
                        if(ColorB != FillColor)     // Subsume C chain to border region 0
                        {
                            int LabelRoot = SubsumptionChain(LabelC);
                            SubsumedLabel[LabelRoot] = 0;
                        }
                    }
                    OldRegion(0, -1, -1);   // attached to border region 0
                }

                LabelMat[jrow][jcol] = LabelD;
                    
            }
        }

        // Compute Condensation map
        int Offset = 0;
        for(int Label = 1; Label <= MaxLabel; Label++)
        {
            if(SubsumedLabel[Label] > -1) Offset++;
            CondensationMap[Label] = Label - Offset;
        }

        // Subsume regions that were flagged as connected; Perimeters add
        for(int Label = 1; Label <= MaxLabel; Label++)
        {
            int BetterLabel = SubsumptionChain(Label);
            if(BetterLabel != Label) Subsume(BetterLabel, Label, 1);
        }   

        // Condense subsumed regions
        int NewMaxLabel = 0;
        for(int OldLabel = 1; OldLabel <= MaxLabel; OldLabel++)
        {
            if(SubsumedLabel[OldLabel] < 0) // Renumber valid regions only
            {
                double [] OldRegion = RegionData[OldLabel];
                int OldParent = (int) OldRegion[BLOBPARENT];
                int NewLabel = CondensationMap[OldLabel];
                int NewParent = SubsumptionChain(OldParent);
                NewParent = CondensationMap[NewParent];
                OldRegion[BLOBLABEL] = (double) NewLabel;
                OldRegion[BLOBPARENT] = (double) NewParent;
                System.arraycopy(OldRegion,0,RegionData[NewLabel],0,BLOBDATACOUNT); //RegionData[NewLabel] <- ThisRegion;
                NewMaxLabel = NewLabel;
            }
        }
    
        // Zero out unneeded high labels
        for(int Label = NewMaxLabel+1; Label <= MaxLabel; Label++) ResetRegion(Label);
        MaxLabel = NewMaxLabel;
        
        // Flag for subsumption regions that have too small area
        for(int Label = MaxLabel; Label > 0; Label--)
        {
            double [] ThisRegion = RegionData[Label];
            int ThisArea = (int) ThisRegion[BLOBAREA];
            if(ThisArea < MinArea)
            {
                int ThisParent = (int) ThisRegion[BLOBPARENT];
                SubsumedLabel[Label] =  ThisParent;             // Flag this label as having been subsumed
            }
            else SubsumedLabel[Label] =  -1;
        }
        
        // Compute Condensation map
        Offset = 0;
        for(int Label = 1; Label <= MaxLabel; Label++)
        {
            if(SubsumedLabel[Label] > -1) Offset++;
            CondensationMap[Label] = Label - Offset;      
        }

        // Subsume regions that were flagged as enclosed; Perimeters subtract
        for(int Label = 1; Label <= MaxLabel; Label++)
        {
            int BetterLabel = SubsumptionChain(Label);
            if(BetterLabel != Label) Subsume(BetterLabel, Label, -1);
        }   
    
        // Condense subsumed regions
        for(int OldLabel = 1; OldLabel <= MaxLabel; OldLabel++)
        {
            if(SubsumedLabel[OldLabel] < 0) // Renumber valid regions only
            {
                double [] OldRegion = RegionData[OldLabel];
                int OldParent = (int) OldRegion[BLOBPARENT];
                int NewLabel = CondensationMap[OldLabel];
                int NewParent = SubsumptionChain(OldParent);
                NewParent = CondensationMap[NewParent];
                OldRegion[BLOBLABEL] = (double) NewLabel;
                OldRegion[BLOBPARENT] = (double) NewParent;
                System.arraycopy(OldRegion,0,RegionData[NewLabel],0,BLOBDATACOUNT); //RegionData[NewLabel] <- ThisRegion;
                NewMaxLabel = NewLabel;
            }
        }
        
        // Zero out unneeded high labels
        for(int Label = NewMaxLabel+1; Label <= MaxLabel; Label++) ResetRegion(Label);
        MaxLabel = NewMaxLabel;

        // Normalize summation fields into moments 
        for(int Label = 0; Label <= MaxLabel; Label++)
        {
            double [] ThisRegion = RegionData[Label];
            
            // Extract fields
            double Area = ThisRegion[BLOBAREA];
            double SumX = ThisRegion[BLOBSUMX];
            double SumY = ThisRegion[BLOBSUMY];
            double SumXX = ThisRegion[BLOBSUMXX];
            double SumYY = ThisRegion[BLOBSUMYY];
            double SumXY = ThisRegion[BLOBSUMXY];
            
            // Get averages
            SumX /= Area;
            SumY /= Area;
            SumXX /= Area;
            SumYY /= Area;
            SumXY /= Area;
            
            // Create moments
            SumXX -= SumX * SumX;
            SumYY -= SumY * SumY;
            SumXY -= SumX * SumY;
            if(SumXY > -1.0E-14 && SumXY < 1.0E-14) SumXY = (float) 0.0; // Eliminate roundoff error

            ThisRegion[BLOBSUMX] = SumX;
            ThisRegion[BLOBSUMY] = SumY;
            ThisRegion[BLOBSUMXX] = SumXX;
            ThisRegion[BLOBSUMYY] = SumYY;
            ThisRegion[BLOBSUMXY] = SumXY;

            System.arraycopy(ThisRegion,0,RegionData[Label],0,BLOBDATACOUNT);   // RegionData[Label] <- ThisRegion;
        }
    
        // Adjust border region
        BorderRegion = RegionData[0];
        BorderRegion[BLOBSUMXX] = BorderRegion[BLOBSUMYY] = BorderRegion[BLOBSUMXY] = 0;    // Mark invalid fields
        System.arraycopy(BorderRegion,0,RegionData[0],0,BLOBDATACOUNT); // RegionData[0] <- BorderRegion;
        
        return MaxLabel;
    }
    
    // Sort RegionData array on any column. (I couldn't figure out how to use the built-in java sort.)
    static double iField, jField;
    static double [] iProperty, jProperty;
    public static void SortRegions(int Col)
    {
        for(int i = 0; i < MaxLabel; i++)
        {
            for(int j = i+1; j <= Blobs.MaxLabel; j++)
            {
                iProperty = RegionData[i];
                jProperty = RegionData[j];
                iField = iProperty[Col];
                jField = jProperty[Col];
                if(iField > jField)
                {
                    RegionData[i] = jProperty;
                    RegionData[j] = iProperty;
                }
            }
        }
    }
}


