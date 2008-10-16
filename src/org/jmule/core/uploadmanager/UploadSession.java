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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.EMulePacketFactory;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerRequestFilePartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotReleaseSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotRequestSP;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.CompletedFile;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.UploadQueue.UploadQueueElement;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 16:57:51 $$
 */
public class UploadSession implements JMTransferSession {
	
	private static final int QUEUE_CHECK_INTERVAL			=   1000;
	
	private static final int QUEUE_PEER_DISCONNECT_TIME     =   9000;
	
	private static final int CHUNK_RQEUST_REMOVE_TIME 		= 	1000;
	
	private SharedFile sharedFile;

	/**Upload Queue*/
	
	private UploadQueue uploadQueue = new UploadQueue();
		
	private long totalUploaded = 0;
	
	private JMThread queue_cleaner;
	
	private List<ChunkResponseData> chunk_requests = new CopyOnWriteArrayList<ChunkResponseData>();	
	public UploadSession(SharedFile sFile){
		
		sharedFile = sFile;
		
		queue_cleaner = new JMThread() {
			
			private boolean stop = false;
			
			public void run() {
				while(!stop) {
					try {
						Thread.sleep(QUEUE_CHECK_INTERVAL);
					} catch (InterruptedException e) {
						if (stop) return ;
						continue;
					}
					Queue<UploadQueueElement> queue = uploadQueue.getQueue();
					boolean first = true;
					for(UploadQueueElement element : queue) {
						if (first) { first = false; continue;}
						if (!element.getPeer().isConnected()) continue;
						long current_time = System.currentTimeMillis();
						if (current_time - element.getTime() >= QUEUE_PEER_DISCONNECT_TIME) {
							element.getPeer().disconnect();
						}
					}
					
				}
			}
			
			public void JMStop() {
				stop = true;
				interrupt();
			}	
		};
		
		queue_cleaner.start();
		
	}
	
	public boolean sharingCompleteFile() {
		boolean result = sharedFile instanceof CompletedFile;
		
		if (!result) {
			PartialFile pFile = (PartialFile) sharedFile;
			if ( pFile.getPercentCompleted() == 100d )
				result = true;
		}
		
		return result;
	}
	
	public int getPeerPosition(Peer peer) {
		return uploadQueue.getPeerQueueID(peer);
	}
	
	public JMIterable<Peer> getPeers() {
		return uploadQueue.getPeers();
	}
	
	public Peer getPeer(int i) {
		int j = 0;
		for(Peer peer : uploadQueue.getPeers()) {
			if( j == i ) return peer;
			++j;
		}
		return null;
	}
	
	public boolean hasPeer(Peer peer) {
		return uploadQueue.hasPeer(peer);
	}
	
	
	public String getSharingName() {
		return sharedFile.getSharingName();
	}
	
	public float getSpeed() {
		Peer peer = uploadQueue.getLastPeer();
		if (peer==null) return 0;
		return peer.getUploadSpeed();
	}
		
	public long getETA() {
		float upload_speed = getSpeed();
		if (upload_speed != 0)
			return (long)(getFileSize()/upload_speed);
		else 
			return Misc.INFINITY_AS_INT;
	}
	
	
	public void processPacket(Peer sender, ScannedPacket packet) {
		
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
		
		if (packet instanceof JMPeerSlotReleaseSP) {

			if (sender.equals(uploadQueue.getLastPeer()))
				removeLastPeer(true);
			else
				uploadQueue.removePeer(sender);
			if (uploadQueue.size()==0) {
				
				UploadManagerFactory.getInstance().removeUpload(this.sharedFile.getFileHash());
				
			}
			
			return ;
			
		}

		if (packet instanceof JMPeerRequestFilePartSP) {
			
			JMPeerRequestFilePartSP sPacket = (JMPeerRequestFilePartSP)packet;
		    Peer last_peer = uploadQueue.getLastPeer();

			if (! last_peer.equals(sender) ) {
				
				sender.sendPacket(EMulePacketFactory.getQueueRankingPacket(uploadQueue.getPeerQueueID(sender)));
				
				return ;
				
			}	
			
			List<Packet> send_packet_list = new LinkedList<Packet>();

			for(FileChunkRequest chunkRequest : sPacket) {
				
				if (containSendChunk(chunkRequest)) continue;
				
				chunk_requests.add(new ChunkResponseData(chunkRequest,System.currentTimeMillis()));
				
				FileChunk chunk;
				
				try {
					
					chunk = sharedFile.getData(chunkRequest);
					
					FileHash fileHash = sharedFile.getFileHash();
					
					send_packet_list.add(PacketFactory.getFilePartSendingPacket(fileHash,  chunk));

					totalUploaded +=(chunkRequest.getChunkEnd() - chunkRequest.getChunkBegin());
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
				for(Packet p : send_packet_list) { 
					sender.sendPacket(p);
				}
				
			}

			return ;
		}
		
		
	}
	
	private boolean containSendChunk(FileChunkRequest chunk) {
		for(int i = 0;i<chunk_requests.size();i++) {
			ChunkResponseData data = chunk_requests.get(i);
			if (data.getRequest().getChunkBegin() == chunk.getChunkBegin())
				if (data.getRequest().getChunkEnd() == chunk.getChunkEnd()) {
					long time = System.currentTimeMillis() - data.getRequestTime();
					if (time>CHUNK_RQEUST_REMOVE_TIME) {
						chunk_requests.remove(i);
						return false;
					}
					return true;
				}
		}
		return false;
	}
	
	
	public void onPeerConnected(Peer peer) {
		if (!uploadQueue.hasPeer(peer))
			uploadQueue.addPeer(peer);
		Peer last_peer = uploadQueue.getLastPeer();
		if (last_peer == null) return;
		if (last_peer.equals(peer)) { 
			peer.sendPacket(PacketFactory.getAcceptUploadPacket(sharedFile.getFileHash()));
			return ;
		}
			
	}
	
	public void onPeerDisconnected(Peer peer) {
		
		Peer last_peer = uploadQueue.getLastPeer();
		if (last_peer != null) {
		
			if(uploadQueue.getLastPeer().equals(peer)) {
			
				removeLastPeer(true);
				
				if (uploadQueue.size()==0) {
					
					stopSesison();
					
				}
			}
		}
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

	private void removeLastPeer(boolean totallyRemove){
		chunk_requests.clear();
		Peer rPeer = this.uploadQueue.pool();
		if ((rPeer!=null)&&(!totallyRemove)) {
			
			//add peer at the end of queue
			
			if (this.uploadQueue.size()<ConfigurationManager.UPLOAD_QUEUE_SIZE)
				
				this.uploadQueue.addPeer(rPeer);
		} else
			rPeer.getSessionList().removeSession(this);
		
		//this.getLastPeer().sendPacket(EMulePacketFactory.getQueueRankingPacket(getPeerID(this.getLastPeer())));
		
		Peer peer = this.uploadQueue.getLastPeer();
		
		if (peer==null) return;
		
		if (peer.isConnected())
			peer.sendPacket(PacketFactory.getAcceptUploadPacket(sharedFile.getFileHash()));
		else
			peer.connect();
		
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
	
	
	public void stopSesison() {
		for(Peer peer : uploadQueue.getPeers()) {
			PeerSessionList list = peer.getSessionList();
			if (list != null)
				list.removeSession(this);
			if (peer.isConnected()) peer.disconnect();
		}
		uploadQueue.clear();
		
		UploadManagerFactory.getInstance().removeUpload(sharedFile.getFileHash());
		queue_cleaner.JMStop();
	}
	
	private class ChunkResponseData {
		private FileChunkRequest request;
		private long request_time;

		public ChunkResponseData(FileChunkRequest request, long requestTime) {
			this.request = request;
		    request_time = requestTime;
		}
		
		public FileChunkRequest getRequest() {
			return request;
		}
		
		public long getRequestTime() {
			return request_time;
		}
	}
}
	