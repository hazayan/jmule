/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.indexer;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.net.packet.tag.TagList;


/**
 * Created on Jan 5, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Source {
	private ClientID clientID;
	
	private IPAddress address;
	private int udpPort;
	private int tcpPort;
	private byte kadVersion;
	private TagList tagList;
	private long creationTime = System.currentTimeMillis();
	
	public Source(ClientID clientID, TagList tagList) {
		this.clientID = clientID;
		this.tagList = tagList;
	}
	
	public Source(ClientID clientID, IPAddress address, int udpPort, int tcpPort) {
		this.clientID = clientID;
		this.address = address;
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
	}
	
	public Source(ClientID clientID) {
		this.clientID = clientID;
	}
	

	public ClientID getClientID() {
		return clientID;
	}

	public void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}

	public IPAddress getAddress() {
		return address;
	}

	public void setAddress(IPAddress address) {
		this.address = address;
	}

	public int getUDPPort() {
		return udpPort;
	}

	public void setUDPPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public int getTCPPort() {
		return tcpPort;
	}

	public void setTCPPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public byte getKadVersion() {
		return kadVersion;
	}

	public void setKadVersion(byte kadVersion) {
		this.kadVersion = kadVersion;
	}

	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof Source)) return false;
		Source source = (Source) object;
		if (!source.getClientID().equals(getClientID())) return false;
		return true;
	}
	
	public int hashCode() {
		return getClientID().hashCode();
	}
	
	public String toString() {
		String result = "";
		result += "Address  : " + address + "\n";
		result += "UDP Port : " + udpPort + "\nTCP Port : " + tcpPort + "\n";
		result += tagList;
		return result;
	}
	
}
