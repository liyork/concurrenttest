package com.wolf.concurrenttest.productandconsumer.manytomany.usersynchronized;

/**
 * <p> Description:这个不太靠谱，还有个未解决的问题。
 *
 * 整体来讲：
 * 生产者和消费者不是使用synchronized对整个方法进行锁定，
 * 那么就要考虑race condition：查看容量后再放入或删除。
 * 作者意思是想分开这两个条件成独立的小原子方法。
 *
 * 多个生产者进行放入，同步放入。若满了，一个生产者同步唤醒一个消费者，进入等待。
 * 多个消费者进行消费，同步放入。若可以，一个消费者同步唤醒一个生产者，进入等待。
 *
 * 锁是否太多了？
 *
 * 对于使用两把锁，是否有假死现象？
 * 应该不会：
 * 生产者是顺序执行的，消费者也是顺序执行的。可能并发问题就是生产者和消费者之间的操作
 * 生产者：若无则放入，若有则等待。消费者：若无则等待，若有则取出。
 * <p>
 * 生产者等待的前提是有，消费者等待的前提是无。
 * 允许某时刻生产者都在等待，因为满了，那么这时一旦有消费者消费那么必然通知所有生产者消费，
 * 而可能消费者也都同时一起等待吗？应该不可能，消费者等待的前提是没有数据，那么前面已经说了生产者已经放入了数据。
 * 而这个数据的读取和放入是原子的，一旦有放入必然取时能看到。
 * <p/>
 * Date: 2016/6/12
 * Time: 16:13
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestProducerAndConsumer {

    public static void main(String[] args) {
        Object producerMonitor = new Object();
        Object consumerMonitor = new Object();
        Container<Bread> container = new Container<Bread>(10);
        //生产者开动
        new Thread(new Producer(producerMonitor, consumerMonitor, container)).start();
        new Thread(new Producer(producerMonitor, consumerMonitor, container)).start();
        new Thread(new Producer(producerMonitor, consumerMonitor, container)).start();
        new Thread(new Producer(producerMonitor, consumerMonitor, container)).start();
        //消费者开动
        new Thread(new Consumer(producerMonitor, consumerMonitor, container)).start();
        new Thread(new Consumer(producerMonitor, consumerMonitor, container)).start();
    }
}
