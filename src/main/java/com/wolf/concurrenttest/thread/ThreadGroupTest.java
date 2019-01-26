package com.wolf.concurrenttest.thread;

import com.wolf.concurrenttest.common.TakeTimeUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p> Description:
 * 批量同类管理
 * <p/>
 * Date: 2015/9/14
 * Time: 9:43
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class ThreadGroupTest {

	public static void main(String[] args) throws InterruptedException {
		//创建5个线程，并入group里面进行管理
		ThreadGroup threadGroup = new ThreadGroup("Searcher");
		SearchTask searchTask = new SearchTask();
		for (int i = 0; i < 5; i++) {
			Thread thread = new Thread(threadGroup, searchTask);
			//thread.start();//线程只有运行才有组的概念
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Thread.sleep(500);
		//通过这种方法可以看group里面的所有信息
		System.out.printf("activeGroupCount: %d\n", threadGroup.activeGroupCount());//子线程组
		System.out.printf("activeCount: %d\n", threadGroup.activeCount());
		System.out.printf("Information about the Thread Group==============\n");
		System.out.println("list start............");
		threadGroup.list();
		System.out.println("list end............");

		//这样可以复制group里面的thread信息
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);
		for (int i = 0; i < threadGroup.activeCount(); i++) {
			System.out.printf("Thread %s: %s\n", threads[i].getName(),threads[i].getState());
		}

		//waitFinish(threadGroup);
		//将group里面的所有线程都给interpet
		threadGroup.interrupt();

		ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
		System.out.println("main group ====>"+threadGroup1.getName());

	}

	private static void waitFinish(ThreadGroup threadGroup) {
		while (threadGroup.activeCount() > 0) {
			System.out.printf("activeCount: %d\n", threadGroup.activeCount());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//线程组之间有parent概念，线程只有关联的线程组。
	@Test
	public void testParent() {
		Thread currentThread = Thread.currentThread();
		System.out.println("currentThread:"+ currentThread.getName());
		ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
		System.out.println("currentThread().getThreadGroup():"+ currentThreadGroup);
		System.out.println("currentThread().getThreadGroup().activeGroupCount:"+ currentThreadGroup.activeGroupCount());
		System.out.println("currentThread().getThreadGroup().activeCount:"+ currentThreadGroup.activeCount());
		System.out.println("currentThread().getThreadGroup().getParent:"+ currentThreadGroup.getParent());
		System.out.println("currentThread().getThreadGroup().getParent.getParent():"+ currentThreadGroup.getParent().getParent());

		ThreadGroup threadGroup = new ThreadGroup(currentThreadGroup,"new threadgroup");
		System.out.println("add threadgroup after");
		System.out.println("currentThread().getThreadGroup().activeGroupCount:"+ currentThreadGroup.activeGroupCount());
	}

	//若未声明父线程组,则使用当前线程所在线程组作为父。
	@Test
	public void testDefault() {

		ThreadGroup threadGroup2 = new ThreadGroup("new threadgroup2");
		System.out.println("threadGroup2.getParent():"+threadGroup2.getParent());

		Thread currentThread = Thread.currentThread();
		System.out.println("currentThread:"+ currentThread.getName());
		ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
		System.out.println("currentThread().getThreadGroup():"+ currentThreadGroup);
		System.out.println("currentThread().getThreadGroup().activeGroupCount:"+ currentThreadGroup.activeGroupCount());
		System.out.println("currentThread().getThreadGroup().activeCount:"+ currentThreadGroup.activeCount());
		System.out.println("currentThread().getThreadGroup().getParent:"+ currentThreadGroup.getParent());
		System.out.println("currentThread().getThreadGroup().getParent.getParent():"+ currentThreadGroup.getParent().getParent());

		ThreadGroup threadGroup = new ThreadGroup(currentThreadGroup,"new threadgroup");
		System.out.println("add threadgroup after");
		System.out.println("currentThread().getThreadGroup().activeGroupCount:"+ currentThreadGroup.activeGroupCount());
	}

	@Test
	public void testTraversal() {
		ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();
		ThreadGroup groupA = new ThreadGroup(mainGroup, "A");
		ThreadGroup groupB = new ThreadGroup(groupA, "B");

		System.out.println("Thread.currentThread().getThreadGroup().activeCount():"+Thread.currentThread().getThreadGroup().activeCount());
		//Returns an estimate of the number of active groups in this thread group and its subgroups.
		System.out.println("Thread.currentThread().getThreadGroup().activeGroupCount():"+Thread.currentThread().getThreadGroup().activeGroupCount());
		ThreadGroup[] listGroup1 = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
		Thread.currentThread().getThreadGroup().enumerate(listGroup1, true);//递归获取所有子组
		for (ThreadGroup threadGroup : listGroup1) {
			System.out.println(threadGroup.getName());
		}

		ThreadGroup[] listGroup2 = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
		Thread.currentThread().getThreadGroup().enumerate(listGroup2, false);

		for (ThreadGroup threadGroup : listGroup2) {
			if (null != threadGroup) {
				System.out.println(threadGroup.getName());
			}
		}
	}

	//一个线程错误，组内所有线程都被interrupt
	@Test
	public void testOneCauseAll() throws InterruptedException {

		MyThreadGroup myThreadGroup = new MyThreadGroup("myThreadGroup");
		for (int i = 0; i < 3; i++) {
			Runnable runnable = new MyRunnable(i+"");
			new Thread(myThreadGroup, runnable).start();
		}

		Thread.sleep(2000);
		Thread thread = new Thread(myThreadGroup,new MyRunnable("a"));
		thread.start();

		Thread.sleep(500000);
	}

	private static class MyRunnable implements Runnable {

		String a ;

		public MyRunnable(String a) {
			this.a = a;
		}

		@Override
		public void run() {
			//若想让一个线程引起线程组uncaughtException调用，进而影响所有其他线程停止，则不要捕获异常，因为捕获了，就没法触发线程组的uncaughtException方法了
//			try {
				System.out.println(Thread.currentThread() + " isInterrupted:" + Thread.currentThread().isInterrupted());
				int i = Integer.parseInt(String.valueOf(a));
				while (!Thread.currentThread().isInterrupted()) {
					System.out.println("current thread is running:" + Thread.currentThread().getName() + " i:" + i);
					//期初测试使用sleep，那么可能刚好在这一秒遇到interrupt，那么就sleep interrupted。所以下面catch恢复了状态，所以每个runnable中判断不出来了。
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
					TakeTimeUtils.simulateLongTimeOperation(200000);
				}
				System.out.println(Thread.currentThread().getName() + "：finish thread");

//			} catch (Exception e) {
//				e.printStackTrace();
//				System.out.println("xxxxxxxxx");
//			}
		}
	}

	protected static class MyThreadGroup extends ThreadGroup {

		public MyThreadGroup(String name) {
			super(name);
		}

		//由出错误的线程调用dispatchUncaughtException进而调用uncaughtException
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			super.uncaughtException(t, e);
			System.out.println("interrupt in MyThreadGroup.uncaughtException,cause thread:"+t.getName());
			this.interrupt();
		}
	}

	static class SearchTask implements Runnable {

		@Override
		public void run() {
			String name = Thread.currentThread().getName();
			System.out.println("Thread Start ,name:" + name);
			try {
				doTask();
			} catch (InterruptedException e) {
				System.out.printf("Thread %s: Interrupted\n", name);
				return;
			}
			System.out.println("Thread end " + name);
		}

		private void doTask() throws InterruptedException {
			Random random = new Random((new Date()).getTime());
			int value = (int) (random.nextDouble() * 10);
			System.out.printf("Thread %s: sleep:%d\n", Thread.currentThread().getName(),value);
			TimeUnit.SECONDS.sleep(value);
		}
	}
}
