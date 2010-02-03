/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.publisher;

import static org.jmule.core.jkad.JKadConstants.MAX_PUBLISH_NOTES;
import static org.jmule.core.jkad.JKadConstants.MAX_PUBLISH_SOURCES;
import static org.jmule.core.jkad.JKadConstants.PUBLISHER_MAINTENANCE_INTERVAL;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;

/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.8 $
 * Last changed by $Author: binary255 $ on $Date: 2010/02/03 13:58:26 $
 */

public class Publisher {
	private static Publisher singleton = null;
	
	private Map<Int128, PublishKeywordTask> keywordTasks = new ConcurrentHashMap<Int128, PublishKeywordTask>();
	private Map<Int128, PublishNoteTask>    noteTasks    = new ConcurrentHashMap<Int128, PublishNoteTask>();
	private Map<Int128, PublishSourceTask>  sourceTasks  = new ConcurrentHashMap<Int128, PublishSourceTask>();
	
	private List<PublisherListener> listener_list = new LinkedList<PublisherListener>();
	
	private boolean isStarted = false;
	
	private PublishTaskListener keywordTaskListener;
	private PublishTaskListener noteTaskListener;
	private PublishTaskListener sourceTaskListener;
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public static Publisher getInstance() {
		if (singleton == null)
			singleton = new Publisher();
		return singleton;
	}
	
	public int getPublishSourcesCount() {
		return sourceTasks.size();
	}
	
	public int getPublishKeywordCount() {
		return keywordTasks.size();
	}
	
	public PublishKeywordTask getPublishKeywordTask(Int128 id) {
		return keywordTasks.get(id);
	}
	
	public PublishNoteTask getPublishNoteTask(Int128 id) {
		return noteTasks.get(id);
	}
	
	public PublishSourceTask getPublishSourceTask(Int128 id) {
		return sourceTasks.get(id);
	}
	
	private Task publisher_maintenance;
	
	private Publisher() {
		keywordTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeKeywordTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeKeywordTask(task.getPublishID());
			}
			
		};
		
		noteTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeNoteTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeNoteTask(task.getPublishID());
			}
			
		};
		
		sourceTaskListener = new PublishTaskListener() {
			public void taskStarted(PublishTask task) {
				
			}

			public void taskStopped(PublishTask task) {
				removeSourceTask(task.getPublishID());
			}

			public void taskTimeOut(PublishTask task) {
				removeSourceTask(task.getPublishID());
			}
			
		};
		
		publisher_maintenance = new Task() {
			public void run() {
				/*for(PublishKeywordTask task : keywordTasks.values()){
					if (Lookup.getSingleton().getLookupLoad()>INDEXTER_MAX_LOAD_TO_NOT_PUBLISH) return ;
					long currentTime = System.currentTimeMillis();
					if (currentTime - task.getLastpublishTime() >= task.getPublishInterval()) {
						task.start();
						notifyListeners(task, TaskStatus.STARTED);
					}
				}
				
				for(PublishNoteTask task : noteTasks.values()){
					if (Lookup.getSingleton().getLookupLoad()>INDEXTER_MAX_LOAD_TO_NOT_PUBLISH) return ;
					long currentTime = System.currentTimeMillis();
					if (currentTime - task.getLastpublishTime() >= task.getPublishInterval()) {
						task.start();
						notifyListeners(task, TaskStatus.STARTED);
					}
				}
				
				for(PublishSourceTask task : sourceTasks.values()){
					if (Lookup.getSingleton().getLookupLoad()>INDEXTER_MAX_LOAD_TO_NOT_PUBLISH) return ;
					long currentTime = System.currentTimeMillis();
					if (currentTime - task.getLastpublishTime() >= task.getPublishInterval()) {
						task.start();
						notifyListeners(task, TaskStatus.STARTED);
					}
				}*/
				
			}
			
		};
		
	}
	
	public void start() {
		isStarted = true;
		Timer.getSingleton().addTask(PUBLISHER_MAINTENANCE_INTERVAL, publisher_maintenance, true);
	}
	
	public void stop() {
		isStarted = false;
		Timer.getSingleton().removeTask(publisher_maintenance);
		for(Int128 key : keywordTasks.keySet())
			keywordTasks.get(key).stop();
		
		for(Int128 key : noteTasks.keySet())
			noteTasks.get(key).stop();
		
		for(Int128 key : sourceTasks.keySet())
			sourceTasks.get(key).stop();
	}
	
	public void publishKeyword(Int128 fileID, List<Tag> tagList) throws JKadException {
		PublishKeywordTask task = new PublishKeywordTask(keywordTaskListener,fileID, tagList);
		
		task.start();
		keywordTasks.put(fileID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	

		
	public void publishSource(Int128 fileID, List<Tag> tagList) throws JKadException  {
		PublishSourceTask task = new PublishSourceTask(sourceTaskListener,fileID, tagList);
		
		task.start();
		sourceTasks.put(fileID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	
	public void publishNote(Int128 fileID, List<Tag> tagList) throws JKadException {
		PublishNoteTask task = new PublishNoteTask(noteTaskListener,fileID, tagList);
		task.start();
		noteTasks.put(fileID, task);
		notifyListeners(task, TaskStatus.ADDED);
		notifyListeners(task, TaskStatus.STARTED);
	}
	
	public void stopPublishKeyword(Int128 fileID) {
		PublishKeywordTask task = keywordTasks.get(fileID);
		if (task == null) return ;
		task.stop();
		keywordTasks.remove(fileID);
		removeKeywordTask(fileID);
	}
	
	public void stopPublishSource(Int128 fileID) {
		PublishSourceTask task = sourceTasks.get(fileID);
		if (task == null) return ;
		task.stop();
		removeSourceTask(fileID);
	}
	
	public void stopPublishNote(Int128 fileID) {
		PublishNoteTask task = noteTasks.get(fileID);
		if (task == null) return ;
		task.stop();
		removeNoteTask(fileID);
	}
	
	public void processGenericResponse(Int128 id, int load) {
		PublishTask task = keywordTasks.get(id);
		if (task == null)
			task = sourceTasks.get(id);
		if (task == null) return ;
		task.addPublishedSources(1);
		if (task.getPublishedSources()>=MAX_PUBLISH_SOURCES) {
			task.stop();
			if (task instanceof PublishKeywordTask)
				removeKeywordTask(id);
			if (task instanceof PublishSourceTask)
				removeSourceTask(id);
		}
		
	}
	
	public void processNoteResponse(Int128 id, int load) {
		PublishTask task = noteTasks.get(id);
		if (task == null) return;
		task.addPublishedSources(1);
		if (task.getPublishedSources()>=MAX_PUBLISH_NOTES) {
			task.stop();
			removeNoteTask(id);
		}
		
	}
	
	public boolean isPublishingKeyword(Int128 id) { return keywordTasks.containsKey(id); }
	public boolean isPublishingNote(Int128 id) { return noteTasks.containsKey(id); }
	public boolean isPublishingSource(Int128 id) { return sourceTasks.containsKey(id); }
	
	void removeKeywordTask(Int128 id) {
		PublishTask task = keywordTasks.get(id);
		keywordTasks.remove(id); 
		notifyListeners(task, TaskStatus.REMOVED);
	}
	
	void removeSourceTask(Int128 id) {
		PublishTask task = sourceTasks.get(id);
		sourceTasks.remove(id);
		notifyListeners(task, TaskStatus.REMOVED);
	}
	
	void removeNoteTask(Int128 id) {
		PublishTask task = noteTasks.get(id);
		noteTasks.remove(id);
		notifyListeners(task, TaskStatus.REMOVED);
	}
	
	public void addListener(PublisherListener listener) {
		listener_list.add(listener);
	}
	
	public void removeListener(PublisherListener listener) {
		listener_list.remove(listener);
	}
	
	private enum TaskStatus {ADDED, STARTED, REMOVED}
	
	private void notifyListeners(PublishTask task, TaskStatus status) {
		for(PublisherListener listener : listener_list)
			if (status == TaskStatus.ADDED)
				listener.publishTaskAdded(task);
			else
				if (status == TaskStatus.STARTED)
					listener.publishTaskStarted(task);
				else
					listener.publishTaskRemoved(task);
	}
	
	interface PublishTaskListener{
		public void taskStarted(PublishTask task);
		public void taskTimeOut(PublishTask task);
		public void taskStopped(PublishTask task);
	}
	
}