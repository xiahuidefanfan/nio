package com.xiahui.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable{

	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;
	
	
	/**
	 * 构造器，资源初始化
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("The time server is start in port: " + port);
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void stop() {
		this.stop = true;
	}


	@Override
	public void run() {
		while(!stop) {
			try {
				// 每个一秒都被唤醒一次
				selector.select(1000);
				// 返回处于就绪状态的Channel时，返回其selectionKeys
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while(it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					}catch(Exception e) {
						if(key != null) {
							key.cancel();
							if(key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
		
	}
	
	private void handleInput(SelectionKey key) throws IOException{
		if(key.isValid()) {
			if(key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			if(key.isReadable()) {
				SocketChannel sc = (SocketChannel)key.channel();
				ByteBuffer readBufer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBufer);
				if(readBytes > 0) {
					readBufer.flip();
					byte[] bytes = new byte[readBufer.remaining()];
					readBufer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order:" + body);
					String cirrentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
							System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, cirrentTime);
				}else if(readBytes < 0){
					key.cancel();
					sc.close();
				}
			}
		}
	}
	
	private void doWrite(SocketChannel channel, String response) throws IOException{
		if(response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
	}

}
