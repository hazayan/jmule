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

import org.jmule.ui.terminal.terminalCommandManager;

public class commandHelp implements terminalCommand {

	public void execCommand(String[] params) {
		// TODO Auto-generated method stub
		String commandList[][]=terminalCommandManager.getInstance().getCommandList();
		if (params.length==1){
			
			
			for(int i=0;i<commandList.length;i++)
				TerminalOutput.println(commandList[i][0]+"\t\t"+commandList[i][1]);
		TerminalOutput.flush();
		} else {
			terminalCommandManager.getInstance().usageCommand(params[1]);
			}
		}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "help";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "show information about commands";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return this.getCommandName()+" [<command name>] ";
	}

}
