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
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 17:12:34 $$
 */
public class Misc {
	
	public static final int    INFINITY_AS_INT = 31536000;
	
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
			
			if (result1 instanceof Float) {
				float int1 = (Float)result1;
				float int2 = (Float)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}
			
			if (result1 instanceof Double) {
				double int1 = (Double)result1;
				double int2 = (Double)result2;
				int result = 0;
				if (int1>int2) result = 1;
				if (int1<int2) result = -1;
				if (order)
					return result;
				else
					return reverse(result);
			}

			if (result1 instanceof Boolean) {
				boolean b1 = (Boolean)result1;
				boolean b2 = (Boolean)result2;
				int result = 0;
				if ((b1==true)&&(b2==false)) result = 1;
				if ((b1==false)&&(b2==true)) result = -1;
				
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
	
	public static final String NO_EXTENSION = "no extension";
	
	public static String getFileExtension(String fileName) {
		int id = fileName.length()-1;
		while(id>0) { 
			if (fileName.charAt(id)=='.') break;
			id--;
		}
		String extension = NO_EXTENSION;
		if (id!=0)
			extension = fileName.substring(id+1, fileName.length());
	
		return extension;
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
	
	public static int getPartCount(long fileSize) {
		
		if (fileSize<PARTSIZE) return 0;
		
		int partCount = (int)(fileSize/PARTSIZE);
		if (fileSize%PARTSIZE !=0) partCount++;
		return partCount;
	}
	
}
