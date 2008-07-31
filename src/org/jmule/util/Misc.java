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
package org.jmule.util;

import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.jmule.core.edonkey.packet.tag.impl.StandardTag;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:24 $$
 */
public class Misc {

	public static int compareAllObjects(Object object1, Object object2,String methodName,boolean order) {

		Class cl = object1.getClass();
		Object result1 = null, result2 = null;
		try {
			Method m = cl.getMethod(methodName, null);
			result1 = m.invoke(object1, null);
			result2 = m.invoke(object2, null);
			
			if (result1 instanceof String) {
				String name1 = (String)result1;
				String name2 = (String)result2;
				int result = name1.compareTo(name2);
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Integer) {
				int int1 = (Integer)result1;
				int int2 = (Integer)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Long) {
				long int1 = (Long)result1;
				long int2 = (Long)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		return 0;
	}
	
	public static int reverse(int value) {
		if (value<0) return 1;
		if (value>0) return -1;
		return 0;
	}
	
	public static ByteBuffer getByteBuffer(long BufferSize){
		return ByteBuffer.allocate(Convert.longToInt(BufferSize)).order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws Exception
	 * @deprecated
	 */
	public static void copyFile(String source, String destination) throws Exception {
        FileChannel srcChannel = new FileInputStream(source).getChannel();
    
        FileChannel dstChannel = new FileOutputStream(destination).getChannel();
    
        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
    
        srcChannel.close();
        dstChannel.close();
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
	
	
	public static StandardTag loadStandardTag(ByteBuffer buffer){
		
		byte tagType = buffer.get();
		short metaTagLength = buffer.getShort();

		byte[] data;
		data = new byte[metaTagLength];
		buffer.get(data);

		StandardTag tag = new StandardTag(tagType, data);
		
		switch (tagType) {
		
		case TAG_TYPE_STRING: {
			short strLength = buffer.getShort();
			
			data = new byte[strLength];
			buffer.get(data);
			tag.insertString(new String(data));
			break;
		}
		
		case TAG_TYPE_DWORD : {
			tag.insertDWORD(buffer.getInt());
			break;
		}
		
		default : {
			break;
		}
		
		}
		
		return tag;
	}
	
	public static int getPartCount(long fileSize) {
		
		if (fileSize<PARTSIZE) return 0;
		
		int partCount = (int)(fileSize/PARTSIZE);
		//if (fileSize%PARTSIZE !=0) partCount;
		return partCount;
	}
	
}
