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

import static org.jmule.core.edonkey.E2DKConstants.FT_FILENAME;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.packet.tag.impl.TagException;
import org.jmule.core.edonkey.packet.tag.impl.TagList;
import org.jmule.util.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:28 $$
 */
public class KnownMetEntity {
	private int date;
	private PartHashSet partHashSet;
	private TagList tagList = new TagList();
	
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	
	public FileHash getFileHash() {
		return partHashSet.getFileHash();
	}
	
	public PartHashSet getPartHashSet() {
		return partHashSet;
	}
	public void setPartHashSet(PartHashSet partFileHash) {
		this.partHashSet = partFileHash;
	}
	public TagList getTagList() {
		return tagList;
	}
	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}

	public String toString() {
		try {
			String result =  "File Name : "+tagList.getStringTag(FT_FILENAME)+
			" Size : "+tagList.getDWORDTag(FT_FILESIZE)
			+" Hash : "+this.partHashSet.getFileHash()+" Part count : "+
			+this.partHashSet.size()+ " Hash sets :\n";
			
			for(int i = 0;i<this.partHashSet.size();i++) {
				byte[] hash = this.partHashSet.get(i);
					result+=Convert.byteToHexString(hash)+"\n";
			}
				
			return result;
		} catch (TagException e) {
			return "";
		}
		
		
	}
	
}
