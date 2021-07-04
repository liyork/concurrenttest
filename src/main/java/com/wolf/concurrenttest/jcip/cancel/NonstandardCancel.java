package com.wolf.concurrenttest.jcip.cancel;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Description: 针对阻塞I/O进行取消
 * Created on 2021/7/3 3:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
class NonstandardCancel {
    // encapsulating nonstandard cancellation in a thread by ovrriding interrup
    // 由于socket读写不响应interrupt，所以close
    class ReaderThead extends Thread {
        private static final int BUFSIZE = 1024;
        private final Socket socket;
        private final InputStream in;

        public ReaderThead(Socket socket) throws IOException {
            this.socket = socket;
            this.in = socket.getInputStream();
        }

        @Override
        public void interrupt() {
            try {
                socket.close();
            } catch (IOException ignored) {
            } finally {
                super.interrupt();
            }
        }

        @Override
        public void run() {
            try {
                byte[] buf = new byte[BUFSIZE];
                while (true) {
                    int count = in.read(buf);
                    if (count < 0) {
                        break;
                    } else if (count > 0) {
                        processBuffer(buf, count);
                    }
                }
            } catch (IOException e) {
                // allow thread to exit
            }
        }

        private void processBuffer(byte[] buf, int count) {
        }
    }

    // 测试更好的方式使用
    public static void main(String[] args) {
        new CancellingExecutor(1, 1, 111, TimeUnit.SECONDS, new ArrayBlockingQueue(1))
                .submit(new BlockIOTask());
    }
}

// 更好的方式实现阻塞io的读写不响应interrupt问题
// Encapsulating Nonstandard Cancellation in a Task with NewtaskFor
interface CancellableTask<T> extends Callable<T> {
    void cancel();

    RunnableFuture<T> newTask();
}

// 可取消的Executor
@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    // hook is a factory method that creates the Future representing the task
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask) {
            return ((CancellableTask<T>) callable).newTask();
        } else {
            return super.newTaskFor(callable);
        }
    }
}

// 公共task实现
abstract class SocketUsingTask<T> implements CancellableTask<T> {
    @GuardedBy("this")
    private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    @Override
    public synchronized void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {

        }
    }

    // 线程池调用时，包装callable为FutureTask，重写cancel调用自己的cancel
    @Override
    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}

class BlockIOTask extends SocketUsingTask {
    @Override
    public Object call() throws Exception {
        TimeUnit.SECONDS.sleep(5);
        return "11";
    }
}