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
package org.jmule.core.networkmanager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.speedmanager.BandwidthController;
import org.jmule.core.speedmanager.SpeedManagerSingleton;
import org.jmule.core.utils.Average;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/24 05:11:41 $$
 */
class JMuleSocketChannel {
	private SocketChannel channel;
	private BandwidthController uploadController, downloadController;

	private Average<Integer> download_trafic 				= new Average<Integer>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	private Average<Integer> upload_trafic 					= new Average<Integer>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	private Average<Integer> service_download_trafic 		= new Average<Integer>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	private Average<Integer> service_upload_trafic 			= new Average<Integer>(ConfigurationManager.CONNECTION_TRAFIC_AVERAGE_CHECKS);
	
	JMuleSocketChannel(SocketChannel channel) throws IOException {
		this.channel = channel;
		this.channel.configureBlocking(true);
		this.channel.socket().setKeepAlive(true);
		init();
	}
	
	float getDownloadSpeed() {
		return download_trafic.getAverage();
	}
	
	float getUploadSpeed() {
		return upload_trafic.getAverage();
	}
	
	float getServiceDownloadSpeed() {
		return service_download_trafic.getAverage();
	}
	
	float getServiceUploadSpeed() {
		return service_upload_trafic.getAverage();
	}

	void connect(InetSocketAddress address) throws IOException {
		channel.connect(address);
	}

	void connect(InetSocketAddress address, int connectingTimeout)
			throws IOException {
		channel.socket().connect(address, connectingTimeout);
	}

	SocketChannel getChannel() {
		return channel;
	}

	private void init() {
		uploadController = SpeedManagerSingleton.getInstance()
				.getUploadController();
		downloadController = SpeedManagerSingleton.getInstance()
				.getDownloadController();
	}

	void close() throws IOException {
		this.channel.close();
	}

	int read(ByteBuffer packetBuffer) throws IOException, JMEndOfStreamException {
		if (downloadController.getThrottlingRate() == 0) {
			packetBuffer.position(0);
			return this.read(packetBuffer, packetBuffer.capacity());
		}
		int totalReaded = 0;
		int cacheLimit = (int) Math.max(downloadController.getThrottlingRate(),
				packetBuffer.capacity());
		ByteBuffer readCache = Misc.getByteBuffer(cacheLimit);
		packetBuffer.position(0);
		do {
			int mustRead = downloadController.getAvailableByteCount(packetBuffer
					.remaining(), true);
			readCache.position(0);
			readCache.limit(mustRead);

			read(readCache, mustRead);
			int readedData = readCache.limit();
			totalReaded += readedData;
			downloadController.markBytesUsed(readedData);
			packetBuffer.put(readCache.array(), 0, readedData);
		} while (packetBuffer.hasRemaining());
		if (totalReaded <= 5)
			return totalReaded;
		if (packetBuffer.get(1 + 4) == E2DKConstants.OP_SENDINGPART)
			download_trafic.add(packetBuffer.capacity());
		else
			service_download_trafic.add(packetBuffer.capacity());

		return totalReaded;
	}

	private int read(ByteBuffer packetBuffer, int bytes) throws IOException,
			JMEndOfStreamException {
		ByteBuffer readCache = Misc.getByteBuffer(bytes);
		int mustRead = bytes;
		do {
			readCache.limit(mustRead);
			readCache.position(0);
			int readedData = 0;

			readedData = channel.read(readCache); // return -1 if socket is
													// closed
			if (readedData == -1) {
				throw new JMEndOfStreamException();
			}

			mustRead -= readedData;
			readCache.limit(readedData);
			readCache.position(0);
			packetBuffer.put(readCache);
		} while (mustRead > 0);
		if (bytes <= 5)
			return bytes;
		if (packetBuffer.get(1 + 4) == E2DKConstants.OP_SENDINGPART)
			download_trafic.add(packetBuffer.capacity());
		else
			service_download_trafic.add(packetBuffer.capacity());
		return bytes;

	}

	int write(ByteBuffer srcs) throws Exception {
		if (srcs.capacity() > 5) {
			if (srcs.get(1 + 4) == E2DKConstants.OP_SENDINGPART)
				upload_trafic.add(srcs.capacity());
			else
				service_upload_trafic.add(srcs.capacity());
		}
		srcs.position(0);
		int totalSended = 0;

		do {
			int numBytes;
			if (uploadController.getThrottlingRate() != 0)
				numBytes = uploadController.getAvailableByteCount(srcs
						.remaining(), true);
			else
				numBytes = srcs.remaining();

			int ipos = srcs.position();
			ByteBuffer toSend = Misc.getByteBuffer(numBytes);
			srcs.get(toSend.array(), 0, numBytes);
			toSend.position(0);

			int sended = channel.write(toSend);
			if (sended == -1) {
				return -1;
			}

			srcs.position(ipos + sended);
			totalSended += sended;
			if (uploadController.getThrottlingRate() != 0)
				uploadController.markBytesUsed(sended);
		} while (totalSended < srcs.capacity());

		return totalSended;
	}

	boolean isOpen() {
		if (channel == null)
			return false;
		return channel.isOpen();
	}

	Socket getSocket() {
		if (channel == null)
			return null;
		return channel.socket();
	}

	boolean isConnected() {
		if (channel == null)
			return false;
		return channel.isConnected();
	}

	void configureBlocking(boolean value) throws IOException {
		channel.configureBlocking(value);
	}

	void disconnect() {
		try {
			channel.socket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
