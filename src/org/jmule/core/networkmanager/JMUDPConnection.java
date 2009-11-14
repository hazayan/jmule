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
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.management.JMException;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerSingleton;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.jkad.IPAddress;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/11/14 09:35:43 $$
 */
public class JMUDPConnection {
	
	public enum UDPConnectionStatus { OPEN, CLOSED};
	
	private JMuleCore _core;
	
	private DatagramChannel listenChannel;
	private UDPListenThread udpListenThread;
	private UDPConnectionStatus connectionStatus = UDPConnectionStatus.CLOSED;
	
	private Queue<UDPPacket> sendQueue = new ConcurrentLinkedQueue<UDPPacket>();
	private UDPSenderThread sender_thread;
	
	JMUDPConnection(){
		_core = JMuleCoreFactory.getSingleton();
		
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
			public void UDPPortChanged(int udp) {
					reOpenConnection();
			
			}
			
			public void isUDPEnabledChanged(boolean enabled) {
					if (enabled)
						reOpenConnection();
					else
						close();
			}
		});
	}
	
	public void open() {
		try {
			int listenPort = ConfigurationManagerSingleton.getInstance().getUDP();
			listenChannel = DatagramChannel.open();
			listenChannel.socket().bind(new InetSocketAddress(listenPort));
			listenChannel.configureBlocking(true);
			
			udpListenThread = new UDPListenThread();
			udpListenThread.start();
			
			sender_thread = new UDPSenderThread();
			sender_thread.start();
			
			connectionStatus = UDPConnectionStatus.OPEN;
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public boolean isOpen() {
		return connectionStatus == UDPConnectionStatus.OPEN;
	}
	
	public void close() {		
		if (!isOpen()) return;
		try {
			connectionStatus = UDPConnectionStatus.CLOSED;
			listenChannel.close();
			udpListenThread.JMStop();
			sender_thread.JMStop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reOpenConnection() {
		if (isOpen())
			close();
		open();
	}
	
	public void sendPacket(UDPPacket packet ) throws JMException {
		if (!isOpen())
			throw new JMException("UDP socket is not open");
		sendQueue.offer(packet);
		if (sender_thread.isSleeping())
			sender_thread.wakeUp();
	}
	
	public void sendPacket(UDPPacket packet, IPAddress address, int port) throws JMException {
		InetSocketAddress ip_address = new InetSocketAddress(address.toString(), port);
		sendPacket(packet, ip_address);
	}
	
	public void sendPacket(UDPPacket packet, InetSocketAddress destination) throws JMException {
		packet.setAddress(destination);
		sendPacket(packet);
	}
	
	public void sendPacket(UDPPacket packet, String address, int port) throws JMException {
		InetSocketAddress ip_address = new InetSocketAddress(address, port);
		sendPacket(packet, ip_address);
	}
	
	private class UDPSenderThread extends JMThread {
		private boolean is_sleeping = false;
		private boolean loop = true;
		public UDPSenderThread() {
			super("UDP Sender thread");
		}
		
		public void run() {
			while(loop) {
				if (!isOpen()) return ;
				is_sleeping = false;
				if (sendQueue.isEmpty()) {
					is_sleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
						}
					}
					continue;
				}
				while(!sendQueue.isEmpty()) {
					if (!isOpen()) return ;
					UDPPacket packet = sendQueue.poll();
					InetSocketAddress destination = packet.getAddress();
					packet.getAsByteBuffer().position(0);
					try {
						listenChannel.send(packet.getAsByteBuffer(), destination);
					} catch (IOException cause) {
						if (!isOpen()) return ;
						cause.printStackTrace();
					}
				}
			}
			
		}
		
		public boolean isSleeping() {
			return is_sleeping;
		}
		
		public void wakeUp() {
			synchronized(this) {
				this.notify();
			}
		}
		
		public void JMStop() {
			loop = false;
			this.interrupt();
		}
		
		
		
	}
	
	private class UDPListenThread extends JMThread {
		
		private volatile boolean stop = false;
		
		public UDPListenThread() {
			super("UDP packets listener");
		}
		
		public void run() {
			while(!stop){
				try {
					PacketReader.readUDPPacket(listenChannel);
				}catch (Throwable cause) {
					if (connectionStatus == UDPConnectionStatus.CLOSED) return ;			
					cause.printStackTrace();
					
				}
				
			}
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
	}

}
