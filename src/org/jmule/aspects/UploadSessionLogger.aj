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

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.*;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotTakenSP;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.core.uploadmanager.UploadSession;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:27 $$
 */
public privileged aspect UploadSessionLogger {
	private Logger log = Logger.getLogger("org.jmule.core.uploadmanager.UploadSession");
	
	before(Peer peer,ScannedPacket packet,UploadSession us) : target(us) && args(peer,packet,*) && execution(void UploadSession.processPacket(Peer,ScannedPacket, PeerSessionList)) {
		
		if (packet instanceof JMPeerRequestFilePartSP) {
			log.info("Peer "+peer+" request file fragments of "+us.getFileHash());
		}
		
		if (packet instanceof JMPeerSlotTakenSP) 
			log.fine("*Remove peer "+peer+" from "+us.sharedFile+" upload queue");
	}
	
	before(Peer peer,long time) : args(peer,time) && execution(void UploadSession.reportInactivity(Peer,long)) {
		if (time>=ConfigurationManager.PEER_INACTIVITY_REMOVE_TIME) {
			log.warning("Remove peer "+peer+" inactivity for "+time/1000+" sec");
		}
	}
	
}
