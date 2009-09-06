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
package org.jmule.core.networkmanager;

import java.util.List;
import java.util.Set;

import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.E2DKConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.networkmanager.JMConnection.ConnectionStatus;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * Created on Aug 19, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/31 17:24:11 $
 */
public interface InternalNetworkManager extends NetworkManager {
	
	public void connectToServer(String ip, int port) throws NetworkManagerException;
	
	public void disconnectFromServer() throws NetworkManagerException ;
	
	public ConnectionStatus getServerConnectionStatus();
	
	public void serverConnectingFailed(Throwable cause);
	
	public void serverDisconnected();
	
	public void serverConnected();
	
	public void offerFilesToServer(ClientID userID, List<SharedFile> filesToShare);
	
	public void serverListRequest();
	
	public void doSearchOnServer(SearchQuery searchQuery);
	
	public void requestSourcesFromServer(FileHash fileHash, long fileSize);
	
	public void callBackRequest(ClientID clientID);
	
	public void receivedMessageFromServer(String message);
	
	public void receivedIDChangeFromServer(ClientID clientID, Set<ServerFeatures> serverFeatures);
	
	public void receivedServerStatus(int userCount, int fileCount);
	
	public void receivedServerList(List<String> ipList, List<Integer> portList);
	
	public void receivedSearchResult(SearchResultItemList resultList);
	
	public void receivedSourcesFromServer(FileHash fileHash, List<ClientID> clientIDList, List<Integer> portList);
	
	public void receivedCallBackRequest(String ip, int port);
	
	public void receivedCallBackFailed();
	
	public void addPeer(String ip, int port);
	
	public void addPeer(JMPeerConnection peerConnection);
	
	public void disconnectPeer(String ip, int port);
	
	public void peerConnectingFailed(String ip, int port, Throwable cause);
	
	public void peerDisconnected(String ip, int port);
	
	public void peerConnected(String ip, int port);
	
	public void sendFileChunk(String peerIP, int peerPort, FileHash fileHash, FileChunk fileChunk);
	
	public void sendFileRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFileStatusRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendUploadRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFilePartsRequest(String peerIP, int peerPort, FileHash fileHash, long... partsData);
	
	public void sendPartHashSetRequest(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendSlotRelease(String peerIP, int peerPort);
	
	public void sendEndOfDownload(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFileRequestAnswer(String peerIP, int peerPort, FileHash fileHash, String fileName);
	
	public void sendFileStatusAnswer(String peerIP, int peerPort, PartHashSet partHashSet, long fileSize, GapList gapList);
	
	public void sendFileHashSetAnswer(String peerIP, int peerPort, PartHashSet partHashSet);
	
	public void sendSlotGiven(String peerIP, int peerPort, FileHash fileHash);
	
	public void sendFileNotFound(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedHelloFromPeerAndRespondTo(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
	
	public void receivedHelloAnswerFromPeer(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
	
	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort, FileHash fileHash, FileChunk chunk);
	
	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort, FileHash fileHash, List<FileChunkRequest> requestedChunks);
	
	public void receivedEndOfDownloadFromPeer(String peerIP, int peerPort);
	
	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort, PartHashSet partHashSet);
	
	public void receivedSlotRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedSlotGivenFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedSlotTakenFromPeer(String peerIP, int peerPort);
	
	public void receivedFileRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort, FileHash fileHash, String fileName);
	
	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort, FileHash fileHash);
	
	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort, FileHash fileHash, JMuleBitSet partStatus);
	
	public void receivedCompressedFileChunkFromPeer(String peerIP, int peerPort, FileHash fileHash, FileChunk compressedFileChunk);
	
	public void receivedQueueRankFromPeer(String peerIP, int peerPort, int queueRank);

	public void sendKadPacket(KadPacket packet, IPAddress address, int port);
	
	public void receiveKadPacket(KadPacket packet);
	
	public void receivedEMuleHelloFromPeer(String ip, int port,byte clientVersion, byte protocolVersion, TagList tagList);
	
	public void receivedServerStatus(String ip , int port, int challenge, long userCount, long fileCount, long softLimit, long hardLimit, Set<ServerFeatures> serverFeatures);
	
	public void receivedServerDescription(String ip, int port, String name, String description);
	
	public void receivedNewServerDescription(String ip, int port, int challenge, TagList tagList);
	
}