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
package org.jmule.core.edonkey.impl;

import org.jmule.util.Convert;


/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:33 $$
 */
public class FileHash {
	
	private byte[] fileHash;
	
	public FileHash(byte[] fileHash){
		
		this.fileHash = fileHash;
		
	}
	
	
	public FileHash(String inputString){
		
		fileHash = new byte[16];
		
		for(int i = 0; i < fileHash.length; i++)
			
			fileHash[i] = Convert.hexToByte(inputString.charAt(i*2)+""+inputString.charAt(i*2+1));
		
	}
	
	public byte[] getHash() {
		
		return this.fileHash;
		
	}
	
	public void setHash(byte[] fileHash){
		
		this.fileHash = fileHash;
		
	}	
	
	public int length() {
		
		return this.fileHash.length;
		
	}
	
	
	public String toString() {
		
		return Convert.byteToHexString(fileHash);
		
	}
	
	public int hashCode() {
		
		return toString().hashCode();
		
	}
	
	
	public boolean equals(Object object){
		
		if (object == null) return false;
		
		if (!(object instanceof FileHash))
			return false;
		
		return this.hashCode()==object.hashCode();
		
	}
	
}
