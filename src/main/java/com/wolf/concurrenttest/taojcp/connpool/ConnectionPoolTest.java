package com.wolf.concurrenttest.taojcp.connpool;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 测试不同线程，对于pool进行20次的fetch，查看成功数量
 * Created on 2021/8/27 12:45 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConnectionPoolTest {
    static ConnectionPool pool = new ConnectionPool(10);
    // 保证所有runner同时开始
    static CountDownLatch start = new CountDownLatch(1);
    // main等待所有runner结束
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        // 线程数量，通过修改20,30,40观察fetch成功失败率
        int threadCount = 10;
        end = new CountDownLatch(threadCount);

        int count = 20;
        AtomicInteger got = new AtomicInteger();
        AtomicInteger notGot = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, notGot), "ConnectionRunnerThread");
            thread.start();
        }

        start.countDown();
        end.await();

        System.out.println("total invoke: " + (threadCount * count));
        System.out.println("got connection: " + got);
        System.out.println("not got connection: " + notGot);
    }

    // 每个线程执行任务，count数内不断对池fetch+release
    static class ConnectionRunner implements Runnable {
        int count;
        AtomicInteger got;
        AtomicInteger notGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger notGot) {
            this.count = count;
            this.got = got;
            this.notGot = notGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (count > 0) {
                try {
                    Connection connection = pool.fetchConnection(1000);
                    if (connection != null) {  // 获取到
                        try {
                            connection.createStatement();
                            connection.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {  // 未获取到
                        notGot.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }
}
