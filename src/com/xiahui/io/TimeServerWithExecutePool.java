package com.xiahui.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServerWithExecutePool {
		
	public static void main(String[] args) {
		int port = 8080;
		if(args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port :" + port);
			Socket socket = null;
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(
					50,10000);
			while(true) {
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(server != null){
				System.out.println("The time server close");
				try {
					server.close();
					server = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
