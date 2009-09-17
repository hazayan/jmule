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
package org.jmule.core.peermanager;

import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_CLIENTVER;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_MISC_OPTIONS1;
import static org.jmule.core.edonkey.E2DKConstants.TAG_NAME_NICKNAME;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import javax.management.JMException;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.E2DKConstants;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.E2DKConstants.PeerFeatures;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.utils.Utils;
import org.jmule.core.utils.Convert;
import org.jmule.core.utils.Misc;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary255 $$ on $$Date: 2009/09/17 18:17:04 $$
 */
public class Peer {
	public enum PeerSource {SERVER, KAD}
	public enum PeerStatus {DISCONNECTED, CONNECTED, CONNECTING}
	
	private String ip;
	private int port;
	
	private String server_ip;
	private int server_port;
	
	private ClientID clientID = null;
	private UserHash userHash = null;
	private TagList tag_list = null;
	
	private Map<PeerFeatures,Integer> peer_features = new Hashtable<PeerFeatures, Integer>();
	private PeerSource peerSource = PeerSource.SERVER;
	
	private PeerStatus peer_status = PeerStatus.DISCONNECTED;
	
	Peer(String ip, int port,PeerSource peerSource) {
		this.ip = ip;
		this.clientID = new ClientID(ip);
		this.port = port;
		this.peerSource = peerSource;
	}
	
	void setPeerStatus(PeerStatus newStatus) {
		this.peer_status = newStatus;
	}
	
	void setServer(String serverIP, int serverPort) {
		this.server_ip = serverIP;
		this.server_port = serverPort;
	}
	
	void setClientID(ClientID clientID) {
		this.clientID = clientID;
	}
	
	void setUserHash(UserHash userHash) {
		this.userHash = userHash;
	}
	
	void setTagList(TagList tagList) {
		this.tag_list = tagList;

		if (tag_list.hasTag(TAG_NAME_MISC_OPTIONS1)) {
			Tag tag = tag_list.getTag(TAG_NAME_MISC_OPTIONS1);
			try {
				long value = Misc.extractNumberTag(tag);
				peer_features = Utils.scanTCPPeerFeatures(Convert
						.longToInt(value));
			} catch (JMException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean isConnected() {
		return getStatus() == PeerStatus.CONNECTED;
	}
	
	public PeerStatus getStatus() {
		return peer_status;
	}
	
	public Map<PeerFeatures, Integer> getPeerFeatures() {
		return peer_features;
	}
		
	public PeerSource getPeerSource() {
		return peerSource;
	}
	
	public String getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getServerIP() {
		return server_ip;
	}
	
	public int getServerPort() {
		return server_port;
	}
	
	public String getNickName() {
		Tag tag = tag_list.getTag(TAG_NAME_NICKNAME);
		if (tag != null)
			return (String) tag.getValue();
		return getIP();

	}
		
	public ClientID getID() {
		return clientID;
	}
	
	public boolean isHighID() {
		return this.clientID.isHighID();
	}
	
	public PeerStatus getPeerStatus() {
		return peer_status;
	}
	
	public String toString() {
		return getIP() + ":" + getPort();
	}
	
	public int hashCode() {
		if (userHash == null)
			return getID().hashCode();
		return userHash.hashCode();
	}
	
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (!(object instanceof Peer))
			return false;
		Peer p = (Peer) object;
		if ((p.getUserHash() != null) && (userHash != null))
			return p.getUserHash().equals(getUserHash());
		ClientID id = p.getID();
		ClientID id2 = getID();
		if ((id == null) || (id2 == null))
			return (getIP() + ":" + getPort()).equals(p.getIP() + ":"
					+ p.getPort());
		byte[] b1 = id.getClientID();
		byte[] b2 = id2.getClientID();

		return Arrays.equals(b1, b2);
	}
	
	public int getClientSoftware() {
		int clientInfo;
		try {
			Tag tag = tag_list.getTag(TAG_NAME_CLIENTVER);
			long value = Misc.extractNumberTag(tag);
			clientInfo = Convert.longToInt(value);
		} catch (Throwable e) {
			e.printStackTrace();
			return E2DKConstants.SO_COMPAT_UNK;
		}
		return (clientInfo >> 24) & 0x000000ff;
	}

	/**
	 * Return client version
	 * @return array with software version 1.2.3.4 => [1,2,3,4]
	 */
	public int[] getVersion() {
		
		int clientInfo;
		try {
			long value = Misc.extractNumberTag(tag_list.getTag(TAG_NAME_CLIENTVER));
			clientInfo = Convert.longToInt(value);
		}catch(Throwable e) {
			return new int[] {0,0,0,0};
		}
		int[] result = new int[4];
		
		result[0] = (clientInfo >> 17) & 0x7F;
		result[1] = (clientInfo >> 10) & 0x7F;
		result[2] = (clientInfo >> 7) & 0x07;
		result[3] = clientInfo & 0x7F;
		return result;
	}
	
	public UserHash getUserHash() {
		return this.userHash;
	}

	public TagList getTagList() {
		return this.tag_list;
	}
	
	public float getDownloadSpeed() {
		return 0;
	}
	
	public float getUploadSpeed() {
		return 0;
	}
	
	public float getDownloadServiceSpeed() {
		return 0;
	}
	
	public float getUploadServiceSpeed() {
		return 0;
	}
	
}