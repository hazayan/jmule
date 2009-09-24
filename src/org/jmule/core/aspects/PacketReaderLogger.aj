/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.aspects;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.util.logging.Logger;

import org.jmule.core.networkmanager.JMEndOfStreamException;
import org.jmule.core.networkmanager.PacketReader;
import org.jmule.core.utils.Misc;

/**
 * Created on Sep 18, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/24 04:51:56 $
 */
public privileged aspect PacketReaderLogger {
	private Logger log = Logger
			.getLogger("org.jmule.core.networkmanager.PacketReader");

	after() throwing (Throwable t): execution (* PacketReader.*(..)) {
		if ((t.getCause() instanceof AsynchronousCloseException)) return ;
		if ((t.getCause() instanceof ClosedByInterruptException)) return ;
		if ((t.getCause() instanceof JMEndOfStreamException)) return ;
		if ((t instanceof JMEndOfStreamException)) return ;
		log.warning(Misc.getStackTrace(t));
	}
}
