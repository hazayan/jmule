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

/**
 * Command not found command
 * @author binary
 * @version 0.1
 */
public class commandCommandNotFound implements terminalCommand {

	public void execCommand(String[] params) {
		// TODO Auto-generated method stub
		this.TerminalOutput.println("jMule2 :  "+params[0]+" : not found !");
		this.TerminalOutput.flush();
	}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "notFound";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "Show command not found message";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return getCommandName()+" <command>";
	}

}
