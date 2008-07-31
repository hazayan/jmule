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

public class commandShared implements terminalCommand {

	public void execCommand(String[] params) {
		if (params.length==1) return;
//		if (params[1].equals("info")) {
//			SharedManager shareManager = SharedManager.getInstance();
//			int count = shareManager.size();
//			TerminalOutput.println("Total shared file : "+count);
//			TerminalOutput.println("jMule shared files : ");
//			Object sharedList[] = shareManager.getList().toArray();
//			for(int i = 0 ; i<count ; i++) {
//				SharedCompletedFile sharedFile = (SharedCompletedFile) sharedList[i];
//				TerminalOutput.println("Shared file ID : "+(i+1));
//				TerminalOutput.println("Name : "+sharedFile.getSharingName());
//				TerminalOutput.println("Size : "+sharedFile.length());
//				TerminalOutput.println("File Hash : "+sharedFile.getFileHash());
//				TerminalOutput.println("Part count : "+sharedFile.getHashSet().size());
//				if (sharedFile.getStatus()) {
//					TerminalOutput.println("Status : Complete file");
//				} else {
//					TerminalOutput.println("Status :  Uncompleted file");
//					SharedPartialFileImpl partialFile = (SharedPartialFileImpl) sharedFile;
//					TerminalOutput.println("Temp file : "+partialFile.getName());
//					TerminalOutput.println("Met file : "+partialFile.getPartFile().getName());
//					TerminalOutput.println("Gap List : \n"+partialFile.getGapList());
//				}
//			}
//		}
		
	}

	public String getCommandName() {
		return "shared";
	}

	public String getShortInfo() {
		return "Manage shared files";
	}

	public String getUsage() {
		return "info - show all shared files";
	}

}
