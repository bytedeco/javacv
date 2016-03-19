/*
 * JavaCV version of OpenCV caffe_googlenet.cpp
 * https://github.com/ludv1x/opencv_contrib/blob/master/modules/dnn/samples/caffe_googlenet.cpp
 *
 * Paolo Bolettieri <paolo.bolettieri@gmail.com>
 */

import static org.bytedeco.javacpp.opencv_core.minMaxLoc;
import static org.bytedeco.javacpp.opencv_dnn.createCaffeImporter;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_dnn.Blob;
import org.bytedeco.javacpp.opencv_dnn.Importer;
import org.bytedeco.javacpp.opencv_dnn.Net;

public class CaffeGooglenet {

    /* Find best class for the blob (i. e. class with maximal probability) */
    public static void getMaxClass(Blob probBlob, Point classId, double[] classProb) {
        Mat probMat =probBlob.matRefConst().reshape(1, 1); //reshape the blob to 1x1000 matrix
        minMaxLoc(probMat, null, classProb, null, classId, null);
    }

    public static List<String> readClassNames() {
        String filename = "synset_words.txt";
        List<String> classNames = null;

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            classNames = new ArrayList<String>();
            String name = null;
            while ((name = br.readLine()) != null) {
                classNames.add(name.substring(name.indexOf(' ')+1));
            }
        } catch (IOException ex) {
            System.err.println("File with classes labels not found " + filename);
            System.exit(-1);
        }
        return classNames;
    }

    public static void main(String[] args) throws Exception {
        String modelTxt = "bvlc_googlenet.prototxt";
        String modelBin = "bvlc_googlenet.caffemodel";
        String imageFile = (args.length > 0) ? args[0] : "space_shuttle.jpg";

        //! [Create the importer of Caffe model]
        Importer importer = null;
        try {                                 //Try to import Caffe GoogleNet model
            importer = createCaffeImporter(modelTxt, modelBin);
        } catch (Exception e) {               //Importer can throw errors, we will catch them
            e.printStackTrace();
        }
        //! [Create the importer of Caffe model]

        if (importer == null) {
            System.err.println("Can't load network by using the following files: ");
            System.err.println("prototxt:   " + modelTxt);
            System.err.println("caffemodel: " + modelBin);
            System.err.println("bvlc_googlenet.caffemodel can be downloaded here:");
            System.err.println("http://dl.caffe.berkeleyvision.org/bvlc_googlenet.caffemodel");
            System.exit(-1);
        }

        //! [Initialize network]
        Net net = new Net();
        importer.populateNet(net);
        importer.close();                     //We don't need importer anymore
        //! [Initialize network]

        //! [Prepare blob]
        Mat img = imread(imageFile);

        if (img.empty()) {
            System.err.println("Can't read image from the file: " + imageFile);
            System.exit(-1);
        }

        resize(img, img, new Size(224, 224)); //GoogLeNet accepts only 224x224 RGB-images
        Blob inputBlob = new Blob(img);       //Convert Mat to dnn::Blob image batch
        //! [Prepare blob]

        //! [Set input blob]
        net.setBlob(".data", inputBlob);      //set the network input
        //! [Set input blob]

        //! [Make forward pass]
        net.forward();                        //compute output
        //! [Make forward pass]

        //! [Gather output]
        Blob prob = net.getBlob("prob");      //gather output of "prob" layer

        Point classId = new Point();
        double[] classProb = new double[1];
        getMaxClass(prob, classId, classProb);//find the best class
        //! [Gather output]

        //! [Print results]
        List<String> classNames = readClassNames();

        System.out.println("Best class: #" + classId.x() + " '" + classNames.get(classId.x()) + "'");
        System.out.println("Best class: #" + classId.x());
        System.out.println("Probability: " + classProb[0] * 100 + "%");
        //! [Print results]
    } //main
}
