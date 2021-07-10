package com.wolf.concurrenttest.jcip.customsync;

import net.jcip.annotations.ThreadSafe;

/**
 * Bounded buffer that balks when preconditions are not met
 * propagating precondition failure to caller, forcing the caller to manage the state dependence
 * makde up for by the substantial complication in using it
 * 满了/空了不满足前置条件则抛出异常，将异常抛给了客户端
 * 其真实的意思是重试，也可以用error value返回，这样就没有误用异常机制，不过调用者需要处理precondition failures
 */
@ThreadSafe
public class GrumpyBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    public GrumpyBoundedBuffer() {
        this(100);
    }

    public GrumpyBoundedBuffer(int size) {
        super(size);
    }

    // "buffer is full" is not an exceptional condition for a bounded buffer
    // 由于都用了synchronized，所以不会产生多放入导致超过buf.length的场景
    public synchronized void put(V v) throws BufferFullException {
        if (isFull()) throw new BufferFullException();
        doPut(v);
    }

    public synchronized V take() throws BufferEmptyException {
        if (isEmpty()) throw new BufferEmptyException();
        return doTake();
    }

    // client logic for calling GrumpyBoundedBuffer
    // 调用者也可以直接重试，或者休眠后重试，要去权衡了。
    public static void main(String[] args) throws InterruptedException {
        GrumpyBoundedBuffer<String> buffer = new GrumpyBoundedBuffer<>();
        int SLEEP_GRANULARITY = 50;

        while (true) {
            try {
                String item = buffer.take();
                // use item
                break;
            } catch (BufferEmptyException e) {//由于服务端抛出异常了，这里还得捕获并且重试
                Thread.sleep(SLEEP_GRANULARITY);
                //Thread.yield可以让出cpu，让别人执行，避免使用完整个cpu调度时间片
            }
        }
    }

    static class BufferFullException extends RuntimeException {
    }

    static class BufferEmptyException extends RuntimeException {
    }
}


