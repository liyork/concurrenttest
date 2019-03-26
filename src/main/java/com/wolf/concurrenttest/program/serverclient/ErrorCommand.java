package com.wolf.concurrenttest.program.serverclient;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ErrorCommand extends Command {

    public ErrorCommand(String[] command) {
        super(command);
    }

    @Override
    public String execute() {
        System.out.println("ErrorCommand execute...");
        return "ErrorCommand";
    }
}
