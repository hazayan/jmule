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
package org.jmule.core.jkad.search;

import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_HELLO_RES;

import java.util.ArrayList;
import java.util.List;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.PacketListener;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.routingtable.KadContact;


/**
 * Created on Jan 16, 2009
 * @author binary256
 * @version $Revision: 1.17 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/28 17:50:25 $
 */
public class SourceSearchTask extends SearchTask {
	private List<KadContact> used_contacts = new ArrayList<KadContact>();
	private LookupTask lookup_task = null;
	private long fileSize = 0;
	public SourceSearchTask(Int128 searchID,long fileSize) {
		super(searchID);
		this.fileSize = fileSize;
	}

	public void startSearch() throws JKadException {
		isStarted = true;
		
		lookup_task = new LookupTask(RequestType.FIND_NODE, searchID, JKadConstants.toleranceZone) {
			public void lookupTimeout() {
			}

			public void processToleranceContacts(ContactAddress sender,
					List<KadContact> results) {
				for(KadContact contact : results) {
					used_contacts.add(contact);
					KadPacket packet = null;
					if (contact.supportKad2())
						try {
							packet = PacketFactory.getHelloReq2Packet(TagList.EMPTY_TAG_LIST);
						} catch (JMException e) {
							e.printStackTrace();
						}
						else
						try {
							packet = PacketFactory.getHello1ReqPacket();
						} catch (JMException e) {
							e.printStackTrace();
						}
					_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
					PacketListener listener = null;
					if (contact.supportKad2())
						listener = new PacketListener(KADEMLIA2_HELLO_RES, contact.getContactAddress().getAsInetSocketAddress()) {
							public void packetReceived(KadPacket packet) {
								KadPacket responsePacket = PacketFactory.getSearchSourceReq2Packet(searchID,(short)0,fileSize);
								_network_manager.sendKadPacket(responsePacket, new IPAddress(packet.getAddress()), packet.getAddress().getPort());
							}
						};
						else
						listener = new PacketListener(KADEMLIA_HELLO_RES, contact.getContactAddress().getAsInetSocketAddress()) {
							public void packetReceived(KadPacket packet) {
								KadPacket responsePacket = PacketFactory.getSearch1ReqPacket(searchID,true);
								_network_manager.sendKadPacket(responsePacket, new IPAddress(packet.getAddress()), packet.getAddress().getPort());
							}
						};
					_jkad_manager.addPacketListener(listener);
				}
			}
			
			public void stopLookupEvent() {
				stopSearch();
			}
			
		};
		lookup_task.setTimeOut(JKadConstants.SEARCH_SOURCES_TIMEOUT);
		Lookup.getSingleton().addLookupTask(lookup_task);
		if (listener!=null)
			listener.searchStarted();
	}

	public void stopSearch() {
		if (!isStarted) return;
		isStarted = false;
		
		if (listener!=null)
			listener.searchFinished();
		//Lookup.getSingleton().removeLookupTask(searchID);
		Search.getSingleton().removeSearchID(searchID);
	}

	public void stopSearchRequest() {
		if (!isStarted) return;
		Lookup.getSingleton().removeLookupTask(searchID);
	}
}
