import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_features2d.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_features2d.*;

/**
 * (De)serialize OpenCV structures using XML to files and memory
 * <p>
 * Created by Maurice Betzel on 24.11.2017.
 */


public class OpenCVFeatures2dSerialization {

    public static void main(String[] args) throws IOException {
        String imageFile = (args.length > 0) ? args[0] : "Blob3.jpg";
        BufferedImage bufferedImage = ImageIO.read(new File(imageFile));
        try (Mat matrix = new OpenCVFrameConverter.ToMat().convert(new Java2DFrameConverter().convert(bufferedImage))
        ) {
            String fileName = "serialized.xml";
            serializeFile(matrix, fileName);
            deserializeFile(fileName);

            String serialized = serializeMemory(matrix);
            System.out.println(serialized);
            deserializeMemory(serialized);
        }
    }

    private static void serializeFile(Mat matrix, String fileName) throws UnsupportedEncodingException {
        try (KeyPointVector keyPointVectorSerialize = new KeyPointVector(); Mat objectDescriptorsSerialize = new Mat(); AKAZE akaze = AKAZE.create();
             FileStorage fileStorage = new FileStorage(fileName, FileStorage.WRITE, StandardCharsets.UTF_8.name())
        ) {
            akaze.detectAndCompute(matrix, new Mat(), keyPointVectorSerialize, objectDescriptorsSerialize, false);
            System.out.println("Vector size: " + keyPointVectorSerialize.size());
            System.out.println("Descriptor size: " + objectDescriptorsSerialize.cols());
            write(fileStorage, "keyPoints", keyPointVectorSerialize);
            write(fileStorage, "descriptors", objectDescriptorsSerialize);
            fileStorage.release();
        }
    }

    private static void deserializeFile(String file) {
        try (KeyPointVector keyPointVectorDeserialize = new KeyPointVector(); Mat objectDescriptorsDeserialize = new Mat();
             FileStorage fileStorage = new FileStorage(file, FileStorage.READ, StandardCharsets.UTF_8.name());
             FileNode keyPointsFileNode = fileStorage.get("keyPoints"); FileNode descriptorsFileNode = fileStorage.get("descriptors")
        ) {
            read(keyPointsFileNode, keyPointVectorDeserialize);
            read(descriptorsFileNode, objectDescriptorsDeserialize);
            System.out.println("Vector size: " + keyPointVectorDeserialize.size());
            System.out.println("Descriptor size: " + objectDescriptorsDeserialize.cols());
            fileStorage.release();
        }
    }

    private static String serializeMemory(Mat matrix) throws UnsupportedEncodingException {
        try (KeyPointVector keyPointVectorSerialize = new KeyPointVector(); Mat objectDescriptorsSerialize = new Mat(); AKAZE akaze = AKAZE.create();
             FileStorage fileStorage = new FileStorage(".xml", FileStorage.WRITE | FileStorage.MEMORY, StandardCharsets.UTF_8.name())
        ) {
            akaze.detectAndCompute(matrix, new Mat(), keyPointVectorSerialize, objectDescriptorsSerialize, false);
            System.out.println("Vector size: " + keyPointVectorSerialize.size());
            System.out.println("Descriptor size: " + objectDescriptorsSerialize.cols());
            write(fileStorage, "keyPoints", keyPointVectorSerialize);
            write(fileStorage, "descriptors", objectDescriptorsSerialize);
            BytePointer bytePointer = fileStorage.releaseAndGetString();
            return bytePointer.getString(StandardCharsets.UTF_8.name());
        }
    }

    private static void deserializeMemory(String serialized) {
        try (KeyPointVector keyPointVectorDeserialize = new KeyPointVector(); Mat objectDescriptorsDeserialize = new Mat();
             FileStorage fileStorage = new FileStorage(serialized, FileStorage.READ | FileStorage.MEMORY, StandardCharsets.UTF_8.name());
             FileNode keyPointsFileNode = fileStorage.get("keyPoints"); FileNode descriptorsFileNode = fileStorage.get("descriptors")
        ) {
            read(keyPointsFileNode, keyPointVectorDeserialize);
            read(descriptorsFileNode, objectDescriptorsDeserialize);
            System.out.println("Vector size: " + keyPointVectorDeserialize.size());
            System.out.println("Descriptor size: " + objectDescriptorsDeserialize.cols());
            fileStorage.release();
        }
    }

}
