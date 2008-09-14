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

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

import org.jmule.core.net.JMUDPConnection2;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 12:00:00 $$
 */
public privileged aspect UDPConnectionImplLogger {
	private Logger log = Logger.getLogger("org.jmule.core.net.impl.UDPConnectionImpl");
	
	after() throwing (Throwable t): execution (* UDPConnection.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	after(SocketAddress s) throwing(IOException e) : args(*,s) && call(* DatagramChannel.send(ByteBuffer,SocketAddress)) {
		log.info("Failed to send UDP packet to "+s);
	}
	
	before() : call (void JMUDPConnection.ban()) {
		log.warning("UDP flood");
	}
}
