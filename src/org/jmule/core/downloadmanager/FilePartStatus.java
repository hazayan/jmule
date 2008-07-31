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
package org.jmule.core.downloadmanager;

import java.util.Hashtable;

import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * Class manage file part(9.28 MB) availability for each peer 
 * and count total availability of the file in the network. 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:56 $$
 */
public class FilePartStatus extends Hashtable<Peer,JMuleBitSet> {
	private int partCount;
	private int partAvailability[];
		
	public FilePartStatus(int partCount) {
		if (partCount==0) partCount++;
		this.partCount = partCount;
		this.partAvailability = new int[partCount];
	}
	
	public boolean hasStatus(Peer peer) {
		return super.get(peer)!=null;
	}
	
	public String toString() {
		String result ="";
		
		for(int i = 0;i <this.keySet().size();i++) {
			Peer p = (Peer)this.keySet().toArray()[i];
			result += "{ ";
			result+= p.getAddress()+" : "+p.getPort()+" ";
			result+=" BitSet :  " +this.get(p);
			result +=" } ";
		}
		
		return result;
	}
	
	public synchronized void addPartStatus(Peer peer,JMuleBitSet partStatus) {
		try {
		if (this.hasStatus(peer)) {
			this.removePartStatus(peer);
		}
		super.put(peer, partStatus);
		this.UpdateTotalAvailability(partStatus, true);
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	public void removePartStatus(Peer peer){
		if (this.hasStatus(peer)) {
			JMuleBitSet bitSet = super.get(peer);
			super.remove(peer);
			this.UpdateTotalAvailability(bitSet, false);
		}
	}
	
	public int[] getPartAvailibility(){
		return this.partAvailability.clone();
	}
	
	private void UpdateTotalAvailability(JMuleBitSet bitSet,boolean add) {
		if (bitSet.getPartCount()!=this.partCount) {
			return ;
		}
		
		for(int i = 0;i<bitSet.getPartCount();i++) {
			if (bitSet.get(i)) 
				if (add) this.partAvailability[i]++;
				else this.partAvailability[i]--;
		}
	}

	public int getPartCount() {
		return partCount;
	}
}
