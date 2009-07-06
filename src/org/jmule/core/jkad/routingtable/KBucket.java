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
package org.jmule.core.jkad.routingtable;

import static org.jmule.core.jkad.JKadConstants.K;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.jkad.Int128;



/**
 * Created on Dec 28, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class KBucket {
	
	private List<KadContact> contact_list = new CopyOnWriteArrayList<KadContact>();
	private static Random random = new Random();
	
	public KBucket() {
		
	}
	
	public void add(KadContact contact) { 
		contact_list.add(contact);
	}
	
	public void remove(KadContact contact) {
		contact_list.remove(contact);
	}
	
	public boolean hasContact(KadContact k) {
		for(KadContact c : contact_list)
			if (c.equals(k)) return true;
		return false;
	}
	
	public KadContact getContact(Int128 contactID) {
		for(KadContact contact : contact_list)
			if (contact.getContactID().equals(contactID)) return contact;
		
		return null;
	}
	
	
	public List<KadContact> getRandomContacts(int contactCount){
		List<KadContact> list = new LinkedList<KadContact>();
		
		if (contact_list.size()<=contactCount) {
			list.addAll(contact_list);
			return list;
		}
		for(int i = 0;i<contactCount; i++) {
			do {
				int r = random.nextInt(contact_list.size());
				KadContact contact = contact_list.get(r);
				if (list.contains(contact)) continue;
				list.add(contact);
				break;
			}while(true);
		}
		
		return list;
	}
	
	public void clear() {
		contact_list.clear();
	}
	
	public String toString() {
		String result = "";
		
		for(KadContact contact : contact_list)
			result += contact + "\n";
		
		return result;
	}
	
	public boolean isFull() {
		return contact_list.size() == K;
	}
	
	public int size() {
		return contact_list.size();
	}
	
	public List<KadContact> getContacts() {
		return contact_list;
	}

}
