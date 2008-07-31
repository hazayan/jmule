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
package org.jmule.ui.swt;

import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.jmule.ui.localizer.Localizer;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:00 $$
 */
public class SWTPreferences implements SWTConstants {

	private static final boolean SERVER_LIST_NAME_VISIBILITY_DEFAULT          =   true;
	private static final boolean SERVER_LIST_IP_VISIBILITY_DEFAULT            =   true;
	private static final boolean SERVER_LIST_DESCRIPTION_VISIBILITY_DEFAULT   =   true;
	private static final boolean SERVER_LIST_PING_VISIBILITY_DEFAULT          =   true;
	private static final boolean SERVER_LIST_USERS_VISIBILITY_DEFAULT         =   true;
	private static final boolean SERVER_LIST_MAX_USERS_VISIBILITY_DEFAULT     =   true;
	private static final boolean SERVER_LIST_FILES_VISIBILITY_DEFAULT         =   true;
	private static final boolean SERVER_LIST_SOFT_LIMIT_VISIBILITY_DEFAULT    =   true;
	private static final boolean SERVER_LIST_HARD_LIMIT_VISIBILITY_DEFAULT    =   true;
	private static final boolean SERVER_LIST_SOFTWARE_VISIBILITY_DEFAULT   	  =   true;
	
	// Column order
	
	public static final int SERVER_LIST_NAME_ORDER_DEFAULT               =   0;
	public static final int SERVER_LIST_IP_ORDER_DEFAULT                 =   1;
	public static final int SERVER_LIST_DESCRIPTION_ORDER_DEFAULT        =   2;
	public static final int SERVER_LIST_PING_ORDER_DEFAULT               =   3;
	public static final int SERVER_LIST_USERS_ORDER_DEFAULT              =   4;
	public static final int SERVER_LIST_MAX_USERS_ORDER_DEFAULT          =   5;
	public static final int SERVER_LIST_FILES_ORDER_DEFAULT              =   6;
	public static final int SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT         =   7;
	public static final int SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT         =   8;
	public static final int SERVER_LIST_SOFTWARE_ORDER_DEFAULT	         =   9;
	
	// Column width
	public static final int SERVER_LIST_NAME_WIDTH_DEFAULT               =   50;
	public static final int SERVER_LIST_IP_WIDTH_DEFAULT                 =   50;
	public static final int SERVER_LIST_DESCRIPTION_WIDTH_DEFAULT        =   50;
	public static final int SERVER_LIST_PING_WIDTH_DEFAULT               =   50;
	public static final int SERVER_LIST_USERS_WIDTH_DEFAULT              =   50;
	public static final int SERVER_LIST_MAX_USERS_WIDTH_DEFAULT          =   50;
	public static final int SERVER_LIST_FILES_WIDTH_DEFAULT              =   50;
	public static final int SERVER_LIST_SOFT_LIMIT_WIDTH_DEFAULT         =   50;
	public static final int SERVER_LIST_HARD_LIMIT_WIDTH_DEFAULT         =   50;
	public static final int SERVER_LIST_SOFTWARE_WIDTH_DEFAULT	         =   50;

	public static final boolean STATUSBAR_VISIBILITY_DEFAULT			 =   true;
	public static final boolean TOOLBAR_VISIBILITY_DEFAULT				 =   true;
	
	private static final String VISIBILITY       =       "VISIBILITY";
	private static final String ORDER            =       "ORDER";
	private static final String WIDTH            =       "WIDTH";
	
	private static final HashMap default_values = new HashMap();
	private static HashMap<String,String> columns_description = new HashMap<String,String>();
	static {
		
		// default table column's visibility
		default_values.put(SERVER_LIST_NAME_COLUMN + VISIBILITY,               SERVER_LIST_NAME_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_IP_COLUMN + VISIBILITY,                 SERVER_LIST_IP_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN + VISIBILITY,        SERVER_LIST_DESCRIPTION_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_PING_COLUMN + VISIBILITY,               SERVER_LIST_PING_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_USERS_COLUMN + VISIBILITY,              SERVER_LIST_USERS_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN + VISIBILITY,          SERVER_LIST_MAX_USERS_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_FILES_COLUMN + VISIBILITY,              SERVER_LIST_FILES_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN + VISIBILITY,         SERVER_LIST_SOFT_LIMIT_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN + VISIBILITY,         SERVER_LIST_HARD_LIMIT_VISIBILITY_DEFAULT);
		default_values.put(SERVER_LIST_SOFTWARE_COLUMN + VISIBILITY,  		    SERVER_LIST_SOFTWARE_VISIBILITY_DEFAULT);
		
		// default table column's order
		default_values.put(SERVER_LIST_NAME_COLUMN + ORDER,               SERVER_LIST_NAME_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_IP_COLUMN + ORDER,                 SERVER_LIST_IP_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN + ORDER,        SERVER_LIST_DESCRIPTION_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_PING_COLUMN + ORDER,               SERVER_LIST_PING_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_USERS_COLUMN + ORDER,              SERVER_LIST_USERS_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN + ORDER,          SERVER_LIST_MAX_USERS_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_FILES_COLUMN + ORDER,              SERVER_LIST_FILES_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN + ORDER,         SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN + ORDER,         SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_SOFTWARE_COLUMN + ORDER,  	  	   SERVER_LIST_SOFTWARE_ORDER_DEFAULT);
		
		// default table column's width
		default_values.put(SERVER_LIST_NAME_COLUMN + WIDTH,               SERVER_LIST_NAME_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_IP_COLUMN + WIDTH,                 SERVER_LIST_IP_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN + WIDTH,        SERVER_LIST_DESCRIPTION_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_PING_COLUMN + WIDTH,               SERVER_LIST_PING_ORDER_DEFAULT);
		default_values.put(SERVER_LIST_USERS_COLUMN + WIDTH,              SERVER_LIST_USERS_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN + WIDTH,          SERVER_LIST_MAX_USERS_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_FILES_COLUMN + WIDTH,              SERVER_LIST_FILES_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN + WIDTH,         SERVER_LIST_SOFT_LIMIT_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN + WIDTH,         SERVER_LIST_HARD_LIMIT_WIDTH_DEFAULT);
		default_values.put(SERVER_LIST_SOFTWARE_COLUMN + WIDTH,  	  	   SERVER_LIST_SOFTWARE_WIDTH_DEFAULT);
		
		// default column description
		columns_description.put(SWTConstants.SERVER_LIST_NAME_COLUMN, Localizer._("columneditorwindow.columndesc.name"));
		columns_description.put(SWTConstants.SERVER_LIST_IP_COLUMN, Localizer._("columneditorwindow.columndesc.ip"));
		columns_description.put(SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN, Localizer._("columneditorwindow.columndesc.description"));
		columns_description.put(SWTConstants.SERVER_LIST_PING_COLUMN, Localizer._("columneditorwindow.columndesc.ping"));
		columns_description.put(SWTConstants.SERVER_LIST_USERS_COLUMN,Localizer._("columneditorwindow.columndesc.users"));
		columns_description.put(SWTConstants.SERVER_LIST_MAX_USERS_COLUMN, Localizer._("columneditorwindow.columndesc.maxusers"));
		columns_description.put(SWTConstants.SERVER_LIST_FILES_COLUMN, Localizer._("columneditorwindow.columndesc.files"));
		columns_description.put(SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN, Localizer._("columneditorwindow.columndesc.soft_limit"));
		columns_description.put(SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN, Localizer._("columneditorwindow.columndesc.hard_limit"));
		columns_description.put(SWTConstants.SERVER_LIST_SOFTWARE_COLUMN, Localizer._("columneditorwindow.columndesc.software"));
		
	}
	
	private Preferences preferences;
	private static SWTPreferences instance = null;
	
	public static SWTPreferences getInstance() {
		if (instance == null)
			instance = new SWTPreferences();
		return instance;
	}
	
	private SWTPreferences() {
		preferences = Preferences.systemRoot();
		
		try {
			  // if the node does not exists then we must store the default values in the system
			  if(!preferences.nodeExists(ROOT_NODE)) {
				  
				 // sets the visibility of the server list columns
				 preferences.node(SERVER_LIST_NAME_COLUMN).
				 putBoolean(VISIBILITY, SERVER_LIST_NAME_VISIBILITY_DEFAULT);	
				 
				 preferences.node(SERVER_LIST_IP_COLUMN).
				 putBoolean(VISIBILITY, SERVER_LIST_IP_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_DESCRIPTION_COLUMN).
				 putBoolean(VISIBILITY, SERVER_LIST_DESCRIPTION_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_PING_COLUMN).
				 putBoolean(VISIBILITY, SERVER_LIST_PING_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_USERS_COLUMN).
				 putBoolean(VISIBILITY, SERVER_LIST_USERS_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_MAX_USERS_COLUMN).
				 putBoolean(VISIBILITY,SERVER_LIST_MAX_USERS_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_FILES_COLUMN).
				 putBoolean(VISIBILITY,SERVER_LIST_FILES_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_SOFT_LIMIT_COLUMN).
				 putBoolean(VISIBILITY,SERVER_LIST_SOFT_LIMIT_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_HARD_LIMIT_COLUMN).
				 putBoolean(VISIBILITY,SERVER_LIST_HARD_LIMIT_VISIBILITY_DEFAULT);
				 
				 preferences.node(SERVER_LIST_SOFTWARE_COLUMN).
				 putBoolean(VISIBILITY,SERVER_LIST_SOFTWARE_VISIBILITY_DEFAULT);
				 
				 // sets the order of the server list columns
				 preferences.node(SERVER_LIST_NAME_COLUMN).
				 putInt(ORDER, SERVER_LIST_NAME_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_IP_COLUMN).
				 putInt(ORDER, SERVER_LIST_IP_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_DESCRIPTION_COLUMN).
				 putInt(ORDER, SERVER_LIST_DESCRIPTION_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_PING_COLUMN).
				 putInt(ORDER, SERVER_LIST_PING_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_USERS_COLUMN).
				 putInt(ORDER, SERVER_LIST_USERS_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_MAX_USERS_COLUMN).
				 putInt(ORDER, SERVER_LIST_MAX_USERS_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_FILES_COLUMN).
				 putInt(ORDER, SERVER_LIST_MAX_USERS_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_SOFT_LIMIT_COLUMN).
				 putInt(ORDER, SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_HARD_LIMIT_COLUMN).
				 putInt(ORDER, SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT);
				 
				 preferences.node(SERVER_LIST_SOFTWARE_COLUMN).
				 putInt(VISIBILITY,SERVER_LIST_SOFTWARE_ORDER_DEFAULT);
				 
				 preferences.node(TOOLBAR_VISIBILITY_PREF_NODE).
				 putBoolean(VISIBILITY,TOOLBAR_VISIBILITY_DEFAULT);
				 
				 preferences.node(STATUSBAR_VISIBILITY_PREF_NODE).
				 putBoolean(VISIBILITY,STATUSBAR_VISIBILITY_DEFAULT);
			  }
			} catch(BackingStoreException e) {
				e.printStackTrace();
			}
		
		
	}
	
	public boolean isToolBarVisible() {
		return preferences.getBoolean(TOOLBAR_VISIBILITY_PREF_NODE + "/" + VISIBILITY, TOOLBAR_VISIBILITY_DEFAULT);
	}
	
	public boolean isStatusBarVisible() {
		return preferences.getBoolean(STATUSBAR_VISIBILITY_PREF_NODE + "/" + VISIBILITY, STATUSBAR_VISIBILITY_DEFAULT);
	}
		
	public void setToolBarVisible(boolean visibility) {
		System.out.println(TOOLBAR_VISIBILITY_PREF_NODE + "/" + VISIBILITY);
		preferences.putBoolean(TOOLBAR_VISIBILITY_PREF_NODE + "/" + VISIBILITY,visibility);
	}
	
	public void setStatusBarVisible(boolean visibility) {
		preferences.putBoolean(STATUSBAR_VISIBILITY_PREF_NODE + "/" + VISIBILITY,visibility);
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(String columnId) {
		return preferences.getInt(columnId + "/" + ORDER, (Integer)default_values.get(columnId + ORDER));
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column width
	 */
	public int getColumnWidth(String columnId) {
		if (isColumnVisible(columnId))
			return preferences.getInt(columnId + "/" + WIDTH, (Integer)default_values.get(columnId + WIDTH));
		else 
			return 0;
	}
	
	public int getDefaultColumnWidth(String columnId) {
		return ((Integer)default_values.get(columnId + "/" + WIDTH));
		
	}
	
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(String columnId) {
		return preferences.getBoolean(columnId + "/" + VISIBILITY, (Boolean)default_values.get(columnId + VISIBILITY));
	}
	
	/**
	 * @param columnId the id of the column
	 * @param order the order of the column
	 */
	public void setColumnOrder(String columnId, int order) {
		preferences.putInt(columnId + "/" + ORDER, order);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value true if the column must be visible, false otherwise
	 */
	public void setColumnVisibility(String columnId, boolean value) {
		preferences.putBoolean(columnId + "/" + VISIBILITY, value);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value the column width
	 */
	public void setColumnWidth(String columnId, int value) {
		preferences.putInt(columnId + "/" + WIDTH , value);
	}
	
	public static String getColumnDescription(String column_id) {
		return columns_description.get(column_id);
	}
	
	public static void main(String... agrs) {
		SWTPreferences p = SWTPreferences.getInstance();
		
		int a = p.getColumnOrder(SERVER_LIST_NAME_COLUMN);
		boolean b = p.isColumnVisible(SERVER_LIST_NAME_COLUMN);
		System.out.println("Loaded : " + a+" "+b);

		p.setColumnOrder(SERVER_LIST_NAME_COLUMN, 2);
		p.setColumnVisibility(SERVER_LIST_NAME_COLUMN, !b);
		
		a = p.getColumnOrder(SERVER_LIST_NAME_COLUMN);
		b = p.isColumnVisible(SERVER_LIST_IP_COLUMN);
		System.out.println("After changing : "+ a+" "+b);
	}
	
}
