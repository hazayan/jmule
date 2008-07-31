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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.JMConstants;
import org.jmule.core.JMuleCore;
import org.jmule.core.JMuleCoreException;
import org.jmule.core.JMuleCoreFactory;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.JMuleUIManagerException;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.maintabs.LogsTab;
import org.jmule.ui.swt.maintabs.SearchTab;
import org.jmule.ui.swt.maintabs.SharedTab;
import org.jmule.ui.swt.maintabs.StatisticsTab;
import org.jmule.ui.swt.maintabs.serverlist.SWTServerListWrapper;
import org.jmule.ui.swt.maintabs.serverlist.ServerListTab;
import org.jmule.ui.swt.maintabs.transfers.TransfersTab;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:51 $$
 */
public class MainWindow implements JMuleUIComponent {

	private static MainWindow singleton = null;
	
	public static MainWindow getInstance() {
		if (singleton==null)
			singleton = new MainWindow();
		return singleton;
	}

	private List<AbstractTab> tab_list = new LinkedList<AbstractTab>();
	private ScrolledComposite window_content;
	private Shell shell;
	
	private JMuleCore _core;
	private Toolbar toolbar;
	private MainMenu main_menu;
	private StatusBar status_bar;
	
	private MainWindow() {	
	}
	
	public void initUIComponents() {
		final Display display = SWTThread.getInstance().getDisplay();
		
		display.asyncExec(new Runnable() {
            public void run() {
		
            	shell = new Shell(display);
            	
				SWTServerListWrapper.createListener(_core.getServersManager());
				            	
            	Utils.centreWindow(shell);
            	
            	shell.setText(JMConstants.JMULE_FULL_NAME);
            	
            	GridLayout gridLayout = new GridLayout(1,true);
            	gridLayout.marginHeight = 2;
            	gridLayout.marginWidth = 2;
            	shell.setLayout(gridLayout);
            	//Setup main_menu
            	main_menu = new MainMenu(shell);
            	//Setup tool bar
            	toolbar = new Toolbar(shell,_core);
		
            	window_content = new ScrolledComposite(shell,SWT.NONE);
            	window_content.setExpandHorizontal(true);
            	window_content.setExpandVertical(true);
            	window_content.setLayout(new FillLayout());
            	GridData gridData = new GridData(GridData.FILL_BOTH);
            	window_content.setLayoutData(gridData);
		
            	status_bar = new StatusBar(shell);
            	
            	shell.setSize(800, 500);
		
            	tab_list.add(new ServerListTab(window_content,_core));
            	tab_list.add(new TransfersTab(window_content));
            	tab_list.add(new SharedTab(window_content));
            	tab_list.add(new SearchTab(window_content));
            	tab_list.add(new StatisticsTab(window_content));
            	tab_list.add(new LogsTab(window_content));
		
            	setTab(AbstractTab.TAB_SERVERLIST);
            	
            	if (!SWTPreferences.getInstance().isStatusBarVisible())
        			statusBarToogleVisibility();
            	
        		if (!SWTPreferences.getInstance().isToolBarVisible())
        			toolbarToogleVisibility();
            	
            	shell.open ();
		
            	shell.addDisposeListener(new DisposeListener() {
            		public void widgetDisposed(DisposeEvent arg0) {
				
            			try {
							JMuleUIManager.getSingleton().shutdown();
						} catch (JMuleUIManagerException e) {
							e.printStackTrace();
						}
            			
            		}
			
            	});
            	
            	
            
            }});
		
	} 
	
	public void statusBarToogleVisibility() {
		status_bar.toogleVisibility();
		shell.layout();
	}
	
	public void toolbarToogleVisibility() {
		toolbar.toogleVisibility();
		shell.layout();
	}
	
	public Shell getShell() {
		return this.shell;
	}
	
	public void setTab(int tabID) {
		if (window_content.getContent()!=null) {
		for(AbstractTab tab : tab_list) {
			if (window_content.getContent().equals(tab)) {
				tab.lostFocus();
			}
		} }
		for(AbstractTab tab : tab_list) {
			if (tab.getTabType()==tabID) {
				window_content.setContent(tab);
				tab.obtainFocus();
				main_menu.setSelectedTab(tab.getTabType());
			}
			else {
			}
		}
		
		
	}

	public void getCoreComponents() {
		
		try {
			_core = JMuleCoreFactory.getSingleton();
		} catch (JMuleCoreException e) {
			e.printStackTrace();
		}
	}

}
