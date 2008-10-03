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

import java.nio.ByteBuffer;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.util.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/03 17:07:49 $$
 */
public class AbstractPacket {
	
	protected ByteBuffer packet_data = null;

	public void clear() {
		
		if (packet_data==null) return ;
		
		packet_data.clear();
		
		packet_data.compact();
		
		packet_data.rewind();
		
		packet_data.limit(0);
		
	}
	
	public String toString() {
		
		return Convert.byteToHexString(packet_data.array()," 0x");
		
	}
	
	public void setCommand(byte packetCommand) {
		
		packet_data.put(5, packetCommand);
		
	}

	public byte getCommand() {
		
		try {
			
		return packet_data.get(5);
		
		}catch(Exception e ){
			
			return 0;
			
		}
		
	}

	public void setProtocol(byte packetType) {
		
		packet_data.put(0,packetType);
		
	}
	
	public byte getProtocol() {
		
		return packet_data.get(0);
		
	}

	public void insertData(byte[] insertData) {
		
		packet_data.put(insertData);
		
	}

	public void insertData(int startPos, byte[] insertData) {
		
		packet_data.position(startPos);
		
		packet_data.put(insertData);
		
	}
	
	public void insertData(ByteBuffer insertData) {
		
		packet_data.put(insertData);
		
	}
	
	public void insertData(int startPos,ByteBuffer insertData) {
		
		packet_data.position(startPos);
		
		packet_data.put(insertData.array());
		
	}

	public void insertData(int insertData) {
		
		packet_data.putInt(insertData);
		
	}

	public void insertData(short insertData) {
		
		packet_data.putShort(insertData);
		
	}

	public void insertData(byte insertData) {
		
		packet_data.put(insertData);
		
	}

	public byte[] getPacket() {
		
		return packet_data.array();
		
	}

	public ByteBuffer getAsByteBuffer() {
		
		return packet_data;
		
	}

	public int getLength() {
		
		return packet_data.capacity();
		
	}
	
    public FileHash getFileHash() {
    	
    	int iPos = packet_data.position();
    	
    	byte[] data = new byte[16];
    	
    	packet_data.position(1+4+1);
    	
    	packet_data.get(data);
    	
    	packet_data.position(iPos);
    	
    	return new FileHash(data);
    }
 
}
