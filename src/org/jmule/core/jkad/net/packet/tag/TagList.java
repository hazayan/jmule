/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.net.packet.tag;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on Jan 28, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class TagList implements Iterable<Tag> {

	public final static TagList EMPTY_TAG_LIST;
	
	static {
		EMPTY_TAG_LIST = new TagList();
	}
	
	private List<Tag> tagList = new LinkedList<Tag>();
	
	public TagList() {
	}
	
	public TagList(List<Tag> tagList) {
		this.tagList.addAll(tagList);
	}
	
	public int size() {
		return tagList.size();
	}
	
	public List<Tag> getTagList() {
		return tagList;
	}
	
	public void addTag(Tag tag) {
		if (hasTag(tag.getTagName())) return;
		tagList.add(tag);
	}
	
	public void removeTag(byte[] tagName) {
		Tag remove = null;
		
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				remove = tag;
		if (remove == null) return;
		tagList.remove(remove);
	}
	
	public boolean hasTag(byte[] tagName) {
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				return true;
		return false;
	}
	
	public Tag getTag(byte[] tagName) {
		for(Tag tag : tagList) 
			if (Arrays.equals(tag.getTagName(), tagName))
				return tag;
		return null;
	}

	public Iterator<Tag> iterator() {
		return tagList.iterator();
	}
	
	public String toString() {
		String result ="";
		for(Tag tag : tagList) {
			result += tag + "\n";
		}
		return result;
	}
}
