package com.wolf.concurrenttest.thread;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/23
 * Time: 11:51
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class StateTest{
	//静态代码块类第一次被使用时加载，应该会保证多线程同步
	static {
		System.out.println(Thread.currentThread().getName());
	}

	public static void test(){
		//System.out.println(Thread.currentThread().getName());
	}
}