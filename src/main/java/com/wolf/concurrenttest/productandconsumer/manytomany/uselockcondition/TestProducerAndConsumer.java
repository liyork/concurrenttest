package com.wolf.concurrenttest.productandconsumer.manytomany.uselockcondition;

/**
 * <p> Description:
 * 使用两把锁，避免生产者/消费者会把生产者和消费者都唤醒
 *
 * 多个消费者，多个生产者,使用两个锁,而不是使用一个，否则生产者唤醒了所有的生产者和消费者。。再次进入惊群效应
 * wait方法类似于谁调用wait,就将当前那个线程持有锁释放，并放入那个对象的等待池中，等待别人唤醒，
 * this.wait,当前线程释放持有的当前对象的锁，xx.wait，当前线程释放持有的xx对象的锁。
 * 等待的和释放的要是一个对象,不然不同步
 *
 * 相比较一把锁，有进步，但是生产者之间还是要顺序执行，消费者之间也要顺序执行，是否可以使用cas进行非阻塞操作同一个集合？
 *
 * 相对于原生synchronized能尝试获取锁，有超时，有中断
 * <p/>
 * Date: 2016/6/12
 * Time: 15:12
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestProducerAndConsumer {

    public static void main(String[] args) {
        // 仓库对象
        Storage storage = new Storage();

        // 生产者对象
        Producer p1 = new Producer(storage);
        Producer p2 = new Producer(storage);
        Producer p3 = new Producer(storage);
        Producer p4 = new Producer(storage);
        Producer p5 = new Producer(storage);
        Producer p6 = new Producer(storage);
        Producer p7 = new Producer(storage);

        // 消费者对象
        Consumer c1 = new Consumer(storage);
        Consumer c2 = new Consumer(storage);
        Consumer c3 = new Consumer(storage);

        // 设置生产者产品生产数量
        p1.setNum(10);
        p2.setNum(10);
        p3.setNum(10);
        p4.setNum(10);
        p5.setNum(10);
        p6.setNum(10);
        p7.setNum(80);

        // 设置消费者产品消费数量
        c1.setNum(50);
        c2.setNum(20);
        c3.setNum(30);

        // 线程开始执行
        c1.start();
        c2.start();
        c3.start();
        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
        p6.start();
        p7.start();
    }
}
