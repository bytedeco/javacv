package org.bytedeco.javacv;

import org.bytedeco.ffmpeg.avcodec.AVPacket;

/**
 * Encoded packet hooker called by {@link FFmpegFrameRecorder}
 */
public interface EncodedPacketHooker {

    /**
     * Called when a packet has been encoded in record processing.
     *
     * @param packet the encoded packet.
     * @param audio weather or not the packet is an audio packet.
     */
    void onPacketEncoded(AVPacket packet, boolean audio);

}
