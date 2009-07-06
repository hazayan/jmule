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

import static org.jmule.core.jkad.JKadConstants.ContactType.Active;
import static org.jmule.core.jkad.JKadConstants.ContactType.Active1Hour;
import static org.jmule.core.jkad.JKadConstants.ContactType.Active2MoreHours;
import static org.jmule.core.jkad.JKadConstants.ContactType.JustAdded;
import static org.jmule.core.jkad.JKadConstants.ContactType.ScheduledForRemoval;
import static org.jmule.core.utils.Convert.byteToHexString;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKad;
import org.jmule.core.jkad.JKadUDPKey;
import org.jmule.core.jkad.JKadConstants.ContactType;

/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */

public class KadContact {

	private ClientID contactID;
	private Int128 contactDistance;
	private ContactAddress contactAddress;
	private int tcp_port;
	private ContactType contactType;
	private long creation;
	private long expiration;
	private byte version;
	private JKadUDPKey kadUDPKey;
	
	private long UDPFirewallQueries = 0;
	private long UDPFirewallResponses = 0;
	private long LastUDPFirewallResponse = 0;
	
	private long lastResponse = System.currentTimeMillis();
	
	private boolean connected = false;
	
	public KadContact() {
	}
	
	public KadContact(ClientID contactID,
			ContactAddress address, int tcpPort,
			byte version, JKadUDPKey kadUDPKey) {
		super();
		this.contactID = contactID;
		this.contactAddress = address;
		this.tcp_port = tcpPort;
		this.version = version;
		this.kadUDPKey = kadUDPKey;
		
		contactDistance = contactID.clone();
		contactDistance.XOR(JKad.getInstance().getClientID());
		
		creation = System.currentTimeMillis();
		expiration = 0;
	}
	
	public String toString() {
		String result = "";
		
		result += "Contact ID : " + contactID +"\n";
		result += "Address : " + getIPAddress()+"\n";
		result += "Type : " + getContactType()+"\n";
	//	result += "Contact distance : " + contactDistance;
	//	result += " " + contactDistance.toLong();
		return result;
	}
	
	public ClientID getContactID() {
		return contactID;
	}
	public void setContactID(ClientID contactID) {
		this.contactID = contactID;
	}
	public Int128 getContactDistance() {
		return contactDistance;
	}
	public void setContactDistance(Int128 contactDistance) {
		this.contactDistance = contactDistance;
	}
	
	public int getTCPPort() {
		return tcp_port;
	}
	public void setTCPPort(int tcpPort) {
		this.tcp_port = tcpPort;
	}
		
	public void setUDPPort(int udpPort) {
		contactAddress.setUdpPort(udpPort);
	}
	
	public ContactType getContactType() {
		return contactType;
	}
	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}
	public long getCreation() {
		return creation;
	}
	public void setCreation(long creation) {
		this.creation = creation;
	}
	public long getExpiration() {
		return expiration;
	}
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public JKadUDPKey getKadUDPKey() {
		return kadUDPKey;
	}
	public void setKadUDPKey(JKadUDPKey kadUDPKey) {
		this.kadUDPKey = kadUDPKey;
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof KadContact)) return false;
		KadContact c = (KadContact) object;
		//return Arrays.equals(contactID.toByteArray(), c.contactID.toByteArray());
		return contactID.toString().equals(c.getContactID().toString());
	}
	
	public int hashCode() {
		return byteToHexString(contactID.toByteArray()).hashCode();
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public long getUDPFirewallQueries() {
		return UDPFirewallQueries;
	}

	public void setUDPFirewallQueries(long firewallQueries) {
		UDPFirewallQueries = firewallQueries;
	}

	public long getUDPFirewallResponses() {
		return UDPFirewallResponses;
	}

	public void setUDPFirewallResponses(long firewallResponses) {
		UDPFirewallResponses = firewallResponses;
	}

	public long getLastUDPFirewallResponse() {
		return LastUDPFirewallResponse;
	}

	public void setLastUDPFirewallResponse(long lastUDPFirewallResponse) {
		LastUDPFirewallResponse = lastUDPFirewallResponse;
	}

	public ContactAddress getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(ContactAddress contactAddress) {
		this.contactAddress = contactAddress;
	}
	
	public IPAddress getIPAddress() {
		return contactAddress.getAddress();
	}
	
	public int getUDPPort() {
		return contactAddress.getUDPPort();
	}

	public long getLastResponse() {
		return lastResponse;
	}

	public void setLastResponse(long lastResponse) {
		this.lastResponse = lastResponse;
	}
	
	void downgrateType() {
		if (contactType == ScheduledForRemoval ) return ;
		switch (contactType) {
			case Active2MoreHours : contactType = Active1Hour; return;
			case Active1Hour : contactType = Active; return ;
			case Active : contactType = JustAdded; return ;
		}
	}
	
	void updateType() {
		if (contactType == Active2MoreHours) return ;
		switch(contactType) {
			case Active1Hour : contactType = Active2MoreHours; return ;
			case Active : contactType = Active1Hour; return ;
			case JustAdded : contactType = Active; return ;
			case ScheduledForRemoval : contactType = JustAdded; return ;
		}
	}
	
		
}