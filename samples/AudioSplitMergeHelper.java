import org.bytedeco.javacv.*;

import java.nio.Buffer;
import java.nio.ShortBuffer;

/**
 * This code is a sample which split a 2-channel stereo audio into 2 single-channel mono audios
 * or merge 2 single-channel mono audios into a 2-channel stereo.
 * <p>
 * The code has been tested on s16le audio.
 * <p>
 * s16le means short 16bit little end. For other format, you may need change the ShortBuffer to other Buffer subclass
 * <p>
 * For s16lep, s32lep,xxxxxp format, the sample point arrangement format is no longer in ‘LRLRLR'.
 * Instead, it is arragement in format 'LLLLLL','RRRRRR'. So you have to change the short copy code.
 * <p>
 * <p>
 * ///////////////////////////////////////////////////////////////////////////
 * JavaCV is an excellent open-source streaming processing framework in the Java field
 * <p>
 * But I see many people, especially in China, making profits for themselves by introducing its usage,
 * which is not in line with the concept of open source projects.
 * I hope that If this code helped you, you can share your experience and knowledge with others in the world, rather
 * than for personal gain. Spread the spirit of open source.
 * ///////////////////////////////////////////////////////////////////////////
 * <p>
 * Acknowledge: Thanks for my hot girlfriend.
 *
 * @author steeveen
 * @date 2023/7/1 14:32
 */
public class AudioSplitMergeHelper {


    /**
     * split a 2-channel stereo audio into 2 single-channel mono audios
     * <p>
     * If you want to split this 2-channel stereo to 2 single-channel stereo, you should create 2 2-channel stereos
     * and fill one channel with 0 data. It is similar in principle, so the code won't go into too much here.
     *
     * @param input       the file path which is to be splited
     * @param outputLeft  the file path which store the left channel audio file
     * @param outputRight the file path which store the right channel audio file
     * @throws FrameGrabber.Exception
     * @throws FrameRecorder.Exception
     */
    public static void split(String input, String outputLeft, String outputRight) throws FrameGrabber.Exception, FrameRecorder.Exception {
        //grabber from input
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(input);
        grabber.start();
        //two recorders for two channels
        FFmpegFrameRecorder leftRecorder = new FFmpegFrameRecorder(outputLeft, 1);
        leftRecorder.setSampleRate(grabber.getSampleRate());
        leftRecorder.start();
        FFmpegFrameRecorder rightRecorder = new FFmpegFrameRecorder(outputRight, 1);
        rightRecorder.setSampleRate(grabber.getSampleRate());
        rightRecorder.start();

        Frame frame = null;
        while ((frame = grabber.grabSamples()) != null) {
            // use s16le for example. so select ShortBuffer to receive the sample
            ShortBuffer sb = (ShortBuffer) frame.samples[0];
            short[] shorts = new short[sb.limit()];
            sb.get(shorts);
            //Split the LRLRLR to LLL in left channel and RRR int right channel
            Frame leftFrame = frame.clone();
            ShortBuffer leftSb = ShortBuffer.allocate(sb.capacity() / 2);
            leftFrame.samples = new Buffer[]{leftSb};
            leftFrame.audioChannels = 1;

            Frame rightFrame = frame.clone();
            ShortBuffer rightSb = ShortBuffer.allocate(sb.capacity() / 2);
            rightFrame.samples = new Buffer[]{rightSb};
            rightFrame.audioChannels = 1;

            for (int i = 0; i < shorts.length; i++) {
                if (i % 2 == 0) {
                    leftSb.put(shorts[i]);
                } else {
                    rightSb.put(shorts[i]);
                }
            }
            // reset the buffer to read mode
            leftSb.rewind();
            rightSb.rewind();
            leftRecorder.record(leftFrame);
            rightRecorder.record(rightFrame);
        }
        //release source
        grabber.close();
        leftRecorder.close();
        rightRecorder.close();
    }

    /**
     * Merge 2 single-channel mono audios into a 2-channel stereo.
     * As usual the two input audios should have the same parameter and length;
     *
     * @param inputLeft  the left channel to be merged in
     * @param inputRight the right channel to be merged in
     * @param output     the merged stereo audio
     * @throws FFmpegFrameGrabber.Exception
     * @throws FFmpegFrameRecorder.Exception
     */
    public static void merge(String inputLeft, String inputRight, String output) throws FrameGrabber.Exception, FrameRecorder.Exception {
        FFmpegFrameGrabber leftGrabber = new FFmpegFrameGrabber(inputLeft);
        leftGrabber.start();
        FFmpegFrameGrabber rightGrabber = new FFmpegFrameGrabber(inputRight);
        rightGrabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(output, 2);
        //you'd better confirm the two input have the same samplerate. otherwise, you should control it manually by yourself
        recorder.setSampleRate(leftGrabber.getSampleRate());
        recorder.start();

        Frame leftFrame = null;
        Frame rightFrame = null;
        int index = 0;
        int maxLength = leftGrabber.getLengthInAudioFrames();
        while (index < maxLength) {
            // carry the bit data from two input into result frame by frame
            leftFrame = leftGrabber.grabSamples();
            rightFrame = rightGrabber.grabSamples();
            ShortBuffer leftSb = (ShortBuffer) leftFrame.samples[0];
            ShortBuffer rightSb = (ShortBuffer) rightFrame.samples[0];
            short[] leftShorts = new short[leftSb.limit()];
            short[] rightShorts = new short[rightSb.limit()];
            leftSb.get(leftShorts);
            rightSb.get(rightShorts);
            ShortBuffer mergeSb = ShortBuffer.allocate(leftSb.capacity() + rightSb.capacity());

            // create a template from the existing frame
            Frame mergeFrame = leftFrame.clone();
            // replace the frame tempalte by our merged buffer
            mergeFrame.samples = new Buffer[]{mergeSb};
            mergeFrame.audioChannels = 2;

            for (int i = 0; i < leftShorts.length; i++) {
                mergeSb.put(leftShorts[i]);
                mergeSb.put(rightShorts[i]);
            }

            //reset buffer to read mode
            mergeSb.flip();
            recorder.record(mergeFrame);
            index++;
        }
        //release source
        leftGrabber.close();
        rightGrabber.close();
        recorder.close();
    }
}
