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
package org.jmule.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.impl.Peer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:29:40 $$
 */
public class JMConnectionWaiter{
	
	public static final int TCP_LISTENER_OPENED = 0x01;
	
	public static final int TCP_LISTENER_CLOSED = 0x00;
	
	private static JMConnectionWaiter singleton = null;
	
	private ConnectionListenerThread connectionListenerThread = null;
	
	private ServerSocketChannel listenSocket = null;
	
	private int status = TCP_LISTENER_CLOSED;
	
	private JMuleCore _core;
	
	public static JMConnectionWaiter getInstance() {
		
		if (singleton == null)
			
			singleton = new JMConnectionWaiter();
		
		return singleton;
		
	}
	
	private JMConnectionWaiter() {
			
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
		
		// Start waiter
		
		try {
			
			listenSocket = ServerSocketChannel.open();
			
			listenSocket.socket().bind(new InetSocketAddress( ConfigurationManagerFactory.getInstance().getTCP() ));
			
			connectionListenerThread = new ConnectionListenerThread();
			
			status = TCP_LISTENER_OPENED;
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stop() {
		
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
		
		status = TCP_LISTENER_CLOSED;
		
	}
	
	//TODO : Add code to throw exception on restarting stopped listener
	
	public void restart() {
		
		if (status==TCP_LISTENER_OPENED) {
			
			stop();
		
			start();
		
		}
		
	}
	
	public boolean isOpen() {
		
		return status == TCP_LISTENER_OPENED;
		
	}
	
	private void addNewPeer(JMuleSocketChannel channel) {
		
		new Peer(channel,_core.getServerManager().getConnectedServer());//Create new peer
		
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
			
			while (!stop) {
				
				try {
					
					JMuleSocketChannel newConnection = new JMuleSocketChannel(listenSocket.accept());
					
					addNewPeer(newConnection);
					
				} catch (IOException e) {
					
					if (!listenSocket.isOpen()) return ;
					
					e.printStackTrace();
				}
			}
		}

	}

}
