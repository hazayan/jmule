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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMThread;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerFactory;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerFactory;
import org.jmule.core.uploadmanager.UploadSession;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:38:03 $$
 */
public class PeerManagerImpl implements PeerManager {
	
	private int PEER_CLEANER_CHECK_INTERVAL			= 1000;
	private int PEER_CLEANER_DISCONNECT_TIME        = 5000;

	private List<Peer> peer_list = new CopyOnWriteArrayList<Peer>();
	
	private List<Peer> unknown_peer_list = new CopyOnWriteArrayList<Peer>();
	
	private List<Peer> low_id_peer_list = new CopyOnWriteArrayList<Peer>();
	
	private PeerCleaner peer_cleaner;
	
	public void addPeer(Peer peer) {
	    peer_list.add(peer);	
    }
	
	public void addUnknownPeer(Peer peer) {
	  unknown_peer_list.add(peer);
	}

	public JMIterable<Peer> getPeers() {
         
	  return new JMIterable<Peer>(peer_list.iterator());
	}
	
	public boolean isReusedPeer(Peer peer) {
		return peer_list.contains(peer);
	}

	public void makeKnownPeer(Peer peer) {
		  if (!unknown_peer_list.contains(peer)) return ;
		
		  unknown_peer_list.remove(peer);
		  
		  if (peer_list.contains(peer)) { // known peer connected
			  
			 for(Peer e_peer : peer_list) {
				 if (e_peer.equals(peer)) {
					 peer_list.remove(e_peer);
					 peer.setSessionList(e_peer.getSessionList());
					 e_peer.setSessionList(null);
					 peer_list.add(peer);
					 return ;
				 }
			 }
		  }
		  
		  if (peer.isHighID()) {
			  
			  addPeer(peer);
			  
			  return ;
		  }

		  if (!low_id_peer_list.contains(peer)) { 
			  	addPeer(peer);
			  	return ;
		  }
		  
		  Peer old_peer = getLowIDPeer(peer);
		  if (old_peer == null) return ;
		  low_id_peer_list.remove(old_peer);
		  
		  peer.reusePeer(old_peer);
		  addPeer(peer);
	}

	private Peer getLowIDPeer(Peer searchPeer) {
		for(Peer peer : low_id_peer_list)
			if (peer.equals(searchPeer))
				return peer;
		return null;
	}
	
	public boolean hasPeer(Peer peer) {

         return peer_list.contains(peer);
	}

	public boolean hasPeer(ClientID clientID) {
		 for(Peer peer : peer_list)
			 if (peer.getID().equals(clientID)) 
				 return true;
		 return false;
	}

	public void removePeer(Peer peer) {		
		if (unknown_peer_list.contains(peer)) {
			unknown_peer_list.remove(peer);
			return ;
		}
		
		// remove peer when is not registred in any session
		
		PeerSessionList list = peer.getSessionList();
		if (list == null)
			peer_list.remove(peer);
			else
				if (peer.getSessionList().getSessionCount()==0)
					peer_list.remove(peer);
		
	}

	public void removePeer(ClientID clientID) {

		if (clientID != null)
			peer_list.remove(clientID);
	}

	public List<Peer> getPeers(FileHash fileHash) {
		
		List<Peer> list = new LinkedList<Peer>();
		
		for(Peer peer : peer_list)
			
			if (peer.hasSharedFile(fileHash))
				
				list.add(peer);
		
		return list;
	}
	
	public JMIterable<Peer> getUnknownPeers() {
		
		return new JMIterable<Peer>(unknown_peer_list.iterator());
		
	}
	
	
	public void initialize() {
      //TODO create our own binary file format (.jmule)
	   Set<String> types = new HashSet<String>();	
	   types.add(JMuleCoreStats.ST_NET_PEERS_COUNT);
	   JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
		public void updateStats(Set<String> types, Map<String, Object> values) {
			if (types.contains(JMuleCoreStats.ST_NET_PEERS_COUNT)) {
				values.put(JMuleCoreStats.ST_NET_PEERS_COUNT, peer_list.size() + low_id_peer_list.size());
			}
		}
	   });
	}

	public void shutdown() {
		
		for(Peer peer : this.getPeers()) {
			if (peer.isConnected())
				peer.disconnect();
			
		}
		
		peer_cleaner.JMStop();

	}

	public void start() {

		peer_cleaner = new PeerCleaner();
		
		peer_cleaner.start();
		
	}

	private boolean hasPeer(String address) {
		
		for(Peer peer : peer_list) {
			
			String s = peer.getAddress()+" : "+peer.getPort();
			
			if (s.equals(address))
				
				return true;
		}
		

		
		return false;
	}
	
	public void addPeer(FileHash fileHash, List<Peer> peerList) {
		
		List<Peer> added_peers = new LinkedList<Peer>();
		
		for(Peer peer : peerList) {
			
			if (unknown_peer_list.contains(peer)) continue;
			
			if (peer_list.contains(peer)) continue;
			
			if (low_id_peer_list.contains(peer)) continue;
			
			added_peers.add(peer);
				
			if (!peer.isHighID()) {
				if (!low_id_peer_list.contains(peer))
					
					low_id_peer_list.add(peer);
			}
			else {
					addPeer(peer);
				}
			
		}
		
		DownloadManagerFactory.getInstance().addDownloadPeers(fileHash, added_peers);
		
	}
	
	public float getDownloadSpeed() {
		float speed = 0;
		
		DownloadManager download_manager = DownloadManagerFactory.getInstance();
		
		for(DownloadSession session : download_manager.getDownloads())
			speed += session.getSpeed();
		
		return speed;
	}

	public float getUploadSpeed() {
		float speed = 0;
		
		UploadManager upload_manager = UploadManagerFactory.getInstance();
		
		for(UploadSession session : upload_manager.getUploads())
			speed += session.getSpeed();
		
		return speed;
	}
	
	
	private class PeerCleaner extends JMThread {

		private boolean stop = false;
		
		public PeerCleaner() {
			
			super("Peer cleaner");
		
		} 

		
		public void JMStop() {
			
			stop = true;
			interrupt();
		
		}

		public void run() {
			
			while(!stop) {
				
				try {
					
					Thread.sleep(PEER_CLEANER_CHECK_INTERVAL);
					
				} catch (InterruptedException e) {
					
					if (stop) return;
					
					continue;
					
				}
				
				
				for(Peer peer : peer_list) {
					
					if (peer.isConnected()) continue;
					
					if (System.currentTimeMillis()-peer.getConnectTime()>=PEER_CLEANER_DISCONNECT_TIME) {
						PeerSessionList list = peer.getSessionList();
						if (list == null)
							peer.disconnect();
							else
								if (peer.getSessionList().getSessionCount()==0)
									peer.disconnect();
						
					}
					
				}
				
				for(Peer peer : unknown_peer_list) {
					
					if (peer.isConnected()) continue;
					
					if (System.currentTimeMillis()-peer.getConnectTime()>=PEER_CLEANER_DISCONNECT_TIME) {
						if (peer.getSessionList().getSessionCount()==0) 
						peer.disconnect();
						
					}
					
				}
				
			}
			
		}
		
	}

}
