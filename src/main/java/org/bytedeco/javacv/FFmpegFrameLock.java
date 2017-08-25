package org.bytedeco.javacv;

import static org.bytedeco.javacpp.avcodec.av_lockmgr_register;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bytedeco.javacpp.LongPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.avcodec.Cb_PointerPointer_int;

public class FFmpegFrameLock {
    private static boolean initialized = false;

    private static AtomicLong lockCounter = new AtomicLong(0);
    private static HashMap<Long, Lock> lockArray = new HashMap<>();
    private static Cb_PointerPointer_int lockCallback = new Cb_PointerPointer_int() {
        @Override
        public int call(@SuppressWarnings("rawtypes") @Cast("void**") PointerPointer mutex, @Cast("AVLockOp") int op) {
            LongPointer myMutex;
            Long number;
            Lock l;
            // System.out.println "Locking: " + op);
            switch (op) {
            case avcodec.AV_LOCK_CREATE:
                Pointer p = Pointer.malloc(Long.BYTES);
                myMutex = new LongPointer(p);
                number = lockCounter.incrementAndGet();
                // System.out.println(
                // "Command: " + op + " number: " + number + " " +
                // mutex.address() + " " + myMutex.address());
                myMutex.put(0, number);
                mutex.put(0, myMutex);
                lockArray.put(number, new ReentrantLock());
                return 0;
            case avcodec.AV_LOCK_OBTAIN:
                myMutex = new LongPointer(mutex.get(0));
                number = myMutex.get(0);
                // System.out.println(
                // "Command: " + op + " number: " + number + " " +
                // mutex.address() + " " + myMutex.address());
                l = lockArray.get(number);
                if (l == null) {
                    System.err.println("Lock not found!");
                    return -1;
                }
                l.lock();
                return 0;
            case avcodec.AV_LOCK_RELEASE:
                myMutex = new LongPointer(mutex.get(0));
                number = myMutex.get(0);
                // System.out.println(
                // "Command: " + op + " number: " + number + " " +
                // mutex.address() + " " + myMutex.address());
                l = lockArray.get(number);
                if (l == null) {
                    System.err.println("Lock not found!");
                    return -1;
                }
                l.unlock();
                return 0;
            case avcodec.AV_LOCK_DESTROY:
                myMutex = new LongPointer(mutex.get(0));
                number = myMutex.get(0);
                // System.out.println(
                // "Command: " + op + " number: " + number + " " +
                // mutex.address() + " " + myMutex.address());
                lockArray.remove(number);
                Pointer.free(myMutex);
                mutex.put(0, null);
                return 0;
            default:
                return -1;
            }
        }
    };

    public static synchronized void init() {
        if (!initialized) {
            initialized = true;
            av_lockmgr_register(lockCallback);
        }
    }
}
