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

import static org.jmule.core.jkad.JKadConstants.TAGTYPE_STRING;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;

/**
 * Created on Dec 31, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class StringTag extends Tag {

	private String tagValue;
	
	public StringTag(byte[] tagName, String data) {
		super(TAGTYPE_STRING, tagName);
		tagValue = data;
	}

	public void setValue(String str) {
		tagValue = str;
	}
	
	public String getValue() {
		return tagValue;
	}
	
	public ByteBuffer getDataAsByteBuffer() {
		ByteBuffer tagHeader = getTagHeader();
		
		ByteBuffer result = getByteBuffer(tagHeader.capacity() + 2 + tagValue.getBytes().length);
		
		result.put(tagHeader);
		result.putShort(Convert.intToShort(tagValue.getBytes().length));
		result.put(tagValue.getBytes());
		return result;
	}
	
	public String toString() {
		return super.toString() + " "+tagValue;
	}

	public void setValue(Object newValue) {
		tagValue = (String) newValue;
	}


}
