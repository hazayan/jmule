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

import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_CLIENTVER;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_NICKNAME;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketChecker;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerChatMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloSP;
import org.jmule.core.edonkey.packet.tag.TagException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.net.JMConnection;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.core.net.PacketScanner;
import org.jmule.core.peermanager.PeerManagerFactory;
import org.jmule.core.peermanager.PeerSessionList;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:34:23 $$
 */
public class Peer extends JMConnection {
	
	private static final int PEER_DISCONNECT_WAIT_TIME = 1000;
	private static final int PEER_DISCONNECT_CHECK_WAIT_INTERVAL = 100;
	
	private PacketProcessorThread incomingPacketProcessor = null;
		
	private PeerSessionList sessionList = new PeerSessionList(this);
	
	private ClientID clientID = null;
	
	private Server connectedServer = null;
	
	private UserHash userHash = null;
	
	private TagList peerTags = new TagList();
	
	private boolean isConnected = false;
	
	private boolean unknownPeerID = true;
	
	private boolean banned = false;
	
	private Set<FileHash> peer_shared_files = new HashSet<FileHash>();
	
	private long connectTime = 0;
	
	private JMThread disconnect_waiting;

	public Peer(JMuleSocketChannel remoteConnection,Server connectedServer) {
		
		super(remoteConnection);
		
		PeerManagerFactory.getInstance().addUnknownPeer(this);
		
		this.connectedServer = connectedServer;
		
		unknownPeerID = true;
		
		super.open();
				
	}

	public Peer(String remoteAddress, int remotePort,Server connectedServer) {
		
		super();
		
		super.setAddress(remoteAddress, remotePort);
		
		this.connectedServer = connectedServer;
		
		PeerManagerFactory.getInstance().addUnknownPeer(this);
		
	}
	
	public Peer(ClientID clientID,int remotePort,Server connectedServer){
		
		super();
		
		this.clientID = clientID;
		
		this.setAddress(clientID+"", remotePort);
		
		this.connectedServer = connectedServer;
		
		//PeerManagerFactory.getInstance().addPeer(this);
		PeerManagerFactory.getInstance().addUnknownPeer(this);
		
	}
	
	public Peer(ClientID clientID,int remotePort,Server connectedServer,FileHash fileHash){
		
		super();
		
		this.clientID = clientID;
		
		this.setAddress(clientID+"", remotePort);
		
		this.connectedServer = connectedServer;
		
		peer_shared_files.add(fileHash);

	}
	
	public SocketChannel getConnection() {
		return remoteConnection.getChannel();
	}
	
	protected void processPackets() {
		
		if ((incomingPacketProcessor == null)||(!incomingPacketProcessor.isAlive()))
			
			this.incomingPacketProcessor = new PacketProcessorThread();
		
	}
	
	public void connect() {
		
		connectTime = System.currentTimeMillis();
		 
		if (isConnected())
			return;
		
		if (isHighID()){
			
			super.connect();
		
		} else {
			
			if (connectedServer == null) disconnect();
			
			else {
				
				connectedServer.callBackRequest(this.clientID);
			}
		}
	}
	
	public void disconnect() {
		try {
			super.disconnect();
		}catch(Throwable t) {
			onDisconnect();
		}
		
	}
		
	public String toString() {
		
		String result;
		
		result = super.toString();
		
		result += " "+getNickName()+" ";
		
		if (isConnected())
			result+=" ED2K Connected ";
		else
			result+=" ED2K Disconnected ";
		
		if (clientID != null)
			
			result += " Client ID : "+clientID+" [ "+(isHighID() ? "HIGH ID" : "LOW ID")+" ] ";
						
		if (sessionList != null)		
			result += " Session count : " + sessionList.getSessionCount();
		else
			result +=" Session count : null";
		
		result += "  "+ (isBanned() ? "Banned" : "Unbanned");
		
		result += "  " + userHash;
		
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
		if (sessionList == null) 
			sessionList = new PeerSessionList(this);
		else
			sessionList.setPeer(this);
		
	}
	
	public void reusePeer(Peer peer) {
		setSessionList(peer.getSessionList()); // warning : peer.getSessionList() may return null
		peer.setSessionList(null);
		if (peer.getUserHash() != null) {
			userHash = peer.getUserHash();
			peer.userHash = null;
		}
	}
	
	public boolean isHighID() {
		
		if (this.connectedServer == null)
			return true;
		
		return this.clientID.isHighID();
		
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
		if (disconnect_waiting!=null)
			if (disconnect_waiting.isAlive()) return ; // Disconnect thread is already running
		
		final Peer peer = this;
		disconnect_waiting = new JMThread(new JMRunnable() {
			public void JMRun() {
				long start_time = System.currentTimeMillis();
				
				while(incoming_packet_queue.size()!=0) { // outgoing_queue can't be used, TCP connection already closed !
					long current_time = System.currentTimeMillis();
					if (current_time - start_time > PEER_DISCONNECT_WAIT_TIME) break; 
					//Wait some time if have incoming packets to process
					try {
						Thread.sleep(PEER_DISCONNECT_CHECK_WAIT_INTERVAL);
					} catch (InterruptedException e) {
						break;
					}
				}
				
				isConnected = false;
				
				sessionList.onDisconnect();
			
				PeerManagerFactory.getInstance().removePeer(peer);
			}
		});
		
		disconnect_waiting.start();
		
		
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
						
			int tcp_port = sPacket.getTCPPort();
			
			if (getPort() != tcp_port) 
				remoteAddress = new InetSocketAddress(getAddress(),tcp_port);
				
			
//			if (auto_add_on_connect)
//				PeerManagerFactory.getInstance().addPeer(this);

			isConnected = true;
			
			if (unknownPeerID) {
				
				try {
					
					PeerManagerFactory.getInstance().makeKnownPeer(this);
					
					unknownPeerID = false;
					
				} catch (Throwable e) {
					// May be thrown exception when connecting to server
					e.printStackTrace();
				}
				
			}
		
			this.sessionList.onConnected();
			
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
			
			return ;
			
		}
		
		if (packet instanceof JMPeerHelloAnswerSP) {
			
			connectTime = 0;
			
			JMPeerHelloAnswerSP sPacket = (JMPeerHelloAnswerSP)packet;
			
			userHash = sPacket.getUserHash();
			
			peerTags = sPacket.getTagList();
			
			clientID = sPacket.getClientID();
			
			int tcp_port = sPacket.getTCPPort();
			
			if (getPort() != tcp_port) 
				remoteAddress = new InetSocketAddress(getAddress(),tcp_port);
			
//			if (auto_add_on_connect)
//				PeerManagerFactory.getInstance().addPeer(this);
			
			isConnected = true;
			
			//Used when Peer was created using IP and Port
		
			if (unknownPeerID) {
				
				try {
					
					PeerManagerFactory.getInstance().makeKnownPeer(this);
					
					unknownPeerID = false;
					
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
		
		if (userHash == null)
		
			return getID().hashCode();
		
		return userHash.hashCode();
		
	}
	
	public boolean equals(Object object) {
		
		if (object == null)
			
			return false;
		
		if (!(object instanceof Peer))
			
			return false;
		Peer p = (Peer) object;
		if ((p.getUserHash()!=null) && (userHash != null))
			return p.getUserHash().equals(getUserHash());
		ClientID id = p.getID();
		ClientID id2 = getID();
		if ((id == null) || (id2 == null)) return getAddress().equals(p.getAddress());
		byte[] b1 = id.getClientID();
		byte[] b2 = id2.getClientID();
		
	//	System.out.println("Compare : "+this +" \n"+p+"  "+Arrays.equals(b1, b2));
		
		return Arrays.equals(b1, b2);
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
	
	public boolean isBanned() {
		
		return banned;
		
	}
	
	public void ban() {
		banned = true;
		super.ban();
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
	
	/**
	 * Return client version
	 * @return array with software version 1.2.3.4 => [1,2,3,4]
	 */
	public int[] getVersion() {
		
		int clientInfo;
		try {
			
			clientInfo = peerTags.getDWORDTag(TAG_NAME_CLIENTVER);
		}catch(Throwable e) {
			return new int[] {0,0,0,0};
		}
		int[] result = new int[4];
		
		result[0] = (clientInfo >> 17) & 0x7F;
		result[1] = (clientInfo >> 10) & 0x7F;
		result[2] = (clientInfo >> 7) & 0x07;
		result[3] = clientInfo & 0x7F;
		return result;
	}
	
}