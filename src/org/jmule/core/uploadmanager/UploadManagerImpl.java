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

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.InternalSharingManager;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.14 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/17 14:54:55 $$
 */
public class UploadManagerImpl extends JMuleAbstractManager implements InternalUploadManager {

	private Map<FileHash,UploadSession> session_list = new ConcurrentHashMap<FileHash,UploadSession>();
	
	private List<UploadManagerListener> listener_list = new LinkedList<UploadManagerListener>(); 
	
	private InternalSharingManager sharing_manager = (InternalSharingManager) SharingManagerSingleton.getInstance();
	private InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	
	UploadManagerImpl() { }

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		
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

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
		for(UploadSession session : session_list.values())
			session.stopSession();
		
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
	
	public UploadQueue getUploadQueue() {
		return UploadQueue.getInstance();
	}
	
	public void addUpload(FileHash fileHash) throws UploadManagerException {
		if (hasUpload(fileHash))
			throw new UploadManagerException("Upload " + fileHash
					+ " already exists");
		UploadSession upload_session = new UploadSession(SharingManagerSingleton
				.getInstance().getSharedFile(fileHash));
		session_list.put(fileHash, upload_session);
		notifyUploadAdded(fileHash);
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

	public void endOfDownload(Peer sender) {
		UploadSession session = getUploadSession(sender);
		if (session == null) return ;
		session.endOfDownload(sender);
		if (session.getPeerCount()==0) {
			session.stopSession();
			session_list.remove(session.getFileHash());
		}
	}
	
	public void receivedSlotReleaseFromPeer(Peer sender) {
		UploadSession session = getUploadSession(sender);
		if (session == null) return ;
		session.endOfDownload(sender);
		if (session.getPeerCount()==0) {
			session.stopSession();
			session_list.remove(session.getFileHash());
		}
	}

	public void receivedFileChunkRequestFromPeer(Peer sender,FileHash fileHash, List<FileChunkRequest> requestedChunks) {
		UploadSession session;
		try {
			session = getUpload(fileHash);
		} catch (UploadManagerException e) {
			e.printStackTrace();
			return ;
		}
		session.receivedFileChunkRequestFromPeer(sender, fileHash, requestedChunks);
	}

	public void receivedFileStatusRequestFromPeer(Peer sender,FileHash fileHash) { 
		if (!sharing_manager.hasFile(fileHash)) {
			// requested file not found!
			return ;
		}
		SharedFile shared_file = sharing_manager.getSharedFile(fileHash);
		if (shared_file instanceof PartialFile){
			PartialFile partialFile = (PartialFile) shared_file;
			network_manager.sendFileStatusAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet(), shared_file.length() ,partialFile.getGapList());
		} else {
			network_manager.sendFileStatusAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet(), shared_file.length() ,new GapList());
		}
		/*UploadSession session;
		try {
			session = getUpload(fileHash);
		} catch (UploadManagerException e) {
			e.printStackTrace();
			return ;
		}
		session.receivedFileStatusRequestFromPeer(sender, fileHash);*/
	}

	public void receivedHashSetRequestFromPeer(Peer sender,FileHash fileHash) {
		if (!sharing_manager.hasFile(fileHash)) {
			// file with fileHash not found !
			return;
		}
		SharedFile shared_file = sharing_manager.getSharedFile(fileHash);
		network_manager.sendFileHashSetAnswer(sender.getIP(), sender.getPort(), shared_file.getHashSet());
		
		/*
		UploadSession session;
		try {
			session = getUpload(fileHash);
		} catch (UploadManagerException e) {
			e.printStackTrace();
			return ;
		}
		session.receivedHashSetRequestFromPeer(sender, fileHash);*/
	}

	public void receivedSlotRequestFromPeer(Peer sender,FileHash fileHash) {
		if (hasUpload(fileHash)) {
			UploadSession upload_session;
			try {
				upload_session = getUpload(fileHash);
			} catch (UploadManagerException e) {
				e.printStackTrace();
				return ;
			}
			upload_session.receivedSlotRequestFromPeer(sender, fileHash);
			return ;
		}
		UploadSession session = new UploadSession(sharing_manager.getSharedFile(fileHash));
		session_list.put(fileHash, session);
		session.receivedSlotRequestFromPeer(sender, fileHash);
	}

	public void receivedFileRequestFromPeer(Peer sender,FileHash fileHash) {
		if (!sharing_manager.hasFile(fileHash)) {
			network_manager.sendFileNotFound(sender.getIP(),sender.getPort(), fileHash);
			return ;
		}
		
		SharedFile shared_file = sharing_manager.getSharedFile(fileHash);
		network_manager.sendFileRequestAnswer(sender.getIP(), sender.getPort(), shared_file.getFileHash(), shared_file.getSharingName());
	}

	public void peerConnected(Peer peer) {
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
	}

	public void peerDisconnected(Peer peer) {
		for(UploadSession session : session_list.values())
			if (session.hasPeer(peer)) {
				session.peerDisconnected(peer);
				break;
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

}
