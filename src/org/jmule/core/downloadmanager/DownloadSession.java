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
package org.jmule.core.downloadmanager;

import static org.jmule.core.edonkey.E2DKConstants.BLOCKSIZE;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILENAME;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTTAKEN;
import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.downloadmanager.strategy.DownloadStrategy;
import org.jmule.core.downloadmanager.strategy.DownloadStrategyImpl;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerAcceptUploadRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileNotFoundSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerQueueRankingSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSendingPartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotTakenSP;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.peermanager.PeerManagerFactory;
import org.jmule.core.peermanager.PeerSessionList;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharedFileException;
import org.jmule.core.sharingmanager.SharingManagerFactory;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * Created on 2008-Apr-20
 * @author binary256
 * @version $$Revision: 1.12 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/30 16:45:50 $$
 */
public class DownloadSession implements JMTransferSession {
	
	public final static int STATUS_STARTED = 0x01;
	
	public final static int STATUS_STOPPED = 0x02;
	
	private final static int PEER_MONITOR_INTERVAL =  1000;
	
	private PartialFile sharedFile;

	private FilePartStatus partStatus;
	
	private int sessionStatus = STATUS_STOPPED;
	
	private DownloadStrategy downloadStrategy = new DownloadStrategyImpl();
	
	private FileRequestList fileRequestList = new FileRequestList();

	private List<Peer> peer_list = new CopyOnWriteArrayList<Peer>();

	private List<Peer> connecting_peers = new CopyOnWriteArrayList<Peer>();
	
	private DownloadStatusList download_status_list = new DownloadStatusList();
		
	/**Request downloading information from server*/
	
	private SourcesQueryThread queryThread;
	
	private PeersMonitor peers_monitor;
	
	public DownloadSession(SearchResultItem searchResult) {
		
		try {
			
			createDownloadFiles( searchResult.getFileName(),searchResult.getFileSize(),searchResult.getFileHash()
					,null,(TagList)searchResult);
			
			int partCount = (int) ((sharedFile.length()/PARTSIZE));
			
			if (sharedFile.length() % PARTSIZE!=0) partCount++;
			
			partStatus = new FilePartStatus(partCount);
			
			
		} catch (Throwable e) {
		}
		
		
	} 
	
	public DownloadSession(ED2KFileLink fileLink){
		
		try {

			TagList tagList = new TagList();
		
			StandardTag tag = new StandardTag(TAG_TYPE_STRING,FT_FILENAME);
		
			tag.insertString(fileLink.getFileName());
		
			tagList.addTag(tag);
		
			tag = new StandardTag(TAG_TYPE_DWORD,FT_FILESIZE);
		
			tag.insertDWORD(Convert.longToInt((fileLink.getFileSize())));
		
			tagList.addTag(tag);
		
			createDownloadFiles(fileLink.getFileName(),fileLink.getFileSize(),fileLink.getFileHash(),null,tagList);
		
			int partCount = (int) ((sharedFile.length() / PARTSIZE));
		
			if (sharedFile.length() % PARTSIZE != 0)
			
			partCount++;
		
			partStatus = new FilePartStatus(partCount);
		
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	public DownloadSession(PartialFile partialFile){

		this.sharedFile = partialFile;
		
		this.sharedFile.getGapList().sort();
		
		int partCount = (int) ((sharedFile.length() / PARTSIZE));
		
		if (sharedFile.length() % PARTSIZE != 0)
			
			partCount++;
		
		partStatus = new FilePartStatus(partCount);
		
	}

	private void createDownloadFiles(String fileName,long fileSize,FileHash fileHash,
		PartHashSet hashSet,TagList tagList) throws SharedFileException {
				
		sharedFile = new PartialFile(fileName, fileSize,fileHash, hashSet, tagList);
		
		SharingManagerFactory.getInstance().addPartialFile(sharedFile);
		
	}
	
	public void startDownload() {
		
		this.setStatus(STATUS_STARTED);
		
		List<Peer> peer_list = PeerManagerFactory.getInstance().getPeers(getFileHash());
		
		for(Peer peer : peer_list) {
			
			if (!peer.isConnected())
				
					peer.connect();
		}
		
		queryThread = new SourcesQueryThread();
		
		queryThread.start();
		
		//slotRequestThread = new SlotRequestThread();
		
		//slotRequestThread.start();
		
		peers_monitor = new PeersMonitor();
		
		peers_monitor.start();
	}
	
	public void stopDownload() {
		
		if (queryThread!=null)
			
			queryThread.JMStop();
		
		//slotRequestThread.JMStop();
		
		peers_monitor.JMStop();
			
		Packet packet = PacketFactory.getEndOfDownloadPacket(sharedFile.getFileHash());
		
		for(Peer peer : peer_list) {
			
			if (peer.isConnected())
				
					peer.sendPacket(packet);
			
			PeerSessionList list = peer.getSessionList();
			if (list != null)
				list.removeSession(this);
			
		}
		
		peer_list.clear();
		download_status_list.clear();
		partStatus.clear();
		fileRequestList.clear();
		for (Peer peer : connecting_peers) {
			if (peer.getSessionList()!=null)
					peer.getSessionList().removeSession(this);
		}
		connecting_peers.clear();
		
		setStatus(STATUS_STOPPED);
		
	}
	
	public void cancelDownload() {
		
		if (getStatus()==STATUS_STARTED) {
			
			stopDownload();
		}
		
		SharingManagerFactory.getInstance().removeSharedFile(sharedFile.getFileHash());
		
	}
	
	public void addDownloadPeers(List<Peer> peerList){

		for(Peer peer : peerList) { 
			if (peer.getSessionList() == null) continue;
			if (peer.getSessionList().hasDownloadSession(this)) continue;
				
			peer.getSessionList().addDownloadSession(this);

			if (peer_list.contains(peer)) continue;
			if (connecting_peers.contains(peer)) continue;
			
			connecting_peers.add(peer);
			
			if (peer.isConnected()) {

				onPeerConnected(peer);
			}
			
			else
				
				peer.connect();
				
		}
		
		
	}
	
	/** Called when a peer is connected */
	public synchronized void onPeerConnected(Peer peer){
		
		PeerDownloadStatus status = download_status_list.getDownloadStatus(peer);
		
		if (status != null) { // known peer is connected
			peer_list.remove(peer); // remove old peer
			peer_list.add(peer);
			download_status_list.setPeer(peer);// update peer in download list
			return ; 
		}
		
		if (this.getStatus() == STATUS_STOPPED) return ;
				
		
		if (connecting_peers.contains(peer)) {
			connecting_peers.remove(peer);
		}
		
		peer_list.add(peer);
		
		download_status_list.addPeer(peer);
		
		peer.sendPacket(PacketFactory.getFileRequestPacket(this.sharedFile.getFileHash()));
		
		peer.sendPacket(PacketFactory.getFileStatusRequestPacket(this.sharedFile.getFileHash()));
	}
	
	public synchronized void onPeerDisconnected(Peer peer) {
				
		if (peer.isBanned()) {
			if (peer_list.contains(peer)) {
				fileRequestList.remove(peer);
				download_status_list.removePeer(peer);
				partStatus.removePartStatus(peer);
				PeerSessionList list = peer.getSessionList();
				if (list != null)
					list.removeSession(this);
				peer_list.remove(peer);
			} else
				connecting_peers.remove(peer);
		}
		
		PeerDownloadStatus status = download_status_list.getDownloadStatus(peer);
		
		if ((download_status_list==null) ||(status.getPeerStatus() != PeerDownloadStatus.IN_QUEUE)) {
			// if peer is not in queue - totally remove
			partStatus.removePartStatus(peer);
			fileRequestList.remove(peer);
			peer_list.remove(peer);
			download_status_list.removePeer(peer);
			peer.getSessionList().removeSession(this);
			return ;
		}
		
		if (connecting_peers.contains(peer)) {
			connecting_peers.remove(peer);
			
		}
		
	}
	
	public void reportInactivity(Peer peer, long time) {
	}
	
	public void  processPacket(Peer sender, ScannedPacket packet,PeerSessionList packetRouter) {		
		if (getStatus() == STATUS_STOPPED)
			return;
		
		if (packet instanceof JMPeerFileRequestAnswerSP ) {
			
			download_status_list.updatePeerHistory(sender,OP_FILEREQANSWER);
					
			download_status_list.setPeerStatus(sender,PeerDownloadStatus.UPLOAD_REQUEST);
			
			sender.sendPacket(PacketFactory.getUploadReuqestPacket(sharedFile.getFileHash()));
			
			return ;
		}
		
		if (packet instanceof JMPeerFileStatusAnswerSP ) {
			
			JMPeerFileStatusAnswerSP scannedPacket = (JMPeerFileStatusAnswerSP) packet;
			
			download_status_list.updatePeerHistory(sender,OP_FILESTATUS);
			
			JMuleBitSet bitSet = scannedPacket.getPartStatus();
			
			if (bitSet.getPartCount() == 0) {
				
				int partCount = (int) ((this.sharedFile.length()/PARTSIZE));
				
				if (this.sharedFile.length() % PARTSIZE != 0)
					
	    			partCount++;
				
				bitSet = new JMuleBitSet(partCount);
				
				bitSet.setPartCount(partCount);
				
				for(int i = 0;i<partCount;i++)
					
					bitSet.set(i);
			}
			
			partStatus.addPartStatus(sender, bitSet);
			
			if (!this.sharedFile.hasHashSet()) {
				
				download_status_list.setPeerStatus(sender,PeerDownloadStatus.HASHSET_REQUEST);
				
				sender.sendPacket(PacketFactory.getRequestPartHashSetPacket(this.sharedFile.getFileHash()));
				
				return;
			}
			
			return ;
		}
		
			
		if (packet instanceof JMPeerFileHashSetAnswerSP ) {
			
			JMPeerFileHashSetAnswerSP sPacket = (JMPeerFileHashSetAnswerSP) packet;
			
			download_status_list.updatePeerHistory(sender,OP_HASHSETANSWER);
			
			PartHashSet partHashSet = sPacket.getPartHashSet();
			if (!sharedFile.hasHashSet()) {
				
					try {
						sharedFile.setHashSet(partHashSet);
					} catch (SharedFileException e) {
						sender.ban();
						return ;
					}
			}

			if (this.sharedFile.getGapList().size()==0) {
					if (sharedFile.checkFullFileIntegrity()) {
						
					completeDownload();
					
					return;
				} 
			}
			return;
		}
		
		/** 
		 * Download begin here
		 **/
		if (packet instanceof JMPeerAcceptUploadRequestSP ) {
			
			download_status_list.setPeerStatus(sender, PeerDownloadStatus.ACTIVE);
			
			download_status_list.getDownloadStatus(sender).setResendCount(0);
			
			download_status_list.updatePeerHistory(sender,OP_SLOTGIVEN);
			
			if (this.sharedFile.getGapList().size()==0)
				
				if (sharedFile.checkFullFileIntegrity()) {
					
				completeDownload();
				
				} else {
				}
			
			fileChunkRequest(sender);
			
			return ;
		}
		
		/** Received file chunk **/
		
		if (packet instanceof JMPeerSendingPartSP ) {
			
			JMPeerSendingPartSP sPacket = (JMPeerSendingPartSP) packet;
			
			download_status_list.updatePeerHistory(sender,OP_SENDINGPART);
			
			download_status_list.setPeerStatus(sender,PeerDownloadStatus.ACTIVE);
			
			download_status_list.getDownloadStatus(sender).setResendCount(0);
			
			long available_part_count = sharedFile.getAvailablePartCount();
			
			try {
				
				sharedFile.writeData(sPacket.getFileChunk());
				
			} catch (SharedFileException e2) {
				
				e2.printStackTrace();
				
			}
			
			long new_available_part_count = sharedFile.getAvailablePartCount();
			
			if (new_available_part_count > available_part_count) {
				// offer new files to connected server
				Server connected_server = JMuleCoreFactory.getSingleton().getServerManager().getConnectedServer();
				if (connected_server!=null)
					connected_server.offerFiles();
			}
			
			fileRequestList.splitFragment(sender, sPacket.getFileChunk().getChunkStart(), sPacket.getFileChunk().getChunkEnd());
			
			if (this.sharedFile.getGapList().size()==0)
				
				if (!sharedFile.hasHashSet()) {
					// Request hash set 
					Packet sendPacket = PacketFactory.getRequestPartHashSetPacket(sharedFile.getFileHash());
					List<PeerDownloadStatus> status_list = download_status_list.getPeersByStatus(PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED);
					
					for(PeerDownloadStatus s : status_list) {
						s.getPeer().sendPacket(sendPacket);
					}
										
					return ;
					
				} else {
					
					if (sharedFile.checkFullFileIntegrity()) {
					
						completeDownload();
						
						return ;
						
					} 
					
				}
			FragmentList list = fileRequestList.get(sender);
			if (list != null)
				if (this.fileRequestList.get(sender).size()!=0) {
					//Have to receive more packets
					return ;
				}
			
			fileChunkRequest(sender);
			
			
			
			return ;
			
		}
		
		
		if (packet instanceof JMPeerFileNotFoundSP ) {
			
			download_status_list.updatePeerHistory(sender,OP_FILEREQANSNOFILE);
			
			download_status_list.setPeerStatus(sender,PeerDownloadStatus.DISCONNECTED);
			
			sender.disconnect();
			
			return ;
			
		}
		
		if (packet instanceof JMPeerSlotTakenSP ) {
						
			download_status_list.updatePeerHistory(sender,OP_SLOTTAKEN);
			
			download_status_list.setPeerStatus(sender, PeerDownloadStatus.SLOTREQUEST);
			
			return;
			
		}
		
		if (packet instanceof JMPeerQueueRankingSP ) {
			
			JMPeerQueueRankingSP scannedPacket = (JMPeerQueueRankingSP) packet;
						
			download_status_list.updatePeerHistory(sender,OP_EMULE_QUEUERANKING,scannedPacket.getQueueRanking());
			
			download_status_list.setPeerStatus(sender, PeerDownloadStatus.IN_QUEUE);
			
			
			return ;
		}
		
	}
	

	private void queueSources() {
		
		Server server;
		
		server = ServerManagerFactory.getInstance().getConnectedServer();
		
		if (server!=null)
			
			server.requestSources(sharedFile.getFileHash(), sharedFile.length());
		
		/** Queue peers for sources **/
		
		//List<Peer> peerList = PeerManagerFactory.getInstance().getPeers(getFileHash());
		
		//for(Peer p : peerList) {
			
		//	if (p.getStatus() == Peer.TCP_SOCKET_CONNECTED) {
				
				//Packet packet = EMulePacketFactory.getSourcesRequestPacket(sharedFile.getFileHash());
				
				//p.sendPacket(packet);
		//	}
		//}
		
	}
	
	

	private void completeDownload() {
		
		stopDownload();
		
		try { 
		  SharingManagerFactory.getInstance().makeCompletedFile(sharedFile.getFileHash());
		}catch(Throwable t) {
			
		}
	
		DownloadManagerFactory.getInstance().removeDownload(getFileHash());
		
		Server server = ServerManagerFactory.getInstance().getConnectedServer();
		
		if (server != null)
			
			server.offerFiles();
	}
	
	private void fileChunkRequest(Peer peer) {
				
		long blockSize = ConfigurationManagerFactory.getInstance().getDownloadBandwidth();
				
		if (blockSize > BLOCKSIZE)
			
				blockSize = BLOCKSIZE;
				
		FileChunkRequest fragments[] = downloadStrategy.fileChunk3Request(peer, blockSize, this.sharedFile.length(),
				sharedFile.getGapList(), partStatus, fileRequestList);
		
		int unused = 0;
		
		for(int i = 0;i<fragments.length;i++) {
			//System.out.println(fragments[i]);
			if (((fragments[i].getChunkBegin()==fragments[i].getChunkEnd())&&(fragments[i].getChunkBegin()==0)))
				
				unused++;
		}
		if (unused==3){
			
			download_status_list.setPeerStatus(peer, PeerDownloadStatus.ACTIVE_UNUSED);
			
			download_status_list.getDownloadStatus(peer).setLastFilePartRequest(null);
			
			return;
			
			}
		
		download_status_list.setPeerStatus(peer, PeerDownloadStatus.ACTIVE);
		
		Packet packet2 = PacketFactory.getPeerRequestFileParts(sharedFile.getFileHash(),
				new long[]{fragments[0].getChunkBegin(),fragments[1].getChunkBegin(),fragments[2].getChunkBegin(),
				   fragments[0].getChunkEnd(),  fragments[1].getChunkEnd(),  fragments[2].getChunkEnd()} );
		
		download_status_list.getDownloadStatus(peer).setLastFilePartRequest(packet2);
		
		peer.sendPacket(packet2);
	}
	
	public float getSpeed() {
		
		float downloadSpeed = 0;
		
		for(Peer peer : peer_list)
			
			downloadSpeed += peer.getDownloadSpeed();
		
		return downloadSpeed;
	}

	public FileHash getFileHash() {
		
		return this.sharedFile.getFileHash();
		
	}

	public String getSharingName() {
		
		return sharedFile.getSharingName();
		
	}
	
	public GapList getGapList() {
		
		return this.sharedFile.getGapList();
		
	}
	
	public long getETA() {
		float speed = getSpeed();
		long time = 0;
		if (speed!=0)
			time = (long)((sharedFile.length() - getTransferredBytes())/speed);
		else
			time = Misc.INFINITY_AS_INT;
		
		return time;
	}
	
	public boolean hasPeer(Peer peer) {
		boolean result =  peer_list.contains(peer);
		if (!result)
			result = connecting_peers.contains(peer);
		return result;
	}
	
	public PeerDownloadStatus getPeerDownloadStatus(Peer peer) {
		return download_status_list.getDownloadStatus(peer);
	}
	
	public JMIterable<Peer> getPeers() {
		List<Peer> list = new LinkedList<Peer>();
		list.addAll(peer_list);
		list.addAll(connecting_peers);
		return new JMIterable<Peer>(list.iterator());
	}
	
	public String toString(){
		
		String str=" [ ";
		
		str+= sharedFile.getSharingName() + " " + sharedFile.getGapList() + " " + sharedFile.getFileHash() + "\n";
		str+= "Peer used by session : \n";
		
		for(Peer peer : peer_list)
			str+=peer+"\n";
		
		str+=" Connecting peers : \n";
		
		for(Peer peer : connecting_peers) 
			str+=peer+"\n";
		
		str += "\nDownload status list :\n"+ download_status_list + "\n";
		
		str+= fileRequestList+"\n";
		str+=" ] ";
		
		return str;
	}
	
	public int getStatus() {
		
		return this.sessionStatus;
		
	}
	
	public boolean isStarted() {
		return sessionStatus==STATUS_STARTED;
	}
	
	private void setStatus(int newStatus) {
		
		this.sessionStatus = newStatus;
	}
	
	public int hashCode() {
		
		return this.sharedFile.getFileHash().hashCode();
		
	}
	
	public boolean equals(Object object){
		
		if (object==null) return false;
		
		if (!(object instanceof DownloadSession)) return false;
		
		return this.hashCode()==object.hashCode();
		
	}
	
	public double getPercentCompleted() {
		
		return this.sharedFile.getPercentCompleted();
		
	}
	
	public long getTransferredBytes() {
		
		return this.sharedFile.getDownloadedBytes();
		
	}
	
	public long getFileSize() {
		
		return this.sharedFile.length();
	}
	
	public int getPeersCount() {
		
		return peer_list.size() + connecting_peers.size();
		
	}
	
	public String getTempFileName() {
		
		return sharedFile.getFile().getAbsolutePath();
		
	}
	
	public String getMetFilePath() {
		
		return sharedFile.getPartMetFile().getAbsolutePath();
		
	}
	
	public long getPartCount() {
		
		return Misc.getPartCount(sharedFile.length());
		
	}
	
	public long getAvailablePartCount() {
		
		return sharedFile.getAvailablePartCount();
		
	}
	
	public ED2KFileLink getED2KLink() {
		
		return new ED2KFileLink(getSharingName(),getFileSize(),getFileHash());
		
	}
	
	public SharedFile getSharedFile() {
		
		return sharedFile;
	}	
	
	private void activateUnusedPeers() {
		
		List<PeerDownloadStatus> status_list = download_status_list.getPeersByStatus(PeerDownloadStatus.ACTIVE_UNUSED);
		
		for(PeerDownloadStatus status : status_list) {
			
			fileChunkRequest(status.getPeer());
			
		}
		
	}
	
	
	private void resendFilePartsRequest(Peer peer) {
		
		PeerDownloadStatus status = download_status_list.getDownloadStatus(peer);
		
		if (status.getResendCount()>5) return ;
		
		status.setResendCount(status.getResendCount()+1);
		
		if (status.getLastFilePartRequest()==null) return ;
		
		status.setPeerStatus(status.getPeerStatus());
		
		peer.sendPacket(status.getLastFilePartRequest());
	}

	public int getCompletedSources() {
		return partStatus.getCompletedSources();
	}
	
	public int getPartialSources() {
		return partStatus.getPartialSources();
	}
	
	public int getUnknownSources() {
		return getPeersCount() - (getCompletedSources() + getPartialSources());
	}
	
	/** Query for sources **/
	
	private class SourcesQueryThread extends JMThread {
		
		private boolean stop = false;
		
		public SourcesQueryThread() {
			
			super("Query sources thread");
			
		}
		
		public void run() {
			
			while(!stop) {
				
				queueSources();
				
				try {
					
					this.join(ConfigurationManager.SOURCES_QUERY_INTERVAL);
					
				} catch (InterruptedException e) {
				}
			}
		}
		
		public void JMStop() {
			
			stop = true;
			
			interrupt();
			
		}
		
	}
	
	// disabled, need more research
	private class SlotRequestThread extends JMThread {
		
		private boolean stop = false;
		
		public SlotRequestThread() {
			super("Slot request thread");
		}
		
		public void run() {
			
			while(!stop) {
				
				try {
					Thread.sleep(1000*60*30);
				} catch (InterruptedException e) {
					if (stop) return ;
					continue;
				}
				
				List<PeerDownloadStatus> status_list = download_status_list.getPeersByStatus(PeerDownloadStatus.IN_QUEUE, PeerDownloadStatus.SLOTREQUEST);
				
				for(PeerDownloadStatus status : status_list) {
		
					// test status.getPeer().sendPacket(PacketFactory.getUploadReuqestPacket(sharedFile.getFileHash()));
					
				}
		
			}			
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
			
		}
		
	}
	
	// partial functionality, need more research
	private class PeersMonitor extends JMThread {
		
		private boolean stop = false;
		
		public PeersMonitor() {
			
			super("Download activity monitor");

		}
		
		public void JMStop() {
			
			stop = true;
			
			interrupt();
			
		}
		
		public void run() {
			
			while(!stop) {

				try {
					Thread.sleep(PEER_MONITOR_INTERVAL);
				} catch (InterruptedException e) {
					if (stop) return ;
					continue;
				}
				
				List<PeerDownloadStatus> frenzed_list = download_status_list.getPeersWithInactiveTime(PeerDownloadStatus.ACTIVE,1000*60 * 3);
				
				for(PeerDownloadStatus status : frenzed_list) {
					
					resendFilePartsRequest(status.getPeer());
					
				}
				
				int activePeers = download_status_list.getPeersByStatus(PeerDownloadStatus.ACTIVE_UNUSED).size();
				
				if (activePeers>5) continue;
				
				List<PeerDownloadStatus> status_list = download_status_list.getPeersWithInactiveTime(1000*60*2);
				
				if (activePeers!=0)  {
				
				for(PeerDownloadStatus status : status_list) {
					
					if ( !fileRequestList.hasPeer( status.getPeer() ) ) continue;
					
					if (status.getPeerStatus()!=PeerDownloadStatus.ACTIVE_UNUSED) 
						if (status.getPeerStatus()!=PeerDownloadStatus.IN_QUEUE) {
											
						status.setPeerStatus(PeerDownloadStatus.INACTIVE);
						
						fileRequestList.remove(status.getPeer());
						
					}
					
				}
				
				}
				
				
				
			}
		}
		
	}
}
