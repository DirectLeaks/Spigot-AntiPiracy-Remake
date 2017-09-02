package me.itzsomebody.spigotantipiracy;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ProcessFile extends Thread implements Runnable{
	private Socket socket;
	
	public ProcessFile(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		// Need these in a certain scope
		String resourceID = null;
		BufferedReader reader = null;
		String path = null;
		
		try {
			this.socket.setSoTimeout(8000);
			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String[] originalFile = reader.readLine().split("\\|");
			path = originalFile[0].replace("\\", "/");
			String userID = originalFile[1];
			resourceID = originalFile[2];
			String antipiracylink = originalFile[3];
			
			File file = new File(path);
			
			if (file.exists()) {
				InputStream getInjected = new InjectAntiPiracy(file, userID, resourceID, antipiracylink).getInjectedFile();
				OutputStream outputstream = this.socket.getOutputStream();
				
				byte[] fileBuffer = new byte[1024];
				int readBuffer;
				
				while ((readBuffer = getInjected.read(fileBuffer)) >= 0) {
					outputstream.write(fileBuffer, 0, readBuffer);
				}
				
				getInjected.close();
				outputstream.close();
				
				Main.logWriter("[Injector] Successful injection - File = " + path, true);
				
				// Set the stuff to null in case some weird fluke happens
				getInjected = null;
				outputstream = null;
				originalFile = null;
			} else {
				Main.logWriter("[Injector] Unable to find File " + path, true);
			}
			
		} catch (Throwable throwable) {
			Main.logWriter("[Injector] Corrupted file - Resource ID = " + resourceID + " | File = " + path, true);
			// No stacktraces cause that makes the console a spammy mess
		} finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                }
                catch (Exception datas) {}
            }
            if (reader != null) {
                try {
                	reader.close();
                }
                catch (Exception datas) {}
            }
            this.socket = null;
            reader = null;
            System.gc();
        }
	}
}
