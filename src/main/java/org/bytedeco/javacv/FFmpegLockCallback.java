package org.bytedeco.javacv;

import static org.bytedeco.javacpp.avcodec.av_lockmgr_register;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avcodec.Cb_PointerPointer_int;
import org.bytedeco.javacpp.annotation.Cast;

public class FFmpegLockCallback {
	private static boolean initialized = false;

	private static AtomicInteger lockCounter = new AtomicInteger(0);
	private static HashMap<Integer, Lock> lockArray = new HashMap<>();
	private static Cb_PointerPointer_int lockCallback = new Cb_PointerPointer_int() {
		@Override
		public int call(@SuppressWarnings("rawtypes") @Cast("void**") PointerPointer mutex, @Cast("AVLockOp") int op) {
			int number;
			Lock l;
			// System.out.println "Locking: " + op);
			switch (op) {
			case avcodec.AV_LOCK_CREATE:
				number = lockCounter.incrementAndGet();
				// System.out.println("Command: " + op + " number: " + number);
				new IntPointer(mutex).put(0, number);
				lockArray.put(number, new ReentrantLock());
				return 0;
			case avcodec.AV_LOCK_OBTAIN:
				number = new IntPointer(mutex).get(0);
				// System.out.println("Command: " + op + " number: " + number);
				l = lockArray.get(number);
				if (l == null) {
					System.err.println("Lock not found!");
					return -1;
				}
				l.lock();
				return 0;
			case avcodec.AV_LOCK_RELEASE:
				number = new IntPointer(mutex).get(0);
				// System.out.println("Command: " + op + " number: " + number);
				l = lockArray.get(number);
				if (l == null) {
					System.err.println("Lock not found!");
					return -1;
				}
				l.unlock();
				return 0;
			case avcodec.AV_LOCK_DESTROY:
				number = new IntPointer(mutex).get(0);
				// System.out.println("Command: " + op + " number: " + number);
				lockArray.remove(number);
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
