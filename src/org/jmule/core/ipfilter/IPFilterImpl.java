/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.ipfilter;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.jmule.core.JMuleAbstractManager;
import org.jmule.core.JMuleManagerException;

/**
 * Created on Nov 4, 2009
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2010/01/07 20:54:20 $
 */
public class IPFilterImpl extends JMuleAbstractManager implements InternalIPFilter {

	private Set<BannedIP> banned_ips = new ConcurrentSkipListSet<BannedIP>();
	private Set<BannedIP> banned_servers = new ConcurrentSkipListSet<BannedIP>();
	private Set<BannedIPRange> banned_peer_ranges = new ConcurrentSkipListSet<BannedIPRange>();
	
	private Set<TemporaryBannedIP> temporary_banned_ips = new ConcurrentSkipListSet<TemporaryBannedIP>();
	private Set<TemporaryBannedIP> temporary_banned_servers = new ConcurrentSkipListSet<TemporaryBannedIP>();
	
	IPFilterImpl() {
		
	}
	
	public void addPeer(InetSocketAddress inetSocketAddress, 
			            BannedReason bannedReason) {
		
		addPeer(inetSocketAddress, bannedReason, 0, TimeUnit.INFINITY);
	}
	
	public void addServer(InetSocketAddress inetSocketAddress, 
			              BannedReason bannedReason) {
		
		addServer(inetSocketAddress, bannedReason, 0, TimeUnit.INFINITY);
	}
	
	public void addPeer(InetSocketAddress inetSocketAddress) {
		
		addPeer(inetSocketAddress, BannedReason.DEFAULT, 0, TimeUnit.INFINITY);
	}
	
	public void addServer(InetSocketAddress inetSocketAddress) {
		
		addServer(inetSocketAddress, BannedReason.DEFAULT, 0, TimeUnit.INFINITY);
	}
	
	public boolean isPeerBanned(InetSocketAddress inetSocketAddress) {
	    
		return false;
	}
	
	public boolean isServerBanned(InetSocketAddress inetSocketAddress) {
		
		return false;
	}
	
	
	public void addPeer(InetSocketAddress inetSocketAddress,
			BannedReason bannedReason, int howLong, TimeUnit timeUnit) {
		
	}

	public void addPeer(InetSocketAddress inetSocketAddress, int howLong,
			TimeUnit timeUnit) {
		
		addPeer(inetSocketAddress, BannedReason.DEFAULT, howLong, timeUnit);
	}

	
	public void addServer(InetSocketAddress inetSocketAddress,
			BannedReason bannedReason, int howLong, TimeUnit timeUnit) {
		
	}

	
	public void addServer(InetSocketAddress inetSocketAddress, int howLong,
			TimeUnit timeUnit) {
		
		addServer(inetSocketAddress, BannedReason.DEFAULT, howLong, timeUnit);
	}	
	
	public void addPeer(String address, BannedReason bannedReason, int howLong,
			TimeUnit timeUnit) {
		
	}

	public void addPeer(String address, BannedReason bannedReason) {
		
		addPeer(address, bannedReason, 0, TimeUnit.INFINITY);
	}

	public void addPeer(String address, int howLong, TimeUnit timeUnit) {
		
		addPeer(address, BannedReason.DEFAULT, howLong, timeUnit);
	}

	public void addPeer(String address) {
		
		addPeer(address, BannedReason.DEFAULT, 0, TimeUnit.INFINITY);
	}

	public void addServer(String address, BannedReason bannedReason,
			int howLong, TimeUnit timeUnit) {
		
	}

	public void addServer(String address, BannedReason bannedReason) {
	     
		addServer(address, bannedReason, 0, TimeUnit.INFINITY);
	}

	public void addServer(String address, int howLong, TimeUnit timeUnit) {
		
		addServer(address, BannedReason.DEFAULT, howLong, timeUnit);
	}

	public void addServer(String address) {
		
	}

	public boolean isPeerBanned(String address) {

		return false;
	}

	public boolean isServerBanned(String address) {

		return false;
	}

	public void unbanPeer(String address) {

		
	}

	public void unbanServer(String address) {

		
	}
	
	public void initialize() {
		try {
			super.initialize();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void shutdown() {
		try {
			super.shutdown();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
	}
	
	public void start() {
		try {
			super.start();
		} catch (JMuleManagerException e) {
			e.printStackTrace();
			return ;
		}
	}
	
	protected boolean iAmStoppable() {

		return false;
	}
	
}
