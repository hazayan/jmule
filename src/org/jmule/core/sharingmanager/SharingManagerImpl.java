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
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.jmule.core.JMIterable;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.metfile.KnownMet;
import org.jmule.core.edonkey.metfile.KnownMetEntity;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/18 08:55:17 $$
 */
public class SharingManagerImpl implements SharingManager {

	private Map<FileHash,SharedFile> sharedFiles;
	private LoadCompletedFiles load_completed_files;
	private LoadPartialFiles load_partial_files;
	private String current_hashing_file;
	private List<CompletedFileListener> completed_file_listeners = new LinkedList<CompletedFileListener>();
	
	public void initialize() {
	     sharedFiles = new Hashtable<FileHash,SharedFile>();
	     
	     Set<String> types = new HashSet<String>();
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT);
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT);
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT);
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES);
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES);
	     types.add(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES);
	     
	     JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT))
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COUNT, sharedFiles.size());
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT)) 
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_COUNT, getPartialFiles().size());
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT)) 
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_COUNT, getCompletedFiles().size());
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES)) { 
					long total_bytes = 0;
					for(SharedFile shared_file : sharedFiles.values())
						total_bytes += shared_file.length();
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES, total_bytes);
				}
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES)) { 
					long total_bytes = 0;
					for(PartialFile shared_file : getPartialFiles())
						total_bytes += shared_file.length();
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES, total_bytes);
				}
				
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES)) { 
					long total_bytes = 0;
					for(CompletedFile shared_file : getCompletedFiles())
						total_bytes += shared_file.length();
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_COMPLETE_BYTES, total_bytes);
				}
			}
	     });
	}
	
	public void start() {
		
	}
	
	public boolean isLoadingCompletedFileProcessRunning() {
		
		if(load_completed_files == null) return false;
		
		return load_completed_files.isDone();
		
	}
	
	public boolean isLoadingPartialFileProcessRunning() {
		
        if( load_partial_files == null ) return false;
        
        return load_partial_files.isDone();
	}
	
	private class LoadPartialFiles extends JMFileTask {
		
		public void run() {
			isDone = false;
			File shared_dir = new File(ConfigurationManager.TEMP_DIR);
			Iterator<File> i = FileUtils.iterateFiles(shared_dir, new String[]{"part.met"}, false);
		    while(i.hasNext()) {
		    	if( stop ) return;
		    	try {
		    		PartMet part_met = new PartMet(i.next());
		    
		    		part_met.loadFile();
		    		PartialFile partial_shared_file = new PartialFile(part_met);
		    		sharedFiles.put(partial_shared_file.getFileHash(), partial_shared_file);
		    	}catch(Throwable t) { t.printStackTrace();}
		    }
		    isDone = true;
		}
		
		public double getPercent() {
			
			return 0;
		}
	}
	
	private class LoadCompletedFiles extends JMFileTask {
		
		private FileHashing file_hashing;
		
		public void JMStop() {
			
			super.JMStop();
			
			if( ( file_hashing != null ) && ( file_hashing.isAlive() ) ) file_hashing.JMStop();
			
		}
		
		public void run() {
			isDone = false;
			String knownFilePath = ConfigurationManager.KNOWN_MET;
			
		     Map<String,KnownMetEntity> known_file_list;
		
		     // new files from user's shared dirs that need to be hashed
		     List<File> files_needed_to_hash = new LinkedList<File>();
		
		     sharedFiles.clear();
		     
		     // load shared completed files
		     try {
		    	 KnownMet known_met = new KnownMet(knownFilePath);
			     known_file_list = known_met.loadFile();
		     } catch (Throwable e) {
			     known_file_list = new Hashtable<String,KnownMetEntity>();
		     }
		     File incoming_dir = new File(ConfigurationManager.INCOMING_DIR);
		     List<File> shared_dirs = ConfigurationManagerFactory.getInstance().getSharedFolders();
		     if (shared_dirs==null)
		    	 shared_dirs = new LinkedList<File>(); 
		     shared_dirs.add(incoming_dir);
		     
		     for(File dir : shared_dirs) {
		    	 if( stop ) return;
		    	 Iterator<File> i = FileUtils.iterateFiles(dir, null, true);
		    	 String file_name;
		    	 long file_size;
		    	 KnownMetEntity known_met_entity = null;
		    	 // checks out if the files from file system are stored in known.met, they need to be hashed otherwise
		    	 while(i.hasNext()) {
		    		 if( stop ) return;
		    		 File f = i.next();
		    		 file_name = f.getName();
		    		 file_size = f.length();
		    		 known_met_entity = known_file_list.get(file_name + file_size);
		    		 if( known_met_entity == null) {
		    			 files_needed_to_hash.add(f);
		    		 } else {
		    			 CompletedFile shared_completed_file = new CompletedFile(f);
		    			 shared_completed_file.setHashSet(known_met_entity.getPartHashSet());
		    			 shared_completed_file.setTagList(known_met_entity.getTagList());
		    			 sharedFiles.put(shared_completed_file.getFileHash(), shared_completed_file);
		    		 }
		    		 known_met_entity = null;
		    	 }  
		      }
	    	 // hash new files from the file system
	    	 for(File f : files_needed_to_hash) {
	    		 if( stop ) return;
	    		 current_hashing_file = f.toString();
	    		 try {
	    		  FileChannel file_channel = new FileInputStream(f).getChannel();
	    		  file_hashing = new FileHashing(file_channel);
	    		  file_hashing.start();
	    		  file_hashing.join();
	    		  if(!file_hashing.isDone()) return;
	    		  //PartHashSet file_hash_set = MD4FileHasher.calcHashSets(file_channel);
	    		  CompletedFile shared_completed_file = new CompletedFile(f);
	    		  shared_completed_file.setHashSet(file_hashing.getFileHashSet());
	    		  sharedFiles.put(shared_completed_file.getFileHash(), shared_completed_file);
	    		  notifyCompletedFileAdded(shared_completed_file);
	    		 } catch( Throwable t ) {
	    			 t.printStackTrace(); 
	    		 }
	    	 }
	    	 // write our files in known.met
	    	 if (files_needed_to_hash.size() !=0 ) {
	    		 writeMetadata();
	    	 } 
	    	 
	    	 isDone = true;
		}
		
		public double getPercent() {
			
			return file_hashing.getPercent();
			
		}
		
		public String getCurrentHashingFile() {
			
			return current_hashing_file;
			
		}
		
	}
	
	public double getCurrentHashingFilePercent() {
			
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive())) return 0;
		
		return load_completed_files.getPercent(); 
    }
	
	public String getCurrentHashingFile() {
		
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive())) return "";
		
		return load_completed_files.getCurrentHashingFile();
	}
	
	public void stopLoadingCompletedFiles() {
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive())) return;
		load_completed_files.JMStop();
	}
	
	public void stopLoadingPartialFiles() {
		
		if( ( load_partial_files == null ) || ( !load_partial_files.isAlive())) return;
		
		load_partial_files.JMStop();
		
	}
	
	public void loadCompletedFiles() {
	  
		load_completed_files = new LoadCompletedFiles();
		
		load_completed_files.start();
	}
	
	public void loadPartialFiles() {

		load_partial_files = new LoadPartialFiles();
		
		load_partial_files.start();
 	}
	
	public CompletedFile getCompletedFile(FileHash fileHash) {
		SharedFile sharedFile = sharedFiles.get(fileHash);
		if (sharedFile == null) return null;
		if ( !sharedFile.exists() ) {
			sharedFiles.remove(fileHash);
			return null;
		}
		if (sharedFile instanceof CompletedFile) 
			return (CompletedFile)sharedFile;
		else
			return null;
	}
	
	public PartialFile getPartialFle(FileHash fileHash) {
		SharedFile sharedFile = sharedFiles.get(fileHash);
		if (sharedFile == null) return null;
		if ( !sharedFile.exists() ) {
			sharedFiles.remove(fileHash);
			return null;
		}
		if (sharedFile instanceof PartialFile) 
			return (PartialFile)sharedFile;
		else
			return null;
	}
	
	public void makeCompletedFile(FileHash fileHash) throws SharingManagerException {
		File incoming_dir = new File(ConfigurationManager.INCOMING_DIR);
		PartialFile shared_partial_file = getPartialFle(fileHash);
		if( shared_partial_file == null ) throw new SharingManagerException("The file " + fileHash + "doesn't exists");
		File file = new File(incoming_dir+"/"+ shared_partial_file.getSharingName());
		try {
			sharedFiles.remove(fileHash);
			File completed_file = new File(ConfigurationManager.TEMP_DIR+"/"+ shared_partial_file.getSharingName());
			
			shared_partial_file.getFile().renameTo(completed_file);
			FileUtils.moveFile(completed_file, file);
			CompletedFile shared_completed_file = new CompletedFile(file);
			shared_completed_file.setHashSet(shared_partial_file.getHashSet());
			sharedFiles.put(fileHash, shared_completed_file);
			writeMetadata();
		} catch (Throwable e) {
			throw new SharingManagerException( e );
		}
	}
	
	public void addPartialFile(PartialFile partialFile){
		sharedFiles.put(partialFile.getFileHash(), partialFile);
	}
	
	public int getFileCount() {
		return sharedFiles.size(); 
	}
	
	public boolean hasFile(FileHash fileHash) {
		return sharedFiles.containsKey(fileHash);
	}
	
	public boolean existsFile(FileHash fileHash) {
	    if( !hasFile(fileHash) ) return false;
	    SharedFile shared_file = sharedFiles.get(fileHash);
	    if( !shared_file.exists() ) return false;
	    return true;
	}
	
	public SharedFile getSharedFile(FileHash fileHash) {
		if (!hasFile(fileHash)) return null;
		SharedFile sharedFile = sharedFiles.get(fileHash);
		
		return sharedFile;
	}
	
	public JMIterable<SharedFile> getSharedFiles() {
		return new JMIterable<SharedFile>(sharedFiles.values().iterator());
	}
	
	public List<PartialFile> getPartialFiles() {
		List<PartialFile> file_list = new LinkedList<PartialFile>();
		
		for(SharedFile file : sharedFiles.values())
			if (file instanceof PartialFile)
					file_list.add((PartialFile) file);
		
		return file_list;
	}
	
	public List<CompletedFile> getCompletedFiles() {
		List<CompletedFile> file_list = new LinkedList<CompletedFile>();
		
		for(SharedFile file : sharedFiles.values())
			if (file instanceof CompletedFile)
					file_list.add((CompletedFile) file);
		
		return file_list;
	}
	
	/**
	 * Write the all meta-info about files from completed files hash table in known.met
	 */
	private void writeMetadata() {
   	   try {
   		  KnownMet known_met = new KnownMet(ConfigurationManager.KNOWN_MET);
   		  
   		  known_met.writeFile(sharedFiles.values());
	   } catch(Throwable t) { }
	}
	
	public void shutdown() {
		
		this.stopLoadingCompletedFiles();
		
		this.stopLoadingPartialFiles();
		
	}
	
	public void addCompletedFileListener(CompletedFileListener listener) {
		
		completed_file_listeners.add(listener);
		
	}
	
	public void removeCompletedFileListener(CompletedFileListener listener) {
		
		completed_file_listeners.remove(listener);
	}
	
	private void notifyCompletedFileAdded(CompletedFile file) {
		
		for(CompletedFileListener listener : completed_file_listeners) {
			
			listener.fileAdded( file );
			
		}
		
	}
}
