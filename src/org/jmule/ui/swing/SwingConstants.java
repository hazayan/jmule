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

import java.awt.Dimension;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:02 $$
 */
public interface SwingConstants {

	// Swing settings
	
	public static final String SWING_SETTINGS_REPOSITORY = "/org/jmule/ui/swing";
	
	// Table columns
	
	// Columns
	
	public static final int SERVER_LIST_NAME_ID          =   100;
	public static final int SERVER_LIST_IP_ID            =   200;
	public static final int SERVER_LIST_DESCRIPTION_ID   =   300;
	public static final int SERVER_LIST_PING_ID          =   400;
	public static final int SERVER_LIST_USERS_ID         =   500;
	public static final int SERVER_LIST_MAX_USERS_ID     =   600;
	public static final int SERVER_LIST_FILES_ID         =   700;
	public static final int SERVER_LIST_SOFT_LIMIT_ID    =   800;
	public static final int SERVER_LIST_HARD_LIMIT_ID    =   900;
	
	public static final String SERVER_LIST_NAME          =   "/org/jmule/ui/swing/tables/columns/server_name";
	public static final String SERVER_LIST_IP            =   "/org/jmule/ui/swing/tables/columns/server_ip";
	public static final String SERVER_LIST_DESCRIPTION   =   "/org/jmule/ui/swing/tables/columns/server_description";
	public static final String SERVER_LIST_PING          =   "/org/jmule/ui/swing/tables/columns/server_ping";
	public static final String SERVER_LIST_USERS         =   "/org/jmule/ui/swing/tables/columns/server_user";
	public static final String SERVER_LIST_MAX_USERS     =   "/org/jmule/ui/swing/tables/columns/server_max_user";
	public static final String SERVER_LIST_FILES         =   "/org/jmule/ui/swing/tables/columns/server_files";
	public static final String SERVER_LIST_SOFT_LIMIT    =   "/org/jmule/ui/swing/tables/columns/server_soft_limit";
	public static final String SERVER_LIST_HARD_LIMIT    =   "/org/jmule/ui/swing/tables/columns/server_hard_limit";
	
	public static final boolean SERVER_LIST_NAME_VISIBILITY_DEFAULT          =   true;
	public static final boolean SERVER_LIST_IP_VISIBILITY_DEFAULT            =   true;
	public static final boolean SERVER_LIST_DESCRIPTION_VISIBILITY_DEFAULT   =   true;
	public static final boolean SERVER_LIST_PING_VISIBILITY_DEFAULT          =   true;
	public static final boolean SERVER_LIST_USERS_VISIBILITY_DEFAULT         =   true;
	public static final boolean SERVER_LIST_MAX_USERS_VISIBILITY_DEFAULT     =   true;
	public static final boolean SERVER_LIST_FILES_VISIBILITY_DEFAULT         =   true;
	public static final boolean SERVER_LIST_SOFT_LIMIT_VISIBILITY_DEFAULT    =   true;
	public static final boolean SERVER_LIST_HARD_LIMIT_VISIBILITY_DEFAULT    =   true;
	
	// Column order
	
	public static final int SERVER_LIST_NAME_ORDER_DEFAULT               =   1;
	public static final int SERVER_LIST_IP_ORDER_DEFAULT                 =   2;
	public static final int SERVER_LIST_DESCRIPTION_ORDER_DEFAULT        =   3;
	public static final int SERVER_LIST_PING_ORDER_DEFAULT               =   4;
	public static final int SERVER_LIST_USERS_ORDER_DEFAULT              =   5;
	public static final int SERVER_LIST_MAX_USERS_ORDER_DEFAULT          =   6;
	public static final int SERVER_LIST_FILES_ORDER_DEFAULT              =   7;
	public static final int SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT         =   8;
	public static final int SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT         =   9;
	
	
	public static final Dimension SETUP_WIZARD_DIMENSION  =  new Dimension(500, 360 + 90);
	
	

	
}
