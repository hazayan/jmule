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

import javax.naming.ConfigurationException;

import org.jmule.core.JMException;
import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.core.utils.AddressUtils;
import org.jmule.core.utils.Misc;
import org.jmule.core.utils.NetworkUtils;

/**
 * Created on 07-22-2008
 * @author javajox
 * @version $$Revision: 1.16 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/08/13 06:37:01 $$
 */
public class ConfigurationManagerImp implements InternalConfigurationManager {

	private static final String 	DEFAULT_FALSE = "false";
	private static final String 	DEFAULT_TRUE  = "true";
	
	private Properties config_store;
	
	private List<ConfigurationListener> config_listeners = new LinkedList<ConfigurationListener>();
		
	public ConfigurationManagerImp() {
		
	}

	
	public void addConfigurationListener(ConfigurationListener listener) {

		  config_listeners.add( listener );
	}

	
	public long getDownloadBandwidth() throws ConfigurationManagerException {
		
		String download_bandwidth = config_store.getProperty(DOWNLOAD_BANDWIDTH_KEY,DOWNLOAD_BANDWIDTH+"");
		long value;
		try {
			value = Long.parseLong( download_bandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value <= 0)
			throw new ConfigurationManagerException("Download bandwidth can't be negative or 0, " + value + " given");
		return value;
	}

	
	public long getDownloadLimit() throws ConfigurationManagerException {
		
		String download_limit = config_store.getProperty(DOWNLOAD_LIMIT_KEY,DOWNLOAD_LIMIT+"");
		
		long value;
		try {
			value = Long.parseLong( download_limit );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value < 0)
			throw new ConfigurationManagerException("Download limit can't be negative, " + value + " given");
		return value;
		
	}

	
	public String getNickName() throws ConfigurationManagerException {
        String nick_name = config_store.getProperty(NICK_NAME_KEY, NICK_NAME);
        
        if ( nick_name.length()==0 )
        	throw new ConfigurationManagerException("The nickname can't be 0 length");
        
		return nick_name;
	}

	
	public int getTCP() throws ConfigurationManagerException {
		
		String tcp = config_store.getProperty(TCP_PORT_KEY,TCP_PORT+"");
		
		int value;
		try {
			value = Integer.parseInt( tcp );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (! ( ( value >= 0 ) && ( value <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + value + " given");
		return value;
	}

	
	public int getUDP() throws ConfigurationManagerException {
		
		String udp = config_store.getProperty(UDP_PORT_KEY,UDP_PORT+"");
		
		int value;
		try {
			value = Integer.parseInt( udp );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (! ( ( value >= 0 ) && ( value <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + value + " given");
		return value;

	}

	
	public long getUploadBandwidth() throws ConfigurationManagerException  {
		
		String upload_bandwidth = config_store.getProperty(UPLOAD_BANDWIDTH_KEY, UPLOAD_BANDWIDTH + "");
		
		long value;
		try {
			value = Long.parseLong( upload_bandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value <= 0)
			throw new ConfigurationManagerException("Upload bandwidth can't be negative or 0, " + value + " given");
		return value;
	}

	
	public long getUploadLimit() throws ConfigurationManagerException {
		
		String upload_limit = config_store.getProperty(UPLOAD_LIMIT_KEY, UPLOAD_LIMIT+"");
		
		long value;
		try {
			value = Long.parseLong( upload_limit );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		
		if (value < 0)
			throw new ConfigurationManagerException("Upload limit can't be negative, " + value + " given");
		return value;
	}
	
	
	public UserHash getUserHash() throws ConfigurationManagerException {
		
		String user_hash_str = config_store.getProperty(USER_HASH_KEY);
		
		if (user_hash_str==null) {
			return null;
		
		} else {
			
			try {
				UserHash user_hash = new UserHash();
				user_hash.loadFromString(user_hash_str);
				return user_hash;
			}catch(Throwable cause) {
				
				throw new ConfigurationManagerException( cause );
			}
		}		
	}
	
	public void setUserHash(String userHash) throws ConfigurationManagerException {
		if (!Misc.isHexadecimalNumber(userHash))
			throw new ConfigurationManagerException("User Hash must be hexadecimal string");
		config_store.setProperty(USER_HASH_KEY, userHash);
		save();
		notifyPropertyChanged(USER_HASH_KEY, userHash);
	}
	
	
	public void load() throws ConfigurationManagerException {

		try {
		   config_store.load( new FileInputStream( CONFIG_FILE ) );	
		 //  loadKeys();
		} catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		

	}


	
	public void removeConfigurationListener(ConfigurationListener listener) {
		
        config_listeners.remove( listener );
	}

	
	public void save() throws ConfigurationManagerException {
       try {
    	   
    	   config_store.store( new FileOutputStream( CONFIG_FILE ),"" );
    	   
       } catch(Throwable cause) {
    	   throw new ConfigurationManagerException( cause );
       }

	}

	
	public void setDownloadBandwidth(long downloadBandwidth) throws ConfigurationManagerException {
		 if (downloadBandwidth <= 0)
			 throw new ConfigurationManagerException("Download bandwidth can't be negative or 0, " + downloadBandwidth + " given");
		 config_store.setProperty( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth + "" );
		 save();
		 notifyPropertyChanged( DOWNLOAD_BANDWIDTH_KEY, downloadBandwidth );
	}
	
	
	public void setDownloadBandwidth(String downloadBandwidth) throws ConfigurationManagerException  {
		long bandwidth;
		try {
			bandwidth = Long.parseLong( downloadBandwidth );
		}catch(Throwable cause) {
			throw new ConfigurationManagerException( cause );
		}
		setDownloadBandwidth(bandwidth);
	}

	
	public void setDownloadLimit(long downloadLimit) throws ConfigurationManagerException {
		if ( downloadLimit < 0 )
			throw new ConfigurationManagerException("Download limit can't be negative, " + downloadLimit + " given");
		config_store.setProperty( DOWNLOAD_LIMIT_KEY, downloadLimit + "" );
		save();	
		notifyPropertyChanged( DOWNLOAD_LIMIT_KEY, downloadLimit );
	}
	
	
	public void setDownloadLimit(String downloadLimit) throws ConfigurationManagerException {
		long download_limit;
		try {
			download_limit = Long.parseLong(downloadLimit);
		}catch ( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		setDownloadLimit(download_limit);
	}

	
	public void setNickName(String nickName) throws ConfigurationManagerException  {
		if( nickName.length() == 0 )
			throw new ConfigurationManagerException("The nickname can't be 0 length");
		
		if( nickName.length() > 65535 )
			throw new ConfigurationManagerException("The nickname length can't be more than 65535 chars");
		
        config_store.setProperty(NICK_NAME_KEY, nickName);
		save();
        notifyPropertyChanged( NICK_NAME_KEY, nickName ); 
	}

	
	public void setSharedFolders(List<File> sharedFolders) throws ConfigurationManagerException {
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
	
	
	public List<File> getSharedFolders() throws ConfigurationManagerException {
		
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
			
		  } catch( Throwable cause ) {
			  
			  throw new ConfigurationManagerException( cause );
		  }
		  
		}
		
		return shared_directories;
	}

	
	public void setTCP(String tcp) throws ConfigurationManagerException {
		int tcp_port;
		try {
			tcp_port = Integer.parseInt( tcp );
		}catch(Throwable cause) {
			
			throw new ConfigurationManagerException(cause);
		}
		
		setTCP( tcp_port );
	}

	
	public void setTCP(int tcp) throws ConfigurationManagerException {
		
		if ( ! ( ( tcp >= 0 ) && ( tcp <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + tcp + " given");
		
		config_store.setProperty( TCP_PORT_KEY, tcp + "" );
		save(); 
		notifyPropertyChanged( TCP_PORT_KEY, tcp );
	}

	
	public void setUDP(String udp) throws ConfigurationManagerException  {	
		
		int udp_port;
		
		try {
			
			udp_port = Integer.parseInt(udp);
			
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		setUDP( udp_port );
	}

	
	public void setUDP(int udp) throws ConfigurationManagerException  {
		
		if ( ! ( ( udp >= 0 ) && ( udp <= 65535 ) ) )
			throw new ConfigurationManagerException("The port between 0 and 65535, " + udp + " given");
		
		config_store.setProperty( UDP_PORT_KEY, udp + "" );
		save(); 
		notifyPropertyChanged( UDP_PORT_KEY, udp );

	}

	
	public void setUploadBandwidth(long uploadBandwidth) throws ConfigurationManagerException {
	   if (uploadBandwidth <= 0) {
		   throw new ConfigurationManagerException("Upload bandwidth can't be negative or 0, " + uploadBandwidth + " given");
	   }
       config_store.setProperty( UPLOAD_BANDWIDTH_KEY, uploadBandwidth + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_BANDWIDTH_KEY, uploadBandwidth );
	}

	
	public void setUploadBandwidth(String uploadBandwidth) throws ConfigurationManagerException {
		long upload_bandwidth;
		try {
			upload_bandwidth = Long.parseLong(uploadBandwidth);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		setUploadBandwidth(upload_bandwidth);
		
	}

	
	public void setUploadLimit(long uploadLimit) throws ConfigurationManagerException {
	   if ( uploadLimit < 0 ) 
		   throw new ConfigurationManagerException("Upload limit can't be negative, " + uploadLimit + " given");
	   
       config_store.setProperty( UPLOAD_LIMIT_KEY, uploadLimit + "" );
       save(); 
       notifyPropertyChanged( UPLOAD_LIMIT_KEY, uploadLimit );
	}
	
	
	public void setUploadLimit(String uploadLimit) throws ConfigurationManagerException {
		
		long upload_limit;
		try {
			upload_limit = Long.parseLong(uploadLimit);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException ( cause );
		}
		
		setUploadLimit(upload_limit);
		
	}
	
	
	public void initialize() {
		config_store = new Properties();
	}

	
	public void shutdown() {

		 try {
			this.save();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
	}

	
	public void start() {
		
         try {
			this.load();
		} catch (ConfigurationManagerException cause) {
			cause.printStackTrace();
		}
	}

	
	public void setUDPEnabled(boolean enabled) throws ConfigurationManagerException  {	
		config_store.setProperty( UDP_ENABLED_KEY, enabled + "" );
		save(); 
		notifyPropertyChanged( UDP_ENABLED_KEY, enabled );
	}
	
	
	public boolean isUDPEnabled() throws ConfigurationManagerException  {
		String udp_enabled = config_store.getProperty(UDP_ENABLED_KEY, UDP_ENABLED+"");
		boolean is_udp_enabled;
		try {
			is_udp_enabled = Boolean.parseBoolean(udp_enabled);
		}catch(Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		
		return is_udp_enabled;
	}
	
	
	public boolean isJKadAutoconnectEnabled() throws ConfigurationManagerException {
		String status = config_store.getProperty(JKAD_ENABLED_KEY, DEFAULT_TRUE);
		boolean jkad_enabled;
		try {
			jkad_enabled = Boolean.parseBoolean(status);
		}catch(Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		return jkad_enabled;
	}

	
	public void setAutoconnectJKad(boolean newStatus) throws ConfigurationManagerException {
		config_store.setProperty(JKAD_ENABLED_KEY, newStatus+"");
		save();
		notifyPropertyChanged(JKAD_ENABLED_KEY, newStatus);
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
	

	
	private void notifyPropertyChanged(String property, Object new_value) {
		
		for(ConfigurationListener listener : config_listeners) {
			
			try {
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
				    
			   else if( property == SERVER_LIST_UPDATE_ON_CONNECT_KEY) listener.updateServerListAtConnectChanged((Boolean)new_value);
			   
			   else if( property == JKAD_ID_KEY) listener.jkadIDChanged((String) new_value);
				    
			   else if( property == NIC_NAME_KEY) listener.nicNameChanged((String) new_value);
				    
			   else if( property == NIC_IP_KEY) listener.nicIPChanged((String) new_value);
				    
			}catch(Throwable cause) {
				cause.printStackTrace();
			}
			
		}
		
	}

	
	public void setUpdateServerListAtConnect(boolean newStatus) throws ConfigurationManagerException {
		config_store.setProperty(SERVER_LIST_UPDATE_ON_CONNECT_KEY, newStatus+"");
		save();
		notifyPropertyChanged(SERVER_LIST_UPDATE_ON_CONNECT_KEY, newStatus);
		
	}

	
	public boolean updateServerListAtConnect() throws ConfigurationManagerException {
		String status = config_store.getProperty(SERVER_LIST_UPDATE_ON_CONNECT_KEY,DEFAULT_FALSE);
		boolean update_server_list;
		try {
			update_server_list = Boolean.parseBoolean(status);
		}catch( Throwable cause ) {
			throw new ConfigurationManagerException( cause );
		}
		return update_server_list;
	}

	
	public String getJKadClientID()throws ConfigurationManagerException {
		return config_store.getProperty(JKAD_ID_KEY, null);
	}

	
	public void setJKadClientID(String newID) throws ConfigurationManagerException {
		if (!Misc.isHexadecimalNumber(newID))
			throw new ConfigurationManagerException("JKad ID must be hexadecimal string");
		config_store.setProperty(JKAD_ID_KEY, newID);
		save();
		notifyPropertyChanged(JKAD_ID_KEY, newID);
	}
	
    public String getNicName() throws ConfigurationManagerException {
    	if (!config_store.containsKey(NIC_NAME_KEY))
    		return null;
    	String nicName = config_store.getProperty(NIC_NAME_KEY);
    	try {
			if (!NetworkUtils.hasNicName(nicName))
				throw new ConfigurationManagerException("The Nic "+nicName+" doesn't exists");
		} catch (JMException cause) {
			throw new ConfigurationManagerException(cause);
		}
    	return nicName;
    }
    
    public void setNicName(String nicName) throws ConfigurationManagerException {
    	try {
			if (!NetworkUtils.hasNicName(nicName))
				throw new ConfigurationManagerException("The Nic "+nicName+" doesn't exists");
		} catch (JMException cause) {
			throw new ConfigurationManagerException(cause);
		}
    	config_store.setProperty(NIC_NAME_KEY, nicName);
    	save();
		notifyPropertyChanged(NIC_NAME_KEY, nicName);
    		
    }
    
    public String getNicIP() throws ConfigurationManagerException {
    	if (!config_store.containsKey(NIC_IP_KEY))
    		return null;
    	String nicIP = config_store.getProperty(NIC_IP_KEY);
    	if (nicIP.length()==0)
    		throw new ConfigurationManagerException("Nic IP length is 0");
    	if (!AddressUtils.isValidIP(nicIP))
    		throw new ConfigurationManagerException("Nic IP "+nicIP+" is not valid");
    	return nicIP;
    }
    
    public void setNicIP(String nicIP) throws ConfigurationManagerException {
    	if (!AddressUtils.isValidIP(nicIP))
    		throw new ConfigurationManagerException("Nic IP "+nicIP+" is not valid");
    	if (nicIP.length()==0)
    		throw new ConfigurationManagerException("Nic IP length is 0");
    	config_store.setProperty(NIC_IP_KEY, nicIP);
    	save();
		notifyPropertyChanged(NIC_IP_KEY, nicIP);
    }


}
