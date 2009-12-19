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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.E2DKConstants.PeerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.peermanager.Peer.PeerStatus;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.17 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/12/19 19:30:20 $$
 */
public class PeerManagerImpl extends JMuleAbstractManager implements InternalPeerManager {
	private Map<String, Peer> peers  = new ConcurrentHashMap<String, Peer>();
	private InternalNetworkManager _network_manager;
	
	private List<PeerManagerListener> listener_list = new LinkedList<PeerManagerListener>();
	
	private InternalDownloadManager _download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
	private InternalUploadManager _upload_manager 	  = (InternalUploadManager)   UploadManagerSingleton.getInstance();

	private JMTimer maintenance_tasks = new JMTimer();
	
	PeerManagerImpl() {
	}
	
	public String toString() {
		String result = "";
		
		for(String key : peers.keySet()) {
			result += "[" + key +"]= " + "["+peers.get(key)+"]\n";
		} 
		
		return result;
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
		maintenance_tasks.cancelAllTasks();
		for(Peer peer : peers.values()) {
			try {
				if (peer.isConnected())
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
		JMTimerTask peer_dropper = new JMTimerTask() {
			public void run() {
				for(Peer peer : peers.values()) { 
					if (!peer.isConnected()) continue;
					if (!_download_manager.hasPeer(peer))
						if (!_upload_manager.hasPeer(peer)) {
							try {
								disconnect(peer);
							} catch (PeerManagerException e) {
								e.printStackTrace();
							}
						}
				}
			}
		};
		maintenance_tasks.addTask(peer_dropper, 10000, true);
		
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
		peer.setStatus(PeerStatus.DISCONNECTED);
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
		Peer peer;
		if (hasPeer(ip, port)) {
			peer = getPeer(ip, port);
		}
		peer = new Peer(ip, port, PeerSource.SERVER);
		peers.put(ip + KEY_SEPARATOR + port, peer);
		notifyNewPeer(peer);
		return peer;
		
	}

	/**
	 * Search replication peer and replace it
	 * @param peer
	 * @return
	 */
	private boolean replaceLowIDPeer(Peer peer) {
		Peer founded_peer = null;
		for(Peer stored_peer : peers.values()) {
			if (stored_peer.getID().equals(peer.getID()))
				if (!stored_peer.isConnected()) {
					founded_peer = stored_peer;
					break;
				}
		}
		//System.out.println("Founded low ID peer : " + founded_peer +"\n for peer : " + peer);
		if (founded_peer != null) {
			String founded_peer_key = founded_peer.getIP() + KEY_SEPARATOR + founded_peer.getPort();
			
			//System.out.println("replaceLowIDPeer");
			//System.out.println("Search key : " + founded_peer_key);
			//System.out.println("Replace : " + peer + "\n"+founded_peer);
			founded_peer.copyFields(peer);
			String remove_peer_key = peer.getIP() + KEY_SEPARATOR + peer.getPort();
			peers.remove(remove_peer_key);
			peers.remove(founded_peer_key);
			peers.put(remove_peer_key, founded_peer);
			return true;
		}
		return false;
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
		peer.setStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		if (!peer.isHighID())
		if (replaceLowIDPeer(peer)) {
			try {
				peer = getPeer(peerIP, peerPort);
				peer.setStatus(PeerStatus.CONNECTED);
			} catch (PeerManagerException e) {
				e.printStackTrace();
				return ;
			}
		}
		_download_manager.peerConnected(peer);
		_upload_manager.peerConnected(peer);
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
		
		peer.setStatus(PeerStatus.CONNECTED);
		peer.setUserHash(userHash);
		peer.setClientID(clientID);
		peer.setTagList(tagList);
		peer.setServer(serverIP, serverPort);
		if (!peer.isHighID())
		if (replaceLowIDPeer(peer)) {
			try {
				peer = getPeer(peerIP, peerPort);
				peer.setStatus(PeerStatus.CONNECTED);
			} catch (PeerManagerException e) {
				e.printStackTrace();
				return ;
			}
		}
		_download_manager.peerConnected(peer);
		_upload_manager.peerConnected(peer);
		notifyPeerConnected(peer);
	}
	
	public void connect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort();
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		try {
			_network_manager.addPeer(ip, port);
		}catch(NetworkManagerException cause) {
			throw new PeerManagerException(cause);
		}
		peer.setStatus(PeerStatus.CONNECTING);
		notifyPeerConnecting(peer);	
	}

	public void disconnect(Peer peer) throws PeerManagerException {
		String ip = peer.getIP();
		int port  = peer.getPort(); 
		if (!hasPeer(ip, port))
			throw new PeerManagerException("Peer " + ip + KEY_SEPARATOR + port + " not found");
		_network_manager.disconnectPeer(ip, port);
		peer.setStatus(PeerStatus.DISCONNECTED);
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
		peer.setStatus(PeerStatus.DISCONNECTED);
		_download_manager.peerConnectingFailed(peer, cause);
		_upload_manager.peerConnectingFailed(peer, cause);
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
		peer.setStatus(PeerStatus.DISCONNECTED);
		_download_manager.peerDisconnected(peer);
		_upload_manager.peerDisconnected(peer);
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
		try {
			Peer peer = getPeer(ip, port);
			Map<PeerFeatures,Integer> peer_features = Utils.scanTagListPeerFeatures(tagList);
			peer_features.put(PeerFeatures.ProtocolVersion, (int)protocolVersion);
			peer.peer_features.putAll(peer_features);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		try {
			Peer peer = getPeer(ip, port);
			Map<PeerFeatures,Integer> peer_features = Utils.scanTagListPeerFeatures(tagList);
			peer_features.put(PeerFeatures.ProtocolVersion, (int)protocolVersion);
			peer.peer_features.putAll(peer_features);
			
			Tag udp_port = tagList.getTag(E2DKConstants.ET_UDPPORT);
			if (udp_port != null)
				peer.tag_list.addTag(udp_port);
			
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	
	public void addPeerManagerListener(PeerManagerListener listener) {
		listener_list.add(listener);
	}
	
	public void removePeerManagerListener(PeerManagerListener listener) {
		listener_list.remove(listener);
	}

	public List<Peer> createPeerList(List<String> peerIPList, List<Integer> peerPort, boolean addKnownPeersInList, PeerSource peerSource) {
		List<Peer> result = new ArrayList<Peer>();
		
		for (int i = 0; i < peerIPList.size(); i++) {
			String peer_ip = peerIPList.get(i);
			int peer_port = peerPort.get(i);
			if (hasPeer(peer_ip, peer_port)) { 
				if (addKnownPeersInList) {
					try {
						Peer peer = getPeer(peer_ip, peer_port);
						result.add(peer);
					} catch (PeerManagerException e) {
						e.printStackTrace();
					}
				}
				continue;
			}
			try {
				result.add(newPeer(peer_ip, peer_port, peerSource));
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
