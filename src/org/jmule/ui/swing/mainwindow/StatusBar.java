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
package org.jmule.ui.swing.mainwindow;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.jmule.core.edonkey.ServerListener;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.swing.UISwingImageRepository;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:20 $$
 */
public class StatusBar extends JToolBar implements ServerListener {

	private JLabel connection_status,
                   connected_server_ip,
                   peer_id_type,
                   peer_id,
                   down_speed_label,
                   down_speed,
                   up_speed_label,
                   up_speed;
	
	public StatusBar() {
        
		this.setPreferredSize(new java.awt.Dimension(425, 23));
		this.setFloatable( false );
		this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED)); 
		connection_status = new JLabel("Connected ", 
				                        UISwingImageRepository.getIcon("mini_globe.png"),
				                        JLabel.LEFT);
		this.add( connection_status );
		connected_server_ip = new JLabel("192.30.2.32:6554 ");
		this.add( connected_server_ip );
		peer_id_type = new JLabel("High ");
		this.add( peer_id_type );
		JLabel peer_id_label = new JLabel("ID=");
		this.add( peer_id_label );
		peer_id = new JLabel("4385495");
		this.add( peer_id );
		this.add( new JPanel() );
		
		down_speed_label = new JLabel(" [10Kb/s]",
				                       UISwingImageRepository.getIcon("down.gif"),
				                       JLabel.LEFT);
		this.add( down_speed_label );
        down_speed = new JLabel(" 8Kb/s ");
		this.add( down_speed );
		up_speed_label = new JLabel(" [1Mb/s]",
				                    UISwingImageRepository.getIcon("up.gif"),
                                    JLabel.LEFT);
		this.add( up_speed_label );
		up_speed = new JLabel(" 450Kb/s");
		this.add( up_speed );
		

	}

	public void connected(Server server) {
		
		final Server s = server;
		
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
		           
		    	   connected_server_ip.setText(""+s.getInetAddress() + ":" + s.getPort());
		    	
		    }
		} );
	}

	public void disconnected(Server server) {
		
		final Server s = server;
		
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
		           
		    	   connected_server_ip.setText("Disconnected");
		    	
		    }
		} );
		
	}

	public void isconnecting(Server server) {
		
		final Server s = server;
		
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
		           
		    	   connected_server_ip.setText("Connecting to " + s.getInetAddress() + ":" + s.getPort());
		    	
		    }
		} );
		
	}

	public void serverMessage(Server server, String message) {
		// TODO Auto-generated method stub
		
	}

}
