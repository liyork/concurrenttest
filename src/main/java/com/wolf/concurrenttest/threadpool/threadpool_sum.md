线程池糅合了对象池模型，但核心原理是生产者-消费者模型

ThreadPoolExecutor -- AbstractExecutorService -- ExecutorService -- Executor

FutureTask -- RunnableFuture -- Runnable/Future

不是每次执行任务时像池中借用线程，而是池中本身保存多个worker一直执行线程获取队列中的task，生产者消费者模型。

线程池的种类，通过Executors创建
FixedThreadPool：维持固定nThreads个线程的线程池；使用无界的异步阻塞队列LinkedBlockingQueue作为任务队列。
CachedThreadPool：维持最少0、最多Integer.MAX_VALUE个线程的线程池，限制线程可缓存60s，超时销毁；使用同步队列SynchronousQueue作为任务队列。
SingleThreadExecutor：维持固定1个线程的FixedThreadPool。
ScheduledThreadPool：维持固定corePoolSize个线程的线程池；使用无界的延迟队列DelayedWorkQueue(有优先概念)作为任务队列。
SingleThreadScheduledExecutor：维持固定1个线程的ScheduledThreadPool。
WorkStealingPool：并行度为parallelism的ForkJoinPool。

最简化公式
CPU 密集型应用：线程池大小设置为 N + 1
IO 密集型应用：线程池大小设置为 2N
为什么要有+1呢？
这是因为，就算是计算密集型任务，也可能存在缺页等问题（需要了解虚拟内存和物理内存的分配），产生“隐式”的IO。
多一个额外的线程能确保CPU时钟周期不会被浪费，又不至于增加太多线程调度成本。

线程切换的成本随线程数增加而增加。竞争激烈通常也会导致线程阻塞，使CPU空闲。
