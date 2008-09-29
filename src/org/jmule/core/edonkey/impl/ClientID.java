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
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/29 19:11:06 $$
 */
public class ClientID {
	
	private byte[] data = new byte[4];
	
	public ClientID(byte[] clientID){
		
		for(int i = 0;i<clientID.length;i++)
			
			this.data[i] = clientID[i];
		
	}
	
	public ClientID(String clientID){
		
		data = (Convert.stringIPToArray(clientID));
		
	}
	
	public byte[] getClientID() {
		
		return data.clone();
		
	}
	
	public boolean isHighID() {
		
		return !(data[3]==0);
		
	}

	public String getAsString() {
		return Convert.byteToInt(data[0])+"."+Convert.byteToInt(data[1])+"."+Convert.byteToInt(data[2])+"."+Convert.byteToInt(data[3]);
	}
	
	public String toString() {
		
		return Convert.intToLong( hashCode() ) + "";
		
	}
	

	public int hashCode() {
		
		long num = data[0];
		
		num+=Math.pow(2, 8)*data[1];
		
		num+=Math.pow(2, 16)*data[2];
		
		num+=Math.pow(2, 24)*data[3];
		
		return Convert.longToInt(num);
	}
	
	public boolean equals(Object object){
		
		if (object==null) return false;
		
		if (!(object instanceof ClientID)) return false;
		
		return this.hashCode()==object.hashCode();
		
	}
		
}
