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
package org.jmule.ui.swt.maintabs.serverlist;

import org.jmule.core.edonkey.ServerListListener;
import org.jmule.core.edonkey.ServerListener;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerException;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.ConnectButton;
import org.jmule.ui.swt.mainwindow.StatusBar;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:10 $$
 */
public class SWTServerListWrapper implements ServerListListener,ServerListener {
	
	private static SWTServerListWrapper instance = null;
	private ServerManager server_manager = null;
	
	private ConnectionInfo connection_info;
	private ServerList server_list;
	private ServerMessages server_messages;
	private StatusBar status_bar;
	private ConnectButton connect_button;
	private boolean is_autoconnect = false;
	
	private boolean single_connect = false;
	private Server connecting_server = null;
		
	public static void createListener(ServerManager serverManager) {
		instance = new SWTServerListWrapper(serverManager);
	}
	
	public static SWTServerListWrapper getInstance() {
		return instance;
	}
	
	private SWTServerListWrapper(ServerManager serverManager) {
		server_manager = serverManager;
		server_manager.addServerListener(this);
		server_manager.addServerListListener(this);
	}
	
	private void setUIConnecting() {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				connect_button.setConnecting();
				status_bar.setStatusConnecting();
				connection_info.setStatusConnecting(null);
			}
		});
	}
	
	private void setUIConnecting(final Server server) {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				connect_button.setConnecting();
				status_bar.setStatusConnecting();
				connection_info.setStatusConnecting(server);
			}});
	}
	
	private void setUIDisconnected() {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				connect_button.setDisconnected();
				status_bar.setStatusDisconnected();
				connection_info.setStatusDisconnected();
			}});
	}
	
	private void setUIConnected(final Server server) {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				connect_button.setConnected();
				status_bar.setStatusConnected(server);
				connection_info.setStatusConnected(server);
			}});
	}
	
	public void startAutoConnect() {
		try {
			server_manager.connect();
		} catch (ServerManagerException e) {
			e.printStackTrace();
			setUIDisconnected();
		}
	}

	public void stopConnecting() {
		if (is_autoconnect) {
			is_autoconnect = false;
			server_manager.stopAutoConnect();
		}
		if (single_connect) {
			single_connect = false;
			connecting_server.disconnect();
		}
	}
	
	public void disconnect() {
		server_manager.getConnectedServer().disconnect();
	}
	
	public void connectTo(Server server) {
		connecting_server = server;
		single_connect = true;
		server.connect();
	}
	
	public void autoConnectStarted() {
		if ( is_autoconnect == false ) {
			is_autoconnect = true;
			setUIConnecting();
		}
	}

	public void autoConnectStopped() {
		if (is_autoconnect) {
			is_autoconnect = false;
			setUIDisconnected();
		}
	}

	public void serverAdded(final Server server) {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				server_list.addServer(server);
			}});
	}

	public void serverListCleared() {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				server_list.clear();
			}});
	}

	public void serverRemoved(final Server server) {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				server_list.removeServer(server);
			}});
	}

	public void connected(Server server) {
		if (is_autoconnect) {
			is_autoconnect = false;
			setUIConnected(server);
		}
		if (single_connect) {
			single_connect = false;
			setUIConnected(server);
		}
	}

	public void disconnected(Server server) {
		if (!is_autoconnect) {
			setUIDisconnected();
		}
		
	}

	public void isconnecting(Server server) {
		setUIConnecting(server);
	}

	public void serverMessage(Server server, final String message) {
		Utils.getDisplay().asyncExec(new Runnable() {
			public void run() {
				server_messages.addText(message);
			}});
	}
	
	public void addServer(Server server) {
		server_manager.addServer(server);
		//server_list.addServer(server);
	}
	
	public void removeServer(Server server) {
		if (server.isConnected()) server.disconnect();
		server_manager.removeServer(server);
	}
	
	public void clearServerList() {
		server_manager.clearServerList();
	}
	
	public void setServerManager(ServerManager serverManager) {
		this.server_manager = serverManager;
	}

	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connection_info = connectionInfo;
	}

	public void setServerList(ServerList serverList) {
		this.server_list = serverList;
	}

	public void setServerMessages(ServerMessages serverMessages) {
		this.server_messages = serverMessages;
	}
	
	public void setStatusBar(StatusBar statusBar) {
		this.status_bar = statusBar;
	}
	
	public void setConnectButton(ConnectButton connectButton) {
		this.connect_button = connectButton;
	}
	
}
