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

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.ED2KFileLink;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.PartialFile;

/**
 * Created on 2008-Apr-20
 * @author javajox
 * @author binary
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 17:42:41 $$
 */
public interface DownloadManager extends JMuleManager {

	public void addDownload(SearchResultItem searchResult)
			throws DownloadManagerException;

	public void addDownload(ED2KFileLink fileLink)
			throws DownloadManagerException;

	public void addDownload(PartialFile partialFile)
			throws DownloadManagerException;

	public DownloadSession getDownload(FileHash fileHash)
			throws DownloadManagerException;

	public int getDownloadCount();

	public void startDownload(FileHash fileHash)
			throws DownloadManagerException;

	public void stopDownload(FileHash fileHash) throws DownloadManagerException;

	public void removeDownload(FileHash fileHash) throws DownloadManagerException;

	public boolean hasDownload(FileHash fileHash);

	public List<DownloadSession> getDownloads();

	public void addDownloadManagerListener(DownloadManagerListener listener);

	public void removeDownloadMangerListener(DownloadManagerListener listener);
	
}
