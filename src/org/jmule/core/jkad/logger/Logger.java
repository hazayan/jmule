/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.jkad.logger;

import static org.jmule.core.utils.Convert.byteToHexString;
import static org.jmule.core.utils.Misc.getStackTrace;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * Created on Mar 1, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/25 10:15:00 $
 */
public class Logger {
	private static final String fileHeader 			= "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
	private static final String fileExtension 		= ".log";
	private static Logger singleton = null;
	
	public static Logger getSingleton() {
		if (singleton == null)
			singleton = new Logger();
		return singleton;
	}
	
	private String logFile;
	private Queue<String> eventsToWrite = new ConcurrentLinkedQueue<String>();
	private LogWriter writer = null;
	private Logger() {
		writer = new LogWriter();
	}
	
	public void start() {
		long time = System.currentTimeMillis();
		logFile = "./logs/jkad_"+time+fileExtension;
		writer.start();
		
	}
	
	public void stop() {
		writer.stopThread();
	}
	
	public void logPacket(String action,InetSocketAddress address,byte[] packetData){
		String record = "<packet "+timeToXML()+" action=\""+action+"\">\n";
		record += addressToXML(address);
		record += "<data>"+byteToHexString(packetData, " 0x")+"</data>\n";
		record += "</packet>\n";
		
		logEvent(record);
		
	}
	
	public void logMessage(String message) {
		String record = "<message "+timeToXML()+">\n";
		record += message+"\n";
		record += "</message>\n";
		logEvent(record);
	}
	
	public void logException(Throwable t) {
		String record = "<exception "+timeToXML()+">\n";
		record += "<![CDATA[";
		record += getStackTrace(t);
		record += "]]>";
		record += "</exception>\n";
		logEvent(record);
	}

	private String addressToXML(InetSocketAddress address) {
		String result = "<address>\n";
		result += "<ip>"+address.getAddress().toString()+"</ip>\n";
		result += "<port>"+address.getPort()+"</port>\n";
		result += "</address>\n";
				
		return result;
	}
	
	private String timeToXML() {
		return timeToXML(System.currentTimeMillis());
	}
	
	private String timeToXML(long time) {
		return "time=\""+time+"\"";
	}
	
	private void logEvent(String event) {
		eventsToWrite.add(event);
		if (!writer.isAlive()) {
			writer = new LogWriter();
			writer.start();
		}
	}
	
	private class LogWriter extends Thread {
		private boolean stop = false;
		public LogWriter() {
			super("Log writer");
		}
		
		public void run() {
			try {
				File tmp = new File(logFile);
				RandomAccessFile file;
				if (!tmp.exists()) {
						file = new RandomAccessFile(logFile,"rw");
						file.write(fileHeader.getBytes());
						file.write("<log>\n".getBytes());
					}
					else {
						file = new RandomAccessFile(logFile,"rw");
						file.getChannel().position(file.getChannel().size() -  "</log>".length()); 
					}
				
				while( ! stop ) {
					if (eventsToWrite.isEmpty()) {
						file.write("</log>".getBytes());
						file.close();
						return ;
					}
					
					String event = eventsToWrite.poll();
					try {
						file.write(event.getBytes());
					} catch (Throwable e) {
						e.printStackTrace();
						if (stop) {
							file.write("</log>".getBytes());
							file.close();
							return ;
						}
					}
				}
				file.write("</xml>".getBytes());
				file.close();
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
		
		public void stopThread() {
			stop = true;
			this.interrupt();
		}
	}	
}