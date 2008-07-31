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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.jmule.core.JMuleCore;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.common.ConnectButton;
import org.jmule.ui.swt.maintabs.AbstractTab;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:51 $$
 */
public class Toolbar extends ToolBar {

	private List<ToolItem> button_list = new LinkedList<ToolItem>();
	private GridData grid_data;
	
	public Toolbar(Composite shell,JMuleCore core) {
		super(shell, SWT.FLAT);

		grid_data = new GridData(GridData.FILL_HORIZONTAL);
		
		setLayoutData(grid_data);
		
		ConnectButton connect_item = new ConnectButton(this);
		
		new ToolItem(this,SWT.SEPARATOR);
		
		final ToolItem servers_item = new ToolItem(this, SWT.PUSH);
		servers_item.setText(Localizer._("mainwindow.toolbar.servers_item"));
		servers_item.setSelection(true);
		servers_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("servers.png")));
		button_list.add(servers_item);
		
		servers_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(servers_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_SERVERLIST);
				
			}
			
		});
		
		final ToolItem transfers_item = new ToolItem(this, SWT.PUSH);
		transfers_item.setText(Localizer._("mainwindow.toolbar.transfers_item"));
		transfers_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("transfer.png")));
		button_list.add(transfers_item);
		
		transfers_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(transfers_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_TRANSFERS);
			}
			
		});
		
		final ToolItem shared_item = new ToolItem(this, SWT.PUSH);
		shared_item.setText(Localizer._("mainwindow.toolbar.shared_item"));
		shared_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("shared_files.png")));
		button_list.add(shared_item);
		
		shared_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(shared_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_SHARED);
			}
			
		});
		
		final ToolItem search_item = new ToolItem(this, SWT.PUSH);
		search_item.setText(Localizer._("mainwindow.toolbar.search_item"));
		search_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("search.png")));
		button_list.add(search_item);
		
		search_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(search_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_SEARCH);
			}
			
		});
		
		final ToolItem statistics_item = new ToolItem(this,SWT.PUSH);
		statistics_item.setText(Localizer._("mainwindow.toolbar.statistics_item"));
		statistics_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("statistics.png")));
		button_list.add(statistics_item);
		
		statistics_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(statistics_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_STATISTICS);
			}
			
		});
		
		final ToolItem logs_item = new ToolItem(this, SWT.PUSH);
		logs_item.setText(Localizer._("mainwindow.toolbar.logs_item"));
		logs_item.setImage(new Image(shell.getDisplay(), UIImageRepository.getImageAsStream("logs.png")));
		button_list.add(logs_item);
		
		logs_item.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				setSelection(logs_item);
				
				MainWindow.getInstance().setTab(AbstractTab.TAB_LOGS);
			}
			
		});
		
	}
	
	private void setSelection(ToolItem selected_item) {
		
		selected_item.setSelection(true);
		
		for(ToolItem item : button_list) {
			
			if (!item.equals(selected_item))
				
				item.setSelection(false);
			
		}
		
	}
	
	public void toogleVisibility() {
		setVisible(!getVisible());
		SWTPreferences.getInstance().setToolBarVisible(getVisible());
		grid_data.exclude = !grid_data.exclude;
		setLayoutData(grid_data);
		layout();
	}
	
	protected void checkSubclass() {
    }
	
}
