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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.utils.Convert;
import org.jmule.core.jkad.utils.MD4;


/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Search {
	
	private static Search singleton = null;
	
	private Map<Int128, SearchTask> searchTasks = new ConcurrentHashMap<Int128, SearchTask>();
	
	private boolean isStarted = false;
	
	public boolean isStarted() {
		return isStarted;
	}
	
	public static Search getSingleton() {
		if (singleton == null)
			singleton = new Search();
		return singleton;
	}
	
	private Search() {
	}
	
	public void start() {
		isStarted = true;
	}
	
	public void stop() {
		isStarted = false;
		for(Int128 key : searchTasks.keySet()) {
			SearchTask task = searchTasks.get(key);
			if (task.isStarted()) task.stopSearch();
		}
	}
	
	public Int128 searchKeyword(String keyword) {
		return searchKeyword(keyword, null);
	}
	
	public Int128 searchKeyword(String keyword, SearchResultListener listener) {
		byte[] tmp = MD4.MD4Digest(keyword.getBytes()).toByteArray();
		Convert.updateSearchID(tmp);
		Int128 keywordID = new Int128(tmp);
		
		if (searchTasks.containsKey(keywordID)) return null;
		KeywordSearchTask search_task = new KeywordSearchTask(keywordID);
		search_task.setSearchResultListener(listener);
		searchTasks.put(keywordID, search_task);
		search_task.startSearch();
		
		return keywordID;
	}

	public void searchSources(Int128 fileID) {
		searchSources(fileID, null);
	}
	
	public void searchSources(Int128 fileID,SearchResultListener listener) {
		if (searchTasks.containsKey(fileID)) return;
		SourceSearchTask search_task = new SourceSearchTask(fileID);
	
		search_task.setSearchResultListener(listener);
		
		searchTasks.put(fileID, search_task);
		search_task.startSearch();
	}

	public void searchNotes(Int128 fileID) {
		searchNotes(fileID,null);
	}
	
	public void searchNotes(Int128 fileID,SearchResultListener listener) {
		byte[] t = fileID.toByteArray();
		Convert.updateSearchID(t);
		Int128 updatedID = new Int128(t);
		if (searchTasks.containsKey(updatedID)) return;
		NoteSearchTask search_task = new NoteSearchTask( updatedID);
		search_task.setSearchResultListener(listener);
		searchTasks.put(updatedID, search_task);
		search_task.startSearch();
	}
	
	public void processResults(InetSocketAddress sender, Int128 targetID, List<Source> sources) {
		SearchTask task = searchTasks.get(targetID);
		if (task == null) return ;
		
		task.addSearchResult(sources);
	}
	
	public boolean hasSearchTask(Int128 searchID) {
		return searchTasks.containsKey(searchID);
	}
	
	public List<Source> getSearchResults(Int128 searchID) {
		return searchTasks.get(searchID).getSearchResults();
	}

	public void cancelSearch(Int128 searchID) {
		searchTasks.get(searchID).stopSearch();
	}
}
