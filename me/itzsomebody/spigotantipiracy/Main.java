package me.itzsomebody.spigotantipiracy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {
	public static Logger logger = Logger.getLogger(Main.class.getName());
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss.SSS-z");
	
	public static void main(String[] args) {
		LogHandler();
		StreamHandler streamhandler = new StreamHandler();
		streamhandler.start();
	}
	
	private static void LogHandler() {
		try {
            FileHandler logHandle = new FileHandler("InjectorLog.txt", false);
            Formatter formatter = new Formatter(){

                @Override
                public String format(LogRecord record) {
                    return format.format(new Date(record.getMillis())) + " - " + this.formatMessage(record) + "\n";
                }
            };
            logHandle.setFormatter(formatter);
            logger.addHandler(logHandle);
            logger.setUseParentHandlers(false);
        } catch (IOException ioexc) {
            ioexc.printStackTrace();
        }
	}
	
	public static void logWriter(String str, boolean shouldWePrintToConsole) {
        logger.info(str);
        if (shouldWePrintToConsole) {
            System.out.println(str);
        }
    }
	
	private static void start() {
        StreamHandler streamhandler = new StreamHandler();
        streamhandler.start();
    }
}
