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
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.InternalJKadManager;
import org.jmule.core.jkad.JKadManagerSingleton;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.networkmanager.InternalNetworkManager;
import org.jmule.core.networkmanager.NetworkManagerSingleton;
import org.jmule.core.servermanager.ServerManager;
import org.jmule.core.servermanager.ServerManagerSingleton;
import org.jmule.core.statistics.JMuleCoreStats;
import org.jmule.core.statistics.JMuleCoreStatsProvider;
import org.jmule.core.utils.timer.JMTimer;
import org.jmule.core.utils.timer.JMTimerTask;

/**
 * Created on 2008-Jul-06
 * 
 * @author javajox
 * @version $$Revision: 1.9 $$ Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:17:43 $$
 */
public class SearchManagerImpl extends JMuleAbstractManager implements
		InternalSearchManager {

	private static int SEARCH_ANSWER_WAIT = 8000;

	private Search jkad_search = Search.getSingleton();
	private InternalJKadManager jkad = (InternalJKadManager) JKadManagerSingleton
			.getInstance();
	private InternalNetworkManager network_manager = (InternalNetworkManager) NetworkManagerSingleton
			.getInstance();

	private Map<SearchQuery, SearchResult> search_result_list = new ConcurrentHashMap<SearchQuery, SearchResult>();
	private Queue<SearchQuery> server_search_request_queue = new LinkedList<SearchQuery>();
	private Queue<SearchQuery> kad_search_request_queue = new LinkedList<SearchQuery>();
	private ServerManager server_manager = ServerManagerSingleton.getInstance();

	private List<SearchResultListener> search_result_listeners = new LinkedList<SearchResultListener>();

	private Map<SearchQuery, Int128> kad_searches = new HashMap<SearchQuery, Int128>();
	private long searches_count = 0;

	private JMTimer timer = new JMTimer();
	private JMTimerTask server_search_task;

	private SearchQuery server_search_request;
	private SearchQuery kad_search_request;

	SearchManagerImpl() {
		server_search_task = new JMTimerTask(timer) {
			public void run() {
				while (!server_search_request_queue.isEmpty()) {
					server_search_request = server_search_request_queue.poll();
					if (server_manager.isConnected()) {
						network_manager.doSearchOnServer(server_search_request);
						notifySearchStarted(server_search_request);
						try {
							Thread.sleep(SEARCH_ANSWER_WAIT);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
					if (server_search_request_queue
							.contains(server_search_request)) {
						notifySearchFailed(server_search_request);
						server_search_request_queue
								.remove(server_search_request);
					}
				}
				this.cancel();
			}
		};
	}

	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}

		Set<String> types = new HashSet<String>();
		types.add(JMuleCoreStats.SEARCHES_COUNT);
		JMuleCoreStats.registerProvider(types, new JMuleCoreStatsProvider() {
			public void updateStats(Set<String> types,
					Map<String, Object> values) {
				if (types.contains(JMuleCoreStats.SEARCHES_COUNT)) {
					values.put(JMuleCoreStats.SEARCHES_COUNT, searches_count);
				}
			}
		});
	}

	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}

	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}

	protected boolean iAmStoppable() {
		return false;
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
		if (searchRequest.getQueryType() == SearchQueryType.SERVER) {
			server_search_request_queue.add(searchRequest);

			if (server_search_request_queue.size() == 1)
				processServerSearchRequest();
		}
		if (searchRequest.getQueryType() == SearchQueryType.KAD) {
			if (!jkad.isConnected())
				return;
			kad_search_request_queue.add(searchRequest);
			if (kad_search_request_queue.size() == 1)
				processKadSearchRequest();
		}
		if (searchRequest.getQueryType() == SearchQueryType.SERVER_KAD) {
			if (jkad.isConnected()) {
				kad_search_request_queue.add(searchRequest);
				if (kad_search_request_queue.size() == 1)
					processKadSearchRequest();
			}

			server_search_request_queue.add(searchRequest);
			if (server_search_request_queue.size() == 1)
				processServerSearchRequest();

		}

		searches_count++;
	}

	public void addSeachResultListener(SearchResultListener searchResultListener) {
		search_result_listeners.add(searchResultListener);
	}

	private void notifySearchArrived(SearchResult search_result) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.resultArrived(search_result);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchStarted(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchStarted(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchCompleted(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchCompleted(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void notifySearchFailed(SearchQuery query) {
		for (SearchResultListener listener : search_result_listeners) {
			try {
				listener.searchFailed(query);
			}catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void processServerSearchRequest() {
		server_search_request = null;
		if (server_search_request_queue.isEmpty())
			return;
		if (server_search_task == null)
			return;
		if (server_search_task.isStarted())
			return;
		timer.addTask(server_search_task, 1);
	}

	private void processKadSearchRequest() {
		if (kad_search_request_queue.isEmpty())
			return;
		kad_search_request = (SearchQuery) kad_search_request_queue.peek(); // remove
																			// query
																			// from
																			// queue
																			// after
																			// complete
																			// search
		Int128 keyword_id;
		try {
			keyword_id = jkad_search.searchKeyword(kad_search_request
					.getQuery(),
					new org.jmule.core.jkad.search.SearchResultListener() {
						SearchQuery r = (SearchQuery) kad_search_request
								.clone();
						SearchResultItemList result_list = new SearchResultItemList();
						SearchResult search_result = new SearchResult(
								result_list, r);

						public void processNewResults(List<Source> result) {
							result_list.clear();
							for (Source source : result) {
								SearchResultItem item = new SearchResultItem(
										source.getClientID().toFileHash(),
										null, (short) 0, SearchQueryType.KAD);
								for (Tag tag : source.getTagList()) {
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
			kad_searches.put(kad_search_request, keyword_id);

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	public void receivedServerSearchResult(SearchResultItemList resultList) {
		SearchResult searchResult = new SearchResult(resultList,
				server_search_request, server_manager.getConnectedServer());
		search_result_list.put(server_search_request, searchResult);

		notifySearchArrived(searchResult);
		notifySearchCompleted(searchResult.searchQuery);
		processServerSearchRequest();
	}

	public void removeSearchResultListener(
			SearchResultListener searchResultListener) {

	}

}
