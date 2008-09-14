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
package org.jmule.aspects;

import java.util.logging.Logger;

import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerChatMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloSP;
import org.jmule.core.net.JMConnection;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:59:59 $$
 */
public privileged aspect PeerLogger {
	
	private Logger log = Logger.getLogger("org.jmule.core.edonkey.impl.Peer");
	
	after() throwing (Throwable t): execution (* Peer.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	before() : call(Peer.new(..)) {
	
	}
	
	before(Peer p) : target(p) && execution(void Peer.onDisconnect()) {
		
	}
	
	before(Peer p) : target(p) && execution(void Peer.onConnect()) {
	}
	
	before(Peer p) : target(p) && call( void Peer.connect()) {
		if (p.getStatus()==JMConnection.TCP_SOCKET_DISCONNECTED)
			if (!p.isHighID())
				if (p.getConnectedServer()==null)
					log.warning("Can't connect to low id peer, dont have server data");
	}
	
	before(ScannedPacket packet, Peer p) : target(p) && args(packet)&& execution (void Peer.processPacket(ScannedPacket)) {
	}
	
	after(Peer p) returning(Packet packet) :target(p) && execution(Packet JMConnection.getReceivedPacket()) {
		if (packet==null)
			log.warning("Scanned Packet is null from client  : "+p);
	}
	
	before(Peer p,long time) : target(p) && args(time) && execution (void Peer.reportInactivity(long)) {
		if (p.lastPacket!=null)
			if (p.getStatus() == JMConnection.TCP_SOCKET_CONNECTED)
				log.warning("Resend last packet to peer "+p);		
	}
}
