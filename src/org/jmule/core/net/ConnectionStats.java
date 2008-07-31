/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.net;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.util.Average;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:47 $$
 */
public class ConnectionStats {

	  private long totalReceived = 0;
	  private long totalSent = 0;
	  private Average downloadSpeed = new Average(20);
	  private Average uploadSpeed = new Average(20);
	  private int activity = 0;
	  private long inactivityTime = 0;
	  private long startTime = 0;
	  private SpeedUpdaterThread speedUpdaterThread;
	  
	  public ConnectionStats() {
		  speedUpdaterThread = new SpeedUpdaterThread();
	  }
	  
	  public void startSpeedCounter() {
		  speedUpdaterThread = new SpeedUpdaterThread();
		  speedUpdaterThread.start();
	  }
	  
	  public void stopSpeedCounter() {
		  this.resetByteCount();
		  speedUpdaterThread.mustStop();
	  }
	  
	  public void reportActivity() {
		  activity = 0;
	  }
	  
	  public void reportInactivity() {
		  activity = 1;
		  this.inactivityTime = 0;
	  }
	  
	  public int getActivity() {
		  return this.activity;
	  }
	  
	  public void addInactivityTime(long AddTime) {
		  this.inactivityTime += AddTime;
	  }
	  
	  public long getInactivityTime() {
		  return this.inactivityTime;
	  }

	  
	  public void addReceivedBytes(long bytes) {
		  this.totalReceived += bytes;
	  }
	  
	  public void addSendBytes(long bytes) {
		  this.totalSent+=bytes;
	  }

	  public long getTotalReceived() {
		return totalReceived;
	  }

	  public long getTotalSent() {
		return totalSent;
	  }

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public float getDownloadSpeed() {
		return Math.round(this.downloadSpeed.getAverage());
	}
	
	public float getUploadSpeed() {
		return this.uploadSpeed.getAverage();
	}
	
	public void resetByteCount() {
		this.totalReceived = 0;
		this.totalSent = 0;
	}
	
	private class SpeedUpdaterThread extends JMThread {
		private boolean mustStop = false;
		
		public SpeedUpdaterThread() {
			super("Speed updater thread");
		}
		
		public void run() {
			long lastDownloadBytes = totalReceived;
			long lastDownloadTime = System.currentTimeMillis();
			
			long lastUploadBytes = totalSent;
			long lastUploadTime = System.currentTimeMillis();
			while(true) {
				try {
					this.join(ConfigurationManagerFactory.getInstance().SPEED_CHECK_INTERVAL);
					
					/** Update download speed**/
					long nowBytes = totalReceived;
					long transfered = nowBytes - lastDownloadBytes;
					lastDownloadBytes = nowBytes;
					
					long nowTime =  System.currentTimeMillis();
					long dTime = nowTime - lastDownloadTime;
					lastDownloadTime = nowTime;
					downloadSpeed.add(((float)transfered/dTime)*1000);
					
					/** Update upload speed **/
					nowBytes = totalSent;
					transfered = nowBytes - lastUploadBytes;
					lastUploadBytes = nowBytes;
					nowTime =  System.currentTimeMillis();
					dTime = nowTime - lastUploadTime;
					lastUploadTime = nowTime;
					uploadSpeed.add(((float)transfered/dTime)*1000);
					
				} catch (InterruptedException e) {
					if (mustStop) return;
				}
				
			}
		}
		
		public void mustStop() {
			mustStop = true;
			this.interrupt();
		}
	}
	
}
