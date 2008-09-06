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
package org.jmule.ui.utils;

import java.util.Hashtable;
import java.util.Map;

import org.jmule.core.downloadmanager.PeerDownloadStatus;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;

/**
 * Created on Aug 9, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/06 14:44:56 $
 */
public class PeerInfoFormatter {

	private static final String SO_EMULE			=  "eMule";
	private static final String SO_LMULE			=  "lMule";
	private static final String SO_AMULE			=  "aMule";
	private static final String SO_SHAREAZA   		=  "Shareaza";
	private static final String SO_EMULE_PLUS   	=  "eMule Plus";
	private static final String SO_HYDRANODE   		=  "Hydranode";
	private static final String SO_NEW2_MLDONKEY  	=  "MLDonkey";
	private static final String SO_LPHANT			=  "LPhant";
	private static final String SO_NEW2_SHAREAZA	=  "Shareaza";
	private static final String SO_EDONKEYHYBRID	=  "eDonkey Hybrid";
	private static final String SO_EDONKEY			=  "eDonkey";
	private static final String SO_MLDONKEY			=  "MLDonkey";
	private static final String SO_OLDEMULE			=  "Old eMule";
	private static final String SO_JMULE			=  "JMule";
	private static final String SO_NEW_MLDONKEY		=  "MLDonkey";
	private static final String SO_COMPAT_UNK		=  "eDonkey compatible(Unknown)";
	private static final String SO_UNKNOWN			=  "Unknown";
	
	
	private static Map<Integer,String> client_software = new Hashtable<Integer,String>();
	
	static {
		client_software.put(E2DKConstants.SO_EMULE, SO_EMULE);
		client_software.put(E2DKConstants.SO_LMULE, SO_LMULE);
		client_software.put(E2DKConstants.SO_AMULE, SO_AMULE);
		client_software.put(E2DKConstants.SO_SHAREAZA, SO_SHAREAZA);
		client_software.put(E2DKConstants.SO_EMULE_PLUS, SO_EMULE_PLUS);
		client_software.put(E2DKConstants.SO_HYDRANODE, SO_HYDRANODE);
		client_software.put(E2DKConstants.SO_NEW2_MLDONKEY, SO_NEW2_MLDONKEY);
		client_software.put(E2DKConstants.SO_LPHANT, SO_LPHANT);
		client_software.put(E2DKConstants.SO_NEW2_SHAREAZA, SO_NEW2_SHAREAZA);
		client_software.put(E2DKConstants.SO_EDONKEYHYBRID, SO_EDONKEYHYBRID);
		client_software.put(E2DKConstants.SO_EDONKEY, SO_EDONKEY);
		client_software.put(E2DKConstants.SO_MLDONKEY, SO_MLDONKEY);
		client_software.put(E2DKConstants.SO_OLDEMULE, SO_OLDEMULE);
		client_software.put(E2DKConstants.SO_JMULE, SO_JMULE);
		client_software.put(E2DKConstants.SO_NEW_MLDONKEY, SO_NEW_MLDONKEY);
		client_software.put(E2DKConstants.SO_COMPAT_UNK, SO_COMPAT_UNK);
	}
	
	public static String formatPeerSoftware(Peer peer) {
		int software = peer.getClientSoftware();
		String result = "";
		result = (String)client_software.get(software);
		if (result==null)
			result = SO_UNKNOWN;
		int v[] = peer.getVersion();
		String version = v[0]+"."+v[1]+"."+v[2]+"."+v[3];
		result += " " + version;
		return result;
	}
	
	public static String formatPeerStatus(PeerDownloadStatus status) {
		if (status == null) return _._("downloadinfowindow.tab.peerlist.column.status.connecting");
		switch (status.getPeerStatus()) {
			case PeerDownloadStatus.DISCONNECTED : 		return _._("downloadinfowindow.tab.peerlist.column.status.disconnected");  
			case PeerDownloadStatus.CONNECTED : 		return _._("downloadinfowindow.tab.peerlist.column.status.connected");
			case PeerDownloadStatus.SLOTREQUEST :		return _._("downloadinfowindow.tab.peerlist.column.status.slot_request");
			case PeerDownloadStatus.ACTIVE : 			return _._("downloadinfowindow.tab.peerlist.column.status.active");
			case PeerDownloadStatus.ACTIVE_UNUSED : 	return _._("downloadinfowindow.tab.peerlist.column.status.active_unued");
			case PeerDownloadStatus.IN_QUEUE : 			return Localizer.getString("downloadinfowindow.tab.peerlist.column.status.in_queue",status.getQueueRank()+"");
			case PeerDownloadStatus.INACTIVE : 			return _._("downloadinfowindow.tab.peerlist.column.status.inactive");
			case PeerDownloadStatus.UPLOAD_REQUEST :	return _._("downloadinfowindow.tab.peerlist.column.status.upload_request");
			case PeerDownloadStatus.HASHSET_REQUEST :	return _._("downloadinfowindow.tab.peerlist.column.status.hashset_request");
		}
		
		return "";
	}
	
}
