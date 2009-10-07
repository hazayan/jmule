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
package org.jmule.core.peermanager;

import static org.jmule.core.JMConstants.KEY_SEPARATOR;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.Peer.PeerStatus;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/10/07 06:05:34 $$
 */
public class PeerManagerImpl extends JMuleAbstractManager implements InternalPeerManager {
	
	private Map<String, Peer> peers  = new Hashtable<String, Peer>();
	private InternalNetworkManager _network_manager;
	
	private List<PeerManagerListener> listener_list = new LinkedList<PeerManagerListener>();
	
	PeerManagerImpl() {
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
	}


	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		
		for(Peer peer : peers.values()) {
			try {
				disconnect(peer);
			}catch(PeerManagerException e) {
				e.printStackTrace();
			}
		}
		
	}


	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
	}

	protected boolean iAmStoppable() {
		return false;
	}
	
	public List<Peer> getPeers() {
		List<Peer> list = new ArrayList<Peer>();
		list.addAll(peers.values());
		return list;
	}
	
	public Peer getPeer(String ip, int port) throws PeerManagerException {
		Peer peer = peers.get(ip + KEY_SEPARATOR + port);
		if (peer == null) 
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		notifyNewPeer(peer);
		return peer;
	}
	
	public Peer newPeer(String ip, int port, PeerSource source) throws PeerManagerException {
		if (hasPeer(ip, port))
			throw new PeerManagerException("Peer already exists");
		
		Peer peer = new Peer(ip, port, source);
		peers.put(ip + KEY_SEPARATOR + port, peer);
		peer.setPeerStatus(PeerStatus.DISCONNECTED);
		notifyNewPeer(peer);		
		return peer;
	}
	
	public void removePeer(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port = peer.getPort();
		if (! hasPeer(ip, port)) 
			throw new PeerManagerException(" Peer " + ip + " : " + port + " not found");
		if (peer.getStatus()!= PeerStatus.DISCONNECTED)
			_network_manager.disconnectPeer(ip, port);
		peers.remove(ip + KEY_SEPARATOR + port);
		notifyPeerRemoved(peer);
	}
	
	public Peer newIncomingPeer(String ip, int port) throws PeerManagerException {
		if (hasPeer(ip, port)) {
			Peer peer = getPeer(ip, port);
			if (!peer.isHighID())
				if (!peer.isConnected()) {
					peer.setPeerStatus(PeerStatus.CONNECTED);
					notifyNewPeer(peer);
					return peer;
				}
			throw new PeerManagerException("Peer already exists");
		}
			
		Peer peer = new Peer(ip, port, PeerSource.SERVER);
		peers.put(ip + KEY_SEPARATOR + port, peer);
		notifyNewPeer(peer);
		return peer;
		
	}

	public void helloAnswerFromPeer(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerPacketPort,
			TagList tagList, String serverIP, int serverPort) {
		Peer peer = null;
		try {
			peer = getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		peer.setPeerStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		
		notifyPeerConnected(peer);
	}

	public void helloFromPeer(String peerIP, int peerPort, UserHash userHash,
			ClientID clientID, int peerPacketPort, TagList tagList,
			String serverIP, int serverPort) {
		Peer peer = null;
		try {
			peer = getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		
		peer.setPeerStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		
		notifyPeerConnected(peer);
	}
	
	public void connect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort();
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		_network_manager.addPeer(ip, port);
		peer.setPeerStatus(PeerStatus.CONNECTING);
		notifyPeerConnecting(peer);	
	}

	public void disconnect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort(); 
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		_network_manager.disconnectPeer(ip, port);
		notifyPeerDisconnected(peer);
	}
	
	public void peerConnectingFailed(String ip, int port, Throwable cause) {
		Peer peer = null;
		try {
			peer = getPeer(ip, port);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		peer.setPeerStatus(PeerStatus.DISCONNECTED);
		notifyPeerConnectingFailed(peer, cause);
	}
	
	public void peerDisconnected(String ip, int port) {
		Peer peer;
		try {
			peer = getPeer(ip, port);
		} catch (PeerManagerException e) {			
			e.printStackTrace();
			return;
		}
		peer.setPeerStatus(PeerStatus.DISCONNECTED);
		notifyPeerDisconnected(peer);
	}

	public boolean hasPeer(String ip, int port) {
		return peers.containsKey(ip + KEY_SEPARATOR + port);
	}

	public void callBackRequestFailed() {
	
	}

	public void receivedCallBackRequest(String ip, int port) {
		try {
			Peer peer = newPeer(ip, port, PeerSource.SERVER);
			connect(peer);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedEMuleHelloFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		
	}
	
	
	public void addPeerManagerListener(PeerManagerListener listener) {
		listener_list.add(listener);
	}
	
	public void removePeerManagerListener(PeerManagerListener listener) {
		listener_list.remove(listener);
	}
	
	public List<Peer> createPeerList(List<ClientID> peerIDList, List<Integer> peerPort, PeerSource peerSource) {
		List<Peer> result = new ArrayList<Peer>();
		
		for (int i = 0; i < peerIDList.size(); i++) {
			String client_id = peerIDList.get(i).getAsString();
			int peer_port = peerPort.get(i);
			if (hasPeer(client_id, peer_port)) continue;
			try {
				result.add(newPeer(client_id, peer_port, peerSource));
			} catch (PeerManagerException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	private void notifyNewPeer(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.newPeer(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}

	
	private void notifyPeerRemoved(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerRemoved(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnecting(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnecting(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnected(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnected(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerDisconnected(Peer peer) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerDisconnected(peer);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyPeerConnectingFailed(Peer peer, Throwable cause) {
		for(PeerManagerListener listener : listener_list) 
			try {
				listener.peerConnectingFailed(peer, cause);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
}
