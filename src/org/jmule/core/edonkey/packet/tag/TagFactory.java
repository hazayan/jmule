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
package org.jmule.core.edonkey.packet.tag;

import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import org.jmule.core.edonkey.packet.tag.impl.StandardTag;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/05/09 16:20:43 $$
 */
public class TagFactory {
	/**
	 * Create String Tag with Special Tag value
	 * @param Str String value
	 * @param tagName name of tag
	 */
	public static Tag getStringTag(String Str,byte[] metaTagName) {
		Tag result=(Tag)new StandardTag(TAG_TYPE_STRING,metaTagName);
		result.insertString(Str);
		
		return result;
	}
	
	/**
	 * Create DWORD tag 
	 * @param Data Data value
	 * @param tagName name of tag
	 */
	public static Tag getDWORDTag(int Data,byte[] tagName) {
		Tag result=(Tag)new StandardTag(TAG_TYPE_DWORD,tagName);
		result.insertDWORD(Data);
		return result;
	}

}
