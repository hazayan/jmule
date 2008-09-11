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
package org.jmule.ui.swt.aboutwindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMThread;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.UIConstants;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 22, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/11 18:31:37 $
 */
public class AboutWindow implements JMuleUIComponent {
	
	private int TOP_GRADIENT_HEIGHT = 80;
	private Color GRADIENT_COLOR_1 = SWTThread.getDisplay().getSystemColor (SWT.COLOR_WHITE);
	private Color GRADIENT_COLOR_2 = new Color(SWTThread.getDisplay(),255,141,5);
	
	private final String logo_image = "jmule_logo.png";
	
	private Shell shell;
	private Display display;
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		display = SWTThread.getDisplay();
		
		final Shell shell1=new Shell(display,SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		
		Listener link_listener = new Listener() {
			public void handleEvent(Event arg0) {
				if (!Program.launch(arg0.text)) 
					Utils.showWarningMessage(shell, _._("aboutwindow.error_open_url.title")
							, Localizer._("aboutwindow.error_open_url",arg0.text));
			}
		};
		
		GridData layout_data;
		GridLayout layout;
		Composite content;
		
		layout = new GridLayout(1,false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		
		shell.setLayout(layout);
		
		// Top gradient
		final Canvas window_top = new Canvas(shell,SWT.NONE);
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.heightHint = TOP_GRADIENT_HEIGHT;
		window_top.setLayoutData(layout_data);
		window_top.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				Rectangle rect = shell.getClientArea ();
				GC gc = arg0.gc;
				gc.setForeground (GRADIENT_COLOR_2);
				gc.setBackground (GRADIENT_COLOR_1);
				gc.fillGradientRectangle (rect.x, rect.y, rect.width, rect.height, false);
				Image image = SWTImageRepository.getImage(logo_image);
				gc.drawImage(image, rect.width - image.getImageData().width - 10, 5);
				
				gc.setForeground(new Color(SWTThread.getDisplay(),0,0,0));
				Font font = new Font(display,"Arial",14,SWT.BOLD ); 
				gc.setFont(font);
				gc.drawText(JMConstants.JMULE_NAME, 20, TOP_GRADIENT_HEIGHT / 2 - 15,true);
				
				gc.setForeground(new Color(SWTThread.getDisplay(),0,0,0));
				font = new Font(display,"Arial",10,SWT.NONE ); 
				gc.setFont(font);
				gc.drawText("Version : " + JMConstants.JMULE_VERSION, 17, TOP_GRADIENT_HEIGHT / 2 + 5,true);
			}
		});
		
		Composite window_content = new Composite(shell,SWT.NONE);
		window_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		FillLayout fill_layout = new FillLayout();
		fill_layout.marginHeight = 0;
		fill_layout.marginWidth = 0;
		window_content.setLayout(fill_layout);
		
		CTabFolder tab_list = new CTabFolder(window_content, SWT.BORDER);
		tab_list.setLayout(new FillLayout());
		tab_list.setSimple(false);
		tab_list.setUnselectedImageVisible(false);
		tab_list.setUnselectedCloseVisible(false);
		
		CTabItem general_tab = new CTabItem(tab_list,SWT.NONE);
		content = new Composite(tab_list,SWT.NONE);
		general_tab.setControl(content);
		general_tab.setText(_._("aboutwindow.tab.general"));
		
		content.setLayout(new GridLayout(1,false));

		if (JMConstants.IS_NIGHTLY_BUILD) {
			Link nightly_build_link = new Link(content,SWT.NONE);
			nightly_build_link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
			layout_data = new GridData(GridData.FILL_HORIZONTAL);
			layout_data.horizontalAlignment = GridData.CENTER;
			nightly_build_link.setLayoutData(layout_data);
			nightly_build_link.setText(_._("aboutwindow.tab.general.label.nightly_buld_str1"));
			
			nightly_build_link = new Link(content,SWT.NONE);
			nightly_build_link.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED));
			layout_data = new GridData(GridData.FILL_HORIZONTAL );
			layout_data.horizontalAlignment = GridData.CENTER;
			nightly_build_link.setLayoutData(layout_data);
			nightly_build_link.setText(_._("aboutwindow.tab.general.label.nightly_buld_str2") + " <a href=\""+JMConstants.JMULE_FORUMS+"\">"+_._("aboutwindow.tab.general.label.forum")+"</a>");
			nightly_build_link.addListener(SWT.Selection, link_listener);
		}
		
		Label about_jmule1 = new Label(content,SWT.NONE);
		about_jmule1.setText(_._("aboutwindow.tab.general.label.about1"));
		layout_data = new GridData(GridData.FILL_HORIZONTAL);
		layout_data.horizontalAlignment = GridData.CENTER;
		about_jmule1.setLayoutData(layout_data);
		
		Label about_jmule2 = new Label(content,SWT.NONE);
		about_jmule2.setText(_._("aboutwindow.tab.general.label.about2"));
		layout_data = new GridData(GridData.FILL_BOTH);
		layout_data.horizontalAlignment = GridData.CENTER;
		about_jmule2.setLayoutData(layout_data);
		
		Group internet_group = new Group(content,SWT.NONE);
		internet_group.setText(_._("aboutwindow.tab.general.group.internet"));
		internet_group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		
		internet_group.setLayout(layout);
		
		
		
		Link link;
		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.JMULE_WEB_SITE+"\">"+_._("aboutwindow.tab.general.label.home_page")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);
		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.JMULE_FORUMS+"\">"+_._("aboutwindow.tab.general.label.forum")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);

		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.ONLINE_HELP_WEB_SITE+"\">"+_._("aboutwindow.tab.general.label.online_help")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);
		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.SF_WEB_SITE+"\">"+_._("aboutwindow.tab.general.label.sf_page")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);
		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.JMULE_BUG_TRACKER+"\">"+_._("aboutwindow.tab.general.label.bugtracker")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);
		
		link = new Link(internet_group,SWT.NONE);
		link.setText("<a href=\""+JMConstants.JMULE_DOWNLOAD_PAGE+"\">"+_._("aboutwindow.tab.general.label.downloads")+"</a>");
		link.addListener(SWT.Selection, link_listener);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.CENTER;
		link.setLayoutData(gridData);
		
		CTabItem settings_tab = new CTabItem(tab_list,SWT.NONE);
		content = new Composite(tab_list,SWT.NONE);
		settings_tab.setControl(content);
		settings_tab.setText(_._("aboutwindow.tab.license"));
		
		content.setLayout(new FillLayout());
		
		Text license_text = new Text(content,SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);
	    license_text.setText(UIConstants.GNU_LICENSE);
        
		tab_list.setSelection(general_tab);
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		layout = new GridLayout(1,false);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		button_bar.setLayout(layout);
		
		Button button_ok = new Button(button_bar,SWT.NONE);
		button_ok.setFont(skin.getButtonFont());
		button_ok.setText(_._("aboutwindow.button.ok"));
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button_ok.setFocus();
		button_ok.setLayoutData(grid_data);
		
		button_ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		
		shell.setText(Localizer._("aboutwindow.title"));
		shell.setImage(SWTImageRepository.getImage("jmule.png"));
		shell.setSize(500,400);
		Utils.centreWindow(shell);
		if (JMConstants.isLinux)
			shell.setAlpha(0);
		else
			shell.setAlpha(100);
		shell.open();
		
		// Mega cool show effect
		if (JMConstants.isLinux) {
			new JMThread(new JMRunnable() {
					int i;
					public void JMRun() {
						for(i = 0; i<255;i++ ) {
						display.syncExec(new JMRunnable() {
							public void JMRun() {
								shell.setAlpha(i);
							}
						});
						}
					}
				}).start();
		}


	}

}
