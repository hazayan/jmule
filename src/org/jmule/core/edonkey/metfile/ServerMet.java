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
package org.jmule.core.edonkey.metfile;

import static org.jmule.core.edonkey.E2DKConstants.SERVERLIST_VERSION;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;


/**
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0xE0</td>
 *   </tr>
 *   <tr>
 *     <td>Server count</td>
 *     <td>4</td>
 *     <td></td>
 *   </tr>
 *   <tr>
 *     <td>Servers</td>
 *     <td>Varies</td>
 *     <td></td>
 *   </tr>
 * </tbody>
 * </table>
 * <br>
 * Server Data Block :
 *  
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>Server IP</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Server Port</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>Tag count</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>variable</td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * @author binary256
 * @version $$Revision: 1.10 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/07/15 06:59:30 $$
 */
public class ServerMet extends MetFile {
	
	private List<String> ip_list;
	private List<Integer> port_list;
	private List<TagList> tag_list;

	
	public ServerMet(String fileName) throws ServerMetException  {
		super(fileName);
		if (fileChannel==null) throw new ServerMetException("Can't open server.met file");
	}
	
	public void load() throws ServerMetException {
		try {
			fileChannel.position(0);
		} catch (IOException e) {
			throw new ServerMetException("Failed to move at position 0 in server.met");
		}
		
		ip_list = new ArrayList<String>();
		port_list = new ArrayList<Integer>();
		tag_list = new ArrayList<TagList>();
		
		byte serverListFormat;
		ByteBuffer data;
		
		data = Misc.getByteBuffer(1);
			
		try {
				
			fileChannel.read(data); 
			
			serverListFormat = data.get(0);

			//if (serverListFormat != SERVERLIST_VERSION)
			//	throw new ServerMetException("Unsupported server met file");
	
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			long serverCount = Convert.intToLong(data.getInt(0));
			
			for(long i = 0; i < serverCount; i++) {
								
				TagList tagList = new TagList();
				//Read server IP
				data = Misc.getByteBuffer(4);
				fileChannel.read(data);
				
				String remonteAddress = Convert.IPtoString(data.array());
				
				//Read server port 
				data = Misc.getByteBuffer(2);
				fileChannel.read(data);
				
				int remontePort = (Convert.shortToInt(data.getShort(0)));
				
				//Read TagList count
				data = Misc.getByteBuffer(4);
				fileChannel.read(data);
				
				int tagCount = data.getInt(0);
								
				//Load tags....
				for(int j = 0; j<tagCount; j++) {
					Tag tag = TagScanner.scanTag(fileChannel);
					if (tag != null)
						tagList.addTag(tag);
				}
				ip_list.add(remonteAddress);
				port_list.add(remontePort);
				tag_list.add(tagList);
				
				
			}
			
		 } catch(Throwable exception) {
			 
			 throw new ServerMetException("Unknown file format");
			 
		 }
	}
	
	public void store() throws ServerMetException {
		try {
			fileChannel.close();
			file.delete();
			fileChannel = new RandomAccessFile(file,"rws").getChannel();
			fileChannel.position(0);
			ByteBuffer data;
		
			data = Misc.getByteBuffer(1);
			data.put(SERVERLIST_VERSION);
			data.position(0);
			fileChannel.write(data);
				
			long count = ip_list.size();
			
			//for(Server server : serverList) 
			//	if (server.isStatic()) count++;
			
			setServersCount(count);
			for (int i = 0; i < count; i++) {
				writeServer(ip_list.get(i), port_list.get(i), tag_list.get(i));
			}
		}
	
		catch(IOException ioe) {
		  
		  throw new ServerMetException("IOException : " + ioe);
		}
		
	}
	
	/**
	 * Obtain server count from fileChannel.
	 * @param fileChannel
	 * @return
	 * @throws IOException
	 */
	private long getServersCount() throws IOException {
		ByteBuffer data;
		data = Misc.getByteBuffer(4);
		fileChannel.position(1);
		fileChannel.read(data);
		return Convert.intToLong(data.getInt(0));
	}
	
	/**
	 * Set server count in fileChannel.
	 * @param fileChannel
	 * @param serverCount
	 * @throws IOException
	 */
	private void setServersCount(long serverCount) throws IOException {
		ByteBuffer data = Misc.getByteBuffer(4);
		data.putInt(Convert.longToInt(serverCount));
		data.position(0);
		fileChannel.position(1);
		fileChannel.write(data);
	}
	
	/**
	 * Write one Server object in fileChannel at current position.
	 * @param fileChannel
	 * @param server
	 * @throws IOException
	 */
	private void writeServer(String serverIP, int serverPort, TagList tagList) throws IOException {
		ByteBuffer data;
		data = Misc.getByteBuffer(4);
		
		data.put(Convert.stringIPToArray(serverIP));
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(2);
		data.putShort(Convert.intToShort(serverPort));
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(4);
		data.putInt(tagList.size());
		data.position(0);
		fileChannel.write(data);
		
		
		for(Tag tag : tagList) {
			data = tag.getAsByteBuffer();
			data.position(0);
			fileChannel.write(data);
		}
		
	  }

	public List<String> getIPList() {
		return ip_list;
	}
	
	public List<Integer> getPortList() {
		return port_list;
	}
	
	public List<TagList> getTagList() {
		return tag_list;
	}
	
	public void setIPList(List<String> ipList) {
		this.ip_list = ipList;
	}

	public void setPortList(List<Integer> portList) {
		this.port_list = portList;
	}
	
	public void setTagList(List<TagList> tagList) {
		this.tag_list = tagList;
	}
}
