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

import org.jmule.core.JMConstants;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.7 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/12 12:54:31 $$
 */
public class UIPreferences extends UIConstants {

	static Preferences preferences = Preferences.systemRoot();

	private static UIPreferences instance = null;
	
	public static UIPreferences getSingleton() {
		if( instance == null ) instance = new UIPreferences();
		return instance;
	}
	
	public static void storeDefaultPreferences(String particular_ui_root) {
		
		try {
			 // sets the visibility of the columns
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_NAME_COLUMN_ID ) ).
			 putBoolean( VISIBILITY,  getDefaultColumnVisibility( SERVER_LIST_NAME_COLUMN_ID ) );	
			 
			 preferences.
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 putBoolean( VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_CC_COLUMN_ID ) );
			 
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
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 putBoolean(VISIBILITY, getDefaultColumnVisibility( SERVER_LIST_STATIC_COLUMN_ID ) );
			 
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
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_CC_COLUMN_ID ) );
			 
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
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 putInt(WIDTH, getDefaultColumnWidth( SERVER_LIST_STATIC_COLUMN_ID ) );
			 
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
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_CC_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_CC_COLUMN_ID ) );
			 
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
			 node( getColumnNodePath( particular_ui_root, SERVER_LIST_STATIC_COLUMN_ID ) ).
			 putInt(ORDER, getDefaultColumnOrder( SERVER_LIST_STATIC_COLUMN_ID ) );
			 
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
			 
			 // other elements
			 
			 preferences.
			 node( getPromptOnExitNodePath( particular_ui_root ) ).
			 putBoolean(ENABLED, getDefaultPromptOnExit());
			
			 preferences.
			 node( getStartupCheckUpdateNodePath( particular_ui_root ) ).
			 putBoolean(ENABLED, getDefaultStartupCheckUpdate());
			 
			 preferences.
			 node( getToolBarNodePath( particular_ui_root ) ).
			 putBoolean(VISIBILITY, getToolBarDefaultVisibility());
			 
			 preferences.
			 node( getStatusBarNodePath( particular_ui_root ) ).
			 putBoolean(VISIBILITY, getStatusBarDefaultVisibility());
			 
			 preferences.
			 node( getConnectAtStartupNodePath( particular_ui_root ) ).
			 putBoolean(ENABLED, getDefaultConnectAtStartup());

			 
		}catch(Throwable t) {
			t.printStackTrace();
		}
		
	}
	
	// PromptOnExit methods
	
	protected void setPromptOnExit(String uiRoot, boolean value) {
		String prompt_on_exit_node = getPromptOnExitNodePath(uiRoot);
		preferences.node(prompt_on_exit_node).putBoolean(ENABLED, value);
	}

	protected boolean isPromptOnExitEnabled(String uiRoot) {
		String node = getPromptOnExitNodePath(uiRoot);
		return preferences.node(node).getBoolean(ENABLED, getDefaultPromptOnExit());
	}
	
	// end PromptOnExitMethods
	
	// CheckForUpdatesAtStartup methods
	
	protected boolean isCheckForUpdatesAtStartup(String uiRoot) {
		String update_check_node = getStartupCheckUpdateNodePath(uiRoot);
		return preferences.node(update_check_node).getBoolean(ENABLED, getDefaultStartupCheckUpdate());
	}
	
	protected void setCheckForUpdatesAtStartup(String uiRoot, boolean value) {
		String update_check_node = getStartupCheckUpdateNodePath(uiRoot);
		preferences.node(update_check_node).putBoolean(ENABLED, value);
	}
	
	// end CheckForUpdatesAtStartup methods
	
	// ToolBar methods
	
	protected boolean isToolBarVisible(String uiRoot) {
		String toolbar_node = getToolBarNodePath(uiRoot);
		return preferences.node(toolbar_node).getBoolean(VISIBILITY, getToolBarDefaultVisibility());
	}
	
	protected void setToolBarVisible(String uiRoot, boolean visibility) {
		String toolbar_node = getToolBarNodePath(uiRoot);
		preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
	}
	
	// end ToolBar methods
	
	// StatusBar methods
	
	protected boolean isStatusBarVisible(String uiRoot) {
		String toolbar_node = getStatusBarNodePath(uiRoot);
		return preferences.node(toolbar_node).getBoolean(VISIBILITY, getStatusBarDefaultVisibility());
	}
	
	protected void setStatusBarVisible(String uiRoot, boolean visibility) {
		String toolbar_node = getStatusBarNodePath(uiRoot);
		preferences.node(toolbar_node).putBoolean(VISIBILITY,visibility);
	}
	
	// end StatusBar methods
	
	// NightlyBuildWarning methods
	
	protected boolean isNightlyBuildWarning(String uiRoot) {
		String node = getNightlyBuildWarningNodePath(uiRoot);
		boolean value = preferences.node(node).getBoolean(ENABLED, getDefaultNightlyBuildWarningEnabled());
		if(value) {
			setNightlyBuildWarningJMVer(uiRoot, JMConstants.DEV_VERSION);
			return true;
		}
		String stored_ver = getNightlyBuildWarningJMVer(uiRoot);
		if( JMConstants.compareDevVersions(stored_ver, JMConstants.DEV_VERSION) !=0 ) {
			setNightlyBuildWarning(uiRoot, true);
			setNightlyBuildWarningJMVer(uiRoot, JMConstants.DEV_VERSION);
			return true;
		}
		return false;
	}
	
	protected void setNightlyBuildWarning(String uiRoot, boolean value) {
		String node = getNightlyBuildWarningNodePath(uiRoot);
		preferences.node(node).putBoolean(ENABLED, value);
	}
	
	protected void setNightlyBuildWarningJMVer(String uiRoot, String value) {
		String node_path = getNightlyBuildWarningJMVerNodePath(uiRoot);
		preferences.node(node_path).put(node_path, value);
	}
	
	protected String getNightlyBuildWarningJMVer(String uiRoot) {
		String node_path = getNightlyBuildWarningJMVerNodePath(uiRoot);
		return preferences.node(node_path).get(ENABLED, getDefaultNightlyBuildWarningJMVer());
	}
	
	// end NightlyBuildWarning methods
	
	// Connect at start up methods
	
	protected boolean isConnectAtStartup(String uiRoot) {
	   String node = getConnectAtStartupNodePath(uiRoot);
	   return preferences.node(node).getBoolean(ENABLED, getDefaultConnectAtStartup());
	}
	
	protected void setConnectAtStartup(String uiRoot, boolean value) {
		String node = getConnectAtStartupNodePath(uiRoot);
		preferences.node(node).putBoolean(ENABLED, value);
	}
	
	// end connect at startup methods
	
	protected void setColumnWidth(String uiRoot, int columnID,int width) {
		String node = getColumnNodePath(uiRoot, columnID);
		preferences.node(node).putInt(WIDTH, width);
	}
	
	protected int getColumnWidth(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		return preferences.node(node).getInt(WIDTH, getDefaultColumnWidth(columnID));
	}
	
	protected void setColumnOrder(String uiRoot, int columnID,int order) {
		String node = getColumnNodePath(uiRoot, columnID);
		preferences.node(node).putInt(ORDER, order);
	}
	
	protected int getColumnOrder(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		return preferences.node(node).getInt(ORDER, getDefaultColumnOrder(columnID));
	}

	protected void setColumnVisibility(String uiRoot, int columnID,boolean visibility) {
		String node = getColumnNodePath(uiRoot, columnID);
		preferences.node(node).putBoolean(VISIBILITY, visibility);
	}
	
	protected boolean isColumnVisible(String uiRoot, int columnID) {
		String node = getColumnNodePath(uiRoot, columnID);
		return preferences.node(node).getBoolean(VISIBILITY, getDefaultColumnVisibility(columnID));
	}
	
}
