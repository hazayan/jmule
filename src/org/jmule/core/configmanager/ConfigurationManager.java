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
import java.util.List;

import org.jmule.core.JMuleManager;
import org.jmule.core.edonkey.impl.UserHash;

/**
 * Created on 07-17-2008
 * @author javajox
 * @version $$Revision: 1.6 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/11 18:30:18 $$
 */
public interface ConfigurationManager extends JMuleManager {

	// directories
	public static final String       INCOMING_DIR    				=     "incoming";
	public static final String       TEMP_DIR        				=     "temp";
	public static final String       LOGS_DIR        				=     "logs";
	public static final String		 SYSTEM_PROPERTIES_DUMP			=     LOGS_DIR + File.separator + "dump.properties";
	public static final String       SETTINGS_DIR    				=     "settings";
	public static final String       CONFIG_FILE     				=     SETTINGS_DIR + File.separator + "jmule.properties";
	public static final String       KNOWN_MET         				=     SETTINGS_DIR + File.separator + "known.met";
	public static final String       SERVER_MET         			=     SETTINGS_DIR + File.separator + "server.met";
	public static final String       GEOIP_DAT                      =     SETTINGS_DIR + File.separator + "geoip.dat";
	
	public static final String       NICK_NAME       				=     "http://jmule.net";
	
	public static final int          TCP_PORT        				=     4662;
	public static final int          UDP_PORT        				=     4662;
	public static final boolean      UDP_ENABLED                    =     true;
	public static final int          LOG_FILES_NUMBER   			=     10;
	public static final int          LOG_FILE_SIZE      			=     2 * 1024 * 1024;
	public static final int          SOURCES_QUERY_INTERVAL     	=     1000 * 60 * 5;
	public static final int          PEER_ACTIVITY_CHECH_INTERVAL   =     1000 * 30;
	public static final int          PEER_INACTIVITY_REMOVE_TIME    =     20 * 1000;
	public static final int          SPEED_CHECK_INTERVAL           =     1000;
	public static final int          UPLOAD_QUEUE_SIZE              =     200;
	public static final int          SERVER_UDP_QUERY_INTERVAL      =     1000 * 3;
	public static final int 		 MAX_PACKET_SIZE				= 	  1024*500;
	// the network
	public static final long          DOWNLOAD_BANDWIDTH            =     1024 * 10 * 256;
	public static final long          UPLOAD_BANDWIDTH              =     1024 * 10 * 256;
	public static final int           MAX_CONNECTIONS               =    500;
	public static final long          DOWNLOAD_LIMIT     			=    1024 * 1024;
	public static final long          UPLOAD_LIMIT       			=    1024 * 512;
	
	public static final long		  MAX_UDP_PACKET_SIZE			=    100*1024;
	
	public static final long          WRONG_PACKET_CHECK_INTERVAL	=     1000 * 5;
	public static final long		  MAX_WRONG_PACKET_COUNT		=     50 ;
	// data base keys
	public static final String       NICK_NAME_KEY                         =     "NickName";
	public static final String       TCP_PORT_KEY                          =     "TCPPort";
	public static final String       UDP_PORT_KEY                   	   =     "UDPPort";
	public static final String       UDP_ENABLED_KEY                       =     "UDPEnabled";
	public static final String       USER_HASH_KEY                         =     "UserHash";
	public static final String       DOWNLOAD_LIMIT_KEY                    =     "DownloadLimit";
	public static final String       UPLOAD_LIMIT_KEY               	   =     "UploadLimit";
	public static final String       DOWNLOAD_BANDWIDTH_KEY                =     "DownloadBandwidth";
	public static final String       UPLOAD_BANDWIDTH_KEY                  =     "UploadBandwidth";
	public static final String       SHARED_DIRECTORIES_KEY                =     "SharedDirectories";
	
	public static final String		 SERVER_LIST_UPDATE_ON_CONNECT_KEY	   =     "ServerListUpdateOnConnect";
	
	public static final String 		 CUSTOM_PARAMETER_KEY					   =     "CustomParameter";
	// 
	//public static final String       PEER_ACTIVITY_CHECK_TIME_KEY   	   =     "PeerActivityCheckTime";
	//public static final String       SOURCES_QUERY_INTERVAL_KEY     	   =     "SourcesQueryInterval";
	//public static final String       PEER_INACTIVITY_REMOVE_TIME_KEY       =     "PeerInactivityRemoveTime";
	
	
	/**
	 * Loads the default configuration
	 */
	public void loadDefault();
	
	/**
	 * Loads the configuration from the repository
	 */
	public void load();
	
	/**
	 * Save the configuration to the repository
	 */
	public void save();
	
	/**
	 * Sets the nick name that is used in the client
	 * @param nickName the given nick name
	 */
	public void setNickName(String nickName);
	
	/**
	 * Sets the tcp port to the given value
	 * @param tcp the given value of the tcp port
	 */
	public void setTCP(int tcp);
	
	public void setTCP(String tcp);
	
	/**
	 * Sets the udp port to the given value
	 * @param udp the given value of the udp port
	 */
	public void setUDP(int udp);
	
	public void setUDP(String udp);
	
	/**
	 * Sets the list of shared folders 
	 * @param sharedFolders the given list of folders
	 */
	public void setSharedFolders(List<File> sharedFolders);
	
	/**
	 * 
	 * @return the list of shared folders
	 */
	public List<File> getSharedFolders();
	
	/**
	 * Sets the download limit
	 * @param downloadLimit the given download limit
	 */
	public void setDownloadLimit(long downloadLimit);
	
	/**
	 * Sets the upload limit
	 * @param uploadLimit the given upload limit
	 */
	public void setUploadLimit(long uploadLimit);
	
	/**
	 * Sets the download bandwidth for the connection that the client will use
	 * @param downloadBandwidth
	 */
	public void setDownloadBandwidth(long downloadBandwidth);
	
	/**
	 * Sets the upload bandwidth for the connection that the client will use
	 * @param downloadBandwidth
	 */
	public void setUploadBandwidth(long downloadBandwidth);
	
	/**
	 * @return the nick name
	 */
	public String getNickName();
	
	/**
	 * 
	 * @return the tcp port
	 */
	public int getTCP();
	
	/**
	 * 
	 * @return the udp port
	 */
	public int getUDP();
	
	/**
	 * Sets the status of the UDP port 
	 * @param enabled
	 */
	public void setUDPEnabled(boolean enabled);
	
	/**
	 * Tells if the UDP port is enabled
	 * @return true if the UDP port is enabled, false otherwise
	 */
	public boolean isUDPEnabled();
    
    /**
     * 
     * @return download limit
     */
    public long getDownloadLimit();
    
    /**
     * 
     * @return upload limit
     */
    public long getUploadLimit();
    
    /**
     * 
     * @return user hash
     */
    public UserHash getUserHash();
    /**
     * 
     * @return download bandwidth
     */
    public long getDownloadBandwidth();
    
    /**
     * 
     * @return upload bandwidth
     */
    public long getUploadBandwidth();
    
    
    public int getIntParameter(String parameter, int defaultValue);
    
    public String getStringParameter(String parameter, String defaultValue);
    
    public float getFloatParameter(String parameter, float defaultValue);
    
    public double getDoubleParameter(String parameter, double defaultValue);
    
    public long getLongParameter(String parameter, long defaultValue);
    
    public boolean getBooleanParameter(String parameter, boolean defaultValue);
    
    public void setParameter(String parameter, int value);
    
    public void setParameter(String parameter,String value);
    
    public void setParameter(String parameter,float value);
    
    public void setParameter(String parameter,double value);
    
    public void setParameter(String parameter,long value);
    
    public void setParameter(String parameter,boolean value);
    
	/**
	 * Adds a configuration listener
	 * @param listener the given configuration listener
	 */
	public void addConfigurationListener(ConfigurationListener listener);
	
	public void addConfigurationListener(ConfigurationListener listener, String parameter);
	
	/**
	 * Removes a configuration listener
	 * @param listener the given configuration listener
	 */
	public void removeConfigurationListener(ConfigurationListener listener);
	
}
