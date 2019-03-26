package com.wolf.concurrenttest.tooltest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * fork/join框架，从一个任务开始，内部将分割成多个子任务，之后再一次次分割，直到问题达到想要的规模。拆分之后执行子任务并等待，然后一层层返回，
 * 也可以进行取消。
 *
 * Fork/Join 框架还有另一个关键特性，即工作窃取算法。该算法确定要执行的任务。当一个任务使用 join()方法等待
 * 某个子任务结束时，执行该任务的线程将会从任务池中选取另一个等待执行的任务并且开始执行。通过这种方式，Fork/Join 执
 * 行器的线程总是通过改进应用程序的性能来执行任务
 * <p>
 * fork/join解决问题时必须要考虑到它的局限性：
 * 不再进行细分的基本问题的规模既不能过大也不能过小。按照 Java API 文档的说明，该基本问题的规模应该介于 100 到 10 000 个基本计算步骤之间。
 * 数据可用前，不应使用阻塞型 I/O 操作，例如读取用户输入或者来自网络套接字的数据，这样的操作将占用CPU核资源并空闲等待，降低并行处理等级，进而使性能无法达到最佳。
 * 不能在任务内部抛出校验异常，必须编写代码来处理异常（例如，陷入未经校验的RuntimeException）
 * <p>
 * RecursiveTask有返回值，RecursiveAction无返回值
 *
 * 分治设计。基于fork创建一个新子任务，基于join操作在获取结果前等待子任务结束。
 * <p>
 * <br/> Created on 12/03/2018 8:19 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class ForkJoinTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        testBase();
//        testStep();
        testPending();
    }

    private static void testBase() throws InterruptedException, ExecutionException {

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask countTask = new CountTask(1, 4);
        ForkJoinTask<Integer> result = forkJoinPool.submit(countTask);

//        result.cancel(true);
        Thread.sleep(2000);//让线程执行会

        System.out.println("countTask.isCompletedAbnormally():" + countTask.isCompletedAbnormally());
        System.out.println("result.isCompletedAbnormally():" + result.isCompletedAbnormally());

        boolean completedAbnormally = result.isCompletedAbnormally();
        if (completedAbnormally) {
            Throwable exception = result.getException();
            System.out.println(exception);
        } else {
            System.out.println("result:" + result.get());//异常或者CancellationException或者null
        }
    }

    //分治算法，计算start——end之间的数字之和
    private static class CountTask extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 2;

        private int start;
        private int end;

        public CountTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            System.out.println(Thread.currentThread().getName() + " start:" + start + " end" + end);
            int sum = 0;

            if (end - start < THRESHOLD) {//只计算两个数
                for (int i = start; i <= end; i++) {
                    sum += i;
                    //throw new RuntimeException("test for join thread exception");
                }
            } else {//太多则新启任务执行
                int middle = (start + end) / 2;
                CountTask countTaskLeft = new CountTask(start, middle);
                CountTask countTaskRight = new CountTask(middle + 1, end);

                //将子任务发送给 Fork/Join 执行器
                countTaskLeft.fork();
                countTaskRight.fork();

                //等待一个任务执行结束后返回其结果
                Integer leftResult = countTaskLeft.join();
                Integer rightResult = countTaskRight.join();

                sum = leftResult + rightResult;
            }
            return sum;
        }
    }

    private static void testStep() {

        int batch = 10;
        int total = 100;

        int start = 0;
        int end;
        int step = total / batch;

        while (start < total) {
            end = start + step;
            if (end > 100) {
                end = 100;
            }
            System.out.println(start + "-" + end);
            start += step + 1;
        }
    }

    private static void testPending() {

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        CountTask1 countTask = new CountTask1();
        forkJoinPool.submit(countTask);
    }

    private static class CountTask1 extends CountedCompleter<Void> {

        AtomicInteger count = new AtomicInteger(0);

        @Override
        public void compute() {

            if (count.get() != 4) {
                System.out.println(Thread.currentThread().getName() + " addToPendingCount");
                addToPendingCount(1);

                CountTask1 countTask1 = new CountTask1();
                System.out.println(Thread.currentThread().getName() + " countTask1.fork");
                countTask1.fork();

                CountTask1 countTask2 = new CountTask1();
                System.out.println(Thread.currentThread().getName() + " countTask2.fork");
                countTask2.fork();
                count.incrementAndGet();
            } else {
                System.out.println(Thread.currentThread().getName() + " count =4");
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                tryComplete();
            }
        }

        @Override
        public void onCompletion(CountedCompleter<?> caller) {
            System.out.println("CountTask1 onCompletion");
        }
    }
}
