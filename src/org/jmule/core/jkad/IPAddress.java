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
package org.jmule.core.jkad;

import static org.jmule.core.utils.Convert.IPtoString;
import static org.jmule.core.utils.Convert.byteToInt;
import static org.jmule.core.utils.Convert.reverseArray;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class IPAddress {
	
	private byte address[] = null;
	
	public IPAddress(InetSocketAddress address) {
		this.address = address.getAddress().getAddress();
		
		this.address = reverseArray(this.address);
	}
	
	public IPAddress() {
		address = new byte[]{0,0,0,0};
	}
	
	public IPAddress(byte[] address) {
		this.address = address;
	}
	
	public byte[] getAddress() {
		return address;
	}

	public boolean equals(IPAddress address) {
		return Arrays.equals(this.address, address.getAddress());
	}
	
	public boolean equals(InetSocketAddress address) {
		return Arrays.equals(this.address, address.getAddress().getAddress());
	}
	
	public int hashCode() {
		return IPtoString(address).hashCode();
	}
	
	public String toString() {
		String result = "";
		
		result += byteToInt(address[3]) + ".";
		result += byteToInt(address[2]) + ".";
		result += byteToInt(address[1]) + ".";
		result += byteToInt(address[0]) ;
		return result;
	}
	
}
