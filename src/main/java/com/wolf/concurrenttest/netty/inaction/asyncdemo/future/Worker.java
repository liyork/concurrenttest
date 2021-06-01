package com.wolf.concurrenttest.netty.inaction.asyncdemo.future;


import com.taobao.gecko.core.core.impl.FutureImpl;
import com.wolf.concurrenttest.netty.inaction.asyncdemo.callback.Data;

import java.util.concurrent.Future;

/**
 * Description:
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
            public Future<Data> fetchData() {
                return new FutureImpl<>();
            }
        };

        Future<Data> future = fetcher.fetchData();
        try {
            while (!future.isDone()) {
                System.out.println("not succ in future");
            }
            System.out.println("Data received: " + future.get());
        } catch (Throwable cause) {
            System.out.println("An error accour: " + cause.getMessage());
        }
    }
}
