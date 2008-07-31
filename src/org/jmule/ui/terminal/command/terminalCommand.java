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

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.jmule.ui.terminal.terminalUI;

/**
 * Terminal Command interface
 * @author binary
 * @version 0.1
 */
public interface terminalCommand {

	/**
	 * Terminal Input and output
	 */
	public BufferedReader TerminalInput=terminalUI.getInstance().getTerminalInput();
	public PrintWriter TerminalOutput=terminalUI.getInstance().getTerminalOutput();
	
	/**
	 * Get command name
	 * @return command name
	 */
	public String getCommandName();
		
	/**
	 * Get short info about comand
	 * @return short info about command
	 */
	public String getShortInfo();
	
	/**
	 * Get command usage
	 * @return get command usage
	 */
	public String getUsage();

	/**
	 * Exec command
	 * @param params command params
	 */
	public void execCommand(String[] params);
	
}
