package com.wolf.concurrenttest.program.serverclient;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class StopCommand extends Command {

    public StopCommand(String[] command) {
        super(command);
    }

    @Override
    public String execute() {
        System.out.println("StopCommand execute...");
        return "StopCommand";
    }
}
