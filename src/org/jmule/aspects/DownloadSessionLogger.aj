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

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerAcceptUploadRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSendingPartSP;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:52:07 $$
 */
public privileged aspect DownloadSessionLogger {

	private Logger log = Logger.getLogger("org.jmule.core.downloadmanager.DownloadSession");
	
	after() throwing (Throwable t): execution (* DownloadSession.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	before(DownloadSession downloadSession) : target(downloadSession) &&call(void DownloadSession.startDownload()) {
		log.info("Start to download : "+downloadSession.getSharingName());
	}
	
	before(Peer peer, ScannedPacket packet, PeerSessionList router,DownloadSession downloadSession) : target(downloadSession) && args(peer,packet,router) && execution (void DownloadSession.processPacket(Peer, ScannedPacket, PeerSessionList)) {
		if (packet instanceof JMPeerSendingPartSP ) {
		}
		
		if (packet instanceof JMPeerAcceptUploadRequestSP ) {
			
		}
		
		
	}
	
	after(DownloadSession downloadSession) : target(downloadSession) && call(void DownloadSession.completeDownload()) {
		log.info("Download of file "+downloadSession.getSharingName()+" finished");
	}
	
	before(Peer peer) : args(peer) && call(void Peer.sendPacket(Packet)) && cflow( call (void DownloadSession.resendFilePartsRequest(Peer))) {
		log.info("Resend last file part request to "+peer);
	}
	
	before(Peer peer) : target(peer) && call(void Peer.sendPacket(Packet)) && cflow( execution (void DownloadSession.SlotRequestThread.run())) {
		log.info("Resend last file part request to "+peer);
	}
	
}
