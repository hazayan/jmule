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
package org.jmule.ui.swt.settingswindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;

/**
 * Created on Aug 19, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 15:23:25 $
 */
public class GeneralTab extends AbstractTab {

	private Text nick_name_text;
	private Button prompt_on_exit_check, server_list_update;
	
	private ConfigurationManager config_manager;
	private SWTPreferences swt_preferences;
	
	private int EDIT_FIELD_WIDTH = 60;
	
	private Spinner tcp_port, udp_port;
	private Button enable_udp, kbit_button, kbyte_button, enable_download_limit, enable_upload_limit, startup_update_check;
	private Text download_limit, upload_limit, download_capacity, upload_capacity;
	
	private boolean kbyte_selected = true;
	
	public GeneralTab(Composite parent, JMuleCore core) {
		super(parent, core);
	
		Listener number_filter = new Listener() {
			public void handleEvent(Event e) {
		        String text = e.text;

		        char[] chars = new char[text.length()];
		        text.getChars(0, chars.length, chars, 0);
		        for (int i = 0; i < chars.length; i++) {
		          if (!('0' <= chars[i] && chars[i] <= '9')) {
			            e.doit = false;
			            return;
		          }
				}
			}
		};
		
		GridData layout_data;
		GridLayout layout;
		Composite container;
		
		config_manager = _core.getConfigurationManager();
		swt_preferences = SWTPreferences.getInstance();
		
		content.setLayout(new GridLayout(2,false));
		Label label;
		
		label = new Label(content,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("settingswindow.tab.general.label.nickname") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		nick_name_text = new Text(content,SWT.BORDER);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		nick_name_text.setLayoutData(layout_data);
		
		nick_name_text.setText(_core.getConfigurationManager().getNickName());
		
		prompt_on_exit_check = new Button(content,SWT.CHECK);
		prompt_on_exit_check.setText(_._("settingswindow.tab.general.checkbox.prompt_on_exit"));
		prompt_on_exit_check.setSelection(swt_preferences.promptOnExit());
		
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		prompt_on_exit_check.setLayoutData(layout_data);
		
		server_list_update = new Button(content,SWT.CHECK);
		server_list_update.setText(_._("settingswindow.tab.general.checkbox.update_server_list"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		server_list_update.setLayoutData(layout_data);
		boolean update = config_manager.getBooleanParameter(ConfigurationManager.SERVER_LIST_UPDATE_ON_CONNECT_KEY, false);
		server_list_update.setSelection(update);

		startup_update_check = new Button(content,SWT.CHECK);
		startup_update_check.setText(_._("settingswindow.tab.general.checkbox.startup_update_check"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		startup_update_check.setLayoutData(layout_data);
		startup_update_check.setSelection(swt_preferences.updateCheckAtStartup());
		
		Group ports = new Group(content,SWT.NONE);
		ports.setText(_._("settingswindow.tab.general.group.ports"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		ports.setLayoutData(layout_data);
		
		ports.setLayout(new GridLayout(2,false));
		
		label = new Label(ports,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.tcp_port") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		tcp_port = new Spinner (ports, SWT.BORDER);
		tcp_port.setMinimum(1);
		tcp_port.setMaximum(65535);
		tcp_port.setSelection(config_manager.getTCP());
		tcp_port.setIncrement(1);
		tcp_port.setPageIncrement(100);

		label = new Label(ports,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.udp_port") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container1 = new Composite(ports,SWT.NONE);
		container1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		layout = new GridLayout(2,false);
		layout.marginWidth=0;
		layout.marginHeight=0;
		container1.setLayout(layout);
		
		udp_port = new Spinner (container1, SWT.BORDER);
		udp_port.setMinimum(1);
		udp_port.setMaximum(65535);
		udp_port.setSelection(config_manager.getUDP());
		udp_port.setIncrement(1);
		udp_port.setPageIncrement(100);
		
		enable_udp = new Button(container1,SWT.CHECK);
		enable_udp.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_udp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateUDPControls();
			}
			
		});
		enable_udp.setSelection(config_manager.isUDPEnabled());
		
		updateUDPControls();
		
		Group limits = new Group(content,SWT.NONE);
		limits.setText(_._("settingswindow.tab.general.group.limits"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		limits.setLayoutData(layout_data);
		limits.setLayout(new GridLayout(2,false));
		
		label = new Label(limits,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.download_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container2 = new Composite(limits,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		container2.setLayoutData(layout_data);
		layout = new GridLayout(3,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container2.setLayout(layout);
		
		download_limit = new Text(container2,SWT.BORDER );
		download_limit.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		download_limit.setLayoutData(layout_data);
		download_limit.setText((config_manager.getDownloadLimit()/1024)+"");
		
		new Label(container2,SWT.NONE).setText(_._("settingswindow.tab.connection.label.kb_s"));
		
		enable_download_limit = new Button(container2,SWT.CHECK);
		enable_download_limit.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_download_limit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateDownloadLimitControls();
			}
		});
		
		label = new Label(limits,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.upload_limit") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Composite container3 = new Composite(limits,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		container3.setLayoutData(layout_data);
		layout = new GridLayout(3,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container3.setLayout(layout);
		
		upload_limit = new Text(container3,SWT.BORDER );
		upload_limit.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		upload_limit.setLayoutData(layout_data);
		upload_limit.setText((config_manager.getUploadLimit()/1024)+"");
		
		new Label(container3,SWT.NONE).setText(_._("settingswindow.tab.connection.label.kb_s"));
		
		enable_upload_limit = new Button(container3,SWT.CHECK);
		enable_upload_limit.setText(_._("settingswindow.tab.connection.button.enabled"));
		enable_upload_limit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				updateUploadLimitControls();
			}
		});
		
		boolean enable;
		enable = config_manager.getDownloadLimit()==0 ? false : true;
		enable_download_limit.setSelection(enable);
		
		enable = config_manager.getUploadLimit() == 0 ? false : true;
		enable_upload_limit.setSelection(enable);
		
		updateDownloadLimitControls();
		updateUploadLimitControls();
		
		Group capacities = new Group(content,SWT.NONE);
		capacities.setText(_._("settingswindow.tab.general.group.capacities"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalSpan = 2;
		capacities.setLayoutData(layout_data);
		capacities.setLayout(new GridLayout(2,false));
		
		label = new Label(capacities,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.download_capacity") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		
		download_capacity = new Text(capacities,SWT.BORDER );
		download_capacity.addListener(SWT.Verify, number_filter);
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		download_capacity.setLayoutData(layout_data);
		
		label = new Label(capacities,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setForeground(skin.getDefaultColor());
		label.setText(_._("settingswindow.tab.connection.label.upload_capacity") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		upload_capacity = new Text(capacities,SWT.BORDER );
		upload_capacity.addListener(SWT.Verify, number_filter);
		
		layout_data = new GridData();
		layout_data.widthHint = EDIT_FIELD_WIDTH;
		upload_capacity.setLayoutData(layout_data);
		
		new Label(capacities,SWT.NONE);
		
		container = new Composite(capacities,SWT.NONE);
		layout_data = new GridData();

		container.setLayoutData(layout_data);
		layout = new GridLayout(2,false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		
		kbit_button = new Button(container,SWT.RADIO);
		kbit_button.setText(_._("settingswindow.tab.connection.button.k_bit"));
		
		kbit_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (kbit_button.getSelection()) {
					if (!kbyte_selected) return ;
					kbyte_selected = false;
					long download = Long.parseLong(download_capacity.getText());
					long upload = Long.parseLong(upload_capacity.getText());
					download*=8;
					upload*=8;
					
					download_capacity.setText(download+"");
					upload_capacity.setText(upload+"");
				}
			}
		});
		
		kbyte_button = new Button(container,SWT.RADIO);
		kbyte_button.setText(_._("settingswindow.tab.connection.button.k_byte"));
		kbyte_button.setSelection(true);
		
		kbyte_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (kbyte_button.getSelection()) {
					if (kbyte_selected) return ;
					kbyte_selected = true;
					long download = Long.parseLong(download_capacity.getText());
					long upload = Long.parseLong(upload_capacity.getText());
					download/=8;
					upload/=8;
					
					download_capacity.setText(download+"");
					upload_capacity.setText(upload+"");
				}
			}
		});
		
		download_capacity.setText((config_manager.getDownloadBandwidth()/1024)+"");
		upload_capacity.setText((config_manager.getUploadBandwidth()/1024)+"");

		
	}

	public Image getImage() {
		return SWTImageRepository.getImage("general.png");
	}


	public String getName() {
		return _._("settingswindow.tab.general.name");
	}

	public boolean checkFields() {
		String nickname = nick_name_text.getText();
		nickname = nickname.trim();
		if (nickname.length()==0) {
			nickname = ConfigurationManager.NICK_NAME;
			nick_name_text.setText(nickname);
		}
			
		if (download_limit.getText().length()==0)
			download_limit.setText("0");
		if (upload_limit.getText().length()==0)
			upload_limit.setText("0");

		
		if (download_capacity.getText().length()==0)
			download_capacity.setText("0");
		
		if (upload_capacity.getText().length()==0) 
			upload_capacity.setText("0");
		
		long download_c = Long.parseLong(download_capacity.getText());
		long upload_c = Long.parseLong(download_capacity.getText());
		if (kbit_button.getSelection()) {
			download_c/=8;
			upload_c/=8;
		}
		
		
		if (download_c==0) {
			download_capacity.setText(config_manager.getDownloadBandwidth()+"");
			kbyte_selected = true;
			kbyte_button.setSelection(true);
			kbit_button.setSelection(false);
		}
		if (upload_c==0) {
			upload_capacity.setText(config_manager.getUploadBandwidth()+"");
			kbyte_selected = true;
			kbyte_button.setSelection(true);
			kbit_button.setSelection(false);
		}
			
		return true;
	}

	public void save() {
		config_manager.setNickName(nick_name_text.getText());
		config_manager.setParameter(ConfigurationManager.SERVER_LIST_UPDATE_ON_CONNECT_KEY, server_list_update.getSelection());
		
		swt_preferences.setPromprtOnExit(prompt_on_exit_check.getSelection());
		swt_preferences.setUpdateCheckAtStartup(startup_update_check.getSelection());
		
		int tcp = tcp_port.getSelection();
		if (config_manager.getTCP() != tcp)
			config_manager.setTCP(tcp);
		int udp = udp_port.getSelection();
		if (config_manager.getUDP()!=udp)
			config_manager.setUDP(udp);
		boolean udp_status = enable_udp.getSelection();
		if (config_manager.isUDPEnabled() != udp_status)
			config_manager.setUDPEnabled(udp_status);
		long download_l = enable_download_limit.getSelection() ? Long.parseLong(download_limit.getText()) : 0;
		config_manager.setDownloadLimit(download_l*1024);
		long upload_l   = enable_upload_limit.getSelection() ? Long.parseLong(upload_limit.getText()) : 0;
		config_manager.setUploadLimit(upload_l * 1024);
		long download_c = Long.parseLong(download_capacity.getText());
		long upload_c = Long.parseLong(download_capacity.getText());
		if (kbit_button.getSelection()) {
			download_c/=8;
			upload_c/=8;
		}

		config_manager.setDownloadBandwidth(download_c*1024);
		config_manager.setUploadBandwidth(upload_c*1024);
	}

	private void updateUDPControls() {
		boolean status = enable_udp.getSelection();
		udp_port.setEnabled(status);
	}
	
	private void updateDownloadLimitControls() {
		boolean status = enable_download_limit.getSelection();
		download_limit.setEnabled(status);
	}
	
	private void updateUploadLimitControls() {
		boolean status = enable_upload_limit.getSelection();
		upload_limit.setEnabled(status);
	}
	
}