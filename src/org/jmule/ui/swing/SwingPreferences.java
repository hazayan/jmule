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
package org.jmule.ui.swing;

import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:01 $$
 */
public class SwingPreferences implements SwingConstants {

	//for now the all data is stored in system preferences
	
	// constants
	
	private static final String VISIBILITY       =       "VISIBILITY";
	private static final String ORDER            =       "ORDER";
	
	private static final HashMap default_values = new HashMap();
	
	static {
		
		// default table column's visibility
		default_values.put(SERVER_LIST_NAME + VISIBILITY,               true);
		default_values.put(SERVER_LIST_IP + VISIBILITY,                 true);
		default_values.put(SERVER_LIST_DESCRIPTION + VISIBILITY,        true);
		default_values.put(SERVER_LIST_PING + VISIBILITY,               true);
		default_values.put(SERVER_LIST_USERS + VISIBILITY,              true);
		default_values.put(SERVER_LIST_MAX_USERS + VISIBILITY,          true);
		default_values.put(SERVER_LIST_FILES + VISIBILITY,              true);
		default_values.put(SERVER_LIST_SOFT_LIMIT + VISIBILITY,         true);
		default_values.put(SERVER_LIST_HARD_LIMIT + VISIBILITY,         true);
		
		// default table column's order
		default_values.put(SERVER_LIST_NAME + ORDER,               1);
		default_values.put(SERVER_LIST_IP + ORDER,                 2);
		default_values.put(SERVER_LIST_DESCRIPTION + ORDER,        3);
		default_values.put(SERVER_LIST_PING + ORDER,               4);
		default_values.put(SERVER_LIST_USERS + ORDER,              5);
		default_values.put(SERVER_LIST_MAX_USERS + ORDER,          6);
		default_values.put(SERVER_LIST_FILES + ORDER,              7);
		default_values.put(SERVER_LIST_SOFT_LIMIT + ORDER,         8);
		default_values.put(SERVER_LIST_HARD_LIMIT + ORDER,         9);
		
	}
	
	private Preferences preferences;
	private static SwingPreferences instance = null;
	
	public static SwingPreferences getSingleton() {
		if( instance == null ) instance = new SwingPreferences();
		return instance;
	}
	
	private SwingPreferences() {
		preferences = Preferences.systemRoot();
		try {
		  // if the node does not exists then we must store the default values in the system
		  if(!preferences.nodeExists("/org/jmule/ui/swing")) {
			  
			 // sets the visibility of the server list columns
			 preferences.node(SERVER_LIST_NAME).
			 putBoolean(VISIBILITY, SERVER_LIST_NAME_VISIBILITY_DEFAULT);	
			 
			 preferences.node(SERVER_LIST_IP).
			 putBoolean(VISIBILITY, SERVER_LIST_IP_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_DESCRIPTION).
			 putBoolean(VISIBILITY, SERVER_LIST_DESCRIPTION_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_PING).
			 putBoolean(VISIBILITY, SERVER_LIST_PING_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_USERS).
			 putBoolean(VISIBILITY, SERVER_LIST_USERS_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_MAX_USERS).
			 putBoolean(VISIBILITY,SERVER_LIST_MAX_USERS_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_FILES).
			 putBoolean(VISIBILITY,SERVER_LIST_FILES_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_SOFT_LIMIT).
			 putBoolean(VISIBILITY,SERVER_LIST_SOFT_LIMIT_VISIBILITY_DEFAULT);
			 
			 preferences.node(SERVER_LIST_HARD_LIMIT).
			 putBoolean(VISIBILITY,SERVER_LIST_HARD_LIMIT_VISIBILITY_DEFAULT);
			 
			 // sets the order of the server list columns
			 preferences.node(SERVER_LIST_NAME).
			 putInt(ORDER, SERVER_LIST_NAME_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_IP).
			 putInt(ORDER, SERVER_LIST_IP_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_DESCRIPTION).
			 putInt(ORDER, SERVER_LIST_DESCRIPTION_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_PING).
			 putInt(ORDER, SERVER_LIST_PING_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_USERS).
			 putInt(ORDER, SERVER_LIST_USERS_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_MAX_USERS).
			 putInt(ORDER, SERVER_LIST_MAX_USERS_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_FILES).
			 putInt(ORDER, SERVER_LIST_MAX_USERS_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_SOFT_LIMIT).
			 putInt(ORDER, SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT);
			 
			 preferences.node(SERVER_LIST_HARD_LIMIT).
			 putInt(ORDER, SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT);
		  }
		} catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(String columnId) {
		return preferences.getInt(columnId, (Integer)default_values.get(columnId + ORDER));
	}
	
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(String columnId) {
		return preferences.getBoolean(columnId, (Boolean)default_values.get(columnId + VISIBILITY));
	}
	
	/**
	 * @param columnId the id of the column
	 * @param order the order of the column
	 */
	public void setColumnOrder(String columnId, int order) {
		preferences.putInt(columnId, order);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value true if the column must be visible, false otherwise
	 */
	public void setColumnVisibility(String columnId, boolean value) {
		preferences.putBoolean(columnId, value);
	}
}
