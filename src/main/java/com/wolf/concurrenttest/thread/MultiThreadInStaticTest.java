package com.wolf.concurrenttest.thread;

/**
 * <p> Description:
 * <p/>
 * Date: 2015/11/27
 * Time: 10:01
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class MultiThreadInStaticTest {

	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					StateTest.test();
				}
			}).start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					StateTest.test();
				}
			}).start();

		}
	}
}