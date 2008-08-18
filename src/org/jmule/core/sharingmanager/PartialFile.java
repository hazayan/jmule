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

import static org.jmule.core.edonkey.E2DKConstants.FT_FILESIZE;
import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.edonkey.metfile.PartMetException;
import org.jmule.core.edonkey.packet.tag.impl.TagList;
import org.jmule.core.utils.MD4;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/18 08:57:31 $$
 */
public class PartialFile extends SharedFile {
	
	private String tempFileName;
	
	private String metFileName;
	
	private boolean hasHashSet = false;
	
	private boolean checkedParts[];
	
	private PartMet partFile;
	
	public PartialFile(PartMet partFile) throws SharedFileException {
		
		String tempDir = ConfigurationManager.TEMP_DIR;
		
		file = new File(tempDir+File.separator+partFile.getTempFileName());
		
		try {
			
			fileChannel = new RandomAccessFile(file,"rws").getChannel();
			
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file);
		}
		
		this.partFile=partFile;
		
		tempFileName = tempDir + File.separator + partFile.getTempFileName();
		
		metFileName = tempDir + File.separator + partFile.getName();
		
		hasHashSet = true;
		
		hashSet = partFile.getFileHashSet();
				
		checkedParts = new boolean[Misc.getPartCount(length())];
		
		int partCount = Misc.getPartCount(length());
		
		for(int i = 0;i < partCount;i++) {
			
			if (partFile.getGapList().getIntersectedGaps(PARTSIZE*i, PARTSIZE*(i+1)-1).size()==0)
				
				checkedParts[i] = true;
			
				else
					
				checkedParts[i] = false;
			
		}
	}
	
	public PartialFile(String fileName, long fileSize,FileHash fileHash,PartHashSet hashSet,TagList tagList) throws SharedFileException {
		
		String tempDir = ConfigurationManager.TEMP_DIR;
		
		tempFileName = tempDir + File.separator+fileName + ".part";
		
		metFileName = tempDir + File.separator+fileName + ".part.met";
		
		file = new File(tempFileName);
		
		try {
			
			fileChannel = new RandomAccessFile(file,"rws").getChannel();
			
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file);
		}
		
		try {
			
			partFile = new PartMet(metFileName);
			
		} catch (PartMetException e1) {
			
			throw new SharedFileException("Failed to create part.met file " + metFileName);
			
		}
		
		
		partFile.setTagList(tagList);
		
		partFile.setFileSize(Convert.longToInt((fileSize)));
		
		partFile.setTempFileName(fileName + ".part");
		
		partFile.setRealFileName(fileName);
		
		
		this.tagList = partFile.getTagList();
		
		GapList gapList = new GapList();
		
		gapList.addGap(0, fileSize);
		
		partFile.setGapList(gapList);
		
		this.hashSet = new PartHashSet(fileHash);

		checkedParts = new boolean[Misc.getPartCount(fileSize)];
		
		int partCount = Misc.getPartCount(fileSize);
		
		for(int i = 0;i<partCount;i++)
			
				checkedParts[i] = false;
				
		hasHashSet = false;
		
		if (hashSet == null)
			return;
		//Save .part.met file only with file part hashes
		
		partFile.setFileHashSet(hashSet);
		
		hasHashSet = true;
		
		this.hashSet = hashSet;
		
		try {
			
			partFile.writeFile();
			
		} catch (PartMetException e) {
			
			throw new SharedFileException("Failed to write part.met file");
		}

	}
		
	public void setHashSet(PartHashSet newSet) {
		
		hasHashSet = true;
		
		this.hashSet = newSet;
		
		partFile.setFileHashSet(hashSet);
		
		try {
			
		partFile.writeFile();
		
		}catch(PartMetException e) {
			
		}
	}

	public void deletePartialFile() {
		partFile.delete();
	}
	
	public String toString() {
		
		return "[ "+partFile.getName()+" "+this.length()+" Completed : "+Math.round(getPercentCompleted())+" % GapList : "+partFile.getGapList()+" ] ";
		
	}
	
	public String getSharingName() {
		
		if (partFile == null) return null;
		
		return this.partFile.getRealFileName();
		
	}

	public synchronized void writeData(FileChunk fileChunk) throws SharedFileException {
		
		if (fileChannel == null) {
			
			try {
				
				fileChannel = new RandomAccessFile(file,"rws").getChannel();
				fileChannel.force(true);
			} catch (Throwable e) {
				
				throw new SharedFileException("Error on opening file");
				
			}
			
		}
		
		try {
			
			/** Check file limit and add more data if need **/
			
			long toAdd = 0;
			
			if (fileChannel.size()<=fileChunk.getChunkStart()) {
				
				toAdd = fileChunk.getChunkStart() - fileChannel.size();
				
				toAdd += fileChunk.getChunkData().capacity();
				
			} else
				
			if ((fileChannel.size()>fileChunk.getChunkStart())&&(fileChannel.size()<=fileChunk.getChunkEnd())) {
				
				toAdd = fileChunk.getChunkEnd() - fileChannel.size();
				
			}
			
			if (toAdd != 0) {
				
				addBytes(toAdd);
				
			}
			
			fileChannel.position(fileChunk.getChunkStart());
			
			fileChunk.getChunkData().position(0);
			
			fileChannel.write(fileChunk.getChunkData());
			
			//next line moved in thread addDataToWrite()
			
			partFile.getGapList().removeGap(fileChunk.getChunkStart(), fileChunk.getChunkStart()+fileChunk.getChunkData().capacity());
			
			//Danger
			
			if (!hasHashSet) return ;
			
			try {
				
				this.partFile.writeFile();
				
			} catch (PartMetException e) {
				
				throw new SharedFileException("Failed to save part.met file");
				
			}
			
			
			checkFilePartsIntegrity();
		
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}

	/** 
	 * Add num bytes at end of file 
	 * @param num
	 */
	private void addBytes(long bytes) {
		
		long blockCount = bytes / PARTSIZE;
		
		ByteBuffer block = Misc.getByteBuffer(PARTSIZE);
		
		try {
			
			fileChannel.position(fileChannel.size());
		
			long byteCount = 0;
			
			for(long i = 0;i<blockCount;i++) {
				
				byteCount +=PARTSIZE;
				
				block.position(0);
				
				fileChannel.write(block);
				
			}
			
			if ((bytes - byteCount)==0) return ;
			
			block = Misc.getByteBuffer(bytes - byteCount);
			
			block.position(0);
			
			fileChannel.write(block);
			
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			
		}
		
	}

	/**
	 * @return true - file is ok !
	 */
	public boolean checkFullFileIntegrity() {
		
		if (!hasHashSet()) return false;
		
		PartHashSet fileHashSet = partFile.getFileHashSet();
		
		PartHashSet newSet = MD4FileHasher.calcHashSets(fileChannel);
		
		System.out.println("Old hash set : "+fileHashSet);
		System.out.println("New hash set : "+newSet);
		
		if (newSet.size()!=fileHashSet.size()) return false;
		
		for(int i = 0;i<fileHashSet.size();i++) {
			
			byte b[] = newSet.get(i);
			
			byte a[] = fileHashSet.get(i);
			
			if (!compareArray(a,b)) {
				
				long begin = PARTSIZE*i;
				
				long end = PARTSIZE*(i+1)-1;
				
				if (end>length()) end = length();
				
				//Adding gap
				
				this.partFile.getGapList().addGap(begin,end);
			
				return false;
			}
			
		}
		
		if (!newSet.getFileHash().equals(fileHashSet.getFileHash())) {
			
			this.partFile.getGapList().addGap(0, length());
			
			return false;
			
		}
		
		return true;
	}
	
	private boolean checkFilePartsIntegrity() {
		
		if (!hasHashSet()) return false;
		
		if (checkedParts.length==0) return true;
		
		boolean status = true;
		
		for(int i = 0;i<checkedParts.length;i++) {
			
			if (!checkedParts[i]) {
				
			long begin = PARTSIZE*i;
			
			long end = PARTSIZE*(i+1)-1;
			
			if(partFile.getGapList().getIntersectedGaps(begin, end).size()==0) {
				
				if (checkPartIntegrity(i)) checkedParts[i] = true;
				
				else {
					
					partFile.getGapList().addGap(begin, end);
					
					status = false;
					
				}
			}
			
			}
		}
		
		return status;
	}
	
	private boolean compareArray(byte[] a1, byte[] a2) {
		
		if (a1.length != a2.length) return false;
	
		for(int i = 0;i<a1.length;i++)
			
			if (a1[i] != a2[i]) return false;
		
		return true;
		
	}
	
	private boolean checkPartIntegrity(int partID) {
		
		long start = PARTSIZE*partID;
		
		MD4 msgDigest = new MD4();
		
		ByteBuffer partData = Misc.getByteBuffer(PARTSIZE);
		msgDigest.reset();
		try {
			
			fileChannel.position(start);
			
			int count = fileChannel.read(partData);
			
			msgDigest.update(partData);
			
			ByteBuffer hashset = Misc.getByteBuffer(16);
			
			msgDigest.finalDigest(hashset);
		
			if (!compareArray(hashSet.get(partID),hashset.array()))
				
				return false;
			
			return true;
			
		} catch (IOException e) {
			
			return false;
			
		}
	}
	
	public GapList getGapList() {
		
		return partFile.getGapList();
		
	}
	
	public String getMetFileName() {
		
		return metFileName;
		
	}	
	
	public long getDownloadedBytes() {
		
		return length() - partFile.getGapList().byteCount();
		
	}
	
	public double getPercentCompleted() {
		
		return (double)(getDownloadedBytes()*100)/(double)length();
		
	}
	
	public boolean hasHashSet() {
		
		return this.hasHashSet;
		
	}

	public String getTempFileName() {
		
		return tempFileName;
		
	}

		
	public long length() {
		
		try {
			
			return Convert.intToLong(tagList.getDWORDTag(FT_FILESIZE));
			
		} catch (Throwable e) { return 0; }
	}
}
