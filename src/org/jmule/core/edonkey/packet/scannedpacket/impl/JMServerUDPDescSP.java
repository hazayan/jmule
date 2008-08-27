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

import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;

import java.net.InetSocketAddress;

import org.jmule.core.edonkey.packet.scannedpacket.ScannedUDPPacket;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 05:23:35 $$
 */
public class JMServerUDPDescSP implements ScannedUDPPacket {

	private String name;
	private String description;
	private InetSocketAddress sender;
	
	public JMServerUDPDescSP(String name, String description,InetSocketAddress sender) {
		super();
		this.name = name;
		this.description = description;
		this.sender = sender;
	}

	public int getPacketCommand() {
		return OP_SERVER_DESC_ANSWER;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InetSocketAddress getSenderAddress() {
		return sender;
	}

	public void setSenderAddress(InetSocketAddress sender) {
		this.sender = sender;
	}

}
