package me.itzsomebody.spigotantipiracy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StreamHandler extends Thread {
	@Override
	public void run() {
		try {
			Main.logWriter("[Injector] Starting JAR Injector", true);
		
			// Let's make a socket to accept File Handling on
			@SuppressWarnings("resource")
			ServerSocket socket = new ServerSocket();
			
			// Make an address:port to bind to. Let's stay on 127.0.0.1 for safety
			InetSocketAddress address = new InetSocketAddress(InetAddress.getLoopbackAddress(), 35565);
			socket.bind(address);
			
			do {
                try {
                    do {
                        Socket fileProcessor = socket.accept();
                        new ProcessFile(fileProcessor).start();
                    } while (true);
                }
                catch (Throwable ex) {
                    ex.printStackTrace();
                    continue;
                }
            } while (true);
		} catch (IOException ioexc) {
			ioexc.printStackTrace();
		}
	}
}
