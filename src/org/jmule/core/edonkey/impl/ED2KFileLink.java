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
package org.jmule.core.edonkey.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Examples :
 * ed2k://|file|NAME|SIZE|MD4-HASH|p=HASH1:HASH2:...|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|h=HASH|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|/|sources,<IP:Port>,<IP:Port>,...|/
 * @author binary
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:34 $$
 */
public class ED2KFileLink {

	private String fileName;
	
	private long fileSize;
	
	private FileHash fileHash;
	

	
	public ED2KFileLink(String fileLink){
		
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|(.*)\\|(.*)\\|([0-9]*)\\|(.*)\\|.*");
		
		m = s.matcher(fileLink);

		if (m.matches()) {
			
			this.fileName=m.group(2);
			
			this.fileSize=Long.valueOf(m.group(3)).longValue();
			
			this.fileHash = new FileHash(m.group(4));
	    }
		
	}
	
	public long getFileSize() {
		
		return this.fileSize;
		
	}
	
	public String getFileName() {
		
		return this.fileName;
		
	}
	
	public String toString(){
		
		return "ed2k://file|"+fileName+"|"+fileSize+"|"+fileHash+"|/";
		
	}
	
	public FileHash getFileHash() {
		
		return this.fileHash;
		
	}
}
