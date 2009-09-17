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
package org.jmule.core.uploadmanager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMIterator;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.peermanager.Peer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:30:12 $$
 */
public class UploadQueue {
	private static final String PEER_SEPARATOR 				=   ":";
	private static UploadQueue instance = null;
	
	public static UploadQueue getInstance() {
		if (instance == null)
			instance = new UploadQueue();
		return instance;
	}
	
	private List<UploadQueueContainer> upload_queue = new CopyOnWriteArrayList<UploadQueueContainer>();
	private Set<String> stored_peers = new ConcurrentSkipListSet<String>();
	
	enum PeerQueueStatus { SLOTGIVEN, SLOTTAKEN }
	
	private UploadQueue() {
	}
	
	void addPeer(Peer peer) throws UploadQueueException {
		if (!(size() < ConfigurationManager.UPLOAD_QUEUE_SIZE))
			throw new UploadQueueException("Queue full");
		stored_peers.add(peer.getIP() + PEER_SEPARATOR + peer.getPort());
		upload_queue.add(new UploadQueueContainer(peer));
	}
	
	boolean isFull() {
		return (!(size() < ConfigurationManager.UPLOAD_QUEUE_SIZE));
	}
	
	private List<UploadQueueContainer> getTopContainers() {
		List<UploadQueueContainer> result = new LinkedList<UploadQueueContainer>();
		if (size()<ConfigurationManager.UPLOAD_QUEUE_SLOTS)
			for(UploadQueueContainer container : upload_queue)
				result.add(container);
		else {
			for(int i = 0;i<ConfigurationManager.UPLOAD_QUEUE_SLOTS;i++) 
				result.add(upload_queue.get(size()-i));
		}
		return result;
	}
	
	List<Peer> getSlotPeers(PeerQueueStatus... status) {
		List<UploadQueueContainer> containers =  getTopContainers();
		List<Peer> peer_list = new LinkedList<Peer>();
		for(UploadQueueContainer container : containers)
			for(PeerQueueStatus s : status)
				if (container.peer_status.contains(s)) {
					peer_list.add(container.peer);
					break;
				}
		return peer_list;
	}
	
	void markSlotGiven(Peer peer) {
		for(UploadQueueContainer container : upload_queue)
			if (container.peer.equals(peer)) {
				container.peer_status.remove(PeerQueueStatus.SLOTTAKEN);
				container.peer_status.add(PeerQueueStatus.SLOTGIVEN);
				return ;
			}
	}
	
	void markSlotTaken(Peer peer) {
		for(UploadQueueContainer container : upload_queue)
			if (container.peer.equals(peer)) {
				container.peer_status.remove(PeerQueueStatus.SLOTGIVEN);
				container.peer_status.add(PeerQueueStatus.SLOTGIVEN);
				return ;
			}
	}
	
	boolean hasPeer(Peer peer) {
		return stored_peers.contains(peer.getIP() + ":" + peer.getPort());
	}
	
	boolean hasPeer(String peerIP, int peerPort) {
		return stored_peers.contains(peerIP + ":" + peerPort);
	}
	
	int getPeerQueueID(Peer peer) {
		int id = 0;
		for (UploadQueueContainer container : upload_queue) 
			if (container.peer.equals(peer))
				return id;
			else
				id++;
		return -1;
	}
	
	void removePeer(Peer peer) {
		int id = getPeerQueueID(peer);
		if (id == -1)
			return;
		upload_queue.remove(id);
		stored_peers.remove(peer.getID() + ":" + peer.getPort());
	}
	
	void moveToLast(Peer peer) {
		int id = getPeerQueueID(peer);
		UploadQueueContainer container = upload_queue.get(id);
		upload_queue.remove(container);
		upload_queue.add(container);
	}
	
	public int size() {
		return upload_queue.size();
	}
	
	void clear() {
		upload_queue.clear();
		stored_peers.clear();
	}
	
			
	public JMIterable<Peer> getPeers() {
		List<Peer> list = new LinkedList<Peer>();
		for (UploadQueueContainer element : upload_queue)
			list.add(element.peer);
		return new JMIterable<Peer>(new JMIterator<Peer>(list.iterator()));
	}
	
	public String toString() {
		String result = "";
		int id = 0;
		for (Peer peer : getPeers()) {
			result += peer + " " + id + " \n";
			id++;
		}
		return result;
	}

	class UploadQueueContainer {
		private Peer peer;
		private long addTime;
		private Set<PeerQueueStatus> peer_status = new HashSet<PeerQueueStatus>();

		public UploadQueueContainer(Peer uploadPeer) {
			peer = uploadPeer;
			addTime = System.currentTimeMillis();
			peer_status.add(PeerQueueStatus.SLOTTAKEN);
		}

	}
	
}

