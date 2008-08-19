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
import java.net.InetAddress;

import org.jmule.countrylocator.CountryLocator;

/**
 * Created on Aug 12, 2008
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/08/19 05:45:01 $
 */
public class UICommon {

	private static String unknown_country = "unknown";
	private static String flag_file_ext = ".png";
	
	private static InputStream getFlag(String countrycode) {
		InputStream input_stream;
		
		if (countrycode==null) {
			countrycode = unknown_country;
		}
		
		if (countrycode.equals(CountryLocator.COUNTRY_CODE_NOT_AVAILABLE)) {
			countrycode = unknown_country;
		}
		
		countrycode = countrycode.toLowerCase()+flag_file_ext;
		input_stream = (UICommon.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/flags/" + countrycode));
		
		if (input_stream==null) {
			countrycode = unknown_country+flag_file_ext;
			input_stream = (UICommon.class.getClassLoader().getResourceAsStream("org/jmule/ui/resources/flags/" + countrycode));
		}
		return input_stream;
	}
	
	public static InputStream getCountryFlag(InetAddress inetAddress) {
		String country_code = CountryLocator.getInstance().getCountryCode(inetAddress);
		return getFlag(country_code);
	}
	
	public static InputStream getCountryFlag(long inetAddress) {
		String country_code = CountryLocator.getInstance().getCountryCode(inetAddress);
		return getFlag(country_code);
	}
	
	public static InputStream getCountryFlag(String inetAddress) {
		String country_code = CountryLocator.getInstance().getCountryCode(inetAddress);
		return getFlag(country_code);
	}
	
}
