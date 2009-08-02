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
package org.jmule.core.jkad.routingtable;

import static org.jmule.core.jkad.JKadConstants.NODES_DAT_VERSION;
import static org.jmule.core.utils.Convert.intToShort;
import static org.jmule.core.utils.Misc.getByteBuffer;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

import org.jmule.core.jkad.ClientID;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.JKadUDPKey;
import org.jmule.core.jkad.utils.Utils;

/**
 * Created on Dec 29, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/08/02 08:01:23 $
 */
public class NodesDat {

	public static List<KadContact> loadFile(String fileName) {
		List<KadContact> result = new LinkedList<KadContact>();
		
		try {
			FileChannel channel = new RandomAccessFile(fileName,"rw").getChannel();
			
			ByteBuffer data = getByteBuffer(4);
			channel.position(4); // skip 'old' contacts count field
			
			channel.read(data); // nodes.dat version
			
			data.position(0);
			channel.read(data);
			
			int totalContacts = data.getInt(0);
			
			for(int i = 1 ; i <= totalContacts ; i++) {
				
				data = getByteBuffer(16);
				channel.read(data);
				ClientID contact_id = new ClientID(data.array());
				
				data = getByteBuffer(4);
				channel.read(data);
				byte[] ip = data.array().clone();
				//ip = Convert.reverseArray(ip);
				IPAddress address = new IPAddress(ip);
				
				data = getByteBuffer(2);
				channel.read(data);
				short udp_port = data.getShort(0);
				
				data = getByteBuffer(2);
				channel.read(data);
				short tcp_port = data.getShort(0);
				
				data = getByteBuffer(1);
				channel.read(data);
				byte contact_version = data.get(0); 
				
				data = getByteBuffer(4);
				channel.read(data);
				
				ByteBuffer data2 = getByteBuffer(4);
				channel.read(data2);
				
				JKadUDPKey udp_key = new JKadUDPKey(data.array(), data2.array());
				
				data = getByteBuffer(1);
				channel.read(data);
				
				//System.out.println("*** IsGoodAddress ** disabled");
				if (Utils.isGoodAddress(address)) {
					KadContact contact = new KadContact(contact_id, new ContactAddress(address, udp_port), tcp_port, contact_version, udp_key, data.get(0)==1 ? true : false);
					
					result.add(contact);
				}
			}
			
			channel.close();
		}catch(Throwable t) {
			t.printStackTrace();
		}
		
		return result;
	}
	
	public static void writeFile(String fileName, List<KadContact> contactList) {
		try {
			FileChannel channel = new FileOutputStream(fileName).getChannel();
			ByteBuffer data = getByteBuffer(4);
			
			channel.write(data);
			
			data.position(0);
			data.put(NODES_DAT_VERSION);
			data.position(0);
			channel.write(data);
			
			data.position(0);
			data.putInt(contactList.size());
			data.position(0);
			channel.write(data);
			
			for(KadContact contact : contactList) {
				data = getByteBuffer(16);
				data.put(contact.getContactID().toByteArray());
				data.position(0);
				channel.write(data);
				
				data = getByteBuffer(4);
				data.put(contact.getIPAddress().getAddress());
				data.position(0);
				channel.write(data);
				
				data  = getByteBuffer(2);
				data.putShort(intToShort(contact.getUDPPort()));
				data.position(0);
				channel.write(data);
				
				data  = getByteBuffer(2);
				data.putShort(intToShort(contact.getTCPPort()));
				data.position(0);
				channel.write(data);
				
				data  = getByteBuffer(1);
				data.put(contact.getVersion());
				data.position(0);
				channel.write(data);
				
				// write key
				JKadUDPKey key = contact.getKadUDPKey();
				if (key == null) {
					data  = getByteBuffer(4 + 4);
					data.position(0);
					channel.write(data);
				} else {
					data  = getByteBuffer(4 + 4);
					data.put(key.getKey());
					data.put(key.getAddress().getAddress());
					data.position(0);
					channel.write(data);
				}
				
				data = getByteBuffer(1);
				data.put((byte)(contact.isIPVerified() ? 1 : 0));
				
			}
			
			channel.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
}
