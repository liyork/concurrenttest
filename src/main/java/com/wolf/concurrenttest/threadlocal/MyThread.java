package com.wolf.concurrenttest.threadlocal;

/**
 * Description:
 * <br/> Created on 3/2/18 9:32 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class MyThread extends Thread {
    public MyThread(Runnable target) {
        super(target);
    }

    @Override
    public synchronized void start() {
        System.out.println("start this:"+this);//构造的Thread对象
        super.start();
    }

    @Override
    public void run() {
        System.out.println("default run this:"+this);//构造的Thread对象
        super.run();
    }

    //静态方法不能使用this，因为this表示谁调用了我，而静态方法不用对象调用，直接类调用，所以不能用this
//    public static void test(){
//        System.out.println("static test this:"+this);
//    }
}
