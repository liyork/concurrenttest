package com.wolf.concurrenttest.taojcp.connpool;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * Description: 用LinkedList+synchronized实现生产者-消费者模式
 * 尾部放入，头部获取
 * 消费时有超时控制
 * Created on 2021/8/27 12:38 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ConnectionPool {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            for (int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    // 释放并唤醒
    public void releaseConnection(Connection connection) {
        if (connection != null) {  // 单线程
            synchronized (pool) {
                // 释放+通知
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    // 超时等待
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {  // 单线程操作池
            if (mills <= 0) {  // 一直等待
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {  // 为空且有超时时间则等待
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }

                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
