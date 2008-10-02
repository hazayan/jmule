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
package org.jmule.ui.swt.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.JMConstants;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.utils.NightlyBuildWarning;

/**
 * Created on Sep 19, 2008
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/02 08:00:11 $
 */
public class NightlyBuildWarningWindow implements JMuleUIComponent {

	private Shell shell;
	
	public void getCoreComponents() {
	}

	public void initUIComponents() {
		SWTSkin skin = null;
		try {
			skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {}
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.BORDER );
		shell.setText(_._("nightlybuildwarningwindow.title"));
		shell.setImage(SWTThread.getDisplay().getSystemImage(SWT.COLOR_WHITE));
		shell.setSize(400,320);
		
		GridData grid_data;
		GridLayout grid_layout = new GridLayout(1,false);
		grid_layout.marginWidth = 0;
		grid_layout.marginHeight = 0;
		
		shell.setLayout(grid_layout);
		
		final Label image = new Label(shell,SWT.NONE);
		image.setImage(SWTImageRepository.getImage("bomb.png"));
		grid_data = new GridData();
		grid_data.grabExcessHorizontalSpace = true;
		grid_data.horizontalAlignment = GridData.CENTER;
		image.setLayoutData(grid_data);
		
		Label window_message = new Label(shell,SWT.NONE);
		window_message.setFont(skin.getLabelFont());
		grid_data = new GridData();
		grid_data.grabExcessHorizontalSpace = true;
		grid_data.horizontalAlignment = GridData.CENTER;
		window_message.setLayoutData(grid_data);
		window_message.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
		window_message.setText(_._("nightlybuildwarningwindow.label.message1"));
		
		Listener link_listener = new Listener() {
			public void handleEvent(Event arg0) {
				if (!Program.launch(arg0.text)) 
					Utils.showWarningMessage(shell, _._("nightlybuildwarningwindow.error_open_url.title")
							, Localizer._("nightlybuildwarningwindow.error_open_url",arg0.text));
			}
		};
		
		Composite container = new Composite(shell,SWT.NONE);
		container.setLayout(new GridLayout(2,false));
		container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Label window_message2 = new Label(container,SWT.NONE);
		window_message2.setFont(skin.getLabelFont());
		grid_data = new GridData();
		grid_data.grabExcessHorizontalSpace = true;
		grid_data.horizontalAlignment = GridData.CENTER;
		window_message2.setLayoutData(grid_data);
		window_message2.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
		window_message2.setText(_._("nightlybuildwarningwindow.label.message2"));
		
		Link link = new Link(container,SWT.NONE);
		link.setFont(skin.getLabelFont());
		link.setText("<a href=\""+JMConstants.JMULE_FORUMS+"\">"+_._("nightlybuildwarningwindow.label.forum")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		
		Composite controls_composite = new Composite(shell,SWT.NONE);
		grid_data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END);
		grid_data.grabExcessHorizontalSpace = true;
		grid_data.grabExcessVerticalSpace = true;
		
		controls_composite.setLayoutData(grid_data);
		
		controls_composite.setLayout(new GridLayout(2,false));
		
		final Button button_check = new Button(controls_composite,SWT.CHECK);
		grid_data = new GridData();
		button_check.setLayoutData(grid_data);
		button_check.setText(_._("nightlybuildwarningwindow.label.show_at_startup"));
		button_check.setSelection(NightlyBuildWarning.showWarning());
		
		Button button = new Button(controls_composite,SWT.PUSH);
		grid_data = new GridData();
	
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		grid_data.horizontalAlignment = GridData.END;
		button.setLayoutData(grid_data);
		button.forceFocus();
		button.setText(_._("nightlybuildwarningwindow.button.ok"));
		button.setImage(skin.getButtonImage(SWTSkin.OK_BUTTON_IMAGE));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				NightlyBuildWarning.setShowWarning(button_check.getSelection());
				shell.close();
			}
		});
		
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				image.getImage().dispose();
			}
		});
		
		Utils.centreWindow(shell);
		shell.open();
		
	}

}
