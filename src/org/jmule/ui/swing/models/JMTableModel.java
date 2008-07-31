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
package org.jmule.ui.swing.models;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:58 $$
 */
public class JMTableModel extends DefaultTableModel {

	int[] indexes;
	JMTableSorter sorter;
	
	//public JMTableModel() {
	//	super();
	//}
	
	public Object getValueAt(int row, int col) {
		int rowIndex = row;
		if(indexes != null) {
			rowIndex = indexes[row];
		}
		return super.getValueAt(rowIndex, col);
	}
	
	public void setValueAt(Object value, int row, int col) {
		int rowIndex = row;
		if(indexes != null) {
			rowIndex = indexes[row];
		}
		super.setValueAt(value, rowIndex, col);
	}
	
	public void sortByColumn(int column, boolean isAscent) {
		if( sorter == null ) {
			sorter = new JMTableSorter(this);
		}
		sorter.sort(column, isAscent);
		fireTableDataChanged();
	}
	
	public int[] getIndexes() {
		int n = getRowCount();
		if(indexes != null) {
			if(indexes.length == n) {
				return indexes;
			}
		}
		indexes = new int[n];
		for(int i=0; i<n; i++) {
			indexes[i] = i;
		}
		return indexes;
	}
	
}
