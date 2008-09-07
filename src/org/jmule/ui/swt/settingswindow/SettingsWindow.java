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

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 19, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 15:23:26 $
 */
public class SettingsWindow implements JMuleUIComponent {

	private JMuleCore _core;
	private Shell shell;
	private ScrolledComposite settings_tab_panel;
	private AbstractTab settings_tab;
	
	public void getCoreComponents() {
		_core = JMuleCoreFactory.getSingleton();
	}

	public void initUIComponents() {
		JMuleUI ui_instance = null;
		try {
		    ui_instance = JMuleUIManager.getJMuleUI();
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );

		shell.setSize(400, 520);
		
		Utils.centreWindow(shell);
		
		shell.setText(_._("settingswindow.title"));
		shell.setImage(SWTImageRepository.getImage("properties.png"));
		
		shell.setLayout(new GridLayout(1,false));
		
		Composite window_content = new Composite(shell,SWT.NONE);
		window_content.setLayout(new GridLayout(1,false));
		window_content.setLayoutData(new GridData(GridData.FILL_BOTH));

		settings_tab_panel = new ScrolledComposite(window_content,SWT.NONE);
		settings_tab_panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		settings_tab_panel.setExpandHorizontal(true);
		settings_tab_panel.setExpandVertical(true);
		settings_tab_panel.setLayout(new FillLayout());
		
		settings_tab = new GeneralTab(settings_tab_panel,_core);
	
		settings_tab_panel.setContent(settings_tab);
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout = new GridLayout(3,false);
		button_bar.setLayout(layout);
		
		Button button_ok = new Button(button_bar,SWT.NONE);
		button_ok.setFont(skin.getButtonFont());
		button_ok.setText(_._("settingswindow.button.ok"));
		button_ok.setImage(SWTImageRepository.getImage("ok.png"));
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setLayoutData(grid_data);
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (apply())
					shell.close();
			}
		});
		
		Button button_cancel = new Button(button_bar,SWT.NONE);
		button_cancel.setFont(skin.getButtonFont());
		button_cancel.setText(_._("settingswindow.button.cancel"));
		button_cancel.setImage(SWTImageRepository.getImage("cancel.png"));
		grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 80;
		button_cancel.setLayoutData(grid_data);
		button_cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		
		Button button_apply = new Button(button_bar,SWT.NONE);
		button_apply.setFont(skin.getButtonFont());
		button_apply.setText(_._("settingswindow.button.apply"));
		button_apply.setImage(SWTImageRepository.getImage("accept.png"));
		grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 80;
		button_apply.setLayoutData(grid_data);
		
		button_apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				apply();
			}
		});
		shell.pack();
		shell.open();
	}

	private boolean apply() {
		if (!settings_tab.checkFields()) return false;
		settings_tab.save();
		return true;
	}
	
}
