package com.wolf.concurrenttest.netty.inaction.asyncdemo.callback;

/**
 * Description: 异步伴随用callback
 * Created on 2021/6/1 10:08 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Worker {
    public void doWork() {
        int a = 1;
        Fetcher fetcher = new Fetcher() {
            @Override
            public void fetchData(FetchCallback callback) {
                if (a == 1) {
                    callback.onData(new Data());
                } else {
                    callback.onError(new RuntimeException("xxx"));
                }
            }
        };

        fetcher.fetchData(new FetchCallback() {
            @Override
            public void onData(Data data) {
                System.out.println("Data received: " + data);
            }

            @Override
            public void onError(Throwable cause) {
                System.out.println("An error accour: " + cause.getMessage());
            }
        });
    }
}
