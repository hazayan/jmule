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
package org.jmule.core.uploadmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.jmule.core.JMException;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerException;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.InternalSharingManager;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.PayloadPeerList.PayloadPeerContainer;
import org.jmule.core.uploadmanager.UploadQueue.UploadQueueContainer;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.27 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/05/29 11:40:35 $$
 */
public class UploadManagerImpl extends JMuleAbstractManager implements InternalUploadManager {

	private Map<FileHash,UploadSession> session_list = new ConcurrentHashMap<FileHash,UploadSession>();
	
	private List<UploadManagerListener> listener_list = new LinkedList<UploadManagerListener>(); 
	
	private InternalSharingManager _sharing_manager;
	private InternalNetworkManager _network_manager;
	private InternalPeerManager _peer_manager;
	private InternalDownloadManager _download_manager;
	
	
	private UploadQueue uploadQueue;
	private PayloadPeerList payload_peers;
	
	private JMTimer maintenance_tasks = new JMTimer();
	
	UploadManagerImpl() { 
		
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		uploadQueue = UploadQueue.getInstance();
		payload_peers = PayloadPeerList.getInstance();
		_sharing_manager = (InternalSharingManager) SharingManagerSingleton.getInstance();
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton.getInstance();
		
  		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
         	public void updateStats(Set<String> types, Map<String, Object> values) {
                 if(types.contains(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES)) {
                	 long total_uploaded_bytes = 0;
	            	 for(UploadSession session : session_list.values()) {
	            		 total_uploaded_bytes+=session.getTransferredBytes();
	            	 }
	            	 values.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_BYTES, total_uploaded_bytes);
                 }
                 if(types.contains(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT)) {
                	 values.put(JMuleCoreStats.ST_NET_SESSION_UPLOAD_COUNT, session_list.size());
                 }
                 if (types.contains(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT)) {
                	 int total_upload_peers = 0;
	            	 for(UploadSession session : session_list.values()) {
						total_upload_peers += session.getPeerCount();
	            	 }
	            	 values.put(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT, total_upload_peers);
                	 
                 }
			}
		});
	}

	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		JMTimerTask frozen_peers_remover = new JMTimerTask() {
			public void run() {
				boolean recalc_slots = false;
					/*for(UploadQueueContainer container : uploadQueue.upload_queue.values()) {
						if (container.getLastResponseTime() >= ConfigurationManager.UPLOADQUEUE_REMOVE_TIMEOUT) {
							if (uploadQueue.slot_clients.contains(container))
								recalc_slots = true;
							removePeer(container.peer);
							continue;
						}
					}*/
					for(UploadQueueContainer container : uploadQueue.slot_clients) {
						if (container.getLastResponseTime() >= ConfigurationManager.UPLOAD_SLOT_LOSE_TIMEOUT) {
							recalc_slots = true;
							removePeer(container.peer);
							continue;
						}
					}
				if (recalc_slots)
					recalcSlotPeers();
			}
		};
		
		JMTimerTask transferred_bytes_updater = new JMTimerTask() {
			public void run() {
				for(UploadQueueContainer container : uploadQueue.slot_clients) {
					Peer peer = container.peer;
					updateBytes(peer, container.fileHash);
				}
				
				Map<Peer, PayloadPeerContainer> payload = payload_peers.getPayloadPeers();
				for(Peer peer : payload.keySet()) {
					PayloadPeerContainer container = payload.get(peer);
					if (container == null)
						continue;
					updateBytes(peer, container.getFileHash());
				}
			}
			
			public void updateBytes(Peer peer, FileHash fileHash) {
				if (!peer.isConnected()) return;
				long transferred_bytes = _network_manager.getUploadedFileBytes(peer.getIP(), peer.getPort());
				_network_manager.resetUploadedFileBytes(peer.getIP(), peer.getPort());
				try {
					UploadSession session = getUpload(fileHash);
					session.addTransferredBytes(transferred_bytes);
				} catch (UploadManagerException e) {
					e.printStackTrace();
				}
			}
		};
		
		JMTimerTask payload_peers_monitor = new JMTimerTask() {
			public void run() {
				boolean recalcslots = false;
				Map<Peer, PayloadPeerContainer> payload = payload_peers.getPayloadPeers();
				for(Peer peer : payload.keySet()) {
					long addTime = payload_peers.getAddTime(peer);
					long payload_timeout = System.currentTimeMillis() - addTime;
					if (payload_timeout > ConfigurationManager.UPLOAD_QUEUE_PAYLOAD_TIME) {
						recalcslots = true;
						try {
							uploadQueue.addPeer(peer, payload_peers.getFileHash(peer));
						} catch (UploadQueueException e) {
							e.printStackTrace();
						}
						payload_peers.removePeer(peer);
						payload_peers.addPayloadLoosed(peer.getUserHash());
						continue;
					}
					
					long lastResponse = payload_peers.getLastActive(peer);
					long last_response_timeout = System.currentTimeMillis() - lastResponse;
					if (last_response_timeout >= ConfigurationManager.UPLOAD_SLOT_LOSE_TIMEOUT) {
						recalcslots = true;
						try {
							uploadQueue.addPeer(peer, payload_peers.getFileHash(peer));
						} catch (UploadQueueException e) {
							e.printStackTrace();
						}
						payload_peers.removePeer(peer);
						removePeer(peer);
						continue;
					}
					
				}
				if (recalcslots) 
					recalcSlotPeers();
			}
		};
		maintenance_tasks.addTask(frozen_peers_remover, ConfigurationManager.UPLOAD_QUEUE_CHECK_INTERVAL, true);
		maintenance_tasks.addTask(transferred_bytes_updater, ConfigurationManager.UPLOAD_QUEUE_TRANSFER_CHECK_INTERVAL, true);
		maintenance_tasks.addTask(payload_peers_monitor, ConfigurationManager.UPLOAD_QUEUE_PAYLOAD_CHECK_INTERVAL, true);
	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		maintenance_tasks.cancelAllTasks();
		for(UploadSession session : session_list.values())
			session.stopSession();
		
	}
	
	protected boolean iAmStoppable() {
		return false;
	}
	
	public UploadQueue getUploadQueue() {
		return UploadQueue.getInstance();
	}
	
	public boolean hasUpload(FileHash fileHash) {
		return session_list.containsKey(fileHash);
	}

	public void removeUpload(FileHash fileHash) {
		UploadSession session = session_list.get(fileHash);
		session.stopSession();
		session_list.remove(fileHash);
		
		notifyUploadRemoved(fileHash);
	}

	public UploadSession getUpload(FileHash fileHash) throws UploadManagerException {
		if (!hasUpload(fileHash))
			throw new UploadManagerException("Upload " + fileHash
					+ " not found");
		return session_list.get(fileHash);
	}

	public List<UploadSession> getUploads() {
		List<UploadSession> result = new ArrayList<UploadSession>();
		result.addAll(session_list.values());
		return result;
	}

	public int getUploadCount() {
		return session_list.size();
	}
	
	public boolean hasPeer(Peer peer) {
		for(UploadSession session : session_list.values())
			if (session.hasPeer(peer))
				return true;
		return false;
	}

	private UploadSession getUploadSession(Peer sender) {
		for(UploadSession session : session_list.values())
			if (session.hasPeer(sender))
				return session;
		return null;
	}

	/**
	 * Remove peer from uploads, WITHOUT slot recalculation
	 * @param peer
	 */
	public void removePeer(Peer peer) {
		UploadSession session = getUploadSession(peer);
		if (session != null) {
			//update transferred bytes
			long transferred_bytes = _network_manager.getUploadedFileBytes(peer.getIP(), peer.getPort());
			session.addTransferredBytes(transferred_bytes);
			
			session.removePeer(peer);
			if (session.getPeerCount()==0) {
				session.stopSession();
				session_list.remove(session.getFileHash());
				notifyUploadRemoved(session.getFileHash());
			}
		}
		if (uploadQueue.hasPeer(peer))
			try {
				uploadQueue.removePeer(peer);
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
	}
	
	
	/*
	 * Generic, non UploadSession requests handling
	 * 
	 */
	public void receivedFileStatusRequestFromPeer(Peer sender,FileHash fileHash) { 
		if (!_sharing_manager.hasFile(fileHash)) {
			// requested file not found!
			return ;
		}
		SharedFile shared_file = _sharing_manager.getSharedFile(fileHash);
		if (shared_file instanceof PartialFile){
			PartialFile partialFile = (PartialFile) shared_file;
			_network_manager.sendFileStatusAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet(), shared_file.length() ,partialFile.getGapList());
		} else {
			_network_manager.sendFileStatusAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet(), shared_file.length() ,new GapList());
		}
	}

	public void receivedHashSetRequestFromPeer(Peer sender,FileHash fileHash) {
		if (!_sharing_manager.hasFile(fileHash)) {
			// file with fileHash not found !
			return;
		}
		SharedFile shared_file = _sharing_manager.getSharedFile(fileHash);
		_network_manager.sendFileHashSetAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet());
	}

	public void receivedFileRequestFromPeer(Peer sender,FileHash fileHash) {
		if (!_sharing_manager.hasFile(fileHash)) {
			_network_manager.sendFileNotFound(sender.getIP(),sender.getPort(), fileHash);
			return ;
		}
		
		SharedFile shared_file = _sharing_manager.getSharedFile(fileHash);
		if(shared_file instanceof PartialFile) {
			try {
				DownloadSession session = _download_manager.getDownload(fileHash);
				if (!session.isStarted()) {
					_network_manager.sendFileNotFound(sender.getIP(),sender.getPort(), fileHash);
					return ;
				}
			} catch (DownloadManagerException e) {
				e.printStackTrace();
			}
		}
		_network_manager.sendFileRequestAnswer(sender.getIP(), sender.getPort(), shared_file.getFileHash(), shared_file.getSharingName());
	}
	
	/*
	 * Slot request, begin/end of upload requests handling
	 * 
	 */
	public void receivedSlotRequestFromPeer(Peer sender,FileHash fileHash) {
		if (!_sharing_manager.hasFile(fileHash)) {
			//don't have requested file
			//TODO : Investigate on eMule
			return;
		}
		UploadSession upload_session = null;
		if (hasUpload(fileHash)) {
			try {
				upload_session = getUpload(fileHash);
			} catch (UploadManagerException e) {
				e.printStackTrace();
				return ;
			}
			upload_session.receivedSlotRequestFromPeer(sender, fileHash);
		} else {
			upload_session = new UploadSession(_sharing_manager.getSharedFile(fileHash));
			session_list.put(fileHash, upload_session);
			notifyUploadAdded(fileHash);
			upload_session.receivedSlotRequestFromPeer(sender, fileHash);
		}
		
		if (payload_peers.hasPeer(sender)) {
			FileHash check_hash = payload_peers.getFileHash(sender);
			if (check_hash.equals(fileHash)) {
				payload_peers.setLastActiveTime(sender, System.currentTimeMillis());
				_network_manager.sendSlotGiven(sender.getIP(), sender.getPort(), fileHash);
			}
			return;
		}
		
		if (!uploadQueue.hasPeer(sender))
			if (!payload_peers.isPayloadLoosed(sender.getUserHash())){
				// give 15 min payload for peer
				payload_peers.addPeer(sender, upload_session.getFileHash());
				payload_peers.setLastActiveTime(sender, System.currentTimeMillis());
				_network_manager.sendSlotGiven(sender.getIP(), sender.getPort(), fileHash);
				return;
			} else {
				try {
					uploadQueue.addPeer(sender, fileHash);
					recalcSlotPeers();
				} catch (UploadQueueException e) {
					// queue is full
					e.printStackTrace();
					return ;
				}
			}
		
		/*if (!uploadQueue.hasPeer(sender)) {
			try {
				uploadQueue.addPeer(sender, fileHash);
				recalcSlotPeers();
			} catch (UploadQueueException e) {
				// queue is full
				e.printStackTrace();
				return ;
			}
		}*/
		
		if (uploadQueue.hasPeer(sender)) {
			uploadQueue.updateLastRequestTime(sender, System.currentTimeMillis());			
		}
		
		if (uploadQueue.hasSlotPeer(sender)) {
			_network_manager.sendSlotGiven(sender.getIP(), sender.getPort(), fileHash);
		} else
			try {
				_network_manager.sendQueueRanking(sender.getIP(), sender.getPort(), uploadQueue.getPeerPosition(sender));
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * Process chunk request
	 * 
	 */
	public void receivedFileChunkRequestFromPeer(Peer sender,FileHash fileHash, List<FileChunkRequest> requestedChunks) {
		if (!_sharing_manager.hasFile(fileHash)) {
			//don't have requested file
			//TODO : Investigate on eMule
			return;
		}
		UploadSession session;
		try {
			session = getUpload(fileHash);
		} catch (UploadManagerException e) {
			new JMException(Misc.getStackTrace(e) + " for peer : " + sender).printStackTrace();
			return ;
		}
		
		if (!payload_peers.hasPeer(sender))
				if (!uploadQueue.hasSlotPeer(sender)) {
			try {
				uploadQueue.updateLastRequestTime(sender, System.currentTimeMillis());
				_network_manager.sendQueueRanking(sender.getIP(), sender.getPort(), uploadQueue.getPeerPosition(sender));
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
			return ;
		}
		if (uploadQueue.hasPeer(sender))
			uploadQueue.updateLastRequestTime(sender, System.currentTimeMillis());
		if (payload_peers.hasPeer(sender))
			payload_peers.setLastActiveTime(sender, System.currentTimeMillis());
		session.receivedFileChunkRequestFromPeer(sender, fileHash, requestedChunks);
	}
	
	public void endOfDownload(Peer sender) {
		receivedSlotReleaseFromPeer(sender);
	}
	
	public void receivedSlotReleaseFromPeer(Peer sender) {
		if (uploadQueue.hasSlotPeer(sender)) {
			removePeer(sender);
			recalcSlotPeers();
		}
	}
	
	public void recalcSlotPeers() {
		List<UploadQueueContainer> lostSlotPeers = new ArrayList<UploadQueueContainer>();
		List<UploadQueueContainer> obtainedSlotPeers = new ArrayList<UploadQueueContainer>();
		uploadQueue.recalcSlotPeers(lostSlotPeers, obtainedSlotPeers);
		for(UploadQueueContainer container : lostSlotPeers) {
			if (container.peer.isConnected())
				_network_manager.sendSlotRelease(container.peer.getIP(), container.peer.getPort());
		}
		for(UploadQueueContainer container : obtainedSlotPeers) {
			uploadQueue.updateLastRequestTime(container.peer, System.currentTimeMillis());
			if (container.peer.isConnected())
				_network_manager.sendSlotGiven(container.peer.getIP(), container.peer.getPort(), container.fileHash);
			else {
				try {
					_peer_manager.connect(container.peer);
				} catch (PeerManagerException e) {
					e.printStackTrace();
				}
			}
				
		}
	}
	
	/*
	 * Peer status handling
	 * 
	 */
	public void peerConnected(Peer peer) {
		if (uploadQueue.hasPeer(peer.getUserHash())) {
			UploadQueueContainer peer_container = uploadQueue.getContainerByUserHash(peer.getUserHash());
						
			peer_container.peer = peer;
			if (uploadQueue.hasSlotPeer(peer)) {
				_network_manager.sendSlotGiven(peer.getIP(), peer.getPort(), peer_container.fileHash);
			} else {
				try {
					int peer_position = uploadQueue.getPeerPosition(peer);
					_network_manager.sendQueueRanking(peer.getIP(), peer.getPort(), peer_position);
				} catch (UploadQueueException e) {
					e.printStackTrace();
				}
			}
		}
		for(UploadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				session.peerConnected(peer);
				return ;
			}
	}

	public void peerConnectingFailed(Peer peer, Throwable cause) {
		for(UploadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				session.peerConnectingFailed(peer, cause);
				break;
			}
		if (uploadQueue.hasPeer(peer)) {
			try {
				uploadQueue.removePeer(peer);
				recalcSlotPeers();
			} catch (UploadQueueException e) {
				e.printStackTrace();
			}
		}
			
	}

	public void peerDisconnected(Peer peer) {
		if (!hasPeer(peer)) return;
		
		if (payload_peers.hasPeer(peer)) {
			payload_peers.removePeer(peer);
			payload_peers.addPayloadLoosed(peer.getUserHash());
			removePeer(peer);
		}

		if (uploadQueue.hasSlotPeer(peer)) {
			removePeer(peer);
			recalcSlotPeers();
		}
		
	}
	
	public void peerRemoved(Peer peer) {
		if (!hasPeer(peer)) return;
		
		if (payload_peers.hasPeer(peer)) {
			payload_peers.removePeer(peer);
			payload_peers.addPayloadLoosed(peer.getUserHash());
			removePeer(peer);
		}

		if (uploadQueue.hasSlotPeer(peer)) {
			removePeer(peer);
			recalcSlotPeers();
		}
	}
	
	public void addUploadManagerListener(UploadManagerListener listener) {
		listener_list.add(listener);
	}

	public void removeUploadMaanagerListener(UploadManagerListener listener) {
		listener_list.remove(listener);
	}
	
	private void notifyUploadAdded(FileHash fileHash) {
		for(UploadManagerListener listener : listener_list)
			try {
				listener.uploadAdded(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyUploadRemoved(FileHash fileHash) {
		for(UploadManagerListener listener : listener_list)
			try {
				listener.uploadRemoved(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}

	public String toString() {
		String result = " Upload sessions : \n";
		for(FileHash hash : session_list.keySet()) {
			result += " Key   : [ " + hash + " ] " + " = \n";
			result += " Value : " + session_list.get(hash) + "\n" ;
				
		}
		result += " Upload queue : \n" + uploadQueue+"\n";
		result += "" + payload_peers;
		return result;
	}
	
}