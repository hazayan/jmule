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
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.PacketListener;
import org.jmule.core.jkad.JKadConstants.ContactType;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.logger.Logger;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;


/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.11 $
 * Last changed by $Author: binary255 $ on $Date: 2010/02/03 13:58:26 $
 */
public class RoutingTable {
	private InternalJKadManager    _jkad_manager;
	private InternalNetworkManager _network_manager;
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
	
	private boolean is_started = false;
	
	public static RoutingTable getSingleton() {
		if (singleton == null)
			singleton = new RoutingTable();
		
		return singleton;
	}
	
	private RoutingTable() {
		root = new Node(null, new Int128(), 0, new KBucket());
		
		maintenanceTask = new Task() {
			private LookupTask lookup_new_contacts = null;
			public void run() {
				if (getTotalContacts() < ROUTING_TABLE_DIFICIT_CONTACTS) {
					if ((lookup_new_contacts == null)||(!lookup_new_contacts.isLookupStarted())) {
						
						Int128 fake_target = new Int128(Utils.getRandomInt128());
						lookup_new_contacts = new LookupTask(RequestType.FIND_NODE, fake_target, JKadConstants.toleranceZone) {
							public void lookupTimeout() {
							}

							public void stopLookupEvent() {
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
						try {
							Lookup.getSingleton().addLookupTask(lookup_new_contacts);
						} catch (JKadException e) {
							e.printStackTrace();
						}
					}
				}
							
				for(MaintenanceContact maintenance_contact : maintenanceContacts.values()) {
					KadContact contact = maintenance_contact.kadContact;
					
					long contact_time = System.currentTimeMillis() - contact.getLastResponse();
					if (contact_time >= ROUTING_TABLE_CONTACT_TIMEOUT) {
						int rcount = maintenance_contact.requestCount;
						if (rcount<=contact.getContactType().toByte()+1) {
							KadPacket hello_packet;
							try {
								hello_packet = PacketFactory.getHello2ReqPacket(TagList.EMPTY_TAG_LIST);
								_network_manager.sendKadPacket(hello_packet, maintenance_contact.kadContact.getIPAddress(), maintenance_contact.kadContact.getUDPPort());
							} catch (JMException e) {
								e.printStackTrace();
							}

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
							notifyContactUpdated(contact);
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
					
					KadPacket hello_packet;
					try {
						hello_packet = PacketFactory.getHello2ReqPacket(TagList.EMPTY_TAG_LIST);
						_network_manager.sendKadPacket(hello_packet, addContact.getIPAddress(), addContact.getUDPPort());
					} catch (JMException e) {
						e.printStackTrace();
					}
					c.requestCount++;
				}
			}
			
		};
		
		routingTableSave = new Task() {
			public void run() {
				storeContacts();
			}			
		};

		contact_checker = new Task() {
			public void run() {
				for(KadContact contact : tree_nodes) {
					long contact_time = System.currentTimeMillis() - contact.getLastResponse();
					if (contact_time >  Active2MoreHours.timeRequired()) {
						if (contact.getContactType()==Active2MoreHours) continue;
						notifyContactUpdated(contact);
						contact.setContactType(Active2MoreHours);
					}
					else
					if (contact_time >  Active1Hour.timeRequired()) { 
						if (contact.getContactType()==Active1Hour) continue;
						notifyContactUpdated(contact);
						contact.setContactType(Active1Hour);
					}
						else
						if (contact_time >  Active.timeRequired()) {
							if (contact.getContactType()==Active) continue;
							notifyContactUpdated(contact);
							contact.setContactType(Active);
						}
							else
							if (contact_time >  JustAdded.timeRequired()) {
								if (contact.getContactType()==JustAdded) continue;
								notifyContactUpdated(contact);
								contact.setContactType(JustAdded);
							}
							else
								if (contact_time >  ScheduledForRemoval.timeRequired()) {
									if (contact.getContactType()==ScheduledForRemoval) continue;
									notifyContactUpdated(contact);
									contact.setContactType(ScheduledForRemoval);
								}
					}
			}
			
		};
		helloListener = new PacketListener(JKadConstants.KADEMLIA2_HELLO_RES) {
			public void packetReceived(KadPacket packet) {
				ContactAddress address = new ContactAddress(packet.getAddress());
				if (maintenanceContacts.containsKey(address)) {
					MaintenanceContact contact = maintenanceContacts.get(address);
					contact.responseCount++;
				}
					
			}		
		};
	}
	
	public void start() {
		_jkad_manager = (InternalJKadManager) JKadManagerSingleton.getInstance();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		loadContacts();
		newContacts = 0;
		
		Timer.getSingleton().addTask(ROUTING_TABLE_CHECK_INTERVAL, maintenanceTask, true);
		Timer.getSingleton().addTask(ROUTING_TABLE_SAVE_INTERVAL, routingTableSave, true);
		Timer.getSingleton().addTask(ROUTING_TABLE_CONTACTS_CHECK_INTERVAL, contact_checker, true);
		_jkad_manager.addPacketListener(helloListener);
		is_started = true;
	}
	
	public void stop() {
		_jkad_manager.removePacketListener(helloListener);
		
		Timer.getSingleton().removeTask(maintenanceTask);
		Timer.getSingleton().removeTask(routingTableSave);
			
		Timer.getSingleton().removeTask(contact_checker);
		
		tree_nodes.clear();
		notifyAllContactsRemoved();
		root = new Node(null, new Int128(), 0, new KBucket());
		is_started = false;
	}
	
	public boolean isStarted() {
		return is_started;
	}
	
	public synchronized void addContact(KadContact contact) {
		// TODO : Create code to filter myself
		if (!Utils.isGoodAddress(contact.getIPAddress())) {
			Logger.getSingleton().logMessage("Filtered address : "+contact.getIPAddress());
			return;
		}
			
		if (contact.getUDPPort()==4666) return ;
		
		if (hasContact(contact)) return ;
		
		if (getTotalContacts() >= MAX_CONTACTS) return ;
		
		newContacts++;
		
		contact.setContactType(JustAdded);
		
		Int128 contact_distance = contact.getContactDistance();
		
		Node node = root;
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = contact_distance.getBit(node_level);
			if (direction) {
				node = node.getRight();
			}
			else {
				node = node.getLeft();
			}
		}
		
		node.addContact(contact);
		
		tree_nodes.add(contact);
		
		notifyContactAdded(contact);
	}
	
	public boolean hasContact(KadContact contact) {
		Node node = getNearestNode(contact.getContactDistance());
		return node.getKBucket().hasContact(contact);
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
	 * Usage in BootStrap and Firewall check
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
	
	/**
	 * Return contactCount contacts nearest to the target
	 * @param target ID of target, not XOR distance !
	 * @param contactCount
	 * @return
	 */
	public List<KadContact> getNearestContacts(Int128 searchTarget, int contactCount) {
		Node node = root;
		
		Int128 target = Utils.XOR(searchTarget, _jkad_manager.getClientID());
		
		while(!node.isLeaf()) {
			int node_level = node.getLevel();
			boolean direction = target.getBit(node_level);
			if (direction)
				node = node.getRight();
			else
				node = node.getLeft();
		}
		
		if (node.getKBucket().size()>=contactCount)
			return node.getKBucket().getNearestContacts(target, contactCount);
		
		List<KadContact> result = new LinkedList<KadContact>();
				
		Queue<Node> nodeQueue = new LinkedBlockingQueue<Node>();
		List<Node>  usedNodes = new LinkedList<Node>();
		nodeQueue.add(node);
		usedNodes.add(node);
		while(result.size()<contactCount) {
			
			if (nodeQueue.size()==0) {
				break;
			}
			node = nodeQueue.poll();
			
			int count = contactCount - result.size();
			if (node.getKBucket()!=null) {
				List<KadContact> list = node.getKBucket().getNearestContacts(target,count);
				result.addAll(list);
			}
			
			Node leftNode = node.getLeft();
			Node rightNode = node.getRight();
			Node parentNode = node.getParent();
			
			if (leftNode != null) {
				if (!usedNodes.contains(leftNode))  {
					nodeQueue.add(leftNode);
					usedNodes.add(leftNode);
				}
			}
			
			if (rightNode != null) {
				if(!usedNodes.contains(rightNode)) {
					nodeQueue.add(rightNode);
					usedNodes.add(rightNode);
				}
			}
			
			if (parentNode!=null) {
				if (!usedNodes.contains(parentNode))
					nodeQueue.add(parentNode);
					usedNodes.add(parentNode);
			}
						
		}
		
		return result;
	}
	
	private Node getNearestNode(Int128 contactDistance) {
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
		node.getKBucket().remove(contact);
		tree_nodes.remove(contact);
		
		notifyContactRemoved(contact);
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
	
	
	private void notifyContactUpdated(KadContact contact) {
		for(RoutingTableListener listener : listener_list) 
			listener.contactUpdated(contact);
			
	}
	
	private void notifyContactAdded(KadContact contact) {
		for(RoutingTableListener listener : listener_list)
			listener.contactAdded(contact);
	}
	
	private void notifyContactRemoved(KadContact contact) {
		for(RoutingTableListener listener : listener_list)
			listener.contactRemoved(contact);
	}
	
	private void notifyAllContactsRemoved() {
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
