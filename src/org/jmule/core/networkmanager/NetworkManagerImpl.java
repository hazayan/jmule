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

import static org.jmule.core.JMConstants.KEY_SEPARATOR;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMException;
import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.configmanager.InternalConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManagerSingleton;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.downloadmanager.InternalDownloadManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.E2DKConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketFactory;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.UDPPacketFactory;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.networkmanager.JMConnection.ConnectionStatus;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.Peer;
import org.jmule.core.peermanager.PeerManagerException;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.peermanager.Peer.PeerSource;
import org.jmule.core.searchmanager.InternalSearchManager;
import org.jmule.core.searchmanager.SearchManagerSingleton;
import org.jmule.core.searchmanager.SearchQuery;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.servermanager.InternalServerManager;
import org.jmule.core.servermanager.Server;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.uploadmanager.InternalUploadManager;
import org.jmule.core.uploadmanager.UploadManagerSingleton;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on Aug 14, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.20 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/04 16:58:19 $
 */
public class NetworkManagerImpl extends JMuleAbstractManager implements InternalNetworkManager {
	private static final long CONNECTION_UPDATE_SPEED_INTERVAL 		= 1000;
	private Map<String, JMPeerConnection> peer_connections = new ConcurrentHashMap<String, JMPeerConnection>();

	private InternalPeerManager _peer_manager;
	private InternalServerManager _server_manager;
	private InternalConfigurationManager _config_manager;
	private InternalDownloadManager _download_manager;
	private InternalUploadManager _upload_manager;
	private InternalJKadManager _jkad_manager;
	private InternalSearchManager _search_manager;

	private JMServerConnection server_connection = null;

	private JMUDPConnection udp_connection;
	private JMConnectionWaiter connection_waiter;

	private JMTimer connections_maintenance = new JMTimer();
	private JMTimerTask connection_speed_updater;
	
	NetworkManagerImpl() {
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		_peer_manager = (InternalPeerManager) PeerManagerSingleton
				.getInstance();
		_server_manager = (InternalServerManager) ServerManagerSingleton
				.getInstance();
		_config_manager = (InternalConfigurationManager) ConfigurationManagerSingleton
				.getInstance();
		_download_manager = (InternalDownloadManager) DownloadManagerSingleton
				.getInstance();
		_upload_manager = (InternalUploadManager) UploadManagerSingleton
				.getInstance();
		_jkad_manager = (InternalJKadManager) JKadManagerSingleton
				.getInstance();
		_search_manager = (InternalSearchManager) SearchManagerSingleton
				.getInstance();
		
		connection_speed_updater = new JMTimerTask() {
			public void run() {
				for(JMPeerConnection connection : peer_connections.values()) {
					if (connection.getStatus() != ConnectionStatus.CONNECTED) continue;
					JMuleSocketChannel channel = connection.getJMConnection();
					synchronized(channel) {
						channel.file_transfer_trafic.syncSpeed();
						channel.service_trafic.syncSpeed();
					}
				}
			}
		};
	}
	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		udp_connection = new JMUDPConnection();
		try {
			if (_config_manager.isUDPEnabled())
				udp_connection.open();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}

		connection_waiter = new JMConnectionWaiter();
		connection_waiter.start();
		
		connections_maintenance.addTask(connection_speed_updater, CONNECTION_UPDATE_SPEED_INTERVAL, true);
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		try {
			if (_config_manager.isUDPEnabled())
				udp_connection.close();
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
		connection_waiter.stop();

		for (JMPeerConnection connection : peer_connections.values())
			try {
				connection.disconnect();
			} catch (NetworkManagerException e) {
				e.printStackTrace();
			}

		if (server_connection != null)
			try {
				server_connection.disconnect();
			} catch (NetworkManagerException e) {
				e.printStackTrace();
			}
		connections_maintenance.removeTask(connection_speed_updater);
	}
	
	public String toString() {
		String result = "";
		result += "Peer connections : \n";
		for (String key_connection : peer_connections.keySet())
			result += "[" + key_connection + "] = " + "["+ peer_connections.get(key_connection) + "]\n";
		result += "Server connection : " + server_connection + "\n";
		return result;
	}
	
	public float getDownloadSpeed() {
		float result = 0;
		for (JMPeerConnection peer_connection : peer_connections.values())
			if (peer_connection.getStatus() == ConnectionStatus.CONNECTED)
				result += peer_connection.getJMConnection().getDownloadSpeed();
		return result;
	}
	
	public float getUploadSpeed() {
		float result = 0;
		for (JMPeerConnection peer_connection : peer_connections.values())
			if (peer_connection.getStatus() == ConnectionStatus.CONNECTED)
				result += peer_connection.getJMConnection().getUploadSpeed();
		return result;
	}

	public void addPeer(JMPeerConnection peerConnection) throws NetworkManagerException {
		peer_connections.put(peerConnection.getIPAddress() + KEY_SEPARATOR+ peerConnection.getPort(), peerConnection);
		try {
			_peer_manager.newIncomingPeer(peerConnection.getIPAddress(),
					peerConnection.getPort());
		} catch (PeerManagerException cause) {
			cause.printStackTrace();
		}
	}

	public synchronized void addPeer(String ip, int port) throws NetworkManagerException {
		Peer peer;
		try {
			peer = _peer_manager.getPeer(ip, port);
			if (!peer.isHighID()) 
				if (server_connection!=null)
					if (server_connection.getStatus() == ConnectionStatus.CONNECTED) {
						if (_server_manager.getConnectedServer().getClientID().isHighID())
							callBackRequest(peer.getID());
						return ;
					}
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
		JMPeerConnection connection = new JMPeerConnection(ip, port);
		peer_connections.put(ip + KEY_SEPARATOR + port, connection);
		connection.connect();
	}

	public void callBackRequest(ClientID clientID) {
		Packet packet = PacketFactory.getCallBackRequestPacket(clientID);
		try {
			server_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void connectToServer(String ip, int port)
			throws NetworkManagerException {
		if (server_connection != null)
			throw new NetworkManagerException("Already connected to "
					+ server_connection.getIPAddress() + " "
					+ server_connection.getPort());
		server_connection = new JMServerConnection(ip, port);
		server_connection.connect();
	}

	public void disconnectFromServer() throws NetworkManagerException {
		if (server_connection == null)
			throw new NetworkManagerException("Not connected to server");
		server_connection.disconnect();
		server_connection = null;
	}

	public void disconnectPeer(String ip, int port) {
		JMPeerConnection connection = null;
		try {
			connection = getPeerConnection(ip, port);
		} catch (NetworkManagerException e) {
			e.printStackTrace();
			return;
		}
		try {
			connection.disconnect();
			peer_connections.remove(ip+KEY_SEPARATOR+port);
			connection = null;
		} catch (NetworkManagerException e) {
			e.printStackTrace();
		}
	}

	public void doSearchOnServer(SearchQuery searchQuery) {
		Packet search_packet = PacketFactory.getSearchPacket(searchQuery);
		try {
			server_connection.send(search_packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	private JMPeerConnection getPeerConnection(String peerIP, int peerPort)
			throws NetworkManagerException {
		JMPeerConnection peer_connection = peer_connections.get(peerIP
				+ KEY_SEPARATOR + peerPort);
		if (peer_connection == null)
			throw new NetworkManagerException("Peer " + peerIP + KEY_SEPARATOR
					+ peerPort + " not found");
		return peer_connection;
	}

	public float getPeerDownloadServiceSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceDownloadSpeed();
		} catch (Throwable cause) {
			cause.printStackTrace();
			return 0f;
		}
	}

	public float getPeerDownloadSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			if (peer_connection == null)  {
				System.out.println("Peer not found : " + peerIP + " : " + peerPort);
				return 0;
			}
			return peer_connection.getJMConnection().getDownloadSpeed();
		} catch (Throwable cause) {
			cause.printStackTrace();
			return 0f;
		}
	}

	public float getPeerUploadServiceSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getServiceUploadSpeed();
		} catch (Throwable cause) {
			cause.printStackTrace();
			return 0f;
		}
	}

	public float getPeerUploadSpeed(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			return peer_connection.getJMConnection().getUploadSpeed();
		} catch (Throwable cause) {
			cause.printStackTrace();
			return 0f;
		}
	}

	public ConnectionStatus getServerConnectionStatus() {
		if (server_connection == null)
			return ConnectionStatus.DISCONNECTED;
		return server_connection.getStatus();
	}

	protected boolean iAmStoppable() {
		return false;
	}

	public void offerFilesToServer(ClientID userID,
			List<SharedFile> filesToShare) {
		try {
			Packet packet = PacketFactory.getOfferFilesPacket(userID,
					filesToShare);
			server_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void peerConnected(String ip, int port) {
		try {
			JMPeerConnection connection = getPeerConnection(ip, port);
			Server connected_server = _server_manager.getConnectedServer();
			byte[] server_ip = null;
			int server_port = 0;
			ClientID client_id = null;
			if (connected_server != null) {
				server_ip = connected_server.getAddressAsByte();
				server_port = connected_server.getPort();
				client_id = connected_server.getClientID();
			}
			Packet packet = PacketFactory.getPeerHelloPacket(_config_manager
					.getUserHash(), client_id, _config_manager.getTCP(),
					server_ip, server_port, _config_manager.getNickName(),
					E2DKConstants.DefaultJMuleFeatures);
			connection.send(packet);

		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void peerConnectingFailed(String ip, int port, Throwable cause) {
		_peer_manager.peerConnectingFailed(ip, port, cause);
	}

	public void peerDisconnected(String ip, int port) {
		peer_connections.remove(ip + KEY_SEPARATOR + port);
		_peer_manager.peerDisconnected(ip, port);
	}

	public void receivedCallBackFailed() {
		_peer_manager.callBackRequestFailed();
	}

	public void receivedCallBackRequest(String ip, int port) {
		_peer_manager.receivedCallBackRequest(ip, port);
	}

	public void receivedCompressedFileChunkFromPeer(String peerIP,
			int peerPort, FileHash fileHash, FileChunk compressedFileChunk) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedCompressedFileChunk(sender,fileHash, compressedFileChunk);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedEndOfDownloadFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.endOfDownload(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileChunkRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash, List<FileChunkRequest> requestedChunks) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileChunkRequestFromPeer(sender,fileHash, requestedChunks);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedFileNotFoundFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileNotFoundFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileRequestAnswerFromPeer(String peerIP, int peerPort,
			FileHash fileHash, String fileName) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileRequestAnswerFromPeer(sender,fileHash, fileName);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileRequestFromPeer(sender,  fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileStatusRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedFileStatusRequestFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedFileStatusResponseFromPeer(String peerIP, int peerPort,
			FileHash fileHash, JMuleBitSet partStatus) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedFileStatusResponseFromPeer(sender,fileHash, partStatus);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedHashSetRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedHashSetRequestFromPeer(sender,fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedHashSetResponseFromPeer(String peerIP, int peerPort,
			PartHashSet partHashSet) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedHashSetResponseFromPeer(sender,partHashSet.getFileHash(), partHashSet);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedHelloAnswerFromPeer(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerPacketPort,
			TagList tagList, String serverIP, int serverPort) {

		_peer_manager.helloAnswerFromPeer(peerIP, peerPort, userHash, clientID,
				peerPacketPort, tagList, serverIP, serverPort);
	}

	public void receivedHelloFromPeerAndRespondTo(String peerIP, int peerPort,
			UserHash userHash, ClientID clientID, int peerListenPort,
			TagList tagList, String serverIP, int serverPort) {

		_peer_manager.helloFromPeer(peerIP, peerPort, userHash, clientID,
				peerListenPort, tagList, serverIP, serverPort);

		try {
			JMPeerConnection connection = getPeerConnection(peerIP, peerPort);

			Server connected_server = _server_manager.getConnectedServer();
			byte[] server_ip = null;
			int server_port = 0;
			ClientID client_id = null;
			if (connected_server != null) {
				server_ip = connected_server.getAddressAsByte();
				server_port = connected_server.getPort();
				client_id = connected_server.getClientID();
			}

			Packet packet = PacketFactory.getPeerHelloAnswerPacket(
					_config_manager.getUserHash(), client_id, _config_manager
							.getTCP(), _config_manager.getNickName(),
					server_ip, server_port, E2DKConstants.DefaultJMuleFeatures);
			connection.send(packet);
			
			Packet emule_hello_packet = PacketFactory.getEMulePeerHelloPacket();
			connection.send(emule_hello_packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}

	}

	public void receivedIDChangeFromServer(ClientID clientID,
			Set<ServerFeatures> serverFeatures) {
		_server_manager.receivedIDChange(clientID, serverFeatures);
	}

	public void receivedMessageFromServer(String message) {
		_server_manager.receivedMessage(message);
	}

	public void receivedNewServerDescription(String ip, int port,
			int challenge, TagList tagList) {
		_server_manager.receivedNewServerDescription(ip, port, challenge, tagList);
	}

	public void receivedQueueRankFromPeer(String peerIP, int peerPort,
			int queueRank) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedQueueRankFromPeer(sender, queueRank);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}

	}

	public void receivedRequestedFileChunkFromPeer(String peerIP, int peerPort,
			FileHash fileHash, FileChunk chunk) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedRequestedFileChunkFromPeer(sender,fileHash, chunk);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}

	}

	public void receivedSearchResult(SearchResultItemList resultList) {
		_search_manager.receivedServerSearchResult(resultList);
	}

	public void receivedServerDescription(String ip, int port, String name,
			String description) {
		_server_manager.receivedServerDescription(ip, port, name, description);
	}

	public void receivedServerList(List<String> ipList, List<Integer> portList) {
		_server_manager.receivedServerList(ipList, portList);
	}

	public void receivedServerStatus(int userCount, int fileCount) {
		_server_manager.receivedServerStatus(userCount, fileCount);
	}

	public void receivedServerStatus(String ip, int port, int challenge,
			long userCount, long fileCount, long softLimit, long hardLimit,
			Set<ServerFeatures> serverFeatures) {
		_server_manager.receivedServerStatus(ip, port, challenge, userCount,
				fileCount, softLimit, hardLimit, serverFeatures);
	}

	public void receivedSlotGivenFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedSlotGivenFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedSlotReleaseFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedSlotReleaseFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedSlotRequestFromPeer(String peerIP, int peerPort,
			FileHash fileHash) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_upload_manager.receivedSlotRequestFromPeer(sender, fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
		
	}

	public void receivedSlotTakenFromPeer(String peerIP, int peerPort) {
		Peer sender;
		try {
			sender = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedSlotTakenFromPeer(sender);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void receivedSourcesFromServer(FileHash fileHash,
			List<String> clientIPList, List<Integer> portList) {
		List<Peer> peer_list = _peer_manager.createPeerList(clientIPList,
				portList, false, PeerSource.SERVER);
		_download_manager.addDownloadPeers(fileHash, peer_list);
	}

	public void receiveKadPacket(KadPacket packet) {
		_jkad_manager.receivePacket(packet);
	}
	
	public void receivedEMuleHelloFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		try {
			JMPeerConnection connection = getPeerConnection(ip, port);
			Packet response = PacketFactory.getEMulePeerHelloAnswerPacket();
			connection.send(response);
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		_peer_manager.receivedEMuleHelloFromPeer(ip, port, clientVersion,
				protocolVersion, tagList);
	}
	
	public void receivedEMuleHelloAnswerFromPeer(String ip, int port,
			byte clientVersion, byte protocolVersion, TagList tagList) {
		
		_peer_manager.receivedEMuleHelloAnswerFromPeer(ip, port, clientVersion,
				protocolVersion, tagList);
	}
	
	public void receivedSourcesRequestFromPeer(String peerIP, int peerPort, FileHash fileHash) {
		try {
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			_download_manager.receivedSourcesRequestFromPeer(peer, fileHash);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}
	
	public void receivedSourcesAnswerFromPeer(String peerIP, int peerPort, FileHash fileHash, List<String> ipList, List<Integer> portList) {
		try {
			Peer peer = _peer_manager.getPeer(peerIP, peerPort);
			List<Peer> peer_list = _peer_manager.createPeerList(ipList, portList, true, PeerSource.PEX);
			_download_manager.receivedSourcesAnswerFromPeer(peer, fileHash, peer_list);
		} catch (PeerManagerException e) {
			e.printStackTrace();
		}
	}

	public void requestSourcesFromServer(FileHash fileHash, long fileSize) {
		Packet packet = PacketFactory.getSourcesRequestPacket(fileHash,
				fileSize);
		try {
			server_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendMessage(String peerIP, int peerPort, String message) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,peerPort);
			Packet packet = PacketFactory.getMessagePacket(message);
			peer_connection.send(packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void sendCallBackRequest(String peerIP, int peerPort,
			Int128 clientID, FileHash fileHash, IPAddress buddyIP,
			short buddyPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getKadCallBackRequest(clientID,
					fileHash, buddyIP, buddyPort);
			peer_connection.send(packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void sendEndOfDownload(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getEndOfDownloadPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileChunk(String peerIP, int peerPort, FileHash fileHash,
			FileChunk fileChunk) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFilePartSendingPacket(fileHash,
					fileChunk);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}

	}

	public void sendFileHashSetAnswer(String peerIP, int peerPort,
			PartHashSet partHashSet) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileHashReplyPacket(partHashSet);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileNotFound(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileNotFoundPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFilePartsRequest(String peerIP, int peerPort,
			FileHash fileHash, FileChunkRequest... partsData) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getPeerRequestFileParts(fileHash,
					partsData);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileRequestPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileRequestAnswer(String peerIP, int peerPort,
			FileHash fileHash, String fileName) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileRequestAnswerPacket(fileHash,
					fileName);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileStatusAnswer(String peerIP, int peerPort,
			PartHashSet partHashSet, long fileSize, GapList gapList) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileStatusReplyPacket(partHashSet,
					fileSize, gapList);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendFileStatusRequest(String peerIP, int peerPort,
			FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getFileStatusRequestPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendKadPacket(KadPacket packet, IPAddress address, int port) {
		try {
			udp_connection.sendPacket(packet, address, port);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

	public void sendPartHashSetRequest(String peerIP, int peerPort,
			FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getRequestPartHashSetPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendQueueRanking(String peerIP, int peerPort, int queueRank) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getQueueRankingPacket(queueRank);
			peer_connection.send(packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public void sendSlotGiven(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getAcceptUploadPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendSlotRelease(String peerIP, int peerPort) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getSlotReleasePacket();
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void sendUploadRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getUploadReuqestPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendSourcesRequest(String peerIP, int peerPort, FileHash fileHash) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP,
					peerPort);
			Packet packet = PacketFactory.getSourcesRequestPacket(fileHash);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendSourcesResponse(String peerIP, int peerPort, FileHash fileHash, List<Peer> peer_list) {
		try {
			JMPeerConnection peer_connection = getPeerConnection(peerIP, peerPort);
			Packet packet = PacketFactory.getSourcesAnswerPacket(fileHash, peer_list);
			peer_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	

	public void serverConnected() {
		try {
			Packet login_packet = PacketFactory.getServerLoginPacket(
					_config_manager.getUserHash(), _config_manager.getTCP(),
					_config_manager.getNickName());
			server_connection.send(login_packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}

	public void serverConnectingFailed(Throwable cause) {
		JMServerConnection connection = server_connection;
		server_connection = null;
		_server_manager.serverConnectingFailed(connection.getIPAddress(),
				connection.getPort(), cause);
	}

	public void serverDisconnected() {
		JMServerConnection connection = server_connection;
		server_connection = null;
		_server_manager.serverDisconnected(connection.getIPAddress(),
				connection.getPort());
	}

	public void serverListRequest() {
		Packet packet = PacketFactory.getGetServerListPacket();
		try {
			server_connection.send(packet);
		} catch (Throwable cause) {
			cause.printStackTrace();
		}
	}
	
	public void sendServerUDPStatusRequest(String serverIP, int serverPort, int clientTime) {
		UDPPacket packet = UDPPacketFactory.getUDPStatusRequest(clientTime);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPDescRequest(String serverIP, int serverPort) {
		UDPPacket packet = UDPPacketFactory.getUDPServerDescRequest();
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSourcesRequest(String serverIP, int serverPort, FileHash... fileHashSet) {
		UDPPacket packet = UDPPacketFactory.getUDPSourcesRequest(fileHashSet);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPSearchRequest(String serverIP, int serverPort, String searchString) {
		UDPPacket packet = UDPPacketFactory.getUDPSearchPacket(searchString);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	public void sendServerUDPReaskFileRequest(String serverIP, int serverPort, FileHash fileHash) {
		UDPPacket packet = UDPPacketFactory.getUDPReaskFilePacket(fileHash);
		try {
			udp_connection.sendPacket(packet, serverIP, serverPort);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

}
