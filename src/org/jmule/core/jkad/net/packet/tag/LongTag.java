/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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

import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT64;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;

/**
 * Created on May 24, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class LongTag extends Tag{

	private long tagValue;
	
	public LongTag(byte[] tagName, long value) {
		super(TAGTYPE_UINT64, tagName);
		tagValue = value;
		
	}

	public ByteBuffer getDataAsByteBuffer() {
		ByteBuffer tagHeader = getTagHeader();
		ByteBuffer result = getByteBuffer(tagHeader.capacity() + 8);
		result.put(tagHeader);
		result.putLong(tagValue);
		return result;
	}

	public Object getValue() {
		return tagValue;
	}

	public void setValue(Object object) {
		tagValue = (Integer) object;
	}

	public String toString() {
		return super.toString()+" "+tagValue;
	}
	
}
