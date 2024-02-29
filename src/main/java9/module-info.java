module org.bytedeco.javacv {
    exports org.bytedeco.javacv;
    requires java.desktop;
    requires javafx.graphics;
    requires transitive org.bytedeco.javacpp;
    requires org.bytedeco.opencv;
    requires org.bytedeco.ffmpeg;
    requires org.bytedeco.flycapture;
    requires org.bytedeco.libdc1394;
    requires org.bytedeco.libfreenect;
    requires org.bytedeco.libfreenect2;
    requires org.bytedeco.librealsense;
    requires org.bytedeco.librealsense2;
    requires org.bytedeco.videoinput;
    requires org.bytedeco.artoolkitplus;
//    requires org.bytedeco.flandmark;
    requires org.bytedeco.leptonica;
    requires org.bytedeco.tesseract;
}
