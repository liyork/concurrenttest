package com.wolf.concurrenttest.mtadp.observethread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/01/31
 */
public class ObservableThreadTest {

    public static void main(String[] args) {

        Task<String> stringTask = () -> {

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("mytaks haha");

            return "111";
        };

        MyTaskLifeCycle myTaskLifeCycle = new MyTaskLifeCycle();
        Observable observable = new ObservableThread(myTaskLifeCycle, stringTask);
        observable.start();
    }

    static class MyTaskLifeCycle implements TaskLifeCycle {

        @Override
        public void onStart(Thread thread) {
            System.out.println("MyTaskLifeCycle start ");
        }

        @Override
        public void onRunning(Thread thread) {
            System.out.println("MyTaskLifeCycle onRunning ");
        }

        @Override
        public void onFinish(Thread thread, Object result) {
            System.out.println("MyTaskLifeCycle onFinish ,result:" + result);
        }

        @Override
        public void onError(Thread thread, Exception e) {
            System.out.println("MyTaskLifeCycle onError ");

        }
    }
}
