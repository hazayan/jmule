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

import static org.jmule.core.downloadmanager.PeerDownloadStatus.ACTIVE;
import static org.jmule.core.edonkey.E2DKConstants.BLOCKSIZE;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILENAME;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.DataFormatException;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.DownloadStatusList.PeerDownloadInfo;
import org.jmule.core.downloadmanager.strategy.DownloadStrategy;
import org.jmule.core.downloadmanager.strategy.DownloadStrategyImpl;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.jkad.search.SearchResultListener;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.session.JMTransferSession;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharedFileException;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on 2008-Apr-20
 * @author binary256
 * @version $$Revision: 1.37 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/12/26 09:47:22 $$
 */
public class DownloadSession implements JMTransferSession {
	
	public enum DownloadStatus {
		STARTED, STOPPED
	}
	private final static int PEER_MONITOR_INTERVAL 			= 1000;
	private final static long PEER_RESEND_PACKET_INTERVAL 	= 1000 * 10;
	private final static long UNUSED_PEER_ACTIVATION 		= 1000 * 10;
	private final static long MAX_PEX_CONCURENT_REQEUSTS 	= 30;

	private PartialFile sharedFile;
	private FilePartStatus partStatus;
	private DownloadStatus sessionStatus = DownloadStatus.STOPPED;
	private DownloadStrategy downloadStrategy = new DownloadStrategyImpl();
	private FileRequestList fileRequestList = new FileRequestList();
	private Collection<Peer> session_peers = new ConcurrentLinkedQueue<Peer>();
	private DownloadStatusList download_status_list = new DownloadStatusList(); // store main information about download process
	
	private Map<Peer, List<CompressedChunkContainer>> compressedFileChunks = new ConcurrentHashMap<Peer, List<CompressedChunkContainer>>();
	
	private InternalJKadManager jkad = (InternalJKadManager) JKadManagerSingleton
			.getInstance();
	private InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton
			.getInstance();
	private InternalPeerManager peer_manager = (InternalPeerManager) PeerManagerSingleton
			.getInstance();
	private InternalDownloadManager download_manager = (InternalDownloadManager) DownloadManagerSingleton
			.getInstance();
	private InternalUploadManager upload_manager = (InternalUploadManager) UploadManagerSingleton
			.getInstance();

	private JMTimer download_tasks;
	private JMTimerTask kad_source_search_task = null;
	private JMTimerTask peer_monitor_task = null;
	private JMTimerTask server_queue_sources_task = null;
	private JMTimerTask pex_queue_sources_task = null;
	private DownloadSession() {
		
	}

	DownloadSession(ED2KFileLink fileLink) {
		this();
		try {
			TagList tagList = new TagList();
			Tag tag;
			tag = new StringTag(FT_FILENAME, fileLink.getFileName());
			tagList.addTag(tag);
			tag = new IntTag(FT_FILESIZE, Convert.longToInt((fileLink
					.getFileSize())));
			tagList.addTag(tag);
			createDownloadFiles(fileLink.getFileName(), fileLink.getFileSize(),
					fileLink.getFileHash(), null, tagList);
			int partCount = (int) ((sharedFile.length() / PARTSIZE));
			if (sharedFile.length() % PARTSIZE != 0)
				partCount++;
			partStatus = new FilePartStatus(partCount);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	DownloadSession(PartialFile partialFile) {
		this();
		this.sharedFile = partialFile;
		this.sharedFile.getGapList().sort();
		int partCount = (int) ((sharedFile.length() / PARTSIZE));
		if (sharedFile.length() % PARTSIZE != 0)
			partCount++;
		partStatus = new FilePartStatus(partCount);
		if (partialFile.isDownloadStarted()) {
			startDownload();
		}
	}

	DownloadSession(SearchResultItem searchResult) {
		this();
		try {
			createDownloadFiles(searchResult.getFileName(), searchResult
					.getFileSize(), searchResult.getFileHash(), null,
					(TagList) searchResult);
			int partCount = (int) ((sharedFile.length() / PARTSIZE));
			if (sharedFile.length() % PARTSIZE != 0)
				partCount++;
			partStatus = new FilePartStatus(partCount);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void createDownloadFiles(String fileName, long fileSize,
			FileHash fileHash, PartHashSet hashSet, TagList tagList)
			throws SharedFileException {
		sharedFile = new PartialFile(fileName, fileSize, fileHash, hashSet,
				tagList);
		SharingManagerSingleton.getInstance().addPartialFile(sharedFile);
	}

	synchronized void startDownload() {
		download_tasks = new JMTimer();
		peer_monitor_task = new JMTimerTask() {
			public void run() {
				List<Peer> status_unresponse_list = download_status_list.getPeersWithInactiveTime(PEER_RESEND_PACKET_INTERVAL, PeerDownloadStatus.FILE_STATUS_REQUEST);
				for(Peer peer : status_unresponse_list) {
					network_manager.sendFileStatusRequest(peer.getIP(), peer.getPort(),
							getFileHash());
				}
				for(Peer peer : status_unresponse_list) {
					network_manager.sendFileStatusRequest(peer.getIP(), peer.getPort(),
							getFileHash());
				}
				
				List<Peer> frenzed_list = download_status_list
						.getPeersWithInactiveTime(PEER_RESEND_PACKET_INTERVAL,
								ACTIVE);
				for (Peer peer : frenzed_list) {
					fileRequestList.remove(peer);
					fileChunkRequest(peer);
					//resendFilePartsRequest(peer);
				}
				List<Peer> peer_list = download_status_list
						.getPeersWithInactiveTime(UNUSED_PEER_ACTIVATION,
								PeerDownloadStatus.ACTIVE_UNUSED);
				for (Peer peer : peer_list) {
					fileChunkRequest(peer);
				}
			}
		};
		server_queue_sources_task = new JMTimerTask() {
			public void run() {
				queueSourcesFromServer();
			}
		};
		download_tasks.addTask(peer_monitor_task, PEER_MONITOR_INTERVAL, true);
		download_tasks.addTask(server_queue_sources_task,
				ConfigurationManager.SERVER_SOURCES_QUERY_INTERVAL, true);

		pex_queue_sources_task = new JMTimerTask() {
			public void run() {
				queueSourcesFromPEX();
			}
		};
		download_tasks.addTask(pex_queue_sources_task,
				ConfigurationManager.PEX_SOURCES_QUERY_INTERVAL, true);
		
		if (jkad.isConnected()) {
			kad_source_search_task = new JMTimerTask() {
				Int128 search_id;
				Search search = Search.getSingleton();

				public void run() {
					byte[] hash = sharedFile.getFileHash().getHash().clone();
					org.jmule.core.jkad.utils.Convert.updateSearchID(hash);
					search_id = new Int128(hash);
					if (search.hasSearchTask(search_id))
						return;
					Search.getSingleton().searchSources(search_id,
							new SearchResultListener() {
								public void processNewResults(
										List<Source> result) {
									List<Peer> peer_list = new LinkedList<Peer>();
									for (Source source : result) {
										String address = source.getAddress()
												.toString();
										int tcpPort = source.getTCPPort();
										if (tcpPort == -1) continue;
										if (hasPeer(address, tcpPort)) continue;
										Peer peer;
										if (!peer_manager.hasPeer(address, tcpPort)) {
											try {
												peer = peer_manager.newPeer(
														address, tcpPort,
														PeerSource.KAD);
												peer_list.add(peer);
											} catch (PeerManagerException e) {
												e.printStackTrace();
												continue;
											} 
										} else {
											try {
												peer = peer_manager.getPeer(address, tcpPort);
												peer_list.add(peer);
											} catch (PeerManagerException e) {
												e.printStackTrace();
												continue;
											}
										}
										
										
									}
									addDownloadPeers(peer_list);
								}

								public void searchFinished() {

								}

								public void searchStarted() {

								}

							});
				}
			};
			download_tasks.addTask(kad_source_search_task,
					JKadConstants.KAD_SOURCES_SEARCH_INTERVAL, true);
			kad_source_search_task.run();
		}
		this.setStatus(DownloadStatus.STARTED);
		sharedFile.markDownloadStarted();

		queueSourcesFromServer();

	}

	private void queueSourcesFromServer() {
		if (ServerManagerSingleton.getInstance().isConnected())
			network_manager.requestSourcesFromServer(getFileHash(), sharedFile
					.length());
		/** Queue peers for sources **/
		// List<Peer> peerList =
		// PeerManagerFactory.getInstance().getPeers(getFileHash());
		// for(Peer p : peerList) {
		// if (p.getStatus() == Peer.TCP_SOCKET_CONNECTED) {
		// Packet packet =
		// EMulePacketFactory.getSourcesRequestPacket(sharedFile.getFileHash());
		// p.sendPacket(packet);
		// }
		// }
	}
	
	private void queueSourcesFromPEX() {
		List<Peer> peer_list = download_status_list.getPeersByStatus(PeerDownloadStatus.IN_QUEUE,PeerDownloadStatus.ACTIVE, PeerDownloadStatus.ACTIVE_UNUSED);
		int request_count = 0;
		for(Peer peer : peer_list) {
			if (!peer.isConnected())  continue;
			network_manager.sendSourcesRequest(peer.getIP(), peer.getPort(), sharedFile.getFileHash());
			request_count++;
			if (request_count > MAX_PEX_CONCURENT_REQEUSTS) break;
		}
		
	}
	
	void addDownloadPeers(List<Peer> peerList) {
		for (Peer peer : peerList) {
			if (download_manager.hasPeer(peer))
				continue;
			if (upload_manager.hasPeer(peer))
				continue;
			if (session_peers.contains(peer))
				continue;
			addPeer(peer);
			if (peer.isConnected())
				peerConnected(peer);
			else
				try {
					network_manager.addPeer(peer.getIP(), peer.getPort());
				} catch (NetworkManagerException e) {
					e.printStackTrace();
				}
		}
	}

	private void addPeer(Peer peer) {
		if (hasPeer(peer))
			return;
		session_peers.add(peer);
	}
	
	/** Called when a peer is connected */
	void peerConnected(Peer peer) {
		if (this.getStatus() == DownloadStatus.STOPPED)
			return;
		download_status_list.addPeer(peer);

		network_manager.sendFileRequest(peer.getIP(), peer.getPort(),
				getFileHash());
		download_status_list.setPeerStatus(peer,
				PeerDownloadStatus.UPLOAD_REQUEST);
		network_manager.sendFileStatusRequest(peer.getIP(), peer.getPort(),
				getFileHash());
		download_status_list.setPeerStatus(peer,
				PeerDownloadStatus.FILE_STATUS_REQUEST);
	}

	void peerConnectingFailed(Peer peer, Throwable cause) {
		peerDisconnected(peer);
	}

	void peerDisconnected(Peer peer) {
		//TODO : mark in status list as disconnected, update download_status_list
		compressedFileChunks.remove(peer);
		PeerDownloadStatus status = download_status_list
				.getPeerDownloadStatus(peer);
		if ((status == null) || (status != PeerDownloadStatus.IN_QUEUE)) {
			partStatus.removePartStatus(peer);
			fileRequestList.remove(peer);
			session_peers.remove(peer);
			download_status_list.removePeer(peer);
			return;
		}

	}
	
	void receivedFileNotFoundFromPeer(Peer sender) {
		if (sender == null) return ;
		download_status_list.addPeerHistoryRecord(sender, "File not found");
		download_status_list.setPeerStatus(sender,
				PeerDownloadStatus.DISCONNECTED);
		try {
			peer_manager.disconnect(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	void receivedFileRequestAnswerFromPeer(Peer sender,String fileName) {
		download_status_list.addPeerHistoryRecord(sender, "File request answer");
		download_status_list.setPeerStatus(sender,PeerDownloadStatus.UPLOAD_REQUEST);
		network_manager.sendUploadRequest(sender.getIP(), sender.getPort(), sharedFile.getFileHash());
	}
	
	void receivedFileStatusResponseFromPeer(Peer sender,FileHash fileHash, JMuleBitSet bitSetpartStatus) {
		JMuleBitSet bitSet = bitSetpartStatus;
		if (bitSet.getPartCount() == 0) {
			int partCount = (int) ((this.sharedFile.length() / PARTSIZE));
			if (this.sharedFile.length() % PARTSIZE != 0)
				partCount++;
			bitSet = new JMuleBitSet(partCount);
			bitSet.setPartCount(partCount);
			for (int i = 0; i < partCount; i++)
				bitSet.set(i);
		}
		partStatus.addPartStatus(sender, bitSet);

		if (!this.sharedFile.hasHashSet()) {
			download_status_list.setPeerStatus(sender,
					PeerDownloadStatus.HASHSET_REQUEST);
			network_manager.sendPartHashSetRequest(sender.getIP() , sender.getPort(),getFileHash());
		}
	}
	
	void receivedHashSetResponseFromPeer(Peer sender,PartHashSet partHashSet) {
		download_status_list.addPeerHistoryRecord(sender, "Hash set answer");
		if (!sharedFile.hasHashSet()) {
			try {
				sharedFile.setHashSet(partHashSet);
			} catch (SharedFileException e) {
				// sender.ban();
				return;
			}
		}
		if (this.sharedFile.getGapList().size() == 0) {
			if (sharedFile.checkFullFileIntegrity()) {
				completeDownload();
				return;
			} else {
				// file is broken
			}
		}
		return;
	}
	
	void receivedQueueRankFromPeer(Peer sender, int queueRank) {
		download_status_list.setPeerQueuePosition(sender, queueRank);
	}

	void receivedSlotGivenFromPeer(Peer sender) {
		download_status_list.setPeerStatus(sender, PeerDownloadStatus.ACTIVE);
		download_status_list.setPeerResendCount(sender, 0);
		download_status_list.addPeerHistoryRecord(sender, "Slot given");
		if (this.sharedFile.getGapList().size() == 0)
			if (sharedFile.checkFullFileIntegrity()) {
				completeDownload();
				return ;
			} else {
				// file is broken
			}
		fileChunkRequest(sender);
		return;

	}
	
	void receivedSlotTakenFromPeer(Peer sender) {
		download_status_list.addPeerHistoryRecord(sender, "Slot taken");
		download_status_list.setPeerStatus(sender,
				PeerDownloadStatus.SLOTREQUEST);
	}
	
	private void fileChunkRequest(Peer peer) {
		if (peer == null) {
			return ;
		}
		long blockSize;
		try {
			blockSize = ConfigurationManagerSingleton.getInstance()
					.getDownloadBandwidth();
		} catch (ConfigurationManagerException e) {
			blockSize = BLOCKSIZE;
		}

		if (blockSize > BLOCKSIZE)

			blockSize = BLOCKSIZE;
		
		FileChunkRequest fragments[] = downloadStrategy.fileChunk3Request(peer,
				blockSize, this.sharedFile.length(), sharedFile.getGapList(),
				partStatus, fileRequestList);

		int unused = 0;

		for (int i = 0; i < fragments.length; i++) {
			if (((fragments[i].getChunkBegin() == fragments[i].getChunkEnd()) && (fragments[i]
					.getChunkBegin() == 0)))
				unused++;
		}
		if (unused == 3) {
			download_status_list.setPeerStatus(peer,
					PeerDownloadStatus.ACTIVE_UNUSED);
			download_status_list.setPeerChunkRequests(peer, null);
			return;
		}
		download_status_list.setPeerChunkRequests(peer, fragments);
		download_status_list.setPeerStatus(peer, PeerDownloadStatus.ACTIVE);
		
		network_manager.sendFilePartsRequest(peer.getIP(), peer.getPort(),
				getFileHash(), fragments);
	}

	void receivedRequestedFileChunkFromPeer(Peer sender,FileHash fileHash, FileChunk chunk) {
		processFileChunk(sender, chunk);
	}
	
	void receivedCompressedFileChunk(Peer sender, FileChunk compressedFileChunk) {
		if (sender == null) return ; // or maybe write chunk!
		download_status_list.updatePeerTime(sender);
		if (!compressedFileChunks.containsKey(sender)) {
			List<CompressedChunkContainer> list = new CopyOnWriteArrayList<CompressedChunkContainer>();
			compressedFileChunks.put(sender, list);
		}
		List<CompressedChunkContainer> chunk_list = compressedFileChunks
				.get(sender);
		
		boolean found = false;
		for (CompressedChunkContainer container : chunk_list) {
			if (container.offset == compressedFileChunk.getChunkStart()) {
				compressedFileChunk.getChunkData().position(0);
				found = true;
				container.compressedData.put(compressedFileChunk.getChunkData()
						.array());
				if (container.compressedData.position() == container.compressedData
						.capacity()) {// TODO : Fixme
					container.compressedData.position(0);
					ByteBuffer buffer;
					try {
						buffer = JMuleZLib
								.decompressData(container.compressedData);
						buffer.position(0);
						FileChunk chunk = new FileChunk(container.offset,
								container.offset + buffer.capacity(), buffer);
						processFileChunk(sender, chunk);
						chunk_list.remove(container);
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
		if (!found) {
			CompressedChunkContainer container = new CompressedChunkContainer();
			container.offset = compressedFileChunk.getChunkStart();
			container.compressedData = Misc.getByteBuffer(compressedFileChunk
					.getChunkEnd());
			container.compressedData.put(compressedFileChunk.getChunkData()
					.array());
			chunk_list.add(container);
			if (container.compressedData.position() == container.compressedData
					.capacity()) {// TODO : Fixme
				container.compressedData.position(0);
				ByteBuffer buffer;
				try {
					buffer = JMuleZLib.decompressData(container.compressedData);
					buffer.position(0);
					FileChunk chunk = new FileChunk(container.offset,
							container.offset + buffer.capacity(), buffer);
					processFileChunk(sender, chunk);
					chunk_list.remove(container);
				} catch (DataFormatException e) {

					e.printStackTrace();
				}

			}
		}
	}
	
	void receivedSourcesAnswerFromPeer(Peer peer, List<Peer> peerList) {
		addDownloadPeers(peerList);
	}
	
	
	private void processFileChunk(Peer sender, FileChunk fileChunk) {
		download_status_list.updatePeerTime(sender);
		
		download_status_list.setPeerStatus(sender, PeerDownloadStatus.ACTIVE);
		download_status_list.setPeerResendCount(sender, 0);
		try {
			sharedFile.writeData(fileChunk);
		} catch (SharedFileException e2) {
			e2.printStackTrace();
		}

		fileRequestList.splitFragment(sender, fileChunk.getChunkStart(),fileChunk.getChunkEnd());

		if (this.sharedFile.getGapList().size() == 0)

			if (!sharedFile.hasHashSet()) {
				// Request hash set
				List<Peer> peer_list = download_status_list.getPeersByStatus(
						PeerDownloadStatus.ACTIVE,
						PeerDownloadStatus.ACTIVE_UNUSED);
				for (Peer peer : peer_list) {
					network_manager.sendPartHashSetRequest(peer.getIP(), peer
							.getPort(), getFileHash());
				}
				return;
			} else {

				if (sharedFile.checkFullFileIntegrity()) {
					completeDownload();
					return;
				}
				// file is broken
				network_manager.sendUploadRequest(sender.getIP(), sender
						.getPort(), getFileHash());
				download_status_list.setPeerStatus(sender,
						PeerDownloadStatus.UPLOAD_REQUEST);
				List<Peer> peer_list = download_status_list.getPeersByStatus(
						PeerDownloadStatus.HASHSET_REQUEST,
						PeerDownloadStatus.ACTIVE_UNUSED);
				for (Peer peer : peer_list) {
					network_manager.sendUploadRequest(peer.getIP(), peer
							.getPort(), getFileHash());
					download_status_list.setPeerStatus(peer,
							PeerDownloadStatus.UPLOAD_REQUEST);
				}
			}
		FragmentList list = fileRequestList.get(sender);
		if (list != null)
			if (this.fileRequestList.get(sender).size() != 0) {
				// Have to receive more packets
				return;
			}

		fileChunkRequest(sender);
	}

	void removePeer(Peer peer) {
		if (hasPeer(peer))
			return;
		session_peers.remove(peer);
	}
	
	private void completeDownload() {

		stopDownload();

		try {
			SharingManagerSingleton.getInstance().makeCompletedFile(
					sharedFile.getFileHash());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		try {
			DownloadManagerSingleton.getInstance()
					.removeDownload(getFileHash());
		} catch (DownloadManagerException e) {
			e.printStackTrace();
		}
	}
	
	private void setStatus(DownloadStatus status) {
		this.sessionStatus = status;
	}
	
	synchronized void stopDownload() {
		stopDownload(true);
	}

	synchronized void stopDownload(boolean saveDownloadStatus) {
		compressedFileChunks.clear();

		download_tasks.stop();
		
	/* for (Peer peer : peer_list.values())
			if (peer.isConnected())
				network_manager.sendEndOfDownload(peer.getIP(), peer.getPort(),
						getFileHash());*/

		session_peers.clear();
		download_status_list.clear();
		partStatus.clear();
		fileRequestList.clear();

		setStatus(DownloadStatus.STOPPED);
		if (saveDownloadStatus)
			sharedFile.markDownloadStopped();
	}
	
	void cancelDownload() {
		if (isStarted())
			stopDownload(false);
		SharingManagerSingleton.getInstance().removeSharedFile(
				sharedFile.getFileHash());
	}

	public FileHash getFileHash() {
		return this.sharedFile.getFileHash();
	}

	public long getFileSize() {
		return this.sharedFile.length();
	}

	public GapList getGapList() {
		return this.sharedFile.getGapList();
	}

	public String getMetFilePath() {
		return sharedFile.getPartMetFile().getAbsolutePath();
	}

	public long getPartCount() {
		return Misc.getPartCount(sharedFile.length());
	}

	public int getPartialSources() {
		return partStatus.getPartialSources();
	}

	public int getPeerCount() {
		return session_peers.size();
	}

	public PeerDownloadInfo getPeerDownloadStatus(Peer peer) {
		return download_status_list.getPeerDownloadInfo(peer);
	}

	public List<Peer> getPeers() {
		return Arrays.asList(session_peers.toArray(new Peer[0]));
	}

	public double getPercentCompleted() {
		return this.sharedFile.getPercentCompleted();
	}

	public SharedFile getSharedFile() {
		return sharedFile;
	}

	public String getSharingName() {
		return sharedFile.getSharingName();
	}

	public float getSpeed() {
		float downloadSpeed = 0;
		for (Peer peer : session_peers) {
			downloadSpeed += peer.getDownloadSpeed();
		}
		return downloadSpeed; 
	}

	public DownloadStatus getStatus() {
		return sessionStatus;
	}

	public String getTempFileName() {
		return sharedFile.getFile().getAbsolutePath();
	}

	public long getTransferredBytes() {
		return this.sharedFile.getDownloadedBytes();
	}

	public int getUnknownSources() {
		return session_peers.size() - (getCompletedSources() + getPartialSources());
	}

	public int hashCode() {
		return this.sharedFile.getFileHash().hashCode();
	}

	public boolean hasPeer(Peer peer) {
		return session_peers.contains(peer);
	}
	
	boolean hasPeer(String ip, int port) {
		for(Peer peer : session_peers) 
			if (peer.getIP().equals(ip))
				if (peer.getPort() == port) return true;
		return false;
	}

	public boolean isStarted() {
		return sessionStatus == DownloadStatus.STARTED;
	}
	
	public long getAvailablePartCount() {
		return sharedFile.getAvailablePartCount();
	}

	public int getCompletedSources() {
		return partStatus.getCompletedSources();
	}

	public ED2KFileLink getED2KLink() {
		return new ED2KFileLink(getSharingName(), getFileSize(), getFileHash());
	}

	public long getETA() {
		float speed = getSpeed();
		long time = 0;
		if (speed != 0)
			time = (long) ((sharedFile.length() - getTransferredBytes()) / speed);
		else
			time = Misc.INFINITY_AS_INT;

		return time;
	}
	
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof DownloadSession))
			return false;
		return this.hashCode() == object.hashCode();
	}
	
	public String toString() {
		String str = "[ ";
		str += sharedFile.getSharingName() + " " + sharedFile.getGapList()
				+ " " + sharedFile.getFileHash() + "\n";
		str += "Peer used by session : \n";
		for (Peer peer : session_peers)
			str += peer + "\n";
		str += "\nDownload status list :\n" + download_status_list + "\n";
		String c_chunks = " [ ";
		for(Peer peer : compressedFileChunks.keySet()) {
			c_chunks += peer + " : " + compressedFileChunks.get(peer)+"\n";
		}
		c_chunks += " ]";
		str += "\nCompressed chunks : " + c_chunks + "\n"; 
		str += fileRequestList + "\n";
		str += "]";
		return str;
	}
	
	FilePartStatus getPartStatus() {
		return partStatus;
	}

	class CompressedChunkContainer {
		public ByteBuffer compressedData;
		public long offset;

		public String toString() {
			return "Offset : " + offset + " position : " + compressedData.position() + " capacity : "
					+ compressedData.capacity();
		}

	}
	
}
