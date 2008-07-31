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
package org.jmule.core.edonkey.packet.impl;

import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBGETSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSEARCHREQ;
import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVRSTATREQ;
import static org.jmule.core.edonkey.E2DKConstants.OP_REASKFILEPING;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_REQ;

import java.net.InetSocketAddress;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.packet.UDPPacket;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:46 $$
 */
public class UDPPacketFactory {

	/**
	 * Server status request packet : request more server information : hard/soft limits, users, files.
	 * 
	 * @param clientTime
	 * 
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x96</td>
	 *       <td>The value of the OP_GLOBSERVSTATREQ opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Challenge</td>
	 *       <td>4</td>
	 *       <td>NA</td>
	 *       <td>A unsigned integer challenge sent to the server, used for reply verification (the corresponding variable is called 'time' on the client)</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static UDPPacket getUDPStatusRequest(int clientTime,InetSocketAddress dest){
		UDPServerPacket packet = new UDPServerPacket(4,dest);
		packet.setCommand(OP_GLOBSERVRSTATREQ);
		
		packet.insertData(clientTime);
		
		return packet;
	}

	/**
	 * Request sources from server.
	 * @param dest
	 * @param fileHashSet
	 * @return
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x9A</td>
	 *       <td>The value of the OP_GLOBGETSOURCES opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID List</td>
	 *       <td>NA</td>
	 *       <td>NA</td>
	 *       <td>A list of file IDs (hashes) (each 16 byte length), The IDs are ordered one after the
	 * other without a preceding count</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPSourcesRequest(InetSocketAddress dest,FileHash... fileHashSet){
		UDPServerPacket packet = new UDPServerPacket(fileHashSet.length*16,dest);
		packet.setCommand(OP_GLOBGETSOURCES);
		for(int i = 0; i < fileHashSet.length; i++){
			packet.insertData(fileHashSet[i].getHash());
		}
		return packet;
	}
	
	/**
	 * Server description request.
	 * @param dest
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0xA2</td>
	 *       <td>The value of the OP_SERVER_DESC_REQ opcode</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPServerDescRequest(InetSocketAddress dest){
		UDPServerPacket packet = new UDPServerPacket(0,dest);
		packet.setCommand(OP_SERVER_DESC_REQ);
		return packet;
	}
	
	
	/**
	 * Server search request.
	 * @param searchString
	 * @param dest
	 * @return
	 * <table cellspacing="0" border="1" cellpadding="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xE3</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x98 or 0x92</td>
	 *       <td>The value of the OP_GLOBSEARCHREQ or the OP_GLOBSEARCHREQ2 opcodes respectively</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Search request parameters</td>
	 *       <td>Varies</td>
	 *       <td>NA</td>
	 *       <td>Same as the search request message parameters in the Client to Server
	 * TCP communication </td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPSearchPacket(String searchString,InetSocketAddress dest){
		UDPServerPacket packet = new UDPServerPacket(1+2+searchString.length(),dest);
		
		packet.setCommand(OP_GLOBSEARCHREQ);
		
		packet.insertData((byte)0x01);
		
		packet.insertData((short)searchString.length());
		packet.insertData(searchString.getBytes());
		return packet;
	}
	
	/**
	 * Re-ask file packet.
	 * @param fileHash
	 * @param dest
	 * @return
	 * <table cellpadding="0" border="1" cellspacing="0">
	 *   <thead>
	 *     <tr>
	 *       <th>Name</th>
	 *       <th>Size in bytes</th>
	 *       <th>Default value</th>
	 *       <th>Comment</th>
	 *     </tr>
	 *   </thead>
	 *   <tbody>
	 *     <tr>
	 *       <td>Protocol</td>
	 *       <td>1</td>
	 *       <td>0xC5</td>
	 *       <td>-</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Size</td>
	 *       <td>4</td>
	 *       <td>-</td>
	 *       <td>The size of the message in bytes not including
	 * the header and size fields
	 * </td>
	 *     </tr>
	 *     <tr>
	 *       <td>Type</td>
	 *       <td>1</td>
	 *       <td>0x90</td>
	 *       <td>The value of the OP_REASKFILEPING opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The ID of the file which is reasked</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Source count</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>Optional. Unsigned integer, The current
	 * number of sources of the requested file</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * </table>
	 */
	public static UDPPacket getUDPReaskFilePacket(FileHash fileHash,InetSocketAddress dest){
		UDPPeerPacket packet = new UDPPeerPacket(16,dest);
		
		packet.setCommand(OP_REASKFILEPING);
		
		packet.insertData(fileHash.getHash());
		
		return packet;
	}
	

}
