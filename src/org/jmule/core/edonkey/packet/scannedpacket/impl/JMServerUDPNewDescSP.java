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

import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;

import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.tag.impl.TagList;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:40 $$
 */
public class JMServerUDPNewDescSP implements ScannedPacket  {

	private TagList tagList;
	private int challenge;
	
	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}

	public int getChallenge() {
		return challenge;
	}

	public void setChallenge(int challenge) {
		this.challenge = challenge;
	}

	
	
	public JMServerUDPNewDescSP(int challenge, TagList tagList) {
		super();
		this.challenge = challenge;
		this.tagList = tagList;
	}

	public int getPacketCommand() {
	
		return OP_SERVER_DESC_ANSWER;
	}

}
