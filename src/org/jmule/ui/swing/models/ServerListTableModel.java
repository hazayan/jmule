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

import java.util.ArrayList;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:59 $$
 */
public class ServerListTableModel extends JMTableModel {
	  
	  public final static int SERVER_ICON = 0;
	  public final static int SERVER_NAME = 1;
	  public final static int SERVER_IP = 2;
	  public final static int DESCRIPTION = 3;
	  public final static int PING = 4;
	  public final static int USERS = 5;
	  public final static int MAX_USERS = 6;
	  public final static int FILES = 7;
	  public final static int SOFT_LIMIT = 8;
	  public final static int HARD_LIMIT = 9;
	  
	  //TODO get the strings from Localizer
	  private final static String[] column_names = {
		                        " ",
		                        "Name",
		                        "IP",
		                        "Description",
		                        "Ping",
		                        "Users",
		                        "Max users",
		                        "Files",
		                        "Soft limit",
		                        "Hard limit"
	                          };
	  
	  private ArrayList server_list_rows;
	  	
	  public Class getColumnClass(int col) {
		  
			switch (col) {
			  case 0 : return String.class;
			  case 1 : return String.class;
			  case 2 : return String.class;
			  case 3 : return String.class;
			  case 4 : return Integer.class;
			  case 5 : return Integer.class;
			  case 6 : return Integer.class;
			  case 7 : return Integer.class;
			  case 8 : return Integer.class;
			  case 9 : return Integer.class;
			  default : return Object.class;
			}
	  }
	  
	  public int getColumnCount() {
		  
		  return column_names.length;
	  }

	  public int getRowCount() {
		  return 10;
	  }

	  public Object getValueAt(int row, int col) {
		  return 2 + row + col;
	  }
		
	  public String getColumnName(int col) {
		  return column_names[col];
	  }
	  
      public boolean isCellEditable(int row, int col) {
         return false;
      }
		
}
