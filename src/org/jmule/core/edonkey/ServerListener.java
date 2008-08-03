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
package org.jmule.core.edonkey;

import org.jmule.core.edonkey.impl.Server;

/**
 * Created on 2008-Jul-10
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/03 09:53:47 $$
 */
public interface ServerListener {

	 /**
	  * Tells when the system connects to the server
	  * @param server the given server
	  */
	 public void connected(Server server);
	 
	 /**
	  * Tells when the system has been disconnected from the server
	  * @param server the given server
	  */
	 public void disconnected(Server server);
	 
	 /**
	  * Tells when the system is waiting for a server connection
	  * @param server the given server
	  */
	 public void isconnecting(Server server);
	 
	 /**
	  * Tells when server send a message to client
	  * @param server the given server
	  * @param message message 
	  */
	 public void serverMessage(Server server,String message);
	
}
