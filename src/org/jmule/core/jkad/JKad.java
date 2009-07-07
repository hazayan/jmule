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
package org.jmule.core.jkad;

import static org.jmule.core.jkad.JKad.JKadStatus.CONNECTED;
import static org.jmule.core.jkad.JKad.JKadStatus.CONNECTING;
import static org.jmule.core.jkad.JKad.JKadStatus.DISCONNECTED;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_HELLO_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_PUBLISH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_KEY_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_NOTES_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA2_SEARCH_SOURCE_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_REQ;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_BOOTSTRAP_RES;
import static org.jmule.core.jkad.JKadConstants.KADEMLIA_FINDBUDDY_REQ;
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
import static org.jmule.core.jkad.JKadConstants.PROTO_KAD_UDP;
import static org.jmule.core.jkad.JKadConstants.PUBLISHER_PUBLISH_CHECK_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.TAG_FILENAME;
import static org.jmule.core.jkad.JKadConstants.TAG_FILERATING;
import static org.jmule.core.jkad.JKadConstants.TAG_FILESIZE;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCEIP;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCEPORT;
import static org.jmule.core.jkad.JKadConstants.TAG_SOURCETYPE;
import static org.jmule.core.jkad.net.packet.tag.TagScanner.scanTag;
import static org.jmule.core.jkad.utils.Utils.getRandomInt128;
import static org.jmule.core.utils.Convert.byteToHexString;
import static org.jmule.core.utils.Convert.byteToInt;
import static org.jmule.core.utils.Convert.hexStringToByte;
import static org.jmule.core.utils.Convert.shortToInt;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMuleManager;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.indexer.Indexer;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.logger.Logger;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.net.packet.CorruptedPacketException;
import org.jmule.core.jkad.net.packet.KadPacket;
import org.jmule.core.jkad.net.packet.PacketFactory;
import org.jmule.core.jkad.net.packet.UnknownPacketOPCodeException;
import org.jmule.core.jkad.net.packet.UnknownPacketType;
import org.jmule.core.jkad.net.packet.tag.IntTag;
import org.jmule.core.jkad.net.packet.tag.StringTag;
import org.jmule.core.jkad.net.packet.tag.Tag;
import org.jmule.core.jkad.net.packet.tag.TagList;
import org.jmule.core.jkad.net.packet.tag.TagScanner;
import org.jmule.core.jkad.publisher.Publisher;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTable;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;
import org.jmule.core.net.JMUDPConnection;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.sharingmanager.SharingManagerFactory;

/**
 * 
 * Utilization : 
 *  Kad.getSingelton()
 *  Kad.initialize()
 *  
 *  more times : 
 *  Kad.connect() / Kad.connect(IP to bootstrap);
 *  Kad.disconnect()
 *  
 *  Kad.stop();
 *  
 *  
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/07 18:38:39 $
 */
public class JKad implements JMuleManager {
	public enum JKadStatus { CONNECTED, CONNECTING, DISCONNECTED }
	
	private static JKad singleton = null;
	
	private ClientID clientID;
	private Queue<KadPacket> packet_queue = new ConcurrentLinkedQueue<KadPacket>();
	private PacketProcessor packetProcessor = null;
	
	private JMUDPConnection udpConnection = null;
	private RoutingTable routing_table = null;
	private Indexer indexer = null;
	private FirewallChecker firewallChecker = null;
	private BootStrap bootStrap = null;
	private Lookup lookup = null;
	private Search search = null;
	private Publisher publisher = null;
		
	private JKadStatus status = DISCONNECTED;
	
	private Map<Byte, List<PacketListener>> packetListeners = new ConcurrentHashMap<Byte, List<PacketListener>>();
	
	private Task filesToPublishChecker;
	
	public static JKad getInstance() {
		if (singleton == null)
			singleton = new JKad();
		return singleton;
	}
	
	private JKad() {
		
	}
	
	public void initialize() {
		Logger.getSingleton().start();
		udpConnection = JMUDPConnection.getInstance();
		routing_table = RoutingTable.getSingleton();
		indexer = Indexer.getSingleton();
		indexer.start();
		
		firewallChecker = FirewallChecker.getSingleton();
		bootStrap = BootStrap.getInstance();
		lookup = Lookup.getSingleton();
		search = Search.getSingleton();
		publisher = Publisher.getInstance();
		
		loadClientID();
	
		filesToPublishChecker = new Task() {
			public void run() {
				SharingManager sharing_manager = SharingManagerFactory.getInstance();
				Publisher publisher = Publisher.getInstance();
				ConfigurationManager config_manager = ConfigurationManagerFactory.getInstance();
				Iterable<SharedFile> shared_files = sharing_manager.getSharedFiles();
				for(SharedFile file : shared_files) {
					Int128 id = new Int128(file.getFileHash());
					if (!publisher.isPublishingSource(id)) {
						List<Tag> tagList = new LinkedList<Tag>();
						tagList.add(new StringTag(TAG_FILENAME, file.getSharingName()));
						tagList.add(new IntTag(TAG_FILESIZE, (int)file.length()));
						tagList.add(new IntTag(TAG_SOURCEIP,org.jmule.core.utils.Convert.byteToInt(JKad.getInstance().getIPAddress().getAddress())));
						tagList.add(new IntTag(TAG_SOURCEPORT,config_manager.getTCP()));
						
						publisher.publishSource(id, tagList);
					}
					if (!publisher.isPublishingKeyword(id)) {
						List<Tag> tagList = new LinkedList<Tag>();
						tagList.add(new StringTag(TAG_FILENAME, file.getSharingName()));
						tagList.add(new IntTag(TAG_FILESIZE, (int)file.length()));
						
						publisher.publishKeyword(id, tagList);
					}
					
					if (file.getTagList().hasTag(TAG_FILERATING)) 
						if (!publisher.isPublishingNote(id)) {
							List<Tag> tagList = new LinkedList<Tag>();
							tagList.add(new StringTag(TAG_FILENAME, file.getSharingName()));
							tagList.add(new IntTag(TAG_FILESIZE, (int)file.length()));
							tagList.add(new IntTag(TAG_FILERATING,file.getFileQuality().getAsInt()));
							
							publisher.publishNote(id, tagList);
						}
				}
			}			
		};		
		
		ConfigurationManagerFactory.getInstance().addConfigurationListener(new ConfigurationAdapter() {
			public void jkadStatusChanged(boolean newStatus) {
				if (newStatus == false)
					if (isConnected()) disconnect();
				if (newStatus==true)
					if (getStatus() == DISCONNECTED) connect();
			}
		});
		
	}
	

	public void connect() {
		setStatus(CONNECTING);
		routing_table.start();
		
		
		lookup.start();
		publisher.start();
		search.start();
		indexer.start();
		
		bootStrap.start();
		
		Timer.getSingleton().addTask(PUBLISHER_PUBLISH_CHECK_INTERVAL, filesToPublishChecker, true);
	}
	
	public void connect(ContactAddress address) {
		setStatus(CONNECTING);
		routing_table.start();

		lookup.start();
		publisher.start();
		search.start();
		indexer.start();
		
		bootStrap.start(address.getAddress(), address.getUDPPort());
				
		Timer.getSingleton().addTask(PUBLISHER_PUBLISH_CHECK_INTERVAL, filesToPublishChecker, true);
	}
	
	public void disconnect() {
		if (!isConnected()) return ;
		Timer.getSingleton().removeTask(filesToPublishChecker);
		
		firewallChecker.stop();
		indexer.stop();
		search.stop();
		
		publisher.stop();
		lookup.stop();
		bootStrap.stop();
		routing_table.stop();
		
		setStatus(DISCONNECTED);
	}
	
	private void setStatus(JKadStatus newStatus) {
		if (status == CONNECTED) {
			if (!firewallChecker.isStarted())
				firewallChecker.start();
		}
		status = newStatus;
	}
	
	private void processPacket(KadPacket packet) throws UnknownPacketOPCodeException, CorruptedPacketException, UnknownPacketType {
		
		if (packet.isCompressed())
			packet.decompress();
		
		if (packet.getProtocol()!=PROTO_KAD_UDP)
			throw new UnknownPacketType();
		
		// update last response
		KadContact rcontact = routing_table.getContact(packet.getAddress());
		if (rcontact != null) {
			rcontact.setLastResponse(System.currentTimeMillis());
			rcontact.setConnected(true);	
		}
		
		// notify packet listeners
		List<PacketListener> listener_list = packetListeners.get(packet.getCommand());
		if (listener_list != null)
			for(PacketListener listener : listener_list)
				try {
					if (listener.processAddress(packet.getAddress()))
						listener.packetReceived(packet);
				}catch(Throwable t) {
					t.printStackTrace();
					Logger.getSingleton().logException(t);
				}
		byte packetOPCode = packet.getCommand();
		ByteBuffer rawData = packet.getAsByteBuffer();
		rawData.position(2);
		;
		if (packetOPCode == KADEMLIA_BOOTSTRAP_REQ) {
			byte[] client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			byte[] address = new byte[4];
			rawData.get(address);
			int udp_port = shortToInt(rawData.getShort());
			int tcp_port = shortToInt(rawData.getShort());
			
			bootStrap.processBootStrapReq(new ClientID(client_id_raw), new IPAddress(address), udp_port);
		} else 
		if (packetOPCode == KADEMLIA_BOOTSTRAP_RES) {
			setStatus(CONNECTED);
			int contactCount = shortToInt(rawData.getShort());
			List<KadContact> contact_list = new LinkedList<KadContact>();
			for(int i = 0;i<contactCount;i++) {
				contact_list.add(packet.getContact());
			}
			
			bootStrap.processBootStrapRes(contact_list);
		} else 
		if (packetOPCode == KADEMLIA_HELLO_REQ) {
			byte[] client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			byte[] address = new byte[4];
			rawData.get(address);
			int udp_port = shortToInt(rawData.getShort());
			int tcp_port = shortToInt(rawData.getShort());
			
			KadContact contact = routing_table.getContact(new ClientID(client_id_raw));
			if (contact!= null) {
				contact.setTCPPort(tcp_port);
				contact.setUDPPort(udp_port);
				
			}
			
			KadPacket response = PacketFactory.getHello1ResPacket();
			udpConnection.sendPacket(response, packet.getAddress());
			
		} else 
		if (packetOPCode == KADEMLIA_HELLO_RES) {
			byte[] client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			byte address[] = new byte[4];
			rawData.get(address);
			int udp_port = shortToInt(rawData.getShort());
			int tcp_port = shortToInt(rawData.getShort());
			
			KadContact contact = routing_table.getContact(new ClientID(client_id_raw));
			setStatus(CONNECTED);
			if (contact!= null) {
				contact.setTCPPort(tcp_port);
				contact.setUDPPort(udp_port);
			}
			// if contact is not in routing table ignore message
			/*KadContact add_contact = new KadContact(new ClientID(client_id_raw), new ContactAddress(new IPAddress(address),udp_port), tcp_port, (byte)0, null);
			routing_table.addContact(add_contact);
			
			add_contact.setConnected(true);*/
			
		}  else
		if (packetOPCode == KADEMLIA_REQ) {
			byte type = rawData.get();
			
			byte[] client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			ClientID targetClientID = new ClientID(client_id_raw);
			
			client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			ClientID receiverClientID = new ClientID(client_id_raw);
			RequestType requestType = RequestType.FIND_VALUE;
			switch(type) {
				case JKadConstants.FIND_VALUE : 
					requestType = RequestType.FIND_VALUE;
					break;
					
				case JKadConstants.STORE : 
					requestType = RequestType.STORE;
					break;
					
				case JKadConstants.FIND_NODE : 
					requestType = RequestType.FIND_NODE;
					break;
			}
			lookup.processRequest(packet.getAddress(), requestType, targetClientID, receiverClientID,1);
		} else 
		if (packetOPCode == KADEMLIA_RES) {	
			byte[] client_id_raw = new byte[16];
			rawData.get(client_id_raw);
			int contactCount = byteToInt(rawData.get());
			List<KadContact> contact_list = new LinkedList<KadContact>();
			for(int i = 0;i<contactCount;i++) {
				contact_list.add(packet.getContact());
			}
			
			lookup.processResponse(packet.getAddress(), new ClientID(client_id_raw), contact_list);
		} else
		if (packetOPCode == KADEMLIA_PUBLISH_REQ){
			byte target_id[] = new byte[16];
			rawData.get(target_id);
			Int128 targetID = new Int128(target_id);
			int clientCount = shortToInt(rawData.getShort());
			
			List<Source> list = new LinkedList<Source>();
			
			for(int i = 0 ;i<clientCount;i++) {
				byte clientID[] = new byte[16];
				rawData.get(clientID);
				int tagCount = shortToInt(rawData.get());
				TagList tagList = new TagList();
				for(int k = 0;k<tagCount;k++) {
					Tag tag = TagScanner.scanTag(rawData);
					tagList.addTag(tag);
				}
				ClientID client_id = new ClientID(clientID);
				Source source = new Source(client_id, tagList);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
				
				KadContact contact = routing_table.getContact(client_id);
				if (contact != null)
					source.setTCPPort(contact.getTCPPort());
				
				list.add(source);
			}
			
			boolean source_load = false;
			
			for(Source source : list) {
				boolean isSourcePublish = false;
				isSourcePublish = source.getTagList().hasTag(TAG_SOURCETYPE);
				if (isSourcePublish) {
					indexer.addFileSource(targetID, source);
					source_load = true;
				}
				else
					indexer.addFileSource(targetID, source);
			}
			KadPacket response = null;
			if (source_load)
				response = PacketFactory.getPublishResPacket(targetID, indexer.getFileSourcesLoad());
			else
				response = PacketFactory.getPublishResPacket(targetID, indexer.getKeywordLoad());
			udpConnection.sendPacket(response, packet.getAddress());
			
		}else
		if (packetOPCode == KADEMLIA_PUBLISH_RES){
			byte targetID[] = new byte[16];
			rawData.get(targetID);
			int load = byteToInt(rawData.get());
			publisher.processGenericResponse(new ClientID(targetID),load);
		} else
		if (packetOPCode == KADEMLIA_SEARCH_NOTES_REQ){
			byte[] targetID = new byte[16];
			rawData.get(targetID);
			List<Source> source_list = indexer.getNoteSources(new Int128(targetID));
			KadPacket response = PacketFactory.getNotesRes(new Int128(targetID), source_list);
			udpConnection.sendPacket(response, packet.getAddress());
		} else
		if (packetOPCode == KADEMLIA_SEARCH_NOTES_RES) {
			byte[] noteID = new byte[16];
			rawData.get(noteID);
			int resultCount = shortToInt(rawData.getShort());
			List<Source> sourceList = new LinkedList<Source>();

			for(int i = 0;i<resultCount;i++) {
				byte[] clientID = new byte[16];
				rawData.get(clientID);
				Convert.updateSearchID(clientID);
				
				int tagCount = shortToInt(rawData.get());
				TagList tagList = new TagList();
				for(int k = 0;k<tagCount;k++) {
					Tag tag = TagScanner.scanTag(rawData);
					tagList.addTag(tag);
				}
				Source source = new Source(new ClientID(clientID),tagList);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
				KadContact contact = RoutingTable.getSingleton().getContact(new ClientID(clientID));
				if (contact!=null)
					source.setTCPPort(contact.getTCPPort());
				sourceList.add(source);
			}
			search.processResults(packet.getAddress(), new Int128(noteID), sourceList);
		} else
		if (packetOPCode == KADEMLIA_PUBLISH_NOTES_REQ) {
			byte[] noteID = new byte[16];
			rawData.get(noteID);
			byte[] publisherID = new byte[16];
			rawData.get(publisherID);
			int tagCount = byteToInt(rawData.get());
			TagList tagList = new TagList(); 
			for(int i = 0;i<tagCount;i++) {
				Tag tag = TagScanner.scanTag(rawData);
				tagList.addTag(tag);
			}
			ClientID publisher_id = new ClientID(publisherID);
			Source source = new Source(publisher_id, tagList);
			source.setAddress(new IPAddress(packet.getAddress()));
			source.setUDPPort(packet.getAddress().getPort());
			
			KadContact contact = routing_table.getContact(publisher_id);
			if (contact != null)
				source.setTCPPort(contact.getTCPPort());
			
			indexer.addNoteSource(new Int128(noteID), source);
			KadPacket response = PacketFactory.getPublishNotesRes(new Int128(noteID), indexer.getNoteLoad());
			udpConnection.sendPacket(response, packet.getAddress());
			
		} else
		if (packetOPCode == KADEMLIA_PUBLISH_NOTES_RES) {
			byte[] noteID = new byte[16];
			rawData.get(noteID);
			int load = byteToInt(rawData.get());
			publisher.processNoteResponse(new Int128(noteID),load);
		} else
		if (packetOPCode == KADEMLIA_SEARCH_REQ) {
			byte targetID[] = new byte[16];
			rawData.get(targetID);
			boolean sourceSearch = false;
			if (rawData.limit() == 17)
				if (rawData.get() == 1)
					sourceSearch = true;
			List<Source> source_list;
			
			if (sourceSearch) 
				source_list = indexer.getFileSources(new Int128(targetID));
			else
				source_list = indexer.getKeywordSources(new Int128(targetID));
			KadPacket response = PacketFactory.getSearchResPacket(new Int128(targetID), source_list);
			udpConnection.sendPacket(response, packet.getAddress());
		} else 
		if (packetOPCode == KADEMLIA_SEARCH_RES) {
			byte targetID[] = new byte[16];
			rawData.get(targetID);
			int resultCount = shortToInt(rawData.getShort());
			
			List<Source> sourceList = new LinkedList<Source>();
							
			for(int i = 0;i<resultCount;i++) {
				byte[] contactID = new byte[16];
				rawData.get(contactID);
				int tagCount = byteToInt(rawData.get());
				TagList tagList = new TagList();
				for(int k = 0;k<tagCount;k++) {
					try {
					Tag tag = TagScanner.scanTag(rawData);
					if (tag == null) continue;
					
					tagList.addTag(tag);
					}catch(Throwable t) {
						t.printStackTrace();
					}
				}
				ClientID client_id = new ClientID(contactID);
				Source source = new Source(client_id, tagList);
				KadContact contact = routing_table.getContact(client_id);
				if (contact != null) {
					source.setUDPPort(contact.getUDPPort());
					source.setTCPPort(contact.getTCPPort());
				}
				sourceList.add(source);
			}
			search.processResults(packet.getAddress(), new Int128(targetID), sourceList);
		}else 
		if (packetOPCode == KADEMLIA_FIREWALLED_REQ) {
			int tcpPort = shortToInt(rawData.getShort());
			firewallChecker.processFirewallRequest(packet.getAddress(), tcpPort);
		} else 
		if (packetOPCode == KADEMLIA_FIREWALLED_RES) { 
			byte[] address = new byte[4];
			rawData.get(address);
			firewallChecker.porcessFirewallResponse(packet.getAddress(), new IPAddress(address));
		}else 
			if (packetOPCode == KADEMLIA2_HELLO_REQ) {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID clientID = new ClientID(client_id_raw);
				int tcpPort = shortToInt(rawData.getShort());
				byte version = rawData.get();
				byte tag_count = rawData.get();
				List<Tag> tag_list  = new LinkedList<Tag>() ;
				for (byte i = 0; i < tag_count; i++) {
					Tag tag = scanTag(rawData);
					if (tag == null) throw new CorruptedPacketException();
					tag_list.add(tag);
				}
				
				KadContact contact = routing_table.getContact(clientID);
				if (contact!= null) {
					contact.setTCPPort(tcpPort);
					contact.setUDPPort(packet.getAddress().getPort());
					contact.setVersion(version);
				}
				
				KadPacket response = PacketFactory.getHello2ResPacket(TagList.EMPTY_TAG_LIST);
				udpConnection.sendPacket(response, packet.getAddress());
			}else 
			if (packetOPCode == KADEMLIA2_HELLO_RES) {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID clientID = new ClientID(client_id_raw);
				int tcpPort = shortToInt(rawData.getShort());
				byte version = rawData.get();
				byte tag_count = rawData.get();
				List<Tag>tag_list  = new LinkedList<Tag>() ;
				
				for (byte i = 0; i < tag_count; i++) {
					Tag tag = scanTag(rawData);
					if (tag == null) throw new CorruptedPacketException();
					tag_list.add(tag);
				}
				
				KadContact contact = routing_table.getContact(clientID);
				setStatus(CONNECTED);
				if (contact!= null) {
					contact.setTCPPort(tcpPort);
					contact.setVersion(version);
				}
				// ignore message if contact is not in routing table
				/*ContactAddress address = new ContactAddress(new IPAddress(packet.getSender()),packet.getSender().getPort());
				KadContact add_contact = new KadContact(clientID, address, tcpPort,version, null);
				routing_table.addContact(add_contact);
				
				add_contact.setConnected(true);*/
				
			} else 
			if (packetOPCode == KADEMLIA2_REQ) {
				byte type = rawData.get();
				
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID targetClientID = new ClientID(client_id_raw);
				
				client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				ClientID receiverClientID = new ClientID(client_id_raw);
				RequestType requestType = RequestType.FIND_VALUE;
				switch(type) {
					case JKadConstants.FIND_VALUE : 
						requestType = RequestType.FIND_VALUE;
						break;
						
					case JKadConstants.STORE : 
						requestType = RequestType.STORE;
						break;
						
					case JKadConstants.FIND_NODE : 
						requestType = RequestType.FIND_NODE;
						break;
				}
				
				lookup.processRequest(packet.getAddress(), requestType, targetClientID, receiverClientID,2);
			} else
			if (packetOPCode == KADEMLIA2_RES) {
				byte[] client_id_raw = new byte[16];
				rawData.get(client_id_raw);
				int contactCount = byteToInt(rawData.get());
				List<KadContact> contact_list = new LinkedList<KadContact>();
				for(int i = 0;i<contactCount;i++) {
					contact_list.add(packet.getContact());
				}
				lookup.processResponse(packet.getAddress(), new ClientID(client_id_raw), contact_list);
			}
			else 
			if (packetOPCode == KADEMLIA2_PUBLISH_KEY_REQ) {
				byte[] client_id = new byte[16];
				rawData.get(client_id);
				ClientID clientID = new ClientID(client_id);
				int count = rawData.getShort();
				for(int i = 0;i<count;i++) {
					byte[] hash = new byte[16];
					rawData.get(hash);
					byte tagCount = rawData.get();
					TagList tag_list = new TagList();
					for(int j = 0;j<tagCount;j++) {
						Tag tag = scanTag(rawData);
						if (tag == null) throw new CorruptedPacketException();
						tag_list.addTag(tag);
					}
					Source source = new Source(clientID, tag_list);
					source.setAddress(new IPAddress(packet.getAddress()));
					source.setUDPPort(packet.getAddress().getPort());
					
					KadContact contact = routing_table.getContact(clientID);
					if (contact != null)
						source.setTCPPort(contact.getTCPPort());
					
					indexer.addKeywordSource(new Int128(hash), source);
				}
				KadPacket response = PacketFactory.getPublishRes2Packet(clientID, indexer.getKeywordLoad());
				udpConnection.sendPacket(response, packet.getAddress());
				
			}else
			if (packetOPCode == KADEMLIA2_PUBLISH_RES) {
				byte targetID[] = new byte[16];
				rawData.get(targetID);
				int load = byteToInt(rawData.get());
				publisher.processGenericResponse(new ClientID(targetID),load);
			}else
			if (packetOPCode == KADEMLIA2_PUBLISH_SOURCE_REQ) {
				byte[] client_id = new byte[16];
				byte[] source_id = new byte[16];
				
				rawData.get(client_id);
				rawData.get(source_id);
				
				int tagCount = rawData.get();
				TagList tag_list = new TagList();
				for(int i = 0;i<tagCount;i++) {
					Tag tag = scanTag(rawData);
					if (tag == null) throw new CorruptedPacketException();
					tag_list.addTag(tag);
				}
				Source source = new Source(new ClientID(client_id), tag_list);
				source.setAddress(new IPAddress(packet.getAddress()));
				source.setUDPPort(packet.getAddress().getPort());
				
				KadContact contact = routing_table.getContact(new ClientID(client_id));
				if (contact != null)
					source.setTCPPort(contact.getTCPPort());
				
				indexer.addFileSource(new Int128(source_id), source);
				
				KadPacket response = PacketFactory.getPublishRes2Packet(new ClientID(client_id), indexer.getFileSourcesLoad());
				udpConnection.sendPacket(response, packet.getAddress());
			}else
			if (packetOPCode == KADEMLIA_FINDBUDDY_REQ) {
				byte[] receiver_id = new byte[16];
				byte[] sender_id   = new byte[16];
				short tcp_port;
				
				rawData.get(receiver_id);
				rawData.get(sender_id);
				tcp_port = rawData.getShort();
				
				ConfigurationManager configManager = ConfigurationManagerFactory.getInstance();
				KadPacket response = PacketFactory.getBuddyResPacket(new ClientID(sender_id), JKad.getInstance().getClientID(), (short)configManager.getTCP());
				udpConnection.sendPacket(response, packet.getAddress());
			} else
				if (packetOPCode == KADEMLIA2_SEARCH_KEY_REQ) {
					byte targetID[] = new byte[16];
					rawData.get(targetID);
					List<Source> source_list;
					
					source_list = indexer.getKeywordSources(new Int128(targetID));
					KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
					udpConnection.sendPacket(response, packet.getAddress());
				}else
				if (packetOPCode == KADEMLIA2_SEARCH_SOURCE_REQ) {
					byte targetID[] = new byte[16];
					rawData.get(targetID);
					short start_pos;
					start_pos = rawData.getShort();
					long fileSize = rawData.getLong();
					List<Source> source_list;
						
					source_list = indexer.getFileSources(new Int128(targetID), start_pos, fileSize);
					KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
					udpConnection.sendPacket(response, packet.getAddress());
				}else
					if (packetOPCode == KADEMLIA2_SEARCH_NOTES_REQ) {
						byte targetID[] = new byte[16];
						rawData.get(targetID);
						long fileSize = rawData.getLong();
						List<Source> source_list;
							
						source_list = indexer.getNoteSources(new Int128(targetID), fileSize);
						KadPacket response = PacketFactory.getSearchRes2Packet(new Int128(targetID), source_list);
						udpConnection.sendPacket(response, packet.getAddress());
					}else
				throw new UnknownPacketOPCodeException();
	}
	
	public boolean isFirewalled() {
		return firewallChecker.isFirewalled();
	}
	
	public void receivePacket(KadPacket packet) {
		if (getStatus()==JKadStatus.DISCONNECTED) return;
		packet_queue.offer(packet);
		if (packetProcessor != null)
			if (packetProcessor.isAlive()) return ;
		packetProcessor = new PacketProcessor();
		packetProcessor.start();
	}
	
	public IPAddress getIPAddress() {
		return firewallChecker.getMyIPAddress();
	}

	public ClientID getClientID() {
		return clientID;
	}
	
	public void addPacketListener(PacketListener listener) {
		List<PacketListener> list = packetListeners.get(listener.getPacketOPCode());
		if (list == null) {
			list = new CopyOnWriteArrayList<PacketListener>();
			packetListeners.put(listener.getPacketOPCode(), list);
		}
		
		list.add(listener);
	}
	
	public void removeListener(PacketListener listener) {
		List<PacketListener> list = packetListeners.get(listener.getPacketOPCode());
		if (list == null) return ;
		list.remove(listener);
		if (list.isEmpty()) 
			packetListeners.remove(listener.getPacketOPCode());
	}
	
	public JKadStatus getStatus() {
		return status;
	}
	
	public boolean isConnected() {
		return status == CONNECTED;
	}
	
	private void loadClientID() {
		ConfigurationManager config_manager = ConfigurationManagerFactory.getInstance();
		String client_id = config_manager.getStringParameter(ConfigurationManager.JKAD_ID_KEY, null);
		if (client_id == null) {
			clientID = new ClientID(getRandomInt128());
			config_manager.setParameter(ConfigurationManager.JKAD_ID_KEY, clientID.toHexString());
		} else {
			clientID = new ClientID(hexStringToByte(client_id));
		}
		
	}
	
	private class PacketProcessor extends Thread {
		
		public PacketProcessor() {
			super("Kad packet processor");
		}
		
		public void run() {
			while (!packet_queue.isEmpty()) {
				
				KadPacket packet = packet_queue.poll();
				try {
					processPacket(packet);
				}catch(Throwable t) {
					t.printStackTrace();
					Logger.getSingleton().logException(t);
					ByteBuffer unkPacket = getByteBuffer(packet.getAsByteBuffer().capacity());
					packet.getAsByteBuffer().position(0);
					packet.getAsByteBuffer().get(unkPacket.array(), 0, packet.getAsByteBuffer().capacity());
					Logger.getSingleton().logMessage("Exception in processing : \n"+byteToHexString(unkPacket.array()," "));
				}
			}
		}
	}

	public void shutdown() {
		this.disconnect();
	}

	public void start() {
		this.connect();
	}

	
	
	
}
