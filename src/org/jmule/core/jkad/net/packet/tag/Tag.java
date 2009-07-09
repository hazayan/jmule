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
package org.jmule.core.jkad.net.packet.tag;

import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;

import org.jmule.core.edonkey.packet.tag.TagException;
import org.jmule.core.edonkey.packet.tag.impl.MetaTag;
import org.jmule.core.utils.Convert;

/**
 * Created on Dec 31, 2008 18:43:11
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/09 13:09:26 $
 */
public abstract class Tag implements org.jmule.core.edonkey.packet.tag.Tag {
	
	private byte tagType;
	private byte[] tagName;
	
	public Tag(byte tagType, byte[] tagName) {
		super();
		this.tagType = tagType;
		this.tagName = tagName;
	}
	
	protected ByteBuffer getTagHeader() {
		ByteBuffer result = getByteBuffer(1 + 2 + tagName.length);
		
		result.put(tagType);
		result.putShort(Convert.intToShort(tagName.length));
		result.put(tagName);
		
		result.position(0);
		
		return result;
	}
	
	public byte getType() {
		return tagType;
	}
	
	public void setType(byte tagType) {
		this.tagType = tagType;
	}
	
	public byte[] getTagName() {
		return tagName;
	}
	
	public void setTagName(byte[] tagName) {
		this.tagName = tagName;
	}

	/** 
	 * Return tag header + tag value
	 * @return
	 */
	public abstract ByteBuffer getDataAsByteBuffer();
	
	public String toString() {
		String result = "";
		
		result += "" + Convert.byteToHex(tagType) + " "+ Convert.byteToHexString(tagName, " 0x");
		
		return result;
	}
	
	public abstract Object getValue();
	public abstract void setValue(Object newValue);

	// compatibility with JMule tags
	public MetaTag getMetaTag() {
		return new MetaTag(getTagName());
	}
	
	public int getMetaNameLength() {
		return getTagName().length;
	}
	
	public void clear() {
		
	}
	
	public int getSize() {
		return getDataAsByteBuffer().capacity();
	}
	
	public byte[] getData() {
		return getDataAsByteBuffer().array();
	}
	
	public int getDWORD() {
		return Convert.longToInt(Long.parseLong(getValue()+""));
	}
	
	public String getString() throws TagException {
		return (String)getValue();
	}
	
	public void  insertString(String stringData) {
		setValue(stringData);
	}
	
	public void insertDWORD(int dwordData) {
		setValue(dwordData);
	}
	
	public void extractTag(ByteBuffer data) {
		
	}
	
}
