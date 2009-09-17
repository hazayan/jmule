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
package org.jmule.core.downloadmanager;

import java.util.List;

import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * Created on Aug 29, 2009
 * 
 * @author binary256
 * @version $Revision: 1.1 $ Last changed by $Author: binary255 $ on $Date: 2009/09/17 17:42:41 $
 */
public interface InternalDownloadManager extends DownloadManager {

	public void receivedSourcesFromServer(FileHash fileHash, List<Peer> peerList);

	public void receivedCompressedFileChunk(String peerIP, int peerPort,
			FileHash fileHash, FileChunk compressedFileChunk);

	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort,
			FileHash fileHash);

	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort,
			FileHash fileHash, String fileName);

	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, JMuleBitSet partStatus);

	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, PartHashSet partHashSet);

	public void receivedQueueRankFromPeer(String peerIP, int peerPort,
			int queueRank);

	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort,
			FileHash fileHash, FileChunk chunk);

	public void receivedSlotGivenFromPeer(String peerIP, int peerPort);

	public void receivedSlotTakenFromPeer(String peerIP, int peerPort);

	/**
	 * Add peers which have fileHash to the download session identified by
	 * fileHash
	 * 
	 * @param fileHash
	 * @param peerList
	 */
	public void addDownloadPeers(FileHash fileHash, List<Peer> peerList);

	public boolean hasPeer(Peer peer);

}
