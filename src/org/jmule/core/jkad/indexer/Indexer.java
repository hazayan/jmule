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
package org.jmule.core.jkad.indexer;

import static org.jmule.core.jkad.JKadConstants.INDEXER_SAVE_DATA_INTERVAL;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_KEYWORDS;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_NOTES;
import static org.jmule.core.jkad.JKadConstants.INDEX_MAX_SOURCES;
import static org.jmule.core.jkad.JKadConstants.KEY_INDEX_DAT;
import static org.jmule.core.jkad.JKadConstants.NOTE_INDEX_DAT;
import static org.jmule.core.jkad.JKadConstants.SRC_INDEX_DAT;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.logger.Logger;
import org.jmule.core.jkad.utils.timer.Task;
import org.jmule.core.jkad.utils.timer.Timer;

/**
 * Created on Jan 5, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Indexer {
	
	private static Indexer singleton = null;
	
	public static Indexer getSingleton() {
		if (singleton == null)
			singleton = new Indexer();
		return singleton;
	}
	
	private Map<Int128, Index> notes = new ConcurrentHashMap<Int128,Index>();
	private Map<Int128, Index> keywords = new ConcurrentHashMap<Int128, Index>();
	private Map<Int128, Index> sources = new ConcurrentHashMap<Int128, Index>();
	
	private Task saveDataTask;
	
	private boolean isStarted = false;
	
	public boolean isStarted() {
		return isStarted;
	}

	private Indexer() {
		
	}
	
	public void start() {
		isStarted = true;
		try {
			notes.putAll(SrcIndexDat.loadFile(NOTE_INDEX_DAT));
			keywords.putAll(SrcIndexDat.loadFile(KEY_INDEX_DAT));
			sources.putAll(SrcIndexDat.loadFile(SRC_INDEX_DAT));
		} catch (Throwable e1) {
			Logger.getSingleton().logException(e1);
			e1.printStackTrace();
		}
		Logger.getSingleton().logMessage("Loaded notes : " + notes.size());
		Logger.getSingleton().logMessage("Loaded keywords : " + keywords.size());
		Logger.getSingleton().logMessage("Loaded sources : " + sources.size());
		
		saveDataTask = new Task() {
			public void run() {
				try {
					SrcIndexDat.writeFile(NOTE_INDEX_DAT, notes);
				} catch (Throwable e) {
					Logger.getSingleton().logException(e);
					e.printStackTrace();
				}
				try {
					SrcIndexDat.writeFile(KEY_INDEX_DAT, keywords);
				} catch (Throwable e) {
					Logger.getSingleton().logException(e);
					e.printStackTrace();
				}
				try {
						SrcIndexDat.writeFile(SRC_INDEX_DAT, sources);
				} catch (Throwable e) {
					Logger.getSingleton().logException(e);
					e.printStackTrace();
				}
				
			}
		};
		
		Timer.getSingleton().addTask(INDEXER_SAVE_DATA_INTERVAL, saveDataTask, true);
		
	}

	public void stop() {
		isStarted = false;
		Timer.getSingleton().removeTask(saveDataTask);
	}
	
	public int getKeywordLoad() {
		return (keywords.size() / INDEX_MAX_KEYWORDS) * 100;
	}
	
	public int getNoteLoad() {
		return (notes.size() / INDEX_MAX_NOTES) * 100;
	}
	
	public int getFileSourcesLoad() {
		return (sources.size() / INDEX_MAX_SOURCES) * 100;
	}
	
	public void addFileSource(Int128 fileID, Source source) {
		Index index = sources.get(fileID);
		if (index == null) {
			index = new SourceIndex(fileID);
			sources.put(fileID,index);
		}
		index.addSource(source);
	}
	
	public void addKeywordSource(Int128 keywordID, Source source) {
		Index index = keywords.get(keywordID);
		if (index == null) {
			index = new KeywordIndex(keywordID);
			keywords.put(keywordID, index);
		}
		index.addSource(source);
		
	}
	
	public void addNoteSource(Int128 noteID, Source source) {
		Index index = notes.get(noteID);		
		if (index == null) {
			index = new NoteIndex(noteID);
			notes.put(noteID, index);
		}
		index.addSource(source);
	}

	public List<Source> getFileSources(Int128 targetID) {
		Index indexer =  sources.get(targetID);
		if (indexer == null) return null;
		return indexer.getSourceList();
	}
	
	public List<Source> getFileSources(Int128 targetID,short start_position, long fileSize) {
		return this.getFileSources(targetID);
	}

	public List<Source> getKeywordSources(Int128 targetID) {
		Index indexer = keywords.get(targetID);
		if (indexer == null) return null;
		return indexer.getSourceList();
	}

	public List<Source> getNoteSources(Int128 noteID) {
		Index indexer = notes.get(noteID);
		if (indexer == null) return null;
		return indexer.getSourceList();
	}
	public List<Source> getNoteSources(Int128 noteID, long fileSize) {
		return getNoteSources(noteID);
	}

	
}
