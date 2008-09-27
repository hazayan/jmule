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
package org.jmule.core.edonkey.packet.tag.impl;

import java.nio.ByteBuffer;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/27 13:02:50 $$
 */
public abstract class AbstractTag {
	
	protected MetaTag metaTagName = new MetaTag();
	
	protected ByteBuffer tag = null;
	
	protected byte tagType = 0;
	
	public AbstractTag() {
		
	}
	
	public AbstractTag(ByteBuffer metaTag, ByteBuffer tag) {
		metaTagName.setMetaTagName(metaTag.array());
		this.tag = tag;
		setType(tag.get(0));
	}
	
	public MetaTag getMetaTag() {
		return metaTagName;
	}

	public int getMetaNameLength() {
		return metaTagName.getMetaTagLength();
	}

	public void clear() {
		tag.clear();
		tag.compact();
		tag.rewind();
		tag.limit(0);
	}

	public byte getType() {
		return tagType;
	}

	public void setType(byte tagType) {
		this.tagType=tagType;
	}

	public int getSize() {
		return tag.capacity();
	}
	
	public byte[] getData() {
		return tag.array();
	}
	
	/** Convert Tag into string*/
	public String toString() {
		String result = "";
		byte[] tagBytes = this.getData();
		
		for (int i = 0 ; i < this.getSize() ; i++){ 
			String byteValue = Integer.toHexString(tagBytes[i]&0xFF).toUpperCase();
			result = result+" 0x"+byteValue;
		}
		return result;
	}
	
	
	
}
