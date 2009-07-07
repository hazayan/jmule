/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.search;

import java.util.LinkedList;
import java.util.List;

import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.indexer.Source;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.routingtable.KadContact;


/**
 * Created on Jan 16, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/07 18:30:01 $
 */
public class SourceSearchTask extends SearchTask {

	private LookupTask lookup_task = null;
	
	public SourceSearchTask(Int128 searchID) {
		super(searchID);
	}

	public void startSearch() {
		isStarted = true;
		
		Int128 toleranceZone = new Int128();
		toleranceZone.setBit(127, true);
		
		lookup_task = new LookupTask(RequestType.FIND_NODE, searchID, toleranceZone) {
			public void lookupTimeout() {
				isStarted = false;
			}

			public void processToleranceContacts(ContactAddress sender,
					List<KadContact> results) {
				List<Source> newSources = new LinkedList<Source>();
				for(KadContact contact : results) {
					Source source = new Source(contact.getContactID(), contact.getIPAddress(), contact.getUDPPort(), contact.getTCPPort());
					if(searchResults.contains(source)) continue;
					newSources.add(source);
					System.out.println("Source found : " + source);
				}
				if (newSources.size()!=0)
					addSearchResult(newSources);
			}
			
			public void stopLookup() {
				super.stopLookup();
				isStarted = false;
				stopSearch();
			}
			
		};
		
		Lookup.getSingleton().addLookupTask(lookup_task);
		if (listener!=null)
			listener.searchStarted();
	}

	public void stopSearch() {
		isStarted = true;
		if (listener!=null)
			listener.searchFinished();
		Lookup.getSingleton().removeLookupTask(searchID);
		
	}

}
