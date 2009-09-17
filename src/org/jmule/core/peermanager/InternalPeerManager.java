/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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

import java.util.List;

import org.jmule.core.edonkey.ClientID;
import org.jmule.core.edonkey.UserHash;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.peermanager.Peer.PeerSource;

/**
 * Created on Aug 16, 2009
 * @author binary256
 * @author javajox
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/09/17 18:17:04 $
 */
public interface InternalPeerManager extends PeerManager {

	public Peer newIncomingPeer(String ip, int port) throws PeerManagerException;
	
	public void peerDisconnected(String ip, int port);
	
	public void peerConnectingFailed(String ip, int port, Throwable cause);
	
	public void helloFromPeer(String peerIP, 
							  int peerPort, 
							  UserHash userHash, 
							  ClientID clientID,  
							  int peerPacketPort, 
							  TagList tagList, 
							  String serverIP, 
							  int serverPort);
	
	public void helloAnswerFromPeer(String peerIP, 
			  int peerPort, 
			  UserHash userHash, 
			  ClientID clientID,  
			  int peerPacketPort, 
			  TagList tagList, 
			  String serverIP, 
			  int serverPort);
		
	public void callBackRequestFailed();
	
	public void receivedCallBackRequest(String ip, int port);
	
	public void receivedEMuleHelloFromPeer(String ip, int port,byte clientVersion, byte protocolVersion,
			TagList tagList);
	
	public List<Peer> createPeerList(List<ClientID> peerIDList,
			List<Integer> peerPortList, PeerSource peerSource);
	
}
