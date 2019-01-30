package com.wolf.concurrenttest.threadpool.customize.demo2;

/**
 * Description: worker达到最大且任务队列已满
 * <br/> Created on 2019-01-27
 *
 * @author 李超
 * @since 1.0.0
 */
@FunctionalInterface
public interface DenyPolicy {

    void reject(Runnable runnable, ThreadPool threadPool);

    class DiscardDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            //ignore
        }
    }

    class AbortDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {

            throw new TaskDenyException("the runnable " + runnable + " will be abort.");
        }
    }

    //提交者线程执行任务
    class RunnerDenyPolicy implements DenyPolicy {
        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {

            if (!threadPool.isShutdown()) {//再次判断
                runnable.run();
            }
        }
    }

}
