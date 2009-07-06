/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.lookup;

import static org.jmule.core.jkad.JKadConstants.CONCURENT_LOOKUP_COUNT;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_NODE_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_TASK_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.PUBLISH_KEYWORD_CONTACT_COUNT;
import static org.jmule.core.jkad.JKadConstants.SEARCH_CONTACTS;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.net.packet.KadPacket;
import org.jmule.core.jkad.net.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.net.JMUDPConnection;


/**
 * Created on Jan 9, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Lookup {

	private static Lookup singleton = null;
	
	private Map<Int128, LookupTask> lookupTasks = new ConcurrentHashMap<Int128, LookupTask>();
	private long currentRunningTasks = 0;
	private Queue<LookupTask> lookupTasksToRun = new ConcurrentLinkedQueue<LookupTask>();
	
	private RoutingTable routing_table = null;
	private JMUDPConnection udpConnection = null;
	
	private Task lookupCleaner;
	
	private boolean isStarted = false;
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public static Lookup getSingleton() {
		if (singleton == null)
			singleton = new Lookup();
		return singleton;
	}
	
	private Lookup() {
		
	}
	
	public void start() {
		isStarted = true;
		routing_table = RoutingTable.getSingleton();
		udpConnection = JMUDPConnection.getInstance();
		
		lookupCleaner  = new Task() {
			public void run() {
				runNextTask();
				for(Int128 key : lookupTasks.keySet()) {
					LookupTask task = lookupTasks.get(key);
					if (System.currentTimeMillis() - task.getResponseTime() > task.getTimeOut()) {
						task.lookupTimeout();
						task.stopLookup();
						lookupTasks.remove(key);
						if (currentRunningTasks>0)
							currentRunningTasks--;
						runNextTask();
					}
				}
				
			}
			
		};
		
		Timer.getSingleton().addTask(LOOKUP_TASK_CHECK_INTERVAL, lookupCleaner, true);
	}
	
	public void stop() {
		isStarted = false;
		for(Int128 key : lookupTasks.keySet()) {
			LookupTask task = lookupTasks.get(key);
			lookupTasksToRun.remove(task);
			if (task.isLookupStarted()) { task.stopLookup();
			if (currentRunningTasks>0)
				currentRunningTasks--;
			}
			lookupTasks.remove(key);
		}
		Timer.getSingleton().removeTask(lookupCleaner);
	}

	public void addLookupTask(LookupTask task) {		
		lookupTasks.put(task.getTargetID(), task);
		runTask(task);
	}
	
	public void removeLookupTask(Int128 targetID) {
		if (lookupTasks.containsKey(targetID)) {
			LookupTask task = lookupTasks.get(targetID);
			lookupTasksToRun.remove(task);
			if (task.isLookupStarted()) { task.stopLookup(); 
			if (currentRunningTasks>0)
				currentRunningTasks--;}
			lookupTasks.remove(targetID);
			runNextTask();
		}
		
	}
	
	public boolean hasTask(Int128 targetID) {
		return lookupTasks.containsKey(targetID);
	}
	
	private void runTask(LookupTask task) {
		if (currentRunningTasks<CONCURENT_LOOKUP_COUNT) {
			currentRunningTasks++;
			task.startLookup();
		}else
			lookupTasksToRun.add(task);
	}
	
	private void runNextTask() {
		if (currentRunningTasks>=CONCURENT_LOOKUP_COUNT) return;
		LookupTask task = lookupTasksToRun.poll();
		if (task == null) return ;
		currentRunningTasks++;
		task.startLookup();
	}
	
	public int getLookupLoad() {
		return (int)((currentRunningTasks * 100) / CONCURENT_LOOKUP_COUNT);
		
	}
	
	public void processRequest(InetSocketAddress sender, RequestType requestType, Int128 targetID, Int128 sourceID, int version ) {
		switch(requestType) {
		case FIND_NODE : {
			List<KadContact> list = routing_table.getNearestRandomContacts(targetID,LOOKUP_NODE_CONTACTS);
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			udpConnection.sendPacket(response, sender);
			break;
		}
		
		case FIND_VALUE : {
			List<KadContact> list = routing_table.getNearestRandomContacts(targetID, SEARCH_CONTACTS);
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			udpConnection.sendPacket(response, sender);
			break;
		}
		
		case STORE : {
			List<KadContact> list = routing_table.getNearestRandomContacts(targetID, PUBLISH_KEYWORD_CONTACT_COUNT);
			
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			udpConnection.sendPacket(response, sender);
		}
		
		}
	}
	
	public void processResponse(InetSocketAddress sender, Int128 targetID, List<KadContact> contactList) {
		LookupTask listener = lookupTasks.get(targetID);
		if (listener == null) return ;
		listener.processResults(new ContactAddress(sender), contactList);
	}
	
	
	
}