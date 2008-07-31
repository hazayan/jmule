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
package org.jmule.core.edonkey.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMFloodException;
import org.jmule.core.net.JMuleSocketChannel;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:14 $$
 */
public interface Packet {
	
	public void setCommand(byte packetCommand);
	
	public byte getCommand();
	
	public void setProtocol(byte protocolType);
	
	public byte getProtocol();
	
	public void insertData(byte[] insertData);
	
	public void insertData(int startPos, byte[] insertData);
	
	public void insertData(ByteBuffer insertData);
	
	public void insertData(int startPos,ByteBuffer insertData);
	
	public void insertData(int insertData);
	
	public void insertData(short insertData);
	
	public void insertData(byte insertData);
	
	public byte[] getPacket();
	
	public ByteBuffer getAsByteBuffer();
	
	public int getLength();
	
	public FileHash getFileHash();
	
    public void addData(ByteBuffer data);
    
    public void readPacket(JMuleSocketChannel connection) throws IOException,JMEndOfStreamException, InterruptedException,JMFloodException;
    
    public void clear();
}
