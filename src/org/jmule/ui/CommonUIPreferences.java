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
package org.jmule.ui;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:56 $$
 */
public class CommonUIPreferences implements UIPreferences {

	private Preferences preferences;
	private static CommonUIPreferences singleton = null;
	
	public static CommonUIPreferences getSingleton() {
		if( singleton == null ) singleton = new CommonUIPreferences();
		return singleton;
	}
	
	private CommonUIPreferences() {
		preferences = Preferences.systemRoot();
		try {
			
		   if(!preferences.nodeExists(UI_ROOT)) {

			    //preferences.node(UI_ROOT).get(UI_TYPE, JMuleUIManager.DEFAULT_UI);
			  
		   }
		} catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the user interface stored in repository
	 * @return the jmule user interface
	 */
	public String getUIType() {
		
		 return preferences.node(UI_ROOT).
		          get(UI_TYPE, JMuleUIManager.DEFAULT_UI);
	}
	
	/**
	 * Sets the user interface
	 * @param type the jmule user interface
	 */
	public void setUIType(String type) {
		
		 preferences.node(UI_ROOT).
		               put(UI_TYPE, type);
	}
	
	public void save() {
		
	  try {	
		
	 	preferences.flush();
	 	
	  }catch(Throwable t) {
		  
		  t.printStackTrace();
		  
	  }
		
	}
	
}
