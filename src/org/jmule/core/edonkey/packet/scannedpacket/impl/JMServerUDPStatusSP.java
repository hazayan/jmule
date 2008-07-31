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

import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVSTATUS;

import java.net.InetSocketAddress;

import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:36 $$
 */
public class JMServerUDPStatusSP implements ScannedPacket {

	private int challenge;
	private int userCount;
	private int filesCount;
	private int softFilesLimit;
	private int hardSoftLimits;
	private InetSocketAddress sender;
	
	public JMServerUDPStatusSP(int challenge, int userCount,
			int filesCount, int softFilesLimit, int hardSoftLimits,InetSocketAddress sender) {
		super();
		this.challenge = challenge;
		this.userCount = userCount;
		this.filesCount = filesCount;
		this.softFilesLimit = softFilesLimit;
		this.hardSoftLimits = hardSoftLimits;
		this.sender = sender;
	}

	public int getPacketCommand() {
		return OP_GLOBSERVSTATUS;
	}

	public int getChallenge() {
		return challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public int getFilesCount() {
		return filesCount;
	}

	public void setFilesCount(int filesCount) {
		this.filesCount = filesCount;
	}

	public int getSoftFilesLimit() {
		return softFilesLimit;
	}

	public void setSoftFilesLimit(int softFilesLimit) {
		this.softFilesLimit = softFilesLimit;
	}

	public int getHardSoftLimits() {
		return hardSoftLimits;
	}

	public void setHardSoftLimits(int hardSoftLimits) {
		this.hardSoftLimits = hardSoftLimits;
	}

	public InetSocketAddress getSender() {
		return sender;
	}

	public void setSender(InetSocketAddress sender) {
		this.sender = sender;
	}
	
	

}
