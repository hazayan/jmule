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

import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_ARC;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_AUDIO;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_DOC;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_IMAGE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_ISO;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_PROGRAM;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_UNKNOWN;
import static org.jmule.core.edonkey.E2DKConstants.TAG_FILE_TYPE_VIDEO;
import static org.jmule.core.edonkey.E2DKConstants.archive_extensions;
import static org.jmule.core.edonkey.E2DKConstants.audio_extensions;
import static org.jmule.core.edonkey.E2DKConstants.doc_extensions;
import static org.jmule.core.edonkey.E2DKConstants.image_extensions;
import static org.jmule.core.edonkey.E2DKConstants.iso_extensions;
import static org.jmule.core.edonkey.E2DKConstants.program_extensions;
import static org.jmule.core.edonkey.E2DKConstants.video_extensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 15:00:45 $$
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
				e.printStackTrace();
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
	
	public abstract boolean isCompleted();
	
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public void delete() {
		FileUtils.deleteQuietly(file);
	}
	
	public String getSharingName() {
		return file.getName();
	}
	
	public byte[] getMimeType() {
		String file_name = getSharingName();
		
		String extension = Misc.getFileExtension(file_name);
		extension = extension.toLowerCase();
		if (audio_extensions.contains(extension))
			return TAG_FILE_TYPE_AUDIO;
		
		if (video_extensions.contains(extension))
			return TAG_FILE_TYPE_VIDEO;
		
		if (image_extensions.contains(extension))
			return TAG_FILE_TYPE_IMAGE;
		
		if (doc_extensions.contains(extension))
			return TAG_FILE_TYPE_DOC;
		
		if (program_extensions.contains(extension))
			return TAG_FILE_TYPE_PROGRAM;
		
		if (archive_extensions.contains(extension))
			return TAG_FILE_TYPE_ARC;
		
		if (iso_extensions.contains(extension))
			return TAG_FILE_TYPE_ISO;
		
		return TAG_FILE_TYPE_UNKNOWN;
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

	public void setHashSet(PartHashSet hashSet) throws SharedFileException {
		this.hashSet = hashSet;
	}

	public TagList getTagList() {
		return this.tagList;
	}

	public void setTagList(TagList newTagList) {
		this.tagList = newTagList;		
	}
	
	public ED2KFileLink getED2KLink() {
		return new ED2KFileLink(getSharingName(),length(),getFileHash());
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
