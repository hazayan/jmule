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
package org.jmule.core.jkad.search;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.MD4;


/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.7 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/25 10:32:33 $
 */
public class Search {
	
	private static Search singleton = null;
	
	private Map<Int128, SearchTask> searchTasks = new ConcurrentHashMap<Int128, SearchTask>();
	
	private boolean isStarted = false;
		
	public static Search getSingleton() {
		if (singleton == null)
			singleton = new Search();
		return singleton;
	}
	
	private Search() {
	}
	
	public SearchTask getSearchTask(Int128 id) {
		return searchTasks.get(id);
	}
	
	public void start() {
		isStarted = true;
	}
	
	public void stop() {
		for(Int128 key : searchTasks.keySet()) {
			SearchTask task = searchTasks.get(key);
			if (task.isStarted()) task.stopSearch();
		}
		isStarted = false;
	}
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public Int128 searchKeyword(String keyword) throws JKadException {
		return searchKeyword(keyword, null);
	}
	
	public Int128 searchKeyword(String keyword, SearchResultListener listener) throws JKadException {
		byte[] tmp = MD4.MD4Digest(keyword.getBytes()).toByteArray();
		Convert.updateSearchID(tmp);
		Int128 keywordID = new Int128(tmp);
		
		if (searchTasks.containsKey(keywordID)) return null;
		KeywordSearchTask search_task = new KeywordSearchTask(keywordID);
		search_task.setSearchKeyword(keyword);
		search_task.setSearchResultListener(listener);
		searchTasks.put(keywordID, search_task);
		search_task.startSearch();
		
		return keywordID;
	}

	public void searchSources(Int128 fileID,long fileSize) throws JKadException {
		searchSources(fileID, null,fileSize);
	}
	
	public void searchSources(Int128 fileID,SearchResultListener listener,long fileSize) throws JKadException {
		if (searchTasks.containsKey(fileID)) return;
		SourceSearchTask search_task = new SourceSearchTask(fileID,fileSize);
	
		search_task.setSearchResultListener(listener);
		
		searchTasks.put(fileID, search_task);
		search_task.startSearch();
	}

	public void searchNotes(Int128 fileID,long fileSize) throws JKadException {
		searchNotes(fileID,null,fileSize);
	}
	
	public void searchNotes(Int128 fileID,SearchResultListener listener, long fileSize) throws JKadException {
		byte[] t = fileID.toByteArray();
		Convert.updateSearchID(t);
		Int128 updatedID = new Int128(t);
		if (searchTasks.containsKey(updatedID)) return;
		NoteSearchTask search_task = new NoteSearchTask( updatedID,fileSize);
		search_task.setSearchResultListener(listener);
		searchTasks.put(updatedID, search_task);
		search_task.startSearch();
	}
	
	public void processResults(IPAddress sender, final Int128 targetID, final List<Source> sources) {
		SearchTask task = searchTasks.get(targetID);
		if (task == null) return ;
				task.addSearchResult(sources);
	}
	
	public boolean hasSearchTask(Int128 searchID) {
		return searchTasks.containsKey(searchID);
	}
	
	public Collection<Source> getSearchResults(Int128 searchID) {
		return searchTasks.get(searchID).getSearchResults();
	}

	public void cancelSearch(Int128 searchID) {
		if (!hasSearchTask(searchID)) return ;
		searchTasks.get(searchID).stopSearchRequest();
		searchTasks.remove(searchID);
	}
	
	void removeSearchID(Int128 searchID) {
		searchTasks.remove(searchID);
	}
	
	public String toString() {
		String result = " [ ";
		
		for(Int128 key : searchTasks.keySet()) {
			SearchTask task = searchTasks.get(key);
			result += "Task ID : " + key.toHexString() + "\n";
			result += "Value   : \n" + task + "\n";
		}
		
		result += " ] ";
		
		return result;
	}
	
}
