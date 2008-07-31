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

import org.jmule.core.JMuleCoreException;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.impl.JMuleCoreImpl;
import org.jmule.ui.terminal.terminalUI;

public class commandExit implements terminalCommand {

	public void execCommand(String[] params) {
		try {
			JMuleCoreFactory.getSingleton().stop();
		} catch (JMuleCoreException e) {
			e.printStackTrace();
		}
		//CoreManager.getInstance().stopListener();
	//	CoreManager.getInstance().disconnectFromServer();
		terminalUI.getInstance().stop();
	}

	public String getCommandName() {
		// TODO Auto-generated method stub
		return "exit";
	}

	public String getShortInfo() {
		// TODO Auto-generated method stub
		return "Exit from jMule";
	}

	public String getUsage() {
		// TODO Auto-generated method stub
		return this.getCommandName()+" - exit from jMule";
	}

}
