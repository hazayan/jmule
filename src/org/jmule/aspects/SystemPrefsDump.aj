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
package org.jmule.aspects;

import java.io.FileOutputStream;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.impl.JMuleCoreImpl;
import org.jmule.main.Main;
/**
 * Created on Sep 11, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/11 15:39:32 $
 */ 
public aspect SystemPrefsDump {
 
	private boolean prefs_dumped = false;
	
	before() : execution(* JMuleCoreImpl.create()) {
		saveSystemPrefs();
	}
	
	before() : execution (void Main.main(..)) {
		saveSystemPrefs();
	}
	
	private void saveSystemPrefs() {
		if (prefs_dumped) return ;
		try {
			System.getProperties().store(new FileOutputStream(ConfigurationManager.SYSTEM_PROPERTIES_DUMP), "Dump of system properties");
			prefs_dumped = true;
		}catch(Throwable t) {
			System.err.println("Failed to dump system properties");
		}
	}
	
}
