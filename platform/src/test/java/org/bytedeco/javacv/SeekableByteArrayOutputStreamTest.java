/*
 * Copyright (C) 2019 Sven Vorlauf
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacv;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Test;

public class SeekableByteArrayOutputStreamTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final int FRAME_COUNT = 100;

    private int writeByte(byte[] originalBytes, int offset, SeekableByteArrayOutputStream byteArrayOutputStream) {
        byteArrayOutputStream.write(originalBytes[offset]);
        return 1;
    }

    private int writePartialBytes(byte[] originalBytes, int offset, Random random,
            SeekableByteArrayOutputStream byteArrayOutputStream) throws IOException {
        int chunkSize = Math.min(random.nextInt(50), originalBytes.length - offset);
        byteArrayOutputStream.write(originalBytes, offset, chunkSize);
        return chunkSize;
    }

    private int writeBytes(byte[] originalBytes, int offset, Random random,
            SeekableByteArrayOutputStream byteArrayOutputStream) throws IOException {
        int chunkSize = Math.min(random.nextInt(50), originalBytes.length - offset);
        byteArrayOutputStream.write(Arrays.copyOfRange(originalBytes, offset, offset + chunkSize));
        return chunkSize;
    }

    private void createVideo(FFmpegFrameRecorder recorder) throws Exception {
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_MPEG4);
        recorder.setFormat("mp4");
        recorder.setFrameRate(30);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        for (int n = 0; n < FRAME_COUNT; n++) {
            Frame frame = new Frame(WIDTH, HEIGHT, Frame.DEPTH_UBYTE, 3);
            UByteIndexer frameIdx = frame.createIndexer();
            for (int i = 0; i < frameIdx.rows(); i++) {
                for (int j = 0; j < frameIdx.cols(); j++) {
                    for (int k = 0; k < frameIdx.channels(); k++) {
                        frameIdx.put(i, j, k, n + i + j + k);
                    }
                }
            }
            recorder.record(frame);
        }
        recorder.close();
    }

    @Test
    public void serialWriteByteTest() {
        System.out.println("SeekableByteArrayOutputStreamSerialWriteByte");
        Random random = new Random(-1);
        byte[] originalBytes = new byte[1000];
        random.nextBytes(originalBytes);

        try (SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream()) {
            int offset = 0;
            while (offset < originalBytes.length) {
                offset += writeByte(originalBytes, offset, byteArrayOutputStream);
            }
            assertArrayEquals(originalBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        }
    }

    @Test
    public void serialWriteBytesTest() {
        System.out.println("SeekableByteArrayOutputStreamSerialWriteBytes");
        Random random = new Random(-1);
        byte[] originalBytes = new byte[1000];
        random.nextBytes(originalBytes);

        try (SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream()) {
            int offset = 0;
            while (offset < originalBytes.length) {
                offset += writeBytes(originalBytes, offset, random, byteArrayOutputStream);
            }
            assertArrayEquals(originalBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        }
    }

    @Test
    public void serialWritePartialBytesTest() {
        System.out.println("SeekableByteArrayOutputStreamSerialWritePartialBytes");
        Random random = new Random(-1);
        byte[] originalBytes = new byte[1000];
        random.nextBytes(originalBytes);

        try (SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream()) {
            int offset = 0;
            while (offset < originalBytes.length) {
                offset += writePartialBytes(originalBytes, offset, random, byteArrayOutputStream);
            }
            assertArrayEquals(originalBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        }
    }

    @Test
    public void serialWriteTest() {
        System.out.println("SeekableByteArrayOutputStreamSerialWrite");
        Random random = new Random(-1);
        byte[] originalBytes = new byte[1000];
        random.nextBytes(originalBytes);

        try (SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream()) {
            int offset = 0;
            while (offset < originalBytes.length) {
                switch (random.nextInt(3)) {
                case 0:
                    offset += writeByte(originalBytes, offset, byteArrayOutputStream);
                    break;
                case 1:
                    offset += writeBytes(originalBytes, offset, random, byteArrayOutputStream);
                    break;
                case 2:
                    offset += writePartialBytes(originalBytes, offset, random, byteArrayOutputStream);
                    break;
                }
            }
            assertArrayEquals(originalBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        }
    }

    public void seekWriteTest() {
        System.out.println("SeekableByteArrayOutputStreamSeekWrite");
        Random random = new Random(-1);
        byte[] originalBytes = new byte[1000];
        random.nextBytes(originalBytes);
        try (SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream()) {
            int offset = 0;
            for (int i = 0; i < 10; i++) {
                // write 100 bytes
                byteArrayOutputStream.write(originalBytes, offset, 100);

                int position = random.nextInt(offset + 20);
                int newBytesPosition = position + 500 % 1000;
                int length = 10 + random.nextInt(20);
                // get current bytes
                byte[] writtenOriginalBytes = Arrays.copyOfRange(byteArrayOutputStream.toByteArray(), position,
                        position + length);

                // bytes to write at the new position
                byte[] newBytes = Arrays.copyOfRange(originalBytes, newBytesPosition, newBytesPosition + length);

                // just assert that the new bytes are different to the written ones
                assertThat(writtenOriginalBytes, IsNot.not(IsEqual.equalTo(newBytes)));

                // replace bytes
                byteArrayOutputStream.seek(position, 0);
                byteArrayOutputStream.write(newBytes);
                byte[] writtenNewBytes = Arrays.copyOfRange(byteArrayOutputStream.toByteArray(), position,
                        position + length);
                assertThat(newBytes, IsEqual.equalTo(writtenNewBytes));

                // write back original bytes
                byteArrayOutputStream.seek(position, 0);
                byteArrayOutputStream.write(originalBytes, position, length);

                // get back to the end of the stream
                byteArrayOutputStream.seek(offset, 0);
                offset += 100;
            }
            while (offset < originalBytes.length) {
                switch (random.nextInt(3)) {
                case 0:
                    offset += writeByte(originalBytes, offset, byteArrayOutputStream);
                    break;
                case 1:
                    offset += writeBytes(originalBytes, offset, random, byteArrayOutputStream);
                    break;
                case 2:
                    offset += writePartialBytes(originalBytes, offset, random, byteArrayOutputStream);
                    break;
                }
            }
            assertArrayEquals(originalBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        }
    }

    @Test
    public void testVideoBytesEqual() {
        // if this test fails it might be due to indeterministic multithreaded encoding
        System.out.println("SeekableByteArrayOutputStreamVideo");
        File tempFile = new File(Loader.getTempDir(), "test.mp4");
        try {
            createVideo(new FFmpegFrameRecorder(tempFile, WIDTH, HEIGHT, 0));
            byte[] fileBytes = Files.readAllBytes(tempFile.toPath());

            SeekableByteArrayOutputStream byteArrayOutputStream = new SeekableByteArrayOutputStream();
            createVideo(new FFmpegFrameRecorder(byteArrayOutputStream, WIDTH, HEIGHT, 0));
            assertArrayEquals(fileBytes, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            fail("Exception should not have been thrown: " + e);
        } finally {
            tempFile.delete();
        }
    }
}
