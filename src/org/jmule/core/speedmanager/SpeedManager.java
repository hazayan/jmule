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
package org.jmule.core.speedmanager;

import org.jmule.core.JMuleManager;
import org.jmule.core.configmanager.ConfigurationManagerFactory;

/**
 *
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:49 $$
 */
public class SpeedManager implements JMuleManager {
	
	private static SpeedManager speedManager = null;
	
	private BandwidthController uploadController;
	
	private BandwidthController downloadController;
	
	public static SpeedManager getInstance() {
		
		if (speedManager == null)
			
			speedManager = new SpeedManager();
		
		return speedManager;
	}

	private SpeedManager() {
		
		long uploadLimit = ConfigurationManagerFactory.getInstance().getUploadLimit();
		
		uploadController = BandwidthController.acquireBandwidthController("Upload bandwidth controller", uploadLimit);
		
		long downloadLimit = ConfigurationManagerFactory.getInstance().getDownloadLimit();
		
		downloadController = BandwidthController.acquireBandwidthController("Download bandwidth controller", downloadLimit);
		
	}
	
	public void shutdown() {
	}
	
	public void setUploadLimit(long newLimit){
		
		uploadController.setThrottlingRate(newLimit);
		
	}
	
	public void sedDownloadLimit(long newLimit){
		
		downloadController.setThrottlingRate(newLimit);
		
	}

	public BandwidthController getUploadController() {
		
		return uploadController;
		
	}

	public BandwidthController getDownloadController() {
		
		return downloadController;
		
	}

	public void initialize() {
	}

	public void start() {
	}

	
}
