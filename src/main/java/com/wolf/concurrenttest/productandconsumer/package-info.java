package com.wolf.concurrenttest.productandconsumer;

//生产者消费者模型是所有线程通信的基础，
//通过他可以扩展线程池概念，即生产者不用等待直接放，消费者开多个线程取。
//synchronized是同步的意思，但是对于消费者，仅仅是同步在从list中取数据，而执行时没有同步概念。
//可能有时想问题多了，真正需要多个消费者一起并发高竞争时在考虑用concurrentlinkedqueue

//生产者消费者模型作用，平衡两者性能达到提高整体性能，解耦