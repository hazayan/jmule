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
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedUDPPacket;
import org.jmule.util.Average;

import static org.jmule.core.net.JMUDPConnection.UDPSocketStatus.*;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/05/09 16:38:09 $$
 */
public class JMUDPConnection {
	
	public enum UDPSocketStatus { OPEN, CLOSED};
	
	private JMuleCore _core;
	
	private static JMUDPConnection singleton = null;
	
	private DatagramChannel listenChannel;
	private UDPListenThread udpListenThread;
	
	private Average wrongPacketCount = new Average(10);
	private int currentlyWrongPackets = 0;
	private WrongPacketCheckingThread wrongPacketChekingThread;
		
	private ConcurrentLinkedQueue<UDPPacket> sendQueue = new ConcurrentLinkedQueue<UDPPacket>();
	private PacketSenderThread packetSenderThread;
	
	private ConcurrentLinkedQueue<ScannedUDPPacket> receiveQueue = new ConcurrentLinkedQueue<ScannedUDPPacket>();
	
	private PacketProcessorThread packetProcessorThread = new PacketProcessorThread();
	
	private UDPSocketStatus connectionStatus = CLOSED;
	
	public static JMUDPConnection getInstance(){
		if (singleton == null)
			 singleton = new JMUDPConnection();
		return singleton;
	}
	
	private JMUDPConnection(){
		_core = JMuleCoreFactory.getSingleton();
		
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
			public void UDPPortChanged(int udp) {
				try {
					reopen();
				} catch (JMUDPConnectionException e) {
					e.printStackTrace();
				}
			}
			
			public void isUDPEnabledChanged(boolean enabled) {
				try {
					if (enabled)
						reopen();
					else
						close();
				} catch (JMUDPConnectionException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void open() throws JMUDPConnectionException {
		try {
			int listenPort = ConfigurationManagerFactory.getInstance().getUDP();
			listenChannel = DatagramChannel.open();
			listenChannel.socket().bind(new InetSocketAddress(listenPort));
			listenChannel.configureBlocking(true);
			
			udpListenThread = new UDPListenThread();
			udpListenThread.start();
			
			wrongPacketChekingThread = new WrongPacketCheckingThread();
			wrongPacketChekingThread.start();
			
			packetProcessorThread = new PacketProcessorThread();
			packetProcessorThread.start();
			
			packetSenderThread = new PacketSenderThread();
			packetSenderThread.start();
			
			connectionStatus = OPEN;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new JMUDPConnectionException(t);
		}
	}
	
	public boolean isOpen() {
		return connectionStatus ==  OPEN;
	}
	
	public void close() throws JMUDPConnectionException {		
		if (!isOpen()) return;
		try {
			connectionStatus = CLOSED;
			wrongPacketChekingThread.JMStop();
			udpListenThread.JMStop();
			packetSenderThread.JMStop();
			packetProcessorThread.JMStop();
			listenChannel.close();
		} catch (IOException e) {
			throw new JMUDPConnectionException(e);
		}
	}
	
	public void reopen() throws JMUDPConnectionException {
		if (isOpen())
			close();
		open();
	}

	private void addReceivedPacket(ScannedUDPPacket packet ){
		receiveQueue.offer(packet);
		if (packetProcessorThread.isSleeping()) 
			packetProcessorThread.wakeUp();
			
	}
	
	// Send UDP packet code 
	public void sendPacket(UDPPacket packet ) {
		if (!isOpen()) return ;
		sendQueue.offer(packet);
		if (packetSenderThread.isSleeping())
			packetSenderThread.wakeUp();
			
	}
	
	private void ban() {
	}
	
	/** Packet listener **/
	private class UDPListenThread extends JMThread {
		
		private volatile boolean stop = false;
		
		public UDPListenThread() {
			super("UDP packets listener");
		}
		
		public void run() {
			while(!stop){
				UDPPacket packet = PacketReader.readPacket(listenChannel);
				if (packet != null) {
					ScannedUDPPacket scanned_packet = PacketScanner.scanPacket(packet);
					// scanned_packet == null is is not supported or decoding was failed
					if (scanned_packet != null)
						addReceivedPacket(scanned_packet);
					else
						currentlyWrongPackets++;
				}
				else {
					if (stop) return ;
					if (!listenChannel.isConnected()) break;
					currentlyWrongPackets++;
				}
			}
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
	}
	
	
	
	private class PacketProcessorThread extends JMThread {
		private boolean stop = false;
		private boolean sleeping = false;
		private ServerManager server_manager = JMuleCoreFactory.getSingleton().getServerManager();
		public PacketProcessorThread() {
			super("UDP Packet processor");
		}
		
		public void run() {
			while(!stop) {
				if (receiveQueue.size()==0) {
					// no more packets, sleeping...
					sleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e1) {} 
					}
					sleeping = false;
					continue ;
				}
				ScannedUDPPacket packet = receiveQueue.poll();
				// call server 
				Server server = server_manager.getServer(packet.getSenderAddress());
				if (server == null) continue ; // Packet from unknown source
				server.processPacket(packet);
			}
		}
		
		public void JMStop() {
			stop = true;
			synchronized (this) {
				notify();
			}
		}
		
		public boolean isSleeping() {
			return sleeping;
		}
		
		public void wakeUp() {
			synchronized (this) {
				notify();
			}
		}
	}
	

	private class PacketSenderThread extends JMThread {
		private boolean stop = false;
		private boolean sleeping = false;
		
		public PacketSenderThread() {
			super("UDP packet sender thread");
		}
		
		public void run() {
			while(!stop) {
				if (sendQueue.size() == 0) {
					// no more packets, sleeping...
					sleeping = true;
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e1) {} 
					}
					sleeping = false;
					continue ;
				}
				UDPPacket packet = sendQueue.poll();
				InetSocketAddress destination = packet.getAddress();
				try {
					packet.getAsByteBuffer().position(0);
					listenChannel.send(packet.getAsByteBuffer(), destination);
				} catch (IOException e) {
					if (stop) return;
					if (!listenChannel.isConnected()) return;
				}
			}
		}
		
		public void JMStop() {
			stop = true;
			synchronized (this) {
				notify();
			}
		}
		
		public boolean isSleeping() {
			return sleeping;
		}
		
		public void wakeUp() {
			synchronized (this) {
				notify();
			}
		}
		
	}
	
	private class WrongPacketCheckingThread extends JMThread {
		
		private volatile boolean stop = false;
		
		public WrongPacketCheckingThread()  {
			super("UDP Wrong packet checking thread");
		}
		
		public void run() {
			while(!stop) {
				try {
					Thread.sleep( ConfigurationManager.WRONG_PACKET_CHECK_INTERVAL );
				} catch (InterruptedException e) {
					if (stop)
						return ;
					else continue;
				}
				wrongPacketCount.add(currentlyWrongPackets);
				currentlyWrongPackets = 0;
				if ( wrongPacketCount.getAverage()>= ConfigurationManager.MAX_WRONG_PACKET_COUNT ) 
					ban();
				
			}
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
	}


	
}
