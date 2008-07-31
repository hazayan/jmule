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

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:08 $$
 */
public class SearchRequest {

	private SearchQuery searchQuery;
	private boolean cancelled = false;

	/**
	 * Constructs a new search request
	 */
	public SearchRequest() {
		
	}
	
	/**
	 * Builds a search request based on the given search query
	 * @param searchQuery the given search query
	 */
	public SearchRequest(SearchQuery searchQuery) {
		this.searchQuery = searchQuery;
	}
	
	/**
	 * Builds a search request based on the given search string
	 * @param searchStr the given search string
	 */
	public SearchRequest(String searchStr) {
		this.searchQuery = new SearchQuery(searchStr);
	}
	
	/**
	 * @return the search query
	 */
	public SearchQuery getSearchQuery() {
		return searchQuery;
	}

	/**
	 * Sets the search query
	 * @param searchQuery the given search query
	 */
	public void setSearchQuery(SearchQuery searchQuery) {
		this.searchQuery = searchQuery;
	}

	/**
	 * Tells if this search request is cancelled
	 * @return true if this search request is cancelled, false otherwise
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Sets the state of this search request
	 * @param cancelled the state of this search request, true if the request must be cancelled, false otherwise
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
