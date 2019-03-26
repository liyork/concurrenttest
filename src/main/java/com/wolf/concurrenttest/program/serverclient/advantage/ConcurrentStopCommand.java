package com.wolf.concurrenttest.program.serverclient.advantage;


import java.net.Socket;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ConcurrentStopCommand extends ConcurrentCommand {

    public ConcurrentStopCommand(String[] command) {
        super(command);
    }

    @Override
    protected byte getPriority() {
        return 0;
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public String execute() {
        System.out.println("StopCommand execute...");
        ConcurrentServer.shutdown();
        return "StopCommand";
    }
}
