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

import static org.jmule.core.jkad.utils.Convert.bitSetToByteArray;
import static org.jmule.core.jkad.utils.Convert.bitSetToLong;
import static org.jmule.core.jkad.utils.Convert.byteArrayToBitSet;
import static org.jmule.core.utils.Convert.byteToHexString;

import java.util.Arrays;
import java.util.BitSet;

import org.jmule.core.edonkey.impl.FileHash;

/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Int128 implements Cloneable {

	private BitSet bit_set;
	
	public Int128() {
		bit_set = new BitSet(128);
		
	}
	
	public Int128(BitSet bitSet) {
		bit_set = bitSet;
	}
	
	public Int128(byte[] byteArray) {
		bit_set = new BitSet(128);
			
		byteArrayToBitSet(byteArray, bit_set);
	}
	
	public Int128(FileHash fileHash) {
		this(fileHash.getHash());
	}
	
	public Int128(Int128 int128) {
		bit_set = (BitSet) int128.bit_set.clone();
	}
	
	public void XOR(Int128 value) {
		bit_set.xor(value.bit_set);
	}
	
	public void shiftLeft(int count) {
		int dest = bit_set.size() - 1;
		int src = dest - count;
		
		while(src>=0) {
			bit_set.set(dest, bit_set.get(src));
			dest--;
			src--;
		}
		while(dest>=0) {
			bit_set.set(dest, false);
			dest--;
		}
	}
	
	public void shiftRight(int count) {
		int dest = 0;
		int src = count;
		while(src<bit_set.size()) {
			bit_set.set(dest, bit_set.get(src));
			src++;
			dest++; 
		}
		while(dest<bit_set.size()) {
			bit_set.set(dest,false);
			dest++;
		}
	}

	public boolean getBit(int position) {
		return bit_set.get(bit_set.size() - position -1 );
	}
	
	public void setBit(int position, boolean value) {
		bit_set.set(bit_set.size() - position - 1, value);
	}
	
	public long toLong() {
		return bitSetToLong(bit_set);
	}
	
	public byte[] toByteArray() {
       return bitSetToByteArray(bit_set);
    }

	public String toHexString() {
		return byteToHexString(toByteArray());
	}
	
	public String toString() {
		String result = "";
		for(int i = 0 ; i < bit_set.size() ; i++ ) {
			
			if (bit_set.get(i)) 
				result = "1" + result;
			else 
				result = "0" + result;
		}
		return result;
		
	}
	
	public FileHash toFileHash() {
		return new FileHash(toByteArray());
	}
	
	public BitSet getBitSet() {
		return bit_set;
	}
	
	public Int128 clone() {
		try {
			super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return new Int128((BitSet) bit_set.clone());
	}
	
	public boolean equals(Object value) {
		if (value == null) return false;
		if (! (value instanceof Int128)) return false;
		return Arrays.equals(toByteArray(), ((Int128) value).toByteArray());
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
}
