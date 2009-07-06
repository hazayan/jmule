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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jmule.core.edonkey.impl.UserHash;

/**
 * Created on 07-22-2008
 * @author javajox
 * @version $$Revision: 1.12 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/06 14:51:30 $$
 */
public class ConfigurationManagerImp implements ConfigurationManager {

	Properties config_store;
	
	List<ConfigurationListener> config_listeners = new LinkedList<ConfigurationListener>();
	
	Map<String,List<ConfigurationListener>> parameter_listeners = new Hashtable<String,List<ConfigurationListener>>();
	
	public ConfigurationManagerImp() {
		
	}
	
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
			
			user_hash = UserHash.genNewUserHash();
			
			config_store.setProperty(USER_HASH_KEY, user_hash.getAsString());
			
		} else
			
			user_hash.loadFromString(user_hash_str);
		
		return user_hash;
		
	}

	public void load() {

		try {
		   config_store.load(new FileInputStream(CONFIG_FILE));	
		 //  loadKeys();
		} catch(Throwable t) {
			t.printStackTrace();
		}
		

	}

	public void loadDefault() {

	}

	public void removeConfigurationListener(ConfigurationListener listener) {
		
        config_listeners.remove( listener );
        
        for(List<ConfigurationListener> list : parameter_listeners.values())
        	if (list.contains(listener)) 
        		list.remove(listener);
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
		 save();
		 notifyPropertyChanged( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}
	
	public void setDownloadBandwidth(String downloadBandwidth) {
		
		setDownloadBandwidth(Long.parseLong(downloadBandwidth));
	}

	public void setDownloadLimit(long downloadLimit) {
		
		config_store.setProperty( DOWNLOAD_LIMIT_KEY, downloadLimit + "" );
		save();	
		notifyPropertyChanged( DOWNLOAD_LIMIT_KEY, downloadLimit );
	}
	
	public void setDownloadLimit(String downloadLimit) {
		
		setDownloadLimit(Long.parseLong(downloadLimit));
		
	}

	public void setNickName(String nickName) {

        config_store.setProperty(NICK_NAME_KEY, nickName);
		save();
        notifyPropertyChanged( NICK_NAME_KEY, nickName ); 
	}

	public void setSharedFolders(List<File> sharedFolders) {
		
		// first remove old values
		List<File> file_list = getSharedFolders();
		
		int key_count = file_list == null ? 0 : file_list.size();
		
		for (int i = 0;i< key_count ;i++)
			
			config_store.remove(SHARED_DIRECTORIES_KEY + i);
		
		for(int i = 0; i < sharedFolders.size(); i++) {
			
			config_store.setProperty(SHARED_DIRECTORIES_KEY + i, sharedFolders.get(i).toString());
			
		}
		save();	
		notifyPropertyChanged( SHARED_DIRECTORIES_KEY, sharedFolders );
	}
	
	public List<File> getSharedFolders() {
		
		int i = 0;
		
		String file_name;
		
		List<File> shared_directories = new LinkedList<File>();
		
		while( true ) {
			
		  try {	
			  
			file_name = config_store.getProperty(SHARED_DIRECTORIES_KEY + i);
			
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
	
		setTCP(Integer.parseInt(tcp));
	}
	
	public void setTCP(int tcp) {
		config_store.setProperty( TCP_PORT_KEY, tcp + "" );
		save(); 
		notifyPropertyChanged( TCP_PORT_KEY, tcp );
		
	}
	
	public void setUDP(String udp) {	
		
		setUDP(Integer.parseInt(udp));
	}

	public void setUDP(int udp) {
		config_store.setProperty( UDP_PORT_KEY, udp + "" );
		save(); 
		notifyPropertyChanged( UDP_PORT_KEY, udp );

	}

	public void setUploadBandwidth(long downloadBandwidth) {
		
       config_store.setProperty( UPLOAD_BANDWIDTH_KEY, downloadBandwidth + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}
	
	public void setUploadBandwidth(String uploadBandwidth) {
		
		setUploadBandwidth(Long.parseLong(uploadBandwidth));
		
	}

	public void setUploadLimit(long uploadLimit) {
		
       config_store.setProperty( UPLOAD_LIMIT_KEY, uploadLimit + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_LIMIT_KEY, uploadLimit );
	}
	
	public void setUploadLimit(String uploadLimit) {
		
		setUploadLimit(Long.parseLong(uploadLimit));
		
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
		save(); 
		notifyPropertyChanged( UDP_ENABLED_KEY, enabled );
	}

	public boolean isUDPEnabled() {
		String udp_enabled = config_store.getProperty(UDP_ENABLED_KEY);
		return ( udp_enabled == null ) ? UDP_ENABLED : Boolean.parseBoolean(udp_enabled);
	}
	
	public Integer getIntParameter(String parameter, Integer defaultValue) {
		Integer result;
		try {
			result = Integer.parseInt(config_store.getProperty(parameter));
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}
	
	public Float getFloatParameter(String parameter, Float defaultValue) {
		Float result;
		try {
			result = Float.parseFloat(config_store.getProperty(parameter));
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}
	
	public Double getDoubleParameter(String parameter, Double defaultValue) {
		Double result;
		try {
			result = Double.parseDouble(config_store.getProperty(parameter));
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}

	public Long getLongParameter(String parameter, Long defaultValue) {
		Long result;
		try {
			result = Long.parseLong(config_store.getProperty(parameter));
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}

	public String getStringParameter(String parameter, String defaultValue) {
		String result;
		try {
			result = config_store.getProperty(parameter);
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}
	
	public boolean isJKadEnabled() {
		
		return getBooleanParameter(JKAD_ENABLED_KEY, true);
	}


	public void setJKadStatus(boolean newStatus) {
		setParameter(JKAD_ENABLED_KEY, newStatus+"");
		notifyPropertyChanged(JKAD_ENABLED_KEY, newStatus);
	}	

	
	public Boolean getBooleanParameter(String parameter, Boolean defaultValue) {
		Boolean result;
		try {
			result = Boolean.parseBoolean(config_store.getProperty(parameter));
		}catch(Throwable e) {
			return defaultValue;
		}
		return result;
	}

	public void setParameter(String parameter, int value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}

	public void setParameter(String parameter, String value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}

	public void setParameter(String parameter, float value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}

	public void setParameter(String parameter, long value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}

	public void setParameter(String parameter, boolean value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}
	
	public void setParameter(String parameter, double value) {
		config_store.put(parameter, value+"");
		save(); 
		notifyCustomPropertyChanged(parameter,value);
	}

	public void removeParameter(String parameter) {
		config_store.remove(parameter);
		save(); 
	}
	
	public boolean hasParameter(String parameter) {
		return config_store.containsKey(parameter);
	}
	
/*	private void loadKeys() {
		BigInteger public_key, private_key;
		BigInteger public_exponent, private_exponent;
		if (config_store.containsKey(PUBLIC_KEY_KEY)&&config_store.containsKey(PRIVATE_KEY_KEY)) {
			private_key = new BigInteger(config_store.getProperty(PRIVATE_KEY_KEY),16);
			private_exponent = new BigInteger(config_store.getProperty(PRIVATE_EXPONENT_KEY),16);
			
			public_key = new BigInteger(config_store.getProperty(PUBLIC_KEY_KEY),16);
			public_exponent = new BigInteger(config_store.getProperty(PUBLIC_EXPONENT_KEY),16);
			
			JMuleRSA.getSingleton().setKeys(public_key, public_exponent, private_key, private_exponent);
		} else {
			JMuleRSA.getSingleton().genKeys();
			setKeys();
			
			save();
		}

	}*/
	
/*	private void setKeys() {
		BigInteger publicKey, publicExponent;
		BigInteger privateKey, privateExponent;
		
		publicKey = JMuleRSA.getSingleton().getPublicKey();
		publicExponent = JMuleRSA.getSingleton().getPublicExponent();
		
		privateKey = JMuleRSA.getSingleton().getPrivateKey();
		privateExponent = JMuleRSA.getSingleton().getPrivateExponent();
		
		config_store.setProperty(PUBLIC_KEY_KEY, publicKey.toString(16));
		config_store.setProperty(PUBLIC_EXPONENT_KEY, publicExponent.toString(16));
		config_store.setProperty(PRIVATE_KEY_KEY, privateKey.toString(16));
		config_store.setProperty(PRIVATE_EXPONENT_KEY, privateExponent.toString(16));
	}*/
	
	private void notifyCustomPropertyChanged(String key, Object new_value) {
		List<ConfigurationListener> listener_list = parameter_listeners.get(key);
		if (listener_list == null) return ;
		for(ConfigurationListener listener : listener_list)
			listener.parameterChanged(key, new_value);
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
			    
		   else if( property == DOWNLOAD_LIMIT_KEY) listener.downloadLimitChanged((Long)new_value);
			    
		   else if( property == UPLOAD_LIMIT_KEY) listener.uploadLimitChanged((Long)new_value);
		   else if( property == JKAD_ENABLED_KEY) listener.jkadStatusChanged((Boolean)new_value);
			
		}
		
	}

	public void addConfigurationListener(ConfigurationListener listener, String parameter) {
		List<ConfigurationListener> listeners_list = parameter_listeners.get(parameter);
		if (listeners_list == null) {
			listeners_list = new ArrayList<ConfigurationListener>();
			parameter_listeners.put(parameter, listeners_list);
		}
		listeners_list.add(listener);
	}


}
