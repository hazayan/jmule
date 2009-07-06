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

import static org.jmule.core.jkad.JKadConstants.TAGTYPE_BSOB;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_HASH;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_STRING;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT16;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT32;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT64;
import static org.jmule.core.jkad.JKadConstants.TAGTYPE_UINT8;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jmule.core.jkad.Hash;
import org.jmule.core.jkad.net.packet.CorruptedPacketException;
import org.jmule.core.utils.Convert;

/**
 * Created on Jan 1, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class TagScanner {
	
	public static Tag scanTag(ByteBuffer data) {
		
		byte tagType;
		short tagNameLength;
		byte[] tagName;
		
		tagType = data.get();
		tagNameLength = data.getShort();
		
		tagName = new byte[Convert.shortToInt(tagNameLength)];
		data.get(tagName);
		
		try {
		switch(tagType) {
				case TAGTYPE_HASH : 
					byte[] hash = new byte[16];
					data.get(hash);
					Hash tag_hash_data = new Hash(hash);
					return new HashTag(tagName, tag_hash_data);
	
				case TAGTYPE_STRING :
					int str_length = Convert.shortToInt(data.getShort());
					byte str_data[] = new byte[str_length];
					data.get(str_data);
					String str = new String(str_data);
					return new StringTag(tagName, str);
					
				case TAGTYPE_UINT8 : 
					byte tag_data = data.get();
					return new ByteTag(tagName, tag_data);
					
				case TAGTYPE_UINT16 : 
					short tag_short_data = data.getShort();
					return new ShortTag(tagName, tag_short_data);
					
				case TAGTYPE_UINT32 :
					int tag_int_data = data.getInt();
					return new IntTag(tagName, tag_int_data);
					
				case TAGTYPE_UINT64 :
					long long1 = data.getLong();
					
					return new LongTag(tagName, long1);
					
				case TAGTYPE_BSOB : 
					byte size = data.get();
					ByteBuffer raw_data = getByteBuffer(size+1);
					raw_data.put(size);
					data.get(raw_data.array(), 1, size);
					return new BSOBTag(tagName, raw_data);
			}
		}catch(Throwable t) {
			
			t.printStackTrace();
			return null;
		}
		return null;
		
	}
	
	public static Tag scanTag(FileChannel file) {
		try {
			byte tagType;
			int tagNameLength;
			byte[] tagName;
			
			ByteBuffer data = getByteBuffer(1);
			file.read(data);
			
			tagType = data.get(0);
			
			data = getByteBuffer(2);
			file.read(data);
			
			tagNameLength = Convert.shortToInt(data.getShort(0));
			//tagName = new byte[tagNameLength];
			data = getByteBuffer(tagNameLength);
			file.read(data);
			tagName = data.array();
			
			switch(tagType) {
				case TAGTYPE_HASH : 
					data = getByteBuffer(16);
					file.read(data);
					byte[] hash = data.array();
					Hash tag_hash_data = new Hash(hash);
					return new HashTag(tagName, tag_hash_data);

				case TAGTYPE_STRING :
					data =  getByteBuffer(2);
					file.read(data);
					int str_length = Convert.shortToInt(data.getShort(0));
					data =  getByteBuffer(str_length);
					file.read(data);
					String str = new String(data.array());
					return new StringTag(tagName, str);
				
				case TAGTYPE_UINT8 : 
					data =  getByteBuffer(1);
					file.read(data);
					byte tag_data = data.get(0);
					return new ByteTag(tagName, tag_data);
				
				case TAGTYPE_UINT16 : 
					data =  getByteBuffer(2);
					file.read(data);
					short tag_short_data = data.getShort(0);
					return new ShortTag(tagName, tag_short_data);
				
				case TAGTYPE_UINT32 :
					data =  getByteBuffer(4);
					file.read(data);
					int tag_int_data = data.getInt(0);
					return new IntTag(tagName, tag_int_data);
					
				case TAGTYPE_UINT64 :
					data =  getByteBuffer(8);
					file.read(data);
					return new LongTag(tagName, data.getLong(0));
			}
			
		}catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
		return null;
	}
	
	public static TagList scanTagList(ByteBuffer buffer) throws CorruptedPacketException{
		byte tagCount = buffer.get();
		TagList tag_list = new TagList();
		for(int j = 0;j<tagCount;j++) {
			Tag tag = scanTag(buffer);
			if (tag == null) throw new CorruptedPacketException();
			tag_list.addTag(tag);
		}
		return tag_list;
	}

	
	
}
