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
package org.jmule.core.searchmanager.tree;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.sharingmanager.FileType;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

import static org.jmule.core.edonkey.E2DKConstants.*;
/**
 * Tree node value.
 * Created on Oct 26, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/28 21:06:40 $
 */
public enum NodeValue {
	NOT { public byte[] getBytes() { return SEARCH_NOT; } },
	OR  { public byte[] getBytes() { return SEARCH_OR; } },
	AND { public byte[] getBytes() { return SEARCH_AND; } },
	FILE_NAME {
		public byte[] getBytes() {
			String str = (String)getValue(Tree.DATA_KEY);
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + str.length());
			data.position(0);
			data.put(SEARCH_BY_NAME);
			data.putShort(Convert.intToShort(str.length()));
			data.put(str.getBytes());
			return data.array();
		}
	},
	FILETYPE {
		public byte[] getBytes() {
			FileType type = (FileType)getValue(Tree.DATA_KEY);
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + type.getBytes().length + 2 + TAG_NAME_FILE_TYPE.length);
			data.position(0);
			data.put(SEARCH_BY_META);
			data.putShort(Convert.intToShort(type.getBytes().length));
			data.put(type.getBytes());
			data.putShort(Convert.intToShort(TAG_NAME_FILE_TYPE.length));
			data.put(TAG_NAME_FILE_TYPE);
			return data.array();
		}
	},
	MINSIZE {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_SIZE.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_SIZE.length));
			data.put(TAG_NAME_SIZE);
			return data.array();
		}
	},
	MAXSIZE {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_SIZE.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_SIZE.length));
			data.put(TAG_NAME_SIZE);
			return data.array();
		}
	},
	MINAVAILABILITY {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_AVIABILITY.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_AVIABILITY.length));
			data.put(TAG_NAME_AVIABILITY);
			return data.array();
		}
	},
	
	MAXAVAILABILITY {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_AVIABILITY.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_AVIABILITY.length));
			data.put(TAG_NAME_AVIABILITY);
			return data.array();
		}
	},
	
	MINCOMPLETESRC {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_COMPLETESRC.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MIN);
			data.putShort(Convert.intToShort(TAG_NAME_COMPLETESRC.length));
			data.put(TAG_NAME_COMPLETESRC);
			return data.array();
		}
	},
	
	MAXCOMPLETESRC {
		public byte[] getBytes() {
			ByteBuffer data = Misc.getByteBuffer(1 + 4 + 1 + 2 + TAG_NAME_COMPLETESRC.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			long v = (Long)getValue(Tree.DATA_KEY);
			data.putInt(Convert.longToInt(v));
			data.put(LIMIT_MAX);
			data.putShort(Convert.intToShort(TAG_NAME_COMPLETESRC.length));
			data.put(TAG_NAME_COMPLETESRC);
			return data.array();
		}
	},
	
	EXTENSION {
		public byte[] getBytes() {
			String str = (String)getValue(Tree.DATA_KEY);
			ByteBuffer data = Misc.getByteBuffer(1 + 2 + str.length() + 2 + TAG_NAME_FORMAT.length);
			data.position(0);
			data.put(SEARCH_BY_LIMIT);
			data.putShort(Convert.intToShort(str.length()));
			data.put(str.getBytes());
			data.putShort(Convert.intToShort(TAG_NAME_FORMAT.length));
			data.put(TAG_NAME_FORMAT);
			return data.array();
		}
	};
	
	private Map<String,Object> data = new Hashtable<String,Object>();
	
	public Object getValue(String key) {
		return data.get(key);
	}
	
	public void setValue(String key,Object value) {
		data.put(key, value);
	}
	
	public void setValue(Object value) {
		setValue(Tree.DATA_KEY, value);
	}

	public abstract byte[] getBytes();
}
