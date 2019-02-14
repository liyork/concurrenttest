package com.wolf.concurrenttest.mtadp.readwritelock;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/01
 */
public class ReadWriteLockTest {

    private final static String text = "thisistheexpleforreadwritelock";

    public static void main(String[] args) {

        ShareDate shareDate = new ShareDate(50);

        //2线程写
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int index = 0; index < text.length(); index++) {
                    try {
                        char c = text.charAt(index);
                        shareDate.write(c);
                        System.out.println(Thread.currentThread() + " write " + c);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //10线程读
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                while (true) {
                    try {
                        System.out.println(Thread.currentThread() + " read " + new String(shareDate.read()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
