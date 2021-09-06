package com.wolf.concurrenttest.bfbczm.lock;

import java.util.concurrent.locks.StampedLock;

/**
 * Description: StampedLock测试
 * Created on 2021/9/6 10:10 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class StampedLockTest {
    class Point {
        private double x, y;
        private final StampedLock sl = new StampedLock();

        // 增量加
        // 排他锁——写锁
        void move(double deltaX, double deltaY) {
            // 获取写锁，排他，其他线程不能读也不能写
            long stamp = sl.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                sl.unlockWrite(stamp);
            }
        }

        // 计算现在x，y到0，0的距离
        // 乐观读锁
        double distanceFromOrigin() {
            // 尝试获取乐观读锁，若当前没有其他线程获取到写锁，那么返回非0
            long stamp = sl.tryOptimisticRead();
            // 将变量复制到方法栈内
            double currentX = x, currentY = y;
            // 检查tryOptimisticRead后，锁是否被其他写线程排他性抢占
            // 若无抢占，则直接用currentX/currentY快照值进行return的计算
            // 先上面复制到栈，然后这里验证，符合就是用这个快照值，乐观锁的精妙之处
            if (!sl.validate(stamp)) {  // 被抢占
                // 若被抢占则获取一个共享读锁(悲观获取)。
                // 若此时有其他线程持有写锁，则会使当前线程阻塞直到其他线程释放了写锁。若没有则获取读锁
                stamp = sl.readLock();  // 加悲观读锁则其他写不能获取。加锁的语义可以保证内存可见性
                try {
                    // 将变量重新复制到方法体栈内
                    currentX = x;
                    currentY = y;
                } finally {
                    // 释放共享读锁
                    sl.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }

        // 若当前坐标为原点则移动到指定的位置
        // 用悲观锁获取读锁，尝试转换为写锁
        void moveIfAtOrigin(double newX, double newY) {
            // 这里可用乐观读锁替换
            long stamp = sl.readLock();
            try {
                // 若当前点在原点
                while (x == 0.0 && y == 0.0) {
                    // 尝试将获取的读锁升级为写锁
                    long ws = sl.tryConvertToWriteLock(stamp);
                    if (ws != 0L) {// 升级成功，更新戳记，设置坐标值，退出循环
                        stamp = ws;
                        x = newX;
                        y = newY;
                        break;
                    } else {
                        // 读锁升级写锁失败，释放读锁，显示获取独占写锁，然后循环重试
                        sl.unlockRead(stamp);
                        stamp = sl.writeLock();
                    }
                }
            } finally {
                // 释放锁
                sl.unlock(stamp);
            }
        }
    }


}
