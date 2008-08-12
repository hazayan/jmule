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
package org.jmule.countrylocator;

import java.io.IOException;
import java.net.InetAddress;

import org.jmule.core.configmanager.ConfigurationManager;

import com.maxmind.geoip.Country;
import com.maxmind.geoip.LookupService;

/**
 *
 * Created on Aug 12, 2008
 * @author javajox
 * @author binary256_
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/08/12 08:05:19 $
 */
public class CountryLocator {

	private static CountryLocator instance;
	
	private LookupService lookup_service;
	
	private boolean service_down = false;
	
	public static final String COUNTRY_NAME_NOT_AVAILABLE = "N/A";
	
	public static final String COUNTRY_CODE_NOT_AVAILABLE = "--";
	
	private CountryLocator() {
	
		try {
			
			lookup_service = new LookupService( ConfigurationManager.GEOIP_DAT, LookupService.GEOIP_MEMORY_CACHE );		
			
		} catch (IOException e) {
			
	        service_down = true;
			
			e.printStackTrace();
		}
		
	}
	
	public static CountryLocator getInstance() {
		
		if( instance == null ) {
			
			instance = new CountryLocator();
			
		}
		
		return instance;
	}
	
	public Country getCountry(InetAddress inetAddress) {
		
		return service_down ? null : lookup_service.getCountry( inetAddress );
	}
	
	public Country getCountry(long ipAddress) {
		
		return service_down ? null : lookup_service.getCountry( ipAddress );
	}
	
	public Country getCountry(String ipAddress) {
		
		return service_down ? null : lookup_service.getCountry( ipAddress );
	}
	
	public String getCountryName( InetAddress inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	public String getCountryName( long inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	public String getCountryName( String inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getName();
	}
	
	public String getCountryCode( InetAddress inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getCode();
	}
	
	public String getCountryCode( long inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getCode();
	}
	
	public String getCountryCode( String inetAddress ) {
		
		return service_down ? null : getCountry( inetAddress ).getCode();
	}
}
