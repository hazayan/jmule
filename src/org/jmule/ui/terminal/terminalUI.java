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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.jmule.ui.JMuleUI;
import org.jmule.ui.skin.Skin;

public class terminalUI implements JMuleUI {

	private Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Terminal's input and output
	 */
	private BufferedReader TerminalInput = null;

	private PrintWriter TerminalOutput = null;

	/**
	 * Terminal prompt
	 */
	private String terminalPrompt = "$jMule> ";

	/**
	 * Terminal instance propery
	 */
	private static terminalUI terminalInstance = null;

	/**
	 * Status of TerminalUI
	 */
	private boolean uiStatus = false;

	/**
	 * Terminal command manager instance
	 */
	private terminalCommandManager commandManager = terminalCommandManager
			.getInstance();

	/**
	 * Command reciver thread
	 */
	private TerminalCommandReciver commandReciver = null;

	/**
	 * Get instance of TerminalUI class
	 * 
	 * @return
	 */
	public static terminalUI getInstance() {
		// TODO Auto-generated method stub
		if (terminalInstance == null)
			terminalInstance = new terminalUI();
		return terminalInstance;
	}

	/**
	 * TerminalUI private constructor
	 */
	private terminalUI() {

	}

	public boolean getStatus() {
		// TODO Auto-generated method stub
		return this.uiStatus;
	}

	public void start() {
		// TODO Auto-generated method stub
		TerminalInput = new BufferedReader(new InputStreamReader(System.in));
		TerminalOutput = new PrintWriter(System.out, false);

		commandManager.loadCommands();

		if ((this.commandReciver == null) || (!this.commandReciver.isAlive()))
			this.commandReciver = new TerminalCommandReciver();
		this.uiStatus = true;
	}

	public void stop() {
		// TODO Auto-generated method stub
		commandManager.unLoadCommands();
		TerminalInput = null;
		TerminalOutput = null;
		this.uiStatus = false;
		commandReciver.stop();
	}

	/**
	 * Get terminal input
	 * 
	 * @return terminal input
	 */
	public BufferedReader getTerminalInput() {
		return TerminalInput;
	}

	/**
	 * Get terminal output
	 * 
	 * @return termianl output
	 */
	public PrintWriter getTerminalOutput() {
		return TerminalOutput;
	}
	

private class TerminalCommandReciver extends Thread {
	public TerminalCommandReciver() {
		super("Terminal Command Reciver");
		start();
	}

	
	public void run() {

		TerminalOutput.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		TerminalOutput.println("jMule project");
		TerminalOutput.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-");
		
		while (true) {
			try {
				if (TerminalOutput==null) return ;
				TerminalOutput.print(terminalPrompt);
				TerminalOutput.flush();
				String userCommand = TerminalInput.readLine();
				commandManager.execCommand(userCommand);
			} catch (IOException e) {
				// TODO Auto-generated catch block
						e.printStackTrace();
			}
		}
	}

}


public void initialize() {
	// TODO Auto-generated method stub
	
}

public void shutdown() {
	// TODO Auto-generated method stub
	
}

public Skin getSkin() {
	// TODO Auto-generated method stub
	return null;
}

}
