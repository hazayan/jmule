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
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerFoundSourceSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerSearchResultSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerServerListSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerCallbackFailed;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:26 $$
 */
public aspect ServerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.edonkey.impl.Server");
	
	before() :call(Server.new(..)) {
	}
	
	before(Server s) : target(s) && call (void Server.searchFiles(String)) {
		if (!s.allowSearch())
			log.warning("Can't search now");
	}
	
	before(Server s, ClientID client_id) : target(s) && args(client_id) && execution(void Server.callBackRequest(ClientID)) {
		log.info("Call back request "+client_id);
	}
	
	before(ScannedPacket packet, Server s) : target(s) && args(packet)&& execution (void Server.processPacket(ScannedPacket)) {
		
		if (packet instanceof JMServerMessageSP) {
			JMServerMessageSP sPacket = (JMServerMessageSP)packet;
			log.info("Server message : \n"+sPacket.getServerMessage());
			return ;
		}
		
		if (packet instanceof JMServerServerListSP) {

			return ;
		}
		
		if (packet instanceof JMServerCallbackFailed) {
			
			log.info("Call back failed");
			return ;
		}
		
		if (packet instanceof JMServerSearchResultSP) {
			log.info("Received  search results from "+s);
			return ;
		}
		
		if (packet instanceof JMServerFoundSourceSP) {
			JMServerFoundSourceSP scannedPacket = (JMServerFoundSourceSP)packet;
			String msg = "Sources for file : "+scannedPacket.getFileHash()+"\n";
			//for(Peer peer : scannedPacket) 
			//	msg+=peer+"\n";
			log.info(msg);
		}
	}
	
	before(Server s) : target(s) && execution(void Server.onConnect()) {
		log.info("Send login packet to "+s);
	}
	
	before(Server s) : target(s) && execution(void Server.onDisconnect()) {
		log.info("Disconnect from  "+s);
	}
	
	before(Server s, FileHash fileHash,long fileSize) : target(s) && args(fileHash,fileSize) && execution(void Server.requestSources(FileHash,long)) {
		log.info("Request sources for "+fileHash);
	}
}
