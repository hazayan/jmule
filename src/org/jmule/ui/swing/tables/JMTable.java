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
package org.jmule.ui.swing.tables;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jmule.ui.swing.models.JMTableModel;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:03 $$
 */
public class JMTable extends JTable {

	 private DefaultTableCellRenderer whiteRenderer;
	 private DefaultTableCellRenderer grayRenderer;
	 
	 String[] headerStr = {"Name", "Date", "Size", "Dir"};
	 int[] columnWidth = {100, 150, 100, 50};
	
	 public JMTable(JMTableModel tableModel) { 
		 //super();
		 this.setModel(tableModel);
		 this.setShowGrid(false);
		 this.setRowHeight(25);
		 this.setShowGrid(false);
		 this.setIntercellSpacing(new Dimension(0,0));
		 //this.setCellSelectionEnabled(true);
		 this.setRowSelectionAllowed(true);
		 
		 SortButtonRenderer renderer = new SortButtonRenderer();
		 TableColumnModel model = this.getColumnModel();
		 //int n = headerStr.length;
		 int n = 10;
		 for(int i = 0; i < n; i++) {
		    model.getColumn(i).setHeaderRenderer(renderer);
		    //model.getColumn(i).setPreferredWidth(columnWidth[i]);
		 }
		 JTableHeader header = this.getTableHeader();
		 header.addMouseListener(new HeaderListener(header, renderer));
	 }
	 
     public TableCellRenderer getCellRenderer(int row, int column) {
	      if (whiteRenderer == null)
	      {
	         whiteRenderer = new DefaultTableCellRenderer();
	      }
	      if (grayRenderer == null)
	      {
	         grayRenderer = new DefaultTableCellRenderer();
	         grayRenderer.setBackground(new Color(240,240,240));
	      }
	 
	      if ( (row % 2) == 0 )
	            return whiteRenderer;
	      else
	            return grayRenderer;
	 }
     
     // We must listen for mouse clicks in order to do table sorting
     class HeaderListener extends MouseAdapter {
 		JTableHeader header;
 		SortButtonRenderer renderer;
 		
 		HeaderListener(JTableHeader header, SortButtonRenderer renderer) {
 			this.header = header;
 			this.renderer = renderer;
 		}
 		
 		public void mousePressed(MouseEvent e) {
 			int col = header.columnAtPoint(e.getPoint());
 			int sortCol = header.getTable().convertColumnIndexToModel(col);
 			renderer.setPressedColumn(col);
 			renderer.setSelectedColumn(col);
 			header.repaint();
 			
 			if(header.getTable().isEditing()) {
 				header.getTable().getCellEditor().stopCellEditing();
 			}
 			
 			boolean isAscent;
 			if(SortButtonRenderer.DOWN == renderer.getState(col)) {
 				isAscent = true;
 			} else {
 				isAscent = false;
 			}
 			((JMTableModel)header.getTable().getModel())
 			    .sortByColumn(sortCol, isAscent);
 		}
 		
 		public void mouseReleased(MouseEvent e) {
             int col = header.columnAtPoint(e.getPoint());
             renderer.setPressedColumn(-1);                  //clear
             header.repaint();
 		}
 	}
	
}
