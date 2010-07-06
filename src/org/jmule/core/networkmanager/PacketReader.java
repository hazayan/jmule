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

import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_PEER_UDP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_SERVER_UDP;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Set;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.edonkey.E2DKConstants.ServerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.ipfilter.InternalIPFilter;
import org.jmule.core.ipfilter.IPFilter.BannedReason;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.26 $
 * Last changed by $Author: binary255 $ on $Date: 2010/07/06 09:00:55 $
 */
public class PacketReader {


	private static ByteBuffer packetBuffer = Misc.getByteBuffer(ConfigurationManager.MAX_UDP_PACKET_SIZE);

	private enum PacketType {
		SERVER_UDP, PEER_UDP, KAD, KAD_COMPRESSED
	};

	public static void readUDPPacket(DatagramChannel channel)
			throws NetworkManagerException, UnknownPacketException,
			MalformattedPacketException {

		InetSocketAddress packetSender;
		boolean kad_enabled = false;
		PacketType type;
		ByteBuffer packet_content;
		try {
			kad_enabled = ConfigurationManagerSingleton.getInstance().isJKadAutoconnectEnabled();
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
				throw new NetworkManagerException("Unknown UDP packet header : " + Convert.byteToHex(packet_content.get(0)));
		} catch (Throwable cause) {
			throw new NetworkManagerException(Misc.getStackTrace(cause));
		}
		InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		InternalIPFilter _ipfilter = (InternalIPFilter) IPFilterSingleton.getInstance();		
		if ((type == PacketType.KAD) || (type == PacketType.KAD_COMPRESSED)) {
			_network_manager.receiveKadPacket(new KadPacket(packet_content,packetSender));
			return;
		}
		byte packet_protocol = packet_content.get(0);
		byte packet_op_code = packet_content.get(1);
		packet_content.position(1 + 1);
		String ip = packetSender.getAddress().getHostAddress();
		int port = packetSender.getPort();
		if (_ipfilter.isServerBanned(ip)) {
			return;
		}
		
		// System.out.println("UDP Packet  " + ip + ":" + port + " Protocol : "
		// + Convert.byteToHex(packet_protocol)+"  OpCode : " +
		// Convert.byteToHex(packet_op_code));
		try {
			switch (packet_protocol) {
			case PROTO_EDONKEY_SERVER_UDP: {
				switch (packet_op_code) {
				case OP_GLOBSERVSTATUS: {
					if (packet_content.capacity() < 15) {
						int challenge = packet_content.getInt();
						long user_count = Convert.intToLong(packet_content.getInt());
						long files_count = Convert.intToLong(packet_content.getInt());
						_network_manager.receivedOldServerStatus(ip, port,challenge, user_count, files_count);
						return;
					}
					int challenge = packet_content.getInt();
					long user_count = Convert.intToLong(packet_content.getInt());
					long files_count = Convert.intToLong(packet_content.getInt());
					long soft_limit = Convert.intToLong(packet_content.getInt());
					long hard_limit = Convert.intToLong(packet_content.getInt());
					int udp_flags = packet_content.getInt();
					Set<ServerFeatures> server_features = Utils.scanUDPFeatures(udp_flags);
					_network_manager.receivedServerStatus(ip, port, challenge,user_count, files_count, soft_limit, hard_limit,server_features);
					return;
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
						return;
					}
					String server_name = readString(packet_content);
					String server_desc = readString(packet_content);
					_network_manager.receivedServerDescription(ip, port,
							server_name, server_desc);
					return;
				}

				default: {
					throw new UnknownPacketException(packet_protocol,
							packet_op_code, packet_content.array());
				}
				}
			}
			}
		} catch (Throwable cause) {
			if (cause instanceof UnknownPacketException)
				throw (UnknownPacketException) cause;
			_ipfilter.addServer(ip, BannedReason.BAD_PACKETS, _network_manager);
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
		for (int i = 0; i < message_length; i++)
			bytes.put(packet.get());
		return new String(bytes.array());
	}

}
