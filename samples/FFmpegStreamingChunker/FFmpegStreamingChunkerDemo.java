package FFmpegStreamingChunker;

import java.io.File;

/**
 *
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class FFmpegStreamingChunkerDemo {

    public static void main(String[] args) throws Exception {
        String source = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
        File outDir = new File("chunks");
        FFmpegStreamingChunker fFmpegChunker = new FFmpegStreamingChunker(source, outDir);

        fFmpegChunker.setChunkHandler((chunkFile) -> {
            System.out.println(chunkFile);
        });
        fFmpegChunker.start();
        while (fFmpegChunker.next()) {
        }
        fFmpegChunker.stop();
    }
}
