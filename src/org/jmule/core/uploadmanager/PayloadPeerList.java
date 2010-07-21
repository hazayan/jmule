/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2010 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.uploadmanager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.peermanager.Peer;

/**
 * Created on Jan 24, 2010
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/21 13:15:42 $
 */
public class PayloadPeerList {

	private Map<Peer, PayloadPeerContainer> payload_peers = new ConcurrentHashMap<Peer, PayloadPeerContainer>();
	
	private Set<UserHash> payload_loosed_peers = new HashSet<UserHash>();
	
	private static PayloadPeerList instance = null;
	
	public static PayloadPeerList getInstance() {
		if (instance == null) {
			instance = new PayloadPeerList();
		}
		return instance;
	}
	
	private PayloadPeerList() { }
	
	public String toString() {
		String result = " Payload peers : size : " + payload_peers.size()+"\n";
		for (Peer peer : payload_peers.keySet())
			result += peer + "\n";
		result += "\n Payload loosed peers : " + payload_loosed_peers.size() + "\n";
		for(UserHash hash : payload_loosed_peers)
			result += hash + "\n";
		return result;
	}
	
	Map<Peer, PayloadPeerContainer> getPayloadPeers() {
		return payload_peers;
	}
	
	public boolean isPayloadLoosed(UserHash userHash) {
		return payload_loosed_peers.contains(userHash);
	}
	
	public void addPayloadLoosed(UserHash userHash) {
		payload_loosed_peers.add(userHash);
	}
	
	public void addPeer(Peer peer, FileHash fileHash) {
		if (hasPeer(peer)) return;
		System.out.println("UPLOAD :: payload_peer :: add ::  " +peer);
		PayloadPeerContainer container = new PayloadPeerContainer(System.currentTimeMillis(), System.currentTimeMillis(), fileHash);
		payload_peers.put(peer, container);
	}
	
	public void removePeer(Peer peer) {
		if (!hasPeer(peer)) return;
		System.out.println("UPLOAD :: payload_peer :: removePeer ::  " +peer);
		payload_peers.remove(peer);
	}
	
	public long getAddTime(Peer peer) {
		if (!hasPeer(peer)) return 0;
		PayloadPeerContainer container = payload_peers.get(peer);
		return container.getAddTime();
	}
	
	public long getLastActive(Peer peer) {
		if (!hasPeer(peer)) return 0;
		PayloadPeerContainer container = payload_peers.get(peer);
		return container.getLastResponseTime();
	}
	
	public void setLastActiveTime(Peer peer, long responseTime) {
		if (!hasPeer(peer)) return ;
		PayloadPeerContainer container = payload_peers.get(peer);
		container.setLastResponseTime(responseTime);
	}
	
	public FileHash getFileHash(Peer peer) {
		if (!hasPeer(peer)) return null;
		PayloadPeerContainer container = payload_peers.get(peer);
		return container.getFileHash();
	}
	
	public boolean hasPeer(Peer peer)  {
		return payload_peers.containsKey(peer);
	}
	
	
	class PayloadPeerContainer {
		private long addTime;
		private long lastResponseTime;
		private FileHash fileHash;
		
		public PayloadPeerContainer(long addTime, long lastResponseTime, FileHash fileHash) {
			this.addTime = addTime;
			this.lastResponseTime = lastResponseTime;
			this.fileHash = fileHash;
		}
		
		public long getAddTime() {
			return addTime;
		}
		public void setAddTime(long addTime) {
			this.addTime = addTime;
		}
		public long getLastResponseTime() {
			return lastResponseTime;
		}
		public void setLastResponseTime(long lastResponseTime) {
			this.lastResponseTime = lastResponseTime;
		}
		public FileHash getFileHash() {
			return fileHash;
		}
		public void setFileHash(FileHash fileHash) {
			this.fileHash = fileHash;
		}
	}
	
}
