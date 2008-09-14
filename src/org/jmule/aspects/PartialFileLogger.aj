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

import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.sharingmanager.PartialFile;
import org.jmule.core.edonkey.metfile.PartMet;
import org.jmule.core.edonkey.metfile.PartMetException;
import org.jmule.util.Misc;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 12:00:00 $$
 */
public privileged aspect PartialFileLogger {
	private Logger log = Logger.getLogger("org.jmule.core.sharingmanager.PartialFile");
	
	after() throwing (Throwable t): execution (* PartMet.*(..)) {
		log.warning(Misc.getStackTrace(t));
	}
	
	after(PartMet part_met) throwing(PartMetException e) : target(part_met) && call (void PartMet.writeFile()) {
		log.info("Failed to write data in file "+part_met.getName() );
	}
	
	after(PartialFile pFile) : target(pFile) && execution(PartialFile.new(..)) {
		log.fine("New partial file : \n"+pFile);
	}
	
	after(PartialFile pFile) returning(boolean result) : target(pFile) && execution(boolean PartialFile.checkFullFileIntegrity()) {
		
		if (!pFile.hasHashSet()) {
			log.info("Can't check file integrity don't have part hash set");
			return ;
		}
		
		if (!result)
			log.warning("Broken file : \n"+pFile+"\n redownload");
	}
	
	after(PartialFile pFile) returning(boolean result) : target(pFile) && execution(boolean PartialFile.checkFilePartsIntegrity()) {
		if (!pFile.hasHashSet()) {
			log.info("Can't check file integrity don't have part hash set");
			return ;
		}
		if (!result)
			log.warning("Broken file : \n"+pFile+"\n mark for redownload bad pars\nGapList : "+pFile.getGapList());
	}
}
