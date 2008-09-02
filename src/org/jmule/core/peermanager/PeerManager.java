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
package org.jmule.core.peermanager;


import java.util.List;

import org.jmule.core.JMIterable;
import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:38:04 $$
 */
public interface PeerManager extends JMuleManager {

	/**
	 * Adds a new peer to peer manager
	 * @param peer the given peer
	 */
	public void addPeer(Peer peer);
	
	/**
	 * Add a list of peers who have fileHash
	 * @param fileHash
	 * @param peerList
	 */
	public void addPeer(FileHash fileHash, List<Peer> peerList);
	
	/**
	 * Add unknown ID peer.
	 * @param peer
	 */
	public void addUnknownPeer(Peer peer);
	
	public void removePeer(Peer peer);
	
	public void removePeer(ClientID cliendID);
	
	public boolean hasPeer(Peer peer);
	
	public boolean hasPeer(ClientID clientID);
	
	
	public void makeKnownPeer(Peer peer);
	
	public JMIterable<Peer> getPeers();
	
	public JMIterable<Peer> getUnknownPeers();
	
	/**
	 * Obtain peers which share file identified by the fileHash.
	 * @param fileHash 
	 * @return
	 */
	public List<Peer> getPeers(FileHash fileHash);
	
	public float getDownloadSpeed();
	
	public float getUploadSpeed();
	
}
