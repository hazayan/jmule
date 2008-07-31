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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.maintabs.AbstractTab;


/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:50 $$
 */
public class MainMenu extends Menu{

	private Map<Integer,MenuItem> tab_map = new HashMap<Integer,MenuItem>();
	
	public MainMenu(Shell shell) {
		super(shell, SWT.BAR);
		
		shell.setMenuBar(this);
		
		//File menu
		MenuItem fileItem = new MenuItem (this, SWT.CASCADE);
		fileItem.setText (Localizer._("mainwindow.mainmenu.file"));
		
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		
		MenuItem import_servers = new MenuItem (submenu, SWT.PUSH);
		import_servers.setText(Localizer._("mainwindow.mainmenu.file.import"));
		
		new MenuItem (submenu, SWT.SEPARATOR);

		MenuItem exit_item = new MenuItem (submenu, SWT.PUSH);
		exit_item.setText(Localizer._("mainwindow.mainmenu.file.exit"));
		
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
		tab_map.put(AbstractTab.TAB_SERVERLIST,servers_item);
		
		servers_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_SERVERLIST);
			}
			
		});
		
		MenuItem transfers_item = new MenuItem (tabs_menu, SWT.RADIO);
		transfers_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.transfers"));
		tab_map.put(AbstractTab.TAB_TRANSFERS,transfers_item);
		transfers_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_TRANSFERS);
			}
			
		});
		
		MenuItem search_item = new MenuItem (tabs_menu, SWT.RADIO);
		search_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.search"));
		tab_map.put(AbstractTab.TAB_SEARCH,search_item);
		
		search_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_SEARCH);
			}
			
		});
		
		MenuItem shared_item = new MenuItem (tabs_menu, SWT.RADIO);
		shared_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.shared"));
		tab_map.put(AbstractTab.TAB_SHARED,shared_item);
		
		shared_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_SHARED);
			}
			
		});
		
		MenuItem stats_item = new MenuItem (tabs_menu, SWT.RADIO);
		stats_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.stats"));
		tab_map.put(AbstractTab.TAB_STATISTICS,stats_item);
		stats_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_STATISTICS);
			}
			
		});
		
		
		MenuItem log_item = new MenuItem (tabs_menu, SWT.RADIO);
		log_item.setText(Localizer._("mainwindow.mainmenu.view.tabs.logs"));
		tab_map.put(AbstractTab.TAB_LOGS,log_item);
		log_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().setTab(AbstractTab.TAB_LOGS);
			}
			
		});
		
		MenuItem toolbar_item = new MenuItem (submenu, SWT.CHECK);
		if (!SWTPreferences.getInstance().isToolBarVisible())
			toolbar_item.setSelection(false);
		else
			toolbar_item.setSelection(true);
		toolbar_item.setText(Localizer._("mainwindow.mainmenu.view.toolbar"));
		toolbar_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().toolbarToogleVisibility();
			}
			
		});
		
		MenuItem status_item = new MenuItem (submenu, SWT.CHECK);
		if (!SWTPreferences.getInstance().isStatusBarVisible())
			status_item.setSelection(false);
		else
			status_item.setSelection(true);
		status_item.setText(Localizer._("mainwindow.mainmenu.view.statusbar"));
		status_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				MainWindow.getInstance().statusBarToogleVisibility();
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
		
		MenuItem options_item = new MenuItem (submenu, SWT.PUSH);
		options_item.setText(Localizer._("mainwindow.mainmenu.tools.options"));
		
		// Help menu
		MenuItem helpItem = new MenuItem (this, SWT.CASCADE);
		helpItem.setText (Localizer._("mainwindow.mainmenu.help"));

		submenu = new Menu (shell, SWT.DROP_DOWN);
		helpItem.setMenu (submenu);
		
		MenuItem help_contents_item = new MenuItem (submenu, SWT.PUSH);
		help_contents_item.setText(Localizer._("mainwindow.mainmenu.help.contents"));
		
		MenuItem forum_item = new MenuItem (submenu, SWT.PUSH);
		forum_item.setText(Localizer._("mainwindow.mainmenu.help.forum"));
		
		new MenuItem (submenu, SWT.SEPARATOR);
		
		MenuItem update_check_item = new MenuItem (submenu, SWT.PUSH);
		update_check_item.setText(Localizer._("mainwindow.mainmenu.help.updatecheck"));
		
		new MenuItem (submenu, SWT.SEPARATOR);
		
		MenuItem about_item = new MenuItem (submenu, SWT.PUSH);
		about_item.setText(Localizer._("mainwindow.mainmenu.help.about"));
	}
	
	public void setSelectedTab(int tabID) {

		for(MenuItem item : tab_map.values()) 
			item.setSelection(false);
		
		MenuItem item = tab_map.get(tabID);
		item.setSelection(true);
	}

	protected void checkSubclass() {

    }

	
}
