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
package org.jmule.core.sharingmanager;

import static org.jmule.core.edonkey.E2DKConstants.FT_FILENAME;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.io.File;

import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:31:12 $$
 */
public class CompletedFile extends SharedFile {

	public CompletedFile(String filePath){
		file = new File(filePath);
		init();
	}
	
	public CompletedFile(File file) {
		this.file = file;
		init();
	}
	
	private void init() {
		StandardTag tag = new StandardTag(TAG_TYPE_STRING,FT_FILENAME);
		String fileName = this.getSharingName();
		if (fileName==null) fileName = file.getName();
		tag.insertString(fileName);
		tagList.addTag(tag);

		tag = new StandardTag(TAG_TYPE_DWORD,FT_FILESIZE);
		tag.insertDWORD(Convert.longToInt(file.length()));
		tagList.addTag(tag);
	}

	public String toString() {
		return "["+getSharingName()+" ; "+file.length()+" ; "+" ]";
	}

	public boolean isCompleted() {
		return true;
	}
}
