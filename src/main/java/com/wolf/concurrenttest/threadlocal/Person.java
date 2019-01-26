package com.wolf.concurrenttest.threadlocal;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/23
 * Time: 11:50
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
class Person{
	private String name;
	//ThreadLocal定义为static，他只是一个工具类，具体的map在每个线程对象中。
	private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>(){
		@Override
		protected Integer initialValue() {
			//每个线程里面有个map，get时会判断thread里面有没有map，没有则调用initialValue进行初始化，放入map
			System.out.println("initialValue ..."+Thread.currentThread().getName());
			return 0;
		}
	};
	private Integer count = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//由于使用了中间全局变量count，那么t1设定+1后，其他人也可以设定+1，然后t1再设定时可能与t2最后一样
	void increase(){

		this.count = threadLocal.get() + 1;
		System.out.println("1111");
		threadLocal.set(count);
	}

	//此处线程有安全问题，虽然使用threadLocal，但是它只是将栈中数据本地拷贝一份到线程中去，但是count++时会一起操作栈中数据，导致问题。
	void increase1(){

		this.count = threadLocal.get();
		System.out.println("1111");//这段如果去掉，可能由于cpu太快，导致下面的++不会出现线程问题，
		count++;
		threadLocal.set(count);
	}

	int getAge(){
		return threadLocal.get();
	}

	public Integer getCount() {
		return count;
	}
}