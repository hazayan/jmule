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
package org.jmule.core.networkmanager;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.ipfilter.IPFilterSingleton;
import org.jmule.core.ipfilter.InternalIPFilter;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/04/12 16:45:45 $$
 */
public class JMConnectionWaiter{

	public enum WaiterStatus { OPEN, CLOSED };
	private ConnectionListenerThread connectionListenerThread = null;
	private ServerSocketChannel listenSocket = null;
	private WaiterStatus status;
	private JMuleCore _core;
	private InternalIPFilter _ip_filter;
	
	JMConnectionWaiter() {
		_core = JMuleCoreFactory.getSingleton();
		_ip_filter = (InternalIPFilter) IPFilterSingleton.getInstance();
		
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
			public void TCPPortChanged(int port) {		
				restart();		
			}
		});
		
	}
	
	public void start() {
		if (connectionListenerThread != null)
			return;
		
		try {
			listenSocket = ServerSocketChannel.open();
			int tcp_port = 0;
			try {			
				tcp_port = ConfigurationManagerSingleton.getInstance().getTCP();
				listenSocket.socket().bind(new InetSocketAddress( tcp_port ));
			} catch (Throwable cause) {
				if (cause instanceof BindException) {
					System.out.println("Port " + tcp_port +" is already in use by other application");
				}
				cause.printStackTrace();
			}
			connectionListenerThread = new ConnectionListenerThread();
			status = WaiterStatus.OPEN;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		status = WaiterStatus.CLOSED;
		if (listenSocket != null){
		try {
			listenSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} }
		if (connectionListenerThread != null)
			connectionListenerThread.JMStop();
		else ;
		connectionListenerThread = null;
		
		
	}
	//TODO : Add code to throw exception on restarting stopped listener
	public void restart() {
		if (status == WaiterStatus.OPEN) {
			stop();
			start();
		}
	}
	
	public boolean isOpen() {
		return status == WaiterStatus.OPEN;
	}
	
	private class ConnectionListenerThread extends JMThread {
		private boolean stop = false;
	
		public ConnectionListenerThread() {
			super("Incoming TCP connections listener");
			start();
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
		
		public void run() {
			JMuleSocketChannel socket = null;
			while (!stop) {
				try {
					SocketChannel socket_channel = listenSocket.accept();
					InetSocketAddress address = (InetSocketAddress) socket_channel.socket().getRemoteSocketAddress();
					String ip_address = address.getAddress().getHostAddress();
					if (_ip_filter.isPeerBanned(ip_address)) {
						socket_channel.close();
						continue;
					}
					socket = new JMuleSocketChannel(socket_channel);
					new JMPeerConnection(socket);
				} catch (IOException e) {
					if (!listenSocket.isOpen()) return ;
				} catch (NetworkManagerException e) {
					e.printStackTrace();
					socket.disconnect();
				}
			}
		}

	}
}
