/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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

import java.util.List;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.peermanager.Peer;

/**
 * Created on Aug 29, 2009
 * 
 * @author binary256
 * @version $Revision: 1.1 $ Last changed by $Author: binary255 $ on $Date: 2009/09/17 18:30:12 $
 */
public interface InternalUploadManager extends UploadManager {

	public void addUpload(FileHash fileHash) throws UploadManagerException;
	
	public void endOfDownload(String peerIP, int peerPort);

	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash, List<FileChunkRequest> requestedChunks);

	public void receivedFileRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash);

	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash);

	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash);

	public void receivedSlotRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash);

	public void receivedSlotReleaseFromPeer(String peerIP, int peerPort);
	
	public void removeUpload(FileHash fileHash);

	public boolean hasPeer(Peer peer);
}
