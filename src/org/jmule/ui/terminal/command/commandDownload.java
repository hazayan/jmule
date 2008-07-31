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

import org.jmule.core.downloadmanager.DownloadException;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.search.SearchResult;
import org.jmule.core.search.SearchResultFile;
import org.jmule.core.search.SearchResultsList;
import org.jmule.core.sharingmanager.SharedFileException;

public class commandDownload implements terminalCommand{

	public void execCommand(String[] params) {
	
		if (params[1].equals("add")) {
			//Add download to list
			int searchID=Integer.parseInt(params[2]);
			int resultID=Integer.parseInt(params[3]);
			
			SearchResult sResult = (SearchResult) SearchResultsList.getInstance().get(searchID);
			SearchResultFile sFile = (SearchResultFile) sResult.get(resultID);
//			try {
//				OldDownloadManager.getInstance().addSession(sFile);
//				OldDownloadManager.getInstance().getSession(sFile.getFileHash()).startDownload();
//			} catch (SharedFileException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (DownloadException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		if (params[1].equals("link")){
			ED2KFileLink fLink = new ED2KFileLink(params[2]);
//			try {
//				OldDownloadManager.getInstance().addSession(fLink);
//			} catch (SharedFileException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (DownloadException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		if (params[1].equals("resume")){
		//	SharedManager shareManager = SharedManager.getInstance();
		//	Object sharedList[] = shareManager.getList().toArray();
	//		int sharedItem = Integer.parseInt(params[2])-1;
		//	if ((sharedItem<0)||(sharedItem>sharedList.length)) {
		//		TerminalOutput.println("Shared item not found !");
		//		return;
		//	}
			
	//		SharedCompletedFile sharedFile = (SharedCompletedFile) sharedList[sharedItem];
	//		if (sharedFile.getStatus()) {
	//			TerminalOutput.println("File is completed");
	//			return;
	//		}
			
	//		try {
	//	//		DownloadManager.getInstance().add((SharedPartialFileImpl)sharedFile);
	//		} catch (DownloadException e) {
				// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
			
			}
			
		}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "download";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "Download files";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return this.getCommandName()+" <params>\nadd <search id> <result id> - download file\nlink <ed2k link> download from ed2k:// link";
	}

}
