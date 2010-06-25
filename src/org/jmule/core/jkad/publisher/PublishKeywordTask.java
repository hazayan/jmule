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
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadException;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.lookup.Lookup;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.packet.KadPacket;
import org.jmule.core.jkad.packet.PacketFactory;
import org.jmule.core.jkad.publisher.Publisher.PublishTaskListener;
import org.jmule.core.jkad.routingtable.KadContact;


/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.10 $
 * Last changed by $Author: binary255 $ on $Date: 2010/06/25 10:27:12 $
 */
public class PublishKeywordTask extends PublishTask {

	private LookupTask lookup_task;
	private Int128 keywordID;
	
	public PublishKeywordTask(PublishTaskListener listener,Int128 publishID,Int128 keywordID, List<Tag> tagList) {
		super(publishID,listener);
		this.keywordID = keywordID;
		this.tagList = new TagList(tagList);
	}
	
	public void start() throws JKadException {
		if (lookup_task!=null)
			if (lookup_task.isLookupStarted()) return;
		isStarted = true;
		lookup_task = new LookupTask(RequestType.STORE, publishID, JKadConstants.toleranceZone) {
				
			public void lookupTimeout() {
				isStarted = false;
				updatePublishTime();
				task_listener.taskTimeOut(task_instance);
			}

			public void processToleranceContacts(ContactAddress sender, List<KadContact> results) {
				for(KadContact contact : results) {
					KadPacket packet = null;
					if (!contact.supportKad2())
						packet = PacketFactory.getPublish1ReqPacket(targetID, _jkad_manager.getClientID(), tagList);
					else
						packet = PacketFactory.getPublishKeyReq2Packet(keywordID,targetID, tagList);
					_network_manager.sendKadPacket(packet, contact.getIPAddress(), contact.getUDPPort());
				}
			}
			
			public void stopLookupEvent() {
				isStarted = false;
				updatePublishTime();
				task_listener.taskStopped(task_instance);
			}
			
		};
		lookup_task.setTimeOut(JKadConstants.PUBLISHER_KEYWORD_PUBLISH_TIMEOUT);
		Lookup.getSingleton().addLookupTask(lookup_task);
		task_listener.taskStarted(task_instance);
	}

	public void stop() {
		isStarted = false;
		Lookup.getSingleton().removeLookupTask(publishID);
	}

}
