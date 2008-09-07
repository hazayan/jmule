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
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.edonkey.packet.UDPPacket;

/**
 * For testing only !
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 14:56:17 $$
 */
public class JMUDPConnection2 {
	
	private DatagramChannel datagram_channel;
	
	private ConcurrentLinkedQueue<UDPPacket> sendQueue = new ConcurrentLinkedQueue<UDPPacket>();
	
	private ConcurrentLinkedQueue<UDPPacket> receiveQueue = new ConcurrentLinkedQueue<UDPPacket>();
	
	private JMThread udp_incoming_thread;
	
	private JMThread udp_outcoming_thread;
	
	public JMUDPConnection2(InetSocketAddress udp_address) throws IOException {
		
		datagram_channel = DatagramChannel.open();
		
		//InetSocketAddress server_udp_address = new InetSocketAddress(server.getAddress(),SERVER_UDP_PORT);
		
		datagram_channel.connect(udp_address);
		
		//datagram_channel.socket().bind(udp_address);
		
		datagram_channel.configureBlocking(true);
	}

	public void sendPacket(UDPPacket packet) {
		
		sendQueue.offer(packet);
		synchronized(udp_outcoming_thread) {
				udp_outcoming_thread.notify();
		}
	}
	
	public void startUDPIncomingThread() {
		
		udp_incoming_thread = new udp_incoming_thread();
		
		udp_incoming_thread.start();
		
	}
	
	public void stopUDPIncomingThread() {
		
		udp_incoming_thread.JMStop();
		
	}
	
	public void startUDPOutcomingThread() {
		
		udp_outcoming_thread = new udp_outcoming_thread();
		
		udp_outcoming_thread.start();
		
	}
	
	public void stopUDPOutcomingThread() {
		
		udp_outcoming_thread.JMStop();
		
	}
	
	public boolean hasIncomingPackets() {
		
		return !receiveQueue.isEmpty();
		
	}
	
	public UDPPacket getNextIncomingPacket() {
		
		return receiveQueue.poll();
		
	}
	
	private class udp_incoming_thread extends JMThread {
		
		private boolean stop = false;
		
		public udp_incoming_thread() {
			
			super("UDP incoming thread");
			
		}
		
		public void run() {
		
			while(!stop) {
				
				UDPPacket packet = PacketReader.readPacket(datagram_channel);
				
				if (packet!=null) {
					
					receiveQueue.offer(packet);
					
				}
			}
			
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
		
	}
	

	private class udp_outcoming_thread extends JMThread {
		
		private boolean stop = false;
		
		public udp_outcoming_thread() {
			
			super("UDP outcomming thread");
			
		}
		
		public void run() {
		
			while(!stop) {
				if (sendQueue.size()==0) {
						synchronized(this) {
							try {
								this.wait();
							} catch (InterruptedException e1) {
							} 
						}
				}
				if (stop) break;
				UDPPacket packet = sendQueue.poll();
				InetSocketAddress destination = packet.getAddress();
				
				packet.getAsByteBuffer().position(0);
				try {
					datagram_channel.send(packet.getAsByteBuffer(), destination);
				} catch (IOException e) {
					
				}
				
			}
			
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
		
	}
	
	
}
