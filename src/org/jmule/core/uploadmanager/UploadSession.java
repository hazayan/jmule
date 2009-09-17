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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.CompletedFile;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharedFileException;
import org.jmule.core.uploadmanager.UploadQueue.PeerQueueStatus;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.12 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:30:12 $$
 */
public class UploadSession implements JMTransferSession {
	private static final String PEER_SEPARATOR 				=   ":";
	//private static final int QUEUE_CHECK_INTERVAL			=   1000;
	//private static final int QUEUE_PEER_DISCONNECT_TIME     =   9000;
	private SharedFile sharedFile;	
	private UploadQueue uploadQueue = UploadQueue.getInstance();
	private long totalUploaded = 0;
	
	private Map<String, Peer> session_peers = new ConcurrentHashMap<String, Peer>();
	
	private InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	private InternalPeerManager peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
		
	UploadSession(SharedFile sFile) {
		sharedFile = sFile;
	}
	
	public boolean sharingCompleteFile() {
		boolean result = sharedFile instanceof CompletedFile;
		if (!result) {
			PartialFile pFile = (PartialFile) sharedFile;
			if (pFile.getPercentCompleted() == 100d)
				result = true;
		}

		return result;
	}
		
	void stopSession() {
		for(Peer peer : session_peers.values()) {
			network_manager.sendSlotRelease(peer.getIP(), peer.getPort());
		}
	}
	
	public int getPeerCount() {
		return session_peers.size();
	}
	
	public List<Peer> getPeers() {
		List<Peer> result = new ArrayList<Peer>();
		result.addAll(session_peers.values());
		return result;
	}
	
	public boolean hasPeer(Peer peer) {
		return hasPeer(peer.getIP(), peer.getPort());
	}
	
	public boolean hasPeer(String ip, int port) {
		return session_peers.containsKey(ip + PEER_SEPARATOR + port);
	}
	
	public String getSharingName() {
		return sharedFile.getSharingName();
	}
	
	public float getSpeed() {
		List<Peer> peer_list = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN, PeerQueueStatus.SLOTTAKEN);
		float upload_speed = 0.0f;
		for(Peer peer : peer_list)
			upload_speed += peer.getUploadSpeed();
		return upload_speed;
	}
		
	public long getETA() {
		float upload_speed = getSpeed();
		if (upload_speed != 0)
			return (long) (getFileSize() / upload_speed);
		else
			return Misc.INFINITY_AS_INT;
	}
	
	public void endOfDownload(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = peer_manager.getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		uploadQueue.removePeer(sender);
		for(Peer peer : uploadQueue.getSlotPeers(PeerQueueStatus.SLOTTAKEN)) {
			if (peer.isConnected())
				network_manager.sendSlotGiven(peer.getIP(), peer.getServerPort(), getFileHash());
			else
				network_manager.addPeer(peer.getIP(), peer.getPort());
		}
	}
	
	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash, List<FileChunkRequest> requestedChunks) {
		Peer sender;
		try {
			sender = peer_manager.getPeer(peerIP, peerPort);
		} catch (PeerManagerException e1) {
			e1.printStackTrace();
			return ;
		}
		List<Peer> slot_peers = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN);

		if (! slot_peers.contains(sender) ) {
			network_manager.sendQueueRanking(sender.getIP(), sender.getPort(), uploadQueue.getPeerQueueID(sender));
			return ;
		}	
		
		for(FileChunkRequest chunk_request : requestedChunks) {
			FileChunk file_chunk;
			try {
				file_chunk = sharedFile.getData(chunk_request);
			} catch (SharedFileException e) {
				e.printStackTrace();
				continue;
			}
			totalUploaded +=(file_chunk.getChunkEnd() - file_chunk.getChunkStart());
			network_manager.sendFileChunk(peerIP, peerPort, getFileHash(), file_chunk);
		}
	}
	
	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		if (sharedFile instanceof PartialFile){
			PartialFile partialFile = (PartialFile) sharedFile;
			network_manager.sendFileStatusAnswer(peerIP, peerPort, sharedFile.getHashSet(), sharedFile.length() ,partialFile.getGapList());
		} else {
			network_manager.sendFileStatusAnswer(peerIP, peerPort, sharedFile.getHashSet(), sharedFile.length() ,new GapList());
		}
	}
	
	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		network_manager.sendFileHashSetAnswer(peerIP, peerPort, sharedFile.getHashSet());
	}
	
	public void receivedFileRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		try {
			Peer peer = peer_manager.getPeer(peerIP, peerPort);
			session_peers.put(peerIP + PEER_SEPARATOR + peerPort, peer);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
		network_manager.sendFileRequestAnswer(peerIP, peerPort, sharedFile.getFileHash(), sharedFile.getSharingName());	
	}
	
	public void receivedSlotRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		addIfNeedToUploadQueue(peerIP, peerPort);
		Peer sender;
		try {
			sender = peer_manager.getPeer(peerIP, peerPort);
		} catch (PeerManagerException e) {
			e.printStackTrace();
			return ;
		}
		boolean contains = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN, PeerQueueStatus.SLOTTAKEN).contains(sender);
		if (contains) {
				network_manager.sendSlotGiven(peerIP, peerPort, sharedFile.getFileHash());
				uploadQueue.markSlotGiven(sender);
		}
			else {
				int queue_id = uploadQueue.getPeerQueueID(sender);
				network_manager.sendQueueRanking(peerIP, peerPort, queue_id);
			}
	}
	
	private void addIfNeedToUploadQueue(String peerIP, int peerPort) {
		if (uploadQueue.hasPeer(peerIP, peerPort)) return ;
		try {
			Peer peer = peer_manager.getPeer(peerIP, peerPort);
			uploadQueue.addPeer(peer);
		} catch (Throwable e) {
			// 0_o
			e.printStackTrace();
			return ;
		}
	}

	
	void peerConnected(Peer peer) {
		List<Peer> peer_list = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTTAKEN);
		for (Peer p : peer_list) {
			if (p.equals(peer)) {
				network_manager.sendSlotGiven(peer.getIP(), peer.getPort(), getFileHash());
				break;
			}
		}
			
	}
	
	void peerDisconnected(Peer peer) {
		List<Peer> peer_list = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN, PeerQueueStatus.SLOTTAKEN);
		for(Peer p : peer_list)
			if (p.equals(peer)) {
				uploadQueue.moveToLast(peer);
				return ;
			}
	}
	
	void peerConnectingFailed(Peer peer, Throwable cause) {
		
	}
	
		
	public String toString() {
		String str = " [ ";
		str += this.sharedFile;
		str += " ]\n" + uploadQueue;
		return str;
	}

	public int hashCode() {
		return sharedFile.hashCode();
	}

	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof UploadSession))
			return false;
		return this.hashCode() == object.hashCode();
	}

	public FileHash getFileHash() {
		return this.sharedFile.getFileHash();
	}

	public long getTransferredBytes() {
		return totalUploaded;
	}

	public long getFileSize() {
		return sharedFile.length();
	}

	public int getPeersCount() {
		return uploadQueue.size();
	}

	public ED2KFileLink getED2KLink() {
		return sharedFile.getED2KLink();
	}

	public SharedFile getSharedFile() {
		return sharedFile;
	}

	public void setSharedFile(SharedFile newFile) {
		sharedFile = newFile;
	}
}
	