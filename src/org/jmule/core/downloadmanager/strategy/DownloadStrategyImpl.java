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
package org.jmule.core.downloadmanager.strategy;

import static org.jmule.core.edonkey.E2DKConstants.PARTSIZE;

import java.util.Collection;
import java.util.Iterator;

import org.jmule.core.downloadmanager.FileFragment;
import org.jmule.core.downloadmanager.FilePartStatus;
import org.jmule.core.downloadmanager.FileRequestList;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.core.sharingmanager.JMuleBitSet;
import org.jmule.core.uploadmanager.FileChunkRequest;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/10/19 18:53:36 $$
 */
public class DownloadStrategyImpl implements DownloadStrategy{
	
	public FileChunkRequest fileChunkRequest(Peer sender, long blockSize,long fileSize,
			GapList gapList,FilePartStatus filePartStatus, FileRequestList fileRequestList) {
		
		int availibility[] = filePartStatus.getPartAvailibility();
		
		int countSet = 0;//Total count of downloaded parts
		
		JMuleBitSet bs = filePartStatus.get(sender);
		
		for(int i = 0;i<filePartStatus.getPartAvailibility().length;i++) {
			
			if (availibility[i]!=0)
				
				if (bs.get(i)){
					
					if (gapList.getIntersectedGaps(PARTSIZE*i, PARTSIZE*(i+1)-1).size()==0) {
						
						availibility[i] = 0;//This part is  downloaded
						
						countSet++;
						
					}
			} 
				else
					
					availibility[i] = 0;//Sender don't have i part
		}
		
		String msg ="";
		
		for(int i = 0;i<availibility.length;i++)
			
			msg+= " "+availibility[i];
		
		if (countSet==availibility.length) {
			//Have all parts, end download
			return null;
			}
		
	    do {
	    	
	    	//Obtain minimal sources part
	    	
	    	int minPos = -1;
	    	
	    	for(int i = 0;i<availibility.length;i++)
	    		
	    		if (availibility[i]!=0) {
	    			
				if (minPos == -1)
					
					minPos = i;
				
				else
					
					if (availibility[i]<availibility[minPos])
						
						minPos = i;
			}
	    	
	    	if (minPos==-1)
	    		
	    		return null;
	    	
	    	long begin = minPos*PARTSIZE;
	    	
	    	long end = (minPos+1)*PARTSIZE;
	    	
	    	if (end>fileSize)
	    		
	    		end = fileSize;
		
	    	long startPos = 0;
	    	
	    	long endPos = 0;
	    	
	    	Collection<Gap> fg = intersectGapListFileRequstList(gapList,fileRequestList,begin,end);

	    	if (fg.size()==0) {
	    		
	    		//Don't have fragments, try another part
	    		
	    		availibility[minPos] = 0;

	    		continue;
	    		
	    	}
		
	    	Gap f1 =(Gap) fg.toArray()[0];
		
	    	startPos = f1.getStart();
	    	
	    	endPos = startPos +blockSize;
		
	    	while (!((endPos>=f1.getStart())&&(endPos<=f1.getEnd())))
	    		
				endPos--;
				
	    	return new FileChunkRequest(startPos,endPos);
	    	}while(true);
		
	} 
	

	public FileChunkRequest[] fileChunk3Request(Peer sender, long blockSize,
			long fileSize, GapList gapList, FilePartStatus filePartStatus,
			FileRequestList fileRequestList) {
	
		FileChunkRequest[] fileChunks = new FileChunkRequest[3];
		
		for(int i = 0;i<fileChunks.length;i++) {
			
			FileChunkRequest fileChunk = this.fileChunkRequest(sender, blockSize, fileSize, gapList, filePartStatus, fileRequestList);
		
			if (fileChunk==null)
			
				fileChunk = new FileChunkRequest(0,0);
		
			else
			
				fileRequestList.addFragment(sender,fileChunk.getChunkBegin(), fileChunk.getChunkEnd());
		
			fileChunks[i] = fileChunk;
		}
		
		return fileChunks;
	}
	
	/**
	 * Intersect GapList with FileFragment list.
	 * @return
	 */
	private Collection<Gap> intersectGapListFileRequstList(GapList gapList,FileRequestList fileRequestList,long begin,long end){
		
		// Obtain Gaps from [begin:end] segment
		
		Collection<Gap> gaps ;
		
		gaps = gapList.getGapsFromSegment(begin, end);
				
		// Obtain File Fragments from [begin:end] segment
		
		Collection<FileFragment> fragment;
		
		fragment = fileRequestList.getFragmentsFromSegment(begin, end);
		
		boolean stop=true;
		
		do {
			
			stop=true;
			
			for(int i = 0;i<gaps.size();i++) {
				
				Gap g = (Gap)gaps.toArray()[i];
			
			for(int j = 0;j<fragment.size();j++) {
				
				FileFragment ff =(FileFragment)fragment.toArray()[j];
				
				if ((ff.getStart()>g.getStart())&&(ff.getEnd()<g.getEnd()))
					
					if (ff.getEnd()>g.getStart()&&(ff.getEnd()<g.getEnd())) {
						
						gaps.remove(g);
						
						Gap g1 = new Gap(g.getStart(),ff.getStart()-1);
						
						gaps.add(g1);
						
						Gap g2 = new Gap(ff.getEnd(),g.getEnd());
						
						gaps.add(g2);
						
						stop=false;
						
						break;
						
					}
				
				if ((ff.getStart()>g.getStart())&&(ff.getEnd()<g.getEnd()))
					
					if (ff.getEnd()>=g.getEnd()) {
						
						gaps.remove(g);
						
						Gap g1 = new Gap(g.getStart(),ff.getStart()-1);
						
						gaps.add(g1);
						
						stop = false;
						
						break;
						
					}
				
				if (ff.getStart()<=g.getStart())
					
					if (ff.getEnd()>g.getStart()&&(ff.getEnd()<g.getEnd())) {
						
						gaps.remove(g);
						
						Gap g1 = new Gap(ff.getEnd(),g.getEnd());
						
						gaps.add(g1);
						
						stop = false;
						
						break;
						
					}
				
				if ((ff.getStart()<=g.getStart())&&(ff.getEnd()>=g.getEnd())) {
					
					gaps.remove(g);
					
					stop = false;
					
					break;
					
				}

			}
			
			if (!stop)
				
				break;
			
			}
			
		}while(stop==false);
		
		return gaps;
	}

	private String collectionToStr(Collection x) {
		
		String result=" ";
		
		for(Iterator i = x.iterator();i.hasNext();) {
			
			Object l = i.next();
			
			result+=" "+l;
			
		}
		
		return result+" ";
	}

}
