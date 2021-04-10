/**
 * Copyright 2021 JavaCV
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

/**
 * OpenCV similarity measurement examples:
 * https://docs.opencv.org/master/d5/dc4/tutorial_video_input_psnr_ssim.html
 */
public class Similarity {
    private static double getPSNR(Mat I1, Mat I2) {
        Mat s1 = new Mat();
        opencv_core.absdiff(I1, I2, s1);             // |I1 - I2|
        s1.convertTo(s1, opencv_core.CV_32F);        // cannot make a square on 8 bits
        s1 = s1.mul(s1).asMat();                     // |I1 - I2|^2

        Scalar s = opencv_core.sumElems(s1);         // sum elements per channel

        double sse = s.get(0) + s.get(1) + s.get(2); // sum channels

        if (sse <= 1e-10) {                          // for small values return zero
            return 0;
        } else {
            double mse = sse / (double) (I1.channels() * I1.total());
            double psnr = 10.0 * Math.log10((255 * 255) / mse);
            return psnr;
        }
    }

    private static Scalar getMSSIM(Mat i1, Mat i2) {
        double C1 = 6.5025, C2 = 58.5225;
        /***************************** INITS **********************************/
        int d = opencv_core.CV_32F;
        Mat I1 = new Mat();
        Mat I2 = new Mat();
        i1.convertTo(I1, d);                  // cannot calculate on one byte large values
        i2.convertTo(I2, d);
        Mat I2_2 = I2.mul(I2).asMat();        // I2^2
        Mat I1_2 = I1.mul(I1).asMat();        // I1^2
        Mat I1_I2 = I1.mul(I2).asMat();       // I1 * I2
        /*************************** END INITS **********************************/
        // PRELIMINARY COMPUTING
        Mat mu1 = new Mat();
        Mat mu2 = new Mat();
        opencv_imgproc.GaussianBlur(I1, mu1, new Size(11, 11), 1.5);
        opencv_imgproc.GaussianBlur(I2, mu2, new Size(11, 11), 1.5);
        Mat mu1_2 = mu1.mul(mu1).asMat();
        Mat mu2_2 = mu2.mul(mu2).asMat();
        Mat mu1_mu2 = mu1.mul(mu2).asMat();
        Mat sigma1_2 = new Mat();
        Mat sigma2_2 = new Mat();
        Mat sigma12 = new Mat();
        opencv_imgproc.GaussianBlur(I1_2, sigma1_2, new Size(11, 11), 1.5);
        sigma1_2 = opencv_core.subtract(sigma1_2, mu1_2).asMat();
        opencv_imgproc.GaussianBlur(I2_2, sigma2_2, new Size(11, 11), 1.5);
        sigma2_2 = opencv_core.subtract(sigma2_2, mu2_2).asMat();
        opencv_imgproc.GaussianBlur(I1_I2, sigma12, new Size(11, 11), 1.5);
        sigma12 = opencv_core.subtract(sigma12, mu1_mu2).asMat();
        Mat t1, t2, t3;
        t1 = opencv_core.add(opencv_core.multiply(2, mu1_mu2), Scalar.all(C1)).asMat();
        t2 = opencv_core.add(opencv_core.multiply(2, sigma12), Scalar.all(C2)).asMat();
        t3 = t1.mul(t2).asMat();                     // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))
        t1 = opencv_core.add(opencv_core.add(mu1_2, mu2_2), Scalar.all(C1)).asMat();
        t2 = opencv_core.add(opencv_core.add(sigma1_2, sigma2_2), Scalar.all(C2)).asMat();
        t1 = t1.mul(t2).asMat();                     // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))
        Mat ssim_map = new Mat();
        opencv_core.divide(t3, t1, ssim_map);        // ssim_map =  t3./t1;
        Scalar mssim = opencv_core.mean(ssim_map);   // mssim = average of ssim map
        return mssim;
    }

    public static void main(String[] args) {
        Mat img1 = opencv_imgcodecs.imread("face.jpg");
        Mat img2 = img1.clone();
        opencv_imgproc.GaussianBlur(img2, img2, new Size(15, 15), 10);
        double psnr = getPSNR(img1, img2);
        Scalar mssim = getMSSIM(img1, img2);
        System.out.println("PSNR: " + psnr);
        System.out.printf("SSIM: %f, %f, %f\n", mssim.get(0), mssim.get(1), mssim.get(2));
    }
}