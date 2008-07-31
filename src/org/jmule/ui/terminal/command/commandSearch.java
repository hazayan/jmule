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

import java.util.Iterator;

import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.search.SearchResult;
import org.jmule.core.search.SearchResultFile;
import org.jmule.core.search.SearchResultsList;


public class commandSearch implements terminalCommand {

	public void execCommand(String[] params) {
		// TODO Auto-generated method stub

		if (params[1].equals("query")){
			//Search 
			Server connectedServer = null;
		//	connectedServer = ServersManagerFactory.getInstance().getConnectedServer();
			
			if (connectedServer==null) {
				TerminalOutput.println("Don't have connection with server");
				return ;
			}
				
			if (connectedServer.allowSearch()){
				connectedServer.searchFiles(params[2]);
			}
			else
				TerminalOutput.println("Can't search now ");
		}
		
		if (params[1].equals("list")){
			//Show breif info

			TerminalOutput.println("Total Results : "+SearchResultsList.getInstance().size());
						
			TerminalOutput.println("<ID>\t\t<Search String>\t\t Result count");
			
			int id = 0;
			for(Iterator i = SearchResultsList.getInstance().iterator();i.hasNext();) {
				SearchResult sResult = (SearchResult)i.next();
				TerminalOutput.println(id+"\t\t"+sResult.getSearchQuery()+"\t\t"+sResult.size());
				id++;
			}
			
		}
		
		if (params[1].equals("info")){
			//Show info
			if (params.length!=3) {
				TerminalOutput.println(this.getUsage());
				return;
			}
			int ID=Integer.parseInt(params[2]);
			
			SearchResult sResult = (SearchResult) SearchResultsList.getInstance().get(ID);
			
			TerminalOutput.println("Total results : "+sResult.size());
			
			int id = 0;
			for(Iterator i = sResult.iterator();i.hasNext();) {
				SearchResultFile sFile = (SearchResultFile) i.next();
				TerminalOutput.println(id+". "+sFile+" Size : "+sFile.getFileSize()+" Avaibility : "+sFile.getFileAviability());
				id++;
			}
			
	//		for(int i=0;i<results.length;i++){
				//TODO : Delete next line
			//	if (results[i].getFileAviability()>10)
	//			TerminalOutput.println(i+". Name : "+results[i].getFileName()+"\tSize : "+results[i].getFileSize()+"\tAviability : "+results[i].getFileAviability()+"\t Sources : "+results[i].getFileCompleteSrc()+" Holding ID : "+results[i].getStringClientID());
///			}
			
			
		}
		
		
		TerminalOutput.flush();
		
		
	}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "search";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "search for files on server";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return this.getCommandName()+" <params>\n" +
				"<params>\n" +
				"query <search string>\t search for files on server\n" +
				"list\t show breif search result \n" +
				"info <search result ID>\t show search result for <search result ID>";
	}

}
