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
package org.jmule.util;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:26 $$
 */
public class Average {

	private float data[];
	
	private int pos;
	
	private boolean useAll = false;
	
	public Average(int size) {
		
		data = new float[size];
		
		pos = 0;
		
	}
	
	public void add(float value) {
		
		if (pos >= data.length) {
			
			useAll = true;
			
			pos=0;
			
		}
		
		data[pos++] = value;
	}
	
	public float getAverage() {
		
		float sum = 0;
		
		int max = pos;
		
		if (useAll)
			max = data.length;
		
		for(int i = 0;i<max;i++)
			
			sum+=data[i];
		
		if (max!=0)
			
			return (float)((float)sum/(float)max);
		
		return 0;
	}
	
	public String toString() {
		
		String result="";
		
		result=" [ ";
		
		for(int i = 0;i<this.data.length;i++)
			
			result+=" "+data[i];
		
		result+=" ] ";
		
		return result;
	}
	
}
