package com.wolf.concurrenttest.queue;

/*
 * 阻塞。长度固定。FIFO,尾进，头出，
 * ArrayBlockingQueue，内部数组，一把锁控制，两个condition，count用int
 * LinkedBlockingQueue，内部额外增加node节点，使用两把锁提高吞吐,两个condition。count用AtomicInteger
 * PriorityBlockingQueue无界队列，优先级，数组存放,一把锁
 * DelayQueue内部有PriorityQueue,使用leader字段防止多个线程超时再产生竞争，这样每个人都按照队列分别被取出。minimize unnecessary timed waiting
 * SynchronousQueue
 *
 * 非阻塞
 * ConcurrentLinkedQueueTest
 * LinkedTransferQueueTest
 * */