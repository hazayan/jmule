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

import static org.jmule.core.edonkey.E2DKConstants.CREDITFILE_VERSION;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Hashtable;

import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.util.Convert;
import org.jmule.util.Misc;
/**
 * Clients.met format
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *  <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0x12</td>
 *   </tr>
 *   <tr>
 *     <td>Number of entries</td>
 *     <td>4</td>
 *     <td>&nbsp; </td>
 *  </tr>
 *  </tbody>
 * </table>
 * <br>
 * One entry format : 
 * 
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * 	<tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>User Hash</td>
 *     <td>16</td>
 *   </tr>
 *   <tr>
 *     <td>nUploadedLo</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nDownloadedLo</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nLastSeen</td>
 *    <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nUploadedHi</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nDownloadedHi</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>nReserved3</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>nKeySize</td>
 *     <td>1</td>
 *   </tr>
 *   <tr>
 *     <td>abySecureIdent</td>
 *     <td><nKeySize></td>
 *   </tr>
 * </tbody>
 * </table>
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:27 $$
 */
public class ClientsMet extends MetFile {

	public ClientsMet(String fileName) throws ClientsMetException {
		super(fileName);
		if (fileChannel==null)
			throw new ClientsMetException("Failed to open "+fileName);
	}

	public Hashtable<UserHash,ClientCredit> loadFile() throws FileNotFoundException,IOException,ClientsMetException {
		fileChannel.position(0);
		Hashtable<UserHash,ClientCredit> result = new Hashtable<UserHash,ClientCredit>();
		ByteBuffer data;
		
		data = Misc.getByteBuffer(1);
		fileChannel.read(data);
		short file_version = data.get(0);
		
		if (file_version!=CREDITFILE_VERSION) 
			throw new ClientsMetException("Unknow file format");
		
		data = Misc.getByteBuffer(4);
		fileChannel.read(data);
		//data.position(0);
		long count = Convert.intToLong(data.getInt(0));
		for(long i = 0; i<count; i++) {
			byte abyKey[] = new byte[16];
			long nUploadedLo, nDownloadedLo,nLastSeen,nUploadedHi,nDownloadedHi;
			byte abySecureIdent[];
			int nKeySize;
			int nReserved3;
			
			data = Misc.getByteBuffer(16);
			fileChannel.read(data);
			data.position(0);
			data.get(abyKey);
			
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			nUploadedLo = Convert.intToLong(data.getInt(0));
			
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			nDownloadedLo = Convert.intToLong(data.getInt(0));
			
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			nLastSeen = Convert.intToLong(data.getInt(0));
			
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			nUploadedHi = Convert.intToLong(data.getInt(0));
			
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			nDownloadedHi = Convert.intToLong(data.getInt(0));
			
			data = Misc.getByteBuffer(2);
			fileChannel.read(data);
			nReserved3 = Convert.shortToInt(data.getShort(0));
			
			data = Misc.getByteBuffer(1);
			fileChannel.read(data);
			nKeySize = Convert.byteToInt(data.get(0));
			
			abySecureIdent = new byte[nKeySize];
			data = Misc.getByteBuffer(nKeySize);
			fileChannel.read(data);
			data.position(0);
			data.get(abySecureIdent);
			
			ClientCredit cc = new ClientCredit(abyKey,nUploadedLo,nDownloadedLo,nLastSeen,nUploadedHi,nDownloadedHi,nReserved3,abySecureIdent);
			result.put(cc.getUserHash(), cc);
		}
		return result;
	}
	
	public void writeFile(Collection<ClientCredit> clientsCredit) throws IOException {
		fileChannel.position(0);
		ByteBuffer data;
		
		data = Misc.getByteBuffer(1);
		data.put(CREDITFILE_VERSION);
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(4);
		data.putInt(clientsCredit.size());
		data.position(0);
		fileChannel.write(data);
		
		for(ClientCredit credit : clientsCredit) {
			data = Misc.getByteBuffer(16);
			data.put(credit.getUserHash().getUserHash());
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(4);
			data.putInt(Convert.longToInt(credit.getNUploadedLo()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(4);
			data.putInt(Convert.longToInt(credit.getNDownloadedLo()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(4);
			data.putInt(Convert.longToInt(credit.getNUploadedHi()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(4);
			data.putInt(Convert.longToInt(credit.getNDownloadedHi()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(2);
			data.putShort(Convert.intToShort(credit.getNReserved3()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(1);
			data.put(Convert.intToByte(credit.getAbySecureIdent().length));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(credit.getAbySecureIdent().length);
			data.put(credit.getAbySecureIdent());
			data.position(0);
			fileChannel.write(data);
			
		}
	}
}
