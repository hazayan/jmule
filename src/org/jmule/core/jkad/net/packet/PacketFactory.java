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
package org.jmule.core.jkad.net.packet;

import static org.jmule.core.jkad.JKadConstants.*;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_BOOTSTRAP_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_FIREWALLUDP;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_RES_ACK;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_CALLBACK_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FINDBUDDY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FINDBUDDY_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FIREWALLED2_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FIREWALLED_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FIREWALLED_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_HELLO_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_NOTES_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_PUBLISH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_NOTES_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_SEARCH_RES;
import static org.jmule.core.jkad.JKadConstants.KAD_VERSION;
import static org.jmule.core.utils.Convert.intToByte;
import static org.jmule.core.utils.Convert.intToShort;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import org.jmule.core.JMException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKad;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.utils.Convert;

/**
 * Created on Dec 31, 2008
 * @author binary256
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/16 12:18:20 $
 */
public class PacketFactory {

	// Kad 1.0
	public static KadPacket getBootStrap1ReqPacket() throws JMException {
		KadPacket packet = new KadPacket(KADEMLIA_BOOTSTRAP_REQ, 16 + 4 + 2 + 2 +1);
		insertMyDetails(packet);
		packet.insertData((byte)0x00);
		
		return packet;
	}
	
	public static KadPacket getBootStrap1ResPacket(List<KadContact> contactList) {
		KadPacket packet = new KadPacket(KADEMLIA_BOOTSTRAP_RES, 2 + contactList.size() * (16 + 4 + 2 + 2 +1));
		
		packet.insertData(intToShort(contactList.size()));
		for(KadContact contact : contactList) {
			insertContact(packet, contact);
		}
		
		return packet;
	}
	
	public static KadPacket getHello1ReqPacket() throws JMException {
		KadPacket packet = new KadPacket(KADEMLIA_HELLO_REQ, 16 + 4 + 2 + 2 + 1);
	
		insertMyDetails(packet);
		packet.insertData((byte)0x00);
		
		return packet;
	}
	
	public static KadPacket getHello1ResPacket() throws JMException {
		KadPacket packet = new KadPacket(KADEMLIA_HELLO_RES, 16 + 4 + 2 + 2 + 1);
		
		insertMyDetails(packet);
		packet.insertData((byte)0x00);
		
		return packet;
	}
	
	public static KadPacket getFirewalled1Req(int port) {
		KadPacket packet = new KadPacket(KADEMLIA_FIREWALLED_REQ,2);
		packet.insertData(intToShort(port));
		return packet;
	}
	
	public static KadPacket getFirewalled1Res(byte[] address) {
		KadPacket packet = new KadPacket(KADEMLIA_FIREWALLED_RES,4);
		packet.insertData(address);
		return packet;
	}	
	
	public static KadPacket getRequestPacket(RequestType type, Int128 target, Int128 receiver) {
		KadPacket packet = new KadPacket(KADEMLIA_REQ, 1 + 16 + 16);
		packet.insertData(type.toByte());
		packet.insertData(target.toByteArray());
		packet.insertData(receiver.toByteArray());
		return packet;
	}
	


	public static KadPacket getResponsePacket(Int128 target, List<KadContact> contactList) {
		KadPacket packet = new KadPacket(KADEMLIA_RES, 16 + 1 + contactList.size() * (16 + 4 + 2 + 2 + 1));
		
		packet.insertData(target.toByteArray());
		packet.insertData(intToByte(contactList.size()));
		
		for(KadContact contact : contactList) {
			insertContact(packet, contact);
		}
		
		return packet;
	}
	
	public static KadPacket getPublishReqPacket(Int128 targetID, ClientID clientID, TagList tagList) {
		ByteBuffer tags = tagsToByteBuffer(tagList);
		
		KadPacket packet = new KadPacket(KADEMLIA_PUBLISH_REQ, 16 + 2 + 16 + 2 + tags.capacity());
		packet.insertData(targetID.toByteArray());
		packet.insertData((short)1);
		packet.insertData(clientID.toByteArray());
		packet.insertData(intToByte(tagList.size()));
		tags.position(0);
		packet.insertData(tags);
		
		return packet;
	}
	
	public static KadPacket getPublishResPacket(Int128 targetID, int load) {
		KadPacket packet = new KadPacket(KADEMLIA_PUBLISH_RES, 16 + 1);
		
		packet.insertData(targetID.toByteArray());
		packet.insertData(intToByte(load));
		
		return packet;
	}
	
	public static KadPacket getSearchReqPacket(Int128 targetID, boolean sourceSearch) {
		KadPacket packet = new KadPacket(KADEMLIA_SEARCH_REQ, 16 + 1 +( sourceSearch ? 0 : 1));
		packet.insertData(targetID.toByteArray());
		packet.insertData(sourceSearch ? (byte)0x01 :(byte)0x00 );
		if (!sourceSearch)
			packet.insertData((byte)0x00);
		return packet;
	}
	
	public static KadPacket getSearchResPacket(Int128 targetID, List<Source> sourceList) {
		List<ByteBuffer> tag_list = new LinkedList<ByteBuffer>();
		int tags_size = 0;
		if (sourceList != null)
			for(Source source : sourceList) {
				ByteBuffer tmp = tagsToByteBuffer(source.getTagList());
				tags_size += tmp.capacity();
				tmp.position(0);
				tag_list.add(tmp);
			}
		
		int sourceCount = 0;
		if (sourceList != null)
			sourceCount = sourceList.size();
		
		KadPacket packet = new KadPacket(KADEMLIA_SEARCH_RES, 16 + 2 + sourceCount * (16 + 2) + tags_size );
		
		packet.insertData(targetID.toByteArray());
		packet.insertData(intToShort(sourceCount));
		for(int i = 0;i<sourceCount;i++) {
			Source source = sourceList.get(i);
			ByteBuffer tags = tag_list.get(i);
			packet.insertData(source.getClientID().toByteArray());
			packet.insertData(intToShort(source.getTagList().size()));
			packet.insertData(tags);
		}
		
		return packet;
		
	}
	
	public static KadPacket getNotesReq(Int128 noteID) {
		KadPacket packet = new KadPacket(KADEMLIA_SEARCH_NOTES_REQ,32);
		packet.insertData(noteID.toByteArray());
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		return packet;
	}
		
	public static KadPacket getNotesRes(Int128 noteID, List<Source> contactList) {
		List<ByteBuffer> tag_list = new LinkedList<ByteBuffer>();
		int tags_size = 0;
		if (contactList!=null)
			for(Source source : contactList) {
				ByteBuffer tmp = tagsToByteBuffer(source.getTagList());
				tags_size += tmp.capacity();
				tmp.position(0);
				tag_list.add(tmp);
			}
		
		int contactCount = 0;
		if (contactList!=null)
			contactCount = contactList.size();
		
		KadPacket packet = new KadPacket(KADEMLIA_SEARCH_NOTES_RES, 16 + 2 + contactCount * 16 + tags_size);
		
		packet.insertData(noteID.toByteArray());
		packet.insertData( intToShort(contactCount));
		for(int i = 0;i<contactCount;i++) {
			Source source = contactList.get(i);
			packet.insertData(source.getClientID().toByteArray());
			packet.insertData( intToShort(source.getTagList().size()));
			packet.insertData(tag_list.get(i));
		}
		
		return packet;
	}
	
	public static KadPacket getPublishNotesReq(Int128 noteID, ClientID publisherID, TagList tagList) {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		tag_list.position(0);
		
		KadPacket packet = new KadPacket(KADEMLIA_PUBLISH_NOTES_REQ, 16 + 16 + 2 + tag_list.capacity() );
		
		packet.insertData(noteID.toByteArray());
		packet.insertData(publisherID.toByteArray());
		packet.insertData( intToByte(tagList.size()));
		packet.insertData(tag_list);
		
		return packet;
	}
	
	public static KadPacket getPublishNotesRes(Int128 noteID, int load) {
		KadPacket packet = new KadPacket(KADEMLIA_PUBLISH_NOTES_RES, 16 + 1);
		packet.insertData(noteID.toByteArray());
		packet.insertData( intToByte(load));
		return packet;
	}
		
	public static KadPacket getBuddyReqPacket(ClientID receiverID, ClientID senderID, short clientPort) {
		KadPacket packet = new KadPacket(KADEMLIA_FINDBUDDY_REQ, 16 + 16 + 2);
		packet.insertData(receiverID.toByteArray());
		packet.insertData(senderID.toByteArray());
		packet.insertData(clientPort);
		return packet;
	}
	
	public static KadPacket getBuddyResPacket(ClientID receiverID, ClientID senderID, short clientPort) {
		KadPacket packet = new KadPacket(KADEMLIA_FINDBUDDY_RES, 16 + 16 + 2);
		packet.insertData(receiverID.toByteArray());
		packet.insertData(senderID.toByteArray());
		packet.insertData(clientPort);
		return packet;
	}

	
	public static KadPacket getCallBackRequestPacket(ClientID clientID, FileHash fileHash, short tcpPort) {
		KadPacket packet = new KadPacket(KADEMLIA_CALLBACK_REQ, 16 + 16 + 2);
		packet.insertData(clientID.toByteArray());
		packet.insertData(fileHash.getHash());
		packet.insertData(tcpPort);
		return packet;
	}
	
	// Kad 2.0
	
	public static KadPacket getBootStrap2ReqPacket() {
		KadPacket packet = new KadPacket(KADEMLIA2_BOOTSTRAP_REQ);
		
		return packet;
	}
	
	public static KadPacket getBootStrap2ResPacket(List<KadContact> contactList) throws JMException {
		KadPacket packet = new KadPacket(KADEMLIA2_BOOTSTRAP_RES, 16 + 2 + 1 + 2 + contactList.size() * (16 + 4 + 2 + 2 +1));
		
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();		
		packet.insertData(Convert.intToShort(configManager.getTCP()));
		packet.insertData(JKadConstants.KAD_VERSION);
		packet.insertData(intToShort(contactList.size()));
		for(KadContact contact : contactList) {
			insertContact(packet, contact);
		}
		
		return packet;
	}
	
	public static KadPacket getHello2ReqPacket(TagList tagList) throws JMException {
		ByteBuffer tags = tagsToByteBuffer(tagList);
		
		KadPacket packet = new KadPacket(KADEMLIA2_HELLO_REQ, 16 + 2 + 1 + 1 + tags.capacity());
		
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		
		ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();
		
		packet.insertData(intToShort(configManager.getTCP()));
		packet.insertData(KAD_VERSION);
		packet.insertData(intToByte(tagList.size()));
		tags.position(0);
		packet.insertData(tags);
		return packet;
		
	}
	
	public static KadPacket getHello2ResPacket(TagList tagList) throws JMException {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		KadPacket packet = new KadPacket(KADEMLIA2_HELLO_RES,16 + 2 + 1 + 1+tag_list.capacity());
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();
		packet.insertData(intToShort(configManager.getTCP()));
		packet.insertData(KAD_VERSION);
		packet.insertData(intToByte(tag_list.capacity()));
		packet.insertData(tag_list);
		return packet;
	}
	
	public static KadPacket getHelloResAck2Packet(TagList tagList) {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		KadPacket packet = new KadPacket(KADEMLIA2_HELLO_RES_ACK,16 + 1 + tag_list.capacity());
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		packet.insertData(Convert.intToByte(tagList.size()));
		packet.insertData(tag_list);
		return packet;
	}
	
	public static KadPacket getFirewalledReq2Packet(byte connectionOptions) throws JMException {
		KadPacket packet = new KadPacket(KADEMLIA_FIREWALLED2_REQ, 2 + 16 + 1);
		packet.insertData(ConfigurationManagerFactory.getInstance().getTCP());
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		packet.insertData(connectionOptions);
		return packet;
	}
	
	public static KadPacket getFirewallUDP2Packet(byte errorCode, short port) {
		KadPacket packet = new KadPacket(KADEMLIA2_FIREWALLUDP, 1 + 2);
		packet.insertData(errorCode);
		packet.insertData(port);
		return packet;
	}
	
	public static KadPacket getRequest2Packet(RequestType type, Int128 target, Int128 receiver) {
		KadPacket packet = new KadPacket(KADEMLIA2_REQ, 1 + 16 + 16);
		packet.insertData(type.toByte());
		packet.insertData(target.toByteArray());
		packet.insertData(receiver.toByteArray());
		return packet;
	}
	
	public static KadPacket getResponse2Packet(Int128 target, List<KadContact> contactList) {
		KadPacket packet = new KadPacket(KADEMLIA2_RES, 16 + 1 + contactList.size() * (16 + 4 + 2 + 2 + 1));
		
		packet.insertData(target.toByteArray());
		packet.insertData(intToByte(contactList.size()));
		
		for(KadContact contact : contactList) {
			insertContact(packet, contact);
		}
		
		return packet;
	}
	
	public static KadPacket getSearchKey2ReqPacket(Int128 targetID) {
		KadPacket packet = new KadPacket(KADEMLIA2_SEARCH_KEY_REQ, 16+1 + 1);
		packet.insertData(targetID.toByteArray());
		packet.insertData(0);
		packet.insertData(0);
		return packet;
	}
	
	public static KadPacket getSearchSource2ReqPacket(Int128 targetID, short startPosition,long fileSize) {
		KadPacket packet = new KadPacket(KADEMLIA2_SEARCH_SOURCE_REQ, 16 + 2 + 8);
		packet.insertData(targetID.toByteArray());
		packet.insertData(startPosition);
		packet.insertData(fileSize);
		return packet;
	}
	
	public static KadPacket getNotes2Req(Int128 noteID, long fileSize) {
		KadPacket packet = new KadPacket(KADEMLIA2_SEARCH_NOTES_REQ,16 + 8);
		packet.insertData(noteID.toByteArray());
		packet.insertData(fileSize);
		return packet;
	}
	
	public static KadPacket getSearchRes2Packet(Int128 targetID, List<Source> sourceList) {
		List<ByteBuffer> tag_list = new LinkedList<ByteBuffer>();
		int tags_size = 0;
		if (sourceList != null)
			for(Source source : sourceList) {
				ByteBuffer tmp = tagsToByteBuffer(source.getTagList());
				tags_size += tmp.capacity();
				tmp.position(0);
				tag_list.add(tmp);
			}
		
		int sourceCount = 0;
		if (sourceList != null)
			sourceCount = sourceList.size();
		
		KadPacket packet = new KadPacket(KADEMLIA2_SEARCH_RES, 16+16 + 2 + sourceCount * (16 + 2) + tags_size );
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		packet.insertData(targetID.toByteArray());
		packet.insertData( intToShort(sourceCount));
		for(int i = 0;i<sourceCount;i++) {
			Source source = sourceList.get(i);
			ByteBuffer tags = tag_list.get(i);
			packet.insertData(source.getClientID().toByteArray());
			packet.insertData( intToShort(source.getTagList().size()));
			packet.insertData(tags);
		}
		
		return packet;
		
	}
	
	public static KadPacket getPublishKeyReq2Packet(ClientID clientID, Int128 keywordID, TagList tagList) {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		KadPacket packet = new KadPacket(KADEMLIA2_PUBLISH_KEY_REQ,16 + 2 + 16 + 1 + tag_list.capacity());
		
		packet.insertData(clientID.toByteArray());
		packet.insertData((short)1);
		packet.insertData(keywordID.toByteArray());
		packet.insertData( intToByte(tagList.size()));
		packet.insertData(tag_list);
		return packet;
	}
	
	public static KadPacket getPublishSource2Packet(ClientID clientID, ClientID sourceID, TagList tagList) {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		KadPacket packet = new KadPacket(KADEMLIA2_PUBLISH_SOURCE_REQ,16 + 16 + 1 + tag_list.capacity());
		packet.insertData(clientID.toByteArray());
		packet.insertData(sourceID.toByteArray());
		packet.insertData( intToByte(tagList.size()));
		packet.insertData(tag_list);
		return packet;
	}
	
	public static KadPacket getPublishNotes2Packet(ClientID sourceID, TagList tagList) {
		ByteBuffer tag_list = tagsToByteBuffer(tagList);
		KadPacket packet = new KadPacket(KADEMLIA2_PUBLISH_SOURCE_REQ, 16 + 1 + tag_list.capacity());
		
		packet.insertData(sourceID.toByteArray());
		packet.insertData(Convert.intToByte(tagList.size()));
		packet.insertData(tag_list.array());
		
		return packet;
		
	}
	
	public static KadPacket getPublishRes2Packet(ClientID clientID, int load) {
		KadPacket packet = new KadPacket(KADEMLIA2_PUBLISH_RES,16 + 1);
		packet.insertData(clientID.toByteArray());
		packet.insertData( intToByte(load));
		return packet;
	}
	
	public static KadPacket getPublishResACK2Packet() {
		KadPacket packet = new KadPacket(KADEMLIA2_PUBLISH_RES_ACK,0);
		return packet;
	}
	
	public static KadPacket getPing2Packet() {
		KadPacket packet = new KadPacket(KADEMLIA2_PING, 0);
		return packet;
	}
	
	public static KadPacket getPong2Packet(int udpPort) {
		KadPacket packet = new KadPacket(KADEMLIA2_PONG, 2);
		packet.insertData(Convert.intToShort(udpPort));
		return packet;
	}
	
	
	private static ByteBuffer tagsToByteBuffer(Iterable<Tag> tagList) {
		List<ByteBuffer> tag_list = new LinkedList<ByteBuffer>();
		int total_tag_size = 0;
		for(Tag tag : tagList) {
			ByteBuffer t = tag.getAsByteBuffer();
			t.position(0);
			total_tag_size += t.capacity();
			tag_list.add(t);
		}
		
		ByteBuffer result = getByteBuffer(total_tag_size);
		for(ByteBuffer tag : tag_list)
			result.put(tag);
		result.position(0);
		return result;
	}


	private static void insertMyDetails(KadPacket packet) throws JMException {
		packet.insertData(JKad.getInstance().getClientID().toByteArray());
		packet.insertData(JKad.getInstance().getIPAddress().getAddress());
		ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();
		packet.insertData(intToShort(configManager.getUDP()));
		packet.insertData(intToShort(configManager.getTCP()));
	}
	
	private static void insertContact(KadPacket packet, KadContact contact) {
		packet.insertData(contact.getContactID().toByteArray());
		packet.insertData(contact.getIPAddress().getAddress());
		packet.insertData(intToShort(contact.getUDPPort()));
		packet.insertData(intToShort(contact.getTCPPort()));
		packet.insertData(contact.getVersion());
	}
	
}
