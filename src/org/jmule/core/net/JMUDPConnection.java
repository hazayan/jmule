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
import java.util.LinkedList;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPDescSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPStatusSP;
import org.jmule.util.Average;

/**
 * 
 * @author binary256
 * @deprecated
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:46 $$
 */
public class JMUDPConnection {
	
	public static final int UDP_SOCKET_OPENED = 0x01;
	public static final int UDP_SOCKET_CLOSED = 0x00;
	
	private static JMUDPConnection udpListener = null;
	private int listenPort;
	private DatagramChannel listenChannel;
	private UDPListenThread udpListenThread;
	
	private Average wrongPacketCount = new Average(10);
	private int currentlyWrongPackets = 0;
	private WrongPacketCheckingThread wrongPacketChekingThread;
	private PacketProcessorThread packetProcessorThread = new PacketProcessorThread();
	
	private LinkedList sendQueue = new LinkedList();
	private PacketSenderThread packetSenderThread = new PacketSenderThread();
	
	private LinkedList receiveQueue;
	private int connectionStatus = UDP_SOCKET_CLOSED;
	
	public static JMUDPConnection getInstance(){
		if (udpListener == null)
			 udpListener = new JMUDPConnection();
		return udpListener;
	}
	
	private JMUDPConnection(){
		receiveQueue = new LinkedList();
	}
	
	private void processPacket(ScannedPacket packet) {
		if (packet instanceof JMServerUDPDescSP ){
			JMServerUDPDescSP sp = (JMServerUDPDescSP) packet;
//			try {
//				Server server ;
	//			server = ServerList.getInstance().get(sp.getSender());
//				server.addReceivedPacket(packet);
//			} catch (ServerListException e) {
//				e.printStackTrace();
//			}
			
			return ;	
			}
					
		if (packet instanceof JMServerUDPStatusSP) {
			JMServerUDPStatusSP sp = (JMServerUDPStatusSP) packet;
//			try {
				
//				Server server;
			//	server = ServerList.getInstance().get(sp.getSender());
//				server.addReceivedPacket(packet);
//			} catch (ServerListException e) {
//					e.printStackTrace();
//			}
		}
	}
	
	public void open() {
		try {
		listenPort = ConfigurationManagerFactory.getInstance().getUDP();
		listenChannel = DatagramChannel.open();
		listenChannel.socket().bind(new InetSocketAddress(listenPort));
		listenChannel.configureBlocking(true);
		udpListenThread = new UDPListenThread();
		udpListenThread.start();
		wrongPacketChekingThread = new WrongPacketCheckingThread();
		wrongPacketChekingThread.start();
		
		connectionStatus = UDP_SOCKET_OPENED;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isOpen() {
		return connectionStatus ==  UDP_SOCKET_OPENED;
	}
	
	public void close() {		
		try {
			if (wrongPacketChekingThread!=null) {
				wrongPacketChekingThread.stop();
			}
			if (udpListenThread!=null)
				udpListenThread.stop();
			if (packetSenderThread!=null)
				packetSenderThread.stop();
			listenChannel.close();
			connectionStatus = UDP_SOCKET_CLOSED;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reopen() {
		if (isOpen())
			close();
		open();
	}
	
	/** Packet listener **/
	private class UDPListenThread extends JMThread {
		public UDPListenThread() {
			super("UDP Listen Thread");
		}
		
		public void run() {
			while(true){
				UDPPacket packet = PacketReader.readPacket(listenChannel);
				if (packet!=null)
					addReceivedPacket(PacketScanner.scanPacket(packet));
				else {
					if (!listenChannel.isConnected()) break;
					currentlyWrongPackets++;
				}
			}
		}
	}
	
	private void addReceivedPacket(ScannedPacket packet ){
		receiveQueue.addLast(packet);
		if (!packetProcessorThread.isAlive())
			packetProcessorThread = new PacketProcessorThread();
	}
	
	private class PacketProcessorThread extends JMThread {
		public PacketProcessorThread() {
			super("UDP Packet processor");
			start();
		}
		
		public void run() {
			if (!isOpen()) return;
			while(receiveQueue.size()!=0) {
				ScannedPacket packet = (ScannedPacket)receiveQueue.removeFirst();
				processPacket(packet);
			}
		}
	}
	
	
	/** Send UDP packet code **/

	
	public void sendPacket(UDPPacket packet ) {
		sendQueue.addLast(packet);
		if (!packetSenderThread.isAlive())
			packetSenderThread = new PacketSenderThread();
	}
	
	private void ban() {
		
	}
	
	private class PacketSenderThread extends JMThread {
		public PacketSenderThread() {
			super("UDP packet sender thread");
			start();
		}
		
		public void run() {
			while(sendQueue.size()>0) {
				UDPPacket packet ;
				packet = (UDPPacket) sendQueue.getFirst();
				sendQueue.removeFirst();
				InetSocketAddress destination = packet.getAddress();
				try {
					packet.getAsByteBuffer().position(0);
					listenChannel.send(packet.getAsByteBuffer(), destination);
				} catch (IOException e) {
					e.printStackTrace();
					if (!listenChannel.isConnected()) break;
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private class WrongPacketCheckingThread extends JMThread {
		public WrongPacketCheckingThread()  {
			super("Wrong packet checking thread");
		}
		public void run() {
			while(true) {
				try {
					this.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return ;
				}
				wrongPacketCount.add(currentlyWrongPackets);
				currentlyWrongPackets = 0;
				if (wrongPacketCount.getAverage()>=50) {
					ban();
				}
			}
		}
	}


	
}
