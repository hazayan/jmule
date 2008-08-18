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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMIterable;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.sharingmanager.SharingManagerFactory;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/18 08:58:07 $$
 */
public class UploadManagerImpl implements UploadManager {

	private Map<FileHash,UploadSession> session_list = new ConcurrentHashMap<FileHash,UploadSession>();
	
	private List<UploadManagerListener> listener_list = new LinkedList<UploadManagerListener>(); 
	
	public void addUpload(FileHash fileHash) {

		UploadSession upload_session = new UploadSession(SharingManagerFactory
				.getInstance().getSharedFile(fileHash));
		
		session_list.put(fileHash, upload_session);
		
		for(UploadManagerListener listener : listener_list)
			
			listener.uploadAdded(fileHash);
		
	}
	
	public boolean hasUpload(FileHash fileHash) {
		
		return session_list.containsKey(fileHash);
		
	}

	public void removeUpload(FileHash fileHash) {
	
		session_list.remove(fileHash);
		
		for(UploadManagerListener listener : listener_list)
			
			listener.uploadRemoved(fileHash);
		
	}

	public UploadSession getUpload(FileHash fileHash) {

		return session_list.get(fileHash);
	
	}

	
	public void addUploadManagerListener(UploadManagerListener listener) {
		
		listener_list.add(listener);
		
	}

	
	public void removeUploadMaanagerListener(UploadManagerListener listener) {
	
		listener_list.remove(listener);
		
	}

	public void initialize() {
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
	            		 total_upload_peers+=session.getPeersCount();
	            	 }
	            	 values.put(JMuleCoreStats.ST_NET_PEERS_UPLOAD_COUNT, total_upload_peers);
                	 
                 }
			}
		});
	}

	public void shutdown() {
	
	}

	public void start() {
	
	}

	public JMIterable<UploadSession> getUploads() {
	
		return new JMIterable<UploadSession>(session_list.values().iterator());
	}


}
