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
package org.jmule.ui.swt.mainwindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jmule.core.edonkey.impl.ClientID;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.maintabs.serverlist.SWTServerListWrapper;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.util.Convert;

/**
 * 
 * @author binary
 * @version $Revision: 1.1 $
 * Last changed by $Author: javajox $ on $Date: 2008/07/31 16:44:50 $
 */
public class StatusBar extends Composite {

	private GridData grid_data;
	
	private Label img_label,connection_status_label,client_id_label,downimg_label,downspeed_label,upimg_label,upspeed_label;
	
	public StatusBar(Composite parent) {
		super(parent, SWT.NONE);
		SWTServerListWrapper.getInstance().setStatusBar(this);
		
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		grid_data = new GridData(GridData.FILL_HORIZONTAL);
		grid_data.heightHint = 16;
		
		setLayoutData(grid_data);

		GridLayout layout = new GridLayout(7,false);
		
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		img_label = new Label(this,SWT.NONE);
		Image img = new Image(this.getDisplay(), UIImageRepository.getImageAsStream("toolbar_disconnected.png"));
		img_label.setImage(img);
		
		connection_status_label = new Label(this,SWT.NONE);
		connection_status_label.setFont(skin.getLabelFont());
			
		client_id_label = new Label(this,SWT.NONE);
		client_id_label.setFont(skin.getLabelFont());
		client_id_label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		downimg_label = new Label(this,SWT.NONE);
		downimg_label.setImage(new Image(this.getDisplay(),UIImageRepository.getImageAsStream("down.gif")));
		downimg_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		downspeed_label = new Label(this,SWT.NONE);
		downspeed_label.setFont(skin.getLabelFont());
		downspeed_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		downspeed_label.setText("100 KB/S");
		upimg_label = new Label(this,SWT.NONE);
		upimg_label.setImage(new Image(this.getDisplay(),UIImageRepository.getImageAsStream("up.gif")));
		upimg_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		upspeed_label = new Label(this,SWT.NONE);
		upspeed_label.setFont(skin.getLabelFont());
		upspeed_label.setText("200 KB/S");
		upspeed_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		setStatusDisconnected();
		//setStatusConnected(new Server("1.1.1.1",123),new ClientID("123.0.1.0"));
	}
	

	public void setStatusDisconnected() {
		connection_status_label.setText(Localizer._("mainwindow.statusbar.connection_status_label.disconnected"));
		Image img = new Image(this.getDisplay(), UIImageRepository.getImageAsStream("toolbar_disconnected.png"));
		img_label.setImage(img);
		client_id_label.setForeground(Utils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		client_id_label.setText("");
		layout();
	}
	
	public void setStatusConnecting() {
		connection_status_label.setText(Localizer._("mainwindow.statusbar.connection_status_label.connecting"));
		Image img = new Image(this.getDisplay(), UIImageRepository.getImageAsStream("toolbar_disconnected.png"));
		img_label.setImage(img);
		client_id_label.setForeground(Utils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		client_id_label.setText("");
		layout();
	}
	
	public void setStatusConnected(Server server) {
		ClientID client_id = server.getClientID();
		connection_status_label.setText(Localizer._("mainwindow.statusbar.connection_status_label.connected"));
		connection_status_label.setToolTipText(server.getAddress() + ":" + server.getPort());
		client_id_label.setText(client_id.isHighID() ? Localizer._("mainwindow.statusbar.client_id_label.high_id") :Localizer._("mainwindow.statusbar.client_id_label.low_id"));
		if (!client_id.isHighID())
			client_id_label.setForeground(Utils.getDisplay().getSystemColor(SWT.COLOR_RED));
		else
			client_id_label.setForeground(Utils.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		long id = Convert.intToLong(client_id.hashCode());
		client_id_label.setToolTipText(id+"");
		Image img = new Image(Utils.getDisplay(), UIImageRepository.getImageAsStream("toolbar_connected.png"));
		img_label.setImage(img);
		layout();
	}
	
	public void toogleVisibility() {
		setVisible(!getVisible());
		SWTPreferences.getInstance().setStatusBarVisible(getVisible());
		grid_data.exclude = !grid_data.exclude;
		setLayoutData(grid_data);
		layout();
	}

	protected void checkSubclass() {
    }
	
}
