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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMThread;
import org.jmule.core.downloadmanager.DownloadManagerFactory;
import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 05:50:38 $$
 */
public class PeerManagerImpl implements PeerManager {

	private Map<ClientID,Peer> peer_list = new ConcurrentHashMap<ClientID,Peer>();
	
	private List<Peer> unknown_peer_list = new CopyOnWriteArrayList<Peer>();
	
	private Map<ClientID,Peer> low_id_peer_list = new ConcurrentHashMap<ClientID,Peer>();
	
	private PeerCleaner peer_cleaner;
	
	public void addPeer(Peer peer) {
		
	      peer_list.put(peer.getID(), peer);	
	      
    }
	
	public void addUnknownPeer(Peer peer) {
		  
		  unknown_peer_list.add(peer);
	}

	public JMIterable<Peer> getPeers() {
         
		  return new JMIterable<Peer>(peer_list.values().iterator());
	}
	

	public void makeKnownPeer(Peer peer) throws PeerManagerException {
		  
		  if (!unknown_peer_list.contains(peer)) throw new PeerManagerException("Peer "+peer+" not found in unknown id peers");
		
		  unknown_peer_list.remove(peer);
		  
		  if (peer.isHighID()) {
			  
			  addPeer(peer);
			  
			  return ;
		  }

		  Peer low_id_peer = low_id_peer_list.get(peer.getID());
		  if (low_id_peer == null) return ;
		 // System.out.println("Low id peer connected "+peer);
		  low_id_peer_list.remove(peer.getID());
		  //removePeer(low_id_peer);
		  
		//  System.out.println("Low ID peer : "+low_id_peer);
		  peer.setSessionList(low_id_peer.getSessionList());
		  addPeer(peer);
	}

	public boolean hasPeer(Peer peer) {

         return peer_list.containsKey(peer.getID());
	}

	public boolean hasPeer(ClientID clientID) {
		 
		 return peer_list.containsKey(clientID);
	}

	public void removePeer(Peer peer) {
		
		if (peer.getID() != null)
			peer_list.remove(peer.getID());
		
		if (unknown_peer_list.contains(peer));
		
			unknown_peer_list.remove(peer);
		
	}

	public void removePeer(ClientID clientID) {

		if (clientID != null)
			peer_list.remove(clientID);
	}

	public List<Peer> getPeers(FileHash fileHash) {
		
		List<Peer> list = new LinkedList<Peer>();
		
		for(Peer peer : peer_list.values())
			
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
				values.put(JMuleCoreStats.ST_NET_PEERS_COUNT, peer_list.size());
			}
		}
	   });
	}

	public void shutdown() {
		
		for(Peer peer : this.getPeers()) {
			
			peer.disconnect();
			
		}
		
		peer_cleaner.JMStop();

	}

	public void start() {

		peer_cleaner = new PeerCleaner();
		
		peer_cleaner.start();
		
	}

	private boolean hasPeer(String address) {
		
		for(Peer peer : peer_list.values()) {
			
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
			
			if (peer_list.containsKey(peer.getID())) continue;
			
			if (low_id_peer_list.containsKey(peer.getID())) continue;
			
			added_peers.add(peer);
				
			if (!peer.isHighID()) {
				if (!low_id_peer_list.containsKey(peer.getID()))
					
					low_id_peer_list.put(peer.getID(), peer);
			}
			else {
					addPeer(peer);
				}
			
		}
		
		DownloadManagerFactory.getInstance().addDownloadPeers(fileHash, added_peers);
		
	}
	
	public float getDownloadSpeed() {
		float speed = 0;
		
		for(Peer peer : peer_list.values()) {
			speed+=peer.getDownloadSpeed();
		}
		
		return speed;
	}

	public float getUploadSpeed() {
		float speed = 0;
		
		for(Peer peer : peer_list.values()) {
			speed+=peer.getUploadSpeed();
		}
		
		return speed;
	}
	
	
	private class PeerCleaner extends JMThread {

		private boolean stop = false;
		
		public PeerCleaner() {
			
			super("Peer cleaner");
		
		} 

		
		public void JMStop() {
			
			stop = true;
			synchronized(this) {
				
				interrupt();
				
			}
			
		}

		public void run() {
			
			while(!stop) {
				
				try {
					
					Thread.sleep(1000);
					
				} catch (InterruptedException e) {
					
					if (stop) return;
					
					continue;
					
				}
				
				
				for(Peer peer : peer_list.values()) {
					
					if (peer.isConnected()) continue;
					
					if (System.currentTimeMillis()-peer.getConnectTime()>=3000) {
						
						peer.disconnect();
						
					}
					
				}
				
				for(Peer peer : unknown_peer_list) {
					
					if (peer.isConnected()) continue;
					
					if (System.currentTimeMillis()-peer.getConnectTime()>=3000) {
						
						peer.disconnect();
						
					}
					
				}
				
			}
			
		}
		
	}

}
