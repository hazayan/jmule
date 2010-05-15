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

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.21 $
 * Last changed by $Author: binary255 $ on $Date: 2010/05/15 15:40:37 $
 */
public final class JMPeerConnection extends JMConnection {

	private JMuleSocketChannel jm_socket_channel;	
	int usePort = 0;
	InetSocketAddress remote_inet_socket_address;
	private ConnectionStatus connection_status = ConnectionStatus.DISCONNECTED;	
	private long uploadedFileBytes = 0;
	private int ioErrors = 0;
	
	JMPeerConnection(InetSocketAddress remoteInetSocketAddress) {
		remote_inet_socket_address = remoteInetSocketAddress;
		connection_status = ConnectionStatus.DISCONNECTED;
	}
	
	JMPeerConnection(JMuleSocketChannel peerConnection) throws NetworkManagerException {
		jm_socket_channel = peerConnection;
		remote_inet_socket_address = (InetSocketAddress) jm_socket_channel.getSocket().getRemoteSocketAddress();
		
		connection_status = ConnectionStatus.CONNECTED;
	}
		
	JMPeerConnection(String ipAddress, int port) {
		remote_inet_socket_address = new InetSocketAddress(ipAddress, port);
	}
	
	InetSocketAddress getRemoteAddress() {
		return remote_inet_socket_address;
	}
	
	String getIPAddress() {
		return remote_inet_socket_address.getAddress().getHostAddress();
	}
	
	void setJMConnection(JMuleSocketChannel socketChannel) {
		jm_socket_channel = socketChannel;
		
	}
	
	JMuleSocketChannel getJMConnection() {
		return jm_socket_channel;
	}
	
	int getPort() {
		return remote_inet_socket_address.getPort();
	}
	
	int getUsePort() {
		if (usePort!=0)
			return usePort;
		return remote_inet_socket_address.getPort();
	}
	
	ConnectionStatus getStatus() {
		return connection_status;
	}
	
	void setStatus(ConnectionStatus newStatus) {
		this.connection_status = newStatus;
	}
	
	void connect() throws IOException {
		jm_socket_channel.connect(remote_inet_socket_address);
	}
	
	void disconnect() throws NetworkManagerException {
		if (connection_status == ConnectionStatus.CONNECTED) {
			try {
				jm_socket_channel.close();
				connection_status = ConnectionStatus.DISCONNECTED;
			} catch (IOException cause) {
				throw new NetworkManagerException(cause);
			}
		}
	}
	
	long getUploadedFileBytes() {
		return uploadedFileBytes;
	}
	
	void addUploadedBytes(long bytes) {
		uploadedFileBytes += bytes;
	}
	
	void resetUploadedFileBytes() {
		uploadedFileBytes = 0;
	}
	
	public String toString() {
		return "Peer connection to : " +  remote_inet_socket_address + " Use port : " + usePort + " Status : " + connection_status;
	}
	
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}

	int getIoErrors() {
		return ioErrors;
	}

	void setIoErrors(int ioErrors) {
		this.ioErrors = ioErrors;
	}
		
}
