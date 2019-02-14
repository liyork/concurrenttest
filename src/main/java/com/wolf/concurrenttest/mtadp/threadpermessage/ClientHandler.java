package com.wolf.concurrenttest.mtadp.threadpermessage;

import com.wolf.concurrenttest.mtadp.twophasetermination.CleaningTracker;

import java.io.*;
import java.net.Socket;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/05
 */
public class ClientHandler implements Runnable {

    private final Socket socket;

    private final String clientIdentify;

    public ClientHandler(Socket socket) {

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
            release();
        }
    }

    private void release() {

        if (null != socket) {
            try {
                socket.close();
            } catch (Throwable e) {//全部异常
                e.printStackTrace();
                if (null != socket) {//最大限度释放
                    CleaningTracker.track(socket);
                }
            }
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

    private void write2Client(PrintStream printStream, String message) {

        printStream.println(message);
        printStream.flush();
    }
}
