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
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.util.Average;
import org.jmule.util.Misc;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 05:38:28 $$
 */
public abstract class JMConnection{
	
	public static final int TCP_SOCKET_DISCONNECTED = 0x00;
	
	public static final int TCP_SOCKET_CONNECTING = 0x01;
	
	public static final int TCP_SOCKET_CONNECTED = 0x02;
		
	private int connectionStatus = TCP_SOCKET_DISCONNECTED;
	
	protected Queue<Packet> incoming_packet_queue = new ConcurrentLinkedQueue<Packet>();
	
	protected Queue<Packet> outgoing_packet_queue = new ConcurrentLinkedQueue<Packet>();
	
	private ConnectingThread connectingThread = null;
	
	protected JMuleSocketChannel remoteConnection = null;
	
	private PacketReceiverThread packetReceiverThread = null;
	
	private PacketSenderThread packetSenderThread = null;
	
	private InetSocketAddress remoteAddress;
	
	private ConnectionStats connectionStats = new ConnectionStats();
	
	private Average wrongPacketCount = new Average(10);
	
	private int currentlyWrongPackets = 0;
	
	private WrongPacketCheckingThread wrongPacketChekingThread;
	
	private  boolean allowStopReciver = true;
	
	protected Packet lastPacket;
	
	public JMConnection() {
	}
	
	public JMConnection(JMuleSocketChannel useConnection) {
		
		remoteConnection = useConnection;
		
		remoteAddress = (InetSocketAddress) useConnection.getSocket().getRemoteSocketAddress();
		
		connectionStatus = TCP_SOCKET_CONNECTED;
		
		connectionStats.stopSpeedCounter();
		connectionStats.startSpeedCounter();
		
	}
	
	public JMConnection(String IPAddress,int port) {
		
		remoteAddress = new InetSocketAddress(IPAddress, port);
		
	}
	
	protected void open() {
		
		startReceiver();
		
		startSender();
		
	}

	public void connect() {
		
		String desc = null;
		
		if (desc!=null) {
			
			return;
			//this.disconnect();
		} else
			
			startConnecting();
		
	}
		
	public void disconnect() {
		
		if ((remoteConnection!=null)&&(remoteConnection.isOpen())){
				
			try {
				remoteConnection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
				//remoteConnection.close();

			
			if (wrongPacketChekingThread!=null)
				
				wrongPacketChekingThread.JMStop();
			
			connectionStats.stopSpeedCounter();
			
			}
		
		stopConnecting();
		
		if (allowStopReciver)
			
			stopReceiver();
		
		
		stopSender();
		
		setStatus(TCP_SOCKET_DISCONNECTED);
		
		onDisconnect();
		
	}

	public int getStatus() {
		
		return connectionStatus;
		
	}
	
	private void setStatus(int newStatus){
		
		this.connectionStatus = newStatus;
		
	}
	
	
	public String getAddress() {
		
		if (this.remoteAddress.getAddress()==null)
			
			return this.remoteAddress.getHostName();
		
		return this.remoteAddress.getAddress().getHostAddress();
		
	//	return Convert.IPtoString(this.remonteAddress.getAddress().getAddress());
	}

	public int getPort() {
		
		return this.remoteAddress.getPort();
		
	}

	public InetSocketAddress getInetAddress() {
		
		return this.remoteAddress;
		
	}
	
	protected void setAddress(String address,int port){
		
		this.remoteAddress = new InetSocketAddress(address, port);
		
	}
	
	protected void setAddress(InetSocketAddress remoteAddress) {
		
		this.remoteAddress = remoteAddress;
		
	}
	
	public String toString() {
		
		String result = getAddress()+" : "+getPort()+" ";
		
		if (getStatus() == TCP_SOCKET_DISCONNECTED)
			
			result += "TCP DISCONNECTED";
		
		if (getStatus() == TCP_SOCKET_CONNECTING)
			
			result += "TCP CONNECTING";

		if (getStatus() == TCP_SOCKET_CONNECTED)
			
			result += "TCP CONNECTED";

		result += " ";
		
		return result;
	}
	
	public byte[] getRemoteIPAddress() {
		
		if (remoteConnection.getSocket() == null)
			
			return new byte[] { 0, 0, 0, 0 };
		
		byte[] iremoteIP = new byte[4];
		
		iremoteIP = remoteAddress.getAddress().getAddress();
		
		return iremoteIP;
		
	}

	private void startConnecting() {
		
		if ((connectingThread != null) && (connectingThread.isAlive())) {
			
			return;
			
		}
		
		this.connectingThread = new ConnectingThread();
		
	}

	private void stopConnecting() {
		
		try {
			
		if ((connectingThread != null) && (connectingThread.isAlive()))
			
			connectingThread.JMStop();
		
		}catch(Exception e){}

	}

	private void startReceiver() {
		
		if ((packetReceiverThread != null) && (packetReceiverThread.isAlive()));
		
		else {
			
			allowStopReciver = true;
			
			packetReceiverThread = new PacketReceiverThread();
			
		}
	}

	private void stopReceiver() {
		
		if ((packetReceiverThread != null) && (packetReceiverThread.isAlive()))
			
			packetReceiverThread.JMStop();
		
		else;
	}

	private void startSender() {
		
		if ((packetSenderThread == null) || (!packetSenderThread.isAlive()))
			
			packetSenderThread = new PacketSenderThread();
		
	}

	private void stopSender() {
		
		if ((packetSenderThread != null) && (packetSenderThread.isAlive()))
			
			this.packetSenderThread.JMStop();
		
		else ;
		
	}

	
	public void sendPacket(Packet packet) {
		
		lastPacket = packet;
		
		addSendPacket(packet);
		
		startSender();
		
	}

	private void  addSendPacket(Packet packet) {
		
		this.outgoing_packet_queue.offer(packet);
		
	}

	private Packet getSendPacket() {
		
		return (Packet) this.outgoing_packet_queue.poll();
	}

	protected synchronized void addReceivedPacket(Packet packet) {
		
		if (packet==null){ return ; }
		
		incoming_packet_queue.offer(packet);
		
		processPackets();
		
	}

	protected Packet getReceivedPacket(){
		
	    return incoming_packet_queue.poll();
	    
	}
	
	protected int getPacketCount() {
		
		return incoming_packet_queue.size();
		
	}

	
	protected void setRemoteConnection(SocketChannel pRemoteConnection) {
		
		if (this.getStatus() != TCP_SOCKET_CONNECTED) {
			
			stopConnecting();
			
			stopReceiver();
			
			stopSender();
			
			disconnect();
			
		}
		
		remoteConnection=new JMuleSocketChannel(pRemoteConnection);
		
		setAddress(this.remoteConnection.getSocket().getInetAddress().getHostAddress(),this.remoteConnection.getSocket().getPort());
		
		startReceiver();
		
		startSender();
		
	}

	protected abstract void processPackets();

	protected abstract void onDisconnect();
	
	protected abstract void onConnect();
	
	protected abstract void reportInactivity(long time);
	
	public float getDownloadSpeed() {
		
		return connectionStats.getDownloadSpeed();
		
	}
	
	public float getUploadSpeed() {
		
		return connectionStats.getUploadSpeed();
		
	}
	
	public int getActivity() {
		
		return connectionStats.getActivity();
		
	}
	
	public void addInactivityTime(long time) {
		
		connectionStats.addInactivityTime(time);
		
		reportInactivity(connectionStats.getInactivityTime());
		
	}
	
	public void ban() {
		
		disconnect();
		
	}
	
	// Threads 
		
	private class PacketSenderThread extends JMThread {
		
		private boolean stop = false;
		
		public PacketSenderThread() {
			
			super(getAddress()+" : "+ getPort()+" : Packet sender thread");
			
			start();
			
		}
		public void run() {
			
			ByteBuffer packet;
			
			while (!stop) {
				
				if (outgoing_packet_queue.isEmpty())
						return ;
				
				connectionStats.reportActivity();
				
				Packet EDPacket = getSendPacket();
				
				packet = EDPacket.getAsByteBuffer();
				
				packet.position(0);
				
				long bSneded = 0;
				
				try {
					
					if (packet!=null) {
						
						bSneded = remoteConnection.write(packet);
						connectionStats.addSendBytes(bSneded);
						
						connectionStats.reportActivity();
						
					}
										
				} catch (Throwable e1) {
					
					e1.printStackTrace();
					
					if (stop) return ;
					
					if (!remoteConnection.isOpen())
						
						disconnect();
					
				}

			}
		}
		
		public void JMStop() {
			
			stop = true;
			
			interrupt();
			
		}
		
	}
	
	

	private class PacketReceiverThread extends JMThread {
		
		private boolean stop = false;
		
		public PacketReceiverThread() {
			
			super(getAddress()+" : "+ getPort()+" Packet reciver thread");
			
			start();
		}
		
		public void run() {
			
			ByteBuffer packetHeader = Misc.getByteBuffer(1);
			
			Packet packet;//Incoming Packet
						
			while (!stop) {

				packetHeader.clear();
				
				packetHeader.position(0);
						
				try {
					
					remoteConnection.read(packetHeader,true);
					
				}catch(Throwable e) {
					if (e instanceof JMEndOfStreamException) {
						
						try {
							
							Thread.sleep(1000);
							
						} catch (InterruptedException e1) {

							e1.printStackTrace();
							
						}
						continue;
					}
					
					e.printStackTrace();
					
					if (stop) return;
					
					if (connectionStatus!=TCP_SOCKET_DISCONNECTED) {
						
						allowStopReciver = false;
						
						disconnect();
						
					}
					disconnect();
					return ;
				}
				
									
				if (!PacketReader.checkPacket(packetHeader)) {
					
					//If have wrong file packet
					
					currentlyWrongPackets++;
					
					continue;
					
				}
					
				packet = PacketReader.getPacketByHeader(packetHeader);
				
				try {
					
					packet.readPacket(remoteConnection);
					
				} catch (Throwable e1) {
					if (e1 instanceof JMFloodException) { 
						ban();
					}
					e1.printStackTrace();
					if (stop) return ;
					
				}
				
				connectionStats.addReceivedBytes(packet.getPacket().length);
								
				try {

					addReceivedPacket(packet);
					
					connectionStats.reportActivity();
					
				} catch (Throwable e) {
					
					e.printStackTrace();
				} 
			}
		}
		
		public void JMStop() {
			
			stop = true;
			
			interrupt();
			
		}
	}	
	
	private class ConnectingThread extends JMThread {
		
		private boolean stop = false;
		
		public ConnectingThread() {
			
			super(getAddress()+" : "+ getPort()+" Connecting thread");
			
			start();
			
		}
		
		public void run() {
			
			connectionStatus = TCP_SOCKET_CONNECTING;
				
			try {
				
				wrongPacketChekingThread = new WrongPacketCheckingThread();
				
				wrongPacketChekingThread.start();
				
				SocketChannel channel = SocketChannel.open(new InetSocketAddress(getAddress(),getPort()));
				channel.socket().setSoTimeout(2000);
				remoteConnection=new JMuleSocketChannel(channel);
				
				remoteConnection.configureBlocking(true);
				
				connectionStatus = TCP_SOCKET_CONNECTED;
				
				startReceiver();
				
				startSender();
				
				connectionStats.setStartTime(System.currentTimeMillis());
				
				connectionStats.startSpeedCounter();
				
				onConnect();
				

				
			} catch (Exception e) {

				e.printStackTrace();
				
				if (stop) return ;
				
				connectionStatus = TCP_SOCKET_DISCONNECTED;
				
				onDisconnect();
				
				return ;
				
			}	
		}
		
		public void JMStop() {
			
			stop = true;
			
			interrupt();
			
		}
		
	}
	
	
	private class WrongPacketCheckingThread extends JMThread {
		
		private boolean stop = false;
		
		public WrongPacketCheckingThread()  {
			
			super("Wrong packet checker ");
			
		}
		
		public void run() {
			
			while (!stop) {
				
				try {
					
					this.join(ConfigurationManager.WRONG_PACKET_CHECK_INTERVAL);
					
				} catch (InterruptedException e) {
					
					if (stop) return ;
					
				}
				
				wrongPacketCount.add(currentlyWrongPackets);
				
				currentlyWrongPackets = 0;
				
				if ( wrongPacketCount.getAverage() >= ConfigurationManager.MAX_WRONG_PACKET_COUNT ) {
					
					ban();
				}
			}
		}
		
		public void JMStop() {
		
			stop = true;
			
			interrupt();
			
		}
		
	}
}
