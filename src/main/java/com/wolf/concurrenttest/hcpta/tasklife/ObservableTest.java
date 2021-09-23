package com.wolf.concurrenttest.hcpta.tasklife;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/23 1:21 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ObservableTest {
    public static void main(String[] args) {
        //test1();

        final TaskLifecycle<String> lifecycle = new TaskLifecycle.EmptyLifecycle<String>() {
            @Override
            public void onFinish(Thread thread, String result) {
                System.out.println("The result is " + result);
            }
        };

        ObservableThread<String> observableThread = new ObservableThread<>(lifecycle, () -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" finished done");
            return "hello Observer";
        });
        observableThread.start();
    }

    private static void test1() {
        ObservableThread<Void> observableThread = new ObservableThread<>(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" finished done");
            return null;
        });
        observableThread.start();
    }
}
