package com.wolf.concurrenttest.jcip.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.*;

/**
 * Description: 针对单线程处理事件场景下的，长任务处理方式
 * Created on 2021/7/7 6:16 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LongRunTask {
    // binding a long-running task to a visual component.
    private ExecutorService backgroudExec = Executors.newCachedThreadPool();

    // 长任务交给后台线程池处理
    public void fireAndForget() {
        Button button = null;
        button.addActionListener((actionEvent) -> {
            backgroudExec.execute(() -> {
                doBigComputation();
            });
        });
    }

    private void doBigComputation() {
    }

    // 长时间执行任务，由外部线程池处理，最后结果还是交给eventloop线程串行处理
    // long-running tasks with user feedback
    public void fireAndFeedback() {
        Button button = null;
        Label label = null;
        button.addActionListener((actionEvent) -> {
            // in event loop thread
            button.setEnabled(false);
            label.setText("busy");
            // background thread
            backgroudExec.execute(() -> {
                try {
                    doBigComputation();
                } finally {
                    // in event loop thread
                    GuiExecutor.instance().execute(() -> {
                        button.setEnabled(true);
                        label.setText("idle");
                    });
                }
            });
        });
    }

    // cancelling a long-running task
    public void cancelTask() throws InterruptedException {
        final Future<?>[] runningTask = {null}; // thread-confined to the event thread
        Button startButton = null;
        // start button listener ensures that only one background task is running at a time
        startButton.addActionListener((actionEvent) -> {
            if (runningTask[0] != null) {
                runningTask[0] = backgroudExec.submit(() -> {
                    while (moreWork()) {
                        if (Thread.currentThread().isAlive()) {
                            cleanUpPartialWork();
                            break;
                        }
                        doSomeWork();
                    }
                });
            }
        });

        TimeUnit.SECONDS.sleep(2);
        Button cancelButton = null;
        cancelButton.addActionListener((actionEvent) -> {
            if (runningTask != null) {
                runningTask[0].cancel(true);
            }
        });
    }

    private void doSomeWork() {
    }

    private void cleanUpPartialWork() {
    }

    private boolean moreWork() {
        return true;
    }

    // background task class supporting cancellation, completion notification, and progress notification
    abstract class BackgroundTask<V> implements Runnable, Future<V> {
        private final FutureTask<V> computation = new Computation();

        private class Computation extends FutureTask<V> {
            public Computation() {
                super(() -> BackgroundTask.this.compute());
            }

            @Override
            protected final void done() {
                GuiExecutor.instance().execute(() -> {
                    V value = null;
                    Throwable thrown = null;
                    boolean cancelled = false;
                    try {
                        value = get();
                    } catch (ExecutionException e) {
                        thrown = e.getCause();
                    } catch (CancellationException e) {
                        cancelled = true;
                    } catch (InterruptedException consumed) {
                    } finally {
                        onCompletion(value, thrown, cancelled);
                    }
                });
            }
        }

        protected void setProgress(final int current, final int max) {
            GuiExecutor.instance().execute(() -> {
                onProgress(current, max);
            });
        }

        // called in the background thread
        protected abstract V compute() throws Exception;

        // called in the event thread
        protected void onCompletion(V result, Throwable exception, boolean cancelled) {

        }

        protected void onProgress(int current, int max) {

        }
    }

    // 后台执行task，设定取消button的事件
    // initiating a long-running, cancellable task with backgroundTask
    public void runInBackground(Runnable task) {
        Button startButton = null;
        Button cancelButton = null;
        Label label = null;
        startButton.addActionListener((ActiveEvent) -> {
            // 可取消，可监控进度的listener，借助BackgroundTask实现
            class CancelListener implements ActionListener {
                BackgroundTask<?> task;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (task != null) {
                        task.cancel(true);
                    }
                }
            }

            final CancelListener listener = new CancelListener();
            listener.task = new BackgroundTask<Void>() {
                @Override
                protected Void compute() throws Exception {
                    while (moreWork() && !isCancelled()) {
                        doSomeWork();
                    }
                    return null;
                }

                @Override
                public void run() {

                }

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return false;
                }

                @Override
                public Void get() throws InterruptedException, ExecutionException {
                    return null;
                }

                @Override
                public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return null;
                }

                @Override
                protected void onCompletion(Void result, Throwable exception, boolean cancelled) {
                    cancelButton.removeActionListener(listener);
                    label.setText("done");
                }

                @Override
                protected void onProgress(int current, int max) {
                }
            };
            cancelButton.addActionListener(listener);
            backgroudExec.execute(task);
        });
    }
}


