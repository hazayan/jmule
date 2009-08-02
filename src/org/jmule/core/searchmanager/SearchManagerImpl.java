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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKad;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;

/**
 * Created on 2008-Jul-06
 * @author javajox
 * @version $$Revision: 1.8 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/08/02 08:04:04 $$
 */
public class SearchManagerImpl implements SearchManager {

	private List<SearchResult> search_result_list = new LinkedList<SearchResult>();
	private Queue<SearchQuery> server_search_request_queue = new LinkedList<SearchQuery>();
	private Queue<SearchQuery> kad_search_request_queue = new LinkedList<SearchQuery>();
	private ServerManager server_manager = ServerManagerFactory.getInstance();
	private Search jkad_search = Search.getSingleton();
	private JKad jkad = JKad.getInstance();
	private List<SearchResultListener> search_result_listeners = new LinkedList<SearchResultListener>();

	private Map<SearchQuery,Int128> kad_searches = new HashMap<SearchQuery, Int128>();
	
	private long searches_count = 0;
	
	public synchronized void addResult(SearchResult searchResult) {
		server_search_request_queue.peek(); // remove last request
		
		server_search_request_queue.remove(searchResult.getSearchQuery());
        search_result_list.add(searchResult);
        
        notifySearchArrived(searchResult);
        notifySearchCompleted(searchResult.searchQuery);
        processServerSearchRequest();
	}

	public synchronized void removeResult(SearchResult searchResult) {
		search_result_list.remove(searchResult);		
	}
	

	public synchronized void removeSearch(SearchQuery searchRequest) {
		server_search_request_queue.remove(searchRequest);
		kad_search_request_queue.remove(searchRequest);
		if (kad_searches.containsKey(searchRequest)) {
			synchronized (kad_searches) {
				Int128 searchID = kad_searches.get(searchRequest);
				jkad_search.cancelSearch(searchID);
			}
		}
	}

	public void search(String searchString) {
		SearchQuery search_request = new SearchQuery(searchString);
		search(search_request);		
	}

	public synchronized void search(SearchQuery searchRequest) {
		if (searchRequest.getQueryType()==SearchQueryType.SERVER) {
			server_search_request_queue.add(searchRequest);
			
			if (server_search_request_queue.size()==1)
				processServerSearchRequest();
		}
		if (searchRequest.getQueryType()==SearchQueryType.KAD) {
			if (!jkad.isConnected()) return;
			kad_search_request_queue.add(searchRequest);
			if (kad_search_request_queue.size()==1)
				processKadSearchRequest();
		}
		if (searchRequest.getQueryType()==SearchQueryType.SERVER_KAD) {
			if (jkad.isConnected()) {
			kad_search_request_queue.add(searchRequest);
			if (kad_search_request_queue.size()==1)
				processKadSearchRequest();
			}
			
			server_search_request_queue.add(searchRequest);
			if (server_search_request_queue.size()==1)
				processServerSearchRequest();
			
			
		}
		
		searches_count++;
	}

	public void addSeachResultListener(SearchResultListener searchResultListener) {
		search_result_listeners.add(searchResultListener);
	}
	
	private void notifySearchArrived(SearchResult search_result) {
		for(SearchResultListener listener : search_result_listeners) {
			listener.resultArrived(search_result);
		}
	}
	
	private void notifySearchStarted(SearchQuery query) {
		for(SearchResultListener listener : search_result_listeners) {
			listener.searchStarted(query);
		}
	}
	
	private void notifySearchCompleted(SearchQuery query) {
		for(SearchResultListener listener : search_result_listeners) {
			listener.searchCompleted(query);
		}
	}
	
	
	public void initialize() {
		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.SEARCHES_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.SEARCHES_COUNT)) {
					values.put(JMuleCoreStats.SEARCHES_COUNT, searches_count);
				}
			}
		});
	}

	public void shutdown() {
		
		
	}

	public void start() {
		
		
	}

	private void processServerSearchRequest() {
		if (server_search_request_queue.isEmpty()) return ;
		SearchQuery search_request;
        search_request = server_search_request_queue.peek(); // search request is removed after arrival of response
        Server server = server_manager.getConnectedServer();
        server.searchFiles(search_request);
        notifySearchStarted(search_request);
	}
	SearchQuery search_request;
	private void processKadSearchRequest() {
		if (kad_search_request_queue.isEmpty()) return ;
		search_request = (SearchQuery) kad_search_request_queue.peek(); // remove query from queue after complete search
        Int128 keyword_id;
			try {
				keyword_id = jkad_search.searchKeyword(search_request.getQuery(), new org.jmule.core.jkad.search.SearchResultListener() {
					SearchQuery r = (SearchQuery) search_request.clone();
					SearchResultItemList result_list = new SearchResultItemList();
					SearchResult search_result = new SearchResult(result_list,r );		
					
					public void processNewResults(List<Source> result) {
								result_list.clear();
								for(Source source : result) {
									SearchResultItem item = new SearchResultItem(source.getClientID().toFileHash(),null,(short)0,SearchQueryType.KAD);
									for(Tag tag : source.getTagList()) {
										item.addTag(tag);
									}
									result_list.add(item);
								}
								notifySearchArrived(search_result);
							}

							public void searchFinished() {
								notifySearchCompleted(r);
								kad_search_request_queue.poll();
								processKadSearchRequest();
								
							}

							public void searchStarted() {
								notifySearchStarted(r);
							}
							
						});
				kad_searches.put(search_request, keyword_id);
				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		
		
	}
	
}
