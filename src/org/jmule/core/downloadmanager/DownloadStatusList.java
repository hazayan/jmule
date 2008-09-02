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
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.edonkey.impl.Peer;

/**
 * Created on 07-19-2008
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 14:22:59 $$
 */
public class DownloadStatusList {

	private List<PeerDownloadStatus> peer_status_list = new CopyOnWriteArrayList<PeerDownloadStatus>();
	
	
	public List<PeerDownloadStatus> getPeersWithInactiveTime(long inactiveTime) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list) {
			
			if ((System.currentTimeMillis() - peer_download_status.getLastUpdateTime())>=inactiveTime) {
				
				peer_list.add(peer_download_status);
				
			}
			
		}
		
		return peer_list;
		
	}
	
	public List<PeerDownloadStatus> getPeersWithInactiveTime(int status, long inactiveTime) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list) {
			
			if ((System.currentTimeMillis() - peer_download_status.getLastUpdateTime())>=inactiveTime) 
				if (peer_download_status.getPeerStatus()==status) {
				
				peer_list.add(peer_download_status);
				
			}
			
		}
		
		return peer_list;
		
	}
	
	public List<PeerDownloadStatus> getPeersByStatus(int... status) {
		
		List<PeerDownloadStatus> peer_list = new LinkedList<PeerDownloadStatus>();
		
		for(PeerDownloadStatus peer_download_status : peer_status_list) {
			for(int peer_status : status) {
				if (peer_download_status.getPeerStatus()==peer_status) {
					
					peer_list.add(peer_download_status);
					
					break;
				}
			}
			
		}
		
		return peer_list;
		
	}
	
	public void setPeer(Peer newPeer) {
		if (!hasPeer(newPeer)) return ;
		PeerDownloadStatus status = getDownloadStatus(newPeer);
		status.setPeer(newPeer);
		peer_status_list.add(status);
	}
	
	public void addPeer(Peer peer) {
		
		PeerDownloadStatus peerStatus = new PeerDownloadStatus(peer);
		
		peer_status_list.add(peerStatus);
		
	}
	
	public void setPeerStatus(Peer peer,int status) {
		
		PeerDownloadStatus download_status = getDownloadStatus(peer);
		if (download_status != null)  // peer may be removed, don't register history for removed peers
			download_status.setPeerStatus(status);
		
	}
	
	public boolean hasPeer(Peer peer) {
		
		for(PeerDownloadStatus status : peer_status_list)
			if (status.getPeer().equals(peer)) return true;
		
		return false;
	}
	
	public void removePeer(Peer peer) {
		
		peer_status_list.remove(getDownloadStatus(peer));
		
	}
	
	public PeerDownloadStatus getDownloadStatus(Peer peer) {
		
		for(PeerDownloadStatus status : peer_status_list) {
			if (status.getPeer().equals(peer)) return status;
		}
		return null;
	}
	
	public void updatePeerHistory(Peer peer,int history_id) {
		
		PeerDownloadStatus download_status = getDownloadStatus(peer);
		if (download_status != null)  // peer may be removed, don't register history for removed peers
			download_status.updatePeerHistory(history_id);
		
	}
	
	public void updatePeerHistory(Peer peer,int history_id, int rank) {
		
		PeerDownloadStatus download_status = getDownloadStatus(peer);
		if (download_status != null)
			download_status.updatePeerHistory(history_id,rank);
		
	}
	
	public void clear() {
		peer_status_list.clear();
	}
	

	public String toString() {
		String result = "";
	
		for(PeerDownloadStatus download_status : peer_status_list) {
			
			result +=download_status + "\n";
		}
		
		return result;
	}
	
}
