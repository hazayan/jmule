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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/11 13:05:14 $
 */
public class Int128 implements Cloneable {

	private BitSet bit_set;
	
	private int mark = -1;
	
	public Int128() {
		bit_set = new BitSet(128);
		
	}
	
	public Int128(BitSet bitSet) {
		bit_set = bitSet;
	}
	
	/**
	 * 
	 */
	public Int128(byte[] byteArray) {
		bit_set = new BitSet(128);
		
		ByteBuffer reversed = Misc.getByteBuffer(16);
		
		ByteBuffer tmp = Misc.getByteBuffer(4);

		for(int i = 0;i<16;i+=4) {
			tmp.clear();
			tmp.put(byteArray, i, 4);
			reversed.put(Convert.reverseArray(tmp.array()));
		}
		
		byteArrayToBitSet(reversed.array(), bit_set);
	}
	
	public Int128(FileHash fileHash) {
		this(fileHash.getHash());
	}
	
	public Int128(Int128 int128) {
		bit_set = (BitSet) int128.bit_set.clone();
	}
	

	public boolean getBit(int position) {
		return bit_set.get(position);
	}
	
	/**
	 * 
	 * @param position position 0 is higher value, 127 is lower
	 * @param value
	 */
	public void setBit(int position, boolean value) {
		bit_set.set(position, value);
	}
	
	public long toLong() {
		return bitSetToLong(bit_set);
	}
	
	/**
	 * Mark one bit 
	 * @param mark
	 */
	public void mark(int mark) {
		this.mark = mark;
	}
	
	/**
	 * Get marked bit
	 * @return  marked bit or -1 ( no marked bit)
	 */
	public int mark() {
		return mark;
	}
	
	public void resetMark() {
		mark = -1;
	}
	
	public boolean isMarked() {
		return mark != -1;
	}
	
	public byte[] toByteArray() {
		return toByteArray(true);
    }
	
	/**
	 * 
	 * @param begin - begin position in bytes [0..16]
	 * @return
	 */
	public long get32Bit(int begin) {
		byte[] byteArray = bitSetToByteArray(bit_set);
		ByteBuffer buffer = Misc.getByteBuffer(4);
		buffer.put(byteArray, begin, 4);
		buffer.position(0);
		buffer.put(Convert.reverseArray(buffer.array()));
		
		return Convert.intToLong(buffer.getInt(0));
	}

	/**
	 * Used in 'debug' situations
	 * @param reverse
	 * @return
	 */
	public byte[] toByteArray(boolean reverse) {
		if (reverse) {
			byte[] byteArray = bitSetToByteArray(bit_set);
		
			ByteBuffer reversed = Misc.getByteBuffer(16);
			
			ByteBuffer tmp = Misc.getByteBuffer(4);
	
			for(int i = 0;i<16;i+=4) {
				tmp.clear();
				tmp.put(byteArray, i, 4);
				reversed.put(Convert.reverseArray(tmp.array()));
			}
			
			return reversed.array();
		}
		return bitSetToByteArray(bit_set);
    }
	
	

	public String toHexString() {
		return byteToHexString(toByteArray(true));
	}
	
	public String toString() {
		return toBinaryString();
		
	}
	
	public String toBinaryString() {
		String result = "";
		int length = mark();
		if (length == -1)
			length = bit_set.size();
		else
			length++;
		for (int i = 0; i < length; i++) {

			if (getBit(i))
				result = result + "1" ;
			else
				result = result + "0" ;
		}
		
		return result;
		
	}
	
	public int getInt(int beginValue) {
	
		return 0;
	}
	
	public FileHash toFileHash() {
		return new FileHash(toByteArray());
	}
	
	public BitSet getBitSet() {
		return bit_set;
	}

	public Int128 clone() {
		return new Int128((BitSet) bit_set.clone());
	}
	
	public boolean equals(Object value) {
		if (value == null)
			return false;
		if (!(value instanceof Int128))
			return false;
		return Arrays.equals(toByteArray(), ((Int128) value).toByteArray());
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
}
