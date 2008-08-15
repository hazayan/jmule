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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Examples :
 * ed2k://|file|NAME|SIZE|MD4-HASH|p=HASH1:HASH2:...|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|h=HASH|/
 * ed2k://|file|NAME|SIZE|MD4-HASH|/|sources,<IP:Port>,<IP:Port>,...|/
 * @author binary
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/15 10:07:21 $$
 */
public class ED2KFileLink {

	private String fileName;
	
	private long fileSize;
	
	private FileHash fileHash;
	
	public ED2KFileLink(String fileName,long fileSize,FileHash fileHash) {
		
		this.fileName = fileName;
		
		this.fileSize = fileSize;
		
		this.fileHash = fileHash;
		
	}
	
	public ED2KFileLink(String fileLink){
		
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|file\\|([^|]*)\\|([0-9]*)\\|([a-h0-9A-H]*)\\|\\/");
		
		m = s.matcher(fileLink);

		if (m.matches()) {
			
			this.fileName=m.group(1);
			
			this.fileSize=Long.valueOf(m.group(2)).longValue();
			
			this.fileHash = new FileHash(m.group(3));
	    }
		
	}
	
	public static List<ED2KFileLink> extractLinks(String rawData) {
		
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|file\\|([^|]*)\\|([0-9]*)\\|([a-h0-9A-H]*)\\|\\/");
		
		m = s.matcher(rawData);
		
		List<ED2KFileLink> links = new LinkedList<ED2KFileLink>();
		
		while(m.find()) {
			
			String fileName = m.group(1);
			
			long fileSize = Long.valueOf(m.group(2)).longValue();
			
			FileHash fileHash = new FileHash(m.group(3));
		
			links.add(new ED2KFileLink(fileName,fileSize,fileHash));
			
		}
		
		return links;
		
	}
	
	public static boolean isValidLink(String link) {
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|file\\|([^|]*)\\|([0-9]*)\\|([a-h0-9A-H]*)\\|\\/");
		
		m = s.matcher(link);
		
		return m.matches();
	}
	
	public String getAsString() {
		
		return "ed2k://file|"+fileName+"|"+fileSize+"|"+fileHash+"|/";
		
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
	
	public static void main(String... args) {
		String data = "ed2k://|file|zzz1.pdf|9496920|14AA4451445C1F45117F4E4676B863DA|/fsdsdsdffed2k://|file|zzz2.pdf|9496920|A4DA4451445C1F45157F4E4676B863DA|/ed2k://|file|zzz3.pdf|9496920|14AA4451445C1F45117F4E4676B863D4|/";
		List<ED2KFileLink> list = ED2KFileLink.extractLinks(data);
		for(ED2KFileLink link : list) {
			System.out.println(link+" ");
		}
	}
}
