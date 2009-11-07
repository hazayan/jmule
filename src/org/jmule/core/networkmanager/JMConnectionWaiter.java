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

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/07 11:58:34 $$
 */
public class JMConnectionWaiter{

	public enum WaiterStatus { OPEN, CLOSED };
	private ConnectionListenerThread connectionListenerThread = null;
	private ServerSocketChannel listenSocket = null;
	private WaiterStatus status;
	private JMuleCore _core;
	
	JMConnectionWaiter() {
		_core = JMuleCoreFactory.getSingleton();
			
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
					socket = new JMuleSocketChannel(listenSocket.accept());
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
