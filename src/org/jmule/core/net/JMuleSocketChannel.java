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

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jmule.core.speedmanager.BandwidthController;
import org.jmule.core.speedmanager.SpeedManager;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:33:57 $$
 */
public class JMuleSocketChannel {
	private SocketChannel channel;
	private BandwidthController uploadController, downloadController;
	
	public JMuleSocketChannel(SocketChannel channel){
		this.channel = channel;
		init();
	}
	
	public SocketChannel getChannel() {
		return channel;
	}
	
	private void init() {
		uploadController = SpeedManager.getInstance().getUploadController();
		downloadController = SpeedManager.getInstance().getDownloadController();
	}

	public void close() throws IOException {
		this.channel.close();
	}
	
	public  int read(ByteBuffer dsts) throws IOException,JMEndOfStreamException,InterruptedException{
		if (downloadController.getThrottlingRate()==0) {
				dsts.position(0);
				return this.read(dsts, dsts.capacity());
		}
		int totalReaded = 0;
		int cacheLimit = (int) Math.max(downloadController.getThrottlingRate(), dsts.capacity());
		ByteBuffer readCache = Misc.getByteBuffer(cacheLimit);
		dsts.position(0);
		do {
			int mustRead = downloadController.getAvailableByteCount(dsts.remaining(),true);
			readCache.position(0);
			readCache.limit(mustRead);
			
			read(readCache,mustRead);
			int readedData = readCache.limit();
			totalReaded += readedData;
			downloadController.markBytesUsed(readedData);
			dsts.put(readCache.array(),0,readedData);
		}while(dsts.hasRemaining());
	
		return totalReaded;
	}
	
	private int read(ByteBuffer dsts,int bytes) throws IOException,JMEndOfStreamException, InterruptedException {
		ByteBuffer readCache = Misc.getByteBuffer(bytes);
		int mustRead = bytes;
		do {
			readCache.limit(mustRead);
			readCache.position(0);
			int readedData = 0;

			readedData = channel.read(readCache); // return -1 if socket is closed
			if (readedData==-1) {
					throw new JMEndOfStreamException();
				}
						
			mustRead-=readedData;
			readCache.limit(readedData);
			readCache.position(0);
			dsts.put(readCache);
		}while(mustRead>0);
		return bytes;
		
	}
	
	public int write(ByteBuffer srcs) throws Exception {
		srcs.position(0);
		int totalSended = 0;
		
		do {
			int numBytes;
			if (uploadController.getThrottlingRate()!=0)
				numBytes = uploadController.getAvailableByteCount(srcs.remaining(),true);
			else numBytes = srcs.remaining();
		
			int ipos = srcs.position();
			ByteBuffer toSend = Misc.getByteBuffer(numBytes);
			srcs.get(toSend.array(),0, numBytes);
			toSend.position(0);
		
			int sended = channel.write(toSend);
			if (sended==-1) {
				return -1;
			}
		
			srcs.position(ipos+sended);
			totalSended+=sended;
			if (uploadController.getThrottlingRate()!=0)
				uploadController.markBytesUsed(sended);
			}while(totalSended<srcs.capacity());
		
		return totalSended;
	}

	public boolean isOpen() {
		if (channel==null) return false;
		return channel.isOpen();
	}
	
	public Socket getSocket() {
		if (channel==null) return null;
		return channel.socket();
	}
	
	public boolean isConnected() {
		if (channel==null) return false;
		return channel.isConnected();
	}
	
	public void configureBlocking(boolean value) throws IOException{
		channel.configureBlocking(value);
	}
	
	public void disconnect(){
		
		try {
			channel.socket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
