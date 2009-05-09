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
package org.jmule.core.edonkey.packet.scannedpacket.impl;

import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import static org.jmule.core.edonkey.E2DKConstants.*;

/**
 * Created on Dec 24, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/05/09 16:24:59 $
 */
public class JMPeerPublicKeySP implements ScannedPacket {

	private byte[] public_key;
	
	public JMPeerPublicKeySP(byte[] publicKey) {
		super();
		this.public_key = publicKey;
	}

	public byte[] getPublicKey() {
		return public_key;
	}
	
	public int getPacketCommand() {
		return OP_PUBLICKEY;
	}

	

}
