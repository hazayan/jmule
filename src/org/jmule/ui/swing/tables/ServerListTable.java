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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jmule.ui.swing.UISwingImageRepository;
import org.jmule.ui.swing.models.ServerListTableModel;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:01 $$
 */
public class ServerListTable extends JMTable {

	//init jtable
	public ServerListTable() {
		super(new ServerListTableModel());
		//this.setModel( new ServerListTableModel() );
		init();
		//this.setModel(new ServerListTableModel());
	}
	
	private void init() {
		this.getColumnModel().
		   getColumn(ServerListTableModel.SERVER_ICON).
		     setCellRenderer(new TableCellRenderer() {
		          public Component getTableCellRendererComponent(JTable table, 
		        		                                         Object stringValue, 
		        		                                         boolean isSelected, 
		        		                                         boolean hasFocus, 
		        		                                         int row, 
		        		                                         int column) {
		        	 JLabel jl = new JLabel();
			         jl.setIcon(UISwingImageRepository.getIcon("mini_globe.png"));
			         return jl;
		          }
	         });
		
		// Set up a JPopup menu for server list
		final JPopupMenu popup_menu = new JPopupMenu();
		popup_menu.add(new JMenuItem("Connect to...",UISwingImageRepository.getIcon("server_connect2.png")));
		popup_menu.add(new JMenuItem("Add",UISwingImageRepository.getIcon("server_add.png")));
		popup_menu.add(new JMenuItem("Remove",UISwingImageRepository.getIcon("server_delete.png")));
		popup_menu.add(new JMenuItem("Edit",UISwingImageRepository.getIcon("server_edit.png")));
		popup_menu.add(new JMenuItem("Properties",UISwingImageRepository.getIcon("properties.png")));
		class PopupListener extends MouseAdapter {
			
			 public void mousePressed(MouseEvent e) {
			     showPopup(e);
			 }
			 
			 public void mouseReleased(MouseEvent e) {
			     showPopup(e);
			 }
			 
			 private void showPopup(MouseEvent e) {
			     if (e.isPopupTrigger()) {
			    	 popup_menu.show(e.getComponent(), e.getX(), e.getY());
			     }
			 }
			 
		}
		this.addMouseListener(new PopupListener());
	}
	
	
	// ServerListTableModel
	
	
}
