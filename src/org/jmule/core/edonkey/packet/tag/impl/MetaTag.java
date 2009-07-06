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

import org.jmule.core.utils.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:10:11 $$
 */
public class MetaTag {

	private byte[] metaTagName;
	
	public MetaTag() {
		metaTagName = new byte[1];
	}
	
	public MetaTag(byte[] metaTagName){
		this.metaTagName = metaTagName;
	}
	
	public void setMetaTagName(byte[] metaTagName){
		this.metaTagName = metaTagName;
	}
	
	public byte[] getRawMetaTagName() {
		return this.metaTagName;
	}
	
	public int getMetaTagLength() {
		return this.metaTagName.length;
	}
	
	public int hashCode() {
		return Convert.byteToHexString(metaTagName, " ").hashCode();
	}
	
	public boolean equals(Object object){
		if (object==null) return false;
		if (!(object instanceof MetaTag)) return false;
		return this.hashCode()==object.hashCode();
	}
	
	public String toString() {
		return Convert.byteToHexString(metaTagName, " ");
	}
	
}
