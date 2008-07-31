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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.sharingmanager.SharingManagerFactory;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:48 $$
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
	
	}

	public void shutdown() {
	
	}

	public void start() {
	
	}

	public JMIterable<UploadSession> getUploads() {
	
		return new JMIterable<UploadSession>(session_list.values().iterator());
	}


}
