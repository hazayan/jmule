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
package org.jmule.ui.swt;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.jmule.ui.UIConstants;

/**
 * Created on Aug 12, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 16:39:00 $
 */
public class SWTImageRepository {

	public static Image getImage(String	name ) {
		InputStream input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/" + name));
		if ( input_stream == null )
			input_stream = (SWTImageRepository.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/image_not_found.png"));
		return new Image(SWTThread.getDisplay(),input_stream);
	}
	
	public static Image getIconByException(String fileName) {
		int id = fileName.length()-1;
		while(id>0) { 
			if (fileName.charAt(id)=='.') break;
			id--;
		}
		String extension = "no extension";
		if (id!=0)
			extension = fileName.substring(id+1, fileName.length());
		Program program = Program.findProgram(extension);
		if (program == null)
			return new Image(SWTThread.getDisplay(),UIConstants.getIconByExtension(extension) );
		
		ImageData image_data = program.getImageData();
		image_data = image_data.scaledTo(16, 16);
		Image image = new Image(SWTThread.getDisplay(),image_data);
		
		return image;
	}
	
}
