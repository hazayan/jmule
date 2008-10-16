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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/10/16 17:35:11 $$
 */
public class SwingPreferences extends SwingConstants {

	
	
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
		  if(!preferences.nodeExists(UI_ROOT + SWING_ROOT)) {
			  
			
			  storeDefaultPreferences(SWING_ROOT);
			 
		  }
		} catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(int columnId) {
		return preferences.getInt(getColumnNodePath(SWING_ROOT,columnId), 
				                  getDefaultColumnOrder(columnId));
	}
	
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(int columnId) {
		return preferences.getBoolean(getColumnNodePath(SWING_ROOT,columnId),
				                      getDefaultColumnVisibility(columnId));
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
	
    public boolean isPromptOnExitEnabled() {
    	return super.isPromptOnExitEnabled(SWING_ROOT);
    }
    
    public boolean isCheckForUpdatesAtStartup() {
    	return super.isCheckForUpdatesAtStartup(SWING_ROOT);
    }
    
    public void setPromptOnExit(boolean value) {
    	super.setPromptOnExit(SWING_ROOT, value);
    }
    
    public void setCheckForUpdatesAtStartup(boolean value) {
    	super.setCheckForUpdatesAtStartup(SWING_ROOT, value);
    }
    
    public boolean isNightlyBuildWarning() {
    	return super.isNightlyBuildWarning(SWING_ROOT);
    }
    
    public void setNightlyBuildWarning(boolean value) {
    	super.setNightlyBuildWarning(SWING_ROOT, value);
    }
    
    public boolean isConnectAtStartup() {
    	return super.isConnectAtStartup(SWING_ROOT);
    }
    
    public void setConnectAtStartup(boolean value) {
    	super.setConnectAtStartup(SWING_ROOT, value);
    }
    
}
