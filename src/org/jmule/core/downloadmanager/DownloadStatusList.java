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
package org.jmule.core.downloadmanager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.Peer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:54 $$
 */
public class DownloadStatusList {

	private Map<ClientID,PeerDownloadStatus> peer_status_list = new ConcurrentHashMap<ClientID,PeerDownloadStatus>();
	
	
	public List<PeerDownloadStatus> getPeersWithInactiveTime(long inactiveTime) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list.values()) {
			
			if ((System.currentTimeMillis() - peer_download_status.getLastUpdateTime())>=inactiveTime) {
				
				peer_list.add(peer_download_status);
				
			}
			
		}
		
		return peer_list;
		
	}
	
	public List<PeerDownloadStatus> getPeersWithInactiveTime(int status, long inactiveTime) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list.values()) {
			
			if ((System.currentTimeMillis() - peer_download_status.getLastUpdateTime())>=inactiveTime) 
				if (peer_download_status.getPeerStatus()==status) {
				
				peer_list.add(peer_download_status);
				
			}
			
		}
		
		return peer_list;
		
	}
	
	public List<PeerDownloadStatus> getPeersByStatus(int status) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list.values()) {
			
			if (peer_download_status.getPeerStatus()==status) {
				
				peer_list.add(peer_download_status);
				
			}
			
		}
		
		return peer_list;
		
	}
	
	public void addPeer(Peer peer) {
		
		PeerDownloadStatus peerStatus = new PeerDownloadStatus(peer);
		
		peer_status_list.put(peer.getID(), peerStatus);
		
	}
	
	public void setPeerStatus(Peer peer,int status) {
		
		PeerDownloadStatus download_status = peer_status_list.get(peer.getID());
		
		download_status.setPeerStatus(status);
		
	}
	
	public boolean hasPeer(Peer peer) {
		
		return peer_status_list.containsKey(peer.getID());
		
	}
	
	public void removePeer(Peer peer) {
		
		peer_status_list.remove(peer.getID());
		
	}
	
	public PeerDownloadStatus getDownloadStatus(Peer peer) {
		
		return peer_status_list.get(peer.getID());
		
	}
	
	public void updatePeerHistory(Peer peer,int history_id) {
		
		PeerDownloadStatus download_status = peer_status_list.get(peer.getID());
	
		download_status.updatePeerHistory(history_id);
		
	}
	
	public void updatePeerHistory(Peer peer,int history_id, int rank) {
		
		PeerDownloadStatus download_status = peer_status_list.get(peer.getID());
	
		download_status.updatePeerHistory(history_id,rank);
		
	}
	

	public String toString() {
		String result = "";
	
		for(PeerDownloadStatus download_status : peer_status_list.values()) {
			
			result +=download_status + "\n";
		}
		
		return result;
	}
	
}
