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

import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTTAKEN;

import java.util.LinkedList;
import java.util.List;

import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.Packet;

/**
 * Created on 04-27-2008
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/01 18:54:44 $$
 */
public class PeerDownloadStatus {
	
	public static final int DISCONNECTED = 0x01;
	public static final int CONNECTED = 0x02;
	public static final int SLOTREQUEST = 0x03;
	public static final int ACTIVE = 0x04;
	public static final int ACTIVE_UNUSED = 0x05;
	public static final int IN_QUEUE = 0x06;
	public static final int INACTIVE = 0x07;
	public static final int UPLOAD_REQUEST = 0x08;
	public static final int HASHSET_REQUEST = 0x09;
	
	private Peer peer;
	
	private List<String> statusList = new LinkedList<String>();
	
	private long lastUpdateTime = 0;
	
	private int peerStatus;

	private int queue_rank = 0;
	
	private Packet lastFilePartRequest;

	private int resendCount = 0;
	
	public int getResendCount() {
		return resendCount;
	}

	public void setResendCount(int resendCount) {
		this.resendCount = resendCount;
	}
	
	public int getQueueRank() {
		return queue_rank;
	}

	public PeerDownloadStatus(Peer StatusPeer) {
		
		this.peer = StatusPeer;
		
		lastUpdateTime = System.currentTimeMillis();
		
		if (peer.isConnected())
			
			peerStatus = CONNECTED;
		
		else
			
			peerStatus = DISCONNECTED;
		
	}
	
	public String toString()  {
		
		String result = "" + peer + " : ";
		
		String status = "";
		
		switch(peerStatus) {
		
		case DISCONNECTED : { status = "DISCONNECTED"; break; }
		
		case CONNECTED : { status = "CONNECTED"; break; }
		
		case SLOTREQUEST : { status = "SLOTREQUEST"; break; }
		
		case ACTIVE : { status = "ACTIVE"; break; }
		
		case ACTIVE_UNUSED : { status = "ACTIVE UNUSED"; break; }
		
		case IN_QUEUE : { status = "IN QUEUE"; break; }
		
		case INACTIVE : { status = "INACTIVE"; break; }
		
		case UPLOAD_REQUEST : { status = "UPLOAD REQUEST"; break; }
		
		case HASHSET_REQUEST : { status = "HASHSET REQUEST"; break; }
		
		}
		
		result+= " Current status : "+status+" History : ";
		
		for(int i = 0;i<this.statusList.size();i++ ) {
			
			result += this.statusList.get(i) + " | ";
			
		}
		
		return result;

	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	public int getPeerStatus() {
		return peerStatus;
	}

	public void setPeerStatus(int peerStatus) {
		
		lastUpdateTime = System.currentTimeMillis();
		
		this.peerStatus = peerStatus;
	}
	
	public void updatePeerHistory(int peerStatus) {
		this.updatePeerHistory(peerStatus, -1);
	}
	
	
	public void updatePeerHistory(int peerStatus, int rank) {
		String result = "";
		if (rank!=-1)
			queue_rank = rank;
		lastUpdateTime = System.currentTimeMillis(); 
		
		switch(peerStatus) {
		
		case OP_FILEREQANSWER : {
			result = " OP_FILEREQANSWER ";
			break;
		}
		
		case OP_SLOTGIVEN : {
			result = " OP_ACCEPTUPLOADREQ ";
			break;
		}
		
		case OP_HASHSETANSWER : {
			result = " OP_HASHSETANSWER ";
			break;
		}
		
		case OP_SENDINGPART : {
			result = " OP_SENDINGPART ";
			break;
		}
		
		case OP_FILEREQANSNOFILE : {
			result = " OP_FILEREQANSNOFILE ";
			break;
		}
		
		case OP_SLOTTAKEN : {
			result = " OP_SLOTTAKEN ";
			break;
		}
		
		case OP_EMULE_QUEUERANKING : {
			result = " OP_EMULE_QUEUERANKING :  "+rank;
			break;
		}
		
					
		
		}
		this.statusList.add(result);
		
	}
	
	public Peer getPeer() {
		return peer;
	}
	
	public void setPeer(Peer peer) {
		this.peer = peer;
	}
	
	public Packet getLastFilePartRequest() {
		return lastFilePartRequest;
	}

	public void setLastFilePartRequest(Packet lastFilePartRequest) {
		this.lastFilePartRequest = lastFilePartRequest;
	}
	
}
