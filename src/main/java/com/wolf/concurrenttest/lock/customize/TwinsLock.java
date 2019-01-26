package com.wolf.concurrenttest.lock.customize;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;

/**
 * Description:
 * <br/> Created on 3/11/18 5:08 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TwinsLock{

    private Syn syn = new Syn(2);

    public void lock() {
        syn.acquireShared(1);
    }

    public void unlock() {
        syn.releaseShared(1);
    }

    private class Syn extends AbstractQueuedSynchronizer {

        Syn(int lockCount) {
            if (lockCount != 2) {
                throw new IllegalArgumentException("lockCount must equal 2, param lockCount:" + lockCount);
            }
            setState(2);//若未设定，默认0都等待。
        }

        @Override
        protected int tryAcquireShared(int reduceCount) {

            while (true) {
                int state = getState();//这瞬间以后就是操作局部变量了。这操作之后若有人更改了state，那么只能下面设定失败，然后循环执行。
                int remainCount = state - reduceCount;
                //小于0表示没有可用资源，未cas则有竞争重新设定。
                if (remainCount < 0 || compareAndSetState(state, remainCount)) {//由state和cas保证只能让state>=0
                    //System.out.println(Thread.currentThread().getName()+" getLock ,state:"+state+" "+",remainCount:"+remainCount);
//                    return remainCount;//开始时写成remainCount，永远返回1，那么当然锁不住了，还说呢日志Thread-1 getLock ,state:0 ,remainCount:-1没有问题啊。
                    return remainCount;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int returnCount) {
            while (true) {
                int state = getState();
                int remainCount = state + returnCount;
                //remainCount > 2，也许不用这么判断，因为release应该是单线程的，只有获取锁的才可以释放。加上防止溢出。
                if (remainCount > 2 || compareAndSetState(state, remainCount)) {
                    //System.out.println(Thread.currentThread().getName()+" releaseLock ,state:"+state+" "+",remainCount:"+remainCount);
                    return true;
                }
            }
        }
    }
}
