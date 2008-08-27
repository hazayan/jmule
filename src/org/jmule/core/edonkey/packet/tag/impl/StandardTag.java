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
package org.jmule.core.edonkey.packet.tag.impl;

import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagException;
import org.jmule.util.Convert;

/**
 * 
 * Standard tag structure :
 * 
 * <table width="100%" cellspacing="0" border="1" cellpadding="0">
 *   <tbody>
 *     <tr>
 *       <td>Name</td>
 *       <td>Size in Bytes</td>
 *       <td>Description</td>
 *     </tr>
 *     <tr>
 *       <td>Tag Type</td>
 *       <td>1</td>
 *       <td>Tag Type : String,Dword</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag name</td>
 *       <td>2</td>
 *       <td>Meta Tag name length</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag Name</td>
 *       <td>&lt;Meta Tag Name length&gt;</td>
 *       <td>Meta tag name : File Name, File Size, Bit Rate etc</td>
 *     </tr>
 *     <tr>
 *       <td colspan="3"><center>String tag</center></td>
 *     </tr>
 *     <tr>
 *       <td>String length</td>
 *       <td>2</td>
 *       <td>Length of string</td>
 *     </tr>
 *     <tr>
 *       <td>String's bytes</td>
 *       <td>&lt;String length&gt;</td>
 *       <td>String's content</td>
 *     </tr>
 *     <tr>
 *       <td colspan="3"><center>Dword tag</center></td>
 *     </tr>
 *     <tr>
 *       <td>Dword value</td>
 *       <td>4</td>
 *       <td>Value of dword tag</td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/27 16:53:02 $$
 */
public class StandardTag extends AbstractTag implements Tag {

	public StandardTag() {
	}
	
	/**
	 * Constructor with basic values
	 * @param tagType tag type (integer, string, float) 
	 * @param metaTagName meta tag name (file name, file size, bitrate, author)
	 */
	public StandardTag(byte tagType, byte[] metaTagName) {
		super();
		setType(tagType);
		this.metaTagName.setMetaTagName(metaTagName.clone());
	}
	
	public void  insertString(String stringData){
		if (getType() != TAG_TYPE_STRING) return ;
				
		tag = ByteBuffer.allocate(1 + 2 +  metaTagName.getMetaTagLength() + 2 + stringData.getBytes().length);
		tag.order(ByteOrder.LITTLE_ENDIAN);
		
		tag.put(getType());
		
		tag.putShort(Convert.intToShort(metaTagName.getMetaTagLength()));
		tag.put(this.metaTagName.getRawMetaTagName());
		tag.putShort(Convert.intToShort(stringData.getBytes().length));
		tag.put(stringData.getBytes());
	}
	
	public void insertDWORD(int dwordData){
		if (getType() != TAG_TYPE_DWORD) return;
		// Allocate memory for tag
		tag = ByteBuffer.allocate(1 + 2 +  metaTagName.getMetaTagLength() +4);
		tag.order(ByteOrder.LITTLE_ENDIAN);
		// Insert tag type
		tag.put(getType());
		// Insert meta tag length
		tag.putShort(Convert.intToShort(metaTagName.getMetaTagLength()));
		// Insert meta tag
		tag.put(metaTagName.getRawMetaTagName());
		// Insert tag's specific data
		tag.putInt(dwordData);
	}

	public String getString() throws TagException {
		if (getType() != TAG_TYPE_STRING)
			throw new TagException("Tag is not string");

		short strLen = tag.getShort(2 + 1 + this.getMetaNameLength());
		byte[] data = new byte[strLen];

		int ipos = tag.position();
		tag.position(2 + this.getMetaNameLength() + 2 + 1);// Get on
		// position
		tag.get(data);
		tag.position(ipos);

		return new String(data);
	}

	/** Extract int form tag*/
	public int getDWORD() throws TagException {
		if (getType() != TAG_TYPE_DWORD)
			throw new TagException("Tag is not DWORD");
		return tag.getInt(2 + this.getMetaNameLength() + 1);
	}

	public void extractTag(ByteBuffer data){
		byte tagType;
	    tagType=data.get();
   	    short metaTagLength;
		metaTagLength = data.getShort();
		byte[] metaTagName = new byte[metaTagLength];
		data.get(metaTagName);
		this.metaTagName.setMetaTagName(metaTagName);
		switch (tagType) {
		case TAG_TYPE_STRING: {
			// Extract String tag
			short strLength = data.getShort();
			tag = ByteBuffer.allocate(1 + 2 + metaTagLength
					+ 2 + strLength);
			tag.order(ByteOrder.LITTLE_ENDIAN);

			tag.put(tagType);
			tag.putShort(metaTagLength);

			tag.put(metaTagName);
			tag.putShort(strLength);

			for (int i = 1; i <= strLength; i++)
				tag.put(data.get());

			break;
		}

		case TAG_TYPE_DWORD: {
			// Extract DWORD tag
			tag = ByteBuffer.allocate(1 + 2 + metaTagLength
					+ 4);
			tag.order(ByteOrder.LITTLE_ENDIAN);
			tag.put(tagType);
			tag.putShort(metaTagLength);
			tag.put(metaTagName);
			tag.putInt(data.getInt());
			break;
		}

		}
		this.setType(tagType);
	}
	
}
