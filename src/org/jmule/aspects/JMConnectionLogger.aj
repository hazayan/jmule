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
package org.jmule.aspects;

import java.util.logging.Logger;
import org.jmule.core.net.JMConnection;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:25 $$
 */
public privileged aspect JMConnectionLogger {
	
	private Logger log = Logger.getLogger("org.jmule.core.edonkey.impl.Server");
	
	after(JMConnection connection) : target(connection) && call(void JMConnection.disconnect()) {
	}
	
	before(JMConnection connection) : target(connection) && call(void JMConnection.startConnecting()) {
		
		if ((connection.connectingThread != null) && (connection.connectingThread.isAlive()))
			
			log.severe("Failed to start connecting to "
					+ connection.getAddress()+" connecting thread already run!");
			
	}

	before(JMConnection connection) : target(connection) && call(void JMConnection.startReceiver()) {
		
		if ((connection.packetReceiverThread != null) && (connection.packetReceiverThread.isAlive()))
			
			log.severe("Failed to start reciver for "+connection.getAddress());
			
	}
	
	before(JMConnection connection) : target(connection) && call(void JMConnection.stopReceiver()) {
		
		if ((connection.packetReceiverThread != null) && (connection.packetReceiverThread.isAlive()))
			
			log.severe("Failed to stop receiver for "+connection.getAddress());
		
	}
	
	before(JMConnection connection) : target(connection) && execution(void JMConnection.ban()) {
		
		log.warning("Ban connection : "+connection);
		
	}

}
