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
package org.jmule.core.edonkey.metfile;

import static org.jmule.core.edonkey.E2DKConstants.FT_FILENAME;
import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.E2DKConstants.KNOWN_VERSION;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagScanner;
import org.jmule.core.sharingmanager.CompletedFile;
import org.jmule.core.sharingmanager.SharedFile;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * Known.met file format.
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *     <td>Default value</td>
 *   </tr>
 *   <tr>
 *     <td>File header</td>
 *     <td>1</td>
 *     <td>0xE0</td>
 *   </tr>
 *   <tr>
 *     <td>Number of entries</td>
 *     <td>4</td>
 *     <td>&nbsp;</td>
 *   </tr>
 * </tbody>
 * </table>
 * <br>
 * File data block : <br>
 *  
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 * <tbody>
 *   <tr>
 *     <td>Name</td>
 *     <td>Size in bytes</td>
 *   </tr>
 *   <tr>
 *     <td>Date</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>File Hash</td>
 *     <td>16</td>
 *   </tr>
 *   <tr>
 *     <td>Part count</td>
 *     <td>2</td>
 *   </tr>
 *   <tr>
 *     <td>&lt;Part hash&gt;*&lt;Part count&gt;</td>
 *     <td>16*&lt;Part count&gt;</td>
 *   </tr>
 *   <tr>
 *     <td>Tag Count</td>
 *     <td>4</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>variable</td>
 *   </tr>
 * </tbody>
 * </table>
 * 
 * @author binary256
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/15 18:05:34 $$
 */
public class KnownMet extends MetFile {

	public KnownMet(String fileName) throws KnownMetException {
		super(fileName);
		if (fileChannel == null)
			throw new KnownMetException("Failed to load "+fileName);
	}

	public Hashtable<String,KnownMetEntity> loadFile() throws KnownMetException,IOException,
									FileNotFoundException {
		fileChannel.position(0);
		// avoid files from known.met that have the same size and name
		HashSet<String> repeated_keys = new HashSet<String>();
		ByteBuffer data;
		Hashtable<String,KnownMetEntity> knownFiles = new Hashtable<String, KnownMetEntity>();
		
		data = Misc.getByteBuffer(1);
		fileChannel.read(data);
		//if (data.get(0)!=KNOWN_VERSION) 
		//		throw new KnownMetException("Unsupported file version");
		
		data = Misc.getByteBuffer(4);
		fileChannel.read(data);
		
		long numRecords = Convert.longToInt(data.getInt(0));

		for(int j = 0;j<numRecords;j++) {
			
			KnownMetEntity known_met_entity = new KnownMetEntity();
		
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			int Date = data.getInt(0);
		
			known_met_entity.setDate(Date);
		
			data = Misc.getByteBuffer(16);
			fileChannel.read(data);
			FileHash fileHash = new FileHash(data.array());
		
			data = Misc.getByteBuffer(2);
			fileChannel.read(data);
			int partCount = data.getShort(0);
			PartHashSet partHash = new PartHashSet(fileHash);
		
			for(int i = 0;i<partCount;i++) {
				data = Misc.getByteBuffer(16);
				fileChannel.read(data);
				partHash.add(data.array());
			}
		
			known_met_entity.setPartHashSet(partHash);
		
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			int tagCount = data.getInt(0);
			TagList tagList = new TagList();
			for(int i = 0;i<tagCount;i++) {
				Tag tag = TagScanner.scanTag(fileChannel);
				if (tag != null)
					tagList.addTag(tag);
			}
			known_met_entity.setTagList(tagList);
		
			try {
				String file_name = (String)known_met_entity.getTagList().getTag(FT_FILENAME).getValue();
				long file_size = Convert.intToLong((Integer)known_met_entity.getTagList().getTag(FT_FILESIZE).getValue());
				String key = file_name + file_size;
				if(repeated_keys.contains(key)) continue;
				KnownMetEntity known_file = knownFiles.get(key);
				if (known_file == null)		
					knownFiles.put(key, known_met_entity);
				else {
					knownFiles.remove(key);
					repeated_keys.add(key);
				}
			} catch (Throwable e) { e.printStackTrace();}
		
		}
		
		return knownFiles;
	}
	
	public void writeFile(Collection<SharedFile> fileList) throws KnownMetException,
					IOException,FileNotFoundException {
		fileChannel.position(0);
		ByteBuffer data;
		
		data = Misc.getByteBuffer(1);
		data.put(KNOWN_VERSION);
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(4);
		data.putInt(fileList.size());
		data.position(0);
		fileChannel.write(data);
		
		for(SharedFile file : fileList) {
			
			data = Misc.getByteBuffer(4);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(16);
			data.put(file.getFileHash().getHash());
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(2);
			data.putShort(Convert.intToShort(file.getHashSet().size()));
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(16);
			for(int a = 0;a<file.getHashSet().size();a++) {
				data.position(0);
				data.put(file.getHashSet().get(a));
				data.position(0);
				fileChannel.write(data);
			}
			
			TagList tagList = file.getTagList();
			
			data = Misc.getByteBuffer(4);
			data.putInt(tagList.size());
			data.position(0);
			fileChannel.write(data);
			for(Tag tag : tagList) {
				ByteBuffer raw_tag = tag.getAsByteBuffer();
				fileChannel.write(raw_tag);
			}
			
		}
		
	}
	
	public void appendFile(CompletedFile addData) throws FileNotFoundException,IOException {
		fileChannel.position(0);
		ByteBuffer data = Misc.getByteBuffer(4);
		
		fileChannel.position(1);
		
		fileChannel.read(data);
		
		long numRecords = Convert.intToLong(data.getInt(0));
		numRecords++;
		
		
		data.putInt(0,Convert.longToInt(numRecords));
		data.position(0);
		fileChannel.position(1);
		fileChannel.write(data);
		
		fileChannel.position(fileChannel.size());
		
		data = Misc.getByteBuffer(4);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(16);
		data.put(addData.getFileHash().getHash());
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(2);
		data.putShort(Convert.intToShort(addData.getHashSet().size()));
		data.position(0);
		fileChannel.write(data);
		
		data = Misc.getByteBuffer(16);
		for(int a = 0;a<addData.getHashSet().size();a++) {
			data.position(0);
			data.put(addData.getHashSet().get(a));
			data.position(0);
			fileChannel.write(data);
		}
		
		TagList tagList = addData.getTagList();
		
		data = Misc.getByteBuffer(4);
		data.putInt(tagList.size());
		data.position(0);
		fileChannel.write(data);
		
		for(Tag tag : tagList) {
			ByteBuffer raw_tag = tag.getAsByteBuffer();
			fileChannel.write(raw_tag);
		}
	}
}
