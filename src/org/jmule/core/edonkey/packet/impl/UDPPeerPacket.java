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

import static org.jmule.core.edonkey.E2DKConstants.*;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.jmule.core.edonkey.packet.UDPPacket;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:44 $$
 */
public class UDPPeerPacket extends UDPAbstractPacket implements UDPPacket{

	public UDPPeerPacket(int packetLength,InetSocketAddress dest){
		super(packetLength,PROTO_EDONKEY_PEER_UDP);
		dataPacket.position(1);
		insertData(packetLength);
		insertData((byte)0);
	}
	
	public UDPPeerPacket(ByteBuffer data,InetSocketAddress sender) {
		super();
		dataPacket = data;
		this.sender = sender;
	}
	
	public void setCommand(byte packetCommand){
		dataPacket.position(5);
		dataPacket.put(packetCommand);
	}

	public byte getCommand() {
		return this.dataPacket.get(5);
	}

	
}
