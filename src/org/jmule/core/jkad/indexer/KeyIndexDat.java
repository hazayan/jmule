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


/**
 * Load/Store data in key_index.dat . 
 * Created on Jan 28, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class KeyIndexDat {
/*
	public static List<KeywordIndex> loadFile(String fileName) throws Throwable {
		List<KeywordIndex> list = new LinkedList<KeywordIndex>();
		
		FileChannel channel = new RandomAccessFile(fileName,"r").getChannel();
		
		channel.position(4 + 4 + 16);
		ByteBuffer data;
			
		data = Utils.getByteBuffer(4);
		channel.read(data);
		
		int keywordCount = data.getInt(0);
		
		for (int i = 0; i < keywordCount; i++) {
			data = Utils.getByteBuffer(16);
			channel.read(data);
			
			KeywordIndex keywordIndex = new KeywordIndex(new Int128(data.array()));
			
			data = Utils.getByteBuffer(4);
			channel.read(data);
			int sourceCount = data.getInt(0);
			
			for(int source = 1 ;source <=sourceCount;source++) {
				data = Utils.getByteBuffer(16);
				channel.read(data);
				
				Int128 sourceID = new Int128(data.array());
				
				data = Utils.getByteBuffer(4);
				channel.read(data);
				int sourceEntry = data.getInt(0);
				for (int entry = 0; entry < sourceEntry; entry++) {
					data = Utils.getByteBuffer(4);
					channel.read(data); // life time
					
					data = Utils.getByteBuffer(4);
					channel.read(data);
					int names = data.getInt(0);
					
					for(int ncount = 1;ncount<=names;ncount++) {
						data = Utils.getByteBuffer(2);
						channel.read(data);
						data = Utils.getByteBuffer(data.getShort(0));
						channel.read(data);
						String name = new String(data.array());
						
						keywordIndex.addName(sourceID, name);
						
						data = Utils.getByteBuffer(4);
						channel.read(data); // popularity index
						
					}
					
					data = Utils.getByteBuffer(4);
					channel.read(data); 
					int ipCount = data.getInt(0);
					for(int k = 0;k<ipCount;k++) {
						data = Utils.getByteBuffer(4);
						channel.read(data);
						
						keywordIndex.addPublisher(sourceID, new IPAddress(data.array()));
						
						data = Utils.getByteBuffer(4);
						channel.read(data); // time
					}
					
					data = Utils.getByteBuffer(1);
					channel.read(data);
					int tagCount = data.get(0);
					TagList tagList = new TagList();
					for(int k2 = 1;k2<=tagCount;k2++) {
						Tag tag = TagScanner.scanTag(channel);
						tagList.addTag(tag);
					}
					keywordIndex.addTagList(sourceID, tagList);
				}
					
			}
			list.add(keywordIndex);
		}
		
		return list;
	}*/
	
}
