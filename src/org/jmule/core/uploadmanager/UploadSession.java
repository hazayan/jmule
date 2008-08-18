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

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.impl.EMulePacketFactory;
import org.jmule.core.edonkey.packet.impl.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerRequestFilePartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotTakenSP;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/18 07:55:45 $$
 */
public class UploadSession implements JMTransferSession {
	
	private SharedFile sharedFile;

	/**Upload Queue*/
	
	private UploadQueue uploadQueue = new UploadQueue();
		
	private long totalUploaded = 0;
	
	public UploadSession(SharedFile sFile){
		
		sharedFile = sFile;
		
	}

	public void processPacket(Peer sender, ScannedPacket packet,PeerSessionList listenItem) {
		
		if (!uploadQueue.hasPeer(sender))
			
			uploadQueue.addPeer(sender);
		
		if (packet instanceof JMPeerFileRequestSP) {
			
			sender.sendPacket(PacketFactory.getFileRequestAnswerPacket(sharedFile.getFileHash(), sharedFile.getSharingName()));
			
			return ;
			
		}
		
		if (packet instanceof JMPeerFileStatusRequestSP) {
			
			if (sharedFile instanceof PartialFile){
				
				PartialFile partialFile = (PartialFile) sharedFile;
				
				sender.sendPacket(PacketFactory.getFileStatusReplyPacket(sharedFile.getHashSet(), sharedFile.length() ,partialFile.getGapList() ));
				
			} else {
				
				sender.sendPacket(PacketFactory.getFileStatusReplyPacket(sharedFile.getHashSet(), sharedFile.length() ,new GapList() ));
				
			}
			
			return ;
			
		}
		
		if (packet instanceof JMPeerFileHashSetRequestSP) {
			
			sender.sendPacket(PacketFactory.getFileHashReplyPacket(sharedFile.getHashSet()));
			
			return ;
			
		}
		
		if (packet instanceof JMPeerSlotRequestSP) {
			
			Peer peer = uploadQueue.getLastPeer();
			
			if ((peer == null) || (peer.equals(sender)))
				
					sender.sendPacket(PacketFactory.getAcceptUploadPacket(sharedFile.getFileHash()));
			
				else
					
					sender.sendPacket(EMulePacketFactory.getQueueRankingPacket(uploadQueue.getPeerQueueID(sender)));
			
			return ;
			
		}
		
		if (packet instanceof JMPeerSlotTakenSP) {
			
			if (sender.equals(uploadQueue.getLastPeer()))
				
				removeLastPeer();
			
			else
				
				uploadQueue.removePeer(sender);
			
			return ;
			
		}

		if (packet instanceof JMPeerRequestFilePartSP) {
			
			JMPeerRequestFilePartSP sPacket = (JMPeerRequestFilePartSP)packet;
		
			if (!uploadQueue.getLastPeer().equals(sender)) {
				
				sender.sendPacket(EMulePacketFactory.getQueueRankingPacket(uploadQueue.getPeerQueueID(sender)));
				
				return ;
				
				}
			
			for(FileChunkRequest chunkRequest : sPacket) {
				
				FileChunk chunk;
				
				try {
					
					chunk = sharedFile.getData(chunkRequest);
					
					FileHash fileHash = sharedFile.getFileHash();
					
					sender.sendPacket(PacketFactory.getFilePartSendingPacket(fileHash,  chunk));
					
					totalUploaded +=(chunkRequest.getChunkEnd() - chunkRequest.getChunkBegin());
					
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
			}
			
			return ;
		}
		
		
	}
	
	public void onPeerConnected(Peer peer) {
		
	}
	
	public void onPeerDisconnected(Peer peer) {
		
		this.uploadQueue.removePeer(peer);
		
		if (uploadQueue.size()==0) {
			
			UploadManagerFactory.getInstance().removeUpload(this.sharedFile.getFileHash());
			
		}
	}
	
	public void reportInactivity(Peer peer, long time) {
		
		if (time>=ConfigurationManager.PEER_INACTIVITY_REMOVE_TIME) {
			
			uploadQueue.removePeer(peer);
			peer.disconnect();
			//PeerManagerFactory.getInstance().getPeerSessionList(peer).removeSession(this);
		}
	}
	
	/**
	 * Check if this item process fileHash
	 * @param fileHash
	 * @return true if hash match else false
	 */
	
	public boolean processHash(FileHash fileHash){
		
		return (this.sharedFile.getFileHash().equals(fileHash));
		
	}
	
	public String toString() {
		
		String str=" [ ";
		
		str+=this.sharedFile;
		
		str += " ]\n" + uploadQueue;
		
		return str;
		
	}	
	
	public int hashCode() {
		
		return sharedFile.hashCode();
		
	}
	
	public boolean equals(Object object){
		
		if (object==null) return false;
		
		if (!(object instanceof UploadSession)) return false;
		
		return this.hashCode()==object.hashCode();
		
	}
	
	public FileHash getFileHash(){
		
		return this.sharedFile.getFileHash();
		
	}	
	
	
	
	private void removeLastPeer(){
		
		Peer rPeer = (Peer) this.uploadQueue.remove();
		
		if (rPeer!=null) {
			
			//Add inactive peer in top of list
			
			if (this.uploadQueue.size()<ConfigurationManagerFactory.getInstance().UPLOAD_QUEUE_SIZE)
				
				this.uploadQueue.add(rPeer);
		}
		
		//this.getLastPeer().sendPacket(EMulePacketFactory.getQueueRankingPacket(getPeerID(this.getLastPeer())));
		
		Peer peer = this.uploadQueue.getLastPeer();
		
		if (peer==null) return;
		
		peer.sendPacket(PacketFactory.getAcceptUploadPacket(sharedFile.getFileHash()));
		
	}

	public long getTransferredBytes() {
		return totalUploaded;
	}

	public float getUploadSpeed() {
		Peer peer = uploadQueue.getLastPeer();
		
		if (peer==null) return 0;
		return peer.getUploadSpeed();
		
	}

	public long getFileSize() {
		
		return sharedFile.length();
	}
	
}
