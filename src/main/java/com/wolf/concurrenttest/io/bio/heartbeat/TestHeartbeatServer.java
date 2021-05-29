package com.wolf.concurrenttest.io.bio.heartbeat;

import com.wolf.utils.DateJudgeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p> Description:测试读超时
 * <p/>
 * Date: 2016/6/20
 * Time: 16:49
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class TestHeartbeatServer {

	private static Map<String, Long> lastAccessTimeMap = new HashMap<String, Long>();

	public static void main(String args[]) {

		try {
			ServerSocket server = null;
			try {
				server = new ServerSocket(4700);
			} catch (Exception e) {
				System.out.println("can not listen to:" + e);
			}

			Socket socket = null;

			try {
				socket = server.accept();
			} catch (Exception e) {
				System.out.println("Error." + e);
			}

			scheduleTestHeartbeat(socket);

			BufferedReader clientBr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String readLine = null;

			try {
				//客户端如果被关闭，卡在这里的会抛出异常
				readLine = clientBr.readLine();
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String hostAddress = getHostAddress(socket);
			updateAccessTime(hostAddress);

			while (!clientBr.equals("bye")) {

				//上面抛出异常，则这里打印null
				System.out.println("Client:" + readLine);
				//因为socket已经关闭，所以这里抛出异常Error:java.net.SocketException: socket closed
				readLine = clientBr.readLine();
				updateAccessTime(hostAddress);
			}

			clientBr.close(); //关闭Socket输入流

		} catch (Exception e) {
			System.out.println("Error:" + e);
		}

	}

	private static void updateAccessTime(String hostAddress) {
		lastAccessTimeMap.put(hostAddress, new Date().getTime());
	}

	private static long getAccessTime(String hostAddress, long currentTime) {
		Long result = lastAccessTimeMap.get(hostAddress);
		if (null == result) {
			lastAccessTimeMap.put(hostAddress, currentTime);
			result = currentTime;
		}
		return result;
	}

	private static void scheduleTestHeartbeat(final Socket socket) {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				System.out.println("test heartbeat");
				heartbeat(socket,new Date().getTime(),countDownLatch);
			}
		},2, 10, TimeUnit.SECONDS);

		shutdownScheduleIfNecessary(executorService,countDownLatch);
	}

	//实际业务中，可能会用这个单独线程去监听好多客户端,应该不用去停掉当前这个线程。
	// 下面方法模拟：如果关联的定时任务完事了，则关闭它。
	//todo 也可以将此线程设为守护线程,系统结束了则守护线程也停止了。
	private static void shutdownScheduleIfNecessary(final ScheduledExecutorService executorService, final CountDownLatch countDownLatch) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				executorService.shutdown();
			}
		}).start();
		
	}

	//todo 如何实现非阻塞模式的交互
	private static void heartbeat(Socket socket,long currentTime,CountDownLatch countDownLatch) {
		String hostAddress = getHostAddress(socket);
		long lastAccessTime = getAccessTime(hostAddress, currentTime);
		//超过1分钟主动关闭。
		if (DateJudgeUtils.ifExceedTime(lastAccessTime, currentTime, 1, TimeUnit.MINUTES)) {
			try {
				System.out.println("heartbeat time is timeout");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				countDownLatch.countDown();
			}
		} else {
			System.out.println("heartbeat time is not timeout");
		}
	}

	private static String getHostAddress(Socket socket) {
		InetAddress inetAddress = socket.getInetAddress();
		return inetAddress.getHostAddress();
	}

}
