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

import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT16;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;

/**
 * Created on Jan 1, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class ShortTag extends Tag {

	private short tagValue;
	
	public ShortTag(byte[] tagName, short tagValue) {
		super(TAGTYPE_UINT16, tagName);
		setValue(tagValue);
	}
	
	public ByteBuffer getDataAsByteBuffer() {
		ByteBuffer tagHeader = getTagHeader();
		
		ByteBuffer result = getByteBuffer(tagHeader.capacity() + 2);
		result.put(tagHeader);
		result.putShort(tagValue);

		return result;
	}

	public Short getValue() {
		return tagValue;
	}

	public void setValue(short tagValue) {
		this.tagValue = tagValue;
	}

	public String toString() {
		return super.toString() + " "+tagValue;
	}

	public void setValue(Object newValue) {
		tagValue = (Short) newValue;
	}
	
}
