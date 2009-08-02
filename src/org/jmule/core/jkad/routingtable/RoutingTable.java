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
package org.jmule.core.jkad.routingtable;

import static org.jmule.core.jkad.JKadConstants.MAX_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.NODES_DAT;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_CONTACTS_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_CONTACT_IGNORE_TIME;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_CONTACT_TIMEOUT;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_DIFICIT_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_DIFICIT_CONTACTS_STOP;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_MAINTENANCE_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_MAX_MAINTENANCE_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.ROUTING_TABLE_SAVE_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.ContactType.Active;
import static org.jmule.core.jkad.JKadConstants.ContactType.Active1Hour;
import static org.jmule.core.jkad.JKadConstants.ContactType.Active2MoreHours;
import static org.jmule.core.jkad.JKadConstants.ContactType.JustAdded;
import static org.jmule.core.jkad.JKadConstants.ContactType.ScheduledForRemoval;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKad;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.PacketListener;
import org.jmule.core.jkad.JKadConstants.ContactType;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.logger.Logger;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.net.packet.KadPacket;
import org.jmule.core.jkad.net.packet.PacketFactory;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.net.JMUDPConnection;


/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/02 08:01:23 $
 */
public class RoutingTable {

	private static RoutingTable singleton = null;
	
	private Node root = null;
	private List<KadContact> tree_nodes = new CopyOnWriteArrayList<KadContact>();
	private Task maintenanceTask = null;
	private Task routingTableSave = null;
	private Task contact_checker = null;
	
	private int newContacts = 0;
	
	private List<RoutingTableListener> listener_list = new CopyOnWriteArrayList<RoutingTableListener>();
	
	private Map<ContactAddress,MaintenanceContact> maintenanceContacts = new ConcurrentHashMap<ContactAddress,MaintenanceContact>();
	private PacketListener helloListener;
	
	public static RoutingTable getSingleton() {
		if (singleton == null)
			singleton = new RoutingTable();
		
		return singleton;
	}
	
	private RoutingTable() {
		root = new Node(null, new Int128(new byte[]{0x00}), 0, new KBucket());
	}
	
	public void start() {
		loadContacts();
		newContacts = 0;
		
		maintenanceTask = new Task() {
			private LookupTask lookup_new_contacts = null;
			public void run() {
				if (getTotalContacts() < ROUTING_TABLE_DIFICIT_CONTACTS) {
					if ((lookup_new_contacts == null)||(!lookup_new_contacts.isLookupStarted())) {
						Int128 toleranceZone = new Int128();
						toleranceZone.setBit(127, true);
						Int128 fake_target = new Int128(Utils.getRandomInt128());
						lookup_new_contacts = new LookupTask(RequestType.FIND_NODE, fake_target, toleranceZone) {
							public void lookupTimeout() {
							}

							public void processToleranceContacts(
									ContactAddress sender,
									List<KadContact> results) {
								for(KadContact contact : results) {
									addContact(contact);
								}
								
								if (getTotalContacts() > ROUTING_TABLE_DIFICIT_CONTACTS_STOP) {
									Lookup.getSingleton().removeLookupTask(targetID);
								}
							}
							
						};
						Lookup.getSingleton().addLookupTask(lookup_new_contacts);
					}
				}
							
				for(MaintenanceContact maintenance_contact : maintenanceContacts.values()) {
					KadContact contact = maintenance_contact.kadContact;
					
					long contact_time = System.currentTimeMillis() - contact.getLastResponse();
					if (contact_time >= ROUTING_TABLE_CONTACT_TIMEOUT) {
						int rcount = maintenance_contact.requestCount;
						if (rcount<=contact.getContactType().toByte()+1) {
							KadPacket hello_packet = PacketFactory.getHello2ReqPacket(TagList.EMPTY_TAG_LIST);
							JMUDPConnection.getInstance().sendPacket(hello_packet, maintenance_contact.kadContact.getIPAddress(), maintenance_contact.kadContact.getUDPPort());
							maintenance_contact.requestCount++;
							continue;
						}
						
						int beta = maintenance_contact.requestCount - maintenance_contact.responseCount;
						boolean downgrate = false;
						if (beta>contact.getContactType().toByte()+1)
							downgrate = true;
						
						if (downgrate) {
							if (contact.getContactType() == ContactType.ScheduledForRemoval) {
								maintenanceContacts.remove(contact.getContactAddress());
								removeNode(contact);
								continue;
							}
							contact.downgrateType();
							contactUpdated(contact);
							if (contact.getContactType()!=ContactType.ScheduledForRemoval)
								maintenanceContacts.remove(contact.getContactAddress());
							continue;
						}
						maintenanceContacts.remove(contact.getContactAddress());
						continue;
					}
					
				}
				if (maintenanceContacts.size()>=ROUTING_TABLE_MAX_MAINTENANCE_CONTACTS) return ;
				List<KadContact> candidatList = getContactsWithTimeout(ROUTING_TABLE_CONTACT_IGNORE_TIME);
				if (candidatList.size() == 0) return ;
				for(int i = 0;i<ROUTING_TABLE_MAINTENANCE_CONTACTS; i++) {
					KadContact addContact = null;
					do {
						int id = Utils.getRandom(candidatList.size());
						addContact = candidatList.get(id);
						
					}while(maintenanceContacts.containsKey(addContact.getContactAddress()));
					MaintenanceContact c = new MaintenanceContact(addContact);
					maintenanceContacts.put(addContact.getContactAddress(), c);
					
					KadPacket hello_packet = PacketFactory.getHello2ReqPacket(TagList.EMPTY_TAG_LIST);
					JMUDPConnection.getInstance().sendPacket(hello_packet, addContact.getIPAddress(), addContact.getUDPPort());
					c.requestCount++;
				}
			}
			
		};
		
		Timer.getSingleton().addTask(ROUTING_TABLE_CHECK_INTERVAL, maintenanceTask, true);
		
		routingTableSave = new Task() {
			public void run() {
				storeContacts();
			}			
		};
		
		Timer.getSingleton().addTask(ROUTING_TABLE_SAVE_INTERVAL, routingTableSave, true);
		
		contact_checker = new Task() {
			public void run() {
				for(KadContact contact : tree_nodes) {
					long contact_time = System.currentTimeMillis() - contact.getLastResponse();
					if (contact_time >  Active2MoreHours.timeRequired()) {
						if (contact.getContactType()==Active2MoreHours) continue;
						contactUpdated(contact);
						contact.setContactType(Active2MoreHours);
					}
					else
					if (contact_time >  Active1Hour.timeRequired()) { 
						if (contact.getContactType()==Active1Hour) continue;
						contactUpdated(contact);
						contact.setContactType(Active1Hour);
					}
						else
						if (contact_time >  Active.timeRequired()) {
							if (contact.getContactType()==Active) continue;
							contactUpdated(contact);
							contact.setContactType(Active);
						}
							else
							if (contact_time >  JustAdded.timeRequired()) {
								if (contact.getContactType()==JustAdded) continue;
								contactUpdated(contact);
								contact.setContactType(JustAdded);
							}
							else
								if (contact_time >  ScheduledForRemoval.timeRequired()) {
									if (contact.getContactType()==ScheduledForRemoval) continue;
									contactUpdated(contact);
									contact.setContactType(ScheduledForRemoval);
								}
					}
			}
			
		};
		Timer.getSingleton().addTask(ROUTING_TABLE_CONTACTS_CHECK_INTERVAL, contact_checker, true);
		
		helloListener = new PacketListener(JKadConstants.KADEMLIA2_HELLO_RES) {
			public void packetReceived(KadPacket packet) {
				ContactAddress address = new ContactAddress(packet.getAddress());
				if (maintenanceContacts.containsKey(address)) {
					MaintenanceContact contact = maintenanceContacts.get(address);
					contact.responseCount++;
				}
					
			}		
		};
		
		JKad.getInstance().addPacketListener(helloListener);
	}
	
	public void stop() {
		JKad.getInstance().removePacketListener(helloListener);
		
		Timer.getSingleton().removeTask(maintenanceTask);
		Timer.getSingleton().removeTask(routingTableSave);
			
		Timer.getSingleton().removeTask(contact_checker);
		
		tree_nodes.clear();
		allContactsRemoved();
		root = new Node(null, new Int128(new byte[]{0x00}), 0, new KBucket());
	}
	
	public void addContact(KadContact contact) {
		// TODO : Create code to filter myself
		if (!Utils.isGoodAddress(contact.getIPAddress())) {
			Logger.getSingleton().logMessage("Filtered address : "+contact.getIPAddress());
			return;
		}
			
		if (contact.getUDPPort()==4666) return ;
		
		if (hasContact(contact)) return ;
		
		if (getTotalContacts() >= MAX_CONTACTS) return ;
		
		newContacts++;
		Node node = root;

		contact.setContactType(JustAdded);
		
		Int128 contact_distance = contact.getContactDistance();
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = contact_distance.getBit(node_level);
			if (direction)
				node = node.getRight();
			else
				node = node.getLeft();
		}
		
		node.addContact(contact);
		tree_nodes.add(contact);
		
		contactAdded(contact);
	}
	
	public boolean hasContact(KadContact contact) {
		Node node = getNearestKBuket(contact.getContactDistance());
		return node.getSubnet().hasContact(contact);
	}
	
	public KadContact getContact(Int128 contactID) {
		for(KadContact contact : tree_nodes) 
			if (contact.getContactID().equals(contactID)) return contact;
		return null;
	}
	
	public KadContact getContact(InetSocketAddress address) {
		for(KadContact contact : tree_nodes) {
			if (contact.getIPAddress().equals(address))
				if (contact.getUDPPort() == address.getPort())
					return contact;
		}
		return null;
	}
	
	public KadContact getContact(IPAddress address) {
		for(KadContact contact : tree_nodes) 
			if (contact.getIPAddress().equals(address))
				return contact;
		return null;
	}
	
	public int getActiveContacts() {
		return 0;
	}
	
	public int getNewContacts() {
		return newContacts;
	}
	
	public int getTotalContacts() {
		return tree_nodes.size();
	}
	
	/**
	 * Use this method for FIND_NODE request
	 * @param contactCount
	 * @return
	 */
	public List<KadContact> getRandomContacts(int contactCount) {
		return getRandomContacts(contactCount, Collections.EMPTY_LIST);
	}
	
	public List<KadContact> getRandomContacts(int contactCount, List<KadContact> exceptContacts) {
		List<KadContact> list = new LinkedList<KadContact>();
		if (tree_nodes.isEmpty()) return list;
		for(int i = 0;i<contactCount; i++) {
			KadContact contact;
			int checkedContacts = 0;
			do {
				contact = tree_nodes.get(Utils.getRandom(tree_nodes.size()));
				checkedContacts++;
				if (checkedContacts>getTotalContacts()) {
					contact = null;
					break;
				}
			}while(list.contains(contact)|| exceptContacts.contains(contact));
			if (contact == null) continue;
			list.add(contact);
		}
		
		return list;
	}
	
	public List<KadContact> getNearestRandomContacts(Int128 target, int contactCount) {
		Node node = root;
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = target.getBit(node_level);
			if (direction)
				node = node.getRight();
			else
				node = node.getLeft();
		}
		
		return node.getSubnet().getRandomContacts(contactCount);
	}
	
	private Node getNearestKBuket(Int128 contactDistance) {
		Node node = root;
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = contactDistance.getBit(node_level);
			if (direction)
				node = node.getRight();
			else
				node = node.getLeft();
		}
		return node;
	}
	
	public void loadContacts() {
		List<KadContact> contact_list = NodesDat.loadFile(NODES_DAT);
		Logger.getSingleton().logMessage("Loaded contacts : " + contact_list.size());
		for(KadContact contact : contact_list) {
			addContact(contact);
		}
	}
	
	public void storeContacts() {
		NodesDat.writeFile(NODES_DAT, tree_nodes);
	}
	
	public String toString() {
		String result =  "";
		Deque<Node> process_nodes = new LinkedList<Node>();
		
		process_nodes.offerFirst(root);
		
		while(process_nodes.size()!=0) {
			Node node = process_nodes.pollLast();
			
			result += node + "\n";
			
			if (node.getRight() != null)
				process_nodes.offerFirst(node.getRight());
			if (node.getLeft() != null)
				process_nodes.offerFirst(node.getLeft());
		}
		
		return result;
	}
	
	public void addListener(RoutingTableListener listener) {
		listener_list.add(listener);
	}
	
	public void removeListener(RoutingTableListener listener) {
		listener_list.remove(listener);
	}
	
	private void removeNode(KadContact contact) {
		Int128 contactDistance = contact.getContactDistance();
		Node node = root;
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = contactDistance.getBit(node_level);
			if (direction)
				node = node.getRight();
			else
				node = node.getLeft();
		}
		node.getSubnet().remove(contact);
		tree_nodes.remove(contact);
		
		contactRemoved(contact);
	}
	
	private List<KadContact> getContactsWithTimeout(long timeout) {
		long currentTime = System.currentTimeMillis();
		List<KadContact> list = new LinkedList<KadContact>();
	
		for(KadContact contact : tree_nodes) 
			if (currentTime - contact.getLastResponse()>=timeout) 
				list.add(contact);
		
		return list;
	}
	
	public List<KadContact> getContacts() {
		return tree_nodes;
	}
	
	
	private void contactUpdated(KadContact contact) {
		for(RoutingTableListener listener : listener_list)
			listener.contactUpdated(contact);
	}
	
	private void contactAdded(KadContact contact) {
		for(RoutingTableListener listener : listener_list)
			listener.contactAdded(contact);
	}
	
	private void contactRemoved(KadContact contact) {
		for(RoutingTableListener listener : listener_list)
			listener.contactRemoved(contact);
	}
	
	private void allContactsRemoved() {
		for(RoutingTableListener listener : listener_list)
			listener.allContactsRemoved();
	}
	
	class MaintenanceContact {
		public KadContact kadContact = null;
		public int requestCount = 0;
		public int responseCount = 0;
		
		public MaintenanceContact(KadContact kadContact) {
			this.kadContact = kadContact;
		}	
		
		public String toString() {
			return kadContact.getContactAddress()+" " + requestCount + " " + responseCount;
		}
	}
	
}
