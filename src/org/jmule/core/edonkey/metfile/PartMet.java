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
import static org.jmule.core.edonkey.E2DKConstants.FT_GAPEND;
import static org.jmule.core.edonkey.E2DKConstants.FT_GAPSTART;
import static org.jmule.core.edonkey.E2DKConstants.FT_TEMPFILE;
import static org.jmule.core.edonkey.E2DKConstants.PARTFILE_VERSION;
import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_DWORD;
import static org.jmule.core.edonkey.E2DKConstants.TAG_TYPE_STRING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagReader;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * <table cellpadding="0" border="1" cellspacing="0" width="70%">
 *  <tbody>
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
 *     <td>Last modification date</td>
 *     <td>4</td>
 *     <td>&nbsp;</td>
 *   </tr>
 *   <tr>
 *     <td>File hash</td>
 *     <td>16</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Part count</td>
 *     <td>2</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Parts hash</td>
 *     <td>&lt;Part count&gt;*16</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Tag count</td>
 *     <td>4</td>
 *     <td>-</td>
 *   </tr>
 *   <tr>
 *     <td>Tag list</td>
 *     <td>Variable</td>
 *     <td>-</td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * Created on Nov 7, 2007
 * @author binary256
 * @version $$Revision: 1.8 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2009/07/05 08:15:26 $$
 */
public class PartMet extends MetFile {
	
	public static final String PART_MET_FILE_EXTENTSION 		=  ".part.met";
	
	private byte partFileFormat;
	private int modDate;
	private TagList tagList = new TagList();
	private FileHash fileHash;
	private PartHashSet fileHashSet;
	private GapList gapList;
	private File part_file;
	
	public PartMet(File file) throws PartMetException {
		super(file);
		if (fileChannel == null) 
			throw new PartMetException("Failed to open "+file.getName());
		this.part_file = file;
	}
	
	public PartMet(String fileName) throws PartMetException {
		super(fileName);
		if (fileChannel == null)
			throw new PartMetException("Failed to open "+fileName);
		this.part_file = new File(fileName);
	}
	
	public String getAbsolutePath() {
		
		return part_file.getAbsolutePath();
		
	}
	
	public void loadFile() throws PartMetException{
		try {
			
			fileChannel.position(0);
			ByteBuffer data;
			
			//Load part file version
			data = Misc.getByteBuffer(1);
			fileChannel.read(data);
			this.partFileFormat = data.get(0);
			if (this.partFileFormat != PARTFILE_VERSION)
				throw new PartMetException("Unsupported part file");
			
			//Load part file modification date
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			this.modDate = data.getInt(0);
			
			//Load file hash
			data = Misc.getByteBuffer(16);
			fileChannel.read(data);
			fileHash = new FileHash(data.array());
			fileHashSet = new PartHashSet(fileHash);
			
			//Read part count
			data = Misc.getByteBuffer(2);
			fileChannel.read(data);
			short partCount = data.getShort(0);
			
			data = Misc.getByteBuffer(16);
			for(int i = 0 ; i <partCount; i++){
				data.clear();
				fileChannel.read(data);
				fileHashSet.add(data.array());
			}
			
			//Read tag count
			data = Misc.getByteBuffer(4);
			fileChannel.read(data);
			int tagCount = data.getInt(0);
			//Load Tags
			this.tagList = new TagList();
			for(int i = 0 ; i < tagCount; i++) {
				
				Tag tag = TagReader.readTag(fileChannel);
				if (tag != null)
					tagList.addTag(tag);
			}
			gapList = new GapList();
			byte tag_id = E2DKConstants.GAP_OFFSET;
			Tag start_tag, end_tag;
			while (true) {
				start_tag = tagList.getRawTag(new byte[]{FT_GAPSTART[0],tag_id});
				if (start_tag == null) break;
				end_tag = tagList.getRawTag(new byte[]{FT_GAPEND[0],tag_id});
				if (end_tag == null)
					throw new PartMetException("Can't find end of gap in file partial file ");
				tagList.removeTag(start_tag.getMetaTag());
				tagList.removeTag(end_tag.getMetaTag());
				try {
					long begin = Convert.intToLong(start_tag.getDWORD());
					long end = Convert.intToLong(end_tag.getDWORD());
					gapList.addGap(begin,end );
				} catch (TagException e) {
					throw new PartMetException("Failed to extract gap positions form file ");
				}
				tag_id++;
			}
		} catch (FileNotFoundException e) {
			throw new PartMetException("Failed to load PartFile ");
		} catch (IOException e) {
			throw new PartMetException("Failed to read data from PartFile ");
		} catch(Throwable t) {
			throw new PartMetException(Misc.getStackTrace(t));
		}
	}

	public void writeFile() throws PartMetException {
		try {
			fileChannel.position(0);
			ByteBuffer data;
			
			data = Misc.getByteBuffer(1);
			data.put(PARTFILE_VERSION);
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(4);
			data.putInt(0, this.getModDate());
			data.position(0);
			fileChannel.write(data);
			
			if (fileHash != null) {
			data = Misc.getByteBuffer(16);
			data.put(fileHash.getHash());
			data.position(0);
			fileChannel.write(data);
			} else {
				data = Misc.getByteBuffer(16);
				data.position(0);
				fileChannel.write(data);
			}
			
			if (fileHashSet != null) {
				data = Misc.getByteBuffer(2);
				data.putShort(Convert.intToShort(fileHashSet.size()));
				data.position(0);
				fileChannel.write(data);
				
				data = Misc.getByteBuffer(16*fileHashSet.size());
				
				for(int i = 0; i <fileHashSet.size();i++)
					data.put(fileHashSet.get(i));
				
				data.position(0);
				fileChannel.write(data);
			} else {
				try {
					long file_size = tagList.getDWORDTag(FT_FILESIZE);
					int part_count = (int)(file_size / PARTSIZE);
					if ((file_size % PARTSIZE) != 0)
						part_count++;
					data = Misc.getByteBuffer(2);
					data.putShort(Convert.intToShort(part_count));
					data.position(0);
					fileChannel.write(data);
					
					data = Misc.getByteBuffer(16);
					for(int i = 0;i<part_count;i++) {
						data.position(0);
						fileChannel.write(data);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
			}
			
			/**Count Gaps */
			int gapCount=gapList.size();
			data = Misc.getByteBuffer(4);
			data.putInt(tagList.getTagCount()+gapCount*2);
			data.position(0);
			fileChannel.write(data);
			
			data = Misc.getByteBuffer(tagList.getTotalSize());
			
			for(int i = 0;i < tagList.getTagCount(); i++) 
				data.put(tagList.getRawTag(i).getData());
	
			data.position(0);
			fileChannel.write(data);
			
			/**Write Gap List*/
			byte counter = E2DKConstants.GAP_OFFSET;
			byte metaTagBegin[] = FT_GAPSTART.clone();
			byte metaTagEnd[] = FT_GAPEND.clone();
			JMIterable<Gap> gap_list = gapList.getGaps();
			for(Gap gap : gap_list){
								
				metaTagBegin[1] = counter;
				Tag tagBegin = new StandardTag(TAG_TYPE_DWORD,metaTagBegin);
				tagBegin.insertDWORD(Convert.longToInt(gap.getStart()));
				
				metaTagEnd[1]=counter;
				Tag tagEnd = new StandardTag(TAG_TYPE_DWORD,metaTagEnd);
				tagEnd.insertDWORD(Convert.longToInt(gap.getEnd()));
				
				data = Misc.getByteBuffer(tagBegin.getSize()+tagEnd.getSize());
				data.put(tagBegin.getData());
				data.put(tagEnd.getData());
				
				data.position(0);
				fileChannel.write(data);
				
				counter++;
			}
		} catch (FileNotFoundException e) {
			throw new PartMetException("Failed to open for writing part file : " +part_file.getName());
		} catch (IOException e) {
			throw new PartMetException("Failed to write dta in part file : "+part_file.getName());	
		}
		
	}
	
	public boolean delete() {
		return part_file.delete();
	}

	public String getTempFileName() {
		String tmpFileName;
		try {
			tmpFileName = this.tagList.getStringTag(FT_TEMPFILE);
		} catch (TagException e) {
			return null;
		}
		return tmpFileName;
	}
	
	public void setTempFileName(String tempFileName) {
		this.tagList.removeTag(FT_TEMPFILE);
		Tag tag = new StandardTag(TAG_TYPE_STRING,FT_TEMPFILE);
		tag.insertString(tempFileName);
		tagList.addTag(tag);
	}
	
	public String getRealFileName() {
		String realFileName;
		try {
			realFileName = this.tagList.getStringTag(FT_FILENAME);
		} catch (TagException e) {
			return null;
		}
		return realFileName;
	}
	
	public void setRealFileName(String realFileName) {
		this.tagList.removeTag(FT_FILENAME);
		Tag tag = new StandardTag(TAG_TYPE_STRING,FT_FILENAME);
		tag.insertString(realFileName);
		tagList.addTag(tag);
	}
	
	public long getFileSize() {
		long fileSize;
		try {
			fileSize = this.tagList.getDWORDTag(FT_FILESIZE);
		} catch (TagException e) {
			return 0;
		}
		return fileSize;
	}
	
	public void setFileSize(long fileSize){
		this.tagList.removeTag(FT_FILESIZE);
		Tag tag = new StandardTag(TAG_TYPE_DWORD,FT_FILESIZE);
		tag.insertDWORD((int)fileSize);
		this.tagList.addTag(tag);
	}
	
	

	public PartHashSet getFileHashSet() {
		return fileHashSet;
	}
	
	public void setFileHashSet(PartHashSet fileHashSet) {
		this.fileHashSet = fileHashSet;
	}


	public byte getPartFileFormat() {
		return partFileFormat;
	}


	public void setPartFileFormat(byte partFileFormat) {
		this.partFileFormat = partFileFormat;
	}


	public int getModDate() {
		return modDate;
	}

	public void setModDate(int modDate) {
		this.modDate = modDate;
	}

	public TagList getTagList() {
		return tagList;
	}

	public void setTagList(TagList tagList) {
		this.tagList = tagList;
	}
	
	public GapList getGapList() {
		return gapList;
	}

	public void setGapList(GapList gapList) {
		this.gapList = gapList;
	}
	
	public String getName() {
		return part_file.getName();
	}
	
	public FileHash getFileHash() {
		return fileHash;
	}

	public void setFileHash(FileHash fileHash) {
		this.fileHash = fileHash;
	}
	
}
