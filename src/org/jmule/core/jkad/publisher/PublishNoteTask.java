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
package org.jmule.core.jkad.publisher;

import java.util.List;

import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.jkad.ContactAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKad;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.net.packet.KadPacket;
import org.jmule.core.jkad.net.packet.PacketFactory;
import org.jmule.core.jkad.publisher.Publisher.PublishTaskListener;
import org.jmule.core.jkad.routingtable.KadContact;


/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.4 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/26 06:13:45 $
 */
public class PublishNoteTask extends PublishTask {

	private TagList tagList;
	private LookupTask lookup_task;
	
	public PublishNoteTask(PublishTaskListener listener, Int128 publishID, List<Tag> tagList) {
		super(publishID, listener);
		this.tagList = new TagList(tagList);
	}

	public void start() {
		if (lookup_task!=null)
			if (lookup_task.isLookupStarted()) return;
		
		Int128 toleranceZone = new Int128();
		toleranceZone.setBit(127, true);
		isStarted = true;
		
		lookup_task = new LookupTask(RequestType.STORE, publishID, toleranceZone) {
			public void lookupTimeout() {
				isStarted = false;
				updatePublishTime();
				task_listener.taskTimeOut(task_instance);
			}

			public void processToleranceContacts(ContactAddress sender,
					List<KadContact> results) {
				
				for(KadContact contact : results) {
					KadPacket packet = PacketFactory.getPublishNotesReq(targetID, JKad.getInstance().getClientID(), tagList);
					udpConnection.sendPacket(packet, contact.getIPAddress(), contact.getUDPPort());
				}
			}
			
			public void stopLookup() {
				super.stopLookup();
				updatePublishTime();
				task_listener.taskStopped(task_instance);
			}
			
		};
		lookup_task.setTimeOut(JKadConstants.PUBLISHER_NOTE_PUBLISH_TIMEOUT);
		Lookup.getSingleton().addLookupTask(lookup_task);
		task_listener.taskStarted(task_instance);
	}

	public void stop() {
		isStarted = false;
		Lookup.getSingleton().removeLookupTask(publishID);
	}

}
