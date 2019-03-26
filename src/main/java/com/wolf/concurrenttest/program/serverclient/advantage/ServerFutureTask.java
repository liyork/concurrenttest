package com.wolf.concurrenttest.program.serverclient.advantage;


import java.util.concurrent.FutureTask;

/**
 * Description: 添加比较功能，优先级队列需要
 *
 * @author 李超
 * @date 2019/02/21
 */
public class ServerFutureTask<V> extends FutureTask<V> implements Comparable<ServerFutureTask<V>> {

    private ConcurrentCommand command;

    public ServerFutureTask(Runnable command) {
        super(command,null);
        this.command = (ConcurrentCommand) command;
    }

    @Override
    public int compareTo(ServerFutureTask<V> other) {
        return command.compareTo(other.getCommand());
    }

    public ConcurrentCommand getCommand() {
        return command;
    }

    public void setCommand(ConcurrentCommand command) {
        this.command = command;
    }
}
