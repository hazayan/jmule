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

import org.jmule.core.JMuleManager;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:07 $$
 */
public interface SearchManager extends JMuleManager {

	/**
	 * Adds a new search result list to the search manager
	 * @param searchResultList the given search result list
	 */
	public void add(SearchResultList searchResultList);
	
	/**
	 * Removes the given search result list
	 * @param searchResultList the given search result list
	 */
	public void remove(SearchResultList searchResultList);
	
	/**
	 * Starts a new search process based on the given search string
	 * @param searchString the given search string
	 */
	public void search(String searchString);
	
	/**
	 * Starts a new search process based on the given search request
	 * @param searchRequest the given search request
	 */
	public void search(SearchRequest searchRequest);
	
}
