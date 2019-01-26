package com.wolf.concurrenttest.threadpool;

import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.util.concurrent.*;

/**
 * Description:Runnable, Future
 * FutureTask机制，内部有volatile的state，若未完成则使用LockSupport等待
 * <br/> Created on 2017/6/25 10:27
 *
 * @author 李超
 * @since 1.0.0
 */
public class FutureTaskTest {

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {

//        testException();
//        testHowWait();
        testCancel();
    }

    private static void testException() {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TakeTimeUtils.simulateLongTimeOperation(8000000);
                int a = 1;
                if(a == 1) {
                    throw new RuntimeException("xxx");
                }
                return "1234";
            }
        });

        Thread thread = new Thread(futureTask);
        thread.start();

        System.out.println("futureTask.get...before");
        try {
            String s = futureTask.get();//目标方法抛出异常则这里也会抛出ExecutionException
            System.out.println(s);
        } catch (InterruptedException e) {
            System.out.println("InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("ExecutionException");
            e.printStackTrace();
        }
        System.out.println("futureTask.get...after");
    }

    //修改超时时间可以测试各种情况
    //当前线程加入队列头部，因为是单链表，通知时从head开始向后
    //超时的node只移除thread为null的node,若是在第一个则更新waiters，否则前node指向next.next跳过当前线程节点。
    //每个超时节点有可能不仅删除自己node还有可能清除其他超时线程的node(其他线程已经设定thread=null但还未清除)
    //removeWaiter逻辑：先设定当前node的thread=null，从waiters开始，当前node的thread不为null则向后移动，
    // 当前node的thread为null时若前面node不为null，则更改连接跳过当前节点，若为null则更新waiters
    //finishCompletion逻辑：设定waiters=null，依次向后唤醒
    //整体逻辑：单线程run执行，其他线程加入队列等待，超时调用removeWaiter)，否则等待超时或者被run线程唤醒。
    private static void testHowWait() throws InterruptedException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TakeTimeUtils.simulateLongTimeOperation(8000000);
                Thread.sleep(100222220);
                return "1234";
            }
        });

        Thread thread = new Thread(futureTask);
        thread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = futureTask.get(22222,TimeUnit.SECONDS);
                    System.out.println(Thread.currentThread().getName()+" get "+s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = futureTask.get(2,TimeUnit.SECONDS);
                    System.out.println(Thread.currentThread().getName()+" get "+s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = futureTask.get(2,TimeUnit.SECONDS);
                    System.out.println(Thread.currentThread().getName()+" get "+s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //取消的线程设定INTERRUPTING : CANCELLED，唤醒其他等待节点。
    //被唤醒的节点抛出CancellationException
    //只有状态是NEW，即futuretask未完成时才能取消
    //futuretask运行时出错，若是NEW则设定outcome并唤醒，取消的就不做操作了
    private static void testCancel() throws InterruptedException, TimeoutException, ExecutionException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
//                try {
                    System.out.println(Thread.currentThread().getName() + " is running task");
//                TakeTimeUtils.simulateLongTimeOperation(8000000);
                    Thread.sleep(100222220);
//                } catch (Exception e) {//这个异常被框架吃了
//                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                    throw e;
//                }
                return "1234";
            }
        });

        Thread thread = new Thread(futureTask);
        thread.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = futureTask.get(200220, TimeUnit.SECONDS);
                    System.out.println(Thread.currentThread().getName()+" get "+s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1000);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                boolean cancel = futureTask.cancel(false);//对于已完成的返回false
                boolean cancel = futureTask.cancel(true);
                System.out.println(cancel);
            }
        }).start();

        futureTask.get(200220, TimeUnit.SECONDS);//状态>= CANCELLED，直接抛出异常

    }
}
