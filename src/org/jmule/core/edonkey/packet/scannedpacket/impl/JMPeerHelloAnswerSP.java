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
package org.jmule.core.edonkey.packet.scannedpacket.impl;

import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLOANSWER;

import java.net.InetSocketAddress;

import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.tag.TagList;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:20:39 $$
 */
public class JMPeerHelloAnswerSP implements ScannedPacket{

	private UserHash userHash;
	private TagList tagList;
	private ClientID clientID;
	private int tcpPort;
	private InetSocketAddress serverAddress;
	
	public JMPeerHelloAnswerSP(UserHash userHash,
			TagList tagList, ClientID clientID, int tcpPort, InetSocketAddress serverAddress) {
		
		this.userHash = userHash;
		this.tagList = tagList;
		this.clientID = clientID;
		this.tcpPort = tcpPort;
		this.serverAddress = serverAddress;
	}
	
	public int getTCPPort() {
		return tcpPort;
	}
	
	public int getPacketCommand() {
		return OP_PEERHELLOANSWER;
	}

	public UserHash getUserHash() {
		return userHash;
	}

	public void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}

	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}

	public ClientID getClientID() {
		return clientID;
	}

	public void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}

	public InetSocketAddress getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(InetSocketAddress serverAddress) {
		this.serverAddress = serverAddress;
	}

}
