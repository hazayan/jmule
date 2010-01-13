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

import static org.jmule.core.jkad.JKadConstants.CONCURRENT_LOOKUP_COUNT;
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
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;


/**
 * Created on Jan 9, 2009
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/13 15:47:24 $
 */
public class Lookup {

	private static Lookup singleton = null;
	
	private Map<Int128, LookupTask> lookupTasks = new ConcurrentHashMap<Int128, LookupTask>();
	private long currentRunningTasks = 0;
	private Queue<LookupTask> lookupTasksToRun = new ConcurrentLinkedQueue<LookupTask>();
	
	private RoutingTable routing_table = null;
	private InternalNetworkManager _network_manager;
	
	private Task lookupCleaner;
	
	private boolean isStarted = false;
	
	private List<LookupListener> listenerList = new CopyOnWriteArrayList<LookupListener>();
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public static Lookup getSingleton() {
		if (singleton == null)
			singleton = new Lookup();
		return singleton;
	}
	
	private Lookup() {
		lookupCleaner  = new Task() {
			public void run() {
				runNextTask();
				for(Int128 key : lookupTasks.keySet()) {
					LookupTask task = lookupTasks.get(key);
					if ((System.currentTimeMillis() - task.getResponseTime() > task.getTimeOut())
					|| (System.currentTimeMillis() - task.getStartTime() > JKadConstants.MAX_LOOKUP_RUNNING_TIME)){
						
						task.lookupTimeout();
						task.stopLookup();
						lookupTasks.remove(key);
						notifyListeners(task, LookupStatus.REMOVED);
						if (currentRunningTasks>0)
							currentRunningTasks--;
						runNextTask();
					}
				}
				
			}
			
		};
	}
	
	public Map<Int128,LookupTask> getLookupTasks() { return lookupTasks; }
	
	public void start() {
		isStarted = true;
		routing_table = RoutingTable.getSingleton();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		
		Timer.getSingleton().addTask(LOOKUP_TASK_CHECK_INTERVAL, lookupCleaner, true);
	}
	
	public void stop() {
		isStarted = false;
		for(Int128 key : lookupTasks.keySet()) {
			LookupTask task = lookupTasks.get(key);
			lookupTasksToRun.remove(task);
			if (task.isLookupStarted()) {
				task.stopLookup();
			if (currentRunningTasks>0)
				currentRunningTasks--;
			}
			
			lookupTasks.remove(key);
			notifyListeners(task, LookupStatus.REMOVED);
		}
		Timer.getSingleton().removeTask(lookupCleaner);
	}

	public void addLookupTask(LookupTask task) {		
		lookupTasks.put(task.getTargetID(), task);
		notifyListeners(task, LookupStatus.ADDED);
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
			notifyListeners(task, LookupStatus.REMOVED);
			runNextTask();
		}
		
	}
	
	public boolean hasTask(Int128 targetID) {
		return lookupTasks.containsKey(targetID);
	}
	
	private void runTask(LookupTask task) {
		if (currentRunningTasks<CONCURRENT_LOOKUP_COUNT) {
			currentRunningTasks++;
			task.startLookup();
			notifyListeners(task, LookupStatus.STARTED);
		}else
			lookupTasksToRun.add(task);
	}
	
	private void runNextTask() {
		if (currentRunningTasks>=CONCURRENT_LOOKUP_COUNT) return;
		LookupTask task = lookupTasksToRun.poll();
		if (task == null) return ;
		currentRunningTasks++;
		task.startLookup();
		notifyListeners(task, LookupStatus.STARTED);
	}
	
	public int getLookupLoad() {
		return (int)((currentRunningTasks * 100) / CONCURRENT_LOOKUP_COUNT);
		
	}
	
	public void processRequest(InetSocketAddress sender, RequestType requestType, Int128 targetID, Int128 sourceID, int version ) {
		switch(requestType) {
		case FIND_NODE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID,LOOKUP_NODE_CONTACTS);
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
			break;
		}
		
		case FIND_VALUE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID, SEARCH_CONTACTS);
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
			break;
		}
		
		case STORE : {
			List<KadContact> list = routing_table.getNearestContacts(targetID, PUBLISH_KEYWORD_CONTACT_COUNT);
			
			KadPacket response;
			if (version==1)
				response = PacketFactory.getResponsePacket(targetID, list);
			else
				response = PacketFactory.getResponse2Packet(targetID, list);
			_network_manager.sendKadPacket(response, new IPAddress(sender), sender.getPort());
		}
		
		}
	}
	
	public void processResponse(InetSocketAddress sender, Int128 targetID, List<KadContact> contactList) {
		LookupTask listener = lookupTasks.get(targetID);
		if (listener == null) return ;
		listener.processResults(new ContactAddress(sender), contactList);
	}
	
	public void addListener(LookupListener listener) {
		listenerList.add(listener);
	}
	
	public void removeListener(LookupListener listener) {
		listenerList.remove(listener);
	}
	
	private enum LookupStatus { ADDED, STARTED, REMOVED}
	
	private void notifyListeners(LookupTask lookup, LookupStatus status) {
		
		for(LookupListener listener : listenerList) {
			if (status == LookupStatus.ADDED)
				listener.taskAdded(lookup);
			if (status == LookupStatus.STARTED)
				listener.taskStarted(lookup);
			if (status == LookupStatus.REMOVED)
				listener.taskRemoved(lookup);
		}
	}
	
	
}
