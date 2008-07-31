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

import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.util.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:28 $$
 */
public class ClientCredit {
	private UserHash userHash;//16
	private long nUploadedLo;
	private long nDownloadedLo;
	private long nLastSeen;
	private long nUploadedHi;
	private long nDownloadedHi;
	private int nReserved3;
	private byte abySecureIdent[]; //max : 80

	public ClientCredit(byte[] abyKey, long uploadedLo, long downloadedLo,
			long lastSeen, long uploadedHi, long downloadedHi, int reserved3,
			byte[] abySecureIdent) {
		super();
		userHash = new UserHash(abyKey);
		nUploadedLo = uploadedLo;
		nDownloadedLo = downloadedLo;
		nLastSeen = lastSeen;
		nUploadedHi = uploadedHi;
		nDownloadedHi = downloadedHi;
		nReserved3 = reserved3;
		this.abySecureIdent = abySecureIdent;
	}


	public String toString() {
		String result="";
		
		result+="User Hash : "+userHash+"\n";
		result+="nUploadedLo : "+nUploadedLo+"\n";
		result+="nDownloadedLo : "+nDownloadedLo+"\n";
		result+="nLastSeen : "+nLastSeen+"\n";
		result+="nUploadedHi : "+nUploadedHi+"\n";
		result+="nDownloadedHi : "+nDownloadedHi+"\n";
		result+="nReserved3 : "+nReserved3+"\n";
		result+="abySecureIdent : "+Convert.byteToHexString(abySecureIdent," ")+"\n";
		return result;
	}


	public long getNUploadedLo() {
		return nUploadedLo;
	}


	public void setNUploadedLo(long uploadedLo) {
		nUploadedLo = uploadedLo;
	}


	public long getNDownloadedLo() {
		return nDownloadedLo;
	}


	public void setNDownloadedLo(long downloadedLo) {
		nDownloadedLo = downloadedLo;
	}


	public long getNLastSeen() {
		return nLastSeen;
	}


	public void setNLastSeen(long lastSeen) {
		nLastSeen = lastSeen;
	}


	public long getNUploadedHi() {
		return nUploadedHi;
	}


	public void setNUploadedHi(long uploadedHi) {
		nUploadedHi = uploadedHi;
	}


	public long getNDownloadedHi() {
		return nDownloadedHi;
	}


	public void setNDownloadedHi(long downloadedHi) {
		nDownloadedHi = downloadedHi;
	}


	public int getNReserved3() {
		return nReserved3;
	}


	public void setNReserved3(int reserved3) {
		nReserved3 = reserved3;
	}


	public byte[] getAbySecureIdent() {
		return abySecureIdent;
	}


	public void setAbySecureIdent(byte[] abySecureIdent) {
		this.abySecureIdent = abySecureIdent;
	}


	public UserHash getUserHash() {
		return userHash;
	}


	public void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}
	

}
