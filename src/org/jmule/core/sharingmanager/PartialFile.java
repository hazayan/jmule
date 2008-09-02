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
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jmule.core.JMThread;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.edonkey.metfile.PartMetException;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.utils.MD4;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.5 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:46:23 $$
 */
public class PartialFile extends SharedFile {
	
	public static final String PART_FILE_EXTENSION = ".part";
	
	private String tempFileName;
	
	private String metFileName;
	
	private boolean checkedParts[];
	
	private boolean hasHashSet = false;
	
	private PartMet partFile;
	
	public PartialFile(PartMet partFile) throws SharedFileException {
		
		String tempDir = ConfigurationManager.TEMP_DIR;
		
		file = new File(tempDir+File.separator+partFile.getTempFileName());
		
		try {
			
			fileChannel = new RandomAccessFile(file,"rws").getChannel();
			
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file);
		}
		
		this.partFile = partFile;
		this.tagList = partFile.getTagList();
		tempFileName = tempDir + File.separator + partFile.getTempFileName();
		
		metFileName = tempDir + File.separator + partFile.getName();
		
		hashSet = partFile.getFileHashSet();
				
		checkedParts = new boolean[Misc.getPartCount(length())];
		
		int partCount = Misc.getPartCount(length());
		
		hasHashSet = true;
		
		for(int i = 0;i < partCount;i++) {
			
			if (partFile.getGapList().getIntersectedGaps(PARTSIZE*i, PARTSIZE*(i+1)-1).size()==0)
				
				checkedParts[i] = true;
			
				else
					
				checkedParts[i] = false;
			
		}
	}
	
	public PartialFile(String fileName, long fileSize,FileHash fileHash,PartHashSet hashSet,TagList tagList) throws SharedFileException {
		
		String tempDir = ConfigurationManager.TEMP_DIR;
		
		tempFileName = tempDir + File.separator+fileName + PART_FILE_EXTENSION;
		
		metFileName = tempDir + File.separator+fileName +  PartMet.PART_MET_FILE_EXTENTSION;
		
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
		
		if (hashSet==null) {
			hasHashSet = false;
			this.hashSet = new PartHashSet(fileHash);
		} else 
			hasHashSet = true;

		checkedParts = new boolean[Misc.getPartCount(fileSize)];
		
		int partCount = Misc.getPartCount(fileSize);
		
		for(int i = 0;i<partCount;i++)
			
				checkedParts[i] = false;
				
		partFile.setFileHash(fileHash);
		
		partFile.setFileHashSet(hashSet);
		
		try {
			
			partFile.writeFile();
			
		} catch (PartMetException e) {
			
			throw new SharedFileException("Failed to write part.met file");
		}

	}
		
	public void setHashSet(PartHashSet newSet) throws SharedFileException  {
		int partCount = Misc.getPartCount(this.length());
		System.out.println("New hash set : \n"+newSet+"\n Part count : "+partCount+"\n Length : "+this.length());
		if (newSet.size() < partCount)
			throw new SharedFileException("Wrong hash set response");
		
		hasHashSet = true;
		hashSet.clear();
		for(int i = 0;i<partCount;i++)
			hashSet.add(newSet.get(i));
			
		
		partFile.setFileHashSet(hashSet);
		
		try {
			
		partFile.writeFile();
		
		}catch(PartMetException e) {
			
		}
	}
	
	public void deletePartialFile() {
		partFile.delete();
	}
	
	public void delete() {
		deletePartialFile();
		super.delete();
	}
	
	public String toString() {
		
		return "[ "+partFile.getName()+" "+this.length()+" Completed : "+Math.round(getPercentCompleted())+" % GapList : "+partFile.getGapList()+" ] ";
		
	}
	
	public String getSharingName() {
		
		if (partFile == null) return null;
		
		return this.partFile.getRealFileName();
		
	}
	
	public long getPartCount() {
		long part_count = (long) Math.ceil((double)length() / (double)PARTSIZE);
		if (length() % PARTSIZE!=0) part_count++;
		return part_count;
	}
	
	public long getAvailablePartCount() {
		long part_count = 0;
		for(int i = 0;i<checkedParts.length;i++)
			if (checkedParts[i])
				part_count++;
		return part_count;
	}

	public void writeData(FileChunk fileChunk) throws SharedFileException {
		
	//	writeChunk(fileChunk);
	//	partFile.getGapList().removeGap(fileChunk.getChunkStart(), fileChunk.getChunkStart()+fileChunk.getChunkData().capacity());
	
		try { 
			if (fileChannel == null) {
				
				try {
					
					fileChannel = new RandomAccessFile(file,"rws").getChannel();
					//fileChannel.force(true);
				} catch (Throwable e) {
					
					throw new SharedFileException("Error on opening file");
					
				}
				
			}
			
			//** Check file limit and add more data if need **//*
			
			long toAdd = 0;
			if (fileChannel.size()<=fileChunk.getChunkStart()) {
				
				toAdd = fileChunk.getChunkStart() - fileChannel.size();
				
				toAdd += fileChunk.getChunkData().capacity();
				
			} else
				
			if ((fileChannel.size()>fileChunk.getChunkStart())&&(fileChannel.size()<=fileChunk.getChunkEnd())) {
				
				toAdd = fileChunk.getChunkEnd() - fileChannel.size();
				
			}
			if (toAdd != 0) {
				toAdd += E2DKConstants.PARTSIZE;
				if (toAdd + fileChannel.size()>length()) {
					toAdd = length() - fileChannel.size();
				}
				addBytes(toAdd);
				
			}
			fileChannel.position(fileChunk.getChunkStart());
			
			fileChunk.getChunkData().position(0);
			
			fileChannel.write(fileChunk.getChunkData());
			partFile.getGapList().removeGap(fileChunk.getChunkStart(), fileChunk.getChunkStart()+fileChunk.getChunkData().capacity());
			try {
				
				partFile.writeFile();
				
			} catch (PartMetException e) {
				
				throw new SharedFileException("Failed to save part.met file");
				
			}
			checkFilePartsIntegrity();
		}catch(Throwable t) {
			throw new SharedFileException("Failed to write data");
		}
		
	}

	private void addBytes(long bytes) {
		long WRITE_BLOCK = 1024*1024*25;
		long blockCount = bytes / WRITE_BLOCK;
		
		ByteBuffer block = Misc.getByteBuffer(WRITE_BLOCK);
		
		try {
			
			fileChannel.position(fileChannel.size());
		
			long byteCount = 0;
			
			for(long i = 0;i<blockCount;i++) {
				
				byteCount +=WRITE_BLOCK;
				
				block.position(0);
				
				fileChannel.write(block);
				
			}
			
			if ((bytes - byteCount)==0) return ;
			
			block = Misc.getByteBuffer(bytes - byteCount);
			
			block.position(0);
			
			fileChannel.write(block);
			
		} catch (IOException e) {
						
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
		System.out.println("File hash : " + fileHashSet+" ");
		System.out.println("New set : "+newSet+"");
		if (newSet.size()!=fileHashSet.size()) 
			return false;
		
		for(int i = 0;i<fileHashSet.size();i++) {
			
			byte b[] = newSet.get(i);
			
			byte a[] = fileHashSet.get(i);
				
			
			
			if (!Arrays.equals(a, b)) {
				
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
	
	
	private boolean checkPartIntegrity(int partID) {
		
		long start = PARTSIZE*partID;
		
		MD4 msgDigest = new MD4();
		
		ByteBuffer partData = Misc.getByteBuffer(PARTSIZE);
		msgDigest.reset();
		try {
			
			fileChannel.position(start);
			
			int count = fileChannel.read(partData);
			partData.limit(count);
			msgDigest.update(partData);
			
			ByteBuffer hashset = Misc.getByteBuffer(16);
			
			msgDigest.finalDigest(hashset);
			
			if (!Arrays.equals(hashSet.get(partID),hashset.array())) {
				System.out.println(Convert.byteToHexString(hashSet.get(partID)));
				System.out.println(Convert.byteToHexString(hashset.array()));
				return false;
			}
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
	

	public PartMet getPartMetFile() {
		
		return partFile;
		
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
			
		} catch (Throwable e) {
			return 0; }
	}
	
	public void closeFile() {
		
		if (write_thread != null)
			if (write_thread.isAlive()) write_thread.JMStop();
		
		super.closeFile();
	}
	
	
	/*
	 *  speed research
	 */
	private Queue<FileChunk> write_list = new ConcurrentLinkedQueue<FileChunk>();
	
	private WriteThread write_thread;
	
	private void writeChunk(FileChunk chunk) {
		write_list.offer(chunk);
		if (write_thread != null)
			if (write_thread.isAlive()) return ;
		write_thread = new WriteThread();
		write_thread.start();
	}
	
	private class WriteThread extends JMThread {
		
		private boolean stop = false;
		
		public void JMStop() {
			stop = true;
		}
		
		public WriteThread() {
			super("File write thread");
		}
		
		public void run() {
			while(!stop) {
				if (write_list.size() == 0) return ;
				System.out.println("Before Size : " + write_list.size());
				FileChunk chunk = write_list.poll();
				System.out.println("After Size : " + write_list.size());
				try {
					
					/** Check file limit and add more data if need **/
					
					long toAdd = 0;
					
					if (fileChannel.size()<=chunk.getChunkStart()) {
						
						toAdd = chunk.getChunkStart() - fileChannel.size();
						
						toAdd += chunk.getChunkData().capacity();
						
					} else
						
					if ((fileChannel.size()>chunk.getChunkStart())&&(fileChannel.size()<=chunk.getChunkEnd())) {
						
						toAdd = chunk.getChunkEnd() - fileChannel.size();
						
					}
					System.out.println("A");
					if (toAdd != 0) {
						
						addBytes(toAdd);
						
					}
					System.out.println("B");
					fileChannel.position(chunk.getChunkStart());
					
					chunk.getChunkData().position(0);
					
					fileChannel.write(chunk.getChunkData());
					System.out.println("C");
					try {
						
						partFile.writeFile();
						
					} catch (PartMetException e) {
						
						throw new SharedFileException("Failed to save part.met file");
						
					}
					System.out.println("D");
					checkFilePartsIntegrity();
					
				}catch(Throwable t) {
					
				}
				
			}
		}
	}
	
}
