/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad;

import org.jmule.core.jkad.routingtable.KadContact;

/**
 * Maintain Kad buddy (jkad buddy or jkad is buddy)
 * Created on Jul 30, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/30 14:52:12 $
 */
public class Buddy {
	
	private KadContact contact = null;
	private boolean kadBuddy = false; // kad is firewalled and buddy is used
	private boolean kadIsBuddy = false; // kad is used by another contact as buddy
	
	private static Buddy instance = null;
	
	public static Buddy getInstance() {
		if (instance == null)
			instance = new Buddy();
		return instance;
	}
	
	private Buddy() {
	}
	
	public KadContact getContact() {
		return contact;
	}
	void setContact(KadContact contact) {
		this.contact = contact;
	}
	
	/**
	 * kad is firewalled and buddy is used
	 * @return
	 */
	public boolean jkadHasBuddy() {
		return kadBuddy;
	}
	
	/**
	 * kad is used by another contact as buddy
	 * @return
	 */
	public boolean isJKadUsedAsBuddy() {
		return kadIsBuddy;
	}
	
	public boolean hasAnyTypeBuddy() {
		return contact != null;
	}
	
	/**
	 * Drop buddy status of JKad
	 */
	void resetBuddyStatus() {
		contact = null;
		kadBuddy = kadIsBuddy = false;
	}
	
	
	

}
