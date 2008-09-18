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

import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerCallbackFailed;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerFoundSourceSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerSearchResultSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerServerListSP;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/18 08:51:11 $$
 */
public aspect ServerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.edonkey.impl.Server");
	
	before() :call(Server.new(..)) {
	}
	
	after() throwing (Throwable t): execution (* Server.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	before(Server s, ClientID client_id) : target(s) && args(client_id) && execution(void Server.callBackRequest(ClientID)) {
	}
	
	before(ScannedPacket packet, Server s) : target(s) && args(packet)&& execution (void Server.processPacket(ScannedPacket)) {
		
		if (packet instanceof JMServerMessageSP) {						
			return ;
		}
		
		if (packet instanceof JMServerServerListSP) {

			return ;
		}
		
		if (packet instanceof JMServerCallbackFailed) {
			return ;
		}
		
		if (packet instanceof JMServerSearchResultSP) {
			return ;
		}
		
		if (packet instanceof JMServerFoundSourceSP) {
			return ;
		}
	}
	
	before(Server s) : target(s) && execution(void Server.onConnect()) {
	}
	
	before(Server s) : target(s) && execution(void Server.onDisconnect()) {
	}
	
	before(Server s, FileHash fileHash,long fileSize) : target(s) && args(fileHash,fileSize) && execution(void Server.requestSources(FileHash,long)) {
	}
}
