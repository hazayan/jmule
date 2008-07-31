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

import java.util.List;
import java.util.logging.Logger;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.peermanager.PeerManagerImpl;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:28 $$
 */
public privileged aspect PeerManagerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.peermanager.PeerManager");
	
	before(Peer peer) : args(peer) && execution(void PeerManager.addUnknownPeer(Peer)) {
		
		log.info("Unknow peer : " + peer);
		
	}
	
	before(Peer peer) : target(peer) && call(void Peer.disconnect()) && cflow (execution(void PeerManagerImpl.PeerCleaner.run())) {
		
		log.warning("Cleaning peer "+peer);
		
	}
	
	before(FileHash fileHash, List<Peer> peerList) : args(fileHash, peerList) && execution(void PeerManager.addPeer(FileHash,List)) {
		
//		String str = " Peers who have "+fileHash+"\n";
//		
//		for(Peer peer : peerList)
//			
//			str += peer + "\n";
//		
//		log.info(str);
	}
	
	after(Peer p) returning(boolean result) : args(p) && execution(boolean PeerManager.hasPeer(Peer)) {
		if (!result);
			//log.warning("Don't have peer "+p);
	}

	
	
}
