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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:10 $$
 */
public class ServerPropertiesWindow implements JMuleUIComponent {

	private Shell shell;
	
	private Label label_name,label_desc,label_users,label_files,label_ping,label_soft_limit,label_hard_limit,label_version;
	private Server server;
	private WindowUpdater updater;
	
	public ServerPropertiesWindow(Server server) {
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		this.server = server;
		
		final Shell shell1=new Shell(Utils.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		shell.setImage(new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("server_properties.png")));
		shell.setSize(500, 310);
		shell.setText(Localizer._("serverpropertieswindow.title"));
	
		Utils.centreWindow(shell);
		
		shell.setLayout(new GridLayout(1,false));
			
		Composite server_fields = new Composite(shell,SWT.BORDER);
		server_fields.setLayout(new GridLayout(2,false));
		server_fields.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_BOTH));

		Label label;
		
		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_address")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(server.getAddress()+":"+server.getPort());

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_name"));
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label_name = new Label(server_fields,SWT.NONE);
		label_name.setFont(skin.getLabelFont());
		label_name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_description") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label_desc = new Label(server_fields,SWT.NONE);
		label_desc.setFont(skin.getDefaultFont());
		label_desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_users") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	
		label_users = new Label(server_fields,SWT.NONE);
		label_users.setFont(skin.getDefaultFont());
		label_users.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_files") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		
		label_files = new Label(server_fields,SWT.NONE);
		label_files.setFont(skin.getDefaultFont());
		label_files.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_ping") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label_ping = new Label(server_fields,SWT.NONE);
		label_ping.setFont(skin.getDefaultFont());
		label_ping.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_hard_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label_hard_limit = new Label(server_fields,SWT.NONE);
		label_hard_limit.setFont(skin.getDefaultFont());
		label_hard_limit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.server_soft_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label_soft_limit = new Label(server_fields,SWT.NONE);
		label_soft_limit.setFont(skin.getDefaultFont());
		label_soft_limit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		label = new Label(server_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("serverpropertieswindow.version") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	
		label_version = new Label(server_fields,SWT.NONE);
		label_version.setFont(skin.getDefaultFont());
		label_version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updater = new WindowUpdater();
		GUIUpdater.getInstance().addRefreshable(updater);
		updater.refresh();
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout = new GridLayout(1,false);
		button_bar.setLayout(layout);

		Button button = new Button(button_bar,SWT.NONE);
		button.setFont(skin.getButtonFont());
		button.setText(Localizer._("serverpropertieswindow.close"));
		
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button.setLayoutData(grid_data);

		button.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(final SelectionEvent e) {
				GUIUpdater.getInstance().removeRefreshable(updater);
				shell.close();
			}
		
		});
	}
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		shell.open();
		while (!shell.isDisposed())
		      if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
	}
		
	private class WindowUpdater implements Refreshable {

		public void refresh() {
			label_version.setText(server.getVersion()+"");
			label_soft_limit.setText(server.getSoftLimit()+"");
			label_hard_limit.setText(server.getHardLimit()+"");
			label_ping.setText(server.getPing()+"");
			label_files.setText(server.getNumFiles()+"");
			label_users.setText(server.getNumUsers()+"");
			label_desc.setText(server.getDesc());
			label_name.setText(server.getName());
		}
		
	}
	
}

