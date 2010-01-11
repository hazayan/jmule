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

import static org.jmule.core.edonkey.E2DKConstants.FT_FILERATING;
import static org.jmule.core.edonkey.E2DKConstants.OP_ANSWERSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.OP_CHATCAPTCHAREQ;
import static org.jmule.core.edonkey.E2DKConstants.OP_CHATCAPTCHARES;
import static org.jmule.core.edonkey.E2DKConstants.OP_COMPRESSEDPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULEHELLOANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_HELLO;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_END_OF_DOWNLOAD;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATREQ;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_MESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLO;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLOANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_REQUESTPARTS;
import static org.jmule.core.edonkey.E2DKConstants.OP_REQUESTSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVERLIST;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTRELEASE;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTTAKEN;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_CALLBACKFAILED;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_CALLBACKREQUESTED;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVFOUNDSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVIDCHANGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVMESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSEARCHRESULT;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_PEER_UDP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_SERVER_UDP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_TCP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_COMPRESSED_TCP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_EXTENDED_TCP;
import static org.jmule.core.edonkey.E2DKConstants.SERVER_SEARCH_RATIO;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.E2DKConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.peermanager.InternalPeerManager;
import org.jmule.core.peermanager.PeerManagerSingleton;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.20 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/11 17:01:12 $
 */
public class PacketReader {

	public static void readServerPacket(JMuleSocketChannel channel)
			throws JMEndOfStreamException, IOException, DataFormatException,
			UnknownPacketException, MalformattedPacketException,
			MalformattedPacketException {
		InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		

		ByteBuffer header_buffer;

		header_buffer = Misc.getByteBuffer(1);

		channel.read(header_buffer);

		byte packet_header = header_buffer.get(0);

		ByteBuffer packet_length = Misc.getByteBuffer(4);
		channel.read(packet_length);

		ByteBuffer packet_opcode_buffer = Misc.getByteBuffer(1);
		channel.read(packet_opcode_buffer);

		byte packet_opcode = packet_opcode_buffer.get(0);

		int packet_length_proto = packet_length.getInt(0);

		if ((packet_length_proto >= E2DKConstants.MAXPACKETSIZE) || (packet_length_proto <= 0))
			throw new MalformattedPacketException("Invalid packet length "
					+ " Header :" + Convert.byteToHex(packet_header)
					+ " Packet opcode : " + packet_opcode + " Length : "
					+ packet_length_proto);
		
		ByteBuffer packet_data = Misc.getByteBuffer(packet_length_proto - 1);
		channel.read(packet_data);
		packet_data.position(0);

		if (packet_header == PROTO_EMULE_COMPRESSED_TCP) {
			packet_data = JMuleZLib.decompressData(packet_data);
			packet_header = PROTO_EDONKEY_TCP;
			packet_data.position(0);
		}

		if (packet_header != PROTO_EDONKEY_TCP) {
			throw new UnknownPacketException(packet_header, packet_opcode,
					packet_data.array());
		}
		try {
			switch (packet_opcode) {
			case PACKET_SRVMESSAGE: {
				String server_message = readString(packet_data);
				_network_manager.receivedMessageFromServer(server_message);
	
				return ;
			}
			case PACKET_SRVIDCHANGE: {
				byte client_id[] = new byte[4];
				packet_data.get(client_id);
				ClientID clientID = new ClientID(client_id);
				int server_features = packet_data.getInt();
				Set<ServerFeatures> features = Utils
						.scanTCPServerFeatures(server_features);
				_network_manager.receivedIDChangeFromServer(clientID, features);
				return ;
			}
			case PACKET_SRVSTATUS: {
				int user_count = packet_data.getInt();
				int file_count = packet_data.getInt();
				_network_manager.receivedServerStatus(user_count, file_count);
				return ;
			}
	
			case OP_SERVERLIST: {
				int server_count = packet_data.get();
				List<String> ip_list = new LinkedList<String>();
				List<Integer> port_list = new LinkedList<Integer>();
				for (int i = 0; i < server_count; i++) {
					byte address[] = new byte[4];
					int port;
	
					packet_data.get(address);
					port = Convert.shortToInt(packet_data.getShort());
					ip_list.add(Convert.IPtoString(address));
					port_list.add(port);
				}
	
				_network_manager.receivedServerList(ip_list, port_list);
				return ;
			}
	
			case PACKET_SRVSEARCHRESULT: {
				int result_count = packet_data.getInt();
				SearchResultItemList searchResults = new SearchResultItemList();
				for (int i = 0; i < result_count; i++) {
					byte fileHash[] = new byte[16];
					packet_data.get(fileHash);
	
					byte clientID[] = new byte[4];
					packet_data.get(clientID);
	
					short clientPort = packet_data.getShort();
	
					SearchResultItem result = new SearchResultItem(new FileHash(
							fileHash), new ClientID(clientID), clientPort);
					int tag_count = packet_data.getInt();
	
					for (int j = 0; j < tag_count; j++) {
						Tag tag = TagScanner.scanTag(packet_data);
						result.addTag(tag);
					}
					// transform Server's file rating into eMule file rating
					if (result.hasTag(FT_FILERATING)) {
						Tag tag = result.getTag(FT_FILERATING);
						try {
							int data = (Integer) tag.getValue();
							data = Convert.byteToInt(Misc.getByte(data, 0));
							int rating_value = data / SERVER_SEARCH_RATIO;
							tag.setValue(rating_value);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
					searchResults.add(result);
	
				}
				_network_manager.receivedSearchResult(searchResults);
				return ;
			}
	
			case PACKET_SRVFOUNDSOURCES: {
				byte[] file_hash = new byte[16];
				packet_data.get(file_hash);
	
				int source_count = Convert.byteToInt(packet_data.get());
				List<String> client_ip_list = new LinkedList<String>();
				List<Integer> port_list = new LinkedList<Integer>();
	
				byte[] peerID = new byte[4];
				int peerPort;
				ByteBuffer data = Misc.getByteBuffer(4);
				for (int i = 0; i < source_count; i++) {
					for (int j = 0; j < 4; j++) {
						data.clear();
						data.rewind();
						byte b = packet_data.get();
						data.put(b);
						peerID[j] = Convert.intToByte(data.getInt(0));
					}
	
					byte[] portArray = new byte[2];
					packet_data.get(portArray);
	
					ByteBuffer tmpData = Misc.getByteBuffer(4);
					tmpData.put(portArray);
					tmpData.position(0);
					peerPort = tmpData.getInt();
	
					ClientID cid = new ClientID(peerID);
					// if (PeerManagerFactory.getInstance().hasPeer(cid)) continue;
					// if (PeerManagerFactory.getInstance().isFull()) continue;
					client_ip_list.add(cid.getAsString());
					port_list.add(peerPort);
				}
				_network_manager.receivedSourcesFromServer(new FileHash(file_hash),
						client_ip_list, port_list);
				return ;
			}
	
			case PACKET_CALLBACKREQUESTED: {
				byte[] ip_address = new byte[4];
				int port;
	
				packet_data.get(ip_address);
	
				port = Convert.shortToInt(packet_data.getShort());
	
				_network_manager.receivedCallBackRequest(Convert
						.IPtoString(ip_address), port);
	
				return ;
			}
	
			case PACKET_CALLBACKFAILED: {
				_network_manager.receivedCallBackFailed();
				return ;
			}
			default:
				throw new UnknownPacketException(packet_header, packet_opcode,
						packet_data.array());
			}
		}catch(Throwable cause) {
			if (cause instanceof UnknownPacketException)
				throw (UnknownPacketException) cause;
			throw new MalformattedPacketException(packet_data.array(), cause);
		}
	}

	public static void readPeerPacket(JMuleSocketChannel channel, int usePort)
			throws JMEndOfStreamException, IOException, DataFormatException,
			UnknownPacketException, MalformattedPacketException {
		ByteBuffer data2;

		channel.transferred_bytes = 0;
		
		data2 = Misc.getByteBuffer(1);
		channel.read(data2);
		byte packet_header = data2.get(0);
		InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();
		InternalPeerManager _peer_manager = (InternalPeerManager) PeerManagerSingleton.getInstance();
		
		int peerPort = channel.getChannel().socket().getPort();
		String peerIP = Convert.IPtoString(channel.getChannel().socket()
				.getInetAddress().getAddress());

		if (usePort != 0)
			peerPort = usePort;
		
		ByteBuffer packet_length = Misc.getByteBuffer(4);
		channel.read(packet_length);

		ByteBuffer packet_opcode_buffer = Misc.getByteBuffer(1);
		channel.read(packet_opcode_buffer);

		byte packet_opcode = packet_opcode_buffer.get(0);

		int pkLength = packet_length.getInt(0);
		
		if ((pkLength >= E2DKConstants.MAXPACKETSIZE) || (pkLength <= 0))
			throw new MalformattedPacketException("Invalid packet length "
					+ " Header :" + Convert.byteToHex(packet_header)
					+ " Packet opcode : " + packet_opcode + " Length : "
					+ pkLength);
		
		ByteBuffer packet_data = Misc.getByteBuffer(pkLength - 1);
		channel.read(packet_data);
		packet_data.position(0);

		if (packet_header == PROTO_EMULE_COMPRESSED_TCP) {
			packet_data = JMuleZLib.decompressData(packet_data);
			packet_data.position(0);
			packet_header = PROTO_EDONKEY_TCP;
		}
		// System.out.println("Peer packet from : " + peerIP + " : " + peerPort +" Header : " + Convert.byteToHex(packet_header) + " opcode : " + Convert.byteToHex(packet_opcode)); 
		try {
			if (packet_header == PROTO_EDONKEY_TCP) 
				switch (packet_opcode) {
				case OP_PEERHELLO: {
					byte[] data = new byte[16];
					packet_data.get(); // skip user hash's length
					packet_data.get(data);
					UserHash userHash = new UserHash(data);
	
					byte client_id[] = new byte[4];
					packet_data.get(client_id);
					ClientID clientID = new ClientID(client_id);
	
					int tcpPort = Convert.shortToInt(packet_data.getShort());
	
					TagList tag_list = readTagList(packet_data);
	
					byte[] server_ip_array = new byte[4];
					packet_data.get(server_ip_array);
					String server_ip = Convert.IPtoString(server_ip_array);
					int server_port;
					server_port = Convert.shortToInt(packet_data.getShort());
	
					_network_manager.receivedHelloFromPeerAndRespondTo(peerIP,
							peerPort, userHash, clientID, tcpPort, tag_list,
							server_ip, server_port);

					break ;
				}
	
				case OP_PEERHELLOANSWER: {
					byte[] data = new byte[16];
					packet_data.get(data);
					UserHash userHash = new UserHash(data);
	
					byte client_id[] = new byte[4];
					packet_data.get(client_id);
					ClientID clientID = new ClientID(client_id);
	
					int tcpPort = Convert.shortToInt(packet_data.getShort());
	
					TagList tag_list = readTagList(packet_data);
	
					byte[] server_ip_array = new byte[4];
					packet_data.get(server_ip_array);
					String server_ip = Convert.IPtoString(server_ip_array);
					int server_port;
					server_port = Convert.shortToInt(packet_data.getShort());
	
					_network_manager.receivedHelloAnswerFromPeer(peerIP, peerPort,
							userHash, clientID, tcpPort, tag_list, server_ip,
							server_port);

					break;
				}
	
				case OP_SENDINGPART: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					long chunk_start = Convert.intToLong(packet_data.getInt());
					long chunk_end = Convert.intToLong(packet_data.getInt());
					ByteBuffer chunk_content = Misc.getByteBuffer(chunk_end
							- chunk_start);
					packet_data.get(chunk_content.array());
					_network_manager.receivedRequestedFileChunkFromPeer(peerIP,
							peerPort, new FileHash(file_hash), new FileChunk(
									chunk_start, chunk_end, chunk_content));

					break;
				}
	
				case OP_REQUESTPARTS: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					long[] startPos = new long[3];
					long[] endPos = new long[3];
					List<FileChunkRequest> chunks = new LinkedList<FileChunkRequest>();
					for (int i = 0; i < 3; i++)
						startPos[i] = Convert.intToLong(packet_data.getInt());
	
					for (int i = 0; i < 3; i++)
						endPos[i] = Convert.intToLong(packet_data.getInt());
	
					for (int i = 0; i < 3; i++) {
						if ((startPos[i] == endPos[i]) && (startPos[i] == 0))
							break;
						chunks.add(new FileChunkRequest(startPos[i], endPos[i]));
					}
	
					_network_manager.receivedFileChunkRequestFromPeer(peerIP,
							peerPort, new FileHash(file_hash), chunks);
					

					break;
				}
	
				case OP_END_OF_DOWNLOAD: {
					_network_manager
							.receivedEndOfDownloadFromPeer(peerIP, peerPort);
					break;
				}
	
				case OP_HASHSETREQUEST: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
	
					_network_manager.receivedHashSetRequestFromPeer(peerIP,
							peerPort, new FileHash(file_hash));
					break;
				}
	
				case OP_HASHSETANSWER: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					int partCount = Convert.shortToInt(packet_data.getShort());
					PartHashSet partSet = new PartHashSet(new FileHash(file_hash));
					byte[] partHash = new byte[16];
	
					for (short i = 1; i <= partCount; i++) {
						packet_data.get(partHash);
						partSet.add(partHash);
					}
					_network_manager.receivedHashSetResponseFromPeer(peerIP,
							peerPort, partSet);
					break;
				}
	
				case OP_SLOTREQUEST: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					_network_manager.receivedSlotRequestFromPeer(peerIP, peerPort,
							new FileHash(file_hash));
					break;
				}
	
				case OP_SLOTGIVEN: {
					_network_manager.receivedSlotGivenFromPeer(peerIP, peerPort);
					break;
				}
	
				case OP_SLOTRELEASE: {
					break;
				}
	
				case OP_SLOTTAKEN: {
					_network_manager.receivedSlotTakenFromPeer(peerIP, peerPort);
					break;
				}
	
				case OP_FILEREQUEST: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
	
					_network_manager.receivedFileRequestFromPeer(peerIP, peerPort,
							new FileHash(file_hash));
					break;
				}
	
				case OP_FILEREQANSWER: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					int name_length = Convert.shortToInt(packet_data.getShort());
					ByteBuffer str_bytes = Misc.getByteBuffer(name_length);
					packet_data.get(str_bytes.array());
					_network_manager.receivedFileRequestAnswerFromPeer(peerIP,
							peerPort, new FileHash(file_hash), new String(str_bytes
									.array()));
					break;
				}
	
				case OP_FILEREQANSNOFILE: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
	
					_network_manager.receivedFileNotFoundFromPeer(peerIP, peerPort,
							new FileHash(file_hash));
					break;
				}
	
				case OP_FILESTATREQ: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
	
					_network_manager.receivedFileStatusRequestFromPeer(peerIP,
							peerPort, new FileHash(file_hash));
	
					break;
				}
	
				case OP_FILESTATUS: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
					short partCount = packet_data.getShort();
					int count = (partCount + 7) / 8;
				//	if (((partCount + 7) / 8) != 0)
				//	if (count == 0)
					//	count++;
	
					byte[] data = new byte[count];
					for (int i = 0; i < count; i++)
						data[i] = packet_data.get();
	
					JMuleBitSet bitSet;
					bitSet = Convert.byteToBitset(data);
					bitSet.setPartCount(partCount);
	
					_network_manager.receivedFileStatusResponseFromPeer(peerIP,
							peerPort, new FileHash(file_hash), bitSet);
					break;
				}
				
				case OP_MESSAGE: {
					int message_length = Convert.shortToInt(packet_data.getShort());
					ByteBuffer message_bytes = Misc.getByteBuffer(message_length);
					packet_data.get(message_bytes.array());
					String message = new String(message_bytes.array());
					message_bytes.clear();
					message_bytes = null;
					_peer_manager.receivedMessage(peerIP, peerPort, message);
					break;
				}
	
				default: {
					throw new UnknownPacketException(packet_header, packet_opcode,
							packet_data.array());
				}
			}
			else 
				if (packet_header == PROTO_EMULE_EXTENDED_TCP)
				switch (packet_opcode) {
				case OP_EMULE_HELLO: {
					byte client_version = packet_data.get();
					byte protocol_version = packet_data.get();
					TagList tag_list = readTagList(packet_data);
					_network_manager.receivedEMuleHelloFromPeer(peerIP, peerPort,
							client_version, protocol_version, tag_list);
					break;
				}
				
				case OP_EMULEHELLOANSWER : {
					byte client_version = packet_data.get();
					byte protocol_version = packet_data.get();
					TagList tag_list = readTagList(packet_data);
					_network_manager.receivedEMuleHelloAnswerFromPeer(peerIP, peerPort,
							client_version, protocol_version, tag_list);
					break;
				}
	
				case OP_COMPRESSEDPART: {
					byte[] file_hash = new byte[16];
					packet_data.get(file_hash);
	
					long chunkStart = Convert.intToLong(packet_data.getInt());
					long chunkEnd = Convert.intToLong(packet_data.getInt());
					long compressedSize = packet_data.capacity()
							- packet_data.position();
					ByteBuffer data = Misc.getByteBuffer(compressedSize);
					packet_data.get(data.array());
	
					_network_manager.receivedCompressedFileChunkFromPeer(peerIP,
							peerPort, new FileHash(file_hash), new FileChunk(
									chunkStart, chunkEnd, data));
					break;
				}
	
				case OP_EMULE_QUEUERANKING: {
					short queue_rank = packet_data.getShort();
					_network_manager.receivedQueueRankFromPeer(peerIP, peerPort,
							Convert.shortToInt(queue_rank));
					break;
				}
				
				case OP_REQUESTSOURCES : {
					byte[] hash = new byte[16];
					packet_data.get(hash);
					_network_manager.receivedSourcesRequestFromPeer(peerIP, peerPort, new FileHash(hash));
					break;
				}
				
				case OP_ANSWERSOURCES : {
					byte[] hash = new byte[16];
					packet_data.get(hash);
					int source_count = Convert.shortToInt(packet_data.getShort());
					List<String> ip_list = new ArrayList<String>();
					List<Integer> port_list = new ArrayList<Integer>();
					byte[] ip = new byte[4];
					short port;
					for(int k = 0;k<source_count;k++) {
						packet_data.get(ip);
						port = packet_data.getShort();
						ip = Convert.reverseArray(ip);
						
						ip_list.add(Convert.IPtoString(ip));
						port_list.add(Convert.shortToInt(port));
					}
					_network_manager.receivedSourcesAnswerFromPeer(peerIP, peerPort,new FileHash(hash), ip_list, port_list);
					break;
				}
				
				case OP_CHATCAPTCHAREQ : {
					packet_data.get();
					
					ByteBuffer image_data = Misc.getByteBuffer(pkLength - 2);
					image_data.position(0);
					packet_data.get(image_data.array());
					image_data.position(0);
					_peer_manager.receivedCaptchaImage(peerIP, peerPort, image_data);
					break;
				}
				
				case OP_CHATCAPTCHARES : {
					byte response = packet_data.get();
					_peer_manager.receivedCaptchaStatusAnswer(peerIP, peerPort, response);
					break;
				}
				
	
				default: {
					throw new UnknownPacketException(packet_header, packet_opcode,
							packet_data.array());
				}
			}
		} catch (Throwable cause) {
			if (cause instanceof UnknownPacketException)
				throw (UnknownPacketException) cause;
			throw new MalformattedPacketException(packet_header, (packet_opcode),
					packet_data.array(), cause);
		}
		if ((packet_opcode == OP_SENDINGPART) || (packet_opcode == OP_COMPRESSEDPART)) {
				channel.file_transfer_trafic.addReceivedBytes(channel.transferred_bytes);
			} else {
				channel.service_trafic.addReceivedBytes(channel.transferred_bytes);
			}
		packet_data = null;
	}

	private static ByteBuffer packetBuffer = Misc
			.getByteBuffer(ConfigurationManager.MAX_UDP_PACKET_SIZE);

	private enum PacketType {
		SERVER_UDP, PEER_UDP, KAD, KAD_COMPRESSED
	};

	public static void readUDPPacket(DatagramChannel channel)
			throws NetworkManagerException, UnknownPacketException, MalformattedPacketException {
		
		InetSocketAddress packetSender;
		boolean kad_enabled = false;
		PacketType type;
		ByteBuffer packet_content;
		try {
			kad_enabled = ConfigurationManagerSingleton.getInstance()
					.isJKadAutoconnectEnabled();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
		try {
			packetBuffer.clear();
			packetSender = (InetSocketAddress) channel.receive(packetBuffer);
			packet_content = getByteBuffer(packetBuffer.position());
			
			packetBuffer.limit(packetBuffer.position());
			packetBuffer.position(0);
			packet_content.position(0);
			packet_content.put(packetBuffer);

			packet_content.position(0);

			if (packet_content.get(0) == PROTO_EDONKEY_SERVER_UDP)
				type = PacketType.SERVER_UDP;
			else if (packet_content.get(0) == PROTO_EDONKEY_PEER_UDP)
				type = PacketType.PEER_UDP;
			else if (packet_content.get(0) == JKadConstants.PROTO_KAD_UDP) {
				if (!kad_enabled)
					return;
				type = PacketType.KAD;
			} else if (packet_content.get(0) == JKadConstants.PROTO_KAD_COMPRESSED_UDP) {
				if (!kad_enabled)
					return;
				type = PacketType.KAD_COMPRESSED;
			} else
				throw new NetworkManagerException(
						"Unknown UDP packet header : "
								+ Convert.byteToHex(packet_content.get(0)));
		} catch (Throwable cause) {
			throw new NetworkManagerException(Misc.getStackTrace(cause));
		}

		InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton
				.getInstance();

		if ((type == PacketType.KAD) || (type == PacketType.KAD_COMPRESSED)) {
			_network_manager.receiveKadPacket(new KadPacket(packet_content,
					packetSender));
			return;
		}

		byte packet_protocol = packet_content.get(0);
		
		byte packet_op_code = packet_content.get(1);

		packet_content.position(1 + 1);

		String ip = packetSender.getAddress().getHostAddress();
		int port = packetSender.getPort();
		
		// System.out.println("UDP Packet  " + ip + ":" + port + " Protocol : " + Convert.byteToHex(packet_protocol)+"  OpCode : " + Convert.byteToHex(packet_op_code));
		try {
			switch (packet_protocol) {
				case PROTO_EDONKEY_SERVER_UDP: {
					switch (packet_op_code) {
					case OP_GLOBSERVSTATUS: {
						if (packet_content.capacity()<15) {
							int challenge = packet_content.getInt();
							long user_count = Convert.intToLong(packet_content.getInt());
							long files_count = Convert.intToLong(packet_content.getInt());
							_network_manager.receivedOldServerStatus(ip, port, challenge, user_count, files_count);
							return ;
						}
						int challenge = packet_content.getInt();
						long user_count = Convert.intToLong(packet_content.getInt());
						long files_count = Convert.intToLong(packet_content.getInt());
						long soft_limit = Convert.intToLong(packet_content.getInt());
						long hard_limit = Convert.intToLong(packet_content.getInt());
						int udp_flags = packet_content.getInt();
						Set<ServerFeatures> server_features = Utils
								.scanUDPFeatures(udp_flags);
						_network_manager.receivedServerStatus(ip, port, challenge,
								user_count, files_count, soft_limit, hard_limit,
								server_features);
						return ;
					}
					case OP_SERVER_DESC_ANSWER: {
						boolean new_packet = false;
						short test_short = packet_content.getShort();
						test_short = packet_content.getShort();
						byte[] test_read = Convert.shortToByte(test_short);
						if (test_read[0] == (byte) 0xFF)
							if (test_read[1] == (byte) 0xFF) { 
								new_packet = true;
							}
						packet_content.position(packet_content.position() - 4);
						if (new_packet) {
							int challenge = packet_content.getInt();
							TagList tag_list = readTagList(packet_content);
							_network_manager.receivedNewServerDescription(ip, port,
									challenge, tag_list);
							return ;
						}
						String server_name = readString(packet_content);
						String server_desc = readString(packet_content);
						_network_manager.receivedServerDescription(ip, port,
								server_name, server_desc);
						return ;
					}
		
					default: {
						throw new UnknownPacketException(packet_protocol,
								packet_op_code, packet_content.array());
					}
					}
				}	
			} }catch(Throwable cause ) {
				if (cause instanceof UnknownPacketException)
					throw (UnknownPacketException)cause;
				throw new MalformattedPacketException(packet_content.array(), cause);
			}
	}

	private static TagList readTagList(ByteBuffer packet) {
		int tag_count = packet.getInt();
		TagList tag_list = new TagList();
		for (int i = 0; i < tag_count; i++) {
			Tag Tag = TagScanner.scanTag(packet);
			tag_list.addTag(Tag);
		}
		return tag_list;
	}

	public static String readString(ByteBuffer packet) {
		int message_length = (packet.getShort());
		ByteBuffer bytes = Misc.getByteBuffer(message_length);
		bytes.position(0);
		for(int i = 0;i<message_length;i++)
			bytes.put(packet.get());
		return new String(bytes.array());
	}

}
