package com.wolf.concurrenttest.program.serverclient.concurrent;

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
    public String execute() {
        System.out.println("StopCommand execute...");
        ConcurrentServer.shutdown();
        return "StopCommand";
    }
}
