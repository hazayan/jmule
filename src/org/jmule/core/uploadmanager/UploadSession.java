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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.CompletedFile;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharedFileException;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.18 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/20 12:23:59 $$
 */
public class UploadSession implements JMTransferSession {
	//private static final String PEER_SEPARATOR 				=   ":";
	//private static final int QUEUE_CHECK_INTERVAL			=   1000;
	//private static final int QUEUE_PEER_DISCONNECT_TIME     =   9000;
	private SharedFile sharedFile;	
	private long totalUploaded = 0;
	
	private Collection<Peer> session_peers = new ConcurrentLinkedQueue<Peer>();
	
	private InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	
	UploadSession(SharedFile sFile) {
		sharedFile = sFile;
	}
	
	void stopSession() {
		for(Peer peer : session_peers) {
			network_manager.sendSlotRelease(peer.getIP(), peer.getPort());
		}
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
		
	public int getPeerCount() {
		return session_peers.size();
	}
	
	public List<Peer> getPeers() {
		return Arrays.asList(session_peers.toArray(new Peer[0]));
	}
	
	public boolean hasPeer(Peer peer) {
		return session_peers.contains(peer);
	}
	
	public String getSharingName() {
		return sharedFile.getSharingName();
	}
	
	public float getSpeed() {
		float upload_speed = 0.0f;
		for(Peer peer : session_peers)
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
	
	void endOfDownload(Peer sender) {
		session_peers.remove(sender);
	}
	
	void receivedFileChunkRequestFromPeer(Peer sender,
			FileHash fileHash, List<FileChunkRequest> requestedChunks) {
	/*	List<Peer> slot_peers = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN);

		if (! slot_peers.contains(sender) ) {
			network_manager.sendQueueRanking(sender.getIP(), sender.getPort(), uploadQueue.getPeerQueueID(sender));
			return ;
		}*/
		
		for(FileChunkRequest chunk_request : requestedChunks) {
			FileChunk file_chunk;
			try {
				file_chunk = sharedFile.getData(chunk_request);
			} catch (SharedFileException e) {
				e.printStackTrace();
				continue;
			}
			totalUploaded +=(file_chunk.getChunkEnd() - file_chunk.getChunkStart());
			network_manager.sendFileChunk(sender.getIP(), sender.getPort(), getFileHash(), file_chunk);
		}
	}
	
	void receivedSlotRequestFromPeer(Peer sender,FileHash fileHash) {
		session_peers.add(sender);
	}
	
	void peerConnected(Peer peer) {
/*		List<Peer> peer_list = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTTAKEN);
		for (Peer p : peer_list) {
			if (p.equals(peer)) {
				network_manager.sendSlotGiven(peer.getIP(), peer.getPort(), getFileHash());
				break;
			}
		}*/
			
	}
	
	void peerDisconnected(Peer peer) {
		/*List<Peer> peer_list = uploadQueue.getSlotPeers(PeerQueueStatus.SLOTGIVEN, PeerQueueStatus.SLOTTAKEN);
		for(Peer p : peer_list)
			if (p.equals(peer)) {
				uploadQueue.moveToLast(peer);
				return ;
			}*/
	}
	
	void peerConnectingFailed(Peer peer, Throwable cause) {
		
	}
	
		
	public String toString() {
		String str = "[\n ";
		str += this.sharedFile + "\n";
		for(Peer peer : session_peers)
			str += " " +peer + "\n";
		str += "\n]";
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

	public ED2KFileLink getED2KLink() {
		return sharedFile.getED2KLink();
	}

	public SharedFile getSharedFile() {
		return sharedFile;
	}

	void setSharedFile(SharedFile newFile) {
		sharedFile = newFile;
	}
}
	