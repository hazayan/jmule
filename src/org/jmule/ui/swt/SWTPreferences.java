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

import java.util.prefs.Preferences;

import org.jmule.ui.UIConstants;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:39:39 $$
 */
public class SWTPreferences extends SWTConstants {
	
	
	private Preferences preferences;
	
	private static SWTPreferences instance = null;
	
	public static SWTPreferences getInstance() {
		if (instance == null)
			instance = new SWTPreferences();
		return instance;
	}
	
	private SWTPreferences() {
		preferences = Preferences.systemRoot();
		
	}
	
	public static int getDefaultColumnOrder(int columnID) {
		return UIConstants.getDefaultColumnOrder(columnID) - 1;
	}
	
	public boolean promptOnExit() {
		String prompt_on_exit_node = getPromptOnExitNodePath(SWT_NODE);
		return preferences.node(prompt_on_exit_node).getBoolean(ENABLED, getDefaultPromptOnExit());
	}
	
	public void setPromprtOnExit(boolean value) {
		String prompt_on_exit_node = getPromptOnExitNodePath(SWT_NODE);
		preferences.node(prompt_on_exit_node).putBoolean(ENABLED, value);
	}
	
	public boolean updateCheckAtStartup() {
		String update_check_node = getStartupUpdateCheckNodePath(SWT_NODE);
		return preferences.node(update_check_node).getBoolean(ENABLED, getDefaultStartupUpdateCheck());
	}
	
	public void setUpdateCheckAtStartup(boolean value) {
		String update_check_node = getStartupUpdateCheckNodePath(SWT_NODE);
		preferences.node(update_check_node).putBoolean(ENABLED, value);
	}
	
	
	public boolean isToolBarVisible() {
		String toolbar_node = getToolBarNodePath(SWT_NODE);
		return preferences.node(toolbar_node).getBoolean(VISIBILITY, getToolBarDefaultVisibility());
	}
	
	public boolean isStatusBarVisible() {
		String toolbar_node = getStatusBarNodePath(SWT_NODE);
		return preferences.node(toolbar_node).getBoolean(VISIBILITY, getStatusBarDefaultVisibility());
	}
		
	public void setToolBarVisible(boolean visibility) {
		String toolbar_node = getToolBarNodePath(SWT_NODE);
		preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
	}
	
	public void setStatusBarVisible(boolean visibility) {
		String toolbar_node = getStatusBarNodePath(SWT_NODE);
		preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column order
	 */
	public int getColumnOrder(int columnID) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		return preferences.node(node).getInt(ORDER, getDefaultColumnOrder(columnID));
	}
	
	/**
	 * @param columnId the id of the column
	 * @return the column width
	 */
	public int getColumnWidth(int columnID) {
		if (isColumnVisible(columnID)) {
			String node = getColumnNodePath(SWT_NODE, columnID);
			return preferences.node(node).getInt(WIDTH, getDefaultColumnWidth(columnID));
		}
		else 
			return 0;
	}
		
	/**
	 * @param columnId the id of the column
	 * @return true if the column is visible
	 */
	public boolean isColumnVisible(int columnID) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		return preferences.node(node).getBoolean(VISIBILITY, getDefaultColumnVisibility(columnID));
	}
	
	/**
	 * @param columnId the id of the column
	 * @param order the order of the column
	 */
	public void setColumnOrder(int columnID, int order) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		preferences.node(node).putInt(ORDER, order);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value true if the column must be visible, false otherwise
	 */
	public void setColumnVisibility(int columnID, boolean value) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		preferences.node(node).putBoolean(VISIBILITY, value);
	}
	
	/**
	 * @param columnId the id of the column
	 * @param value the column width
	 */
	public void setColumnWidth(int columnID, int value) {
		String node = getColumnNodePath(SWT_NODE, columnID);
		preferences.node(node).putInt(WIDTH , value);
	}
	
}
