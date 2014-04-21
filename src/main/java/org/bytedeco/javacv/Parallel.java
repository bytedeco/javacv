/*
 * Copyright (C) 2009,2010,2011,2012 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bytedeco.javacv;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Samuel Audet
 */
public class Parallel {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    public static final String NUM_THREADS = "org.bytedeco.javacv.numthreads";

    public static int getNumThreads() {
        try {
            String s = System.getProperty(NUM_THREADS);
            if (s != null) {
                return Integer.valueOf(s);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return getNumCores();
    }
    public static void setNumThreads(int numThreads) {
        System.setProperty(NUM_THREADS, Integer.toString(numThreads));
    }

    public static int getNumCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void run(Runnable ... runnables) {
        if (runnables.length == 1) {
            runnables[0].run();
            return;
        }

        Future[] futures = new Future[runnables.length];
        for (int i = 0; i < runnables.length; i++) {
            futures[i] = threadPool.submit(runnables[i]);
        }

        Throwable error = null;
        try {
            for (Future f : futures) {
                if (!f.isDone()) {
                    f.get();
                }
            }
        } catch (Throwable t) {
            error = t;
        }

        if (error != null) {
            for (Future f : futures) {
                f.cancel(true);
            }
            throw new RuntimeException(error);
        }
    }

    public interface Looper {
        void loop(int from, int to, int looperID);
    }

    public static void loop(int from, int to, final Looper looper) {
        loop(from, to, getNumThreads(), looper);
    }
    public static void loop(int from, int to, int numThreads, final Looper looper) {
        int numLoopers = Math.min(to-from, numThreads > 0 ? numThreads : getNumCores());
        Runnable[] runnables = new Runnable[numLoopers];
        for (int i = 0; i < numLoopers; i++) {
            final int subFrom = (to-from)*i/numLoopers + from;
            final int subTo = (to-from)*(i+1)/numLoopers + from;
            final int looperID = i;
            runnables[i] = new Runnable() {
                public void run() {
                    looper.loop(subFrom, subTo, looperID);
                }
            };
        }
        run(runnables);
    }
}
