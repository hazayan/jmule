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
package org.jmule.ui;

import java.util.prefs.Preferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/22 08:19:25 $$
 */
public class UIPreferences extends UIConstants {


	public static void storeDefaultPreferences(String particular_ui_root) {
		
		Preferences preferences = Preferences.systemRoot();
		
		try {
			 // sets the visibility of the columns
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 putBoolean( VISIBILITY,  getDefaultColumnVisibility( SERVER_LIST_NAME_COLUMN_ID ) );	
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 putBoolean( VISIBILITY,  getDefaultColumnVisibility( SERVER_LIST_FLAG_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_PING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_FILES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_VERSION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILENAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILESIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SEARCH_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SHARED_LIST_COMPLETED_COLUMN_ID ) );

			 // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
			 
			// sets the default width of the columns
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 putInt(WIDTH,  getDefaultColumnWidth( SERVER_LIST_NAME_COLUMN_ID ) );	
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 putInt(WIDTH,  getDefaultColumnWidth( SERVER_LIST_FLAG_COLUMN_ID ) );	
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_PING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_FILES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_VERSION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILENAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILESIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SEARCH_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SHARED_LIST_COMPLETED_COLUMN_ID ) );

			// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
			 
			// sets the default order of the columns
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 putInt(ORDER,  getDefaultColumnOrder( SERVER_LIST_NAME_COLUMN_ID ) );	
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FLAG_COLUMN_ID ) ).
			 putInt(ORDER,  getDefaultColumnOrder( SERVER_LIST_FLAG_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_IP_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_DESCRIPTION_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_DESCRIPTION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_PING_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_PING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_USERS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_MAX_USERS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_MAX_USERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_FILES_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_FILES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_SOFT_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_HARD_LIMIT_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_HARD_LIMIT_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_VERSION_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_VERSION_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SIZE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_PROGRESS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_SOURCES_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_SOURCES_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_REMAINING_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_REMAINING_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_LIST_STATUS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_PEERS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_PEERS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_ETA_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_ETA_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_LIST_UPLOADED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_LIST_UPLOADED_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_IP_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_IP_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_STATUS_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILENAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_FILENAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILESIZE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_FILESIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_AVAILABILITY_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_AVAILABILITY_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_COMPLETESRC_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_COMPLETESRC_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_TYPE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SEARCH_FILE_ID_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SEARCH_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_NAME_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_NAME_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_SIZE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_SIZE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_TYPE_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_TYPE_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_FILE_ID_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_FILE_ID_COLUMN_ID ) );
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SHARED_LIST_COMPLETED_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SHARED_LIST_COMPLETED_COLUMN_ID ) );
			
		}catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	
}
