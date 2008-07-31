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

import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATUS;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:38 $$
 */
public class JMPeerFileStatusAnswerSP implements ScannedPacket {

	private FileHash fileHash;
	private JMuleBitSet partStatus;
	
	public JMPeerFileStatusAnswerSP(FileHash fileHash, JMuleBitSet partStatus) {
		this.fileHash = fileHash;
		this.partStatus = partStatus;
	}

	public int getPacketCommand() {
		return OP_FILESTATUS;
	}

	public FileHash getFileHash() {
		return fileHash;
	}

	public void setFileHash(FileHash fileHash) {
		this.fileHash = fileHash;
	}

	public JMuleBitSet getPartStatus() {
		return partStatus;
	}

	public void setPartStatus(JMuleBitSet partStatus) {
		this.partStatus = partStatus;
	}

}
