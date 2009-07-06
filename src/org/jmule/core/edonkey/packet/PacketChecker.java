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
package org.jmule.core.edonkey.packet;

import static org.jmule.core.edonkey.E2DKConstants.BLOCKSIZE;

import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerRequestFilePartSP;
import org.jmule.core.net.PacketScanner;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:05:08 $$
 */
public class PacketChecker {

	public static boolean checkServerPacket(Packet packet) {
		ScannedPacket scannedPacket;
		try {
			scannedPacket = PacketScanner.scanServerPacket(packet);
			return checkPacket(scannedPacket);
		} catch (Exception e) {
			return false;
		} 
		
	}
	
	public static boolean checkPeerPacket(Packet packet) {
		ScannedPacket scannedPacket;
		try {
			scannedPacket = PacketScanner.scanPeerPacket(packet);
			return checkPacket(scannedPacket);
		} catch (Exception e) {
			return false;
		} 
		
	}
	
	public static boolean checkPacket(ScannedPacket scannedPacket) {
		try {		
			if (scannedPacket instanceof JMPeerRequestFilePartSP) {
				JMPeerRequestFilePartSP sPacket = (JMPeerRequestFilePartSP)scannedPacket;
				for(FileChunkRequest chunkRequest : sPacket) {
					if (chunkRequest.getChunkEnd()-chunkRequest.getChunkBegin()>BLOCKSIZE*3)
						return false;
				}
			}
		} catch (Throwable e) {
			return false;
		}
		return true;
	}
	
}
