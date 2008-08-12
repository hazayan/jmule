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
package org.jmule.core.searchmanager;

import java.util.LinkedList;
import java.util.List;

import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.impl.Server;

/**
 * Created on 2008-Jul-06
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/12 07:20:15 $$
 */
public class SearchManagerImpl implements SearchManager {

	List<SearchResult> search_result_list = new LinkedList<SearchResult>();
	List<SearchRequest> search_request_list = new LinkedList<SearchRequest>();
	ServerManager server_manager = ServerManagerFactory.getInstance();
	List<SearchResultListener> search_result_listeners = new LinkedList<SearchResultListener>();
	
	public synchronized void addResult(SearchResult searchResult) {
		search_request_list.remove(searchResult.getSearchRequest());
        search_result_list.add(searchResult);
        
        searchArrived(searchResult);
        SearchRequest search_request;
        if(!search_request_list.isEmpty()) {
          search_request = ((LinkedList<SearchRequest>)search_request_list).removeLast();	
          Server server = server_manager.getConnectedServer();
          server.searchFiles(search_request);
        }

	}

	public synchronized void removeResult(SearchResult searchResult) {
		search_result_list.remove(searchResult);		
	}
	

	public synchronized void removeSearch(SearchRequest searchRequest) {
		search_request_list.remove(searchRequest);
	}

	public void search(String searchString) {
		SearchRequest search_request = new SearchRequest(searchString);
		search(search_request);
	}

	public synchronized void search(SearchRequest searchRequest) {
		((LinkedList<SearchRequest>)search_request_list).addFirst(searchRequest);
		if (search_request_list.size()==1) {
			Server server = server_manager.getConnectedServer();
	        server.searchFiles(searchRequest);
		}
		
	}

	public void addSeachResultListener(SearchResultListener searchResultListener) {
		search_result_listeners.add(searchResultListener);
	}
	
	private void searchArrived(SearchResult search_result) {
		
		for(SearchResultListener listener : search_result_listeners) {
			
			listener.resultArrived(search_result);
			
		}
		
	}
	
	public void initialize() {
		
		
	}

	public void shutdown() {
		
		
	}

	public void start() {
		
		
	}

}
