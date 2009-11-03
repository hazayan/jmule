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

import static org.jmule.core.edonkey.E2DKConstants.OP_COMPRESSEDPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jmule.core.speedmanager.BandwidthController;
import org.jmule.core.speedmanager.SpeedManagerSingleton;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.8 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/03 07:25:15 $$
 */
class JMuleSocketChannel {
	private SocketChannel channel;
	private BandwidthController uploadController, downloadController;

	JMConnectionTransferStats file_transfer_trafic = new JMConnectionTransferStats();
	JMConnectionTransferStats service_trafic	   = new JMConnectionTransferStats();

	long transferred_bytes = 0;
	
	JMuleSocketChannel(SocketChannel channel) throws IOException {
		this.channel = channel;
		this.channel.configureBlocking(true);
		this.channel.socket().setKeepAlive(true);
		init();
	}
	
	float getDownloadSpeed() {
		return file_transfer_trafic.getDownloadSpeed();
	}
	
	float getUploadSpeed() {
		return file_transfer_trafic.getUploadSpeed();
	}
	
	float getServiceDownloadSpeed() {
		return service_trafic.getDownloadSpeed();
	}
	
	float getServiceUploadSpeed() {
		return service_trafic.getUploadSpeed();
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
		transferred_bytes += totalReaded;
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
		transferred_bytes += bytes;
		return bytes;

	}

	int write(ByteBuffer srcs) throws Exception {
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
		if (srcs.capacity()<1+4) return totalSended; // 0_0
		byte packet_opcode = srcs.get(1 + 4);
		if ((packet_opcode == OP_SENDINGPART)
				|| (packet_opcode == OP_COMPRESSEDPART)) {
			file_transfer_trafic.addReceivedBytes(totalSended);
		} else {
			service_trafic.addReceivedBytes(totalSended);
		}
		
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
