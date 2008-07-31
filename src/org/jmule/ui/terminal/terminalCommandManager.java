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
package org.jmule.ui.terminal;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.jmule.ui.terminal.command.*;

/**
 * Terminal commands manager
 * 
 * @author binary
 * 
 */
public class terminalCommandManager {

	private Logger log = Logger.getLogger(this.getClass().getName());

	private LinkedList commandList = new LinkedList();

	private static terminalCommandManager commandManager = null;

	/**
	 * Get instance of terminalCommandManager class
	 * 
	 * @return terminalCommandManager instance
	 */
	public static terminalCommandManager getInstance() {
		if (commandManager == null)
			commandManager = new terminalCommandManager();
		return commandManager;
	}

	/**
	 * Load terminal commands
	 */
	public void loadCommands() {
		log.info("Load terminal commands");
		/**
		 * Load Commands..
		 */
		this.commandList.add(new commandExit());
		this.commandList.add(new commandHelp());
		this.commandList.add(new commandServer());
		this.commandList.add(new commandSearch());
		this.commandList.add(new commandDownload());
		this.commandList.add(new commandShared());
		this.commandList.add(new commandDebug());
	}

	/**
	 * Unload commands list
	 */
	public void unLoadCommands() {
		log.info("Unload terminal commands");
		while (this.commandList.size() > 0)
			this.commandList.remove();
	}

	/**
	 * 
	 * @param command
	 */
	public void execCommand(String command) {
		String[] params = parseUserCommand(command);
		// Find command in List
		for (int i = 0; i < this.commandList.size(); i++) {
			terminalCommand tCommand = (terminalCommand) commandList.get(i);
			if (tCommand.getCommandName().equals(params[0])) {
				tCommand.execCommand(params);
				return;
			}
		}

		// Show "NOT" found command
		if (command.length()==0) return;
		new commandCommandNotFound().execCommand(params);

	}
	
	public void usageCommand(String command) {
		String[] params = parseUserCommand(command);
		// Find command in List
		for (int i = 0; i < this.commandList.size(); i++) {
			terminalCommand tCommand = (terminalCommand) commandList.get(i);
			if (tCommand.getCommandName().equals(params[0])) {
				terminalUI.getInstance().getTerminalOutput().println(tCommand.getUsage());
				terminalUI.getInstance().getTerminalOutput().flush();
				return;
			}
		}

		// Show "NOT" found command
		new commandCommandNotFound().execCommand(params);

	}

	/**
	 * Parse user's command
	 * 
	 * @param userCommand
	 *            User's command
	 * @return Array with params, [0] - command name
	 */
	private String[] parseUserCommand(String userCommand) {
		int numParams = 0;
		int i = 0;
		// Count params
		do {
			while ((i < userCommand.length()) && (userCommand.charAt(i) != ' '))
				i++;
			numParams++;
			while ((i < userCommand.length()) && (userCommand.charAt(i) == ' '))
				i++;
		} while (i < userCommand.length());

		String[] params = new String[numParams];

		// Extract params
		i = 0;
		int cParam = 0;
		do {
			params[cParam] = "";
			while ((i < userCommand.length()) && (userCommand.charAt(i) != ' ')) {
				params[cParam] = params[cParam] + userCommand.charAt(i);
				i++;
			}
			cParam++;
			while ((i < userCommand.length()) && (userCommand.charAt(i) == ' '))
				i++;
		} while (i < userCommand.length());

		return params;
	}
	

	/**
	 * Get command list
	 * @return command list
	 */
	public String[][] getCommandList(){
		String[][] commandList=new String[this.commandList.size()][2];
		
		for (int i=0;i<this.commandList.size();i++){
			terminalCommand command=(terminalCommand) this.commandList.get(i);
			commandList[i][0]=command.getCommandName();
			commandList[i][1]=command.getShortInfo();
		}

		return commandList;
	}

}
