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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.edonkey.impl.Peer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:47 $$
 */
public class UploadQueue {
	
	private Queue<Peer> peer_queue = new ConcurrentLinkedQueue<Peer>();
	
	public void addPeer(Peer peer) {
		
		peer_queue.offer(peer);
		
	}
	
	public Peer getLastPeer() {
		
		return peer_queue.peek();
		
	}
	
	public boolean hasPeer(Peer peer) {
		
		for(Peer peer2 : peer_queue)
			
			if (peer2.equals(peer))
				
				return true;
		
		return false;
		
	}
	
	public int getPeerQueueID(Peer peer) {
		
		int i = 0;
		
		for(Peer check_peer : peer_queue)
			
			if (peer.equals(check_peer))
				return i;
			else 
				i++;
		
		return 0;
	}
	
	public void removePeer(Peer peer) {
		
		peer_queue.remove(peer);
		
	}
	
	public int size() {
		
		return peer_queue.size();
		
	}
	
	public Peer remove() {
		
		return peer_queue.poll();
		
	}
	
	public void add(Peer peer) {
		
		peer_queue.add(peer);
		
	}
	
	public String toString() {
		
		String result="";
		int id = 0;
		for(Peer peer : peer_queue) {
			
			result += peer +" "+id+" \n";
			id++;
		}
		
		return result;
	}

}
