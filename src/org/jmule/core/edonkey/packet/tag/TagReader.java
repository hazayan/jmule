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
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXBYTE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXDWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_LONG;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_SHORT_BEGIN;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_SHORT_END;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.packet.tag.impl.ExtendedTag;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.utils.Misc;

/**
 * Created on Aug 27, 2008
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:09:53 $
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
	
	public static Tag readTag(FileChannel fileChannel) throws IOException {
		long position = fileChannel.position();
		ByteBuffer header = Misc.getByteBuffer(1);
		fileChannel.read(header);
		fileChannel.position(position);
		byte tag_header = header.get(0);
		
		boolean isStandardTag = standard_tags.contains(tag_header);
		
		ByteBuffer tagType = Misc.getByteBuffer(1);
	    fileChannel.read(tagType);
		if (isStandardTag) {
			
	   	    ByteBuffer metaTagLength = Misc.getByteBuffer(2);
			fileChannel.read(metaTagLength);
			metaTagLength.position(0);
			ByteBuffer metaTag = Misc.getByteBuffer(metaTagLength.getShort(0));
			fileChannel.read(metaTag);
			metaTag.position(0);
			switch(tagType.get(0)) {
				case TAG_TYPE_STRING : {
					ByteBuffer strLength = Misc.getByteBuffer(2);
					fileChannel.read(strLength);
					strLength.position(0);
					ByteBuffer str = Misc.getByteBuffer(strLength.getShort(0));
					fileChannel.read(str);
					str.position(0);					
					ByteBuffer tag_data = Misc.getByteBuffer(1 + 2 + metaTagLength.get(0) + 2 + strLength.getShort(0));
					tag_data.put(TAG_TYPE_STRING);
					tag_data.put(metaTagLength);
					tag_data.put(metaTag);
					tag_data.put(strLength);
					tag_data.put(str);
					
					return new StandardTag(metaTag,tag_data);
				}
				
				case TAG_TYPE_DWORD : {
					ByteBuffer dword_tag = Misc.getByteBuffer(4);
					fileChannel.read(dword_tag);
					dword_tag.position(0);
					
					ByteBuffer tag_data = Misc.getByteBuffer(1 + 2 + metaTagLength.getShort(0) + 4);
					tag_data.put(TAG_TYPE_DWORD);
					tag_data.put(metaTagLength);
					tag_data.put(metaTag);
					tag_data.put(dword_tag);
					
					return new StandardTag(metaTag,tag_data);
				}
			}
		} else {
			byte type = tagType.get(0);
			if (type == TAG_TYPE_EXSTRING_LONG) {
				ByteBuffer metaTagName = Misc.getByteBuffer(1);
				fileChannel.read(metaTagName);
				metaTagName.position(0);
				
				ByteBuffer strLen = Misc.getByteBuffer(2);
				fileChannel.read(strLen);
				strLen.position(0);
				ByteBuffer str = Misc.getByteBuffer(strLen.getShort(0));
				fileChannel.read(str);
				str.position(0);
				
				ByteBuffer tag_data = Misc.getByteBuffer(1 + 1 + 2 + strLen.getShort(0));
				tag_data.put(TAG_TYPE_EXSTRING_LONG);
				tag_data.put(metaTagName);
				tag_data.put(strLen);
				tag_data.put(str);
				
				return new ExtendedTag(metaTagName,tag_data);
			}
			
			 if ((type>=TAG_TYPE_EXSTRING_SHORT_BEGIN)&&(type<=TAG_TYPE_EXSTRING_SHORT_END)){
				 ByteBuffer metaTagName = Misc.getByteBuffer(1);
				 fileChannel.read(metaTagName);
				 metaTagName.position(0);
				 short strLength = (short)(type - TAG_TYPE_EXSTRING_SHORT_BEGIN);
				 ByteBuffer tag_data = Misc.getByteBuffer(1 + 1 + strLength);
				 tag_data.put(type);
				 tag_data.put(metaTagName);
				 fileChannel.read(tag_data);

				 return new ExtendedTag(metaTagName,tag_data);
			 }
			 
			 if (type == TAG_TYPE_EXBYTE) {
				 ByteBuffer metaTagName = Misc.getByteBuffer(1);
				 fileChannel.read(metaTagName);
				 metaTagName.position(0);
				 ByteBuffer tag_data = Misc.getByteBuffer(1 + 1 + 1);
				 tag_data.put(type);
				 tag_data.put(metaTagName);
				 fileChannel.read(tag_data);
				 
				 return new ExtendedTag(metaTagName,tag_data);
			 }
			 
			 if (type == TAG_TYPE_EXWORD) {
				 ByteBuffer metaTagName = Misc.getByteBuffer(1);
				 fileChannel.read(metaTagName);
				 metaTagName.position(0);
				 
				 ByteBuffer tag_data = Misc.getByteBuffer(1 + 1 + 2);
				 tag_data.put(type);
				 tag_data.put(metaTagName);
				 fileChannel.read(tag_data);
				 
				 return new ExtendedTag(metaTagName,tag_data);
			 }
			 
			 if (type == TAG_TYPE_EXDWORD) {
				 ByteBuffer metaTagName = Misc.getByteBuffer(1);
				 fileChannel.read(metaTagName);
				 metaTagName.position(0);
				 ByteBuffer tag_data = Misc.getByteBuffer(1 + 1 + 4);
				 
				 tag_data.put(type);
				 tag_data.put(metaTagName);
				 fileChannel.read(tag_data);
				 
				 return new ExtendedTag(metaTagName,tag_data);
			 }
		}
		
		return null;
	}
}
