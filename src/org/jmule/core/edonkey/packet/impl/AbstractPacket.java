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
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:43 $$
 */
public class AbstractPacket {
	
	protected ByteBuffer dataPacket = null;

	public void clear() {
		
		if (dataPacket==null) return ;
		
		dataPacket.clear();
		
		dataPacket.compact();
		
		dataPacket.rewind();
		
		dataPacket.limit(0);
		
	}
	
	public String toString() {
		
		return Convert.byteToHexString(dataPacket.array()," 0x");
		
	}
	
	public void setCommand(byte packetCommand) {
		
		this.dataPacket.put(5, packetCommand);
		
	}

	public byte getCommand() {
		
		try {
			
		return this.dataPacket.get(5);
		
		}catch(Exception e ){
			
			return 0;
			
		}
		
	}

	public void setProtocol(byte packetType) {
		
		dataPacket.put(0,packetType);
		
	}
	
	public byte getProtocol() {
		
		return dataPacket.get(0);
		
	}

	public void insertData(byte[] insertData) {
		
		dataPacket.put(insertData);
		
	}

	public void insertData(int startPos, byte[] insertData) {
		
		dataPacket.position(startPos);
		
		dataPacket.put(insertData);
		
	}
	
	public void insertData(ByteBuffer insertData) {
		
		dataPacket.put(insertData);
		
	}
	
	public void insertData(int startPos,ByteBuffer insertData) {
		
		dataPacket.position(startPos);
		
		dataPacket.put(insertData.array());
		
	}

	public void insertData(int insertData) {
		
		dataPacket.putInt(insertData);
		
	}

	public void insertData(short insertData) {
		
		dataPacket.putShort(insertData);
		
	}

	public void insertData(byte insertData) {
		
		dataPacket.put(insertData);
		
	}

	public byte[] getPacket() {
		
		return dataPacket.array();
		
	}

	public ByteBuffer getAsByteBuffer() {
		
		return dataPacket;
		
	}

	public int getLength() {
		
		return dataPacket.capacity();
		
	}
	
    public FileHash getFileHash() {
    	
    	int iPos = dataPacket.position();
    	
    	byte[] data = new byte[16];
    	
    	dataPacket.position(1+4+1);
    	
    	dataPacket.get(data);
    	
    	dataPacket.position(iPos);
    	
    	return new FileHash(data);
    }
 
}
