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
package org.jmule.ui.localizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.jmule.ui.UIConstants;

/**
 * 
 * @author javajox
 * @author binary
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/26 19:41:32 $$
 */
public class Localizer {

	private static ResourceBundle resources = null;
	
	private static HashMap<Integer,String> columns_description = new HashMap<Integer,String>();
	
	public static void initialize() {
		InputStream input_stream = (Localizer.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/internat/Language_en_US.properties"));
		try {
			resources = new PropertyResourceBundle(input_stream);
			
			columns_description.put(UIConstants.SERVER_LIST_NAME_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.name.desc"));
			columns_description.put(UIConstants.SERVER_LIST_CC_COLUMN_ID, 				_._("mainwindow.serverlisttab.serverlist.column.country_code.desc"));
			columns_description.put(UIConstants.SERVER_LIST_FLAG_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.country.desc"));
			columns_description.put(UIConstants.SERVER_LIST_IP_COLUMN_ID, 				_._("mainwindow.serverlisttab.serverlist.column.address.desc"));
			columns_description.put(UIConstants.SERVER_LIST_DESCRIPTION_COLUMN_ID, 		_._("mainwindow.serverlisttab.serverlist.column.description.desc"));
			columns_description.put(UIConstants.SERVER_LIST_PING_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.ping.desc"));
			columns_description.put(UIConstants.SERVER_LIST_USERS_COLUMN_ID,			_._("mainwindow.serverlisttab.serverlist.column.users.desc"));
			columns_description.put(UIConstants.SERVER_LIST_MAX_USERS_COLUMN_ID, 		_._("mainwindow.serverlisttab.serverlist.column.maxusers.desc"));
			columns_description.put(UIConstants.SERVER_LIST_FILES_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.files.desc"));
			columns_description.put(UIConstants.SERVER_LIST_SOFT_LIMIT_COLUMN_ID, 		_._("mainwindow.serverlisttab.serverlist.column.soft_limit.desc"));
			columns_description.put(UIConstants.SERVER_LIST_HARD_LIMIT_COLUMN_ID, 		_._("mainwindow.serverlisttab.serverlist.column.hard_limit.desc"));
			columns_description.put(UIConstants.SERVER_LIST_VERSION_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.software.desc"));
			columns_description.put(UIConstants.SERVER_LIST_STATIC_COLUMN_ID, 			_._("mainwindow.serverlisttab.serverlist.column.static.desc"));
			
			
			columns_description.put(UIConstants.DOWNLOAD_LIST_FILE_NAME_COLUMN_ID, 		_._("mainwindow.transferstab.downloads.column.filename.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_SIZE_COLUMN_ID, 			_._("mainwindow.transferstab.downloads.column.size.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_TRANSFERRED_COLUMN_ID,	_._("mainwindow.transferstab.downloads.column.transferred.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_DOWNLOAD_SPEED_COLUMN_ID,	_._("mainwindow.transferstab.downloads.column.download_speed.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_UPLOAD_SPEED_COLUMN_ID,	_._("mainwindow.transferstab.downloads.column.upload_speed.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_PROGRESS_COLUMN_ID,		_._("mainwindow.transferstab.downloads.column.progress.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_SOURCES_COLUMN_ID,		_._("mainwindow.transferstab.downloads.column.sources.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_REMAINING_COLUMN_ID,		_._("mainwindow.transferstab.downloads.column.remaining.desc"));
			columns_description.put(UIConstants.DOWNLOAD_LIST_STATUS_COLUMN_ID,			_._("mainwindow.transferstab.downloads.column.status.desc"));
			
			columns_description.put(UIConstants.UPLOAD_LIST_FILE_NAME_COLUMN_ID,		_._("mainwindow.transferstab.uploads.column.filename.desc"));
			columns_description.put(UIConstants.UPLOAD_LIST_FILE_SIZE_COLUMN_ID,		_._("mainwindow.transferstab.uploads.column.filesize.desc"));
			columns_description.put(UIConstants.UPLOAD_LIST_UPLOAD_SPEED_COLUMN_ID,		_._("mainwindow.transferstab.uploads.column.uploadspeed.desc"));
			columns_description.put(UIConstants.UPLOAD_LIST_PEERS_COLUMN_ID,			_._("mainwindow.transferstab.uploads.column.peers.desc"));
			columns_description.put(UIConstants.UPLOAD_LIST_ETA_COLUMN_ID,				_._("mainwindow.transferstab.uploads.column.eta.desc"));
			columns_description.put(UIConstants.UPLOAD_LIST_UPLOADED_COLUMN_ID,			_._("mainwindow.transferstab.uploads.column.uploaded.desc"));
			
			columns_description.put(UIConstants.SEARCH_FILENAME_COLUMN_ID,				_._("mainwindow.searchtab.column.filename.desc"));
			columns_description.put(UIConstants.SEARCH_FILESIZE_COLUMN_ID,				_._("mainwindow.searchtab.column.filesize.desc"));
			columns_description.put(UIConstants.SEARCH_AVAILABILITY_COLUMN_ID,			_._("mainwindow.searchtab.column.availability.desc"));
			columns_description.put(UIConstants.SEARCH_COMPLETESRC_COLUMN_ID,			_._("mainwindow.searchtab.column.completesrcs.desc"));
			columns_description.put(UIConstants.SEARCH_FILE_TYPE_COLUMN_ID,				_._("mainwindow.searchtab.column.filetype.desc"));
			columns_description.put(UIConstants.SEARCH_FILE_ID_COLUMN_ID,				_._("mainwindow.searchtab.column.fileid.desc"));
			
			columns_description.put(UIConstants.SHARED_LIST_FILE_NAME_COLUMN_ID,		_._("mainwindow.sharedtab.column.filename.desc"));
			columns_description.put(UIConstants.SHARED_LIST_FILE_SIZE_COLUMN_ID,		_._("mainwindow.sharedtab.column.filesize.desc"));
			columns_description.put(UIConstants.SHARED_LIST_FILE_TYPE_COLUMN_ID,		_._("mainwindow.sharedtab.column.filetype.desc"));
			columns_description.put(UIConstants.SHARED_LIST_FILE_ID_COLUMN_ID,			_._("mainwindow.sharedtab.column.fileid.desc"));
			columns_description.put(UIConstants.SHARED_LIST_COMPLETED_COLUMN_ID,		_._("mainwindow.sharedtab.column.completed.desc"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getColumnDesc(int columnID) {
		return columns_description.get(columnID);
	}
	
	public static String _(String key) {
		
		return getString(key);
		
	}
	
	/**
	 * Expands a message text and replaces occurrences of %1 with first param, %2 with second...
	 * @param key
	 * @param params
	 * @return
	 */
	public static String _(String key,String... params) {
		return getString(key, params);
	}
	
	public static String getString(String key) {
		try {
			return resources.getString(key);
		}catch(MissingResourceException e) {
			return "";
		}
	}
	
	/**
	  * Expands a message text and replaces occurrences of %1 with first param, %2 with second...
	  * @param key
	  * @param params
	  * @return
	  */
	public static String getString(String key,String... params ) {
	  	String	res = getString(key);
	  	
	  	for(int i=0;i<params.length;i++){
	  		
	  		String	from_str 	= "%" + (i+1);
	  		String	to_str		= params[i];
	  		
	  		res = replaceStrings( res, from_str, to_str );
	  	}
	  	
	  	return( res );
	}

	private static String replaceStrings(String	str,String	f_s,String	t_s ) {
	  	int	pos = 0;
	  	
	  	String	res  = "";
	  	
	  	while( pos < str.length()){
	  	
	  		int	p1 = str.indexOf( f_s, pos );
	  		
	  		if ( p1 == -1 ){
	  			
	  			res += str.substring(pos);
	  			
	  			break;
	  		}
	  		
	  		res += str.substring(pos, p1) + t_s;
	  		
	  		pos = p1+f_s.length();
	  	}
	  	
	  	return( res );
	}
	  
}
