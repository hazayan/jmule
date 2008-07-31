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
package org.jmule.ui.swing.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:45:19 $$
 */
public class ServerEditDialog extends JDialog {

	private JLabel server_ip_label;
	private JTextField ip1;
	private JButton cancel;
	private JButton ok;
	private JLabel server_port_label;
	private JTextField server_port;
	private JTextField ip4;
	private JTextField ip3;
	private JTextField ip2;
	
	public ServerEditDialog(JFrame jf) {
		super(jf);
		this.init();
	}
	
    private void init() {
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setModal(true);
		this.setTitle("Edit EDonkey server");
		getContentPane().setLayout(null);
		{
			ip1 = new JTextField();
			getContentPane().add(ip1);
			ip1.setBounds(74, 38, 30, 22);
			ip1.setName("ip1");
		}
		{
			ip2 = new JTextField();
			getContentPane().add(ip2);
			ip2.setName("ip2");
			ip2.setBounds(110, 38, 30, 22);
		}
		{
			ip3 = new JTextField();
			getContentPane().add(ip3);
			ip3.setName("ip3");
			ip3.setBounds(146, 38, 30, 22);
		}
		{
			ip4 = new JTextField();
			getContentPane().add(ip4);
			ip4.setName("ip4");
			ip4.setBounds(182, 38, 30, 22);
		}
		{
			server_port_label = new JLabel();
			getContentPane().add(server_port_label);
			server_port_label.setBounds(28, 71, 34, 22);
			server_port_label.setName("server_port_label");
			server_port_label.setText("Port");
		}
		{
			server_ip_label = new JLabel();
			getContentPane().add(server_ip_label);
			server_ip_label.setBounds(28, 40, 34, 19);
			server_ip_label.setName("server_ip_label");
			server_ip_label.setText("IP");
		}
		{
			server_port = new JTextField();
			getContentPane().add(server_port);
			server_port.setBounds(74, 72, 66, 22);
		}
		{
			ok = new JButton();
			getContentPane().add(ok);
			ok.setBounds(28, 134, 65, 30);
			ok.setName("ok");
			ok.setText("Ok");
		}
		{
			cancel = new JButton();
			getContentPane().add(cancel);
			cancel.setBounds(128, 134, 85, 30);
			cancel.setName("cancel");
			cancel.setText("Cancel");
		}
	    this.setSize(234, 205);
    }
    
	
	public static void main(String args[]) {
		JFrame jf = new JFrame();
		jf.setSize(400, 500);
		jf.setVisible(true);
		
		ServerEditDialog se = new ServerEditDialog(jf);
		
		se.setVisible(true);
		

		
	}
	
}
