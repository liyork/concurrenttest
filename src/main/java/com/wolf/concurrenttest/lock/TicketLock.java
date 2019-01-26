package com.wolf.concurrenttest.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:Ticket Lock 是为了解决SpinLock的公平性问题
 * 缺点：多处理器系统上，每个进程/线程占用的处理器都在读写同一个变量serviceNum ，
 * 每次读写操作都必须在多个处理器缓存之间进行缓存同步，这会导致繁重的系统总线和内存的流量，大大降低系统整体的性能。MCSLock解决这个问题
 * <br/> Created on 2017/2/20 17:26
 *
 * @author 李超
 * @since 1.0.0
 */
public class TicketLock {
    private AtomicInteger serviceNum = new AtomicInteger(); // 服务号
    private AtomicInteger ticketNum = new AtomicInteger(); // 排队号
    private ThreadLocal<Integer> currentTicketNum = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return -1;
        }
    };

    public int lock() {
        // 首先原子性地获得一个排队号，相对于SpinLock每个竞争者只调用1次原子操作
        int myTicketNum = ticketNum.getAndIncrement();
        currentTicketNum.set(myTicketNum);

        // 只要当前服务号不是自己的就不断轮询
        while(serviceNum.get() != myTicketNum) {
        }

        return myTicketNum;
    }

    public void unlock() {
        Integer myTicket = currentTicketNum.get();
        // 只有当前线程拥有者才能释放锁
        int next = myTicket + 1;
        //serviceNum.compareAndSet(myTicket, next);//既然只有当前线程才能释放锁，应该就不用比较设定了，除非是只调用释放锁
        serviceNum.set(next);
    }
}
