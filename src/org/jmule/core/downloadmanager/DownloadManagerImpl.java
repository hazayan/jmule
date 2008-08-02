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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.sharingmanager.SharingManagerFactory;

/**
 * Created on 07-08-2008
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/02 14:21:10 $$
 */
public class DownloadManagerImpl implements DownloadManager {

	private Map<FileHash,DownloadSession> session_list = new ConcurrentHashMap<FileHash,DownloadSession>();
	
	private List<DownloadManagerListener> listener_list = new LinkedList<DownloadManagerListener>();
	
	public DownloadManagerImpl() {
		
		List<PartialFile> file_list = SharingManagerFactory.getInstance().getPartialFiles();
	
		for(PartialFile file : file_list)
			addDownload(file);
		
	}
	
	public void addDownload(SearchResult searchResult) {
		
		DownloadSession download_session = new DownloadSession(searchResult);
		
		session_list.put(searchResult.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(searchResult.getFileHash());
		
	}

	public void addDownload(ED2KFileLink fileLink) {

		DownloadSession download_session = new DownloadSession(fileLink);
		
		session_list.put(fileLink.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(fileLink.getFileHash());
		

	}

	public void addDownload(PartialFile partialFile) {

		DownloadSession download_session = new DownloadSession(partialFile);
		
		session_list.put(partialFile.getFileHash(), download_session);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadAdded(partialFile.getFileHash());
		
	}
	
	public boolean hasDownload(FileHash fileHash) {
		
		return session_list.containsKey(fileHash);
		
	}

	public void removeDownload(FileHash fileHash) {
		
		getDownload(fileHash).cancelDownload();
		
		session_list.remove(fileHash);
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadRemoved(fileHash);
		
	}

	public void startDownload(FileHash fileHash) {
		
		session_list.get(fileHash).startDownload();
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadStarted(fileHash);
		
	}

	public void stopDownload(FileHash fileHash) {
	
		session_list.get(fileHash).stopDownload();
		
		for(DownloadManagerListener listener : listener_list)
			
			listener.downloadStopped(fileHash);
		
	}

	public JMIterable<DownloadSession> getDownloads() {
		
		return new JMIterable<DownloadSession>(session_list.values().iterator());
		
	}
	
	public void initialize() {

		
	}

	public void shutdown() {

		
	}

	public void start() {

		
	}

	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList) {
		
		DownloadSession downloadSession = session_list.get(fileHash);
		
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
