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
package org.jmule.ui.swt.maintabs.serverlist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.jmule.core.JMuleCore;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:11 $$
 */
public class ServerListTab extends AbstractTab {

	private Composite server_info_panel;
	private Composite server_table_panel;
	
	private ServerMessages server_messages_text;
	
	private ServerList server_list;
	
	public ServerListTab(Composite shell,JMuleCore _core) {
		
		super(shell);

		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		FormLayout form = new FormLayout ();
		setLayout (form);
		
		server_table_panel = new Composite(this,SWT.BORDER);
		server_table_panel.setLayout(new FillLayout());
		
		server_info_panel = new Composite(this,SWT.BORDER);
		
		
		SashControl.createHorizontalSash(20, 50, this, server_table_panel, server_info_panel);
		
		server_info_panel.setLayout(new FormLayout());
		
		Composite server_messages = new Composite(server_info_panel,SWT.NONE);
		server_messages.setLayout(new FillLayout());
		
		Composite peer_info = new Composite(server_info_panel,SWT.NONE);
		peer_info.setLayout(new FillLayout());
		
		SashControl.createVerticalSash(20, 70, server_info_panel, server_messages, peer_info);

		// Server List
		server_list = new ServerList(server_table_panel,_core.getServersManager());
		
		//Server Messages
		server_messages_text = new ServerMessages(server_messages);
		
		ConnectionInfo myInfo = new ConnectionInfo(peer_info,_core);
		
		myInfo.setFont(skin.getDefaultFont());
	}

	public int getTabType() {
		return TAB_SERVERLIST;
	}

	public void lostFocus() {
		server_list.lostFocus();
	}

	public void obtainFocus() {
		server_list.obtainFocus();
	}

	public void disposeTab() {
		
	}

}
