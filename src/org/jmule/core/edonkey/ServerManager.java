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
package org.jmule.core.edonkey;

import java.net.InetSocketAddress;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.impl.Server;

/**
 * Created on 2008-Jul-06
 * @author javajox
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/23 13:18:05 $$
 */
public interface ServerManager extends JMuleManager {

	public boolean isConnected();
	
	/**
	 * Import servers from specified file
	 * @param fileName file with servers
	 * @throws ServerManagerException
	 */
	public void importList(String fileName) throws ServerManagerException;
	
	/**
	 * Adds a new server to the server list
	 * @param server the server that is added to the server list
	 */
	public void addServer(Server server);
	
	/**
	 * Removes the given server form the server list
	 * @param server the given server
	 */
	public void removeServer(Server server);
	
	/**
	 * Returns the current connected server or null if there is no connected server
	 * @return the connected server
	 */
	public Server getConnectedServer();
	
	/**
	 * Gets the server identified by the given InetSocketAddress
	 * @param address the given InetSocketAddress
	 * @return the server identified by the InetSocketAddress
	 */
	public Server getServer(InetSocketAddress address);
	
	/**
	 * Connect automatically to server which have smaller ping.
	 */
	public void connect() throws ServerManagerException;
	/**
	 * Tried to connect to the given server
	 * @param server the given server
	 * @throws ServerManagerException if the server is not present in the list or the server can't be reached
	 */
	public void connect(Server server) throws ServerManagerException;
	
	public void startAutoConnect() throws ServerManagerException;
	
	/** 
	 * Stop connecting to servers.
	 */
	public void stopAutoConnect();
	
	/**
	 * @return the number of servers from the server list
	 */
	public int getServersCount();
	
	/**
	 * @return the current server list
	 */
	public Iterable<Server> getServers();
	
	/**
	 * Clears the current server list
	 */
	public void clearServerList();
	
	/**
	 * Loads the server list from the given server met file (usually server.met)
	 * @param serverMet the given server met file
	 * @throws ServerManagerException
	 */
	public void loadServerList() throws ServerManagerException;
	
	/**
	 * Stores the server list to the given met file (usually server.met)
	 * @param serverMet the given met file
	 * @throws ServerManagerException
	 */
	public void storeServerList() throws ServerManagerException;
	
	/**
	 * Tells if the given server exists in the server list.
	 * @param server the given server
	 * @return true if the given server exists in the server list, false otherwise
	 */
	public boolean hasServer(Server server);
	
	/**
	 * Starts the UDP query thread
	 */
	public void startUDPQuery();
	
	/**
	 * Stops the UDP query thread
	 */
	public void stopUDPQuery();
	
	/**
	 * Adds a new server list listener
	 * @param serverListListener the given server list listener
	 */
	public void addServerListListener(ServerListListener serverListListener);
	
	/**
	 * Removes the given server list listener
	 * @param serverListListener the given server list listener
	 */
	public void removeServerListListener(ServerListListener serverListListener);
	
	/**
	 * Adds a new server listener to all servers from the server list
	 * @param serverListener the given server listener
	 */
	public void addServerListener(ServerListener serverListener);
	
	/**
	 * Removes the given server list listener from the servers 
	 * @param serverListener the given server listener
	 */
	public void removeServerListener(ServerListener serverListener);
	
}
