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
package org.jmule.core.net;

import static org.jmule.core.edonkey.E2DKConstants.OP_ANSWERSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.OP_EMULE_QUEUERANKING;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATREQ;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_GLOBSERVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_MESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLO;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLOANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_REQUESTPARTS;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVERLIST;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVER_DESC_ANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTGIVEN;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTRELEASE;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_SLOTTAKEN;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_CALLBACKFAILED;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVFOUNDSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVIDCHANGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVMESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSEARCHRESULT;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSTATUS;

import java.net.InetSocketAddress;
import java.util.LinkedList;

import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.UDPPacket;
import org.jmule.core.edonkey.packet.impl.EMuleCompressedPacket;
import org.jmule.core.edonkey.packet.impl.EMuleExtendedTCPPacket;
import org.jmule.core.edonkey.packet.impl.EMulePacketException;
import org.jmule.core.edonkey.packet.impl.PacketException;
import org.jmule.core.edonkey.packet.impl.StandardPacket;
import org.jmule.core.edonkey.packet.impl.UDPPacketException;
import org.jmule.core.edonkey.packet.impl.UDPServerPacket;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerAcceptUploadRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerChatMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileNotFoundSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerHelloSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerQueueRankingSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerRequestFilePartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSendingPartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotReleaseSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotTakenSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerCallbackFailed;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerFoundSourceSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerIDChangeSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerMessageSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerSearchResultSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerServerListSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerStatusSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPDescSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPNewDescSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMServerUDPStatusSP;
import org.jmule.core.edonkey.packet.tag.impl.TagList;
import org.jmule.core.sharingmanager.JMuleBitSet;

/**
 * 
 * @author javajox
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/08/20 16:45:17 $$
 */
public class PacketScanner {
	
	public static ScannedPacket scanPacket(Packet packet) throws PacketException,EMulePacketException {
		
		ScannedPacket scannedPacket = null;
		
		if (packet instanceof EMuleCompressedPacket ) {
			((EMuleCompressedPacket)packet).decompressPacket();
		}
		
		switch (packet.getCommand()) {
		/** Server <-> Peer **/
		case PACKET_SRVIDCHANGE: {
			StandardPacket sPacket = (StandardPacket)packet;
			scannedPacket = new JMServerIDChangeSP(sPacket.getIDChangeData());
				
			break;
		}
		
		case PACKET_SRVSTATUS : {
			StandardPacket sPacket = (StandardPacket)packet;
			scannedPacket = new JMServerStatusSP(sPacket.getNumFilesData(),sPacket.getNumUsersData());
			break;
		}
		
		case PACKET_SRVMESSAGE : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMServerMessageSP(sPacket.getServerMessageData());
			break;
		}
		
		case OP_SERVERLIST : {
			
			StandardPacket sPacket = (StandardPacket) packet;
			
			scannedPacket = new JMServerServerListSP();
			((LinkedList<Server>) scannedPacket).addAll(sPacket.getServerList());

			break;
		}
		
		case PACKET_SRVSEARCHRESULT: {
			StandardPacket sPacket = (StandardPacket) packet;
			
			JMServerSearchResultSP sResultPacket = new JMServerSearchResultSP(sPacket.getSearchResults());
			//sResultPacket.addAll();
			scannedPacket = sResultPacket;
			break;
		}
		
		case PACKET_SRVFOUNDSOURCES: {
			StandardPacket sPacket = (StandardPacket) packet;
			
			JMServerFoundSourceSP foundSrc = new JMServerFoundSourceSP(sPacket.getFileHashFoundSources());
			foundSrc.addAll(sPacket.getFoundSources());
			scannedPacket = foundSrc;
			break;
		}
		
		case PACKET_CALLBACKFAILED : {
			
			scannedPacket = new JMServerCallbackFailed();

		}
		
		/** Peer <-> Peer **/
		case OP_PEERHELLO : {
			StandardPacket sPacket = (StandardPacket) packet;
			UserHash userHash = sPacket.getUserHash();
			TagList tagList = sPacket.getPeerHelloTagList();
			ClientID  clientID = sPacket.getPeerClientIDPeerPackets();
			InetSocketAddress socketAddress = sPacket.getPeerHelloServerAddress();
			
			scannedPacket = new JMPeerHelloSP(userHash,tagList,clientID,socketAddress);
			break;
		}
		
		case OP_PEERHELLOANSWER : {
			StandardPacket sPacket = (StandardPacket) packet;

			UserHash userHash = sPacket.getUserHash();
			TagList tagList = sPacket.getPeerHelloAnswerTagList();
			ClientID  clientID = sPacket.getPeerClientIDPeerPackets();
			InetSocketAddress socketAddress = sPacket.getPeerHelloAnswerServerAddress();
			scannedPacket = new JMPeerHelloAnswerSP(userHash,tagList,clientID,socketAddress);
			break;
		}
		
		case OP_MESSAGE : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerChatMessageSP(sPacket.getPeerMessage());
			break;
		}
		
		/** Download Process **/
		case OP_FILEREQANSWER : {
			StandardPacket sPacket = (StandardPacket) packet;
			
			FileHash fileHash = sPacket.getFileHash();
			String fileName = sPacket.getFileNameRequestAnswer();
			scannedPacket = new JMPeerFileRequestAnswerSP(fileHash,fileName);
				
			break;
		}
		
		case OP_FILESTATUS : {
			StandardPacket sPacket = (StandardPacket) packet;
				
			FileHash fileHash = sPacket.getFileHash();
			JMuleBitSet bitSet = sPacket.getPartStatus();
			scannedPacket = new JMPeerFileStatusAnswerSP(fileHash, bitSet);
			break;
		}
	
		case OP_SLOTGIVEN : {
			scannedPacket = new JMPeerAcceptUploadRequestSP();
			break;
		}
		
		case OP_HASHSETANSWER : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerFileHashSetAnswerSP(sPacket.getPartHashSet());
			break;
		}
		
		
		case OP_SENDINGPART : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerSendingPartSP(sPacket.getFileHash(),sPacket.getFileChunk());
			break;
		}
		
		case  OP_FILEREQANSNOFILE : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerFileNotFoundSP(sPacket.getFileHash());
			break;
		}
		
		case OP_SLOTTAKEN :  {
			scannedPacket = new JMPeerSlotTakenSP();
			break;
		}
		
		case OP_EMULE_QUEUERANKING : {
			EMuleExtendedTCPPacket ePacket = (EMuleExtendedTCPPacket) packet;
			try {
				scannedPacket = new JMPeerQueueRankingSP(ePacket.getQueueRankingPosition());
			} catch (EMulePacketException e) {
				e.printStackTrace();
			}
			break;
		}
		
		case OP_ANSWERSOURCES : {
			System.out.println("OP_ANSWERSOURCES");
			break;
		}
		
		/** Upload **/
		
		case OP_FILEREQUEST : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerFileRequestSP(sPacket.getFileHash());
			break;
		}
		
		case OP_FILESTATREQ : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerFileStatusRequestSP(sPacket.getFileHash());
			break;
		}
		
		case OP_HASHSETREQUEST : {
			StandardPacket sPacket = (StandardPacket) packet;
			scannedPacket = new JMPeerFileHashSetRequestSP(sPacket.getFileHash());
			break;
		}
		
		case OP_SLOTREQUEST : {
			scannedPacket = new JMPeerSlotRequestSP();
			
			break;
		}
		
		case OP_SLOTRELEASE : {
			scannedPacket = new JMPeerSlotReleaseSP();
			break;
		}
		
		case OP_REQUESTPARTS : {
			StandardPacket sPacket = (StandardPacket) packet;
			JMPeerRequestFilePartSP pPacket = new JMPeerRequestFilePartSP(packet.getFileHash());
			pPacket.addAll(sPacket.getFileChunksRequest());
			scannedPacket = pPacket;

			break;
		}
		}
		
		return scannedPacket;

	}
	
	public static ScannedPacket scanPacket(UDPPacket packet) {
		ScannedPacket scannedPacket = null;
		
		switch (packet.getCommand()) {
		
		case OP_GLOBSERVSTATUS : {
			UDPServerPacket sPacket = (UDPServerPacket)packet;
			try {
				scannedPacket = new JMServerUDPStatusSP(sPacket.getChallenge(),
						sPacket.getUserCount(),sPacket.getFilesCount(),sPacket.getSoftLimit(),
						sPacket.getHardLimit(),sPacket.getAddress());
			}catch (UDPPacketException e) {
					e.printStackTrace();
			}
			
			break;
		}
		
		case OP_SERVER_DESC_ANSWER : {
			UDPServerPacket sPacket = (UDPServerPacket)packet;
			
			
			if (!sPacket.isNewDescAnswerPacket()) {
			try {
				
				scannedPacket = new JMServerUDPDescSP(sPacket.getServerName(),
						sPacket.getServerDesc(),sPacket.getAddress());
				
			} catch (UDPPacketException e) {
				
				scannedPacket = null;
				
			} } else {
				
				try {
					
					scannedPacket = new JMServerUDPNewDescSP(sPacket.getDescPacketChallenge(),sPacket.getDescPacketTagList());
					
				} catch (UDPPacketException e) {
					
					e.printStackTrace();
					
				}
			}
			break;
		}
	
		}
		
		return scannedPacket;
	}

}
