package com.wolf.concurrenttest.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:CLH自旋锁 Craig, Landin, and Hagersten (CLH) locks
 * CLH优缺点
 * CLH队列锁的优点是空间复杂度低
 * （如果有n个线程，L个锁，每个线程每次只获取一个锁，那么需要的存储空间是O（L+n），其中对于每个线程自己内存中，n个线程有n个myNode，L个锁对应L个tail），
 * CLH的一种变体被应用在了JAVA并发框架中(AbstractQueuedSynchronizer.doAcquireSharedInterruptibly)。唯一的缺点是在NUMA系统结构下性能很差，在这种系统结构下，
 * 每个线程有自己的内存，如果前趋结点的内存位置比较远，自旋判断前趋结点的locked域，性能将大打折扣，
 * 但是在SMP系统结构下该法还是非常有效的。一种解决NUMA系统结构的思路是MCS队列锁。
 * <br/> Created on 2017/2/20 16:12
 *
 * @author 李超
 * @since 1.0.0
 */
public class CLHLock {
    private AtomicReference<QNode> tail;
    private ThreadLocal<QNode> currentNodeThreadLocal;
    private ThreadLocal<QNode> preNodeThreadLocal;

    public CLHLock() {
        tail = new AtomicReference<QNode>(new QNode());
        currentNodeThreadLocal = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
        preNodeThreadLocal = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return null;
            }
        };
    }

    public void lock() {
        QNode qnode = currentNodeThreadLocal.get();

        //测试myNode.set(preNodeThreadLocal.get());有何用图？不知道
        //qnode.setName(Thread.currentThread().getName());
        //currentNodeThreadLocal.set(qnode);

        qnode.setLocked(true);
        QNode preNode = tail.getAndSet(qnode);
        this.preNodeThreadLocal.set(preNode);
        while(preNode.isLocked()) {
            //这里开始有sleep导致线程有机会取到已经修改后的lock，后来注释掉也发现了问题，也加上了volatile
//            System.out.println(Thread.currentThread().getName() + " sleep 1s");
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void unlock() {
        QNode qnode = currentNodeThreadLocal.get();
        qnode.setLocked(false);
        //这句可能是为gc，感觉要释放前一个节点应该是preNode.set(null);
        //后来发现：将前一个节点(locked肯定是false)，设置成当前threadlocal中，便于下个app使用线程池中的当前线程使用，其实感觉没必要，本身已经设定false了,唯一可能是最后一个线程的node被回收了
        currentNodeThreadLocal.set(preNodeThreadLocal.get());

    }


    //下面暂时为了打印使用
    public AtomicReference<QNode> getTail() {
        return tail;
    }

    public void setTail(AtomicReference<QNode> tail) {
        this.tail = tail;
    }

    public ThreadLocal<QNode> getCurrentNodeThreadLocal() {
        return currentNodeThreadLocal;
    }

    public void setCurrentNodeThreadLocal(ThreadLocal<QNode> currentNodeThreadLocal) {
        this.currentNodeThreadLocal = currentNodeThreadLocal;
    }

    public ThreadLocal<QNode> getPreNodeThreadLocal() {
        return preNodeThreadLocal;
    }

    public void setPreNodeThreadLocal(ThreadLocal<QNode> preNodeThreadLocal) {
        this.preNodeThreadLocal = preNodeThreadLocal;
    }
}
