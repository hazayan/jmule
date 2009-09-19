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
import java.nio.channels.SocketChannel;
import java.util.zip.DataFormatException;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.packet.Packet;

/**
 * Created on Aug 20, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/19 18:01:50 $
 */
public class JMServerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;
	
	private JMThread connecting_thread;
	private JMThread receiver_thread;
	
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
	
	void send(Packet packet) throws Exception {
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
	
	
	private void notifyDisconnect() {
		_network_manager.serverDisconnected();
		
		receiver_thread.JMStop();
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DataFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
				
			}
			
			public void JMStop() {
				stop_thread = true;
			}
		};
	}
	
}
