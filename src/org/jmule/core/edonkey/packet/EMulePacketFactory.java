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

import static org.jmule.core.edonkey.E2DKConstants.OP_EMULEHELLOANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_REQUESTSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.*;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.packet.impl.EMuleExtendedTCPPacket;
import org.jmule.util.Convert;


/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/05/09 14:09:23 $$
 */
public class EMulePacketFactory{

	/**
	 * Create eMule hello answer packet.
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
	 *       <td>0x02</td>
	 *       <td>The value of the OP EMULEINFOANSWER
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>eMule Info
	 * fields</td>
	 *       <td>&nbsp;</td>
	 *       <td>&nbsp;</td>
	 *       <td>This message has the same fields as an eMule
	 * info message.</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static EMuleExtendedTCPPacket getPeerHelloAnswerPacket() {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(1+1+4);
		packet.setCommand(OP_EMULEHELLOANSWER);

		packet.insertData((byte)0x42);
		packet.insertData((byte)0x42);
		packet.insertData((int)0);
		return packet;
	}
	
	/**
	 * Create Queue ranking packet.
	 * @param position position in queue
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
	 *       <td>0x60</td>
	 *       <td>The value of the OP_QUEUERANKING
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Queue position</td>
	 *       <td>2</td>
	 *       <td>NA</td>
	 *       <td>The position of the client in the queue</td>
	 *     </tr>
	 *     <tr>
	 *       <td>Buffer</td>
	 *       <td>10</td>
	 *       <td>0</td>
	 *       <td>10 zero bytes, purpose unknown</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 */
	public static EMuleExtendedTCPPacket getQueueRankingPacket(int position) {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(2+10);
		packet.setCommand(OP_EMULE_QUEUERANKING);
		packet.insertData(Convert.intToShort(position));

		byte ZArray[] = new byte[10];
		for(int i = 0;i<ZArray.length;i++) ZArray[i] = 0;
		packet.insertData(ZArray);
		return packet;
	}
	
	/**
	 * Create sources request packet.
	 * @param fileHash hash of file which sources are requested.
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
	 *       <td>0x81</td>
	 *       <td>The value of the OP_ REQUESTSOURCES
	 * opcode</td>
	 *     </tr>
	 *     <tr>
	 *       <td>File ID</td>
	 *       <td>16</td>
	 *       <td>NA</td>
	 *       <td>The file ID of the required file</td>
	 *     </tr>
	 *   </tbody>
	 */
	public static EMuleExtendedTCPPacket getSourcesRequestPacket(FileHash fileHash) {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(16);
		packet.setCommand(OP_REQUESTSOURCES);
		packet.insertData(fileHash.getHash());
		
		return packet;
	}
	
	public static EMuleExtendedTCPPacket getSecureIdentificationPacket(byte[] challenge, boolean isPublicKeyNeeded) {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(5);
		packet.setCommand(OP_SECIDENTSTATE);
		if (isPublicKeyNeeded)
			packet.insertData((byte)2);
		else
			packet.insertData((byte)1);
		packet.insertData(challenge);
		return packet;
	}
	
	public static EMuleExtendedTCPPacket getPublicKeyPacket(byte[] publicKey) {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(1+76);
		
		packet.setCommand(OP_PUBLICKEY);
		packet.insertData((byte)publicKey.length);
		packet.insertData(publicKey);
		
		return packet;
	}
	
	public static EMuleExtendedTCPPacket getSignaturePacket(byte[] signature) {
		EMuleExtendedTCPPacket packet = new EMuleExtendedTCPPacket(1 + 48);
		packet.setCommand(OP_SIGNATURE);
		packet.insertData((byte)signature.length);
		packet.insertData(signature);
		return packet;
	}
	
}
