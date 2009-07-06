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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:09:12 $$
 */
public abstract class UDPAbstractPacket {
	protected ByteBuffer dataPacket = null;
	protected InetSocketAddress sender;

	public UDPAbstractPacket(int packetLength,byte packetProtocol){
		dataPacket = Misc.getByteBuffer(packetLength);
		this.setProtocol(packetProtocol);
	}
	
	public UDPAbstractPacket(){
	}
	
	public byte getProtocol() {
		return this.dataPacket.get(0);
	}
	
	public void setProtocol(byte protocol){
		this.dataPacket.put(0, protocol);
	}
	
	public void insertData(int insertData) {
		dataPacket.putInt(insertData);
	}
	
	public void insertData(byte[] insertData) {
		dataPacket.put(insertData);
	}

	public void insertData(short insertData) {
		dataPacket.putShort(insertData);
	}

	public void insertData(byte insertData) {
		dataPacket.put(insertData);
	}

	public void insertData(int startPos, byte[] insertData) {
		dataPacket.position(startPos);
		dataPacket.put(insertData);
	}
	
	public byte[] getPacket() {
		return dataPacket.array();
	}

	public ByteBuffer getAsByteBuffer() {
		return dataPacket;
	}

	public int getLength() {
		return dataPacket.limit();
	}
	
	public void clear() {
		if (dataPacket==null) return ;
		dataPacket.clear();
		dataPacket.compact();
		dataPacket.rewind();
		dataPacket.limit(0);
	}
	
	public InetSocketAddress getAddress() {
		return sender;
	}

	public void setAddress(InetSocketAddress sender) {
		this.sender = sender;
	}

	public String toString() {
		return Convert.byteToHexString(dataPacket.array()," 0x");
	}
	

}
