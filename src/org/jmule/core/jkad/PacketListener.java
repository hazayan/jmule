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
package org.jmule.core.jkad;

import static org.jmule.core.jkad.JKadConstants.DEFAULT_PACKET_LISTENER_TIMEOUT;

import java.net.InetSocketAddress;

import org.jmule.core.jkad.packet.KadPacket;

/**
 * Created on Feb 8, 2009
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/25 10:15:00 $
 */
public abstract class PacketListener {
	private Byte packetOPCode;
	private long timeOut = DEFAULT_PACKET_LISTENER_TIMEOUT;
	private InetSocketAddress sender;
	
	public PacketListener(Byte packetOPCode, InetSocketAddress sender) {
		super();
		this.sender = sender;
		this.packetOPCode = packetOPCode;
	}

	public PacketListener(Byte packetOPCode) {
		super();
		this.packetOPCode = packetOPCode;
	}
	
	public abstract void packetReceived(KadPacket packet);

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}

	public InetSocketAddress getSender() {
		return sender;
	}

	public void setSender(InetSocketAddress sender) {
		this.sender = sender;
	}

	public Byte getPacketOPCode() {
		return packetOPCode;
	}

	public void setPacketOPCode(Byte packetOPCode) {
		this.packetOPCode = packetOPCode;
	}
	
	public boolean processAddress(InetSocketAddress address) {
		if (sender == null) return true;
		return sender.equals(address);
	}
	
}
