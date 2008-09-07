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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.JMConstants;
import org.jmule.core.JMRunnable;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.aboutwindow.AboutWindow;
import org.jmule.ui.swt.maintabs.AbstractTab.JMULE_TABS;
import org.jmule.ui.swt.serverlistimportwindow.ServerListImportWindow;
import org.jmule.ui.swt.settingswindow.SettingsWindow;
import org.jmule.ui.swt.updaterwindow.UpdaterWindow;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:52:19 $$
 */
public class MainMenu extends Menu{

	private Map<JMULE_TABS,MenuItem> tab_map = new HashMap<JMULE_TABS,MenuItem>();
	private MainWindow main_window;
	
	public MainMenu(Shell shell, MainWindow mainWindow) {
		super(shell, SWT.BAR);
		main_window = mainWindow;
		shell.setMenuBar(this);
		
		//File menu
		MenuItem fileItem = new MenuItem (this, SWT.CASCADE);
		fileItem.setText (Localizer._("mainwindow.mainmenu.file"));
		
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		
		MenuItem import_servers = new MenuItem (submenu, SWT.PUSH);
		import_servers.setText(Localizer._("mainwindow.mainmenu.file.import"));
		
		import_servers.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ServerListImportWindow window = new ServerListImportWindow();
				window.getCoreComponents();
				window.initUIComponents();
			}
			
		});
		
		new MenuItem (submenu, SWT.SEPARATOR);

		MenuItem exit_item = new MenuItem (submenu, SWT.PUSH);
		exit_item.setText(Localizer._("mainwindow.mainmenu.file.exit"));
		
		exit_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.close();
			}
			
		});
		
		// View menu
		MenuItem viewItem = new MenuItem (this, SWT.CASCADE);
		viewItem.setText (Localizer._("mainwindow.mainmenu.view"));
		
		submenu = new Menu (shell, SWT.DROP_DOWN);
		viewItem.setMenu (submenu);
		
		MenuItem tabs_item = new MenuItem (submenu, SWT.CASCADE);
		tabs_item.setText(Localizer._("mainwindow.mainmenu.view.tabs"));
		
		Menu tabs_menu = new Menu (submenu);
		tabs_item.setMenu(tabs_menu);
		
		MenuItem servers_item = new MenuItem (tabs_menu, SWT.RADIO);
		servers_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.servers"));
		tab_map.put(JMULE_TABS.SERVERLIST,servers_item);
		
		servers_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.SERVERLIST);
			}
		});
		
		MenuItem transfers_item = new MenuItem (tabs_menu, SWT.RADIO);
		transfers_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.transfers"));
		tab_map.put(JMULE_TABS.TRANSFERS,transfers_item);
		transfers_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.TRANSFERS);
			}
		});
		
		MenuItem search_item = new MenuItem (tabs_menu, SWT.RADIO);
		search_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.search"));
		tab_map.put(JMULE_TABS.SEARCH ,search_item);
		
		search_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.SEARCH);
			}
			
		});
		
		MenuItem shared_item = new MenuItem (tabs_menu, SWT.RADIO);
		shared_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.shared"));
		tab_map.put(JMULE_TABS.SHARED,shared_item);
		
		shared_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.SHARED);
			}
			
		});
		
		MenuItem stats_item = new MenuItem (tabs_menu, SWT.RADIO);
		stats_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.stats"));
		tab_map.put(JMULE_TABS.STATISTICS,stats_item);
		stats_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.STATISTICS);
			}
		});
		
		MenuItem log_item = new MenuItem (tabs_menu, SWT.RADIO);
		log_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.logs"));
		tab_map.put(JMULE_TABS.LOGS,log_item);
		log_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.setTab(JMULE_TABS.LOGS);
			}
		});
		
		MenuItem toolbar_item = new MenuItem (submenu, SWT.CHECK);
		if (!SWTPreferences.getInstance().isToolBarVisible())
			toolbar_item.setSelection(false);
		else
			toolbar_item.setSelection(true);
		toolbar_item.setText(Localizer._("mainwindow.mainmenu.view.toolbar"));
		toolbar_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.toolbarToogleVisibility();
			}
			
		});
		
		MenuItem status_item = new MenuItem (submenu, SWT.CHECK);
		if (!SWTPreferences.getInstance().isStatusBarVisible())
			status_item.setSelection(false);
		else
			status_item.setSelection(true);
		status_item.setText(Localizer._("mainwindow.mainmenu.view.statusbar"));
		status_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				main_window.statusBarToogleVisibility();
			}
		});
		
		//Tools menu
		MenuItem toolsItem = new MenuItem (this, SWT.CASCADE);
		toolsItem.setText (Localizer._("mainwindow.mainmenu.tools"));
		submenu = new Menu (shell, SWT.DROP_DOWN);
		toolsItem.setMenu (submenu);
		MenuItem gui_chooser_item = new MenuItem (submenu, SWT.PUSH);
		gui_chooser_item.setText(Localizer._("mainwindow.mainmenu.tools.uichooser"));
		
		MenuItem wizard_item = new MenuItem (submenu, SWT.PUSH);
		wizard_item.setText(Localizer._("mainwindow.mainmenu.tools.wizard"));
		
		new MenuItem (submenu, SWT.SEPARATOR);
		
		MenuItem options_item = new MenuItem (submenu, SWT.PUSH);
		options_item.setText(Localizer._("mainwindow.mainmenu.tools.options"));
		
		options_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SettingsWindow window = new SettingsWindow();
				window.getCoreComponents();
				window.initUIComponents();
			}
		});
		
		// Help menu
		MenuItem helpItem = new MenuItem (this, SWT.CASCADE);
		helpItem.setText (Localizer._("mainwindow.mainmenu.help"));

		submenu = new Menu (shell, SWT.DROP_DOWN);
		helpItem.setMenu (submenu);
		
		MenuItem help_contents_item = new MenuItem (submenu, SWT.PUSH);
		help_contents_item.setText(Localizer._("mainwindow.mainmenu.help.contents"));
		help_contents_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						Program.launch(JMConstants.ONLINE_HELP_WEB_SITE);
					}
				});	
			}
		});
		
		MenuItem forum_item = new MenuItem (submenu, SWT.PUSH);
		forum_item.setText(Localizer._("mainwindow.mainmenu.help.forum"));
		forum_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						Program.launch(JMConstants.JMULE_FORUMS);
					}
				});
			}
		});
		
		new MenuItem (submenu, SWT.SEPARATOR);
		
		MenuItem update_check_item = new MenuItem (submenu, SWT.PUSH);
		update_check_item.setText(Localizer._("mainwindow.mainmenu.help.updatecheck"));
		update_check_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				UpdaterWindow window = new UpdaterWindow();
				window.getCoreComponents();
				window.initUIComponents();
			}
			
		});
		
		new MenuItem (submenu, SWT.SEPARATOR);
		
		MenuItem about_item = new MenuItem (submenu, SWT.PUSH);
		about_item.setText(Localizer._("mainwindow.mainmenu.help.about"));
		about_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				AboutWindow window = new AboutWindow();
				window.getCoreComponents();
				window.initUIComponents();
			}
		});
	}
	
	public void setSelectedTab(JMULE_TABS tabID) {

		for(MenuItem item : tab_map.values()) 
			item.setSelection(false);
		
		MenuItem item = tab_map.get(tabID);
		item.setSelection(true);
	}

	protected void checkSubclass() {

    }

	
}
