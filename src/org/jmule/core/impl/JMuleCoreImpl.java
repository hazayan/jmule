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
package org.jmule.core.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jmule.core.JMRawData;
import org.jmule.core.JMThread;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreComponent;
import org.jmule.core.JMuleCoreException;
import org.jmule.core.JMuleCoreLifecycleListener;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.downloadmanager.DownloadManagerFactory;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.jkad.JKad;
import org.jmule.core.net.JMConnectionWaiter;
import org.jmule.core.net.JMUDPConnection;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.peermanager.PeerManagerFactory;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchManagerFactory;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.sharingmanager.SharingManagerFactory;
import org.jmule.core.speedmanager.SpeedManager;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerFactory;

/**
 * Created on 2008-Apr-16
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.9 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/07/07 18:29:00 $$
 */
public class JMuleCoreImpl implements JMuleCore {
	
	private static JMuleCoreImpl instance = null;
	
	private DebugThread debugThread ;
	
	private JMConnectionWaiter connectionWaiter;
	
	private JMUDPConnection udp_connection;
	
	private List<JMuleCoreLifecycleListener> lifecycle_listeners = new LinkedList<JMuleCoreLifecycleListener>();
	
	// the first run flag is true when certain conditions have been met
	private boolean first_run = false; 
	
	private JMRawData core_params;
	
	private JMuleCoreImpl() {
		// when the JMule core is created we must exactly know if this is the first start up
	}
	
	private JMuleCoreImpl(JMRawData coreParams) {
		
		this.core_params = coreParams;
	}
	
	public static JMuleCore create() throws JMuleCoreException {
		
		if (instance != null)
			
			throw new JMuleCoreException("JMule core already instantiated");
		
			instance = new JMuleCoreImpl();
			
		return instance;
	}
	
	public static JMuleCore create(JMRawData coreParams) throws JMuleCoreException {
		
		if(instance != null)
			
			throw new JMuleCoreException("JMule core already instantiated");
		
		    instance = new JMuleCoreImpl(coreParams);
		    
		return instance;
	}
	
	public static JMuleCore getSingleton() throws JMuleCoreException {
		
		if (instance == null)
			
			throw new JMuleCoreException("JMule core is not instantiated");
		
		return instance;
	}
	
	
	public void start() throws JMuleCoreException {
		
		System.out.println("Core starting process initiated");
		
		long start_time = System.currentTimeMillis();
		
		File[] main_dirs = new File[4];
		
		File incoming_dir = new File( ConfigurationManager.INCOMING_DIR );
		
		main_dirs[0] = incoming_dir; 
		
		File temp_dir = new File( ConfigurationManager.TEMP_DIR );
		
		main_dirs[1] = temp_dir;
		
		File logs_dir = new File( ConfigurationManager.LOGS_DIR );
		
		main_dirs[2] = logs_dir;
		
		File settings_dir = new File( ConfigurationManager.SETTINGS_DIR );
		
		main_dirs[3] = settings_dir;
		
		for(File file : main_dirs) {

		    if( !file.exists()  ) {
				
		      try {
		    	
		    	  file.mkdir();
			   
		      }catch(Throwable cause) {
		    	  
		    	  throw new JMuleCoreException( cause );
		    	  
		      }
			      
		    }
			
		    if( !file.isDirectory() ) throw new JMuleCoreException("The file " + incoming_dir + " is not a directory");
		
		}
		
		File config_file = new File(ConfigurationManager.CONFIG_FILE);
		
		if( !config_file.exists() ) {
			
			first_run = true;
			
			try {
			
				config_file.createNewFile();
			
			} catch( Throwable cause ) {
				
				throw new JMuleCoreException( cause );
				
			}
			
		}
		
		ConfigurationManager configuration_manager = ConfigurationManagerFactory.getInstance();
		
		configuration_manager.initialize();
		
		configuration_manager.start();

		Logger log = Logger.getLogger("org.jmule");
		
		/**Setup logger*/
		
		log.setLevel(Level.ALL);//Log all events
		
		try {
			FileHandler fileHandler = new FileHandler(ConfigurationManager.LOGS_DIR+File.separator+"JMule%u.log",(int)ConfigurationManager.LOG_FILE_SIZE,ConfigurationManager.LOG_FILES_NUMBER);
			
			fileHandler.setFormatter(new SimpleFormatter());
			
			log.addHandler(fileHandler);
			
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
		
		// notifies that the config manager has been started
		notifyComponentStarted(configuration_manager);
		
		SharingManager sharingManager = SharingManagerFactory.getInstance();
		
		sharingManager.initialize();
		
		sharingManager.start();
		
		sharingManager.loadCompletedFiles();
		
		sharingManager.loadPartialFiles();
		
		// notifies that the sharing manager has been started
		notifyComponentStarted(sharingManager);
		
		UploadManagerFactory.getInstance().initialize();
		
		UploadManagerFactory.getInstance().start();
		
		// notifies that the upload manager has been started
		notifyComponentStarted(UploadManagerFactory.getInstance());
		
		SpeedManager.getInstance().initialize();
		
		SpeedManager.getInstance().start();
		
		// notifies that the speed manager has been started
		notifyComponentStarted(UploadManagerFactory.getInstance());
		
		PeerManagerFactory.getInstance().initialize();
		
		PeerManagerFactory.getInstance().start();
		
		// notifies that the peer manager has been started
		notifyComponentStarted(PeerManagerFactory.getInstance());
		
		
		connectionWaiter = JMConnectionWaiter.getInstance();
		
		connectionWaiter.start();
		
		udp_connection = JMUDPConnection.getInstance();
		if (configuration_manager.isUDPEnabled()) {
			try {
				
				udp_connection.open();
				
			} catch (Throwable t) {
				
				t.printStackTrace();
			}
		}
		
		
		DownloadManagerFactory.getInstance().initialize();
		// notifies that the download manager has been started
		notifyComponentStarted(DownloadManagerFactory.getInstance());
		
		ServerManager servers_manager = ServerManagerFactory.getInstance();
		
		servers_manager.initialize();
			
		try {
			
			servers_manager.loadServerList();
			
		} catch (Throwable t) {
			
			t.printStackTrace();
		} 
		
		// notifies that the download manager has been started
		notifyComponentStarted(servers_manager);
		
		servers_manager.startUDPQuery();
		
		SearchManager search_manager = SearchManagerFactory.getInstance();
		
		search_manager.initialize();
		
		notifyComponentStarted(search_manager);
		
		if  (configuration_manager.isJKadEnabled())
			JKad.getInstance().initialize();
		
		JKad.getInstance().connect();
		
		/** Enable Debug thread!**/	
		// debugThread = new DebugThread();
		
		Runtime.getRuntime().addShutdownHook( new JMThread("Shutdown Hook") {
			
		    public void JMRun() {
		    	
		    	try {
		    	
				   JMuleCoreImpl.this.stop();
				
		    	} catch(Throwable t) {
		    		
		    	}
				
		     }
		 });
		
		System.out.println("Total start up time = " + ( System.currentTimeMillis() - start_time ) );
	}
	
	public boolean isStarted() {
		
		return instance != null;
		
	}
	
	public boolean isFirstRun() {
		
		return first_run;
	}

	public void stop() throws JMuleCoreException {
		
		System.out.println("Core stopping process initiated");
		
		long stop_time = System.currentTimeMillis();
		
		logEvent("Stop jMule");
		
		connectionWaiter.stop();
		
		try {
			udp_connection.close();
		} catch (Throwable t) {

			t.printStackTrace();
			
		}
		
		JKad jkad = JKad.getInstance();

		jkad.disconnect();
		
		Server server = ServerManagerFactory.getInstance().getConnectedServer();
		
		if (server!=null)
			
			server.disconnect();
		
		SearchManager search_manager = SearchManagerFactory.getInstance();
		
		search_manager.shutdown();
		
		notifyComponentStopped(search_manager);
		
		ServerManagerFactory.getInstance().shutdown();
		
		// notifies that the server manager has been stopped
		notifyComponentStopped(ServerManagerFactory.getInstance());
		
		PeerManagerFactory.getInstance().shutdown();
		
		// notifies that the peer manager has been stopped
		notifyComponentStopped(PeerManagerFactory.getInstance());
		
		DownloadManagerFactory.getInstance().shutdown();
		
		// notifies that the download manager has been stopped
		notifyComponentStopped(DownloadManagerFactory.getInstance());
		
		UploadManagerFactory.getInstance().shutdown();
		
		// notifies that the upload manager has been stopped
		notifyComponentStopped(UploadManagerFactory.getInstance());
		
		SharingManagerFactory.getInstance().shutdown();
		
		// notifies that the sharing manager has been stopped
		notifyComponentStopped(SharingManagerFactory.getInstance());
		
		ConfigurationManagerFactory.getInstance().shutdown();
		
		notifyComponentStopped(ConfigurationManagerFactory.getInstance());
		
		if (debugThread != null)
			debugThread.JMStop();
		
		System.out.println("Total shutdown time = " + ( System.currentTimeMillis() - stop_time ) );
		
		System.exit( 0 );
	}
	
	
	public void logEvent(String event) {
		//Check aspect
	}
	
	public DownloadManager getDownloadManager() {
		
		return DownloadManagerFactory.getInstance();
		
	}
	
	public UploadManager getUploadManager() {
		
		return UploadManagerFactory.getInstance();
		
	}
	
	public ServerManager getServerManager() {
		
		return ServerManagerFactory.getInstance();
		
	}
	
	public PeerManager getPeerManager() {
		
		return PeerManagerFactory.getInstance();
	}
	
	public SharingManager getSharingManager() {
		
		return SharingManagerFactory.getInstance();
		
	}
	
	public SpeedManager getSpeedManager() {
		
		return SpeedManager.getInstance();
		
	}
	
	public JMConnectionWaiter getTCPConnectionListener() {
		
		return connectionWaiter;
		
	}
	
	public ConfigurationManager getConfigurationManager() {
		
		return ConfigurationManagerFactory.getInstance();
		
	}
	
	public SearchManager getSearchManager() {
		
		return SearchManagerFactory.getInstance();
		
	}

	private class DebugThread extends Thread {
		
		private boolean stop = false;
		
		public DebugThread() {
			
			super("JMule Debug thread");
			
			start();
			
		}
		
		public void run() {
		
			while(!stop){
			
				logEvent("Debug thread");
			
				try {
					Thread.sleep(9000);			
				} catch (InterruptedException e) {}
			}
		}
		
		public void JMStop() {
			stop = true;
			interrupt();
		}
		
	}
	
	private void notifyComponentStarted(JMuleCoreComponent manager) {
		
		for(JMuleCoreLifecycleListener listener : lifecycle_listeners) {
			
			 listener.componentStarted( manager );
		}
		
	}
	
	private void notifyComponentStopped(JMuleCoreComponent manager) {
		
		for(JMuleCoreLifecycleListener listener : lifecycle_listeners) {
			
			listener.componentStopped( manager );
		}
		
	}

	public void addLifecycleListener(
			JMuleCoreLifecycleListener lifeCycleListener) {
		
		lifecycle_listeners.add( lifeCycleListener );
	}

	public void removeLifecycleListener(
			JMuleCoreLifecycleListener lifeCycleListener) {
		
		lifecycle_listeners.remove( lifeCycleListener );
	}

	
}
