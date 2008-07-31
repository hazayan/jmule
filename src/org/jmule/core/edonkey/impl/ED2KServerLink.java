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
package org.jmule.core.edonkey.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmule.core.JMException;

/**
 * ED2K server link
 * ed2k://|server|IP|PORT|/
 * @author binary
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:34 $$
 */
public class ED2KServerLink {

	private String server_address = "";
	
	private int server_port = 0;
	
	public ED2KServerLink(String server_address,int server_port) {
		
		this.server_address = server_address;
		
		this.server_port = server_port;
		
	}
	
	public ED2KServerLink(String link) throws JMException {
		Pattern s;
		
		Matcher m;
		
		s = Pattern.compile("ed2k:\\/\\/\\|server\\|([0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+.[0-9]{1,3}+)\\|([0-65535]*)\\|\\/");
		
		m = s.matcher(link);
		
		if (m.matches()) {
			
			server_address = m.group(1);
			server_port = Integer.valueOf(m.group(2)).intValue();
		}else throw new JMException();
	}
	
	public String getStringLink() {
		return "ed2k://|server|" + server_address + "|" + server_port + "|/";
	}
	
	public String toString() {
		return "ed2k://|server|" + server_address + "|" + server_port + "|/";
	}
	
	public String getServerAddress() {
		return server_address;
	}

	public int getServerPort() {
		return server_port;
	}
	
	public static void main(String... args) {
		
		ED2KServerLink link;
		try {
			link = new ED2KServerLink("ed2k://|server|207.44.222.51|4242|/");
			System.out.println("Link : "+link.getServerAddress()+" : "+link.getServerPort());
		} catch (JMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
