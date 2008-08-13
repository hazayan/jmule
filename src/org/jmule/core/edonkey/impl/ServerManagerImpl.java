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
package org.jmule.core.edonkey.impl;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.JMIterable;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.edonkey.AutoConnectDoesNotSucceedException;
import org.jmule.core.edonkey.ServerListIsNullException;
import org.jmule.core.edonkey.ServerListListener;
import org.jmule.core.edonkey.ServerListener;
import org.jmule.core.edonkey.ServerListenerAdapter;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerException;
import org.jmule.core.edonkey.metfile.ServerMet;
import org.jmule.core.edonkey.metfile.ServerMetException;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/13 11:43:24 $$
 */
public class ServerManagerImpl implements ServerManager {

	private List<Server> server_list = new CopyOnWriteArrayList<Server>();
	
	private List<ServerListListener> server_list_listeners = new CopyOnWriteArrayList<ServerListListener>();
	
	// Auto connect process
	private List<Server> checked_servers = new LinkedList<Server>();
	
	private boolean auto_connect = false;
	
	private ServerMet server_met;
	
	private Comparator compare_servers_by_ping = new Comparator() {

		public int compare(Object object1, Object object2) {

			if( ((Server)object1).getPing() < ((Server)object2).getPing() ) {
				
				return -1;
				
			} else if( ((Server)object1).getPing() > ((Server)object2).getPing() ) {
				
				return +1;
				
			}
			
			return 0;
		}
	};
	
	public ServerManagerImpl() {
		
	}
	
	public void addServer(Server server) {
		
		if (server_list.contains(server)) return ;
		
		server_list.add(server);
		
		// notify all listeners that a new server has been added to the server list
		for(ServerListListener s : server_list_listeners) {
			
			s.serverAdded( server );
			
		}
		
		try {
			
			storeServerList();
			
		} catch (ServerManagerException e) {
			
			e.printStackTrace();
			
		}
	
	}
	

	public void removeServer(Server server) {
		
         server_list.remove(server);
         
         // notify all listeners that the given server has been removed from the server list
         for(ServerListListener s : server_list_listeners) {
        	 
        	 s.serverRemoved( server );
        	 
         }
         
 		 try {
 			 
			storeServerList();
			
		 } catch (ServerManagerException e) {
			 
			e.printStackTrace();
			
		 }

         
	}

	public void addServerListListener(ServerListListener serverListListener) {
		
		server_list_listeners.add(serverListListener);
		
	}

	public void clearServerList() {
		
		Server connected_server = getConnectedServer();
		
		if (connected_server != null)
			
			connected_server.disconnect();
		
		server_list.clear();
		
		//notify all listeners that the server list has been cleared
		for(ServerListListener s : server_list_listeners) {
			
			s.serverListCleared();
			
		}
		
		try {
			
			storeServerList();
			
		} catch (ServerManagerException e) {
			
			e.printStackTrace();
			
		}

		
	}

	public void connect(Server server) throws ServerManagerException {
		 
		 if (getConnectedServer()!=null)
			 getConnectedServer().disconnect();
		
		 server.connect();

	}
	
	public void connect() throws ServerManagerException {
		
		startAutoConnect();
	}
	
	public void startAutoConnect() throws ServerManagerException {
		
		if( auto_connect ) throw new ServerManagerException("The auto connect process is already running");
	    
		if( server_list.isEmpty() ) throw new ServerListIsNullException();
		
		Server connected_server = getConnectedServer();
		
		if( connected_server != null ) { // maybe should throw an exception
			
			connected_server.disconnect();
		}
		
	    for(ServerListListener listener : server_list_listeners) {
	    	
	    	listener.autoConnectStarted();
	    }
		
		List<Server> candidate_servers = new LinkedList<Server>();
		
		for(Server server : server_list) {
			
			if( !server.isDown() ) candidate_servers.add(server);
			
			// for testing only
			if( !server.isDown() ) System.out.println("---"+server.getPing());
		}
		
		while( !candidate_servers.isEmpty() ) {
			
			Object min_ping_server = Collections.min(candidate_servers, compare_servers_by_ping);
			
			if( ((Server)min_ping_server).isDown() ) {
				
				candidate_servers.remove(min_ping_server);
				
				continue;
			}
			
			/// for testing only
			
			((Server)min_ping_server).addServerListener(new ServerListenerAdapter() {
				public void connected(Server server) {
                     System.out.println("Connected to " + server);
				}
				
				 public void isconnecting(Server server) {
					 System.out.println("Is connecting to " + server);
				 }
				 
				 public void disconnected(Server server) {
					 System.out.println("Disconnected from " + server);
				 }
			});
			
            ((Server)min_ping_server).connect();
			
            try {
            	
              Thread.currentThread().sleep(10000);
              
            }catch(Throwable t) {
            	t.printStackTrace();
            }
            
            if( ((Server)min_ping_server).isConnected() ) {
            	
        	    for(ServerListListener listener : server_list_listeners) {
        	    	
        	    	listener.autoConnectStopped();
        	    }
            	
            	return;
            	
            } else {
            	
            	((Server)min_ping_server).disconnect();
            	
            	candidate_servers.remove(min_ping_server);
            }
		}
		
		if( candidate_servers.isEmpty() ) throw new AutoConnectDoesNotSucceedException();
		
	}
	
	public void stopAutoConnect() {
		
		auto_connect = false;
		
		checked_servers.get(checked_servers.size()-1).disconnect();
		
		for(ServerListListener s : server_list_listeners) {
				
				s.autoConnectStopped();
				
		}
		
	}
	
	public Server getConnectedServer() {
         
		 Server result = null;
		 
         for(Server server : server_list) {
        	 
        	 if( server.isConnected() ) {
        	     
        		 result = server;
        		 
        		 break;
        		 
        	 }
        	 
         }
		
        return result;
	}

	public Iterable<Server> getServers() {
			   
        return new JMIterable<Server>(server_list.iterator());
	}

	public int getServersCount() {
		
        return server_list.size();
        
	}

	public void loadServerList() throws ServerManagerException {
		
		//TODO : modify the exception architecture
		try {
			
            server_met.load();
            
            server_list = server_met.getServerList();
         
		} catch( ServerMetException sme ) {
			
			  throw new ServerManagerException("ServerMetException " + sme); }

	}
	
	public void storeServerList() throws ServerManagerException {
        
		//TODO : modify the exception architecture
		try {
			
			server_met.setServerList( server_list );
			
		    server_met.store();
		   
			
		} catch( ServerMetException sme ) {
			
			throw new ServerManagerException("ServerMetException :" + sme);
			
		}
		
	}

	public void removeServerListListener(ServerListListener serverListListener) {
		
		server_list_listeners.remove(serverListListener);
		
	}

	public void initialize() {
		try {
			server_met = new ServerMet(ConfigurationManager.SERVER_MET);
		} catch (ServerMetException e) {
		}
		
	}

	public void shutdown() {
	   
		stopUDPQuery();
		
		for(Server server : server_list) {
			
			if(server.isConnected()) {
				
				server.disconnect();
				
				return;
				
			}
			
		}
		
    }

	public void start() {}
	
	private void disconnectIfConnected(Server server) {
		
		if( ( server != null ) && ( server.isConnected() ) ) {
			
			server.disconnect();
			
		}
		 
	}


	public void startUDPQuery() {
		
		for(Server server : server_list) {
			
			server.start();
			
		}
		
	}


	public void stopUDPQuery() {
		
		for(Server server : server_list) {
			
			server.stop();
			
		}
		
	}


	public boolean hasServer(Server server) {
		
		for(Server checked_server : server_list) {
			
			if (checked_server.equals(server)) return true;
			
		}
		
		return false;
	}


	public Server getServer(InetSocketAddress address) {
		
		for(Server checked_server : server_list) {
			
			if (checked_server.getInetAddress().equals(address)) return checked_server;
			
		}
		
		return null;
	}


	public void addServerListener(ServerListener serverListener) {
		
          for(Server server : this.getServers()) {
        	  
        	  server.addServerListener(serverListener);
        	  
          }
		
	}


	public void removeServerListener(ServerListener serverListener) {
		
          for(Server server : this.getServers()) {
      	  
      	      server.addServerListener(serverListener);
      	  
          }
		
	}
		
}
