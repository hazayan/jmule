/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.jkad.indexer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jmule.core.jkad.Int128;


/**
 * Created on Apr 22, 2009
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/06 14:13:25 $
 */
public class Index {

	protected Int128 id;
	protected List<Source> sourceList = new CopyOnWriteArrayList<Source>();
	
	public Index(Int128 id) {
		this.id = id;
	}
	
	public Int128 getId() {
		return id;
	}
	public void setId(Int128 id) {
		this.id = id;
	}
	public List<Source> getSourceList() {
		return sourceList;
	}
	public void setSourceList(List<Source> sourceList) {
		this.sourceList = sourceList;
	}
	
	public void addSource(Source source) {
		if (sourceList.contains(source)) return ;
		sourceList.add(source);
	}
	
}
