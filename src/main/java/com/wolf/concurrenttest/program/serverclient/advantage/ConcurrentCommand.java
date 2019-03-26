package com.wolf.concurrenttest.program.serverclient.advantage;

import com.wolf.concurrenttest.program.serverclient.Command;
import com.wolf.concurrenttest.program.serverclient.concurrent.ConcurrentServer;
import com.wolf.concurrenttest.program.serverclient.concurrent.Logger;
import com.wolf.concurrenttest.program.serverclient.concurrent.ParallelCache;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/21
 */
public abstract class ConcurrentCommand extends Command implements Runnable,
        Comparable<ConcurrentCommand> {

    private String username;
    private byte priority;
    private Socket socket;

    public ConcurrentCommand(String[] command, Socket socket) {
        super(command);
        this.username = command[1];
        this.priority = Byte.parseByte(command[2]);
        this.socket = socket;
    }

    public ConcurrentCommand(String[] command) {
        super(command);
    }

    @Override
    public void run() {

        String message = "Running a Task: Username: " + username +
                "Priority: " + priority;
        Logger.sendMessage(message);
        String ret = execute();
        ParallelCache cache = ConcurrentServer.getCache();
        if (isCacheable()) {
            cache.put(String.join(";", command), ret);
        }

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ret);
    }

    @Override
    public int compareTo(ConcurrentCommand o) {
        return Byte.compare(o.getPriority(),this.getPriority());
    }

    protected abstract byte getPriority();


    public  boolean isCacheable(){
        return true;}

    public  Socket getSocket(){
       return this.socket;
    }

    public  String getUsername(){
        return this.username;
    }
}
