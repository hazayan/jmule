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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jmule.ui.swing.tables.MyInfoTable;
import org.jmule.ui.swing.tables.ServerListTable;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:02 $$
 */
public class ServerListTab extends JPanel {

   private JSplitPane splitPane;
   private JSplitPane splitPane2;
   private JScrollPane serverListScrollPane;
   private JScrollPane serverMessagesScrollPane;
   private JScrollPane myInfoScrollPane;
   private GridLayout gridLayout;
   private ServerListTable serverListTable;
   private MyInfoTable myInfoTable;
   private JTextArea serverMessages;
	
   public ServerListTab() {
	   initComponents();
   }
   
   private void initComponents() {
	   
	   splitPane = new JSplitPane();
	   splitPane2 = new JSplitPane();
	   serverListScrollPane = new JScrollPane();
	   serverMessagesScrollPane = new JScrollPane();
	   myInfoScrollPane = new JScrollPane();
	   gridLayout = new GridLayout(1,1);
	   serverListTable = new ServerListTable(); 
	   myInfoTable = new MyInfoTable();
	   serverMessages = new JTextArea();
	   
	   this.setLayout(gridLayout);
	   splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	   this.add(splitPane);
	   
	   splitPane2.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
	   splitPane.setBottomComponent(splitPane2);
	   
	   //The following declarations are the style for titled border used in the appl.
	   Font titledBorderTextFont = new java.awt.Font("Dialog", 1, 12);
	   Color titledBorderTextColor = new java.awt.Color(0, 0, 255); 
	   Color titledBorderColor = new java.awt.Color(0, 0, 0);
	   LineBorder border = new javax.swing.border.LineBorder(titledBorderColor, 1, true);
	   
	   //Set up the border and border style for the all titled borders
	   /*TitledBorder titledBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
                   "Unknown", 
                   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                   titledBorderTextFont, 
                   titledBorderTextColor);*/
	   
	   //Set up the title border for serverListScrollPane
	   TitledBorder serverListScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
                   "Servers(212)", 
                   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
                   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
                   titledBorderTextFont, 
                   titledBorderTextColor);
	   serverListScrollPane.setBorder(serverListScrollPaneBorder);
	   
	   serverListScrollPane.setViewportView(serverListTable);
	   splitPane.setTopComponent(serverListScrollPane);
	   
	   //Set up the border for myInfoScrollPane
	   TitledBorder myInfoScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
				   "My info", 
				   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
				   titledBorderTextFont, 
				   titledBorderTextColor);
	   myInfoScrollPane.setBorder(myInfoScrollPaneBorder);
	   
	   myInfoScrollPane.setViewportView(myInfoTable);
	   splitPane2.setBottomComponent(myInfoScrollPane);
	   serverMessagesScrollPane.setViewportView(serverMessages);
	   
	   //Set up the border for serverMessagesScrollPane
	   TitledBorder serverMessagesScrollPaneBorder = 
		   javax.swing.BorderFactory.createTitledBorder(
				   border, 
				   "Server messages", 
				   javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
				   javax.swing.border.TitledBorder.DEFAULT_POSITION, 
				   titledBorderTextFont, 
				   titledBorderTextColor);
	   serverMessagesScrollPane.setBorder(serverMessagesScrollPaneBorder);
	   splitPane2.setTopComponent(serverMessagesScrollPane);
	   
	   serverListScrollPane.setPreferredSize(new Dimension(300,200));
	   serverMessagesScrollPane.setPreferredSize(new Dimension(200,300));
	   
   }
   
   public ServerListTable getServerListTable() {
		return serverListTable;
   }

   public void setServerListTable(ServerListTable serverListTable) {
		this.serverListTable = serverListTable;
   }

   public MyInfoTable getMyInfoTable() {
		return myInfoTable;
   }

   public void setMyInfoTable(MyInfoTable myInfoTable) {
		this.myInfoTable = myInfoTable;
   }
   
   public static void main(String args[]) {
	   JFrame frame = new JFrame();
	   ServerListTab server_list_tab = new ServerListTab();
	   frame.setSize(600,900);
	   frame.add(server_list_tab);
	   frame.setVisible(true);
   }
   
}