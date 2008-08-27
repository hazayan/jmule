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
package org.jmule.core.edonkey.packet.tag;

import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.packet.tag.impl.ExtendedTag;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.util.Misc;

/**
 * Created on Aug 27, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/08/27 17:03:04 $
 */
public class TagReader {

	private static Set<Byte> standard_tags = new HashSet<Byte>();
	
	static {
		standard_tags.add(E2DKConstants.TAG_TYPE_DWORD);
		standard_tags.add(E2DKConstants.TAG_TYPE_STRING);
	}
	
	public static Tag readTag(ByteBuffer data) {
		int position = data.position();
		byte tag_header = data.get();
		data.position(position);
		Tag tag = null;
		
		if (standard_tags.contains(tag_header)) 
			tag = new StandardTag();
		 else 
			tag = new ExtendedTag();
		
		tag.extractTag(data);
		
		return tag;
	}
	
	/**
	 * Load 1 standard tag from file channel 
	 * @param fileChannel
	 * @return
	 * @throws IOException
	 */
	public static StandardTag loadStandardTag(FileChannel fileChannel) throws IOException{
		ByteBuffer data;
		data = Misc.getByteBuffer(1);
		fileChannel.read(data);
		byte tagType = data.get(0);

		data =  Misc.getByteBuffer(2);
		fileChannel.read(data);
		short metaTagLength = data.getShort(0);

		data = Misc.getByteBuffer(metaTagLength);
		fileChannel.read(data);

		StandardTag tag = new StandardTag(tagType, data.array());
		
		switch (tagType) {
		
		case TAG_TYPE_STRING: {
			data = Misc.getByteBuffer(2);
			fileChannel.read(data);
			short strLength = data.getShort(0);
			
			data = Misc.getByteBuffer(strLength);
			fileChannel.read(data);
			
			tag.insertString(new String(data.array()));
			
			break;
		}
		
		case TAG_TYPE_DWORD : {
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			tag.insertDWORD(data.getInt(0));
			
			break;
		}
		
		default : {
			break;
		}
		
		}
		return tag;
	}
	
}
