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
package org.jmule.core.search;

import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_AVIABILITY;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_COMPLETESRC;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_NAME;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_SIZE;

import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.packet.tag.impl.TagException;
import org.jmule.core.edonkey.packet.tag.impl.TagList;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:59 $$
 */
public class SearchResultFile  extends  TagList{
	private FileHash fileHash;
	private ClientID clientID;
	private short clientPort;
	
	public String toString() {
		String result="";
		result+="Name : " + this.getFileName()+" Size : "+this.getFileSize();
		return result;
	}
	
	public SearchResultFile(FileHash fileHash, ClientID clientID,
			short clientPort) {
		super();
		this.fileHash = fileHash;
		this.clientID = clientID;
		this.clientPort = clientPort;
	}
	
	public String getFileName(){
		try {
			return super.getStringTag(TAG_NAME_NAME);
		} catch (TagException e) {
			return null;
		}
	}
	
	public int getFileSize() {
		try {
			return super.getDWORDTag(TAG_NAME_SIZE);
		} catch (TagException e) {
			return 0;
		}
	}
	
	public int getFileAviability() {
		try {
			return super.getDWORDTag(TAG_NAME_AVIABILITY);
		} catch (TagException e) {
			return 0;
		}
	}
	
	public int getFileCompleteSrc() {
		try {
			return super.getDWORDTag(TAG_NAME_COMPLETESRC);
		} catch (TagException e) {
			return 0;
		}
	}

	public FileHash getFileHash() {
		return fileHash;
	}

	public void setFileHash(FileHash fileHash) {
		this.fileHash = fileHash;
	}

	public ClientID getClientID() {
		return clientID;
	}

	public void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}

	public short getClientPort() {
		return clientPort;
	}

	public void setClientPort(short clientPort) {
		this.clientPort = clientPort;
	}
	
	
}
