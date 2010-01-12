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
package org.jmule.core.jkad.indexer;

import static org.jmule.core.jkad.JKadConstants.SRC_INDEX_VERSION;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.Int128;
import org.jmule.core.utils.Convert;

/**
 * Created on Apr 21, 2009
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/12 14:41:39 $
 */
public class SrcIndexDat {

	public static  Map<Int128, Index> loadFile(String fileName) throws Throwable {
		Map<Int128, Index> result = new Hashtable<Int128, Index>();
		FileChannel channel = new RandomAccessFile(fileName,"rw").getChannel();
		
		channel.position(4+4);
		
		ByteBuffer data;
		
		data = getByteBuffer(4);
		
		channel.read(data);
		int count = data.getInt(0);
		
		for(int i=0;i<count;i++) {
			data = getByteBuffer(16);
			channel.read(data);
			Int128 key_id = new Int128(data.array());
			data = getByteBuffer(4);
			channel.read(data);
			Index index = new Index(key_id);
			int source_count = data.getInt(0);
		
			for(int j = 0;j<source_count;j++) {
				data = getByteBuffer(16);
				channel.read(data);
				ClientID client_id = new ClientID(data.array());

				data = getByteBuffer(8);
				channel.read(data);
				long creation_time = data.getLong(0);
				data = getByteBuffer(1);
				channel.read(data);
				int tagCount = data.get(0);
				TagList tagList = new TagList();
				
				for(int k = 0;k<tagCount;k++) {
					Tag tag = TagScanner.scanTag(channel);
					
					if (tag!=null)
						tagList.addTag(tag);
				}
				Source source = new Source(client_id, tagList,creation_time);
				source.setTagList(tagList);
				index.addSource(source);
			}
			result.put(key_id, index);
		}
		
		return result;
	}
	
	public static void writeFile(String fileName, Map<Int128, Index> sourceData) throws Throwable {
		FileChannel channel = new FileOutputStream(fileName).getChannel();
		ByteBuffer data = getByteBuffer(4);
		
		data.put(SRC_INDEX_VERSION);
		data.position(0);
		channel.write(data);
		
		data.position(0);
		data.putInt(Convert.longToInt(System.currentTimeMillis()));
		data.position(0);
		channel.write(data);
		
		data.position(0);
		data.putInt(sourceData.size());
		data.position(0);
		channel.write(data);
		
		for(Int128 key : sourceData.keySet()) {
			data = getByteBuffer(16);
			data.put(key.toByteArray());
			data.position(0);
			channel.write(data);
			
			Index index = sourceData.get(key);
			data = getByteBuffer(4);
			data.putInt(index.getSourceList().size());
			data.position(0);
			channel.write(data);
			
			for(Source source : index.getSourceList()) {
				data = getByteBuffer(16);
				data.put(source.getClientID().toByteArray());
				data.position(0);
				channel.write(data);
				
				data = getByteBuffer(8);
				data.putLong(0, source.getCreationTime());
				data.position(0);
				channel.write(data);
				
				data = getByteBuffer(1);
				data.put(Convert.intToByte(source.getTagList().size()));
				data.position(0);
				channel.write(data);
				for(Tag tag : source.getTagList()) {
					ByteBuffer buffer = tag.getAsByteBuffer();
					buffer.position(0);
					channel.write(buffer);
				}
			}
		}
	}
	
}
