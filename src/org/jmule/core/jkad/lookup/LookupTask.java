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

import static org.jmule.core.jkad.JKadConstants.ALPHA;
import static org.jmule.core.jkad.JKadConstants.INITIAL_LOOKUP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_CONTACT_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_CONTACT_TIMEOUT;
import static org.jmule.core.jkad.JKadConstants.LOOKUP_TASK_DEFAULT_TIMEOUT;
import static org.jmule.core.jkad.utils.Utils.getNearestContact;
import static org.jmule.core.jkad.utils.Utils.inToleranceZone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;

/**
 * Created on Jan 9, 2009
 * @author binary256
 * @version $Revision: 1.8 $
 * Last changed by $Author: binary255 $ on $Date: 2010/02/03 13:58:26 $
 */
public abstract class LookupTask {
	
	protected int initialLookupContacts; 
	protected long responseTime;
	protected Int128 targetID;
	protected long toleranceZone;
	protected long timeOut =  LOOKUP_TASK_DEFAULT_TIMEOUT;
	
	protected List<KadContact> possibleContacts = new ArrayList<KadContact> ();
	protected List<KadContact> usedContacts = new ArrayList<KadContact>();
	protected Map<ContactAddress, RequestedContact> requestedContacts = new ConcurrentHashMap<ContactAddress, RequestedContact>();
	
	protected RoutingTable routingTable;
	protected RequestType requestType;
	
	protected Task contactCleaner = null;
	
	protected boolean lookupStarted = false;
	
	protected InternalNetworkManager _network_manager;
	protected InternalJKadManager 	 _jkad_manager;
	
	protected long startTime = 0;
	
	public long getStartTime() {
		return startTime;
	}
	
	public LookupTask(RequestType requestType, Int128 targetID, long toleranceZone) {
		this(requestType, targetID, toleranceZone, INITIAL_LOOKUP_CONTACTS);
	}
	
	public LookupTask(RequestType requestType, Int128 targetID, long toleranceZone, int initialLookupContacts) {
		this.startTime = System.currentTimeMillis();
		this.targetID = targetID;
		this.toleranceZone = toleranceZone;
	
		this.requestType = requestType;
		this.initialLookupContacts = initialLookupContacts;
		responseTime = System.currentTimeMillis();
		routingTable = RoutingTable.getSingleton();
		
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_jkad_manager    = (InternalJKadManager) JKadManagerSingleton.getInstance();
	}
	
	public void startLookup() {
		lookupStarted = true;
		responseTime = System.currentTimeMillis();
		possibleContacts.addAll(routingTable.getNearestContacts(targetID, initialLookupContacts));
		
				
		int count = ALPHA;
		if (count > possibleContacts.size()) count = possibleContacts.size();
		for (int i = 0; i < count; i++) {
			KadContact contact = getNearestContact(Utils.XOR(targetID,_jkad_manager.getClientID()), possibleContacts,usedContacts);
			
			lookupContact(contact);
		}
		
		contactCleaner = new Task() {
			public void run() {
				for(ContactAddress address : requestedContacts.keySet()) {
					RequestedContact c = requestedContacts.get(address);
					if (c==null) { requestedContacts.remove(address); continue; }
					if (System.currentTimeMillis() - c.getRequestTime() > LOOKUP_CONTACT_TIMEOUT) {
						requestedContacts.remove(address);
						// lookup next
						KadContact contact = getNearestContact(Utils.XOR(targetID,_jkad_manager.getClientID()), possibleContacts,usedContacts);
						if (contact == null) return;
						lookupContact(contact);
					}
				}
						
			}
		};
		
		Timer.getSingleton().addTask(LOOKUP_CONTACT_CHECK_INTERVAL, contactCleaner, true);
	}
	
	public void stopLookup() {
		stopLookupEvent();
		lookupStarted = false;
		
		Timer.getSingleton().removeTask(contactCleaner);
		possibleContacts.clear();
		usedContacts.clear();
		requestedContacts.clear();
	}
	
	public void  processResults(ContactAddress sender, List<KadContact> results) {
		responseTime = System.currentTimeMillis();
		
		List<KadContact> alpha  = new LinkedList<KadContact>();
		
		for(KadContact contact : results) {
			if (usedContacts.contains(contact)) continue;
			if (possibleContacts.contains(contact)) continue;			
			if (inToleranceZone(Utils.XOR(contact.getContactID(), _jkad_manager.getClientID()), Utils.XOR(targetID, _jkad_manager.getClientID()), toleranceZone)) {

				alpha.add(contact);
			}
			else 
				possibleContacts.add(contact);
		}
		if (alpha.size()!=0) {
			
			processToleranceContacts(sender, alpha);
		}
		
		requestedContacts.remove(sender);
		KadContact contact = getNearestContact(Utils.XOR(targetID, _jkad_manager.getClientID()), possibleContacts,usedContacts);
		if (contact != null) lookupContact(contact);

	}
	
	private void lookupContact(KadContact contact) {
		possibleContacts.remove(contact);
		usedContacts.add(contact);
		RequestedContact requested_contact = new RequestedContact(contact, System.currentTimeMillis());
		
		requestedContacts.put(contact.getContactAddress(), requested_contact);
		KadPacket packet;
		//if (contact.getVersion()>=2)/*47a*/
		//	packet = PacketFactory.getRequest2Packet(requestType, targetID, (Int128)contact.getContactID());
		//else 
			packet = PacketFactory.getRequestPacket(requestType, targetID, (Int128)contact.getContactID());
		_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
	}
	
	public long getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	
	public Int128 getTargetID() {
		return targetID;
	}
	public void setTargetID(Int128 targetID) {
		this.targetID = targetID;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(long timeOut) {
		this.timeOut = timeOut;
	}
	

	public long getToleranceZone() {
		return toleranceZone;
	}

	public void setToleranceZone(long toleranceZone) {
		this.toleranceZone = toleranceZone;
	};
	
	public abstract void stopLookupEvent();
	
	/**
	 * Lookup process timeout
	 */
	public abstract void lookupTimeout();
	
	/**
	 * Process tolerance contacts
	 * @param sender
	 * @param results
	 */
	public abstract void processToleranceContacts(ContactAddress sender, List<KadContact> results);
	
	protected class RequestedContact {
		private KadContact contact;
		private long requestTime;
		
		public RequestedContact(KadContact contact, long requestTime) {
			super();
			this.contact = contact;
			this.requestTime = requestTime;
		}

		public KadContact getContact() {
			return contact;
		}

		public void setContact(KadContact contact) {
			this.contact = contact;
		}

		public long getRequestTime() {
			return requestTime;
		}
		
	}

	public boolean isLookupStarted() {
		return lookupStarted;
	}

	public RequestType getRequestType() {
		return requestType;
	}
}
