package com.wolf.concurrenttest.program.serverclient;

/**
 * Description: 命令部件是dao部件和服务部件之间的中介
 *
 * @author 李超
 * @date 2019/02/20
 */
public abstract class Command {

    protected BizDao bizDao = new BizDao();

    protected final String[] command;

    public Command(String[] command) {
        this.command = command;
    }

    public abstract String execute();

    public boolean isCacheable() {
        return true;
    }
}
