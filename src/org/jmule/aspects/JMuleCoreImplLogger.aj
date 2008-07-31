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

import java.util.logging.Logger;

import org.jmule.core.downloadmanager.*;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.impl.JMuleCoreImpl;
import org.jmule.core.peermanager.*;
import org.jmule.core.uploadmanager.*;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:27 $$
 */
public privileged aspect JMuleCoreImplLogger {
	private Logger log = Logger.getLogger("org.jmule");
	
	before() : call( JMuleCoreImpl.new()) {
		//	log=Logger.getLogger("org.jmule.core.impl.JMuleCoreImpl");
	}
	
	before() : execution(void JMuleCoreImpl.start()) {
	
	};
	
	after() : execution(void JMuleCoreImpl.start()) {
		log.info("Core started ");
	}
	
	before(String event) : args(event) && (call(void JMuleCoreImpl.logEvent(String))) {
		
		String downloadmanager = "Downloads : \n";
		
		for(DownloadSession download_session : DownloadManagerFactory.getInstance().getDownloads()) {
			
			downloadmanager += download_session + "\n";

		}
		
		String peermanager = "Peer Manager : \n";
		
		for (Peer peer : PeerManagerFactory.getInstance().getPeers()) {
			
			peermanager += peer + " \n";
		}
		
		peermanager += "Unknown peers : \n";
		
		for(Peer peer : PeerManagerFactory.getInstance().getUnknownPeers())
			
			peermanager += peer + "\n";
		
		String uploadmanager = "Upload Manager :\n";
		
		for(UploadSession upload_session : UploadManagerFactory.getInstance().getUploads()) 
			
			uploadmanager +=upload_session+"\n";
		
		log.fine("\nEvent : "+event+"\n"+
				peermanager+"\n"+
				downloadmanager+"\n"+
				uploadmanager+"\n");
//		log.info("\nEvent : "+event+"\nDebug data :\n"+
//				"Connection Listener : \n"+JMConnectionWaiter.getInstance()+"\n");
//				"Peer Listener : \nSize : "+OldPeerManager.getInstance().size()+"\n"+PeerManagerFactory.getInstance()+"\n" +
//				"Share Manager :\n"+SharingManagerFactory.getInstance()+"\n"+
//				"Download Manager :\n"+OldDownloadManager.getInstance()+"\n"+
//				"Upload Manager : \n"+UploadManager.getInstance());
	}
	

}
