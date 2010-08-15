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
package org.jmule.core.downloadmanager.strategy;

import java.util.List;

import org.jmule.core.downloadmanager.FilePartStatus;
import org.jmule.core.downloadmanager.FileRequestList;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/08/15 12:28:18 $$
 */
public interface DownloadStrategy {
	
	/**
	 * @param partsID : ID's of broken parts
	 */
	public void processPartCheckResult(List<Integer> partsID);
	
	/**
	 * 
	 * @param processResult : ID's of broken parts, -1 - file hash not match, -2 - don't have hash set, -3 - hash set size not match 
	 */
	public void processFileCheckResult(List<Integer> processResult); 
	
	public FileChunkRequest fileChunkRequest(Peer sender, long blockSize,long fileSize,GapList gapList,
			FilePartStatus filePartStatus,FileRequestList fileRequestList);
	
	public FileChunkRequest[] fileChunk3Request(Peer sender, long blockSize,long fileSize,GapList gapList,
			FilePartStatus filePartStatus,FileRequestList fileRequestList);
}
