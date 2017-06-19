package FFmpegStreamingChunker;

import java.util.Objects;

/**
 *
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class ChunkDetector {

    private final long chunkDuration;
    private ChunkListener chunkListener = null;
    private long chunksNum = 0;
    private long chunkBegin = 0;
    private long lastTimestamp = 0;

    /**
     * @param chunkDuration In microseconds.
     */
    public ChunkDetector(long chunkDuration) {
        if (chunkDuration < 1) {
            throw new IllegalArgumentException("Chunk duration is less than zero.");
        }
        this.chunkDuration = chunkDuration;
    }

    public ChunkListener getChunkListener() {
        return chunkListener;
    }

    public void setChunkListener(ChunkListener chunkListener) {
        this.chunkListener = chunkListener;
    }

    public long getChunksNum() {
        return chunksNum;
    }

    public long getChunkDuration() {
        return chunkDuration;
    }

    public long getCalculatedChunkBegin() {
        if (chunksNum == 0) {
            throw new IllegalStateException();
        }
        return (chunksNum - 1) * chunkDuration;
    }

    public long getChunkBegin() {
        if (chunksNum == 0) {
            throw new IllegalStateException();
        }
        return chunkBegin;
    }

    public long getLastTimestamp() {
        if (chunksNum == 0) {
            throw new IllegalStateException();
        }
        return lastTimestamp;
    }

    public void next(boolean isKeyFrame, long frameTimestamp) throws Exception {
        if (chunksNum == 0
                || isKeyFrame && frameTimestamp - chunkBegin > chunkDuration) {

            if (chunksNum != 0 && chunkListener != null) {
                chunkListener.onChunkEnd();
            }
            chunksNum++;
            chunkBegin = frameTimestamp;
            if (chunkListener != null) {
                chunkListener.onChunkBegin();
            }

        }
        lastTimestamp = frameTimestamp;
    }

    public void reset() {
        chunksNum = 0;
        chunkBegin = 0;
        lastTimestamp = 0;
    }
}
