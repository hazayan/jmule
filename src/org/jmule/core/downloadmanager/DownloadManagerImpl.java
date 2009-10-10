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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerListener;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * Created on 2008-Jul-08
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.16 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/10/10 07:47:53 $$
 */
public class DownloadManagerImpl extends JMuleAbstractManager implements InternalDownloadManager {

	private Map<FileHash, DownloadSession> session_list = new ConcurrentHashMap<FileHash, DownloadSession>();
	private List<DownloadManagerListener> listener_list = new LinkedList<DownloadManagerListener>();
	
	private InternalPeerManager peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
	
	public DownloadManagerImpl() {
		peer_manager.addPeerManagerListener(new PeerManagerListener() {
			public void newPeer(Peer peer) {
			}
			
			public void peerConnecting(Peer peer) { }
			
			public void peerRemoved(Peer peer) {
				
			}

			public void peerConnected(Peer peer) {
				for(DownloadSession session : session_list.values())
					if (session.hasPeer(peer)) {
						session.peerConnected(peer);
						return ;
					}
			}

			public void peerConnectingFailed(Peer peer, Throwable cause) {
				for(DownloadSession session : session_list.values())
					if (session.hasPeer(peer)) {
						session.peerConnectingFailed(peer, cause);
						return ;
					}
			}

			public void peerDisconnected(Peer peer) {
				for(DownloadSession session : session_list.values())
					if (session.hasPeer(peer)) {
						session.peerDisconnected(peer);
						return ;
					}
			}

			
			
		});
	}
	
	public void addDownload(SearchResultItem searchResult) throws DownloadManagerException {
		synchronized(session_list) {
			if (hasDownload(searchResult.getFileHash()))
				throw new DownloadManagerException("Download "
						+ searchResult.getFileHash() + " already exists");
			DownloadSession download_session = new DownloadSession(searchResult);
			session_list.put(searchResult.getFileHash(), download_session);
		}
		notifyDownloadAdded(searchResult.getFileHash());
	}

	public void addDownload(ED2KFileLink fileLink) throws DownloadManagerException {
		synchronized(session_list) {
			if (hasDownload(fileLink.getFileHash()))
				throw new DownloadManagerException("Download "
						+ fileLink.getFileHash() + " already exists");
			DownloadSession download_session = new DownloadSession(fileLink);
			session_list.put(fileLink.getFileHash(), download_session);
		}
		notifyDownloadAdded(fileLink.getFileHash());
	}

	public void addDownload(PartialFile partialFile) throws DownloadManagerException {
		synchronized(session_list) {
			if (hasDownload(partialFile.getFileHash()))
				throw new DownloadManagerException("Download "
						+ partialFile.getFileHash() + " already exists");
			DownloadSession download_session = new DownloadSession(partialFile);
			session_list.put(partialFile.getFileHash(), download_session);
		}
		notifyDownloadAdded(partialFile.getFileHash());
	}

	public void removeDownload(FileHash fileHash) throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = getDownload(fileHash);
		
		if (download_session.getPercentCompleted() != 100d)
			download_session.cancelDownload();
		
		session_list.remove(fileHash);
		notifyDownloadRemoved(fileHash);
	}

	public void startDownload(FileHash fileHash) throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = session_list.get(fileHash);
		if (download_session.isStarted())
			throw new DownloadManagerException("Download " + fileHash+" is already started");
		download_session.startDownload();
		notifyDownloadStarted(fileHash);
	}

	public void stopDownload(FileHash fileHash)  throws DownloadManagerException {
		if (!hasDownload(fileHash))
			throw new DownloadManagerException("Download " + fileHash
					+ " not found ");
		DownloadSession download_session = session_list.get(fileHash);
		if (!download_session.isStarted())
			throw new DownloadManagerException("Download " + fileHash + " is already stopped");
		download_session.stopDownload();
		notifyDownloadStopped(fileHash);
	}

	public int getDownloadCount() {
		return session_list.size();
	}

	public boolean hasDownload(FileHash fileHash) {
		return session_list.containsKey(fileHash);
	}

	public List<DownloadSession> getDownloads() {
		List<DownloadSession> result = new ArrayList<DownloadSession>();
		result.addAll(session_list.values());
		return result;
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		List<PartialFile> file_list = SharingManagerSingleton.getInstance()
				.getPartialFiles();

		for (PartialFile file : file_list)
			try {
				addDownload(file);
			} catch (DownloadManagerException e) {
				e.printStackTrace();
			}

		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,
					Map<String, Object> values) {
				if (types
						.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES)) {
					long total_downloaded_bytes = 0;
					for (DownloadSession session : session_list.values()) {
						total_downloaded_bytes += session.getTransferredBytes();
					}
					values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES,
							total_downloaded_bytes);
				}
				if (types
						.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT)) {
					values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT,
							session_list.size());
				}
				if (types.contains(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT)) {
					int download_peers_count = 0;
					for (DownloadSession session : session_list.values()) {
						download_peers_count += session.getPeerCount();
					}
					values.put(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT,
							download_peers_count);
				}
			}
		});

	}

	public void shutdown() {

		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		for (DownloadSession download_session : session_list.values())
			if (download_session.isStarted())
				download_session.stopDownload(false);

	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

	}

	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList) {
		DownloadSession downloadSession = session_list.get(fileHash);
		if (downloadSession != null)
			downloadSession.addDownloadPeers(peerList);
	}

	public DownloadSession getDownload(FileHash fileHash) throws DownloadManagerException {
		return session_list.get(fileHash);
	}

	public void addDownloadManagerListener(DownloadManagerListener listener) {
		listener_list.add(listener);
	}

	public void removeDownloadMangerListener(DownloadManagerListener listener) {
		listener_list.add(listener);
	}

	protected boolean iAmStoppable() {
		return false;
	}
	

	public void receivedCompressedFileChunk(String peerIP, int peerPort,
			FileHash fileHash, FileChunk compressedFileChunk) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedCompressedFileChunk(peerIP, peerPort,
				compressedFileChunk);
	}

	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedFileNotFoundFromPeer(peerIP, peerPort);
	}

	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort,
			FileHash fileHash, String fileName) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedFileRequestAnswerFromPeer(peerIP, peerPort, fileName);
	}

	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, JMuleBitSet partStatus) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedFileStatusResponseFromPeer(peerIP, peerPort, fileHash,
				partStatus);
	}

	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, PartHashSet partHashSet) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedHashSetResponseFromPeer(peerIP, peerPort, partHashSet);
	}

	public void receivedQueueRankFromPeer(String peerIP, int peerPort,
			int queueRank) {
		DownloadSession session;
		try {
			session = getDownloadSession(peerIP, peerPort);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return ;
		}	
		session.receivedQueueRankFromPeer(peerIP, peerPort, queueRank);
	}

	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort,
			FileHash fileHash, FileChunk chunk) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedRequestedFileChunkFromPeer(peerIP, peerPort, fileHash,
				chunk);
	}

	public void receivedSlotGivenFromPeer(String peerIP, int peerPort) {
		DownloadSession session;
		try {
			session = getDownloadSession(peerIP, peerPort);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.receivedSlotGivenFromPeer(peerIP, peerPort);
	}

	public void receivedSlotTakenFromPeer(String peerIP, int peerPort) {
		DownloadSession session;
		try {
			session = getDownloadSession(peerIP, peerPort);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return ;
		}
		session.receivedSlotTakenFromPeer(peerIP, peerPort);
	}

	public void receivedSourcesFromServer(FileHash fileHash, List<Peer> peerList) {
		DownloadSession session;
		try {
			session = getDownload(fileHash);
		} catch (DownloadManagerException e) {
			e.printStackTrace();
			return;
		}
		session.addDownloadPeers(peerList);
	}

	private DownloadSession getDownloadSession(String ip, int port) throws DownloadManagerException {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(ip, port)) 
				return session;
		throw new DownloadManagerException("Downloadsession with " + ip + ":" + port+" not found");
	}

	public boolean hasPeer(Peer peer) {
		for(DownloadSession session : session_list.values())
			if (session.hasPeer(peer)) 
				return true;
		return false;
	}
	
	private void notifyDownloadStarted(FileHash fileHash) {
		for(DownloadManagerListener listener : listener_list)
			try {
				listener.downloadStarted(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadStopped(FileHash fileHash) {
		for(DownloadManagerListener listener : listener_list)
			try {
				listener.downloadStopped(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadAdded(FileHash fileHash) {
		for(DownloadManagerListener listener : listener_list)
			try {
				listener.downloadAdded(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	
	private void notifyDownloadRemoved(FileHash fileHash) {
		for(DownloadManagerListener listener : listener_list)
			try {
				listener.downloadRemoved(fileHash);
			}catch(Throwable t) {
				t.printStackTrace();
			}
	}
	

}
