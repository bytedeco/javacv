package FFmpegStreamingChunker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
 */
public class FpsCalculator {

    private final List<Long> timestamps = new ArrayList<>(60);
    private double fps = Double.NaN;
    private static final double GAP_DEVIATION_RATIO = 0.1;

    public FpsCalculator() {
    }

    /**
     * @param timestamp In microseconds.
     */
    public void addTimestamp(long timestamp) {
        fps = Double.NaN;
        timestamps.add(timestamp);
    }

    /**
     * @return In microseconds.
     */
    public double getFps() {
        if (timestamps.size() < 2) {
            throw new IllegalStateException("Not enough data.");
        }
        if (Double.isNaN(fps)) {
            double[] gaps = new double[timestamps.size() - 1];
            for (int i = 1; i < timestamps.size(); i++) {
                gaps[i - 1] = timestamps.get(i) - timestamps.get(i - 1);
            }

            int[] similarGapOccurrences = new int[gaps.length];
            Arrays.fill(similarGapOccurrences, 0);
            for (int i = 0; i < gaps.length; i++) {
                for (int j = 0; j < gaps.length; j++) {
                    if (Math.abs(gaps[i] - gaps[j]) < gaps[i] * GAP_DEVIATION_RATIO) {
                        similarGapOccurrences[i]++;
                    }
                }
            }

            int maxOccurrences = 0;
            for (int i = 0; i < similarGapOccurrences.length; i++) {
                maxOccurrences = Math.max(maxOccurrences, similarGapOccurrences[i]);
            }
            if (maxOccurrences == 0) {
                throw new RuntimeException("No similar gaps.");
            }

            for (int i = 0; i < similarGapOccurrences.length; i++) {
                if (similarGapOccurrences[i] == maxOccurrences) {
                    fps = 1000000 / gaps[i];
                    break;
                }
            }
        }
        return fps;

    }

    /**
     * @return In microseconds.
     */
    public long getFpsRounded() {
        return Math.round(getFps());
    }

    public int getTimestampsNum() {
        return timestamps.size();
    }

    public void reset() {
        timestamps.clear();
        fps = Double.NaN;
    }

}
