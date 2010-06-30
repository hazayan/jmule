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

import org.jmule.core.InternalJMuleCore;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCoreEvent;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.NotEnoughSpaceDownloadingFile;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.FileHash;
import org.jmule.core.edonkey.PartHashSet;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.edonkey.metfile.PartMetException;
import org.jmule.core.edonkey.packet.tag.IntTag;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.MD4;
import org.jmule.core.utils.MD4FileHasher;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.20 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2010/06/30 18:14:40 $$
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
			readChannel = new RandomAccessFile(file,"rws").getChannel();
			readChannel.position(0);
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file+"\n"+Misc.getStackTrace(e));
		}
		
		try {
			writeChannel = new RandomAccessFile(file,"rws").getChannel();
			writeChannel.position(0);
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file+"\n"+Misc.getStackTrace(e));
		}
		
		this.partFile = partFile;
		this.tagList = new TagList(partFile.getTagList().getAsList());
		tagList.removeTag(E2DKConstants.JMuleInternalTags);
		
		tempFileName = tempDir + File.separator + partFile.getTempFileName();
		
		metFileName = tempDir + File.separator + partFile.getName();
		
		hashSet = partFile.getFileHashSet();
		if (hashSet.isEmpty()) {
			hasHashSet = false;
		}
		else
			hasHashSet = true;
		
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
		
		tempFileName = tempDir + File.separator+fileName + PART_FILE_EXTENSION;
		
		metFileName = tempDir + File.separator+fileName +  PartMet.PART_MET_FILE_EXTENTSION;
		
		file = new File(tempFileName);
		
		try {
			readChannel = new RandomAccessFile(file,"rws").getChannel();
			readChannel.position(0);
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file+"\n"+Misc.getStackTrace(e));
		}
		
		try {
			writeChannel = new RandomAccessFile(file,"rws").getChannel();
			writeChannel.position(0);
		} catch (Throwable e) {
			throw new SharedFileException("Failed to open "+file+"\n"+Misc.getStackTrace(e));
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
			e.printStackTrace();
			throw new SharedFileException("Failed to write part.met file\n"+Misc.getStackTrace(e));
		}

	}
		
	public void setHashSet(PartHashSet newSet) throws SharedFileException  {
		int partCount = Misc.getPartCount(this.length());
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
			e.printStackTrace();
			throw new SharedFileException("Failed to save part.met file\n"+Misc.getStackTrace(e));
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

	public boolean isCompleted() {
		if (getPercentCompleted()== 100d) return true;
		return false;
	}
	
	public synchronized void writeData(FileChunk fileChunk) throws SharedFileException {
			try {
				if (writeChannel == null) {
					
					try {
						writeChannel = new RandomAccessFile(file,"rws").getChannel();
					} catch (Throwable e) {
						throw new SharedFileException("Error on opening file");
					}
					
				}
				//** Check file limit and add more data if need **//
				long toAdd = 0;
				if (writeChannel.size()<=fileChunk.getChunkStart()) {
					toAdd = fileChunk.getChunkStart() - writeChannel.size();
					toAdd += fileChunk.getChunkData().capacity();
					
				} else
				if ((writeChannel.size()>fileChunk.getChunkStart())&&(writeChannel.size()<=fileChunk.getChunkEnd())) {
					toAdd = fileChunk.getChunkEnd() - writeChannel.size();
				}
				if (toAdd != 0) {
					toAdd += E2DKConstants.PARTSIZE;
					if (toAdd + writeChannel.size()>length()) {
						toAdd = length() - writeChannel.size();
					}
					addBytes(toAdd);
				}
				writeChannel.position(fileChunk.getChunkStart());
				fileChunk.getChunkData().position(0);
				writeChannel.write(fileChunk.getChunkData());
				partFile.getGapList().removeGap(fileChunk.getChunkStart(), fileChunk.getChunkStart()+fileChunk.getChunkData().capacity());
				
				fileChunk.getChunkData().clear();
				fileChunk.setChunkData(null);
				
				try {
					partFile.writeFile();
				} catch (PartMetException e) {
					e.printStackTrace();
					throw new SharedFileException("Failed to save part.met file\n"+Misc.getStackTrace(e));
				}
				checkFilePartsIntegrity();
			}catch(Throwable t) {
				t.printStackTrace();
				if( t instanceof java.io.IOException ) {
					JMuleCoreFactory.getSingleton().
					            getDownloadManager().stopDownload();
					((InternalJMuleCore)JMuleCoreFactory.getSingleton()).
					              notifyListenersEventOccured(JMuleCoreEvent.NOT_ENOUGH_SPACE,
					            		  new NotEnoughSpaceDownloadingFile( 
					            				   file.getName(),
	        		                               file.getTotalSpace(),
	        		                               file.getFreeSpace()
	        		                      )
					               );
				}
				throw new SharedFileException("Failed to write data\n"+Misc.getStackTrace(t));
			}
		
	}

	private void addBytes(long bytes) {
		long WRITE_BLOCK = 1024*1024*20;
		ByteBuffer block = Misc.getByteBuffer(WRITE_BLOCK);	
		long blockCount = bytes / WRITE_BLOCK;
		
		try {
			
			writeChannel.position(writeChannel.size());
		
			long byteCount = 0;
			
			for(long i = 0;i<blockCount;i++) {
				
				byteCount +=WRITE_BLOCK;
				
				block.position(0);
				
				writeChannel.write(block);
				
			}
			block.clear();
			System.gc();
			if ((bytes - byteCount)==0) return ;
			
			ByteBuffer block2 = Misc.getByteBuffer(bytes - byteCount);
			
			block2.position(0);
			
			writeChannel.write(block2);
			block2.clear();
		} catch (IOException e) {
						
			e.printStackTrace();
			
		}
		
	}

	/**
	 * @return true - file is ok !
	 */
	public boolean checkFullFileIntegrity() {
		if (length()<PARTSIZE) {
			PartHashSet newSet = MD4FileHasher.calcHashSets(writeChannel);
			System.out.println(length() + "  \nNew hash : " + newSet.getFileHash());
			System.out.println("hash : " + getFileHash());
			if (newSet.getFileHash().equals(getFileHash()))
				return true;
			this.partFile.getGapList().addGap(0, length());
			return false;
		}
		if (!hasHashSet()) return false;
		
		PartHashSet fileHashSet = partFile.getFileHashSet();
		PartHashSet newSet = MD4FileHasher.calcHashSets(writeChannel);

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
			
			partFile.getGapList().addGap(0, length());
			
			return false;
			
		}
		
		return true;
	}
	
	private boolean checkFilePartsIntegrity() {
		
		if (!hasHashSet()) {
			if (E2DKConstants.PARTSIZE < length()) return true;
			return false;
		}
		
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
			
			readChannel.position(start);
			
			int count = readChannel.read(partData);
			partData.limit(count);
			msgDigest.update(partData);
			
			ByteBuffer hashset = Misc.getByteBuffer(16);
			
			msgDigest.finalDigest(hashset);
			
			if (!Arrays.equals(hashSet.get(partID),hashset.array())) {
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
		if (length() < E2DKConstants.PARTSIZE) return true;
		return this.hasHashSet;
		
	}

	public String getTempFileName() {
		
		return tempFileName;
		
	}

		
	public long length() {
		
		try {
			
			return Convert.intToLong((Integer)tagList.getTag(FT_FILESIZE).getValue());
			
		} catch (Throwable e) {
			return 0; }
	}
	
	public void closeFile() {
		
		if (write_thread != null)
			if (write_thread.isAlive()) write_thread.JMStop();
		partFile.close();
		super.closeFile();
	}
	
	public void markDownloadStarted() {
		IntTag tag = new IntTag(E2DKConstants.FT_NAME_STATUS, 0x00);
		partFile.getTagList().addTag(tag, true);
		try {
			partFile.writeFile();
		} catch (PartMetException e) {
			e.printStackTrace();
		}
	}
	
	public void markDownloadStopped() {
		IntTag tag = new IntTag(E2DKConstants.FT_NAME_STATUS, 0x01);
		partFile.getTagList().addTag(tag, true);
		try {
			partFile.writeFile();
		} catch (PartMetException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isDownloadStarted() {
		Tag tag = partFile.getTagList().getTag(E2DKConstants.FT_NAME_STATUS);
		if (tag == null) 
			return true;
		int value = (Integer)tag.getValue();
		if (value==0x00) return true;
		return false;
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
					
					if (readChannel.size()<=chunk.getChunkStart()) {
						
						toAdd = chunk.getChunkStart() - readChannel.size();
						
						toAdd += chunk.getChunkData().capacity();
						
					} else
						
					if ((readChannel.size()>chunk.getChunkStart())&&(readChannel.size()<=chunk.getChunkEnd())) {
						
						toAdd = chunk.getChunkEnd() - readChannel.size();
						
					}
					System.out.println("A");
					if (toAdd != 0) {
						
						addBytes(toAdd);
						
					}
					System.out.println("B");
					readChannel.position(chunk.getChunkStart());
					
					chunk.getChunkData().position(0);
					
					readChannel.write(chunk.getChunkData());
					System.out.println("C");
					try {
						
						partFile.writeFile();
						
					} catch (PartMetException e) {
						e.printStackTrace();
						throw new SharedFileException("Failed to save part.met file\n"+Misc.getStackTrace(e));
						
					}
					System.out.println("D");
					checkFilePartsIntegrity();
					
				}catch(Throwable t) {
					
				}
				
			}
		}
	}
	
}
