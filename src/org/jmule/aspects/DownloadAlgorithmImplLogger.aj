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

import org.jmule.core.downloadmanager.FilePartStatus;
import org.jmule.core.downloadmanager.FileRequestList;
import org.jmule.core.downloadmanager.strategy.DownloadStrategyImpl;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:25 $$
 */
public aspect DownloadAlgorithmImplLogger {
	private Logger log = Logger.getLogger("org.jmule.core.downloadmanager.algorithm.DownloadAlgorithmImpl");
	
	before(): call(DownloadStrategyImpl.new()) {
	}
	
	after() returning(FileChunkRequest req) : execution(FileChunkRequest DownloadStrategyImpl.fileChunkRequest(Peer,long,long,GapList, FilePartStatus,FileRequestList)) {
		if (req!=null)
			log.info(" Algorithm :: Request  :"+req.getChunkBegin()+" "+req.getChunkEnd());
	}
}
