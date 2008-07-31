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

import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXBYTE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXDWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_LONG;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_SHORT_BEGIN;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXSTRING_SHORT_END;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_EXWORD;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jmule.core.edonkey.packet.tag.Tag;


/**
 * 
 * String tag :
 * String length &lt;=15
 * 
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *   <tbody>
 *     <tr>
 *       <td>Name</td>
 *       <td>Size in Bytes</td>
 *       <td>Description</td>
 *     </tr>
 *     <tr>
 *       <td>Tag Type</td>
 *       <td>1</td>
 *       <td>Tag Type = 0x90+String Length</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag name</td>
 *       <td>1</td>
 *       <td>Meta Tag name, like standard tag</td>
 *     </tr>
 *     <tr>
 *       <td>String data</td>
 *       <td>-</td>
 *       <td>String data,length loaded from Meta Tag type</td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * String Length &gt;15
 * 
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *   <tbody>
 *     <tr>
 *       <td>Name</td>
 *       <td>Size in Bytes</td>
 *       <td>Description</td>
 *     </tr>
 *     <tr>
 *       <td>Tag Type</td>
 *       <td>1</td>
 *       <td>Tag Type : 0x82 (constant value)</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag name</td>
 *       <td>1</td>
 *       <td>Meta Tag Value similar with standard tag</td>
 *     </tr>
 *     <tr>
 *       <td>String length</td>
 *       <td>2</td>
 *       <td>Length of string</td>
 *     </tr>
 *     <tr>
 *       <td>String data</td>
 *       <td>-</td>
 *       <td>String data</td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * DWORD TAG
 * <table width="70%" cellspacing="0" border="1" cellpadding="0">
 *   <tbody>
 *     <tr>
 *       <td>Name</td>
 *       <td>Size in Bytes</td>
 *       <td>Description</td>
 *     </tr>
 *     <tr>
 *       <td>Tag Type</td>
 *       <td>1</td>
 *       <td>Tag Type : <br>0x89 - byte value(&lt;256);<br>0x88 - short value(&gt;=256);<br>0x83 - dword  value( 4 bytes) value(&gt;65536);</td>
 *     </tr>
 *     <tr>
 *       <td>Meta Tag name</td>
 *       <td>1</td>
 *       <td>Meta Tag value similar with standard tag</td>
 *     </tr>
 *     <tr>
 *       <td>DWORD value</td>
 *       <td>-</td>
 *       <td>Tag value, size depends of meta tag type</td>
 *     </tr>
 *   </tbody>
 * </table>
 * </table>
 *
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:58 $$
 */

public class ExtendedTag extends AbstractTag implements Tag{

	/**
	 * Extract extended eDonkeyTag from ByteBuffer value 
	 */
	public void extractTag(ByteBuffer data, int startPos) {
		int initPos=data.position();
		
		// byte tagType;
		boolean tagIsOk=false;
		
		data.position(startPos);
		
	    setType(data.get());

	    // If Have long string >15
	    if (getType() == TAG_TYPE_EXSTRING_LONG) {
	    	tagIsOk=true;
	    	byte[] metaTagName = new byte[1];
	    	data.get(metaTagName);
	    	this.metaTagName.setMetaTagName(metaTagName);
	    	
	    	byte[] bytes=new byte[2];
	    	data.get(bytes);
	    	ByteBuffer tmp1 = ByteBuffer.allocate(4);
	    	tmp1.order(ByteOrder.LITTLE_ENDIAN);
	    	tmp1.position(0);
	    	tmp1.put(bytes);
	    	tmp1.put((byte)0);
	    	tmp1.put((byte)0);
	    	
	    	int strLength=tmp1.getInt(0);

	    	tag = ByteBuffer.allocate(1+1+2+strLength);
	    	tag.order(ByteOrder.LITTLE_ENDIAN);
	    	tag.put(getType());
	    	tag.put(metaTagName);
	    	
	    	tag.putShort((short)tmp1.getShort(0));
	    	
	    	for(int i = 1; i <= strLength; i++) 
	    		tag.put(data.get());
	    }
	    // If have short string <=15
	    if ((getType()>=TAG_TYPE_EXSTRING_SHORT_BEGIN)&&(getType()<=TAG_TYPE_EXSTRING_SHORT_END)){
	    	tagIsOk=true;
	    	byte[] metaTagName = new byte[1];
	    	data.get(metaTagName);
	    	
	    	this.metaTagName.setMetaTagName(metaTagName);
	    	
	    	byte strLength = (byte)(getType() - TAG_TYPE_EXSTRING_SHORT_BEGIN);
	    	
	    	tag = ByteBuffer.allocate(1+1+strLength);
	    	tag.order(ByteOrder.LITTLE_ENDIAN);
	    	
	    	tag.put(getType());
	    	tag.put(metaTagName);
	    	
	    	for(int i = 1; i <= strLength; i++)
	    		tag.put(data.get());
	    	
	    }
	    
	    // If have byte value
	    if (getType() == TAG_TYPE_EXBYTE){
	    	tagIsOk=true;
	    	byte[] metaTagName = new byte[1];
	    	data.get(metaTagName);
	    	this.metaTagName.setMetaTagName(metaTagName);
	    	
	    	tag = ByteBuffer.allocate(1+1+1);
	    	tag.order(ByteOrder.LITTLE_ENDIAN);
	    	
	    	tag.put(getType());
	    	tag.put(metaTagName);
	    	tag.put(data.get());
	    }
	    
	  // If have word value
	    if (getType() == TAG_TYPE_EXWORD){
	    	tagIsOk=true;
	    	byte[] metaTagName = new byte[1];
	    	data.get(metaTagName);
	    	
	    	this.metaTagName.setMetaTagName(metaTagName);
	    	
	    	tag = ByteBuffer.allocate(1+1+2);
	    	tag.order(ByteOrder.LITTLE_ENDIAN);
	    	
	    	tag.put(getType());
	    	tag.put(metaTagName);
	    	tag.putShort(data.getShort());
	    }
	    
	  // If have dword value
	    if (getType() == TAG_TYPE_EXDWORD){
	    	tagIsOk=true;
	    	byte[] metaTagName = new byte[1];
	    	data.get(metaTagName);
	    	this.metaTagName.setMetaTagName(metaTagName);
	    	
	    	tag = ByteBuffer.allocate(1+1+4);
	    	tag.order(ByteOrder.LITTLE_ENDIAN);
	    	
	    	tag.put(getType());
	    	tag.put(metaTagName);
	    	tag.putInt(data.getInt());
	    }
		if (!tagIsOk){
//			data.position(startPos);
//			byte tagtype=data.get();
//			log.warning("Failed to decode extended tag \n"+"Tag type : "+Integer.toHexString(tagtype&0xFF)+"\n at position : "+Integer.toHexString(startPos&0xFFFFFFFF)+"\nDumping packet in file : errordump");
//			try {
				// TODO : Log wrong tags
//				FileOutputStream out = new FileOutputStream(new File("./errordump"));
//				out.write(data.array());
//				out.close();
//				log.warning("Dump of tags in ./errordump ok ! ");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}	
		}
		data.position(initPos);

	}

	/**
	 * Get DWORD tag
	 */
	public int getDWORD() throws TagException {
		try {
		if (getType()==TAG_TYPE_EXBYTE){
			byte[] data=new byte[1];
			
			int pos=tag.position();
			
			tag.position(1+1);
			data[0]=tag.get();
			tag.position(pos);
			
			ByteBuffer value = ByteBuffer.allocate(4);
			value.order(ByteOrder.LITTLE_ENDIAN);
			value.put(data);
			
	    	return value.getInt(0);
			}
		if (getType()==TAG_TYPE_EXWORD){
			byte[] data=new byte[2];
			int pos=tag.position();
			
			tag.position(1+1);
			data[0]=tag.get();
			data[1]=tag.get();
			tag.position(pos);
			
			ByteBuffer value = ByteBuffer.allocate(4);
			value.order(ByteOrder.LITTLE_ENDIAN);
			value.put(data);
			
	    	return value.getInt(0);
		}
		if (getType()==TAG_TYPE_EXDWORD){
			byte[] data=new byte[4];
			int pos=tag.position();
			
			tag.position(1+1);
			data[0]=tag.get();
			data[1]=tag.get();
			data[2]=tag.get();
			data[3]=tag.get();
			tag.position(pos);
			
			ByteBuffer value = ByteBuffer.allocate(4);
			value.order(ByteOrder.LITTLE_ENDIAN);
			value.put(data);
			
	    	return value.getInt(0);
		}
		}catch(Exception e){
			throw new TagException("Wrong DWORD tag format") ;
		}
		return 0;
	}

	/**Get string TAG*/
	public String getString() throws TagException {
		
		if (getType()==TAG_TYPE_EXSTRING_LONG){
			try {
			int iPos=tag.position();
			
			short strLength = tag.getShort(1+1);
			byte[] stringData = new byte[strLength];

			tag.position(1+1+2);
			tag.get(stringData);
			
			tag.position(iPos);
			return new String(stringData);
			}catch(Exception e){
				throw new TagException("Wrong string tag format") ;
			}
		}
		
		if ((getType()>=TAG_TYPE_EXSTRING_SHORT_BEGIN)&&(getType()<=TAG_TYPE_EXSTRING_SHORT_END)){
			int iPos=tag.position();
			
			short strLength=(short)(getType()-TAG_TYPE_EXSTRING_SHORT_BEGIN);
			
			byte[] stringData = new byte[strLength];
			
			tag.position(1+1);
			
			tag.get(stringData);
				
			tag.position(iPos);
			return new String(stringData);
		}
		
		return null;
	}

	public void insertDWORD(int dwordData) {
		// Support of extended tags is partial
	}

	public void insertString(String stringData) {
		// Support of extended tags is partial
	}

}
