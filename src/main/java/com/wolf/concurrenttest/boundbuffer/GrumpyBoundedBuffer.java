package com.wolf.concurrenttest.boundbuffer;

import net.jcip.annotations.*;

/**
 * GrumpyBoundedBuffer
 * <p/>
 * Bounded buffer that balks when preconditions are not met
 *
 * 满了/空了则抛出异常，将异常抛给了客户端
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class GrumpyBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    public GrumpyBoundedBuffer() {
        this(100);
    }

    public GrumpyBoundedBuffer(int size) {
        super(size);
    }

    public synchronized void put(V v) throws BufferFullException {
        if(isFull()) throw new BufferFullException();
        doPut(v);
    }

    public synchronized V take() throws BufferEmptyException {
        if(isEmpty()) throw new BufferEmptyException();
        return doTake();
    }


     class ExampleUsage {
        private GrumpyBoundedBuffer<String> buffer;
        int SLEEP_GRANULARITY = 50;

        void useBuffer() throws InterruptedException {
            while(true) {
                //由于服务端你抛出异常了，这里还得捕获并且重试
                try {
                    String item = buffer.take();
                    // use item
                    break;
                } catch (BufferEmptyException e) {
                    Thread.sleep(SLEEP_GRANULARITY);
                    //Thread.yield可以让出cpu，让别人执行，避免使用完整个cpu调度时间片
                }
            }
        }
    }

    static class BufferFullException extends RuntimeException {
    }

    static class BufferEmptyException extends RuntimeException {
    }
}


