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
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:06 $$
 */
public class SearchManagerImpl implements SearchManager {

	LinkedList<SearchResultList> search_result;
	ConcurrentLinkedQueue<SearchRequest> search_request_queue;
	
	public void initialize() {
		search_result = new LinkedList<SearchResultList>(); 
		search_request_queue = new ConcurrentLinkedQueue<SearchRequest>(); 
	}
	
	public void add(SearchResultList searchResultList) {
		search_result.addLast(searchResultList);
	}
	
	public void remove(SearchResultList searchResultList) {
		search_result.remove(searchResultList);
	}
	
	public void search(String searchString) {
		//Server connected_server = ServerList.getInstance().getConnectedServer();
		//connected_server.searchFiles(searchString);
		search_request_queue.offer(new SearchRequest(searchString));
	}
	
	public void search(SearchRequest searchRequest) {
		search_request_queue.offer(searchRequest);
	}
	

	public void shutdown() {
		
	}

	public void start() {

	}

}
