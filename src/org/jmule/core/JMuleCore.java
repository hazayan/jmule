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
package org.jmule.core;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.net.JMConnectionWaiter;
import org.jmule.core.peermanager.PeerManager;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.core.speedmanager.SpeedManager;
import org.jmule.core.uploadmanager.UploadManager;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:22 $$
 */
public interface JMuleCore {

	/**
	 * Start the system
	 */
	public void start();
	
	/**
	 * Stop the system
	 */
	public void stop() throws JMuleCoreException;
	
	/**
	 * Tells if the system is started
	 * @return true if the system is started, false otherwise
	 */
	public boolean isStarted();
	
	public JMConnectionWaiter getTCPConnectionListener();
	
	public DownloadManager getDownloadManager();
	
	public UploadManager getUploadManager();
	
	public PeerManager getPeerManager();
	
	public ServerManager getServersManager();
	
	public SharingManager getSharingManager();
	
	public SpeedManager getSpeedManager();
	
	public ConfigurationManager getConfigurationManager();
	
	public boolean isFirstRun();
	
	/**
	 * Adds a new life cycle listener to the core
	 * @param lifeCycleListener the given life cycle listener
	 */
	public void addLifecycleListener(JMuleCoreLifecycleListener lifeCycleListener);
	
	/**
	 * Removes the given life cycle listener from the core
	 * @param lifeCycleListener the given life cycle listener
	 */
	public void removeLifecycleListener(JMuleCoreLifecycleListener lifeCycleListener);
}
