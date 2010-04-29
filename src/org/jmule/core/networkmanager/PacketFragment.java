/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2010 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.networkmanager;

import java.nio.ByteBuffer;

import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Created on Mar 4, 2010
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2010/04/29 10:54:45 $
 */
public class PacketFragment {

	private JMConnection connection;
	private ByteBuffer content;
	private int packetLimit;
	private long lastUpdate;
	
	public String toString() {
		return "packetLimit : " + packetLimit + " " + " lastUpdate : " + lastUpdate + " content:: capacity :: " + content.capacity();
	}
	
	public PacketFragment(JMConnection connection, long size) {
		this.connection = connection;
		content = Misc.getByteBuffer(size);
		lastUpdate = System.currentTimeMillis();
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public boolean isFullUsed() {
		boolean value = content.position() >= getPacketLimit();
		if (content.capacity()==0)
			value = false;
		return value;
	}
	
	public JMConnection getConnection() {
		return connection;
	}
	
	public ByteBuffer getContent() {
		return content;
	}
	
	public void concat(PacketFragment fragment) {
		long size = getPacketLimit() + fragment.getPacketLimit();
		ByteBuffer result = Misc.getByteBuffer(size);
		
		int l;
		l = getPacketLimit();
//		if (l>content.capacity())
//			l--;
		result.put(content.array(), 0, l);
		
		l = fragment.getPacketLimit();
//		if (l>fragment.getContent().capacity())
//			l--;
		result.put(fragment.getContent().array(),0, l);
		
		setPacketLimit(result.position());
		//ByteBuffer oldContent = content;
		content = result;
		content.position(0);
		
		lastUpdate = System.currentTimeMillis();
		
		fragment.clear();
		//oldContent.reset();
		//oldContent = null;
	}
	
	public byte getHead() {
		content.position(0);
		byte result = content.get();
		content.position(0);
		return result;
	}
	
	public int getLength() {
		content.position(0);
		ByteBuffer len = Misc.getByteBuffer(4);
		content.position(0);
		return Convert.byteToInt(len.array());
	}
	
	
	public void moveUnusedBytes(int beginPos) {
		
		if (beginPos > getPacketLimit()) {
			
			return;
		}
		
		int moveFragmentSize = (getPacketLimit()- beginPos);
		
//		System.out.println("moveUnusedBytes :: beginPos :: " + beginPos);
//		System.out.println("moveUnusedBytes :: getPacketLimit :: " + getPacketLimit());
//		System.out.println("moveUnusedBytes :: moveFragmentSize    :: " + moveFragmentSize);
		
		if (moveFragmentSize <= 0) {
			return;
		}
		
		if (moveFragmentSize == 0) {
//			System.out.println("moveFragmentSize=0");
			return;
		}
		
		content.position(beginPos);
		
		ByteBuffer temp = Misc.getByteBuffer(moveFragmentSize );
		
//		System.out.println("moveFragmentSize :: " + moveFragmentSize + " ::  capacity :: " + content.capacity()+"  ::  position :: " + content.position());
		
		content.get(temp.array());
		
//		int end = 10;
//		if(end > temp.capacity())
//			end = temp.capacity();
//		System.out.println("moveUnusedBytes :: move bytes :: " + Convert.byteToHexString(temp.array(), 0, end));
		
		content.clear();
		content.position(0);
		
		temp.position(0);
		
		content.put(temp);
		setPacketLimit(content.position());
		content.position(0);
		
		lastUpdate = System.currentTimeMillis();
		
		//temp.clear();
		//temp = null;
	}
	
	public void clear() {
		content.clear();
		content.compact();
		content.rewind();
		content.limit(0);
		content = null;
	}

	public int getPacketLimit() {
		return packetLimit;
	}

	public void setPacketLimit(int packetLimit) {
		this.packetLimit = packetLimit;
	}
	
}
