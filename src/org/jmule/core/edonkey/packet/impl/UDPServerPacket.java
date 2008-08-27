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

import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_SERVER_UDP;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagReader;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 17:09:28 $$
 */
public class UDPServerPacket extends UDPAbstractPacket implements UDPPacket {
	
	public UDPServerPacket(int packetSize,InetSocketAddress sender){
		super(packetSize+1+1,PROTO_EDONKEY_SERVER_UDP);
		this.sender = sender;
	}
 	
	public UDPServerPacket(ByteBuffer data,InetSocketAddress sender) {
		super();
		dataPacket = data;
		this.sender = sender;
	}
	
	public void setCommand(byte packetCommand){
		dataPacket.position(1);
		dataPacket.put(packetCommand);
	}
	
	public byte getCommand(){
		return dataPacket.get(1);
	}

	public int getChallenge()throws UDPPacketException {
		int packetChallenge;
		
		if (this.getCommand()!=OP_GLOBSERVSTATUS)
					throw new UDPPacketException("No OP_GLOBSERVSTATUS packet "+this);
		packetChallenge = dataPacket.getInt(2);
		return packetChallenge;
	}
	
	public int getUserCount() throws UDPPacketException {
		int userCount=0;
		if (this.getCommand()!=OP_GLOBSERVSTATUS)
				throw new UDPPacketException("No OP_GLOBSERVSTATUS packet "+this);
		try {
		userCount = dataPacket.getInt(2+4);
		}catch (Exception e){
			return 0;
		}
		return userCount;
	}
	
	public int getFilesCount() throws UDPPacketException {
		int userFiles=0;
		
		if (this.getCommand()!=OP_GLOBSERVSTATUS)
				throw new UDPPacketException("No OP_GLOBSERVSTATUS packet "+this);
		try {
		userFiles = dataPacket.getInt(2+4+4);
		}catch (Exception e ){
			return 0;
		}
		return userFiles;
	}
	
	public int getSoftLimit() throws UDPPacketException {
		int softLimit=0;
		
		if (this.getCommand()!=OP_GLOBSERVSTATUS)
				throw new UDPPacketException("No OP_GLOBSERVSTATUS packet "+this);
		try {
		softLimit = dataPacket.getInt(2+4+4+4);
		}catch (Exception e){
			return 0;
		}
		return softLimit;
	}
	
	public int getHardLimit() throws UDPPacketException {
		int hardLimit = 0;
		
		if (this.getCommand()!=OP_GLOBSERVSTATUS)
				throw new UDPPacketException("No OP_GLOBSERVSTATUS packet "+this);
		try {
		hardLimit = dataPacket.getInt(2+4+4+4+4);
		}catch (Exception e){
			return 0;
		}
		return hardLimit;
	}
	
	
	public String getServerName() throws UDPPacketException {
		if (this.getCommand()!=OP_SERVER_DESC_ANSWER)
			throw new UDPPacketException("No OP_SERVER_DESC_ANSWER packet "+this);
		String serverName="";
		
		int strLength = Convert.shortToInt(dataPacket.getShort(1+1));
		
		if (strLength>dataPacket.limit()-1-1-2)
			strLength = dataPacket.limit()-1-1-2;
		
		byte chrArray[] = new byte[strLength];
		
		dataPacket.position(1+1+2);
		dataPacket.get(chrArray);
		serverName = new String(chrArray);
		
		if (serverName.length()==0) return "";
		
		return serverName;
	}
	
	public String getServerDesc() throws UDPPacketException {
		if (this.getCommand()!=OP_SERVER_DESC_ANSWER)
			throw new UDPPacketException("No OP_SERVER_DESC_ANSWER "+this);
		String serverDesc="";
			
		try {
		int strLength1 = Convert.shortToInt(dataPacket.getShort(1+1));
		
		dataPacket.position(1+1+2+strLength1);
		int strLength = 0;
		
		strLength = Convert.shortToInt(dataPacket.getShort());
		
		byte chrArray[] = new byte[strLength];
				dataPacket.get(chrArray);
		
		serverDesc = new String(chrArray);
		}catch(Exception e) {
			return "";
		}
	
		if (serverDesc.length()==0) return "";
		
		return serverDesc;
	}
	
	// New 0xA3 packet support 
	
	public boolean isNewDescAnswerPacket() {
		if (this.getCommand()!=OP_SERVER_DESC_ANSWER)
			return false;
		//if (dataPacket.capacity()>1000) return true; else return false;
		short string_length = dataPacket.getShort(1+1);
		
		if (string_length<0) return true;
		
		if (string_length>100) return true;

		dataPacket.position(1+1+4);
		
		int tagCount = dataPacket.getInt();
		
		if (tagCount<0) return false;
		if (tagCount>10) return false;
		
//		if (dataPacket.capacity()>100) return true;
//		
		return true;
	}
	
	public int getDescPacketChallenge() throws UDPPacketException {
		if (this.getCommand()!=OP_SERVER_DESC_ANSWER)
			throw new UDPPacketException("No OP_SERVER_DESC_ANSWER "+this);
		
		return dataPacket.getInt(1+1);
	}
	
	public TagList getDescPacketTagList() throws UDPPacketException {
		if (this.getCommand()!=OP_SERVER_DESC_ANSWER)
			throw new UDPPacketException("No OP_SERVER_DESC_ANSWER "+this);
		
		TagList tagList = new TagList();
		
		dataPacket.position(1+1+4);
		
		int tagCount = dataPacket.getInt();
		try {
		for(int i = 0;i<tagCount;i++) 
			tagList.addTag(TagReader.readTag(dataPacket));
		}catch(RuntimeException e) {
			e.printStackTrace();
		}
		return tagList;
	}
	
}
