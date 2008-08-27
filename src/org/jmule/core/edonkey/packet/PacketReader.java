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
package org.jmule.core.edonkey.packet;

import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_PEER_UDP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_SERVER_UDP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_TCP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_COMPRESSED_TCP;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EMULE_EXTENDED_TCP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.packet.impl.EMuleCompressedPacket;
import org.jmule.core.edonkey.packet.impl.EMuleExtendedTCPPacket;
import org.jmule.core.edonkey.packet.impl.StandardPacket;
import org.jmule.core.edonkey.packet.impl.UDPPeerPacket;
import org.jmule.core.edonkey.packet.impl.UDPServerPacket;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 05:48:20 $$
 */
public class PacketReader {

	 public static Packet getPacketByHeader(ByteBuffer header){
	    	Packet packet = null;
	    	if (header.get(0) == PROTO_EDONKEY_TCP) 
				packet = new StandardPacket();

			if (header.get(0) == PROTO_EMULE_COMPRESSED_TCP) 
				packet = new EMuleCompressedPacket();

			if (header.get(0) == PROTO_EMULE_EXTENDED_TCP) {
				packet  = new EMuleExtendedTCPPacket();
			}
			
			return packet;
	    }
	 
	 public static boolean checkPacket(ByteBuffer packet) {
			if (packet.position() == 0) return false;
			if (packet.get(0) == PROTO_EDONKEY_TCP) return true;
			if (packet.get(0) == PROTO_EMULE_COMPRESSED_TCP) return true;
			if (packet.get(0) == PROTO_EMULE_EXTENDED_TCP) return true;
			return false;
		}

		public static ByteBuffer readBytes(JMuleSocketChannel channel,int length)throws IOException, JMEndOfStreamException, InterruptedException{
			ByteBuffer buffer = Misc.getByteBuffer(length);
			channel.read(buffer,true);
			return buffer;
		}
	
	public static UDPPacket readPacket(DatagramChannel channel) {
		InetSocketAddress packetSender;
		ByteBuffer data = Misc.getByteBuffer(ConfigurationManager.MAX_UDP_PACKET_SIZE); 
		
		try {
			packetSender = (InetSocketAddress) channel.receive(data);
			data.flip();
			
			if (data.get(0) == PROTO_EDONKEY_SERVER_UDP) 
				return new UDPServerPacket(data,packetSender);
			if (data.get(0) == PROTO_EDONKEY_PEER_UDP)
				return new UDPPeerPacket(data,packetSender);
		} catch (Throwable t) {
			return null;
		}
		
		return null;
	}
	
}
