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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.zip.DataFormatException;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2009/10/14 15:13:47 $
 */
public class JMPeerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;
	
	private JMThread connecting_thread;
	private JMThread receiver_thread;
	
	private InetSocketAddress remote_inet_socket_address;
		
	private InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	
	private ConnectionStatus connection_status = ConnectionStatus.DISCONNECTED;	
	
	JMPeerConnection(InetSocketAddress remoteInetSocketAddress) {
		remote_inet_socket_address = remoteInetSocketAddress;
	}
	
	JMPeerConnection(JMuleSocketChannel peerConnection) {
		jm_socket_channel = peerConnection;
		remote_inet_socket_address = (InetSocketAddress) jm_socket_channel.getSocket().getRemoteSocketAddress();
		_network_manager.addPeer(this);
		connection_status = ConnectionStatus.CONNECTED;
		createReceiverThread();
		receiver_thread.start();
	}
	
	JMPeerConnection(String ipAddress, int port) {
		remote_inet_socket_address = new InetSocketAddress(ipAddress, port);
	}
	
	void connect() {
		connecting_thread = new JMThread() {
			public void JMStop() {
			}
			public void run() {
				try {
					connection_status = ConnectionStatus.CONNECTING;
					jm_socket_channel = new JMuleSocketChannel(SocketChannel
							.open());
					jm_socket_channel.connect(remote_inet_socket_address,
							ConfigurationManager.PEER_CONNECTING_TIMEOUT);

					connection_status = ConnectionStatus.CONNECTED;

					_network_manager.peerConnected(remote_inet_socket_address
							.getAddress().getHostAddress(),
							remote_inet_socket_address.getPort());

					createReceiverThread();

					receiver_thread.start();

				} catch (IOException cause) {
					cause.printStackTrace();
					connection_status = ConnectionStatus.DISCONNECTED;
					_network_manager.peerConnectingFailed(
							remote_inet_socket_address.getAddress()
									.getHostAddress(),
							remote_inet_socket_address.getPort(), cause);
				}
			}

		};

		connecting_thread.start();
	}
	
	private void createReceiverThread() {
		receiver_thread = new JMThread() {
			private boolean stop_thread = false;

			public void JMStop() {
				stop_thread = true;
			}

			public void run() {
				while (!stop_thread) {
					try {
						PacketReader.readPeerPacket(jm_socket_channel);
					} catch (JMEndOfStreamException e) {
						notifyDisconnect();
					} catch (UnknownPacketException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	void disconnect() throws NetworkManagerException {
		if (connection_status == ConnectionStatus.CONNECTED) {
			receiver_thread.JMStop();
			try {
				jm_socket_channel.close();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
		}
		
		if (connection_status == ConnectionStatus.CONNECTING) {
			try {
				jm_socket_channel.close();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
		}
	}
	
	String getIPAddress() {
		return remote_inet_socket_address.getAddress().getHostAddress();
	}
	
	JMuleSocketChannel getJMConnection() {
		return jm_socket_channel;
	}
	
	int getPort() {
		return remote_inet_socket_address.getPort();
	}
	
	ConnectionStatus getStatus() {
		return connection_status;
	}
	
	private void notifyDisconnect() {
		int port = jm_socket_channel.getSocket().getPort();
		String peerIP = Convert.IPtoString(jm_socket_channel.getChannel().socket().getInetAddress().getAddress());
		_network_manager.peerDisconnected(peerIP, port);
		
		receiver_thread.JMStop();
	}
	
	
	void send(Packet packet) throws Exception {
		if (packet.getLength() >= E2DKConstants.PACKET_SIZE_TO_COMPRESS) {
			byte op_code = packet.getCommand(); 
			ByteBuffer raw_data = Misc.getByteBuffer(packet.getLength()-1-4-1);
			ByteBuffer data = packet.getAsByteBuffer();
			data.position(1 + 4 + 1);
			raw_data.put(data);
			raw_data.position(0);
			raw_data = JMuleZLib.compressData(raw_data);
			packet = new Packet(raw_data.capacity(), E2DKConstants.PROTO_EMULE_COMPRESSED_TCP);
			packet.setCommand(op_code);
			raw_data.position(0);
			packet.insertData(raw_data);
		}
		if(jm_socket_channel.write(packet.getAsByteBuffer()) == -1 )
				notifyDisconnect();
	}
	
	public String toString() {
		return "Peer connection to : " +  getIPAddress() + " : " + getPort();
	}
}
