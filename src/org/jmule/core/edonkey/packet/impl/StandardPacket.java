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
package org.jmule.core.edonkey.packet.impl;

import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSNOFILE;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILEREQUEST;
import static org.jmule.core.edonkey.E2DKConstants.OP_FILESTATUS;
import static org.jmule.core.edonkey.E2DKConstants.OP_HASHSETANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_MESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLO;
import static org.jmule.core.edonkey.E2DKConstants.OP_PEERHELLOANSWER;
import static org.jmule.core.edonkey.E2DKConstants.OP_REQUESTPARTS;
import static org.jmule.core.edonkey.E2DKConstants.OP_SENDINGPART;
import static org.jmule.core.edonkey.E2DKConstants.OP_SERVERLIST;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVFOUNDSOURCES;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVIDCHANGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVMESSAGE;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSEARCHRESULT;
import static org.jmule.core.edonkey.E2DKConstants.PACKET_SRVSTATUS;
import static org.jmule.core.edonkey.E2DKConstants.PROTO_EDONKEY_TCP;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.core.downloadmanager.FileChunk;
import org.jmule.core.edonkey.ServerManagerFactory;
import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.edonkey.impl.PartHashSet;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.core.edonkey.impl.UserHash;
import org.jmule.core.edonkey.packet.Packet;
import org.jmule.core.edonkey.packet.PacketReader;
import org.jmule.core.edonkey.packet.tag.Tag;
import org.jmule.core.edonkey.packet.tag.TagList;
import org.jmule.core.edonkey.packet.tag.TagReader;
import org.jmule.core.edonkey.packet.tag.impl.StandardTag;
import org.jmule.core.net.JMEndOfStreamException;
import org.jmule.core.net.JMFloodException;
import org.jmule.core.net.JMuleSocketChannel;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.searchmanager.SearchResultItemList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.uploadmanager.FileChunkRequest;
import org.jmule.util.Convert;
import org.jmule.util.Misc;

/**
 * Created on 2007-Nov-07
 * @author binary256
 * @version $$Revision: 1.4 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/02 15:18:57 $$
 */
public class StandardPacket extends AbstractPacket implements Packet {

	public StandardPacket() {
	}
	
	public StandardPacket(int packetLength) {
		
		dataPacket=ByteBuffer.allocate(packetLength+1+4+1);
		
		dataPacket.order(ByteOrder.LITTLE_ENDIAN);
		
		dataPacket.put(PROTO_EDONKEY_TCP);
		
		dataPacket.putInt(packetLength+1);//Put length +1 to write command byte
		
		dataPacket.put((byte)0);//Put default command
	}
	
	/** 
	 * Get ID Change packet data
	 */
	public ClientID getIDChangeData()throws PacketException{
		if (this.getCommand()!=PACKET_SRVIDCHANGE){
			
			throw new PacketException("No SRVIDCHANGE Packet "+this);
		}
		try {
			
			byte[] b = new byte[4];
			
			ClientID id;
			
			dataPacket.position(6);
			
			dataPacket.get(b);
			
			id = new ClientID((b));
			
			return id;
			
		}catch(Exception e){
			
			throw new PacketException("Failed to extract IDChange data from packet : "+this);
		}
	}
	
	/**
	 * Get number of users from packet
	 */
	public int getNumUsersData() throws PacketException {
		
		if (this.getCommand()!=PACKET_SRVSTATUS){
			
			throw new PacketException("No SRVSTATUS Packet "+this);
			
		}
		try {
			
			return dataPacket.getInt(6);
			
		}catch(Exception e){
			throw new PacketException("Failed to extract NumUsers data from packet : "+this);
		}
	}
	
	/**
	 * Get number of files from packet
	 */
	public int getNumFilesData() throws PacketException {
		
		if (this.getCommand() != PACKET_SRVSTATUS) {
			
			throw new PacketException("No SRVSTATUS Packet " + this);
			
		}
		try {
			
			return dataPacket.getInt(10);
			
		} catch (Exception e) {
			
			throw new PacketException(
					"Failed to extract NumFiles data from packet : " + this);
		
		}
	}
	
	/**
	 * Get Server message from packet
	 */
	public String getServerMessageData() throws PacketException {
		
		if (this.getCommand() != PACKET_SRVMESSAGE) {
			
			throw new PacketException("No SRVMESSAGE Packet"+this);
		}
		
		
		try {
			
			short msgLen = dataPacket.getShort(6);
			
			String srvMsg = "";
			
			for (int i = 0; i < msgLen; i++)
				
				srvMsg = srvMsg + (char) dataPacket.get(8 + i);
			
			return srvMsg;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new PacketException(
					"Failed to extract Server Message data from packet : "
							+ this);
		}
	}

	public List<Server> getServerList() throws PacketException {
		
		if (this.getCommand() != OP_SERVERLIST) {
			throw new PacketException("No SRVMESSAGE Packet"+this);
		}
		
		List<Server> server_list = new LinkedList<Server>();
		
		int server_count = Convert.byteToInt(dataPacket.get(6));
		
		dataPacket.position(7);
		
		for(int i = 0;i < server_count; i++) {
			byte address[] = new byte[4];
			int port ;
			
			dataPacket.get(address);
			port = Convert.shortToInt(dataPacket.getShort());
			
			server_list.add(new Server(Convert.IPtoString(address),port));
		}
		
		return server_list;
	}
	
	/** 
	 * Extract user Hash from packet 
	 */
	public UserHash getUserHashHelloAnswerPacket(){
		byte[] data = new byte[16];
		dataPacket.position(1 + 4 + 1);
		dataPacket.get(data);
		return new UserHash(data);
	}
	
	public int getTCPPortHelloAnswerPacket() {
		int port;
		dataPacket.position(1 + 4 + 1 + 16 + 4 );
		port = Convert.shortToInt(dataPacket.getShort());
		return port;
	}
	
	
	/** 
	 * Extract user Hash from packet 
	 */
	public UserHash getUserHashHelloPacket(){
		byte[] data = new byte[16];
		dataPacket.position(1 + 4 + 1 + 1);
		dataPacket.get(data);
		return new UserHash(data);
	}
	
	public int getTCPPortHelloPacket() {
		int port;
		dataPacket.position(1 + 4 + 1 + 1 + 16 + 4 );
		port = Convert.shortToInt(dataPacket.getShort());
		return port;
	}
	
	/**
	 * Get search result count
	 * @return search result count
	 */
	public int getSearchResultCount()throws PacketException {
		if (this.getCommand()!=PACKET_SRVSEARCHRESULT){
			throw new PacketException("No SRVSEARCHRESULT Packet "+this);
		}
		try {
			return dataPacket.getInt(6);
		}catch(Exception e){
			throw new PacketException("Failed to extract result count "+this);
		}
	}
	
	
	public SearchResultItemList getSearchResults() throws PacketException {
		if (this.getCommand()!=PACKET_SRVSEARCHRESULT){
			throw new PacketException("No SRVSEARCHRESULT Packet "+this);
		}
		SearchResultItemList searchResults = new SearchResultItemList();
		dataPacket.position(6);
		int resultCount = dataPacket.getInt();
		for(int i = 0;i<resultCount;i++) {
			byte fileHash[] = new byte[16];
			dataPacket.get(fileHash);
			
			byte clientID[] = new byte[4];
			dataPacket.get(clientID);
			
			short clientPort = dataPacket.getShort();
			
			SearchResultItem result = new SearchResultItem(new FileHash(fileHash), new ClientID(clientID),clientPort); 
			
			int tagCount = dataPacket.getInt();
			
			for(int j=0;j<tagCount;j++) {
				//Tag tag = Misc.loadStandardTag(dataPacket);
				Tag tag = TagReader.readTag(dataPacket);
				result.addTag(tag);
			}
			searchResults.add(result);
		}
		
		return searchResults;
	}
	
    public long getSourceCountFoundSources() throws PacketException {
            if (this.getCommand() != PACKET_SRVFOUNDSOURCES)
                    throw new PacketException("No PACKET_SRVFOUNDSOURCES "+this);
            long sourceCount = Convert.byteToLong(dataPacket.get( 1 + 4 + 1 + 16 ));
            return sourceCount;
    }
    
    public Collection<Peer> getFoundSources() throws PacketException {
            if (this.getCommand() != PACKET_SRVFOUNDSOURCES)
                    throw new PacketException("No PACKET_SRVFOUNDSOURCES "+this);
            
            FileHash fileHash = getFileHashFoundSources();
            
            long sourceCount = getSourceCountFoundSources();
            Collection<Peer> peerList = new LinkedList<Peer>();
            dataPacket.position( 1 + 4 + 1 + 16 + 1 );
            byte[] peerID = new byte[4];
            int peerPort;
            ByteBuffer data = ByteBuffer.allocate(4);
            data.order( ByteOrder.LITTLE_ENDIAN );
            for( int i = 0 ; i < sourceCount ; i++ ){
            	for( int j = 0 ; j < 4 ; j++ ){
            	data.clear();
            	data.rewind();
                byte b = dataPacket.get();
                data.put(b);
                peerID[j] = Convert.intToByte(data.getInt(0));
                }
            	
            	byte[] portArray=new byte[2];
            	dataPacket.get(portArray);
                
            	ByteBuffer tmpData=ByteBuffer.allocate(4);
                tmpData.order(ByteOrder.LITTLE_ENDIAN);
                tmpData.put(portArray);
                tmpData.position(0);
                peerPort=tmpData.getInt();
                
                ClientID cid = new ClientID(peerID);
               // if (PeerManagerFactory.getInstance().hasPeer(cid)) continue;
               // if (PeerManagerFactory.getInstance().isFull()) continue;
                
             
                Peer peer = new Peer(cid,peerPort,ServerManagerFactory.getInstance().getConnectedServer(),fileHash);
                
				peerList.add(peer);
			              
                }
      
            return peerList;
    }
    
    
    public FileHash getFileHashFoundSources() throws PacketException {
    	byte[] fileHash = new byte[16];
    	if (this.getCommand() != PACKET_SRVFOUNDSOURCES)
            throw new PacketException("No PACKET_SRVFOUNDSOURCES "+this);
    	dataPacket.position( 1 + 4 + 1 );
    	dataPacket.get(fileHash);
    	return new FileHash(fileHash);
    }
	
    public TagList getPeerHelloTagList() throws PacketException{
    	if (this.getCommand()!=OP_PEERHELLO) throw new PacketException("No OP_PEERHELLO "+this);
    	this.dataPacket.position(1+4+1+1+16+4+2);
    	TagList TagList = this.getTags();
    	
    	return TagList;
    }
    
    public InetSocketAddress getPeerHelloServerAddress() throws PacketException {
    	if (this.getCommand()!=OP_PEERHELLO) throw new PacketException("No OP_PEERHELLO packet "+this);
    	dataPacket.position(dataPacket.capacity()-6);
    	byte[] IPAddress = new byte[4];
    	dataPacket.get(IPAddress);
    	short port = dataPacket.getShort();
    	InetSocketAddress address = InetSocketAddress.createUnresolved(Convert.IPtoString(IPAddress), port);
    	return address;
    }
    
    public TagList getPeerHelloAnswerTagList() throws PacketException{
    	if (this.getCommand()!=OP_PEERHELLOANSWER) throw new PacketException("No OP_PEERHELLOANSWER packet "+this);
    	this.dataPacket.position(1+4+1+16+4+2);
    	TagList TagList = this.getTags();
    	
    	return TagList;
    }
    
    public InetSocketAddress getPeerHelloAnswerServerAddress() throws PacketException {
    	if (this.getCommand()!=OP_PEERHELLOANSWER) throw new PacketException("No OP_PEERHELLOANSWER packet "+this);
    	dataPacket.position(dataPacket.capacity()-6);
    	byte[] IPAddress = new byte[4];
    	dataPacket.get(IPAddress);
    	short port = dataPacket.getShort();
    	InetSocketAddress address = InetSocketAddress.createUnresolved(Convert.IPtoString(IPAddress), port);
    	return address;
    }
    
    public String getPeerMessage()throws PacketException {
    	if (this.getCommand()!=OP_MESSAGE)
    		throw new PacketException("No OP_MESSAGE packet "+this);
    	short strLength = dataPacket.getShort(1+4+1);
    	byte[] strData = new byte[strLength];
    	dataPacket.position(1+4+1+2);
    	dataPacket.get(strData);
    	return new String(strData);
    }
        
    /**
     * Extract ClientID from  packet 
     */
    public ClientID getPeerClientIDHelloPacket() throws PacketException {
    	byte clientID[] = new byte[4];
    	dataPacket.position(1+4+1+1+16);
    	dataPacket.get(clientID);
    	return new ClientID(clientID);
    }
   
    /**
     * Extract ClientID from  packet 
     */
    public ClientID getPeerClientIDHelloAnswerPacket() throws PacketException {
    	byte clientID[] = new byte[4];
    	dataPacket.position(1+4+1+16);
    	dataPacket.get(clientID);
    	return new ClientID(clientID);
    }
	
    /**
     * Extract file hash from No Such file packet
     * @return file hash
     * @throws PacketException
     */
    public FileHash getFileHashNoSuchFile() throws PacketException {
    	byte[] fileHash = new byte[16];
    	if (this.getCommand() != OP_FILEREQANSNOFILE)
    				throw new PacketException("No OP_FILEREQANSNOFILE packet "+this);
    	dataPacket.position( 1 + 4 + 1 );
    	dataPacket.get( fileHash );
    	return new FileHash(fileHash);
    }

    /**
     * Get File Hash from found file packet
     * @return file hash, 16 bytes
     * @throws PacketException
     */
    public FileHash getFileHashRequestAnswer() throws PacketException {
    	byte[] fileHash = new byte[16];
    	if (this.getCommand() != OP_FILEREQANSWER)
    				throw new PacketException("No OP_FILEREQANSWER packet "+this);
    	int iPos = dataPacket.position();
    	dataPacket.position(1 + 4 + 1);
    	dataPacket.get(fileHash);
    	dataPacket.position(iPos);
    	return  new FileHash(fileHash);
    }
    
    public String getFileNameRequestAnswer() throws PacketException {
    	dataPacket.position(1 + 4 + 1+16);
    	short strLen = dataPacket.getShort();
    	byte strData[] = new byte[strLen];
    	dataPacket.get(strData);
    	
    	return new String(strData);
    }

    
    /**
     * Get file ID form file part packet
     * @return file hash
     */
    public FileHash getFileIDFilePart() throws PacketException {
    	byte[] fileHash=new byte[16];

    	if (this.getCommand() != OP_SENDINGPART) 
    		throw new PacketException("No OP_SENDINGPART packet : "+this);
    	int iPos = dataPacket.position();
    	dataPacket.position(1 + 4 + 1);//Move to file hash
    	dataPacket.get(fileHash);
    	dataPacket.position(iPos);
    	return new FileHash(fileHash);
    }
    
    
    public FileChunk getFileChunk() throws PacketException {
    	if (this.getCommand() != OP_SENDINGPART) 
    		throw new PacketException("No OP_SENDINGPART packet "+this);
    	dataPacket.position(1+4+1+16);
    	long chunkStart = Convert.intToLong(dataPacket.getInt());
    	long chunkEnd = Convert.intToLong(dataPacket.getInt());
    	ByteBuffer data = Misc.getByteBuffer(chunkEnd-chunkStart);
    	dataPacket.get(data.array());
    	return new FileChunk(chunkStart,chunkEnd,data);
    }
       
    public PartHashSet getPartHashSet() throws PacketException {
       	if (this.getCommand() != OP_HASHSETANSWER) 
    		throw new PacketException("No OP_HASHSETANSWER packet "+this);
    	byte[] fileHash = new byte[16];
    	dataPacket.position(1 + 4 + 1);
    	dataPacket.get(fileHash);
    	int partCount = Convert.shortToInt(dataPacket.getShort());
    	PartHashSet partSet = new  PartHashSet(new FileHash(fileHash));
    	byte[] partHash = new byte[16];

    	for( short i = 1 ; i <= partCount ; i++ ) {
    		dataPacket.get(partHash);
    		partSet.add(partHash);
    	}
    	return partSet;
    }
    
    public FileHash getFileHashFileRequest() throws  PacketException {
    	if (this.getCommand() != OP_FILEREQUEST)
    		throw new PacketException("No OP_FILEREQUEST packet " + this);
    	
    	int iPos = dataPacket.getInt();
    	byte[] data = new byte[16];
    	
    	dataPacket.position(1+4+1);
    	dataPacket.get(data);
    	FileHash fileHash = new FileHash(data);
    	dataPacket.position(iPos);
    	
    	return fileHash;
    }
    
    public Collection<FileChunkRequest> getFileChunksRequest() throws PacketException {
    	if (this.getCommand()!=OP_REQUESTPARTS)
    		throw new PacketException("No OP_REQUESTPARTS packet "+this);
    	dataPacket.position(1+4+1+16);
    	Collection<FileChunkRequest> chunks = new LinkedList<FileChunkRequest>();

    	long[] startPos = new long[3];
    	long[] endPos = new long[3];
    	
      	for(int i = 0; i<3 ; i++ )
    		startPos[i]=Convert.intToLong(dataPacket.getInt());

    	for(int i = 0; i<3 ; i++ )
    		endPos[i]=Convert.intToLong(dataPacket.getInt());
    	
    	
    	for(int i = 0;i<3;i++) {
    		if ((startPos[i]==endPos[i])&&(startPos[i]==0)) break;
    		chunks.add(new FileChunkRequest(startPos[i],endPos[i]));
    	}
    	
    	return chunks;
    }
    
    /**
     * @deprecated
     * @return
     * @throws PacketException
     */
    public long[][] getFileRequestPositions() throws PacketException {
    	if (this.getCommand()!=OP_REQUESTPARTS)
    		throw new PacketException("No OP_REQUESTPARTS packet " + this);
    	
    	long[][] pos = new long[3][2];
    	int iPos = dataPacket.position();
    	dataPacket.position(1+4+1+16);
    	//Part begin
    	for(int i = 0; i<3 ; i++ )
    		pos[i][0]=dataPacket.getInt();
    	//Part end
    	for(int i = 0; i<3 ; i++ )
    		pos[i][1]=dataPacket.getInt();
    	dataPacket.position(iPos);
    	return pos;
    }
    
    public int getPartCount() throws PacketException {
    	if (this.getCommand()!=OP_FILESTATUS)
    		throw new PacketException("No OP_FILESTATUS packet "+this);
    	dataPacket.position(1+4+1+16);
    	return dataPacket.getShort();
    }
    
    public JMuleBitSet getPartStatus() throws PacketException {
    	byte pcmd = this.getCommand();
    	
    	if (pcmd!=OP_FILESTATUS)
    		throw new PacketException("No OP_FILESTATUS packet "+this);
    	dataPacket.position(1+4+1+16);
    	short partCount = dataPacket.getShort();
    	
    	int count = (partCount+7)/8;
    	if (((partCount+7)/8)!=0) count++;
    	
    	byte[] data = new byte[count];
    	for(int i=0;i<count;i++)
    		data[i] = dataPacket.get();
    	
    	JMuleBitSet bitSet;
     	bitSet = Convert.byteToBitset(data);
    	bitSet.setPartCount(partCount);
    	return bitSet;
    }
    
    /** Get all tags from current position **/
	public TagList getTags() {
	    int tagCount = this.dataPacket.getInt();
	    TagList TagList = new TagList();
	    for(int i = 0;i<tagCount;i++) {
	    	StandardTag Tag = new StandardTag();
	    	Tag.extractTag(this.dataPacket);
	    	TagList.addTag(Tag);
	    }
		return TagList;
	}

	 public void addData(ByteBuffer data){
		 this.insertData(5,data.array());
	  }
	 
	  
	/**
	 * Read packet from remonte connection
	 * @param connection remonte connection
	 * @return readed packet
	 * @throws InterruptedException 
	 * @throws JMEndOfStreamException 
	 */
	public void readPacket(JMuleSocketChannel connection) throws IOException, JMEndOfStreamException, InterruptedException,JMFloodException {
		this.clear();
		
		ByteBuffer packetLength=Misc.getByteBuffer(4);		
		connection.read(packetLength);
		
		int pkLength = packetLength.getInt(0);
		
		if (pkLength>ConfigurationManager.MAX_PACKET_SIZE) 
			throw new JMFloodException("Packet length is too big, packet length : "+pkLength);
		
		dataPacket=ByteBuffer.allocate(pkLength+1+4+1);
		dataPacket.order(ByteOrder.LITTLE_ENDIAN);
		dataPacket.put(PROTO_EDONKEY_TCP);
		dataPacket.putInt(pkLength+1);//Put length +1 to write command byte
		dataPacket.put((byte)0);//Put default command
		ByteBuffer defaultData = PacketReader.readBytes(connection, pkLength);
		this.insertData(5,defaultData.array());
		
	}


}
