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

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/18 08:51:10 $$
 */
public privileged aspect PeerManagerLogger {
	private Logger log = Logger.getLogger("org.jmule.core.peermanager.PeerManager");
	
	after() throwing (Throwable t): execution (* PeerManager.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	before(Peer peer) : args(peer) && execution(void PeerManager.addUnknownPeer(Peer)) {
	
	}
	
	before(FileHash fileHash, List<Peer> peerList) : args(fileHash, peerList) && execution(void PeerManager.addPeer(FileHash,List)) {
	}
	
	after(Peer p) returning(boolean result) : args(p) && execution(boolean PeerManager.hasPeer(Peer)) {
	}

	
	
}
