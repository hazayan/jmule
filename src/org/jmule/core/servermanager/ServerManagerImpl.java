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
package org.jmule.core.servermanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.ED2KServerLink;
import org.jmule.core.edonkey.E2DKConstants.ServerFeatures;
import org.jmule.core.edonkey.metfile.ServerMet;
import org.jmule.core.edonkey.metfile.ServerMetException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerException;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.servermanager.Server.ServerStatus;
import org.jmule.core.sharingmanager.InternalSharingManager;
import org.jmule.core.sharingmanager.SharingManagerSingleton;
import org.jmule.core.utils.Convert;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/19 14:21:53 $$
 */
public class ServerManagerImpl extends JMuleAbstractManager implements InternalServerManager  {
	
	private List<Server> server_list = new LinkedList<Server>();
	private Server connected_server = null;
	
	private InternalNetworkManager _network_manager;
	private InternalSharingManager _sharing_manager;
	
	private ServerMet server_met;
	
	private List<ServerManagerListener> listener_list = new LinkedList<ServerManagerListener>();
	
	
	private boolean auto_connect_started = false;
	private List<Server> candidate_servers = new ArrayList<Server>();
	
	private Comparator<Server> compare_servers_by_ping = new Comparator<Server>() {
		public int compare(Server object1, Server object2) {
			if (object1.getPing() < object2.getPing()) {
				return -1;
			} else if (object1.getPing() > object2.getPing()) {
				return +1;
			}
			return 0;
		}
	};

	ServerManagerImpl() {
	}
	
	public void addServerListListener(ServerManagerListener serverListListener) {
		listener_list.add(serverListListener);
	}

	public void clearServerList() {
		server_list.clear();
		notifyServerListCleared();
	}

	public void connect() throws ServerManagerException {
		auto_connect_started = true;
		candidate_servers.addAll(server_list);
		
		requestNextAutoConnectServer();
		
		notifyAutoConnectStarted();
	}

	public void connect(Server server) throws ServerManagerException {
		if (connected_server != null)
			throw new ServerManagerException("JMule is already connected (connecting) to another server");
		String ip = server.getAddress();
		int port = server.getPort();
		if (hasServer(ip, port)) {
			connected_server = server;
			connected_server.setStatus(ServerStatus.CONNECTING);
			try {
				_network_manager.connectToServer(ip, port);
			} catch (NetworkManagerException e) {
				throw new ServerManagerException(e);
			}
			notifyIsConnecting(connected_server);
		}
			
	}
	
	public void disconnect() throws ServerManagerException {
		if (connected_server == null)
			throw new ServerManagerException("JMule is not connected to server ");
		try {
			_network_manager.disconnectFromServer();
		} catch (NetworkManagerException e) {
			throw new ServerManagerException(e);
		}
		notifyDisconnected(connected_server);
		connected_server = null;
	}
	
	public Server getConnectedServer() {
		return connected_server;
	}

	private Server getServer(String ip, int port) {
		for(Server server : server_list)
			if (server.getAddress().equals(ip))
				if (server.getPort() == port)
					return server;
		return null;
	}
	
	private int getServerID(String ip, int port) {
		for (int i = 0; i < server_list.size(); i++) {
			Server server = server_list.get(i);
			if (server.getAddress().equals(ip))
				if (server.getPort() == port) {
					return i;
				}
		}
		return -1;
	}
	
	public List<Server> getServers() {
		return server_list;
	}
	
	public int getServersCount() {
		return server_list.size();
	}
	
	public boolean hasServer(String address, int port) {
		for(Server server : server_list)
			if (server.getAddress().equals(address))
				if (server.getPort() == port)
					return true;
		return false;
	}

	
	protected boolean iAmStoppable() {
		return false;
	}
	
	public void importList(String fileName) throws ServerManagerException {
		ServerMet server_met;
		try {
			server_met = new ServerMet(fileName);
			server_met.load();
		} catch (ServerMetException cause) {
			throw new ServerManagerException(cause);
		}
			
		List<String> ip_list = server_met.getIPList();
		List<Integer> port_list = server_met.getPortList();
		List<TagList> tag_list = server_met.getTagList();
			
		for (int i = 0; i < ip_list.size(); i++) {
			try {
				Server server = newServer(ip_list.get(i), port_list.get(i));
				server.setTagList(tag_list.get(i));
			}catch(ServerManagerException cause) {
				cause.printStackTrace();
			}
		}
		server_met.close();
		
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
		
		_network_manager = (InternalNetworkManager) NetworkManagerSingleton.getInstance();
		_sharing_manager = (InternalSharingManager) SharingManagerSingleton.getInstance();
		 try {
			 server_met = new ServerMet(ConfigurationManager.SERVER_MET);
		 } catch (ServerMetException e) { }
	}

	public boolean isConnected() {
		return connected_server != null;
	}

	public void loadServerList() throws ServerManagerException {
        try {
        	server_met.load();
        	List<String> ip_list = server_met.getIPList();
    		List<Integer> port_list = server_met.getPortList();
    		List<TagList> tag_list = server_met.getTagList();
    			
    		for (int i = 0; i < ip_list.size(); i++) {
    			try {
    				Server server = newServer(ip_list.get(i), port_list.get(i));
    				server.setTagList(tag_list.get(i));
    			}catch(ServerManagerException cause) {
    				cause.printStackTrace();
    			}
    		}
        	} catch( Throwable cause ) {
        		throw new ServerManagerException(cause); 
        	}
	}

	public Server newServer(ED2KServerLink serverLink) throws ServerManagerException {
		String serverIP = serverLink.getServerAddress();
		int serverPort = serverLink.getServerPort();
		if (hasServer(serverIP, serverPort))
			throw new ServerManagerException("Server "+serverIP +" : " + serverPort+" already exists");
		Server server = new Server(serverLink);
		server_list.add(server);
		notifyServerAdded(server);
		return server;
	}

	public Server newServer(String serverIP, int serverPort) throws ServerManagerException {
		if (hasServer(serverIP, serverPort))
			throw new ServerManagerException("Server "+serverIP +" : " + serverPort+" already exists");
		Server server = new Server(serverIP, serverPort);
		server_list.add(server);
		notifyServerAdded(server);
		return server;
	}
	
	private void notifyAutoConnectStarted() {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.autoConnectStarted();
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}
	
	private void notifyAutoConnectFailed() {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.autoConnectFailed();
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyConnected(Server server) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.connected(server);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyDisconnected(Server server) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.disconnected(server);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyIsConnecting(Server server) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.isConnecting(server);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyMessage(Server server, String message) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.serverMessage(server, message);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerAdded(Server server) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.serverAdded(server);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerConnectingFailed(Server server, Throwable cause) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.serverConnectingFailed(server, cause);
			}catch(Throwable cause1) {
				cause1.printStackTrace();
			}
	}

	private void notifyServerListCleared() {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.serverListCleared();
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	private void notifyServerRemoved(Server server) {
		for(ServerManagerListener listener : listener_list) 
			try {
				listener.serverRemoved(server);
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
	}

	public void receivedIDChange(ClientID clientID,
			Set<ServerFeatures> serverFeatures) {
		if (connected_server == null) {
			// log, must newer get here
			return ;
		}
		connected_server.setClientID(clientID);
		connected_server.setFeatures(serverFeatures);
		
		connected_server.setStatus(ServerStatus.CONNECTED);
		
		if (auto_connect_started == true) {
			auto_connect_started = false;
			candidate_servers.clear();
		}
		
		_sharing_manager.startSharingFilesToServer();
		
		notifyConnected(connected_server);
			
	}
	
	public void receivedMessage(String message) {
		notifyMessage(connected_server, message);
	}

	public void receivedNewServerDescription(String ip, int port,
			TagList tagList) {
		Server server = getServer(ip, port);
		if (server == null) return;
		server.getTagList().addTag(tagList, true);
	}
	
	
	public void receivedServerDescription(String ip, int port, String name,
			String description) {
		Server server = getServer(ip, port);
		if (server == null) return ;
		server.setName(name);
		server.setDesc(description);
	}
	
	public void receivedServerList(List<String> ipList, List<Integer> portList) {
		for (int i = 0; i < ipList.size(); i++) {
			try {
				newServer(ipList.get(i), portList.get(i));
			} catch (ServerManagerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void receivedServerStatus(int userCount, int fileCount) {
		if (connected_server == null) return ;
		connected_server.setNumUsers(userCount);
		connected_server.setNumFiles(fileCount);
	}
	
	public void receivedServerStatus(String ip, int port, int challenge,
			long userCount, long fileCount, long softLimit, long hardLimit,
			Set<ServerFeatures> serverFeatures) {
		Server server = getServer(ip, port);
		if (server == null) return ;
		server.setNumUsers(userCount);
		server.setNumFiles(fileCount);
		server.setSoftLimit(Convert.longToInt(softLimit));
		server.setHardLimit(Convert.longToInt(hardLimit));
		server.setFeatures(serverFeatures);
	}
	
	public void removeServer(Server server) throws ServerManagerException {
		String ip = server.getAddress();
		int port = server.getPort();
		if (!hasServer(ip, port))
			throw new ServerManagerException("Server " + ip + " : " + port + " not found");
		int id = getServerID(ip, port);
		notifyServerRemoved(server_list.get(id));
		server_list.remove(id);
		
	}
	
	public void removeServerListListener(ServerManagerListener serverListListener) {
		listener_list.remove(serverListListener);
	}
	
	public void serverConnectingFailed(String ip, int port, Throwable cause) {
		Server failed_server = connected_server;
		connected_server = null;
		notifyServerConnectingFailed(failed_server, cause);
		if (auto_connect_started) {
			try {
				requestNextAutoConnectServer();
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public void serverDisconnected(String ip, int port) {
		connected_server.setStatus(ServerStatus.DISCONNECTED);
		notifyDisconnected(connected_server);
		_sharing_manager.stopSharingFilesToServer();
		connected_server = null;
		
		if (auto_connect_started) {
			try {
				requestNextAutoConnectServer();
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void storeServerList() throws ServerManagerException {
		List<String> ip_list = new LinkedList<String>();
		List<Integer> port_list = new LinkedList<Integer>();
		List<TagList> tag_list = new LinkedList<TagList>();
		
		for (int i = 0; i < server_list.size(); i++) {
			Server server = server_list.get(i);
			ip_list.add(server.getAddress());
			port_list.add(server.getPort());
			tag_list.add(server.getTagList());
		}
		server_met.setIPList(ip_list);
		server_met.setPortList(port_list);
		server_met.setTagList(tag_list);
		try {
			server_met.store();
		} catch (ServerMetException cause) {
			throw new ServerManagerException(cause);
		}
		
	}
	
	private void requestNextAutoConnectServer() throws ServerManagerException {
		if (candidate_servers.size() == 0) {
			auto_connect_started = false;
			notifyAutoConnectFailed();
			return ;
		}
		
		Server server = (Server) Collections.min(candidate_servers,
				compare_servers_by_ping);
		candidate_servers.remove(server);
		connect(server);
	}	
}
