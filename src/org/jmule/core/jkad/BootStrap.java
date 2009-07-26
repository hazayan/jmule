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
package org.jmule.core.jkad;

import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_REMOVE_TIME;
import static org.jmule.core.jkad.JKadConstants.BOOTSTRAP_STOP_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.MIN_CONTACTS_TO_SEND_BOOTSTRAP;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.edonkey.packet.tag.TagList;
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
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/26 06:11:27 $
 */
public class BootStrap {

	private static BootStrap singleton = null;

	private JMUDPConnection udpConnection = null;
	private RoutingTable routingTable = null;
	
	private List<KadContact> bootStrapContacts = new CopyOnWriteArrayList<KadContact>();
	private List<KadContact> usedContacts 	   = new CopyOnWriteArrayList<KadContact>();
	private int bootStrapResponses = 0;
	private Task bootStrapTask;
	
	private boolean isStarted = false;
	private PacketListener bootStrapResponseListener;


	public static BootStrap getInstance() {
		if (singleton == null)
			singleton = new BootStrap();
		return singleton;
	}
	
	private BootStrap(){
		udpConnection = JMUDPConnection.getInstance();
		routingTable = RoutingTable.getSingleton();
	}
	
	public void start() {
		isStarted = true;
		List<KadContact> contactList = routingTable.getRandomContacts(BOOTSTRAP_CONTACTS);
		
		bootStrapResponses = 0;
		bootStrapContacts.clear();
		bootStrapContacts.addAll(contactList);
		
		usedContacts.clear();
		usedContacts.addAll(contactList);
		
		for(KadContact contact : contactList) {
			bootStrapContacts.add(contact);
			usedContacts.add(contact);
			if (routingTable.getTotalContacts()<MIN_CONTACTS_TO_SEND_BOOTSTRAP) {					
				KadPacket packet = PacketFactory.getBootStrap1ReqPacket();
				udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
			} else {
				KadPacket packet = PacketFactory.getHello2ReqPacket(org.jmule.core.edonkey.packet.tag.TagList.EMPTY_TAG_LIST);
				udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
			}
		}
		
		bootStrapTask = new Task() {
			public void run() {
				if (bootStrapResponses >= BOOTSTRAP_STOP_CONTACTS) {
					System.out.println("BootStrap completed!");
					
					JKad.getInstance().removePacketListener(bootStrapResponseListener);
					
					// stop task if already have enough contacts
					Timer.getSingleton().removeTask(this);
					FirewallChecker.getSingleton().startNowFirewallCheck();
					return ;
				}
				for(KadContact contact : bootStrapContacts) {
					long time = System.currentTimeMillis() - contact.getLastResponse();
					if (time >= BOOTSTRAP_REMOVE_TIME) {
						bootStrapContacts.remove(contact);
						continue;
					}
					
				}
				
				int contactCount = BOOTSTRAP_CONTACTS - bootStrapContacts.size();
				if (contactCount<=0) return ;
				
				List<KadContact> contactList = routingTable.getRandomContacts(contactCount,usedContacts);
				
				for(KadContact contact : contactList) {
					bootStrapContacts.add(contact);
					usedContacts.add(contact);
					if (routingTable.getTotalContacts()<MIN_CONTACTS_TO_SEND_BOOTSTRAP) {					
						KadPacket packet = PacketFactory.getBootStrap1ReqPacket();
						udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
					} else {
						KadPacket packet = PacketFactory.getHello2ReqPacket(TagList.EMPTY_TAG_LIST);
						udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
					}
				}
			}
		};
		Timer.getSingleton().addTask(BOOTSTRAP_CHECK_INTERVAL, bootStrapTask, true);
	
		bootStrapResponseListener = new PacketListener(JKadConstants.KADEMLIA2_HELLO_RES) {
			public void packetReceived(KadPacket packet) {
				IPAddress address= new IPAddress(packet.getAddress());
				for(KadContact c : usedContacts)
					if (c.getContactAddress().getAddress().equals(address)) {
						bootStrapResponses++;
						return ;
					}
			}
			
		};
		
		JKad.getInstance().addPacketListener(bootStrapResponseListener);
		
	}
	
	public void stop() {
		isStarted = false;
		if (bootStrapTask== null) return ;
		Timer.getSingleton().removeTask(bootStrapTask);
	}
		
	/**
	 * Bootstrap from client
	 * @param address
	 * @param port
	 */
	public void start(IPAddress address, int port) {
		KadPacket packet = PacketFactory.getBootStrap1ReqPacket();
		udpConnection.sendPacket(packet, address, port);
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public void processBootStrapReq(ClientID clientID, IPAddress ipAddress, int udpPort) {
		List<KadContact> contactList = routingTable.getNearestRandomContacts(clientID, BOOTSTRAP_CONTACTS);
		KadPacket packet = PacketFactory.getBootStrap1ResPacket(contactList);
		udpConnection.sendPacket(packet, ipAddress, udpPort);
	}
	
	public void processBootStrapRes(List<KadContact> contactList) {
		for(KadContact contact : contactList) {
			if (!routingTable.hasContact(contact)) {
				bootStrapResponses++;
				bootStrapContacts.remove(contact);
				
				routingTable.addContact(contact);
								
				//Packet packet = PacketFactory.getRequestPacket(FIND_NODE, contact.getContactID(), Kad.getSingelton().getClientID());
				//udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
			}
		}		
	}
}
