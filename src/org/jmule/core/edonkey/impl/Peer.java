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

import static org.jmule.core.edonkey.E2DKConstants.SL_VERSION;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_CLIENTVER;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_NICKNAME;
import java.util.HashSet;
import java.util.Set;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketChecker;
import org.jmule.core.edonkey.packet.impl.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerChatMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloSP;
import org.jmule.core.edonkey.packet.tag.impl.TagException;
import org.jmule.core.edonkey.packet.tag.impl.TagList;
import org.jmule.core.net.JMConnection;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.core.net.PacketScanner;
import org.jmule.core.peermanager.PeerManagerFactory;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.util.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/26 11:32:13 $$
 */
public class Peer extends JMConnection {
	
	/**Peer's search status*/
	
	public static final int FILE_NOT_FOUND = 0;
	
	public static final int FILE_REQUESTING = 1;
	
	public static final int FILE_FOUND = 2;
	
	/**Upload status*/
	
	public static final int PEER_ACCEPT_UPLOAD_WAIT = 0;
	
	public static final int PEER_ACCEPT_UPLOAD = 1;
	
	private PacketProcessorThread processIncomingPackets = null;
	
	private int uploadStatus = PEER_ACCEPT_UPLOAD_WAIT;
	
	private PeerSessionList sessionList = new PeerSessionList(this);
	
	private ClientID clientID = null;
	
	private Server connectedServer = null;
	
	private UserHash userHash = null;
	
	private TagList peerTags = new TagList();
	
	private boolean isConnected = false;
	
	private boolean unknownPeer = false;
	
	private Set<FileHash> peer_shared_files = new HashSet<FileHash>();
	
	private boolean autoadd_to_peermanager = true;
	
	private long connectTime = 0;

	public Peer(JMuleSocketChannel remoteConnection,Server connectedServer) {
		
		super(remoteConnection);
		
		super.open();
		
		this.connectedServer = connectedServer;
		
		unknownPeer = true;
		
		PeerManagerFactory.getInstance().addUnknownPeer(this);
		
	}

	public Peer(String remoteAddress, int remotePort,Server connectedServer) {
		
		super();
		
		super.setAddress(remoteAddress, remotePort);
		
		this.connectedServer = connectedServer;
		
	}
	
	public Peer(ClientID clientID,int remotePort,Server connectedServer){
		
		super();
		
		this.clientID = clientID;
		
		this.setAddress(clientID+"", remotePort);
		
		this.connectedServer = connectedServer;
		
		PeerManagerFactory.getInstance().addPeer(this);
	}
	
	public Peer(ClientID clientID,int remotePort,Server connectedServer,FileHash fileHash){
		
		super();
		
		this.clientID = clientID;
		
		this.setAddress(clientID+"", remotePort);
		
		this.connectedServer = connectedServer;
		
		peer_shared_files.add(fileHash);
		
		autoadd_to_peermanager = false;
		
		sessionList = new PeerSessionList(this);

	}
	
	protected void processPackets() {
		
		if ((processIncomingPackets == null)||(!processIncomingPackets.isAlive()))
			
			this.processIncomingPackets = new PacketProcessorThread();
		
	}
	
	public void connect() {
		
		connectTime = System.currentTimeMillis();
		 
		if (isConnected())
			return;
		
		if (isHighID()){
			
			super.connect();
		
		} else {
			
			if (connectedServer == null) ;
			
			else {
				
				connectedServer.callBackRequest(this.clientID);
			}
		}
	}
	
	public void disconnect() {
		
		super.disconnect();
		
	}
		
	public String toString() {
		
		String result;
		
		result = super.toString();
		
		if (isConnected())
			result+=" ED2K Connected ";
		else
			result+=" ED2K Disconnected ";
		
		if (clientID != null)
			
			result += " Client ID : "+clientID+" [ "+(isHighID() ? "HIGH ID" : "LOW ID")+" ] ";
						
		result += " Incoming : "+incoming_packet_queue.size();
		result += " Outcomming : "+outgoing_packet_queue.size();
		
		return result;
		
	}
	
	public long getConnectTime() {
		return connectTime;
	}
	
	public ClientID getID() {
		
		return clientID;
		
	}
	
	public String getNickName() {
		try {
			return peerTags.getStringTag(TAG_NAME_NICKNAME);
		} catch (TagException e) {
			return "";
		}
	}
	
	public PeerSessionList getSessionList() {
		
		return sessionList;
		
	}
	
	public void setSessionList(PeerSessionList sessionList ) {
		
		this.sessionList = sessionList;
		
		this.sessionList.setPeer(this);
		
	}
	
	public boolean isHighID() {
		
		if (this.connectedServer == null)
			return true;
		
		return this.clientID.isHighID();
		
	}

	public int getUploadStatus() {
		
		return uploadStatus;
		
	}
		
	protected void onConnect() {
		
		if (connectedServer == null)
			
			connectedServer = ServerManagerFactory.getInstance().getConnectedServer();
		
		if (connectedServer!=null)
			
			super.sendPacket(PacketFactory.getPeerHelloPacket(ConfigurationManagerFactory.getInstance().getUserHash(),
				connectedServer.getClientID(), ConfigurationManagerFactory.getInstance().getTCP(),
				connectedServer.getServerIPAddress(), 
				connectedServer.getPort(), 
				ConfigurationManagerFactory.getInstance().getNickName()));
		
		else
			
			super.sendPacket(PacketFactory.getPeerHelloPacket(ConfigurationManagerFactory.getInstance().getUserHash(),
					null, ConfigurationManagerFactory.getInstance().getTCP(),
					null, 0, 
					ConfigurationManagerFactory.getInstance().getNickName()));
		
	}

	protected void onDisconnect() {
		
		sessionList.onDisconnect();
		
		PeerManagerFactory.getInstance().removePeer(this);
		
	}
	
	protected void reportInactivity(long time) {
		
		if (time % ConfigurationManagerFactory.getInstance().PEER_ACTIVITY_CHECH_INTERVAL>=3) {
			
			if (lastPacket!=null)
				
				if (isConnected)
					
					super.sendPacket(lastPacket);

			
			super.sendPacket(lastPacket);
		}
		
		
		sessionList.reportInactivity(time);
		
	}
	
	/**
	 * Process incoming packet from peer
	 */
	private void processPacket(ScannedPacket packet){
		if (packet instanceof JMPeerHelloSP) {
			
			connectTime = 0;
			
			JMPeerHelloSP sPacket = (JMPeerHelloSP)packet;
			
			userHash = sPacket.getUserHash();
			
			peerTags = sPacket.getTagList();
			
			clientID = sPacket.getClientID();
			
			if (autoadd_to_peermanager)
				PeerManagerFactory.getInstance().addPeer(this);
			Packet answerPacket;
			
			if (this.connectedServer == null)
				
				answerPacket = PacketFactory.getPeerHelloAnswerPacket(
						ConfigurationManagerFactory.getInstance().getUserHash(),
						null, 
						ConfigurationManagerFactory.getInstance().getTCP(), 
						ConfigurationManagerFactory.getInstance().getNickName(),
						null,0);
			
				else
					
				answerPacket = PacketFactory.getPeerHelloAnswerPacket(
						ConfigurationManagerFactory.getInstance().getUserHash(),
						connectedServer.getClientID(), 
						ConfigurationManagerFactory.getInstance().getTCP(), 
						ConfigurationManagerFactory.getInstance().getNickName(),
						connectedServer.getRemoteIPAddress(),connectedServer.getPort());
			
			super.sendPacket(answerPacket);
			
			isConnected = true;
			
			
			
			if (unknownPeer) {
				
				try {
					
					PeerManagerFactory.getInstance().makeKnownPeer(this);
					
					unknownPeer = false;
					
				} catch (Throwable e) {
					
					e.printStackTrace();
					
				}
				
			}
		
			this.sessionList.onConnected();
			
			return ;
			
		}
		
		if (packet instanceof JMPeerHelloAnswerSP) {
			
			connectTime = 0;
			
			JMPeerHelloAnswerSP sPacket = (JMPeerHelloAnswerSP)packet;
			
			userHash = sPacket.getUserHash();
			
			peerTags = sPacket.getTagList();
			
			clientID = sPacket.getClientID();
			
			if (autoadd_to_peermanager)
				PeerManagerFactory.getInstance().addPeer(this);
			
			isConnected = true;
			
			//Used when Peer was created using IP and Port
		
			if (unknownPeer) {
				
				try {
					
					PeerManagerFactory.getInstance().makeKnownPeer(this);
					
					unknownPeer = false;
					
				} catch (Throwable e) {
					
					e.printStackTrace();
					
				}
				
			}

			this.sessionList.onConnected();
			
			return ;
			
		}
		
		if (packet instanceof JMPeerChatMessageSP) {
			
			JMPeerChatMessageSP sPacket = (JMPeerChatMessageSP)packet;
			
			return;
			
		}
		
		sessionList.processPacket(packet);
		
	}
	
	public int hashCode() {
		
		//if (clientID == null)
		
			return (getAddress() + this.getPort()).hashCode();
		
		//return clientID.hashCode();
		
	}
	
	public boolean equals(Object object) {
		
		if (object == null)
			
			return false;
		
		if (!(object instanceof Peer))
			
			return false;
		
		return this.hashCode() == object.hashCode();
	}
	
	private class PacketProcessorThread extends JMThread {
		
		public  PacketProcessorThread() {
			
			super("Incoming packet processor");
			
			start();
			
		}
		
		public void run(){
			
			ScannedPacket packet;
			
			Packet raw_packet;
			
			while( getPacketCount()>0 ) {
				
					raw_packet = getReceivedPacket();
					
					try {
						
						packet = PacketScanner.scanPacket(raw_packet);
						
					} catch (Throwable e) {
						
						continue;
						
					}
					
					if (!PacketChecker.checkPacket(packet)) {

						ban();
						
					}
					try {
						processPacket(packet);
					}catch(Exception e) {e.printStackTrace(); }
			}
		}
	}
	
	public boolean hasSharedFile(FileHash fileHash) {
		
		return peer_shared_files.contains(fileHash);
		
	}
	
	public Server getConnectedServer() {
		
		return connectedServer;
		
	}

	public UserHash getUserHash() {
		
		return this.userHash;
		
	}

	public TagList getPeerTags() {
		
		return this.peerTags;
		
	}

	public boolean isConnected() {
		
		return isConnected;
		
	}
	
	public int getClientSoftware() {
		int clientInfo;
		try {
			
			clientInfo = peerTags.getDWORDTag(TAG_NAME_CLIENTVER);
		}catch(Throwable e) {
			return E2DKConstants.SO_COMPAT_UNK;
		}
		
		int cSoft =(int) (clientInfo & 0x00ffffff);
		
		cSoft=(cSoft>>17) & 0x7f;
		
		return cSoft;
	}
	
	public String getVersion() {
		return "";
		// Not work
		/*try {
			long version = peerTags.getDWORDTag(TAG_NAME_CLIENTVER);
			
			long major = version >> 16;
			long minor = version & 0xFFFF;
			
			return major+"."+minor;
		} catch (TagException e) {
			return "";
		}*/
	}
	
}