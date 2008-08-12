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

import java.util.List;

import org.jmule.core.JMIterable;
import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.PartialFile;

/**
 * Created on 2008-Apr-20
 * @author javajox
 * @author binary
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/12 07:00:53 $$
 */
public interface DownloadManager extends JMuleManager {
	
	/**
	 * Add download based on the search result.
	 * @param searchResult 
	 */
	public void addDownload(SearchResultItem searchResult);
	
	/**
	 * Add download based on the ed2k file link.
	 * @param fileLink
	 */
	public void addDownload(ED2KFileLink fileLink);
	
	/**
	 * Add download based on the partialFile
	 * @param partialFile
	 */
	public void addDownload(PartialFile partialFile);

	/**
	 * Obtain download by FileHash
	 * @param fileHash
	 * @return
	 */
	public DownloadSession getDownload(FileHash fileHash);
	
	public void startDownload(FileHash fileHash);
	
	public void stopDownload(FileHash fileHash);
	
	public void removeDownload(FileHash fileHash);
	
	public boolean hasDownload(FileHash fileHash);

	public JMIterable<DownloadSession> getDownloads();
	
	/**
	 * Add peers who has fileHash to download session.
	 * @param fileHash
	 * @param peerList
	 */
	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList);
	
	/**
	 * Add new Download Manager listener.
	 * @param listener
	 */
	public void addDownloadManagerListener(DownloadManagerListener listener);
	
	/**
	 * Remove the given listener from listener list.
	 * @param listener
	 */
	public void removeDownloadMangerListener(DownloadManagerListener listener);
	
}
