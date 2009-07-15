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
package org.jmule.core.jkad.utils;

import static org.jmule.core.utils.Convert.byteToInt;

import java.util.List;
import java.util.Random;

import org.jmule.core.jkad.IPAddress;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.routingtable.KadContact;

/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/15 18:05:34 $
 */
public class Utils {
	
	private static Random random = new Random();

	public static byte[] getRandomInt128() {
		byte[] data = new byte[16];
		random.nextBytes(data);
		
		return data;
	}
	
	public static int getRandom(int range) {
		return random.nextInt(range);
	}

	/**
	 * Always filter following IP's : 
	 * 0.0.0.0							invalid.
	 * 127.0.0.0 - 127.255.255.255		Loopback.
     * 224.0.0.0 - 239.255.255.255		Multicast.
     * 240.0.0.0 - 255.255.255.255		Reserved for Future Use.
	 * 255.255.255.255					invalid.
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isGoodAddress(IPAddress address) {
		long value = byteToInt(address.getAddress());
		int s = byteToInt(address.getAddress()[3]);
		if ((s==0) || (s==127) || (s>=224)) return false;
		if (value<=60) return false;
		return true;
	}
	


	
	public static boolean inToleranceZone(Int128 target, Int128 source, Int128 toleranceZone) {
		long distance = Math.abs(target.toLong() - source.toLong());
		return distance < toleranceZone.toLong();
	}
	
	public static long XORDistance(Int128 a, Int128 b) {
		Int128 tmp = new Int128(a);
		tmp.XOR(b);
		return tmp.toLong();
	}
	
	public static KadContact getNearestContact(Int128 targetID, List<KadContact> contactList) {
		KadContact contact = null;
		long distance = -1;
		
		for(KadContact c : contactList) {
			if (distance == -1) {
				distance = Utils.XORDistance(targetID, c.getContactID());
				contact = c;
				continue;
			}
			
			long d2 = Utils.XORDistance(targetID, c.getContactID());
			if (d2 < distance) {
				distance = d2;
				contact = c;
			}
		}
		return contact;
	}
	
}
