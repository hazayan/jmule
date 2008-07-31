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
package org.jmule.ui.swing.maintabs;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.jmule.ui.swing.tables.DownloadsTable;
import org.jmule.ui.swing.tables.UploadsTable;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:01 $$
 */
public class TransfersTab extends JPanel {

	private JSplitPane splitPane;
	private GridLayout gridLayout;
	private JScrollPane downloadsScrollPane;
	private JScrollPane uploadsScrollPane;
	private DownloadsTable downloadsTable;
	private UploadsTable uploadsTable;
	
	public TransfersTab() {
		initComponents();
	}
	
	private void initComponents() {
		
		splitPane = new JSplitPane();
		gridLayout = new GridLayout(1,1);
		downloadsScrollPane = new JScrollPane();
		uploadsScrollPane = new JScrollPane();
		downloadsTable = new DownloadsTable();
		uploadsTable = new UploadsTable();
		
	    downloadsScrollPane.setPreferredSize(new Dimension(100,200));
		uploadsScrollPane.setPreferredSize(new Dimension(10,10));
		
		this.setLayout( gridLayout );
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		this.add(splitPane);
		
		splitPane.setTopComponent( downloadsScrollPane );
		splitPane.setBottomComponent( uploadsScrollPane );
		
		downloadsScrollPane.setViewportView(downloadsTable);
		uploadsScrollPane.setViewportView(uploadsTable);
		
		//splitPane.resetToPreferredSizes();

	}
	
	public static void main(String args[]) {
		
		 JFrame jf = new JFrame();
		 jf.setSize(400,500);
		 
		 TransfersTab tt = new TransfersTab();
		 
		 jf.add(tt);
		 
		 jf.setVisible(true);
		
		
	}
	
}
