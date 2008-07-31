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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.packet.impl.PacketFactory;
import org.jmule.core.edonkey.packet.scannedpacket.ScannedPacket;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerAcceptUploadRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileHashSetRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileNotFoundSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusAnswerSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerFileStatusRequestSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerQueueRankingSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerRequestFilePartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSendingPartSP;
import org.jmule.core.edonkey.packet.scannedpacket.impl.JMPeerSlotTakenSP;
import org.jmule.core.sharingmanager.SharingManagerFactory;
import org.jmule.core.uploadmanager.UploadManager;
import org.jmule.core.uploadmanager.UploadManagerFactory;
import org.jmule.core.uploadmanager.UploadSession;


/**
 * Class which manage sessions assigned to each peer.
 * @author binary
 */ 
public class PeerSessionList {
	
	private Peer peer = null;
	
	private Map<FileHash,DownloadSession> downloadList = new ConcurrentHashMap<FileHash,DownloadSession>();
	
	private Map<FileHash,UploadSession> uploadList = new ConcurrentHashMap<FileHash,UploadSession>();
	
	
	public PeerSessionList(Peer peer){
		
		this.peer = peer;
		
	}
	
	/**Send when peer is connected, send to all from **/
	public void onConnected(){
		
		/** Notify Download List **/
		
		for(DownloadSession downloadSession : downloadList.values()) {
			
			downloadSession.onPeerConnected(getPeer());
			
		}
		
		/** Notify Upload List **/
		
		for(UploadSession uploadSession : uploadList.values()) {
			
			uploadSession.onPeerConnected(getPeer());
			
		}
	}
	
	public void onDisconnect() {
		
		/** Notify Download List **/
		
		for(DownloadSession downloadSesion : downloadList.values())
			
			downloadSesion.onPeerDisconnected(getPeer());
		
		/** Notify Upload List **/
		
		for(UploadSession uploadSession : uploadList.values())
			
			uploadSession.onPeerDisconnected(getPeer());
		
		PeerManagerFactory.getInstance().removePeer(this.getPeer());
		
	}
	
	public void reportInactivity(long time) {
		
		/** Notify Donwload list **/
		
		for(DownloadSession downloadSession : downloadList.values())
			
			downloadSession.reportInactivity(getPeer(), time);
		
		for(UploadSession uploadSession : uploadList.values())
			
			uploadSession.reportInactivity(getPeer(),time);
		
	}
	
	private DownloadSession getDownloadSession(FileHash fileHash) {
		
		return downloadList.get(fileHash);
		
	}
		
	private UploadSession getUploadSession(FileHash fileHash) {
		
		return uploadList.get(fileHash);
		
	}
	
	public void processPacket(ScannedPacket packet){
		
		int packetType = 1; //Upload packet
		
		if (packet instanceof JMPeerFileRequestAnswerSP ) packetType = 0;

		if (packet instanceof JMPeerFileStatusAnswerSP ) packetType = 0;
		
		if (packet instanceof JMPeerAcceptUploadRequestSP ) packetType = 0;
		
		if (packet instanceof JMPeerFileHashSetAnswerSP ) packetType = 0;
		
		if (packet instanceof JMPeerSendingPartSP ) packetType = 0;
		
		if (packet instanceof JMPeerFileNotFoundSP ) packetType = 0;
		
		if (packet instanceof JMPeerSlotTakenSP ) packetType = 0;
		
		if (packet instanceof JMPeerQueueRankingSP ) packetType = 0;
				
		if (packetType==0) { //Have Download Packet
			
			FileHash fileHash = null;
			
			if (packet instanceof JMPeerFileRequestAnswerSP )
				
				fileHash = ((JMPeerFileRequestAnswerSP)packet).getFileHash();

			if (packet instanceof JMPeerFileStatusAnswerSP )
				
				fileHash = ((JMPeerFileStatusAnswerSP)packet).getFileHash();

			if (packet instanceof JMPeerFileHashSetAnswerSP )
				
				fileHash = ((JMPeerFileHashSetAnswerSP)packet).getPartHashSet().getFileHash();
			
			if (packet instanceof JMPeerSendingPartSP )
				
				fileHash = ((JMPeerSendingPartSP)packet).getFileHash();
			
			if (packet instanceof JMPeerFileNotFoundSP )
				
				fileHash = ((JMPeerFileNotFoundSP)packet).getFileHash();
		
			if ( fileHash == null ) {
				
				for(DownloadSession downloadSession : downloadList.values())
					
					downloadSession.processPacket(this.getPeer(), packet, this);
				
			} else {
				
				DownloadSession downloadSession = getDownloadSession(fileHash);

				downloadSession.processPacket(getPeer(), packet, this);
			}
			
		} else {
			
			//Upload process
			
			FileHash fileHash = null;
			
			
			if (packet instanceof JMPeerFileRequestSP )
				
				fileHash = ((JMPeerFileRequestSP)packet).getFileHash();
			
			if (packet instanceof JMPeerFileStatusRequestSP ) 
				
				fileHash = ((JMPeerFileStatusRequestSP)packet).getFileHash();
			
			if (packet instanceof JMPeerFileHashSetRequestSP )
				
				fileHash = ((JMPeerFileHashSetRequestSP)packet).getFileHash();

			if (packet instanceof JMPeerRequestFilePartSP )
				
				fileHash = ((JMPeerRequestFilePartSP)packet).getFileHash();
			
			//If don't have upload session, register one new
			
			if (fileHash != null) {
				
				if (!this.hasUploadSession(fileHash)){
					
					if (!SharingManagerFactory.getInstance().hasFile(fileHash)) {
					
						peer.sendPacket(PacketFactory.getFileNotFoundPacket(fileHash));
						
						return ;
						
					}
					
					UploadManager upload_manager = UploadManagerFactory.getInstance();
					
					if (!upload_manager.hasUpload(fileHash))
					
						UploadManagerFactory.getInstance().addUpload(fileHash);
					
					UploadSession uploadSession = upload_manager.getUpload(fileHash);
										
					addUploadSession(uploadSession);
					
				} 
					
				UploadSession uploadSession = getUploadSession(fileHash);
				
				if (uploadSession != null)
						
					uploadSession.processPacket(this.getPeer(), packet, this);
			
			}
			
			if ( fileHash == null ) {
				
				for(UploadSession uploadSession : uploadList.values()) {
					
					uploadSession.processPacket(this.getPeer(), packet, this);
					
				}
				
			} 
		}
		
	}
	
	/** Process download sessions */
	
	public boolean hasDownloadSession(DownloadSession dSession){
		
		return downloadList.containsKey(dSession.getFileHash());
		
	}
	
	public boolean hasDownloadSession(FileHash fileHash){
		
		return downloadList.containsKey(fileHash);
		
	}
	
	public boolean addDownloadSession(DownloadSession dSession){
		
		if (hasDownloadSession(dSession)) return false;
		
		downloadList.put(dSession.getFileHash(), dSession);
		
		return true;
		
	}
	
	public void removeSession(DownloadSession dSession){
		
		if (this.hasDownloadSession(dSession)) {
			
			this.downloadList.remove(dSession.getFileHash());
			
			if ((downloadList.size()==0)&&(uploadList.size()==0))
				
				peer.disconnect();
			
		}
		
	}
	
	/** Process Upload Sessions */
	public boolean hasUploadSession(UploadSession uSession){
		
		return !(this.uploadList.get(uSession.getFileHash()) == null);
		
	}
	
	public boolean hasUploadSession(FileHash fileHash){
		
		return !(this.uploadList.get(fileHash) == null);
		
	}
	
	public void addUploadSession(UploadSession uploadSession){
		
		if (hasUploadSession(uploadSession)) return;
		
		this.uploadList.put(uploadSession.getFileHash(), uploadSession);
		
	}
	
	public void removeSession(UploadSession uploadSession){
		
		if (hasUploadSession(uploadSession)){
			
			uploadList.remove(uploadSession.getFileHash());
			
			if ((downloadList.size() == 0) && (uploadList.size() == 0))
				
				peer.disconnect();
		}
	}
	
	public Peer getPeer() {
		
		return this.peer;
		
	}

	public void setPeer(Peer peer){
		
		this.peer = peer;
	}
}
