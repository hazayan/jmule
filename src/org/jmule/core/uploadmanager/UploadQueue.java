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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMIterable;
import org.jmule.core.JMIterator;
import org.jmule.core.edonkey.impl.Peer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:56:00 $$
 */
public class UploadQueue {
	
	private Queue<UploadQueueElement> upload_queue = new ConcurrentLinkedQueue<UploadQueueElement>();
	
	public void addPeer(Peer peer) {
		upload_queue.offer(new UploadQueueElement(peer));
	}
	
	public Peer getLastPeer() {
		UploadQueueElement element = upload_queue.peek();
		if (element == null) return null;
		return element.getPeer();
		
	}
	
	public Queue<UploadQueueElement> getQueue() {
		return upload_queue;
	}
	
	public void clear() {
		upload_queue.clear();
	}
	
	public boolean hasPeer(Peer peer) {
		
		for(UploadQueueElement queue_element : upload_queue)
			
			if (queue_element.getPeer().equals(peer))
				
				return true;
		
		return false;
		
	}
	
	public void setPeer(Peer peer) {
		for(UploadQueueElement queue_element : upload_queue)
			if (queue_element.getPeer().equals(peer))
				queue_element.setPeer(peer);
	}
	
	public int getPeerQueueID(Peer peer) {
		
		int i = 0;
		
		for(UploadQueueElement queue_element : upload_queue)
			
			if (queue_element.getPeer().equals(peer))
				return i;
			else 
				i++;
		
		return -1;
	}
	
	public void removePeer(Peer peer) {
		
		upload_queue.remove(peer);
		
	}
	
	public int size() {
		
		return upload_queue.size();
		
	}
	
	public Peer pool() {
		
		return upload_queue.poll().getPeer();
		
	}
			
	public JMIterable<Peer> getPeers() {
		List<Peer> list = new LinkedList<Peer>();
		for(UploadQueueElement element : upload_queue)
			list.add(element.getPeer());
		return new JMIterable<Peer>(new JMIterator<Peer>(list.iterator()));
	}
	
	public String toString() {
		
		String result="";
		int id = 0;
		for(Peer peer : getPeers()) {
			
			result += peer +" "+id+" \n";
			id++;
		}
		
		return result;
	}

	public class UploadQueueElement {
		
		private Peer peer;
		
		private long addTime;
		
		public UploadQueueElement(Peer uploadPeer) {
			peer = uploadPeer;
			addTime = System.currentTimeMillis();
		}
		
		public void setPeer(Peer peer) {
			addTime = System.currentTimeMillis();
			this.peer = peer;
		}
		
		public Peer getPeer() {
			return peer;
		}
		
		public long getTime() {
			return addTime;
		}
	}
	
}

