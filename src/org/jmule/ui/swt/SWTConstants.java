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

import org.jmule.ui.UIConstants;

/**
 * 
 * @author binary256
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:00 $$
 */
public interface SWTConstants extends UIConstants {

	public static final long GUI_UPDATE_INTERVAL		 = 1000;
	
	public static final String ROOT_NODE 				 =   "/org/jmule/ui/swt";
	
	public static final String SERVER_LIST_NAME_COLUMN          =   ROOT_NODE + "/tables/columns/server_name";
	public static final String SERVER_LIST_IP_COLUMN            =   ROOT_NODE + "/tables/columns/server_ip";
	public static final String SERVER_LIST_DESCRIPTION_COLUMN   =   ROOT_NODE + "/tables/columns/server_description";
	public static final String SERVER_LIST_PING_COLUMN          =   ROOT_NODE + "/tables/columns/server_ping";
	public static final String SERVER_LIST_USERS_COLUMN         =   ROOT_NODE + "/tables/columns/server_user";
	public static final String SERVER_LIST_MAX_USERS_COLUMN     =   ROOT_NODE + "/tables/columns/server_max_user";
	public static final String SERVER_LIST_FILES_COLUMN         =   ROOT_NODE + "/tables/columns/server_files";
	public static final String SERVER_LIST_SOFT_LIMIT_COLUMN    =   ROOT_NODE + "/tables/columns/server_soft_limit";
	public static final String SERVER_LIST_HARD_LIMIT_COLUMN    =   ROOT_NODE + "/tables/columns/server_hard_limit";
	public static final String SERVER_LIST_SOFTWARE_COLUMN   	=   ROOT_NODE + "/tables/columns/server_software";
	
	public static final String TOOLBAR_VISIBILITY_PREF_NODE		= 	ROOT_NODE + "/toolbar";
	public static final String STATUSBAR_VISIBILITY_PREF_NODE	= 	ROOT_NODE + "/statusbar";
	
	
	public static final String COLUMN_NAME_KEY			 =   "ColumnID"; 
	public static final String ROW_OBJECT_KEY			 =   "Column Object";
}
