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
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMException;
import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.ipfilter.InternalIPFilter;
import org.jmule.core.ipfilter.IPFilter.BannedReason;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.18 $
 * Last changed by $Author: binary255 $ on $Date: 2010/02/06 08:16:10 $
 */
public final class JMPeerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;
	
	private JMThread connecting_thread;
	private JMThread receiver_thread;
	
	private volatile SenderThread sender_thread;
	private volatile Queue<Packet> send_queue = new ConcurrentLinkedQueue<Packet>();
	
	volatile int usePort = 0;
	
	private InetSocketAddress remote_inet_socket_address;
		
	private InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
	private InternalIPFilter _ip_filter = (InternalIPFilter) IPFilterSingleton.getInstance();
	private ConnectionStatus connection_status = ConnectionStatus.DISCONNECTED;	
	
	private long uploadedFileBytes = 0;
	
	JMPeerConnection(InetSocketAddress remoteInetSocketAddress) {
		remote_inet_socket_address = remoteInetSocketAddress;
	}
	
	JMPeerConnection(JMuleSocketChannel peerConnection) throws NetworkManagerException {
		jm_socket_channel = peerConnection;
		remote_inet_socket_address = (InetSocketAddress) jm_socket_channel.getSocket().getRemoteSocketAddress();
		_network_manager.addPeer(this);
		connection_status = ConnectionStatus.CONNECTED;
		send_queue.clear();
		createSenderThread();
		sender_thread.start();
		
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
					send_queue.clear();
					createSenderThread();
					sender_thread.start();
					
					createReceiverThread();
					receiver_thread.start();
					
					_network_manager.peerConnected(remote_inet_socket_address.getAddress().getHostAddress(),remote_inet_socket_address.getPort());

				} catch (IOException cause) {
					cause.printStackTrace();
					connection_status = ConnectionStatus.DISCONNECTED;
					int port = remote_inet_socket_address.getPort();
					if (usePort!=0)
						port = usePort;
					_network_manager.peerConnectingFailed(remote_inet_socket_address.getAddress().getHostAddress(),port, cause);
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

			private int io_errors = 0;
			
			public void run() {
				while (!stop_thread) {
					try {
						PacketReader.readPeerPacket(jm_socket_channel,usePort);
						io_errors = 0;
					} catch (Throwable cause) {
						if (cause instanceof JMEndOfStreamException)
							notifyDisconnect(); else
						if (cause instanceof AsynchronousCloseException)
							return ; // closed from another thread, just return don't notify
						if (cause instanceof IOException) {
							if (io_errors > ConfigurationManager.ERRORS_TO_DISCONNECT_PEER) {
								try {
									notifyDisconnect();
									disconnect();
								} catch (NetworkManagerException e) {
									e.printStackTrace();
								}
								return ;
							}
							io_errors++;
						}
						if (cause instanceof MalformattedPacketException) {
							_ip_filter.addPeer(getIPAddress(), BannedReason.BAD_PACKETS, _network_manager);
							try {
								notifyDisconnect();
								disconnect();
							} catch (NetworkManagerException e) {
								e.printStackTrace();
							}
						}
						else { 
							JMException exception = new JMException("Exception in connection " + remote_inet_socket_address+"\n"+Misc.getStackTrace(cause));
							exception.printStackTrace();
						}
					}
				}
			}
		};
	}
	
	private void createSenderThread() {
		sender_thread = new SenderThread();
	}

	void disconnect() throws NetworkManagerException {
		if (connection_status == ConnectionStatus.CONNECTED) {
			try {
				jm_socket_channel.close();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
			receiver_thread.JMStop();
			sender_thread.JMStop();
			send_queue.clear();
		}
		
		if (connection_status == ConnectionStatus.CONNECTING) {
			try {
				jm_socket_channel.close();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
		}
		jm_socket_channel = null;
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
		connection_status = ConnectionStatus.DISCONNECTED;
		int port = jm_socket_channel.getSocket().getPort();
		if (usePort!=0)
			port = usePort;
		String peerIP = Convert.IPtoString(jm_socket_channel.getChannel().socket().getInetAddress().getAddress());
		_network_manager.peerDisconnected(peerIP, port);
		
		receiver_thread.JMStop();
	}
	
	void send(Packet packet) throws JMException {
		if (connection_status != ConnectionStatus.CONNECTED)
			throw new JMException("Not connected to " + remote_inet_socket_address);
		send_queue.add(packet);
		if (sender_thread.isSleeping()) {
			sender_thread.wakeUp();
		}
	}
	
	private void sendPacket(Packet packet) throws Exception {
		try {
			if ((!E2DKConstants.PEER_PACKETS_NOT_ALLOWED_TO_COMPRESS.contains(
				packet.getCommand()))
				&& (packet.getLength() >= E2DKConstants.PACKET_SIZE_TO_COMPRESS)) {
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
//			System.out.println("Send packet to peer : " + getIPAddress() + " : " + getPort() +" Header : " + Convert.byteToHex(packet.getProtocol()) + " opcode : " + Convert.byteToHex(packet.getCommand())); 
			if(jm_socket_channel.write(packet.getAsByteBuffer()) == -1 )
					notifyDisconnect();
			if (packet.getCommand() == E2DKConstants.OP_SENDINGPART) {
				uploadedFileBytes += packet.getLength() - 16 + 4 + 4;
			}
		}catch(Throwable cause) {
			if (jm_socket_channel != null)
				if (!jm_socket_channel.isConnected())
					notifyDisconnect();
			throw new JMException("Exception in connection : " +remote_inet_socket_address + "\n"+Misc.getStackTrace(cause));
		}
	}
	
	long getUploadedFileBytes() {
		return uploadedFileBytes;
	}
	
	void resetUploadedFileBytes() {
		uploadedFileBytes = 0;
	}
	
	public String toString() {
		return "Peer connection to : " +  remote_inet_socket_address + " Status : " + connection_status;
	}
	
	private class SenderThread extends JMThread {
		
		private volatile boolean loop = true;
		private volatile boolean is_sleeping = false;
		public SenderThread() {
			super("Peer packet sender for " + remote_inet_socket_address);
		}
		
		public void run() {
			while(loop) {
				is_sleeping = false;
				if (!jm_socket_channel.isConnected()) break;
				if (send_queue.isEmpty()) {
					is_sleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
						}
					}
					continue;
				}
				Packet packet = send_queue.poll();
				try {
					sendPacket(packet);
				} catch (Exception e) {
					if (!jm_socket_channel.isConnected()) break;
				}
				
			}
		}
		public void JMStop() {
			loop = false;
			synchronized(this) {
				this.notify();
			}
		}
		public boolean isSleeping() { return is_sleeping; }
		public void wakeUp() {
			synchronized(this) {
				this.notify();
			}
		}
		
	}
	
}
