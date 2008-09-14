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

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.edonkey.packet.tag.impl.MetaTag;
import org.jmule.util.Convert;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:36:04 $$
 */
public class TagList extends ConcurrentHashMap<MetaTag,Tag>{
	private int tagsSize=0;
	
	public Collection<Tag> getTags() {
		return super.values();
	}
	
	public String toString() {
		String Result=" [ ";
		
		Collection<Tag> Tags = super.values();
		for(Iterator<Tag> i = Tags.iterator();i.hasNext();)
			Result += "\n[ "+i.next()+" ] ";
		Result+=" ] ";
		return Result;
	}
	
	/**
	 * Add Tag
	 * @param tag
	 */
	public void inserTag(Tag tag){
		if (!this.hasTag(tag.getMetaTag()))
			addTag(tag);
		else {
			this.removeTag(tag.getMetaTag());
			addTag(tag);
		}
	}
	
    /**
     * Add new Tag to list, raw insert, may not use!
     * @param tag
     */
	public void addTag(Tag tag){
		super.put(tag.getMetaTag(), tag);
		tagsSize+=tag.getSize();
	}

	public void addTag(TagList tagList,boolean overwrite) {
		for(Tag tag : tagList.getTags()) {
			if (hasTag(tag.getMetaTag())&&(overwrite)) {
				removeTag(tag.getMetaTag());
				addTag(tag);
			}
			if (!hasTag(tag.getMetaTag()))
				addTag(tag);
				
		}
	}
	
	public void removeTag(byte[] metaTag) {
		MetaTag mtag = new MetaTag(metaTag);
		this.removeTag(mtag);
	}
	
	public void removeTag(MetaTag metaTag){
		if (hasTag(metaTag)) {
			Tag tag = super.get(metaTag);
			tagsSize = tagsSize - tag.getSize();
			super.remove(metaTag);
		}
	}
	
	public boolean hasTag(MetaTag metaTag){
		return !(super.get(metaTag) == null);
	}

	/**
	 * Get Tag count from list
	 * @return tag count
	 */
	public int getTagCount() {
		return super.size();
	}
	
	/**
	 * Get size of tags from list in bytes
	 * @return size of eDonkey tags
	 */
	public int getTotalSize() {
		return this.tagsSize;
	}
	
	/**
	 * Get Raw Tag class
	 * @param tagID id of tag
	 * @return eDonkey tag class
	 */
	public Tag getRawTag(int tagID){
		 return (Tag)super.values().toArray()[tagID];
	}
	
	public Tag getRawTag(byte[] metaTagName){
		return super.get(new MetaTag(metaTagName));
	}
	
	/**
	 * Get a DWORD tag 
	 * @param tagName tag name
 	 * @return return value of DWORD tag
	 */
	public int getDWORDTag(byte[] tagName) throws TagException{
			Tag tmpTag=super.get(new MetaTag(tagName));
			if (tmpTag != null){
				try {
					return tmpTag.getDWORD();
				} catch (TagException e) {
					throw new TagException("Wrong tag format");
				}
			}		
		return 0;
	}
	
	public String getStringTag(byte[] metaTagName) throws TagException {
			Tag tmpTag=super.get(new MetaTag(metaTagName));
			if (tmpTag != null)
				try {
					return tmpTag.getString();
				} catch (TagException e) {
					throw new TagException("Wrong tag format");
				}
		return "";
	}
}
