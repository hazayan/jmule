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
package org.jmule.core.edonkey.packet.impl;

import static org.jmule.core.edonkey.E2DKConstants.OP_COMPRESSEDPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_PUBLICKEY;
import static org.jmule.core.edonkey.E2DKConstants.OP_SECIDENTSTATE;
import static org.jmule.core.edonkey.E2DKConstants.OP_SIGNATURE;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_EXTENDED_TCP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.packet.EMulePacketException;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMFloodException;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:08:23 $$
 */
public class EMuleExtendedTCPPacket extends AbstractPacket implements Packet {

	public EMuleExtendedTCPPacket() {
	}
	
	public EMuleExtendedTCPPacket(int packetLength) {
		packet_data = Misc.getByteBuffer(packetLength+1+4+1);
		packet_data.position(0);
		packet_data.put(PROTO_EMULE_EXTENDED_TCP);
		packet_data.putInt(packetLength+1);
		packet_data.put((byte)0);
	}
	
	
	/**
	 * Get Queue ranking position
	 * @return
	 * @throws eMulePacketException
	 */
	public short getQueueRankingPosition()throws EMulePacketException {
		if (this.getCommand() != OP_EMULE_QUEUERANKING)
			throw new EMulePacketException("Wrong packet type");
		
		short queueRanking=0;
		packet_data.position(1+4+1);
		queueRanking = packet_data.getShort();
		return queueRanking;
	}
	
	public boolean isPublicKeyNeeded() throws EMulePacketException {
		if (getCommand() != OP_SECIDENTSTATE)
			throw new EMulePacketException("Wrong packet type");
		packet_data.position(1+4+1);
		byte data = packet_data.get();
		if (data == 2) return true;
		return false;
	}
	
	public byte[] getChallenge() throws EMulePacketException {
		if (getCommand() != OP_SECIDENTSTATE)
			throw new EMulePacketException("Wrong packet type");
		packet_data.position(1 + 4 + 1 + 1);
		byte[] result = new byte[4];
		packet_data.get(result);
		
		return result;
	}
	
	public byte[] getPublicKey() throws EMulePacketException {
		if (getCommand() != OP_PUBLICKEY)
			throw new EMulePacketException("Wrong packet type");
		
		packet_data.position(1 + 4 + 1);
		byte key_length = packet_data.get();
		byte key[] = new byte[key_length];
		packet_data.get(key);
		return key;
	}
	
	public byte[] getSignature() throws EMulePacketException {
		if (getCommand() != OP_SIGNATURE)
			throw new EMulePacketException("Wrong packet type");
		packet_data.position(1 + 4 + 1);
		byte signature_length = packet_data.get();
		byte signature[] = new byte[signature_length];
		packet_data.get(signature);
		return signature;
	}

	public void addData(ByteBuffer data){
			byte[] defaultData = data.array();
			this.insertData(5,defaultData);
	}
	
    public FileChunk getCompressedFileChunk() throws PacketException {
    	if (this.getCommand() != OP_COMPRESSEDPART) 
    		throw new PacketException("No OP_COMPRESSEDPART packet "+this);
    	packet_data.position(1+4+1+16);
    	
    	long chunkStart = Convert.intToLong(packet_data.getInt());
    	long chunkEnd = Convert.intToLong(packet_data.getInt());
    	long compressedSize = packet_data.capacity() - packet_data.position()-1;
    	ByteBuffer data = Misc.getByteBuffer(compressedSize);
    	packet_data.get(data.array());
    	return new FileChunk(chunkStart,chunkEnd,data);
    }
		
	/**
	 * Read packet from connection
	 * @throws InterruptedException 
	 * @throws JMEndOfStreamException 
	 */
	public void  readPacket(JMuleSocketChannel connection)throws IOException, JMEndOfStreamException, InterruptedException,JMFloodException {
		clear();
		ByteBuffer packetLength = Misc.getByteBuffer(4);
		
		connection.read(packetLength);
		int pkLength = packetLength.getInt(0);
		if (pkLength>ConfigurationManager.MAX_PACKET_SIZE) 
			throw new JMFloodException("Packet length is too big, packet length : "+pkLength);
		
		packet_data=ByteBuffer.allocate(pkLength+1+4+1);
		packet_data.order(ByteOrder.LITTLE_ENDIAN);
		packet_data.put(PROTO_EMULE_EXTENDED_TCP);
		packet_data.putInt(pkLength+1);//Put length +1 to write command byte
		packet_data.put((byte)0);//Put default command
		ByteBuffer defaultData = PacketReader.readBytes(connection, pkLength);
		this.insertData(5,defaultData.array());
	}

}
