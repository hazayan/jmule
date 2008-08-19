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
package org.jmule.ui;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * 
 * @author javajox
 * @author binary
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/19 19:45:37 $$
 */
public class UIConstants {
	
	// Columns ID
	public static final int SERVER_NAME_ID          =   100;
	public static final int SERVER_IP_ID            =   200;
	public static final int SERVER_DESCRIPTION_ID   =   300;
	public static final int SERVER_PING_ID          =   400;
	public static final int SERVER_USERS_ID         =   500;
	public static final int SERVER_MAX_USERS_ID     =   600;
	public static final int SERVER_FILES_ID         =   700;
	public static final int SERVER_SOFT_LIMIT_ID    =   800;
	public static final int SERVER_HARD_LIMIT_ID    =   900;
	public static final int SERVER_VERSION_ID   	=   1000;
	
	private static Map<String,String> icon_name_by_extension = new Hashtable<String,String>();

	static {
		icon_name_by_extension.put("rar","mimetypes/archive.png");
		icon_name_by_extension.put("zip","mimetypes/archive.png");
		icon_name_by_extension.put("tar","mimetypes/archive.png");
		icon_name_by_extension.put("gz","mimetypes/archive.png");
		icon_name_by_extension.put("7z","mimetypes/archive.png");
		
		icon_name_by_extension.put("mp3","mimetypes/audio.png");
		icon_name_by_extension.put("wav","mimetypes/audio.png");
		icon_name_by_extension.put("midi","mimetypes/audio.png");
		icon_name_by_extension.put("midi","mimetypes/audio.png");
		
		icon_name_by_extension.put("iso","mimetypes/cd_image.png");
		icon_name_by_extension.put("nrg","mimetypes/cd_image.png");
		icon_name_by_extension.put("cue","mimetypes/cd_image.png");
		
		icon_name_by_extension.put("png","mimetypes/image.png");
		icon_name_by_extension.put("jpg","mimetypes/image.png");
		icon_name_by_extension.put("jpeg","mimetypes/image.png");
		icon_name_by_extension.put("gif","mimetypes/image.png");
		icon_name_by_extension.put("bmp","mimetypes/image.png");
		icon_name_by_extension.put("ico","mimetypes/image.png");
		
		icon_name_by_extension.put("pdf","mimetypes/pdf.png");
		
		icon_name_by_extension.put("doc","mimetypes/doc.png");
		icon_name_by_extension.put("wri","mimetypes/doc.png");
		icon_name_by_extension.put("odt","mimetypes/doc.png");
		icon_name_by_extension.put("sxw","mimetypes/doc.png");
		icon_name_by_extension.put("vor","mimetypes/doc.png");
		
		icon_name_by_extension.put("xls","mimetypes/calc.png");
		
		icon_name_by_extension.put("exe", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("com", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("bat", "mimetypes/windows_exe.png");
		icon_name_by_extension.put("cmd", "mimetypes/windows_exe.png");
		
		icon_name_by_extension.put("so", "mimetypes/executable.png");
		icon_name_by_extension.put("bin", "mimetypes/executable.png");
		icon_name_by_extension.put("sh", "mimetypes/executable.png");
		
		icon_name_by_extension.put("mpg", "mimetypes/video.png");
		icon_name_by_extension.put("mpeg", "mimetypes/video.png");
		icon_name_by_extension.put("avi", "mimetypes/video.png");
		icon_name_by_extension.put("wmv", "mimetypes/video.png");
		icon_name_by_extension.put("bik", "mimetypes/video.png");
		icon_name_by_extension.put("mov", "mimetypes/video.png");
	}
	
	  public static final String INFINITY_STRING	= "\u221E"; // "oo";
	  public static final int    INFINITY_AS_INT = 31536000; // seconds (365days)
	
	public static InputStream getIconByExtension(String extension) {
		extension = extension.toLowerCase();
		String image_path = icon_name_by_extension.get(extension);
		if (image_path == null)
			image_path = "mimetypes/default.png";
		return UIImageRepository.getImageAsStream(image_path);
	}
	
}
