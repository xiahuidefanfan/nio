package com.xiahui.nio;

public class TimeClient {
	public static void main(String[] args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
			try {
				port = Integer.valueOf(args[0]);
			}catch(Exception e) {
				System.out.println("采用默认端口号");
			}
		}
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
	}
}
