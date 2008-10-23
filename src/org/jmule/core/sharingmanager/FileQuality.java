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
package org.jmule.core.sharingmanager;

import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_EXCELENT;
import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_FAIR;
import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_FAKE;
import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_GOOD;
import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_NOTRATED;
import static org.jmule.core.edonkey.E2DKConstants.FILE_QUALITY_POOR;

/**
 * Created on Oct 23, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/23 17:09:51 $
 */
public enum FileQuality { 
	NOTRATED { int getAsInt() { return FILE_QUALITY_NOTRATED; } },
	FAKE 	 { int getAsInt() { return FILE_QUALITY_FAKE; } }, 
	POOR 	 { int getAsInt() { return FILE_QUALITY_POOR; } }, 
	FAIR 	 { int getAsInt() { return FILE_QUALITY_FAIR; } }, 
	GOOD 	 { int getAsInt() { return FILE_QUALITY_GOOD; } }, 
	EXCELENT { int getAsInt() { return FILE_QUALITY_EXCELENT; } };
	
	/**
	 * Convert int value to FileQuality
	 * @param value
	 * @return
	 */
	public static FileQuality getAsFileQuality(int value) {
		switch(value) {
			case FILE_QUALITY_FAKE : 	return FAKE; 
			case FILE_QUALITY_POOR : 	return POOR;
			case FILE_QUALITY_FAIR : 	return FAIR;
			case FILE_QUALITY_GOOD : 	return GOOD;
			case FILE_QUALITY_EXCELENT : return EXCELENT;
		}
		return NOTRATED;
	}
	
	/**
	 * Convert enum value into int
	 * @return int value of enum
	 */
	abstract int getAsInt();
};
