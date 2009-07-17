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

import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedUDPPacket;
import org.jmule.core.net.PacketScanner;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;
/**
 * Created on Jul 17, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/17 08:21:26 $
 */
public aspect PacketScannerLogger {

	private Logger log = Logger.getLogger("org.jmule.core.net.PacketScanner");
	
	after() throwing (Throwable t): execution (* PacketScanner.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	after(Packet ipacket) returning(ScannedPacket packet) : execution( ScannedPacket PacketScanner.*(Packet)) && args(ipacket) {
		if (packet == null) {
			log.warning("Unknown packet : " + Convert.byteToHexString(ipacket.getPacket(), " 0x"));
		}
	}
	
	after(UDPPacket ipacket) returning(ScannedUDPPacket packet) : execution( ScannedUDPPacket PacketScanner.scanPacket(UDPPacket)) && args(ipacket) {
		if (packet == null) {
			log.warning("Unknown packet : " + Convert.byteToHexString(ipacket.getPacket(), " 0x"));
		}
	}
	
}
