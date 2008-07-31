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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.packet.tag.impl.TagList;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:41:03 $$
 */
public abstract class SharedFile {
	
	protected FileChannel fileChannel = null;
	
	protected PartHashSet hashSet = null;
	protected TagList tagList = new TagList();
	protected File file;

	public FileChunk getData(FileChunkRequest chunkData) throws SharedFileException{
		if (fileChannel == null)
			try {
				fileChannel = new RandomAccessFile(file,"rws").getChannel();
			} catch (FileNotFoundException e) {
				throw new SharedFileException("Can't open shared file "+file);
			}
			
		ByteBuffer data = Misc.getByteBuffer(chunkData.getChunkEnd()-chunkData.getChunkBegin());
		data.position(0);
		try {
			fileChannel.position(chunkData.getChunkBegin());
			
			fileChannel.read(data);
		} catch (IOException e) {
			throw new SharedFileException("I/O error on reading file "+this);
		}
		return new FileChunk(chunkData.getChunkBegin(),chunkData.getChunkEnd(),data);
	}
	
	public void delete() {
		
		FileUtils.deleteQuietly(file);
		
	}
	
	public String getSharingName() {
		return file.getName();
	}
	
	public int hashCode() {
		return this.getFileHash().hashCode();
	}
	
	public boolean equals(Object object){
		if (object == null) return false;
		return object.hashCode()==this.hashCode();
	}
	
	public FileHash getFileHash() {
		return hashSet.getFileHash();
	}
	
	public File getFile() {
		return file;
	}
	
	public long length() {
		return file.length();
	}
	
	public boolean exists() {
		return file.exists();
	}
	
	public void updateHashes() throws SharedFileException {
		if (fileChannel == null)
			try {
				fileChannel = new RandomAccessFile(file,"rws").getChannel();
			} catch (FileNotFoundException e) {
				throw new SharedFileException("Shared file not found");
			}
		PartHashSet newSets = MD4FileHasher.calcHashSets(fileChannel);
		hashSet = newSets;
	}

	public PartHashSet getHashSet() {
		return hashSet;
	}

	public void setHashSet(PartHashSet hashSet) {
		this.hashSet = hashSet;
	}

	public TagList getTagList() {
		return this.tagList;
	}

	public void setTagList(TagList newTagList) {
		this.tagList = newTagList;		
	}
	
	
	
	public void closeFile() {
		
		if (fileChannel!=null)
			try {
				fileChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
