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

import java.nio.ByteBuffer;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:26 $$
 */
public class JMuleZLib{

	public static ByteBuffer compressData(ByteBuffer inputData) {
		
		Deflater compressor = new Deflater();
		
		compressor.setInput(inputData.array());
		
		long capacity = 0;
		
		int byte_count = 0;
		
		Vector<ByteBuffer> vector = new Vector<ByteBuffer>();
		
		do {
			
			ByteBuffer tmpData = Misc.getByteBuffer(1000);
			
			byte_count = compressor.deflate(tmpData.array());
		
			tmpData.limit(byte_count);
			
			vector.add(tmpData);
			
			capacity += byte_count;
			
		} while (byte_count != 0);
		
		ByteBuffer outputData = Misc.getByteBuffer(capacity);
		
		for(ByteBuffer buffer : vector) {
			
			outputData.put(buffer.array(), 0, buffer.limit());
			
		}
		
		return outputData;
		
	}
	
	public static ByteBuffer decompressData(ByteBuffer inputData) throws DataFormatException {
		
		Inflater decompressor = new Inflater();
		
		decompressor.setInput(inputData.array());
		
		long capacity = 0;
		
		int byte_count = 0;
		
		Vector<ByteBuffer> vector = new Vector<ByteBuffer>();
		
		do {
			
			ByteBuffer tmpData = Misc.getByteBuffer(1000);
			
			byte_count = decompressor.inflate(tmpData.array());
		
			tmpData.limit(byte_count);
			
			vector.add(tmpData);
			
			capacity += byte_count;
			
		} while(byte_count !=0 );
		
		
		
		ByteBuffer outputBuffer = Misc.getByteBuffer(capacity);
		
		for(ByteBuffer buffer : vector) {
			
			outputBuffer.put(buffer.array(), 0, buffer.limit());
			
		}
		
		return outputBuffer;
		
		
	}
	
}
