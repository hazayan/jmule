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

import java.awt.GridLayout;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jmule.countrylocator.CountryLocator;

/**
 *
 * Created on Oct 6, 2008
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/10/09 15:10:24 $
 */
public class FlagPack {

	private static String UNKNOWN_COUNTRY = "unknown";
	private static String FLAG_FILE_EXT = ".png";
	public static String FLAGS_FOLDER18x25 = "org/jmule/ui/resources/" + "flags18x12" + "/";
	public static String FLAGS_FOLDER25x15 = "org/jmule/ui/resources/" + "flags25x15" + "/";
	
	public enum FlagSize {
		S18x25, S25x15
	}
	
	public static URL getFlagAsURL(String countryCode, FlagSize flag_size) {
		 if(countryCode.compareTo(CountryLocator.COUNTRY_CODE_NOT_AVAILABLE) == 0)	 
		     return null;
		
	  URL image_url = null; 
		 
	  try {	
		  
		switch(flag_size) {
		   case  S18x25  : 	image_url = FlagPack.class.getClassLoader().getResource(
				                            FLAGS_FOLDER18x25 + 
                                            countryCode.toLowerCase() + 
                                            FLAG_FILE_EXT);
			               return  image_url;
			               
		   case S25x15   : image_url = FlagPack.class.getClassLoader().getResource(
				                           FLAGS_FOLDER25x15 + 
                                           countryCode.toLowerCase() + 
                                           FLAG_FILE_EXT);
			               return  image_url;
		}
	  } catch(Throwable t) {
		  
		  return null;
	  }
		return null;
	}
	
	public static Icon getFlagAsIcon(String countryCode, FlagSize flag_size) {
		
		Icon icon = null;
		
		try {
			icon = new ImageIcon(getFlagAsURL(countryCode, flag_size));
		}catch(Throwable t) {
			return null;
		}
		
        return icon; 
	}
	
	public static Icon getFlagAsIconByIP(Object inetAddress, FlagSize flagSize) {
 
	    String country_code = CountryLocator.getInstance().getCountryCode(inetAddress);

		return getFlagAsIcon(country_code, flagSize);
	}
	
	public static URL getFlagAsURLByIP(Object inetAddress, FlagSize flagSize) {
        return 	getFlagAsURL(CountryLocator.getInstance().getCountryCode(inetAddress), flagSize);	
	}
	
}
