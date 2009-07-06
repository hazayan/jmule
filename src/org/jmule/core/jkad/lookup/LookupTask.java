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
import static org.jmule.core.jkad.JKadConstants.LOOKUP_TASK_TIMEOUT;
import static org.jmule.core.jkad.utils.Utils.getNearestContact;
import static org.jmule.core.jkad.utils.Utils.inToleranceZone;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
public abstract class LookupTask {
	
	protected int initialLookupContacts; 
	protected long responseTime;
	protected Int128 targetID;
	protected Int128 toleranceZone;
	protected long timeOut =  LOOKUP_TASK_TIMEOUT;
	
	protected List<KadContact> possibleContacts = new CopyOnWriteArrayList<KadContact> ();
	protected List<KadContact> usedContacts = new CopyOnWriteArrayList<KadContact>();
	protected Map<ContactAddress, RequestedContact> requestedContacts = new ConcurrentHashMap<ContactAddress, RequestedContact>();
	
	protected RoutingTable routingTable;
	protected RequestType requestType;
	
	protected Task contactCleaner = null;
	
	protected boolean lookupStarted = false;
	
	public LookupTask(RequestType requestType, Int128 targetID, Int128 toleranceZone) {
		this(requestType, targetID, toleranceZone, INITIAL_LOOKUP_CONTACTS);
	}
	
	public LookupTask(RequestType requestType, Int128 targetID, Int128 toleranceZone, int initialLookupContacts) {
		this.targetID = targetID;
		this.toleranceZone = toleranceZone;
	
		this.requestType = requestType;
		this.initialLookupContacts = initialLookupContacts;
		responseTime = System.currentTimeMillis();
		routingTable = RoutingTable.getSingleton();
	}
	
	public void startLookup() {
		lookupStarted = true;
		responseTime = System.currentTimeMillis();
		possibleContacts.addAll(routingTable.getRandomContacts(initialLookupContacts));
		
		int count = ALPHA;
		if (count > possibleContacts.size()) count = possibleContacts.size();
		for (int i = 0; i < count; i++) {
			KadContact contact = getNearestContact(targetID, possibleContacts);
			
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
						KadContact contact = getNearestContact(targetID, possibleContacts);
						if (contact == null) return;
						lookupContact(contact);
					}
				}
						
			}
		};
		
		Timer.getSingleton().addTask(LOOKUP_CONTACT_CHECK_INTERVAL, contactCleaner, true);
	}
	
	public void stopLookup() {
		lookupStarted = false;
		
		Timer.getSingleton().removeTask(contactCleaner);
		possibleContacts.clear();
		usedContacts.clear();
		requestedContacts.clear();
	}
	
	public void processResults(ContactAddress sender, List<KadContact> results) {
		responseTime = System.currentTimeMillis();
		
		List<KadContact> alpha  = new LinkedList<KadContact>();
		
		for(KadContact contact : results) {
			if (usedContacts.contains(contact)) continue;
			if (possibleContacts.contains(contact)) continue;
			
			if (inToleranceZone(contact.getContactID(), targetID, toleranceZone)) {
				//System.out.println("In tollerance zone : " + contact);
				alpha.add(contact);
			}
			else 
				possibleContacts.add(contact);
		}
		if (alpha.size()!=0)
			processToleranceContacts(sender, alpha);
		
		requestedContacts.remove(sender);
		//for(int i =0;i<3;i++) {
			KadContact contact = getNearestContact(targetID, possibleContacts);
			if (contact != null) lookupContact(contact);
		//}
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
		
		JMUDPConnection.getInstance().sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
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
	

	public Int128 getToleranceZone() {
		return toleranceZone;
	}

	public void setToleranceZone(Int128 toleranceZone) {
		this.toleranceZone = toleranceZone;
	};
	
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
}
