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

import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_TCP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_COMPRESSED_TCP;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMFloodException;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.util.JMuleZLib;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:18:57 $$
 */
public class EMuleCompressedPacket extends StandardPacket implements Packet  {
	
	private byte packetCmd;
	
	private boolean isCompressed = false;
	
	
	public EMuleCompressedPacket() {
	}
	
	public EMuleCompressedPacket(int packetLength) {
		
		dataPacket = ByteBuffer.allocate(packetLength + 1 + 4 + 1);
		
		dataPacket.order(ByteOrder.LITTLE_ENDIAN);

		dataPacket.put(PROTO_EMULE_COMPRESSED_TCP);
		
		dataPacket.putInt(packetLength + 1);
		
		dataPacket.put((byte) 0);
		
	}
	
	public void compressPacket() {
		
		if (isCompressed) return;
		
		byte packetCmd = getCommand();
		
		byte data[] = new byte[getLength() - 1 - 4 - 1];
		
		dataPacket.get(data,1+4+1,data.length);
		
		ByteBuffer un_compressed_data = Misc.getByteBuffer(data.length);
		
		un_compressed_data.put(data);
		
		ByteBuffer compressed_data = JMuleZLib.compressData(un_compressed_data);
		
		dataPacket = Misc.getByteBuffer(compressed_data.capacity()+1+4+1);
		
		insertData((byte) PROTO_EMULE_COMPRESSED_TCP);
		
		insertData((int) (compressed_data.capacity() + 1));// Put length +1
															// to write
															// command byte
		
		insertData((byte) packetCmd);// Insert packet command
		
		dataPacket.position(0);
		
		insertData(6, compressed_data);// Insert packet data
		
		isCompressed = true;
		
	}
	
	public void decompressPacket() {
		
		if ( !isCompressed ) return ;
		
		byte packetCmd = getCommand();
		
		byte data[] = new byte[dataPacket.capacity() - 1 - 4 - 1];
		
		dataPacket.position(1 + 4 + 1);
		
		ByteBuffer compressed_data = Misc.getByteBuffer(data.length);
		
		compressed_data.position(0);
		
		compressed_data.put(dataPacket.array(), 1+4+1, dataPacket.capacity()-1-4-1);
		
		try {
			
			ByteBuffer decompressed_data = JMuleZLib.decompressData(compressed_data);
			
			dataPacket = Misc.getByteBuffer(decompressed_data.capacity()+1+4+1);
			
			insertData((byte) PROTO_EDONKEY_TCP);
			
			insertData((int) (decompressed_data.capacity() + 1));// Put length +1
																// to write
																// command byte
			
			insertData((byte) packetCmd);// Insert packet command
			
			dataPacket.position(0);

			insertData(6, decompressed_data.array());// Insert packet data
			
			isCompressed = false;
			
		} catch (DataFormatException e) {
			
			e.printStackTrace();
			
		}
		
	}
	

	 public void addData(ByteBuffer data){

			int pkLength=1+4+1+data.capacity();
			
			dataPacket=Misc.getByteBuffer(pkLength);
			
			insertData((byte)PROTO_EMULE_COMPRESSED_TCP);	
			
			insertData(data.capacity()+1);			
			
			insertData((byte)packetCmd);
			
			data.position(0);

	  }
	
 
	/**
	 * Read packet from connection.
	 * @throws InterruptedException 
	 * @throws JMEndOfStreamException 
	 */
	public void readPacket(JMuleSocketChannel connection) throws IOException, JMEndOfStreamException, InterruptedException,JMFloodException {
		
		this.clear();
		
		ByteBuffer packetLength=Misc.getByteBuffer(4);

		connection.read(packetLength);
		
		int pkLength = packetLength.getInt(0)-1;		
		
		if (pkLength>ConfigurationManager.MAX_PACKET_SIZE) 
			throw new JMFloodException("Packet length is too big, packet length : "+pkLength);

		
		ByteBuffer packetCommand=Misc.getByteBuffer(1);
		
		connection.read(packetCommand);
		
		byte packetCmd=packetCommand.get(0);
			
		ByteBuffer packetContent = PacketReader.readBytes(connection, pkLength);
	
		pkLength = 1 + 4 + 1 + packetContent.capacity();
		
		dataPacket = Misc.getByteBuffer(pkLength);
			
		insertData((byte)PROTO_EMULE_COMPRESSED_TCP);
		
		insertData((int)(packetContent.capacity()+1));//Put length +1 to write command byte
		
		insertData((byte)packetCmd);//Insert packet command
		
		dataPacket.position(0);
		
		insertData(6,packetContent);//Insert packet data
		
		isCompressed = true;
	}
}
