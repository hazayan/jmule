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

import static org.jmule.core.jkad.JKadConstants.FIREWALLED_STATUS_CHANGE_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.FIREWALL_CHECK_CONTACTS;
import static org.jmule.core.jkad.JKadConstants.FIREWALL_CHECK_INTERVAL;
import static org.jmule.core.utils.Convert.reverseArray;

import java.net.InetSocketAddress;
import java.util.List;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.jkad.net.packet.KadPacket;
import org.jmule.core.jkad.net.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.net.JMUDPConnection;


/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/07 18:39:49 $
 */
public class FirewallChecker {
	
	private static FirewallChecker singleton = null;
	
	private boolean firewalled = true;
	private long lastStateChange = System.currentTimeMillis();
	
	private JMUDPConnection udpConnection = null;
	private RoutingTable routingTable = null;
	private Task firewallCheckTask = null;
	
	private IPAddress my_ip_address = null;
	
	private boolean isStarted = false;
	
	public static FirewallChecker getSingleton() {
		if (singleton == null)
			singleton = new FirewallChecker();
		return singleton;
	}

	private FirewallChecker() {
		
	}
	
	public void startNowFirewallCheck() {
		firewallCheckTask.run();
	}

	public boolean isStarted() {
		return isStarted;
	}
	
	public void start() {
		isStarted = true;
		udpConnection = JMUDPConnection.getInstance();
		routingTable = RoutingTable.getSingleton();
		firewallCheckTask = new Task() {
			public void run() {
				if (!JKad.getInstance().isConnected()) {return ;}
				List<KadContact> list = routingTable.getRandomContacts(FIREWALL_CHECK_CONTACTS);
				ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();
				for(KadContact contact : list) {
					KadPacket packet = PacketFactory.getFirewalled1Req(configManager.getTCP());
					udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
				}
			}
		};
		Timer.getSingleton().addTask(FIREWALL_CHECK_INTERVAL, firewallCheckTask, true);
		firewallCheckTask.run();
	}
	
	public void stop() {
		isStarted = false;
		Timer.getSingleton().removeTask(firewallCheckTask);
	}
	
	public boolean isFirewalled() {
		return firewalled;
	}
	
	private void setFirewalled(boolean value) {
		if (System.currentTimeMillis() - lastStateChange < FIREWALLED_STATUS_CHANGE_INTERVAL) return ;
		this.firewalled = value;
		lastStateChange = System.currentTimeMillis();
	}
		
	public void processFirewallRequest(InetSocketAddress sender, int TCPPort) {
		byte data[] = sender.getAddress().getAddress();
		data = reverseArray(data);
		
		KadPacket packet = PacketFactory.getFirewalled1Res(data);
		udpConnection.sendPacket(packet, sender);
	}
	
	public void sendFirewallRequest(InetSocketAddress sender, Int128 contactID) {
		
		KadContact contact = routingTable.getContact(contactID);
		if (contact == null) return ;
		contact.setLastUDPFirewallResponse(System.currentTimeMillis());
		
		if (System.currentTimeMillis() - contact.getLastUDPFirewallResponse() < FIREWALL_CHECK_INTERVAL) return ;
		contact.setUDPFirewallQueries(contact.getUDPFirewallQueries() + 1);
		
		KadPacket packet = PacketFactory.getFirewalled1Req(contact.getTCPPort());
		udpConnection.sendPacket(packet, sender);
	}
	
	public void porcessFirewallResponse(InetSocketAddress sender, IPAddress address) {
		KadContact contact = routingTable.getContact(new IPAddress(sender));
		if (contact != null) {
			contact.setLastUDPFirewallResponse(System.currentTimeMillis());
			contact.setUDPFirewallResponses(contact.getUDPFirewallResponses() + 1);
			
			if (my_ip_address==null) {
				my_ip_address=address;
			}
			else
				if (my_ip_address.equals(address))
					setFirewalled(false);
				else
					setFirewalled(true);
			
		}
	}
	
	public IPAddress getMyIPAddress() {
		if (my_ip_address==null) return new IPAddress(new byte[]{0,0,0,0});
		return my_ip_address;
	}
}
