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


import java.util.HashMap;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;


/**
 * 
 * @author javajox
 * @author binary
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/21 16:42:20 $$
 */

public class UIConstants {
	
	public static final String UI_ROOT = "/org/jmule/ui";
	
	protected static final String VISIBILITY        =       "VISIBILITY";
	protected static final String ORDER             =       "ORDER";
	protected static final String WIDTH             =       "WIDTH";
	protected static final String TABLE_COLUMN_PATH =       "/tables/columns";
	
	public static final int TOOL_BAR_ID                         = 1;
	public static final int STATUS_BAR_ID                       = 2;

	// Columns ID

	// server list
	public static final int SERVER_LIST_NAME_COLUMN_ID          =   100;
	public static final int SERVER_LIST_IP_COLUMN_ID            =   200;
	public static final int SERVER_LIST_DESCRIPTION_COLUMN_ID   =   300;
	public static final int SERVER_LIST_PING_COLUMN_ID          =   400;
	public static final int SERVER_LIST_USERS_COLUMN_ID         =   500;
	public static final int SERVER_LIST_MAX_USERS_COLUMN_ID     =   600;
	public static final int SERVER_LIST_FILES_COLUMN_ID         =   700;
	public static final int SERVER_LIST_SOFT_LIMIT_COLUMN_ID    =   800;
	public static final int SERVER_LIST_HARD_LIMIT_COLUMN_ID    =   900;
	public static final int SERVER_LIST_VERSION_COLUMN_ID   	=   1000;
	
	// downloads
	public static final int DOWNLOAD_LIST_FILE_NAME_COLUMN_ID         = 1100; 
	public static final int DOWNLOAD_LIST_SIZE_COLUMN_ID              = 1200;
	public static final int DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID       = 1300;
	public static final int DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID    = 1400;
	public static final int DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID      = 1500;
	public static final int DOWNLOAD_LIST_PROGRESS_COLUMN_ID          = 1600;
	public static final int DOWNLOAD_LIST_SOURCES_COLUMN_ID           = 1700;
	public static final int DOWNLOAD_LIST_REMAINING_COLUMN_ID         = 1800;
	public static final int DOWNLOAD_LIST_STATUS_COLUMN_ID            = 1900;
	
	public static final int DOWNLOAD_PEER_LIST_IP_COLUMN_ID           = 2000;
	public static final int DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID       = 2100;
	public static final int DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID     = 2200;
	public static final int DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID     = 2300;
	
	// uploads
	public static final int UPLOAD_LIST_FILE_NAME_COLUMN_ID           = 2400;
	public static final int UPLOAD_LIST_FILE_SIZE_COLUMN_ID           = 2500;
	public static final int UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID        = 2600;
	public static final int UPLOAD_LIST_PEERS_COLUMN_ID               = 2700;
	public static final int UPLOAD_LIST_ETA_COLUMN_ID                 = 2800;
	public static final int UPLOAD_LIST_UPLOADED_COLUMN_ID            = 2900;
	
	public static final int UPLOAD_PEER_LIST_IP_COLUMN_ID             = 3000;
	public static final int UPLOAD_PEER_LIST_STATUS_COLUMN_ID         = 3100;
	public static final int UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID       = 3200;
	public static final int UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID       = 3300;
	
	// search
	public static final int SEARCH_FILENAME_COLUMN_ID                 = 3400;
	public static final int SEARCH_FILESIZE_COLUMN_ID                 = 3500;
	public static final int SEARCH_AVAILABILITY_COLUMN_ID             = 3600;
	public static final int SEARCH_COMPLETESRC_COLUMN_ID              = 3700;
	public static final int SEARCH_FILE_TYPE_COLUMN_ID                = 3800;
	public static final int SEARCH_FILE_ID_COLUMN_ID                  = 3900;
	
	// shared
	public static final int SHARED_LIST_FILE_NAME_COLUMN_ID           = 4000;
	public static final int SHARED_LIST_FILE_SIZE_COLUMN_ID           = 4100;
	public static final int SHARED_LIST_FILE_TYPE_COLUMN_ID           = 4200;
	public static final int SHARED_LIST_FILE_ID_COLUMN_ID             = 4300;
	public static final int SHARED_LIST_COMPLETED_COLUMN_ID           = 4400;
	
	// Column UI nodes
	protected static final String SERVER_LIST_NAME_NODE                     = "/server_list_name_column";
	protected static final String SERVER_LIST_IP_NODE                       = "/server_list_ip_column";
	protected static final String SERVER_LIST_DESCRIPTION_NODE              = "/server_list_description_column";
	protected static final String SERVER_LIST_PING_NODE                     = "/server_list_ping_column";
	protected static final String SERVER_LIST_USERS_NODE                    = "/server_list_user_column";
	protected static final String SERVER_LIST_MAX_USERS_NODE                = "/server_list_max_user_column";
	protected static final String SERVER_LIST_FILES_NODE                    = "/server_list_files_column";
	protected static final String SERVER_LIST_SOFT_LIMIT_NODE               = "/server_list_soft_limit_column";
	protected static final String SERVER_LIST_HARD_LIMIT_NODE               = "/server_list_hard_limit_column";
	protected static final String SERVER_LIST_VERSION_NODE                  = "/server_list_version_column";
	
	protected static final String DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE       = "/download_list_file_name_column";
	protected static final String DOWNLOAD_LIST_SIZE_COLUMN_NODE            = "/download_list_size_column";
	protected static final String DOWNLOAD_LIST_TRANSFERRED_COLUMN_NODE     = "/download_list_transferred_column";
	protected static final String DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_NODE  = "/download_list_download_speed_column";
	protected static final String DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_NODE    = "/download_list_upload_speed_column";
	protected static final String DOWNLOAD_LIST_PROGRESS_COLUMN_NODE        = "/download_list_progress_column";
	protected static final String DOWNLOAD_LIST_SOURCES_COLUMN_NODE         = "/download_list_sources_column";
	protected static final String DOWNLOAD_LIST_REMAINING_COLUMN_NODE       = "/download_remaining_column";
	protected static final String DOWNLOAD_LIST_STATUS_COLUMN_NODE          = "/download_list_status_column";
	
	protected static final String DOWNLOAD_PEER_LIST_IP_COLUMN_NODE         = "/download_peer_list_ip_column";
	protected static final String DOWNLOAD_PEER_LIST_STATUS_COLUMN_NODE     = "/download_peer_list_status_column";
	protected static final String DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_NODE   = "/download_peer_list_nickname_column";
	protected static final String DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE   = "/download_peer_list_software_column";
	
	protected static final String UPLOAD_LIST_FILENAME_COLUMN_NODE          = "/upload_list_filename_column";
	protected static final String UPLOAD_LIST_FILESIZE_COLUMN_NODE          = "/upload_list_filesize_column";
	protected static final String UPLOAD_LIST_UPLOADSPEED_COLUMN_NODE       = "/upload_list_uploadspeed_column";
	protected static final String UPLOAD_LIST_PEERS_COLUMN_NODE             = "/upload_list_peers_column";
	protected static final String UPLOAD_LIST_ETA_COLUMN_NODE               = "/upload_list_eta_column";
	protected static final String UPLOAD_LIST_UPLOADED_COLUMN_NODE          = "/upload_list_uploaded_column";
	
	protected static final String UPLOAD_PEER_LIST_IP_COLUMN_NODE           = "/upload_peer_list_ip_column";
	protected static final String UPLOAD_PEER_LIST_STATUS_COLUMN_NODE       = "/upload_peer_list_status_column";
	protected static final String UPLOAD_PEER_LIST_NICKNAME_COLUMN_NODE     = "/upload_peer_list_nickname_column";
	protected static final String UPLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE     = "/upload_peer_list_software_column";
	
	protected static final String SEARCH_FILENAME_COLUMN_NODE               = "/search_filename_column";
	protected static final String SEARCH_FILESIZE_COLUMN_NODE               = "/search_filesize_column";
	protected static final String SEARCH_AVAILABILITY_COLUMN_NODE           = "/search_availability_column";
	protected static final String SEARCH_COMPLETESRC_COLUMN_NODE            = "/search_completsrc_column";
	protected static final String SEARCH_FILE_TYPE_COLUMN_NODE              = "/search_file_type_column";
	protected static final String SEARCH_FILE_ID_COLUMN_NODE                = "/search_file_id_column";
	
	protected static final String SHARED_LIST_FILE_NAME_COLUMN_NODE         = "/shared_list_file_name_column";
	protected static final String SHARED_LIST_FILE_SIZE_COLUMN_NODE         = "/shared_list_file_size_column";
	protected static final String SHARED_LIST_FILE_TYPE_COLUMN_NODE         = "/shared_list_file_type_column";
	protected static final String SHARED_LIST_FILE_ID_COLUMN_NODE           = "/shared_list_file_id_column";
	protected static final String SHARED_LIST_COMPLETED_COLUMN_NODE         = "/shared_list_completed_column";
	
	protected static final String TOOL_BAR_NODE                             = "/tool_bar";
	protected static final String STATUS_BAR_NODE                           = "/status_bar";
	
	protected static final HashMap<String,Object> default_values = new HashMap<String,Object>();
	
	static {
		// default table column's visibility
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + VISIBILITY,                true);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + VISIBILITY,       true);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + VISIBILITY,             true);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + VISIBILITY,             true);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + VISIBILITY,        true);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + VISIBILITY,        true);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + VISIBILITY,           true);
		
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,       true);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + VISIBILITY,            true);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + VISIBILITY,     true);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + VISIBILITY,  true);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + VISIBILITY,    true);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + VISIBILITY,        true);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + VISIBILITY,         true);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + VISIBILITY,       true);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + VISIBILITY,          true);
		
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + VISIBILITY,         true);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + VISIBILITY,     true);
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + VISIBILITY,   true);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + VISIBILITY,   true);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,         true);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + VISIBILITY,      true);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + VISIBILITY,             true);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + VISIBILITY,               true);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + VISIBILITY,          true);
		
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + VISIBILITY,           true);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + VISIBILITY,       true);
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + VISIBILITY,     true);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + VISIBILITY,     true);
		
		default_values.put(SEARCH_FILENAME_COLUMN_ID + VISIBILITY,               true);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + VISIBILITY,               true);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + VISIBILITY,           true);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + VISIBILITY,            true);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + VISIBILITY,              true);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + VISIBILITY,                true);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + VISIBILITY,         true);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + VISIBILITY,           true);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + VISIBILITY,         true);
		
		// default table column's order
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + ORDER,               1);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + ORDER,                 2);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + ORDER,        3);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + ORDER,               4);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + ORDER,              5);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + ORDER,          6);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + ORDER,              7);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + ORDER,         8);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + ORDER,         9);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + ORDER,            10);
		
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + ORDER,        1);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + ORDER,             2);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + ORDER,      3);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + ORDER,   4);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + ORDER,     5);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + ORDER,         6);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + ORDER,          7);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + ORDER,        8);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + ORDER,           9);
		
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + ORDER,          1);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + ORDER,      2);
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + ORDER,    3);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + ORDER,    4);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + ORDER,          1);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + ORDER,          2);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + ORDER,       3);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + ORDER,              4);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + ORDER,                5);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + ORDER,           6);
		
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + ORDER,            1);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + ORDER,        2);
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + ORDER,      3);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + ORDER,      4);
		
		default_values.put(SEARCH_FILENAME_COLUMN_ID + ORDER,                1);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + ORDER,                2);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + ORDER,            3);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + ORDER,             4);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + ORDER,               5);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + ORDER,                 6);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + ORDER,          1);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + ORDER,          2);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + ORDER,          3);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + ORDER,            4);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + ORDER,          5);
		
		default_values.put(SERVER_LIST_NAME_COLUMN_ID + WIDTH,              150);
		default_values.put(SERVER_LIST_IP_COLUMN_ID + WIDTH,                40);
		default_values.put(SERVER_LIST_DESCRIPTION_COLUMN_ID + WIDTH,       150);
		default_values.put(SERVER_LIST_PING_COLUMN_ID + WIDTH,              150);
		default_values.put(SERVER_LIST_USERS_COLUMN_ID + WIDTH,             150);
		default_values.put(SERVER_LIST_MAX_USERS_COLUMN_ID + WIDTH,         150);
		default_values.put(SERVER_LIST_FILES_COLUMN_ID + WIDTH,             150);
		default_values.put(SERVER_LIST_SOFT_LIMIT_COLUMN_ID + WIDTH,        150);
		default_values.put(SERVER_LIST_HARD_LIMIT_COLUMN_ID + WIDTH,        150);
		default_values.put(SERVER_LIST_VERSION_COLUMN_ID + WIDTH,           150);
		
		default_values.put(DOWNLOAD_LIST_FILE_NAME_COLUMN_ID + WIDTH,        100);
		default_values.put(DOWNLOAD_LIST_SIZE_COLUMN_ID + WIDTH,             50);
		default_values.put(DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID + WIDTH,      100);
		default_values.put(DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID + WIDTH,   100);
		default_values.put(DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + WIDTH,     100);
		default_values.put(DOWNLOAD_LIST_PROGRESS_COLUMN_ID + WIDTH,         100);
		default_values.put(DOWNLOAD_LIST_SOURCES_COLUMN_ID + WIDTH,          100);
		default_values.put(DOWNLOAD_LIST_REMAINING_COLUMN_ID + WIDTH,        100);
		default_values.put(DOWNLOAD_LIST_STATUS_COLUMN_ID + WIDTH,           100);
		
		default_values.put(DOWNLOAD_PEER_LIST_IP_COLUMN_ID + WIDTH,          150);
		default_values.put(DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID + WIDTH,      150);
		default_values.put(DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID + WIDTH,    100);
		default_values.put(DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + WIDTH,    100);
		
		default_values.put(UPLOAD_LIST_FILE_NAME_COLUMN_ID + WIDTH,          100);
		default_values.put(UPLOAD_LIST_FILE_SIZE_COLUMN_ID + WIDTH,          100);
		default_values.put(UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID + WIDTH,       100);
		default_values.put(UPLOAD_LIST_PEERS_COLUMN_ID + WIDTH,              100);
		default_values.put(UPLOAD_LIST_ETA_COLUMN_ID + WIDTH,                100);
		default_values.put(UPLOAD_LIST_UPLOADED_COLUMN_ID + WIDTH,           100);
		
		default_values.put(UPLOAD_PEER_LIST_IP_COLUMN_ID + WIDTH,            150);
		default_values.put(UPLOAD_PEER_LIST_STATUS_COLUMN_ID + WIDTH,        150);
		default_values.put(UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID + WIDTH,      100);
		default_values.put(UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID + WIDTH,      100);
		
		default_values.put(SEARCH_FILENAME_COLUMN_ID + WIDTH,                150);
		default_values.put(SEARCH_FILESIZE_COLUMN_ID + WIDTH,                150);
		default_values.put(SEARCH_AVAILABILITY_COLUMN_ID + WIDTH,            150);
		default_values.put(SEARCH_COMPLETESRC_COLUMN_ID + WIDTH,             150);
		default_values.put(SEARCH_FILE_TYPE_COLUMN_ID + WIDTH,               159);
		default_values.put(SEARCH_FILE_ID_COLUMN_ID + WIDTH,                 150);
		
		default_values.put(SHARED_LIST_FILE_NAME_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_SIZE_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_TYPE_COLUMN_ID + WIDTH,          150);
		default_values.put(SHARED_LIST_FILE_ID_COLUMN_ID + WIDTH,            150);
		default_values.put(SHARED_LIST_COMPLETED_COLUMN_ID + WIDTH,          150);
		
		default_values.put(TOOL_BAR_ID + VISIBILITY,                         true);
		default_values.put(STATUS_BAR_ID + VISIBILITY,                       true);
	}
	
	protected static String getColumnNodeById(int ColumnID) {
		
		switch(ColumnID) {
		
		  	case SERVER_LIST_NAME_COLUMN_ID             :  return SERVER_LIST_NAME_NODE;
		  	case SERVER_LIST_IP_COLUMN_ID               :  return SERVER_LIST_IP_NODE;
		  	case SERVER_LIST_DESCRIPTION_COLUMN_ID      :  return SERVER_LIST_DESCRIPTION_NODE;
		  	case SERVER_LIST_PING_COLUMN_ID             :  return SERVER_LIST_PING_NODE;
		  	case SERVER_LIST_USERS_COLUMN_ID            :  return SERVER_LIST_USERS_NODE;
		  	case SERVER_LIST_MAX_USERS_COLUMN_ID        :  return SERVER_LIST_MAX_USERS_NODE;
		  	case SERVER_LIST_FILES_COLUMN_ID            :  return SERVER_LIST_FILES_NODE;
		  	case SERVER_LIST_SOFT_LIMIT_COLUMN_ID       :  return SERVER_LIST_SOFT_LIMIT_NODE;
		  	case SERVER_LIST_HARD_LIMIT_COLUMN_ID       :  return SERVER_LIST_HARD_LIMIT_NODE;
		  	case SERVER_LIST_VERSION_COLUMN_ID          :  return SERVER_LIST_VERSION_NODE;
		  	
		  	case DOWNLOAD_LIST_FILE_NAME_COLUMN_ID      :  return DOWNLOAD_LIST_FILE_NAME_COLUMN_NODE;
		  	case DOWNLOAD_LIST_SIZE_COLUMN_ID           :  return DOWNLOAD_LIST_SIZE_COLUMN_NODE;
		  	case DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID    :  return DOWNLOAD_LIST_TRANSFERRED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID :  return DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID   :  return DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_NODE;
		  	case DOWNLOAD_LIST_PROGRESS_COLUMN_ID       :  return DOWNLOAD_LIST_PROGRESS_COLUMN_NODE;
		  	case DOWNLOAD_LIST_SOURCES_COLUMN_ID        :  return DOWNLOAD_LIST_SOURCES_COLUMN_NODE;
		  	case DOWNLOAD_LIST_REMAINING_COLUMN_ID      :  return DOWNLOAD_LIST_REMAINING_COLUMN_NODE;	
		  	case DOWNLOAD_LIST_STATUS_COLUMN_ID         :  return DOWNLOAD_LIST_STATUS_COLUMN_NODE;
		  		
		  	case DOWNLOAD_PEER_LIST_IP_COLUMN_ID        :  return DOWNLOAD_PEER_LIST_IP_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_STATUS_COLUMN_ID    :  return DOWNLOAD_PEER_LIST_STATUS_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_ID  :  return DOWNLOAD_PEER_LIST_NICKNAME_COLUMN_NODE;
		  	case DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_ID  :  return DOWNLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE;
		  		
		  	case UPLOAD_LIST_FILE_NAME_COLUMN_ID        :  return UPLOAD_LIST_FILENAME_COLUMN_NODE;
		  	case UPLOAD_LIST_FILE_SIZE_COLUMN_ID        :  return UPLOAD_LIST_FILESIZE_COLUMN_NODE;
		  	case UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID     :  return UPLOAD_LIST_UPLOADSPEED_COLUMN_NODE;
		  	case UPLOAD_LIST_PEERS_COLUMN_ID            :  return UPLOAD_LIST_PEERS_COLUMN_NODE;
		  	case UPLOAD_LIST_ETA_COLUMN_ID              :  return UPLOAD_LIST_ETA_COLUMN_NODE;
		  	case UPLOAD_LIST_UPLOADED_COLUMN_ID         :  return UPLOAD_LIST_UPLOADED_COLUMN_NODE;
		  		
		  	case UPLOAD_PEER_LIST_IP_COLUMN_ID          :  return UPLOAD_PEER_LIST_IP_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_STATUS_COLUMN_ID      :  return UPLOAD_PEER_LIST_STATUS_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID    :  return UPLOAD_PEER_LIST_NICKNAME_COLUMN_NODE;
		  	case UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID    :  return UPLOAD_PEER_LIST_SOFTWARE_COLUMN_NODE;
		  	
		  	case SEARCH_FILENAME_COLUMN_ID              :  return SEARCH_FILENAME_COLUMN_NODE;
		  	case SEARCH_FILESIZE_COLUMN_ID              :  return SEARCH_FILESIZE_COLUMN_NODE;
		  	case SEARCH_AVAILABILITY_COLUMN_ID          :  return SEARCH_AVAILABILITY_COLUMN_NODE;
		  	case SEARCH_COMPLETESRC_COLUMN_ID           :  return SEARCH_COMPLETESRC_COLUMN_NODE;
		  	case SEARCH_FILE_TYPE_COLUMN_ID             :  return SEARCH_FILE_TYPE_COLUMN_NODE;
		  	case SEARCH_FILE_ID_COLUMN_ID               :  return SEARCH_FILE_ID_COLUMN_NODE;
		  		
		  	case SHARED_LIST_FILE_NAME_COLUMN_ID        :  return SHARED_LIST_FILE_NAME_COLUMN_NODE;
		  	case SHARED_LIST_FILE_SIZE_COLUMN_ID        :  return SHARED_LIST_FILE_SIZE_COLUMN_NODE;
		  	case SHARED_LIST_FILE_TYPE_COLUMN_ID        :  return SHARED_LIST_FILE_TYPE_COLUMN_NODE;
		  	case SHARED_LIST_FILE_ID_COLUMN_ID          :  return SHARED_LIST_FILE_ID_COLUMN_NODE;
		  	case SHARED_LIST_COMPLETED_COLUMN_ID        :  return SHARED_LIST_COMPLETED_COLUMN_NODE;
		  	 
		}
		
		return "unknown column ID";
	}
	
	protected static String getToolBarNode() {
		
		return TOOL_BAR_NODE;
	}
	
	protected static String getStatusBarNode() {
		
		return STATUS_BAR_NODE;
	}
	
	protected static String getToolBarNodePath(String variablePath) {
		
		return UI_ROOT + variablePath + TOOL_BAR_NODE;
	}
	
	protected static boolean getToolBarDefaultVisibility() {
		return Boolean.parseBoolean(default_values.get(TOOL_BAR_ID + VISIBILITY).toString());
	}
	
	protected static String getStatusBarNodePath(String variablePath) {
		return UI_ROOT + variablePath + STATUS_BAR_NODE;
	}
	
	protected static boolean getStatusBarDefaultVisibility() {
		return Boolean.parseBoolean(default_values.get(STATUS_BAR_ID + VISIBILITY).toString());
	}
	
	protected static String getColumnNodePath(String variablePath, int columnID) {
		
		return UI_ROOT + variablePath + getColumnNodeById(columnID);
	}
	
	protected static int getDefaultColumnOrder(int columnID) {
		
		return Integer.parseInt(default_values.get(columnID + ORDER).toString());
	}
	
	protected static boolean getDefaultColumnVisibility(int columnID) {
		
		return Boolean.parseBoolean(default_values.get(columnID + VISIBILITY).toString());
	}
	
	protected static int getDefaultColumnWidth(int columnID) {
		
		return Integer.parseInt(default_values.get(columnID + WIDTH).toString());
	}

	private static Map<String,String> icon_name_by_extension = new Hashtable<String,String>();

	static {
		icon_name_by_extension.put("rar","mimetypes/archive.png");
		icon_name_by_extension.put("zip","mimetypes/archive.png");
		icon_name_by_extension.put("tar","mimetypes/archive.png");
		icon_name_by_extension.put("gz","mimetypes/archive.png");
		icon_name_by_extension.put("7z","mimetypes/archive.png");
		
		icon_name_by_extension.put("mp3","mimetypes/audio.png");
		icon_name_by_extension.put("wav","mimetypes/audio.png");
		icon_name_by_extension.put("midi","mimetypes/audio.png");
		icon_name_by_extension.put("midi","mimetypes/audio.png");
		
		icon_name_by_extension.put("iso","mimetypes/cd_image.png");
		icon_name_by_extension.put("nrg","mimetypes/cd_image.png");
		icon_name_by_extension.put("cue","mimetypes/cd_image.png");
		
		icon_name_by_extension.put("png","mimetypes/image.png");
		icon_name_by_extension.put("jpg","mimetypes/image.png");
		icon_name_by_extension.put("jpeg","mimetypes/image.png");
		icon_name_by_extension.put("gif","mimetypes/image.png");
		icon_name_by_extension.put("bmp","mimetypes/image.png");
		icon_name_by_extension.put("ico","mimetypes/image.png");
		
		icon_name_by_extension.put("pdf","mimetypes/pdf.png");
		
		icon_name_by_extension.put("doc","mimetypes/doc.png");
		icon_name_by_extension.put("wri","mimetypes/doc.png");
		icon_name_by_extension.put("odt","mimetypes/doc.png");
		icon_name_by_extension.put("sxw","mimetypes/doc.png");
		icon_name_by_extension.put("vor","mimetypes/doc.png");
		
		icon_name_by_extension.put("xls","mimetypes/calc.png");
		
		icon_name_by_extension.put("exe", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("com", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("bat", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("cmd", "mimetypes/windows_exe.png");
		
		icon_name_by_extension.put("so", "mimetypes/executable.png");
		icon_name_by_extension.put("bin", "mimetypes/executable.png");
		icon_name_by_extension.put("sh", "mimetypes/executable.png");
		
		icon_name_by_extension.put("mpg", "mimetypes/video.png");
		icon_name_by_extension.put("mpeg", "mimetypes/video.png");
		icon_name_by_extension.put("avi", "mimetypes/video.png");
		icon_name_by_extension.put("wmv", "mimetypes/video.png");
		icon_name_by_extension.put("bik", "mimetypes/video.png");
		icon_name_by_extension.put("mov", "mimetypes/video.png");
	}
	
	public static final String INFINITY_STRING	= "\u221E"; // "oo";
	public static final int    INFINITY_AS_INT = 31536000; // seconds (365days)
	
	public static InputStream getIconByExtension(String extension) {
		extension = extension.toLowerCase();
		String image_path = icon_name_by_extension.get(extension);
		if (image_path == null)
			image_path = "mimetypes/default.png";
		return UIImageRepository.getImageAsStream(image_path);
	}
	

}
