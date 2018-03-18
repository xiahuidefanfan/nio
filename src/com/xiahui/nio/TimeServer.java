package com.xiahui.nio;

public class TimeServer {
	public static void main(String[] args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
			try {
				port = Integer.valueOf(args[0]);
			}catch(Exception e) {
				System.out.println("����Ĭ�϶˿ں�");
			}
		}
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
	}
}
