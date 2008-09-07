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

import java.util.List;

import org.jmule.core.JMRunnable;
import org.jmule.core.edonkey.ServerListListener;
import org.jmule.core.edonkey.ServerListener;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerException;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.common.ConnectButton;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.mainwindow.StatusBar;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:48:20 $$
 */
public class SWTServerListWrapper {
	
	private static SWTServerListWrapper instance = null;
	private ServerManager server_manager = null;
	
	private ConnectionInfo connection_info;
	private ServerList server_list;
	private ServerMessages server_messages;
	private StatusBar status_bar;
	private ConnectButton connect_button;
	private ServerListTab server_list_tab;
	
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
		server_manager.addServerListener(new ServerListener() {

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
					setUIDisconnected(server);
				}
			}

			public void isconnecting(Server server) {
				setUIConnecting(server);
			}

			public void serverMessage(Server server,final String message) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						server_messages.addText(message);
					}});
			}

			
		});
		
		server_manager.addServerListListener(new ServerListListener() {

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
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_added",server.getAddress()+":"+server.getPort()));
						server_list.addServer(server);
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}

			public void serverListCleared() {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_list_cleared"));
						server_list.clear();
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}

			public void serverRemoved(final Server server) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_server_removed",server.getAddress()+":"+server.getPort()));
						server_list.removeServer(server);
						server_list_tab.setServerCount(server_manager.getServersCount());
				}});
			}
			
		});
	}
	
	private void setUIConnecting() {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setConnecting();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnecting();
				if (!connection_info.isDisposed())
					connection_info.setStatusConnecting(null);
			}
		});
	}
	
	private void setUIConnecting(final Server server) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setConnecting();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnecting();
				if (!connection_info.isDisposed())
					connection_info.setStatusConnecting(server);
			}});
	}
	
	private void setUIDisconnected(final Server... serverList) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setDisconnected();
				if (!status_bar.isDisposed())
					status_bar.setStatusDisconnected();
				if (!connection_info.isDisposed())
					connection_info.setStatusDisconnected();
				if (!server_list.isDisposed()) 
					if (serverList.length>0)
						server_list.serverDisconnected(serverList[0]);
					
			}});
	}
	
	private void setUIConnected(final Server server) {
		if (SWTThread.getDisplay().isDisposed()) return ;
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (!connect_button.isDisposed())
					connect_button.setConnected();
				if (!status_bar.isDisposed())
					status_bar.setStatusConnected(server);
				if (!connection_info.isDisposed())
					connection_info.setStatusConnected(server);
			}});
	}
	
	public void startAutoConnect() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				try {
					server_manager.connect();
				} catch (ServerManagerException e) {
					e.printStackTrace();
					setUIDisconnected();
				}
			}
		});
	}

	public void stopConnecting() {
		SWTThread.getDisplay().asyncExec(new JMRunnable() {
			public void JMRun() {
				if (is_autoconnect) {
					is_autoconnect = false;
					server_manager.stopAutoConnect();
				}
				if (single_connect) {
					single_connect = false;
					connecting_server.disconnect();
				}
			}
		});
		
	}
	
	public void disconnect() {
		server_manager.getConnectedServer().disconnect();
	}
	
	public void connectTo(Server server) {
		connecting_server = server;
		single_connect = true;
		try {
			server_manager.connect(server);
		} catch (ServerManagerException e) {
			
		}
	}
	
	public boolean isAutoconnecting() {
		return is_autoconnect;
	}
	
	public void addServer(Server server) {
		server_manager.addServer(server);
	}
	
	public void removeServer(List<Server> servers) {
		server_manager.removeServer(servers);
	}
	
	public void removeServer(Server server) {
		server_manager.removeServer(server);
	}
	
	public void clearServerList() {
		server_manager.clearServerList();
	}
	
	public void setServerManager(ServerManager serverManager) {
		this.server_manager = serverManager;
	}
	
	public void setServerListTab(ServerListTab tab) {
		server_list_tab = tab;
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
