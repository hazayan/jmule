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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.jmule.core.JMIterable;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.downloadmanager.DownloadManagerFactory;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.metfile.KnownMet;
import org.jmule.core.edonkey.metfile.KnownMetEntity;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadSession;

public class SharingManagerImpl implements SharingManager {
	
	private Logger log = Logger.getLogger(SharingManagerImpl.class.toString());
	
	private Map<FileHash,SharedFile> sharedFiles;
	private LoadCompletedFiles load_completed_files;
	private LoadPartialFiles load_partial_files;
	private SharedFile current_hashing_file;
	
	private List<CompletedFileListener> completed_file_listeners = new LinkedList<CompletedFileListener>();
	
	private List<SharedFile> new_files = new CopyOnWriteArrayList<SharedFile>();
	private Timer sharing_manager_timer;
	private TimerTask rescan_dirs_task;
	
	public void initialize() {

		log.info("Initialize method");
		
		sharing_manager_timer = new Timer();
	    sharedFiles = new ConcurrentHashMap<FileHash,SharedFile>();
	     
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
					for(PartialFile shared_file : getPartialFiles())
						total_bytes += shared_file.getDownloadedBytes();
					
					for(CompletedFile shared_file : getCompletedFiles())
						total_bytes += shared_file.length();
					
					values.put(JMuleCoreStats.ST_DISK_SHARED_FILES_BYTES, total_bytes);
				}
				if (types.contains(JMuleCoreStats.ST_DISK_SHARED_FILES_PARTIAL_BYTES)) { 
					long total_bytes = 0;
					for(PartialFile shared_file : getPartialFiles())
						total_bytes += shared_file.getDownloadedBytes();
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
	     
	     JMuleCoreFactory.getSingleton().getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {
	    	 public void sharedDirectoriesChanged(List<File> sharedDirs) {
	    		 loadCompletedFiles();
	     }});
	}
	
	/**
	 * A set of actions that are applied on the given file
	 * This interface is meant to be used in conjunction with traverseDir method
	 * @see {@link #traverseDir(File, boolean, WorkOnFiles) traverseDir}
	 * @author javajox
	 */
	private interface WorkOnFiles {
		/**
		 * A set of actions that are applied on the given file
		 * @param file
		 */
		public void doWork(File file);
		
		/**
		 * Tells if we still need to traverse the directory
		 * This method is very important, if we need to interrupt the "traverse process" without side effects we MUST
		 * call this method
		 */
		public boolean stopTraverseDir();
	}
	
	/**
	 * Traverse a directory in a non-recursive mode
	 * @param dir the directory that we have to traverse
	 * @param with_part_met_extension has a role of filter, if given, only files with part.met at the end of file name are permitted  
	 * @param work_on_files the actions that are applied on the found file
	 */
	private void traverseDir(File dir, boolean with_part_met_extension, WorkOnFiles work_on_files) {
		log.info("Traverse : " + dir + ", with_part_met_extension = " + with_part_met_extension);
		Queue<File> list_of_dirs = new LinkedList<File>();
		File[] list_of_files;
		File current_dir;
		
		list_of_dirs.offer( dir );
		while( list_of_dirs.size() != 0 ) {
			if( work_on_files.stopTraverseDir() ) return;
			current_dir = list_of_dirs.poll();
			if( with_part_met_extension )
			   list_of_files = current_dir.listFiles( new FileFilter() {
				   @Override
				   public boolean accept(File file) {
                       if( file.isDirectory() ) return true;
                       if( file.getName().endsWith( PART_MET_EXTENSION ) ) return true;
                       return false;
				   }
				   
			   });
			else
				list_of_files = current_dir.listFiles();
			for(File file : list_of_files) {
				if( work_on_files.stopTraverseDir() ) return;
				if( file.isDirectory() ) list_of_dirs.offer( file );
				else {
					log.info("Do work on file > " + file.getName());
					work_on_files.doWork( file );
				}
			}
		}
	}
	
	public void start() {
		rescan_dirs_task = new TimerTask() {
			public void run() {
				loadCompletedFiles();
			}
		};
		sharing_manager_timer.scheduleAtFixedRate(rescan_dirs_task, ConfigurationManager.DIR_RESCAN_INTERVAL, ConfigurationManager.DIR_RESCAN_INTERVAL);
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
			traverseDir(shared_dir, true, new WorkOnFiles() {
				  @Override
				  public void doWork(File file) {
				    	try {
				    		PartMet part_met = new PartMet(file);				    
				    		part_met.loadFile();
				    		PartialFile partial_shared_file = new PartialFile(part_met);
				    		sharedFiles.put(partial_shared_file.getFileHash(), partial_shared_file);
				    		new_files.add(partial_shared_file);
				    		JMuleCoreFactory.getSingleton().getDownloadManager().addDownload(partial_shared_file);				    		
				    	}catch(Throwable t) { t.printStackTrace();}
				  }
				@Override
				public boolean stopTraverseDir() {

					return stop;
				}
				  
			});
		    isDone = true;
		}
		
		public double getPercent() {
			
			return 0;
		}
	}
	
	private class LoadCompletedFiles extends JMFileTask {
		
		private FileHashing file_hashing;
		private List<CompletedFile> files_needed_to_hash;
		
	    Map<String,KnownMetEntity> known_file_list;
	    Set<FileHash> files_hash_set = new HashSet<FileHash>();
		
		public void JMStop() {
			
			super.JMStop();
			
			if( ( file_hashing != null ) && ( file_hashing.isAlive() ) ) file_hashing.JMStop();
			
		}
		
		public void run() {
			isDone = false;
			String knownFilePath = ConfigurationManager.KNOWN_MET;
			
		     // new files from user's shared dirs that need to be hashed
		     files_needed_to_hash = new CopyOnWriteArrayList<CompletedFile>();
		     
		     files_hash_set.clear();
		     for(FileHash fileHash : sharedFiles.keySet())
		    	 if (!DownloadManagerFactory.getInstance().hasDownload(fileHash))
		    		 	files_hash_set.add(fileHash);
		     
		     // load shared completed files
		     try {
		    	 KnownMet known_met = new KnownMet(knownFilePath);
			     known_file_list = known_met.loadFile();
			     known_met.close();
		     } catch (Throwable e) {
		    	 e.printStackTrace();
			     known_file_list = new Hashtable<String,KnownMetEntity>();
		     }

		     File incoming_dir = new File(ConfigurationManager.INCOMING_DIR);
		     List<File> shared_dirs = ConfigurationManagerFactory.getInstance().getSharedFolders();
		     if (shared_dirs==null)
		    	 shared_dirs = new LinkedList<File>(); 
		     shared_dirs.add(incoming_dir);
		     
		     for(File dir : shared_dirs) {
		    	 if( stop ) return;		    	 	    	 
		    	 traverseDir(dir, false, new WorkOnFiles() {
			    	String file_name;
			    	long file_size;
			    	KnownMetEntity known_met_entity = null;
					@Override
					public void doWork(File file) {
			    		 file_name = file.getName(); 
			    		 file_size = file.length();
			    		 known_met_entity = known_file_list.get(file_name + file_size);
			    		 if( known_met_entity == null) {
			    			 files_needed_to_hash.add(new CompletedFile(file));
			    		 } else {
			    			 FileHash hash = known_met_entity.getFileHash();
			    			 if (files_hash_set.contains(hash))
			    				 files_hash_set.remove(hash);
			    			 if (sharedFiles.get(hash)!= null) return; // file already in list
			    			 CompletedFile shared_completed_file = new CompletedFile(file);
			    			 try {
								shared_completed_file.setHashSet(known_met_entity.getPartHashSet());
							} catch (SharedFileException e) {
								e.printStackTrace();
							}
			    			 shared_completed_file.setTagList(known_met_entity.getTagList());
			    			 sharedFiles.put(shared_completed_file.getFileHash(), shared_completed_file);
			    			 new_files.add(shared_completed_file);
			    		 }
			    		 known_met_entity = null;
					}
					@Override
					public boolean stopTraverseDir() {
						
						return stop;
					}		    		 
		    	 });

		    	 System.out.println("Finished ");
		      }
		     for(FileHash file_hash : files_hash_set) {
		    	 sharedFiles.remove(file_hash);
		     }
		     
	    	 // hash new files from the file system
		     boolean need_to_write_metadata = files_needed_to_hash.size() != 0;
	    	 for(CompletedFile shared_completed_file : files_needed_to_hash) {
	    		 current_hashing_file = null;
	    		 if( stop ) return;
	    		 try {
	    			 current_hashing_file = shared_completed_file;
	    			 FileChannel file_channel = new FileInputStream(shared_completed_file.getFile()).getChannel();
	    			 file_hashing = new FileHashing(file_channel);
	    			 file_hashing.start();
	    			 file_hashing.join();
	    			 file_channel.close();
	    			 files_needed_to_hash.remove(shared_completed_file);
	    			 if(!file_hashing.isDone()) 
	    				 continue;
	    			 if (sharedFiles.containsKey(file_hashing.getFileHashSet().getFileHash()))
	    				 continue;
	    			 shared_completed_file.setHashSet(file_hashing.getFileHashSet());
	    			 sharedFiles.put(shared_completed_file.getFileHash(), shared_completed_file);
	    			 new_files.add(shared_completed_file);
	    			 
	    			 notifyCompletedFileAdded(shared_completed_file);
	    		 } catch( Throwable t ) {
	    			current_hashing_file = null;
	    		 	t.printStackTrace(); 
	    		 }
	    	 }
	    	 current_hashing_file = null;
	    	 // write our files in known.met
	    	 if ( need_to_write_metadata ) {
	    		 writeMetadata();
	    	 } 
	    	 isDone = true;
		}
		
		public double getPercent() {
			if (file_hashing==null) return 100;
			if (!file_hashing.isAlive()) return 100;
			return file_hashing.getPercent();
		}
		
		public SharedFile getCurrentHashingFile() {
			return current_hashing_file;
		}
		
		public List<CompletedFile> getUnhashedFiles() {
			return files_needed_to_hash;
		}
		
	}
	
	public List<CompletedFile> getUnhashedFiles() {
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive()))
			return null;
		return load_completed_files.getUnhashedFiles();
	}
	
	public double getCurrentHashingFilePercent() {
			
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive())) return 0;
		
		return load_completed_files.getPercent(); 
    }
	
	public SharedFile getCurrentHashingFile() {
		
		if( ( load_completed_files == null ) || ( !load_completed_files.isAlive())) return null;
		
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
		if (load_completed_files != null)
			if (load_completed_files.isAlive()) 
				return ;
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
		shared_partial_file.closeFile();
		shared_partial_file.deletePartialFile();
		File completed_file = new File(incoming_dir.getAbsoluteFile() + File.separator + shared_partial_file.getSharingName());
		UploadManager upload_manager = JMuleCoreFactory.getSingleton().getUploadManager();
		try {
		
			if (upload_manager.hasUpload(fileHash)) { 	// JMule is now uploading file, need to synchronize moving
				UploadSession upload_sessison = upload_manager.getUpload(fileHash);
				synchronized(upload_sessison.getSharedFile()) {
					sharedFiles.remove(fileHash);
					log.info("Moving " + shared_partial_file.getFile() + " to " + completed_file);
					//FileUtils.moveFile(shared_partial_file.getFile(), completed_file);
					shared_partial_file.getFile().renameTo( completed_file );
					CompletedFile shared_completed_file = new CompletedFile(completed_file);
					shared_completed_file.setHashSet(shared_partial_file.getHashSet());
					sharedFiles.put(fileHash, shared_completed_file);
					new_files.add(shared_completed_file);
				}
			} else {
				sharedFiles.remove(fileHash);
				log.info("Moving " + shared_partial_file.getFile() + " to " + completed_file);
				//FileUtils.moveFile(shared_partial_file.getFile(), completed_file);
				shared_partial_file.getFile().renameTo( completed_file );
				CompletedFile shared_completed_file = new CompletedFile(completed_file);
				shared_completed_file.setHashSet(shared_partial_file.getHashSet());
				sharedFiles.put(fileHash, shared_completed_file);
				new_files.add(shared_completed_file);
			}
			writeMetadata();
		} catch (Throwable e) {
			e.printStackTrace();
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
	
	
	public void writeMetadata() {
   	   try {
   		  KnownMet known_met = new KnownMet(ConfigurationManager.KNOWN_MET);
   		  
   		  known_met.writeFile(sharedFiles.values());
   		  
   		  known_met.close();
	   } catch(Throwable t) {
		   t.printStackTrace();
	   }
	}
	
	public void shutdown() {
		sharing_manager_timer.cancel();
		
		stopLoadingCompletedFiles();
		
		stopLoadingPartialFiles();
		
	}
	
	public List<SharedFile> getUnsharedFiles() {
		return new_files;
	}
	
	public void resetUnsharedFiles() {
		new_files.clear();
		new_files.addAll(sharedFiles.values());
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
	public SharedFile getSharedFile(File file) {
		for(SharedFile shared_file : sharedFiles.values()){
			if (shared_file.getAbsolutePath().equals(file.getAbsolutePath()))
				return shared_file;
		}
		
		return null;
	}

	public void removeSharedFile(FileHash fileHash) {
		SharedFile shared_file = sharedFiles.get(fileHash);
		if (shared_file==null) return ;
		sharedFiles.remove(shared_file);
		if (shared_file instanceof CompletedFile) return ;
		shared_file.closeFile();
		shared_file.delete();
	}
}
