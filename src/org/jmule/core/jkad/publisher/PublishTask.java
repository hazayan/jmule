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

import org.jmule.core.jkad.Int128;
import org.jmule.core.net.JMUDPConnection;


/**
 * Created on Jan 14, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public abstract class PublishTask {

	protected Int128 publishID;
	protected long lastpublishTime;
	protected boolean isStarted = false;
	protected JMUDPConnection udpConnection = null;
	protected long publishInterval = 1000 * 60 * 60 * 12;
	
	private int publishedSources = 0;
	
	public PublishTask(Int128 publishID) {
		super();
		this.publishID = publishID;
		udpConnection = JMUDPConnection.getInstance();
		lastpublishTime = 0;
	}

	public Int128 getPublishID() {
		return publishID;
	}

	public long getLastpublishTime() {
		return lastpublishTime;
	}
	
	protected void updatePublishTime() {
		lastpublishTime = System.currentTimeMillis();
	}
	
	public abstract void start();
	public abstract void stop();

	public boolean isStarted() {
		return isStarted;
	}

	public void addPublishedSources(int sources) {
		publishedSources+= sources;
	}
	
	public int getPublishedSources() {
		return publishedSources;
	}
	
	public long getPublishInterval() {
		return publishInterval;
	}
	
	
}
