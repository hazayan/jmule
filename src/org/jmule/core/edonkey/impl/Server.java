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
package org.jmule.core.edonkey.impl;

import static org.jmule.core.edonkey.E2DKConstants.SERVER_UDP_PORT;
import static org.jmule.core.edonkey.E2DKConstants.SL_DESCRIPTION;
import static org.jmule.core.edonkey.E2DKConstants.SL_FILES;
import static org.jmule.core.edonkey.E2DKConstants.SL_HARDFILES;
import static org.jmule.core.edonkey.E2DKConstants.SL_PING;
import static org.jmule.core.edonkey.E2DKConstants.SL_SERVERNAME;
import static org.jmule.core.edonkey.E2DKConstants.SL_SOFTFILES;
import static org.jmule.core.edonkey.E2DKConstants.SL_SRVMAXUSERS;
import static org.jmule.core.edonkey.E2DKConstants.SL_USERS;
import static org.jmule.core.edonkey.E2DKConstants.SL_VERSION;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.UDPPacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedUDPPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerFoundSourceSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerIDChangeSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerSearchResultSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerServerListSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerStatusSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPDescSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPNewDescSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPStatusSP;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.net.JMConnection;
import org.jmule.core.net.JMUDPConnection;
import org.jmule.core.net.PacketScanner;
import org.jmule.core.peermanager.PeerManagerFactory;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchManagerFactory;
import org.jmule.core.searchmanager.SearchRequest;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.util.Convert;

/**
 * Created on 2007-Nov-07
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.12 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/05 10:43:01 $$
 */
public class Server extends JMConnection {
	
	private ClientID clientID = new ClientID(new byte[] { 0, 0, 0, 0 });

	private UserHash userHash = ConfigurationManagerFactory.getInstance().getUserHash();

	private TagList tagList = new TagList();
	
	private boolean isConnected = false;

	private ProcessPacketsThread processPacketsThread = null;
	
	private boolean isStatic = false;
	
	// UDP
	private int challenge;

	private long challenge_send_time;

	private long last_udp_response = System.currentTimeMillis();
	
	private Random random = new Random(); // challenge generator
	
	// Search
	
	private volatile SearchRequest lastSearchQuery;
	
	private volatile SearchRequest nextSearchQuery;
	
	private volatile boolean allowSearch = false;
	
	private Timer server_timer;
	
	private TimerTask search_task;
	
	private TimerTask shared_files_offer_task;

	public Server() {
		
		super();

	}

	public Server(String IPAddress, int port) {

		super(IPAddress, port);

	}
	
	public Server(ED2KServerLink serverLink) {
		
		super(serverLink.getServerAddress(),serverLink.getServerPort());

	}

	public Server(String IPAddress, int port, TagList tagList) {

		super(IPAddress, port);

		this.tagList = tagList;
	
	}

	public void start() {

	}

	public void stop() {

		
	}

	private void startSearchTask() {
		
		search_task = new TimerTask() {

			int failed_count = 0;
			
			public void run() {
				
				if (failed_count == 5) {
					
					search_task = null;
					
					this.cancel();
					
					return;
				}
				
				if (!allowSearch) {
					
					return;
					
				}
				
				if (nextSearchQuery==null) {
					
					failed_count++;
					
					return ;
				}
				
				failed_count = 0;
				
				Packet searchQueryPacket = PacketFactory.getSearchPacket(nextSearchQuery.getSearchQuery().getQuery());
				
				lastSearchQuery = nextSearchQuery;

				nextSearchQuery = null;
				
				sendPacket(searchQueryPacket);
			}};
			
		server_timer.purge();
		server_timer.scheduleAtFixedRate(search_task, new Date(System.currentTimeMillis()), ConfigurationManager.SEARCH_QUERY_CHECK_INTERVAL);
		
	}
	
	private void stopServerTimer() {
		if (server_timer != null)
			server_timer.cancel();
	}
	
	private void startSharedFilesOfferTask() {
		shared_files_offer_task = new TimerTask() {
			SharingManager manager = JMuleCoreFactory.getSingleton().getSharingManager();
			public void run() {
				if (manager.getUnsharedFiles().size()!=0)
					offerFiles();
			}
		};
		server_timer.purge();
		server_timer.scheduleAtFixedRate(shared_files_offer_task, new Date(System.currentTimeMillis()), ConfigurationManager.SHARED_FILES_PUBLISH_INTERVAL);
	}
	
	public ED2KServerLink getServerLink() {
		
		return new ED2KServerLink(getAddress(),getPort());
		
	}
	
	public byte[] getServerIPAddress() {

		return getRemoteIPAddress();

	}

	public ClientID getClientID() {
		
		return clientID;
		
	}

	private void setClientID(ClientID clientID) {
		
		this.clientID = clientID;
		
	}

	protected void processPackets() {
		
		if ((processPacketsThread == null) || (!processPacketsThread.isAlive())) {
			
			processPacketsThread = new ProcessPacketsThread();
		
			processPacketsThread.start();
			
		}
		
	}

	public String toString() {
		
		return this.getAddress() + " : " + this.getPort();
		
	}

	public void disconnect() {
		
		this.allowSearch = false;
		
		stopServerTimer();
		
		super.disconnect();
		
	}
	
	public void connect() {
		
		notify_server_event(TCP_SOCKET_CONNECTING);
		
		super.connect();
	}

	public boolean allowSearch() {
		
		return this.allowSearch && (this.getStatus() == TCP_SOCKET_CONNECTED);
		
	}

	public void searchFiles(SearchRequest searchQuery) {
		
		nextSearchQuery = searchQuery;
		
		if (search_task==null)
			
			startSearchTask();
	}


	public void requestSources(FileHash fileHash, long fileSize) {
		
		sendPacket(PacketFactory.getSourcesRequestPacket(fileHash,fileSize));
	}

	public void callBackRequest(ClientID clientID) {
		
		super.sendPacket(PacketFactory.getCallBackRequestPacket(clientID));
		
	}

	private void offerFiles() {
		
		super.sendPacket(PacketFactory.getOfferFilesPacket(clientID));
		
	}
	
	public void processPacket(ScannedUDPPacket packet) {
		processPacket((ScannedPacket)packet);
	}
	
	private void processPacket(ScannedPacket packet) {
		
		if (packet instanceof JMServerIDChangeSP) {
			
			JMServerIDChangeSP scannedPacket = (JMServerIDChangeSP) packet;

			setClientID(scannedPacket.getClientID());
			
			allowSearch = true;
			
			isConnected = true;
			
			server_timer = new Timer();
			
			JMuleCore core = JMuleCoreFactory.getSingleton();
			
			core.getSharingManager().resetUnsharedFiles();
			
			boolean update_server_list = core.getConfigurationManager().getBooleanParameter(
										 ConfigurationManager.SERVER_LIST_UPDATE_ON_CONNECT_KEY, false);
			
			if (update_server_list)
				sendPacket(PacketFactory.getGetServerListPacket());
			
			notify_server_event(TCP_SOCKET_CONNECTED);
			
			startSharedFilesOfferTask();
			
			return;
			
		}

		if (packet instanceof JMServerStatusSP) {
			
			JMServerStatusSP scannedPacket = (JMServerStatusSP) packet;

			setNumFiles(scannedPacket.getNumFiles());
			
			setNumUsers(scannedPacket.getNumUsers());

			return;
		}

		if (packet instanceof JMServerMessageSP) {
			
			JMServerMessageSP scannedPacket = (JMServerMessageSP) packet;
			 
			if (scannedPacket.getServerMessage().contains("WARNING : This server is full.")) {
				
				disconnect();
				
				return;
			}
			
			ServerManagerFactory.getInstance().serverMessage(this, scannedPacket.getServerMessage());

			return;
			
		}
		
		if (packet instanceof JMServerServerListSP) {
			ServerManager server_manager = JMuleCoreFactory.getSingleton().getServerManager();
			JMServerServerListSP scanned_packet = (JMServerServerListSP) packet;
			for(Server server : scanned_packet)
				server_manager.addServer(server);
			return ;
		}

		if (packet instanceof JMServerSearchResultSP) {
			
			JMServerSearchResultSP scannedPacket = (JMServerSearchResultSP) packet;

			SearchManager search_manager = SearchManagerFactory.getInstance();
			
			SearchResultItemList search_result_item_list = scannedPacket.getSearchResultItemList();
			
			SearchResult search_result = new SearchResult(search_result_item_list,lastSearchQuery,this);
			
			search_manager.addResult(search_result);
			
			this.allowSearch = true;
			
			return;
			
		}

		if (packet instanceof JMServerFoundSourceSP) {
			
			JMServerFoundSourceSP scannedPacket = (JMServerFoundSourceSP) packet;
			
			PeerManagerFactory.getInstance().addPeer(scannedPacket.getFileHash(), scannedPacket);

			return;
		}

		if (packet instanceof JMServerUDPStatusSP) {
			
			last_udp_response = System.currentTimeMillis();
			
			JMServerUDPStatusSP scannedPacket = (JMServerUDPStatusSP) packet;
			
			setNumFiles(scannedPacket.getFilesCount());
			
			setNumUsers(scannedPacket.getUserCount());
			
			setSoftLimit(scannedPacket.getSoftFilesLimit());
			
			setHardLimit(scannedPacket.getHardSoftLimits());

			long challenge_udp = scannedPacket.getChallenge();

			if (challenge_udp == challenge)
				
				setPing(System.currentTimeMillis() - challenge_send_time);

			return;
		}

		if (packet instanceof JMServerUDPDescSP) {
			
			last_udp_response = System.currentTimeMillis();
			
			JMServerUDPDescSP scannedPacket = (JMServerUDPDescSP) packet;
			
			setName(scannedPacket.getName());
			
			setDesc(scannedPacket.getDescription());
			
			return;
		}
		
		if (packet instanceof JMServerUDPNewDescSP) {
			
			last_udp_response = System.currentTimeMillis();
			
			JMServerUDPNewDescSP scannedPacket = (JMServerUDPNewDescSP) packet;
			
			tagList.addTag(scannedPacket.getTagList(), true);
			
		}
	}

	public boolean isConnected() {
		
		return this.isConnected;
		
	}

	public boolean isDown() {
		
		if (isConnected) return false;
		
		if (System.currentTimeMillis() - last_udp_response > ConfigurationManager.SERVER_UDP_QUERY_INTERVAL * 3)
			
			return true;
		
		return false;
		
	}
	
	protected void onConnect() {
		
		sendPacket(PacketFactory.getServerLoginPacket(userHash, ConfigurationManagerFactory.getInstance().getTCP(), 
				ConfigurationManagerFactory.getInstance().getNickName()));
	}

	protected void onDisconnect() {
		
		last_udp_response = System.currentTimeMillis();
		
		isConnected = false;
		
		allowSearch = false;
		
		notify_server_event(TCP_SOCKET_DISCONNECTED);
	}

	public TagList getTagList() {
		
		return tagList;
		
	}

	private void setName(String newName) {
		
		Tag tag = new StandardTag(TAG_TYPE_STRING, SL_SERVERNAME);
		
		tag.insertString(newName);
		
		this.tagList.removeTag(SL_SERVERNAME);
		
		this.tagList.addTag(tag);
		
	}

	public String getName() {
		
		try {
			
			String result = tagList.getStringTag(SL_SERVERNAME);
			
			result = result.trim();
			
			if (result.length()!=0)
				
				return result;
			
			return getAddress();
			
		} catch (TagException e) {
			
			return getAddress();
		}
	}

	private void setDesc(String serverDesc) {
		
		Tag tag = new StandardTag(TAG_TYPE_STRING, SL_DESCRIPTION);
		
		tag.insertString(serverDesc);
		
		this.tagList.removeTag(SL_DESCRIPTION);
		
		this.tagList.addTag(tag);
		
	}

	public String getDesc() {
		
		try {
			
			return this.tagList.getStringTag(SL_DESCRIPTION);
			
		} catch (TagException e) {
			
			return "";
		}
	}

	private void setSoftLimit(int softLimit) {
		
		Tag tag = new StandardTag(TAG_TYPE_DWORD, SL_SOFTFILES);
		
		tag.insertDWORD(softLimit);
		
		this.tagList.removeTag(SL_SOFTFILES);
		
		this.tagList.addTag(tag);
		
	}

	public int getSoftLimit() {
		
		try {
			
			return this.tagList.getDWORDTag(SL_SOFTFILES);
			
		} catch (TagException e) {
			return 0;
		}
	}

	private void setHardLimit(int hardLimit) {
		
		Tag tag = new StandardTag(TAG_TYPE_DWORD, SL_HARDFILES);
		
		tag.insertDWORD(hardLimit);
		
		this.tagList.removeTag(SL_HARDFILES);
		
		this.tagList.addTag(tag);
		
	}

	public int getHardLimit() {
		
		try {
			
			return this.tagList.getDWORDTag(SL_HARDFILES);
			
		} catch (TagException e) {
			
			return 0;
			
		}
	}

	private void setPing(long ping) {
		
		Tag tag = new StandardTag(TAG_TYPE_DWORD, SL_PING);
		
		tag.insertDWORD(Convert.longToInt(ping));
		
		tagList.removeTag(SL_PING);
		
		tagList.addTag(tag);

	}

	public long getPing() {
		
		try {
			
			return Convert.longToInt(tagList.getDWORDTag(SL_PING));
			
		} catch (TagException e) {
			
			return 0;
			
		}
	}

	public String getVersion() {
		
		try {
			
			long version = tagList.getDWORDTag(SL_VERSION);
			
			long major = version >> 16;
			
			long minor = version & 0xFFFF;
			
			return major+"."+minor;
			
		} catch (TagException e) {
			
			return "";
		}
	}
	
	public long getNumFiles() {
		
		try {
			
			return Convert.intToLong(tagList.getDWORDTag(SL_FILES));
			
		} catch (TagException e) {
			
			return 0;
			
		}
		
	}
	
	public long getMaxUsers() {
		
		try {
			
			return Convert.intToLong(tagList.getDWORDTag(SL_SRVMAXUSERS));
			
		} catch (TagException e) {
			
			return 0;
			
		}
	}

	public long getNumUsers() {
		
		try {
			
			return Convert.intToLong(tagList.getDWORDTag(SL_USERS));
			
		} catch (TagException e) {
			
			return 0;
			
		}
	}
	
	private void setNumUsers(long numUsers) {
		
		Tag tag = new StandardTag(TAG_TYPE_DWORD, SL_USERS);
		
		tag.insertDWORD(Convert.longToInt(numUsers));
		
		tagList.removeTag(SL_USERS);
		
		tagList.addTag(tag);

	}

	private void setNumFiles(long numFiles) {
		
		Tag tag = new StandardTag(TAG_TYPE_DWORD, SL_FILES);
		
		tag.insertDWORD(Convert.longToInt(numFiles));
		
		tagList.removeTag(SL_FILES);
		
		tagList.addTag(tag);
		
	}
	
	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public int hashCode() {
		
		return (getAddress()+" : "+getPort()).hashCode(); 
	}
	
	public int getAddressAsInt() {
		
		return Convert.IPtoInt( Convert.stringIPToArray( getAddress() ) );
	}
	
	public boolean equals(Object object) {
		
		if (!(object instanceof Server)) return false;
		
		if (object.hashCode()!=this.hashCode()) return false;
		
		return true;
	}
	
	
	public void sendUDPDescRequest() {
		InetSocketAddress udpAddress = new InetSocketAddress(getAddress(), SERVER_UDP_PORT);
		UDPPacket packet = UDPPacketFactory.getUDPServerDescRequest(udpAddress);
		JMUDPConnection.getInstance().sendPacket(packet);
	}
	
	public void sendUDPStatusRequest() {
		challenge = random.nextInt();
		challenge_send_time = System.currentTimeMillis();
		InetSocketAddress udpAddress = new InetSocketAddress(getAddress(), SERVER_UDP_PORT);
		UDPPacket packet = UDPPacketFactory.getUDPStatusRequest(challenge, udpAddress);
		JMUDPConnection.getInstance().sendPacket(packet);
	}

	private class ProcessPacketsThread extends JMThread {
		
		public ProcessPacketsThread() {
			
			super("Server incoming packets process thread");

		}

		public void run() {
			
			ScannedPacket packet;
			
			Packet raw_packet;
			
			while (getPacketCount() > 0) {
				
				try {
					
					raw_packet = getReceivedPacket();
					
					packet = PacketScanner.scanPacket(raw_packet);
					
					processPacket(packet);
					
				} catch (Throwable e) {
					
					e.printStackTrace();
					
					return;
				}

			}
		}
	}
	
	private void notify_server_event(int socket_status) {
		
		switch( socket_status ) {
	    
	      case TCP_SOCKET_CONNECTED : {
	    	  
	    	  ServerManagerFactory.getInstance().serverConnected(this);
	    	  
	    	  break;
	      }
	      
	      case TCP_SOCKET_DISCONNECTED : {
	    	  
	    	  ServerManagerFactory.getInstance().serverDisconnected(this);
	    	
	    	  break;
	      }
	      
	      case TCP_SOCKET_CONNECTING : {
	    	  
	    	  ServerManagerFactory.getInstance().serverConnecting(this);
	    	  
	    	  break;
	      }
	   }	
	}
}
