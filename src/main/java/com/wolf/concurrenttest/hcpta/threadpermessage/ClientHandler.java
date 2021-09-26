package com.wolf.concurrenttest.hcpta.threadpermessage;

import java.io.*;
import java.net.Socket;

/**
 * Description: 处理客户端连接上来的请求
 * Created on 2021/9/25 9:30 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final String clientIdentify;

    public ClientHandler(final Socket socket) {
        this.socket = socket;
        this.clientIdentify = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public void run() {
        try {
            this.chat();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 任务执行结束，释放资源
            release();
        }
    }

    private void chat() throws IOException {
        BufferedReader bufferedReader = wrap2Reader(this.socket.getInputStream());
        PrintStream printStream = wrap2Print(this.socket.getOutputStream());

        String received;
        while ((received = bufferedReader.readLine()) != null) {
            System.out.printf("client:%s-message:%s\n", clientIdentify, received);
            if (received.equals("quit")) {
                write2Client(printStream, "client will close");
                socket.close();
                break;
            }
            write2Client(printStream, "Server:" + received);
        }
    }

    private BufferedReader wrap2Reader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private PrintStream wrap2Print(OutputStream outputStream) {
        return new PrintStream(outputStream);
    }

    private void write2Client(PrintStream print, String message) {
        print.println(message);
        print.flush();
    }

    private void release() {
        try {
            if (socket != null) {
                socket.close();
            }
        }
        // 若关闭客户端连接出现异常，则视为不可恢复的
        //catch (Throwable e) {
        //    // ignore
        //}
        catch (Throwable e) {
            if (socket != null) {
                // 包装成Tracker，当gc时再尝试一次close
                SocketCleaningTracker.tracker(socket);
            }
        }
    }
}
