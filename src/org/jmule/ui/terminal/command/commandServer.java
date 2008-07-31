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
package org.jmule.ui.terminal.command;

import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.net.JMConnection;

/**
 * Command : "server"
 * @author binary
 */
public class commandServer implements terminalCommand {

	public void execCommand(String[] params) {
		if (params.length==1) return;
		
		if (params[1].equals("connect")){
			//Connect to server case
			if (params.length!=3){
                TerminalOutput.println(getUsage());
                return ;
        }
            
        try {
                String srvAddress="";
                short port;
                int i=0;
                while (params[2].charAt(i)!=':'){
                        srvAddress=srvAddress+params[2].charAt(i);
                        i++;
                }
                String srvPort="";
                for(int j=i+1;j<params[2].length();j++)
                        srvPort=srvPort+params[2].charAt(j);

        port=Short.parseShort(srvPort);
        Server server = new Server(srvAddress,port);
       // ServerList.getInstance().add(server);
        server.connect();
        }catch(Exception e){
                TerminalOutput.println("Invalid command params");
                TerminalOutput.println(this.getUsage());
                e.printStackTrace();
        }	
		}
		
		if (params[1].equals("disconnect")){
			//Disconnect from server
			//CoreManager.getInstance().stopListener();
			//Server s = ServersManagerFactory.getInstance().getConnectedServer();
			//if (s!=null) s.disconnect();
			
		}
		
		if (params[1].equals("status")){
			//Status message
			Server server = null;
			//server = ServersManagerFactory.getInstance().getConnectedServer();
			
			if (server==null) {
				TerminalOutput.println("Don't have connection with server");
				return ;
			}
			TerminalOutput.println("Server status : "+((server.getStatus())==JMConnection.TCP_SOCKET_CONNECTED ? "connecetd " : "disconnected"));
			TerminalOutput.println("Users : "+server.getNumUsers());
			TerminalOutput.println("Files : "+server.getNumFiles());
			TerminalOutput.flush();
		}
	}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "server";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "eDonkey server management ";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return this.getCommandName()+" [<params>]\n" +
				"<params>\n" +
				"connect <IP>:<Port>\t connect to remonte server\n" +
				"disconnect\t disconnect from remonte server\n" +
				"status\t\t get status of server connection";
	}

}
