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
 * Created on 2008-Jul-06
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/20 15:49:58 $$
 */
public class SearchRequest {

	private SearchQuery searchQuery;

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
	
	public int hashCode() {
		return searchQuery.getQuery().hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof SearchRequest)) return false;
		SearchRequest query = (SearchRequest)object;
		return query.getSearchQuery().getQuery().equals(searchQuery.getQuery());
	}
	
}
