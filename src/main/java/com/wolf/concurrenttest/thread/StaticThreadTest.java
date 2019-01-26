package com.wolf.concurrenttest.thread;

/**
 * Description:静态方法中的变量是私有的
 * <br/> Created on 2016/11/6 11:43
 *
 * @author 李超()
 * @since 1.0.0
 */
public class StaticThreadTest {

	public static void test1(){
		int a = 1;
		System.out.println("a===>"+a);
		a++;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("a===>"+a);
	}

	public void test2(){
		int a = 1;
		System.out.println("a===>"+a);
		a++;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("a===>"+a);
	}

	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StaticThreadTest.test1();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				StaticThreadTest.test1();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				new StaticThreadTest().test2();
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				new StaticThreadTest().test2();
			}
		}).start();
	}
}
