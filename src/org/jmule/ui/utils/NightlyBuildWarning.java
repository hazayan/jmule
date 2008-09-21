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
package org.jmule.ui.utils;

import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.core.configmanager.ConfigurationManager;

/**
 * Created on Sep 19, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/21 13:59:08 $
 */
public class NightlyBuildWarning {

	private static final String NIGHTLY_BUILD_VERSION		= "NightlyBuildVersion";
	private static final String SHOW_NIGHTLY_BUILD_WARNING	= "ShowNightlyBuildWarning";
	
	public static boolean showWarning() {
		ConfigurationManager manager = JMuleCoreFactory.getSingleton().getConfigurationManager();
		
		if (!manager.hasParameter(SHOW_NIGHTLY_BUILD_WARNING)) {
			manager.setParameter(SHOW_NIGHTLY_BUILD_WARNING, true);
			return true;
		}
		
		if (!manager.hasParameter(NIGHTLY_BUILD_VERSION)) {
			manager.setParameter(NIGHTLY_BUILD_VERSION, JMConstants.CURRENT_JMULE_VERSION);
			return true;
		}
		String jmule_ver = manager.getStringParameter(NIGHTLY_BUILD_VERSION, null);
		if (!jmule_ver.equals(JMConstants.CURRENT_JMULE_VERSION)) {
			manager.setParameter(NIGHTLY_BUILD_VERSION, JMConstants.CURRENT_JMULE_VERSION);
			return true;
		}
		
		return manager.getBooleanParameter(SHOW_NIGHTLY_BUILD_WARNING, true);
	}
	
	public static void setShowWarning(boolean newStatus) {
		ConfigurationManager manager = JMuleCoreFactory.getSingleton().getConfigurationManager();
		manager.setParameter(SHOW_NIGHTLY_BUILD_WARNING, newStatus);
	}
	
}
