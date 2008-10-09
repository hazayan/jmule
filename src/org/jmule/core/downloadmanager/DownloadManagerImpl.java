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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharingManagerFactory;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * Created on 2008-Jul-08
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/09 09:30:57 $$
 */
public class DownloadManagerImpl implements DownloadManager {

	private Map<FileHash,DownloadSession> session_list = new ConcurrentHashMap<FileHash,DownloadSession>();
	
	private List<DownloadManagerListener> listener_list = new LinkedList<DownloadManagerListener>();
	
	public DownloadManagerImpl() {
	}
	
	public void addDownload(SearchResultItem searchResult) {
		
		DownloadSession download_session = new DownloadSession(searchResult);
		
		session_list.put(searchResult.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(download_session.getFileHash());
		
	}

	public void addDownload(ED2KFileLink fileLink) {

		DownloadSession download_session = new DownloadSession(fileLink);
		
		session_list.put(fileLink.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(download_session.getFileHash());
		

	}

	public void addDownload(PartialFile partialFile) {

		DownloadSession download_session = new DownloadSession(partialFile);
		
		session_list.put(partialFile.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(download_session.getFileHash());
		
	}
	
	public int getDownloadCount() {
		return session_list.size();
	}
	
	public boolean hasDownload(FileHash fileHash) {
		
		return session_list.containsKey(fileHash);
		
	}

	public void removeDownload(FileHash fileHash) {
		
		DownloadSession download_session = getDownload(fileHash);
		
		if (download_session.getPercentCompleted() != 100d)
			download_session.cancelDownload();
		
		session_list.remove(fileHash);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadRemoved(download_session.getFileHash());
		
	}

	public void startDownload(FileHash fileHash) {
		
		DownloadSession download_session = session_list.get(fileHash);
		
		download_session.startDownload();
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadStarted(download_session.getFileHash());
		
	}

	public void stopDownload(FileHash fileHash) {
	
		DownloadSession download_session = session_list.get(fileHash);
		
		download_session.stopDownload();
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadStopped(download_session.getFileHash());
		
	}

	public JMIterable<DownloadSession> getDownloads() {
		
		return new JMIterable<DownloadSession>(session_list.values().iterator());
		
	}
	
	public void initialize() {
		List<PartialFile> file_list = SharingManagerFactory.getInstance().getPartialFiles();
		
		for(PartialFile file : file_list)
			addDownload(file);
		

  		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES);
		types.add(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT);
		types.add(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types, Map<String, Object> values) {
	             if(types.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES)) {
	            	 long total_downloaded_bytes = 0;
	            	 for(DownloadSession session : session_list.values()) {
	            		 total_downloaded_bytes+=session.getTransferredBytes();
	            	 }
	            	 values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_BYTES, total_downloaded_bytes);
	             }
	             if(types.contains(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT)) {
	            	 values.put(JMuleCoreStats.ST_NET_SESSION_DOWNLOAD_COUNT, session_list.size());
	             }
	             if (types.contains(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT)) {
	            	 int download_peers_count = 0;
	            	 for(DownloadSession session : session_list.values()) {
	            		 download_peers_count+=session.getPeersCount();
	            	 }
	            	 values.put(JMuleCoreStats.ST_NET_PEERS_DOWNLOAD_COUNT, download_peers_count);
	             }
			}
		});	

	}

	public void shutdown() {
		for(DownloadSession download_session : session_list.values())
			if (download_session.isStarted())
				download_session.stopDownload();
		
	}

	public void start() {

		
	}

	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList) {
		
		DownloadSession downloadSession = session_list.get(fileHash);
		if (downloadSession != null)
			downloadSession.addDownloadPeers(peerList);
	}

	public DownloadSession getDownload(FileHash fileHash) {
		
		return session_list.get(fileHash);
		
	}

	public void addDownloadManagerListener(DownloadManagerListener listener) {
		
		listener_list.add(listener);
		
	}

	public void removeDownloadMangerListener(DownloadManagerListener listener) {

		listener_list.add(listener);
		
	}
	


}
