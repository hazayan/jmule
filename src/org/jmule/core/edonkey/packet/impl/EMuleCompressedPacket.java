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
import java.util.zip.DataFormatException;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMFloodException;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:07:39 $$
 */
public class EMuleCompressedPacket extends StandardPacket implements Packet  {
	
	private byte packetCmd;
	
	private boolean isCompressed = false;
	
	
	public EMuleCompressedPacket() {
	}
	
	public EMuleCompressedPacket(StandardPacket packet) {
		packet_data = Misc.getByteBuffer(packet.getLength());
		packet_data.position(0);
		packet.getAsByteBuffer().position(0);
		packet_data.put(packet.getAsByteBuffer());
		packet_data.position(0);
		packet_data.put(PROTO_EMULE_COMPRESSED_TCP);
		isCompressed = false;
	}
	
	public EMuleCompressedPacket(int packetLength) {
		
		packet_data = Misc.getByteBuffer(packetLength + 1 + 4 + 1);

		packet_data.put(PROTO_EMULE_COMPRESSED_TCP);
		
		packet_data.putInt(packetLength + 1);
		
		packet_data.put((byte) 0);
		
	}
	
	public void compressPacket() {
		
		if (isCompressed) return;
		
		byte packetCmd = getCommand();
		
		byte data[] = new byte[getLength() - 1 - 4 - 1];
		packet_data.position(1+4+1);
		packet_data.get(data,0,data.length);
		
		ByteBuffer un_compressed_data = Misc.getByteBuffer(data.length);
		
		un_compressed_data.put(data);
		
		ByteBuffer compressed_data = JMuleZLib.compressData(un_compressed_data);
		
		packet_data = Misc.getByteBuffer(compressed_data.capacity()+1+4+1);
		
		packet_data.position(0);
		insertData((byte) PROTO_EMULE_COMPRESSED_TCP);
		
		insertData((int) (compressed_data.capacity() + 1));// Put length +1
															// to write
															// command byte
		
		insertData((byte) packetCmd);// Insert packet command
		
		packet_data.position(0);
		compressed_data.position(0);
		insertData(6, compressed_data);// Insert packet data
		
		isCompressed = true;
		
	}
	
	public void decompressPacket() {
		
		if ( !isCompressed ) return ;
		
		byte packetCmd = getCommand();
		
		byte data[] = new byte[packet_data.capacity() - 1 - 4 - 1];
		
		packet_data.position(1 + 4 + 1);
		
		ByteBuffer compressed_data = Misc.getByteBuffer(data.length);
		
		compressed_data.position(0);
		
		compressed_data.put(packet_data.array(), 1+4+1, packet_data.capacity()-1-4-1);
		
		try {
			
			ByteBuffer decompressed_data = JMuleZLib.decompressData(compressed_data);
			
			packet_data = Misc.getByteBuffer(decompressed_data.capacity()+1+4+1);
			
			insertData((byte) PROTO_EDONKEY_TCP);
			
			insertData((int) (decompressed_data.capacity() + 1));// Put length +1
																// to write
																// command byte
			
			insertData((byte) packetCmd);// Insert packet command
			
			packet_data.position(0);

			insertData(6, decompressed_data.array());// Insert packet data
			
			isCompressed = false;
			
		} catch (DataFormatException e) {
			
			e.printStackTrace();
			
		}
		
	}
	

	 public void addData(ByteBuffer data){

			int pkLength=1+4+1+data.capacity();
			
			packet_data=Misc.getByteBuffer(pkLength);
			
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
		
		packet_data = Misc.getByteBuffer(pkLength);
			
		insertData((byte)PROTO_EMULE_COMPRESSED_TCP);
		
		insertData((int)(packetContent.capacity()+1));//Put length +1 to write command byte
		
		insertData((byte)packetCmd);//Insert packet command
		
		packet_data.position(0);
		
		insertData(6,packetContent);//Insert packet data
		
		isCompressed = true;
	}
}
