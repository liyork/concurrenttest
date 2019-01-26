package com.wolf.concurrenttest.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Description:原子操作的次数已经减少到最少，大多数时候只需要本地读写node变量，优于CLHLock
 * 是一种基于链表的可扩展、高性能、公平的自旋锁，直接前驱负责通知其结束自旋，从而极大地减少了不必要的处理器缓存同步的次数，
 * 降低了总线和内存的开销。
 *
 * <br/> Created on 2017/2/20 17:26
 *
 * @author 李超
 * @since 1.0.0
 */
public class MCSLock {
    private AtomicReference<MCSNode> tail = new AtomicReference<MCSNode>();
    private ThreadLocal<MCSNode> local = new ThreadLocal<MCSNode>() {
        @Override
        protected MCSNode initialValue() {
            return new MCSNode();
        }
    };

    public void lock() {

        MCSNode currentNode = local.get();
        //只为测试
        currentNode.setName(Thread.currentThread().getName());

        MCSNode preNode = tail.getAndSet(currentNode);
        String preName = preNode == null ? null : preNode.getName();
        System.out.println("currentNode:" + currentNode.getName() + ",preNode:" + preName);
        if(null != preNode) {
            currentNode.setLocked(true);
            preNode.setNext(currentNode);

            while(currentNode.isLocked()) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //这里可能需要稍微等一下，不然可能cpu转速太快，其他线程修改不了属性？由于属性一开始不是volatile
                //System.out.println(Thread.currentThread().getName()+" lock is "+currentNode.isLocked()+" preNode lock is "+preNode.isLocked());
            }
        }
    }

    public void unlock() {
        MCSNode currentNode = local.get();
        MCSNode nextNode = currentNode.getNext();
        if(null == nextNode) {
            if(tail.compareAndSet(currentNode, null)) {
                return;
            } else {
                //等到后来节点连接上本节点
                while(null != currentNode.getNext()) {

                }
            }
        }
        System.out.println(Thread.currentThread().getName() + " nextNode" + nextNode.getName() + " nextNode's lock is " + nextNode.isLocked());
        nextNode.setLocked(false);
        currentNode.setLocked(true);

        System.out.println(Thread.currentThread().getName() + " nextNode" + nextNode.getName() + " nextNode's lock is " + nextNode.isLocked());

    }


    public static void main(String[] args) {
        testIfCanSetPropertyWhenCpuIsRunning();
    }

    private static void testIfCanSetPropertyWhenCpuIsRunning() {
        final MCSNode aa = new MCSNode();
        aa.setLocked(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(aa.isLocked()) {
                    //这里如果注释掉，永远不停，开始以为可能当前线程太快导致不能顾及到已被修改，
                    // 后来想想jvm内存模型发现把属性设定volatile即可，应该由于volatile会使每个线程都重新获取下都能看到修改
                    //System.out.println("11111");
                }
            }
        });

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(8000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("2222");
                aa.setLocked(false);
            }
        });
        thread.start();
        thread1.start();
    }
}
