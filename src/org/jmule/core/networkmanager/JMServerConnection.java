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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.DataFormatException;

import org.jmule.core.JMException;
import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.utils.JMuleZLib;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 20, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary255 $ on $Date: 2009/11/14 09:35:43 $
 */
public class JMServerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;
	
	private JMThread connecting_thread;
	private JMThread receiver_thread;
	
	private SenderThread sender_thread;
	private Queue<Packet> send_queue = new ConcurrentLinkedQueue<Packet>();
	
	private InetSocketAddress remote_inet_socket_address;
		
	private InternalNetworkManager _network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		
	private ConnectionStatus connection_status = ConnectionStatus.DISCONNECTED;	
	
	JMServerConnection(String ipAddress, int port) {
		remote_inet_socket_address = new InetSocketAddress(ipAddress, port);
	}
	
	JMServerConnection(InetSocketAddress remoteInetSocketAddress) {
		remote_inet_socket_address = remoteInetSocketAddress;
	}
	
	String getIPAddress() {
		return remote_inet_socket_address.getAddress().getHostAddress();
	}
	
	int getPort() {
		return remote_inet_socket_address.getPort();
	}
	
	ConnectionStatus getStatus() {
		return connection_status;
	}
	
	void send(Packet packet) throws JMException {
		if (connection_status != ConnectionStatus.CONNECTED)
			throw new JMException("Server connection not open to " + remote_inet_socket_address);
		send_queue.add(packet);
		if (sender_thread.isSleeping())
			sender_thread.wakeUp();
	}
	
	private void sendPacket(Packet packet) throws Exception {
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
	
	void connect() {
		connecting_thread = new JMThread() {
			public void run() {			
				try {
					connection_status = ConnectionStatus.CONNECTING;
					SocketChannel channel = SocketChannel.open();
					
					jm_socket_channel = new JMuleSocketChannel(channel);
					
					jm_socket_channel.connect(remote_inet_socket_address, ConfigurationManager.SERVER_CONNECTING_TIMEOUT);
					
					connection_status = ConnectionStatus.CONNECTED;
					send_queue.clear();
					sender_thread = new SenderThread();
					sender_thread.start();
					
					createReceiverThread();
					
					receiver_thread.start();
					
					_network_manager.serverConnected();
					
				} catch (Throwable cause) {
					cause.printStackTrace();
					connection_status = ConnectionStatus.DISCONNECTED;
					_network_manager.serverConnectingFailed(cause);
				}
				
			}
			
			public void JMStop() {
			}
			
		};
		
		connecting_thread.start();
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
	
	
	private void notifyDisconnect() {
		_network_manager.serverDisconnected();
		
		receiver_thread.JMStop();
		sender_thread.JMStop();
	}
	
	
	private void createReceiverThread() {
		receiver_thread = new JMThread() {
			 
			private boolean stop_thread = false;
			
			public void run() {
				while(!stop_thread) {
					try {
						PacketReader.readServerPacket(jm_socket_channel);
					} catch (JMEndOfStreamException e) {
						e.printStackTrace();
						notifyDisconnect();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (UnknownPacketException e) {
						e.printStackTrace();
					} catch (DataFormatException e) {
						e.printStackTrace();
					} catch (MalformattedPacketException e) {
						e.printStackTrace();
					}
				}
					
				
			}
			
			public void JMStop() {
				stop_thread = true;
			}
		};
	}
	
	private class SenderThread extends JMThread {
		private boolean loop = true;
		private boolean is_sleeping = false;
		public SenderThread() {
			super("Server packet sender to " + remote_inet_socket_address);
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
		
		public boolean isSleeping() {return is_sleeping; }
		public void wakeUp() {
			synchronized (this) {
				this.notify();
			}
		}
		public void JMStop() {
			loop = false;
			synchronized (this) {
				this.notify();
			}
		}
		
	}
	
}
