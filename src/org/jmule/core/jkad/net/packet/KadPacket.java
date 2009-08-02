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
package org.jmule.core.jkad.net.packet;

import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_COMPRESSED_UDP;
import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_UDP;
import static org.jmule.core.utils.Convert.shortToInt;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.logger.Logger;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.utils.JMuleZLib;

/**
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/02 08:10:19 $
 */
public class KadPacket implements UDPPacket {
	private InetSocketAddress sender;
	private ByteBuffer packet_data = null;
	
	public KadPacket(ByteBuffer packetData) {
		packet_data = packetData;
	}
	
	public KadPacket(ByteBuffer packetData,InetSocketAddress sender) {
		packet_data = packetData;
		this.sender = sender;
	}
	
	public KadPacket(byte opCode) {
		this(opCode,0);
	}
	
	public KadPacket(byte opCode,int packetLength) {
		packet_data = getByteBuffer(1 + 1 + packetLength);
		packet_data.put(PROTO_KAD_UDP);
		packet_data.put(opCode);
	}
		
	public void compress() {
		if (isCompressed()) return;
	}
	
	public void decompress() {
		if (!isCompressed()) return;
		ByteBuffer compressedData = getByteBuffer(packet_data.limit() - 2);
		packet_data.position(2);
		packet_data.get(compressedData.array());
		packet_data.position(0);
		try {
			ByteBuffer decompressedData = JMuleZLib.decompressData(compressedData);
			decompressedData.position(0);
			
			byte packetOPCode = packet_data.get(1);
			packet_data = getByteBuffer(decompressedData.capacity()+2);

			packet_data.put(PROTO_KAD_UDP);
			packet_data.put(packetOPCode);
			packet_data.put(decompressedData);
			
		} catch (DataFormatException e) {
			Logger.getSingleton().logException(e);
			e.printStackTrace();
		} 
		
		
	}
	
	protected void finalize() {
		if (packet_data != null) {
			packet_data.clear();
			packet_data.position(0);
		}
	}

	public void insertData(byte... insertData) {
		packet_data.put(insertData);
	}
	
	public void insertData(byte insertData) {
		packet_data.put(insertData);
	}

	public void insertData(int startPos, byte... insertData) {
		packet_data.position(startPos);
		packet_data.put(insertData);
	}
	
	public void insertData(long data) {
		packet_data.putLong(data);
	}
	
	public void insertData(ByteBuffer insertData) {
		packet_data.put(insertData);
	}
	
	public void insertData(int startPos, ByteBuffer insertData) {
		packet_data.position(startPos);
		packet_data.put(insertData.array());
	}

	public void insertData(int insertData) {
		packet_data.putInt(insertData);
	}

	public void insertData(short insertData) {
		packet_data.putShort(insertData);
	}

	public boolean isCompressed() {
		try {
			return packet_data.get(0) == PROTO_KAD_COMPRESSED_UDP;
		}catch(Throwable t) { Logger.getSingleton().logException(t); return false; }
	}

	public InetSocketAddress getAddress() {
		return sender;
	}

	public void setAddress(InetSocketAddress sender) {
		this.sender = sender;
	}

	public byte getCommand() {
		return packet_data.get(1);
	}
	
	public void setCommand(byte packetCommand) {
		packet_data.put(1, packetCommand);
	}
	
	public byte getProtocol() {
		return packet_data.get(0);
	}
	
	public void setProtocol(byte protocol) {
		packet_data.put(0,protocol);
	}
	
	public int capacity() {
		return packet_data.capacity();
	}
	
	public KadContact getContact() {
		byte[] client_id_raw = new byte[16];
		packet_data.get(client_id_raw);
		byte[] address = new byte[4];
		packet_data.get(address);
		int udp_port = shortToInt(packet_data.getShort());
		int tcp_port = shortToInt(packet_data.getShort());
		byte client_version = packet_data.get();
		return new KadContact(new ClientID(client_id_raw), new ContactAddress(address, udp_port), tcp_port,client_version, null, false);
	}

	public ByteBuffer getAsByteBuffer() {		
		return packet_data;
	}

	public int getLength() {
		return packet_data.capacity();
	}

	public byte[] getPacket() {
		return packet_data.array();
	}
	
}
