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
package org.jmule.core.configmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jmule.core.edonkey.impl.UserHash;

/**
 * Created on 07-22-2008
 * @author javajox
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/08/03 09:38:27 $$
 */
public class ConfigurationManagerImp implements ConfigurationManager {

	Properties config_store;
	
	List<ConfigurationListener> config_listeners = new LinkedList<ConfigurationListener>();
	
	public void addConfigurationListener(ConfigurationListener listener) {

		  config_listeners.add( listener );
	}

	public long getDownloadBandwidth() {
		
		String download_bandwidth = config_store.getProperty(DOWNLOAD_BANDWIDTH_KEY);
		
		return ( download_bandwidth == null ) ? DOWNLOAD_BANDWIDTH : Integer.parseInt(download_bandwidth);
	}

	public long getDownloadLimit() {
		
		String download_limit = config_store.getProperty(DOWNLOAD_LIMIT_KEY);
		
		return ( download_limit == null ) ? DOWNLOAD_LIMIT : Integer.parseInt(download_limit);
	}

	public String getNickName() {
        
		return config_store.getProperty(NICK_NAME_KEY, NICK_NAME);
	}

	public int getTCP() {
		
		String tcp = config_store.getProperty(TCP_PORT_KEY);
		
		return ( tcp == null ) ? TCP_PORT : Integer.parseInt(tcp);
	}
	
	public int getUDP() {
		
		String udp = config_store.getProperty(UDP_PORT_KEY);
		
		return ( udp == null ) ? UDP_PORT : Integer.parseInt(udp);
	}

	public long getUploadBandwidth() {
		
		String upload_bandwidth = config_store.getProperty(UPLOAD_BANDWIDTH_KEY);
		
		return ( upload_bandwidth == null ) ? UPLOAD_BANDWIDTH : Long.parseLong(upload_bandwidth);
	}

	public long getUploadLimit() {
		
		String upload_limit = config_store.getProperty(UPLOAD_LIMIT_KEY);
		
		return ( upload_limit == null ) ? UPLOAD_LIMIT : Long.parseLong(upload_limit); 
	}
	
	public UserHash getUserHash() {
		
		String user_hash_str = config_store.getProperty(USER_HASH_KEY);
		
		UserHash user_hash = new UserHash();
		
		if (user_hash_str==null) {
			
			user_hash.genNewUserHash();
			
			config_store.setProperty(USER_HASH_KEY, user_hash.getAsString());
			
		} else
			
			user_hash.loadFromString(user_hash_str);
		
		return user_hash;
		
	}

	public void load() {

		try {
			
		   config_store.load(new FileInputStream(CONFIG_FILE));	
			
		} catch(Throwable t) {
			
		}
		

	}

	public void loadDefault() {

	}

	public void removeConfigurationListener(ConfigurationListener listener) {
		
        config_listeners.remove( listener );
	}

	public void save() {
	
       try {
    	   
    	   config_store.store(new FileOutputStream(CONFIG_FILE),"");
    	   
       } catch(Throwable t) {
    	   t.printStackTrace();
       }

	}

	public void setDownloadBandwidth(long downloadBandwidth) {
		
		 config_store.setProperty( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth + "" );
		 
		 notifyPropertyChanged( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}

	public void setDownloadLimit(long downloadLimit) {
		
		config_store.setProperty( DOWNLOAD_LIMIT_KEY, downloadLimit + "" );
		
		notifyPropertyChanged( DOWNLOAD_LIMIT_KEY, downloadLimit );
	}


	public void setNickName(String nickName) {

        config_store.setProperty(NICK_NAME_KEY, nickName);
        
        notifyPropertyChanged( NICK_NAME_KEY, nickName ); 
	}

	public void setSharedFolders(List<File> sharedFolders) {
		
		for(int i = 0; i < sharedFolders.size(); i++) {
			
			config_store.setProperty(SHARED_DIRECTORIES_KEY + i, sharedFolders.get(i).toString());
			
		}
		
		notifyPropertyChanged( SHARED_DIRECTORIES_KEY, sharedFolders );
	}
	
	public List<File> getSharedFolders() {
		
		int i = 0;
		
		String file_name;
		
		List<File> shared_directories = new LinkedList<File>();
		
		while( true ) {
			
		  try {	
			  
			file_name = config_store.getProperty("SharedDirectory" + i);
			
			if( file_name == null ) {
				
				if( shared_directories.isEmpty() ) shared_directories = null;
				
				break;
			}
			
			shared_directories.add( new File( file_name ) );
			
			++i;
			
		  } catch( Throwable t ) {
	
		  }
		  
		}
		
		return shared_directories;
	}

	public void setTCP(String tcp) {
	
		config_store.setProperty( TCP_PORT_KEY, tcp );
		 
	}
	
	public void setTCP(int tcp) {
		
		setTCP( tcp + "" );
		
		notifyPropertyChanged( TCP_PORT_KEY, tcp );
	}
	
	public void setUDP(String udp) {	
		
		config_store.setProperty( UDP_PORT_KEY, udp );
		
	}

	public void setUDP(int udp) {
		
		setUDP( udp + "" );
       
       notifyPropertyChanged( UDP_PORT_KEY, udp );
	}

	public void setUploadBandwidth(long downloadBandwidth) {
		
       config_store.setProperty( UPLOAD_BANDWIDTH_KEY, downloadBandwidth + "" );
       
       notifyPropertyChanged( UPLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}

	public void setUploadLimit(long uploadLimit) {
		
       config_store.setProperty( UPLOAD_LIMIT_KEY, uploadLimit + "" );
       
       notifyPropertyChanged( UPLOAD_LIMIT_KEY, uploadLimit );
	}

	public void initialize() {
		config_store = new Properties();
	}

	public void shutdown() {

		 this.save();
	}

	public void start() {
		
         this.load();
	}

	public void setUDPEnabled(boolean enabled) {
		
		config_store.setProperty( UDP_ENABLED_KEY, enabled + "" );
		
		notifyPropertyChanged( UDP_ENABLED_KEY, enabled );
	}

	public boolean isUDPEnabled() {
		
		String udp_enabled = config_store.getProperty(UDP_ENABLED_KEY);
		return ( udp_enabled == null ) ? UDP_ENABLED : Boolean.parseBoolean(udp_enabled);
	}
	
	private void notifyPropertyChanged(String property, Object new_value) {
		
		for(ConfigurationListener listener : config_listeners) {
			
			    if( property == NICK_NAME_KEY ) listener.nickNameChanged((String)new_value); 
			    
		   else if( property == TCP_PORT_KEY ) listener.TCPPortChanged((Integer)new_value);
			    
		   else if( property == UDP_PORT_KEY ) listener.UDPPortChanged((Integer)new_value);
			    
		   else if( property == DOWNLOAD_BANDWIDTH_KEY ) listener.downloadBandwidthChanged((Long)new_value);
			    
		   else if( property == UPLOAD_BANDWIDTH_KEY ) listener.uploadBandwidthChanged((Long)new_value);
			    
		   else if( property == UDP_ENABLED_KEY ) listener.isUDPEnabledChanged((Boolean)new_value);
			    
		   else if( property == SHARED_DIRECTORIES_KEY) listener.sharedDirectoriesChanged((List<File>)new_value);
			
		}
		
	}

}
