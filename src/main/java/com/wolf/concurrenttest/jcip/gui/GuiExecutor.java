package com.wolf.concurrenttest.jcip.gui;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: Executor Built Atop SwingUtilities
 * an Executor that delegates tasks to SwingUtilities for execution.
 * Created on 2021/7/6 1:44 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GuiExecutor extends AbstractExecutorService {
    // singletons have a private constructor and a public factory
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {

    }

    public static GuiExecutor instance() {
        return instance;
    }

    @Override
    public void execute(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
