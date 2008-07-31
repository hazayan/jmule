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
package org.jmule.ui.swt.maintabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:37 $$
 */
public class SearchTab extends AbstractTab{

	private CTabFolder search_query_tab_list;
	private Text search_query;
	
	public SearchTab(Composite shell) {
		super(shell);
	
		setLayout(new GridLayout(1,true));
		
		Composite search_bar_composite = new Composite(this,SWT.NONE);
		search_bar_composite.setLayout(new FillLayout());
		search_bar_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group search_bar = new Group(search_bar_composite,SWT.NONE);
		search_bar.setText("Search");
		
		search_bar.setLayout(new GridLayout(3,false));
		
		Label search_label = new Label(search_bar,SWT.NONE);
		search_label.setText("Search : ");
	
		search_query = new Text(search_bar,SWT.SINGLE | SWT.BORDER);
		
		search_query.setLayoutData(new GridData(300,17));
		
		Button search_button = new Button(search_bar,SWT.PUSH);
		
		search_button.setText("Search");
		
		search_button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				if (search_query.getText().length()==0) return ;
				CTabItem search_tab = new CTabItem(search_query_tab_list, SWT.CLOSE);
				
				search_tab.setText(search_query.getText());
				search_query.setText("");
				search_query_tab_list.setSelection(search_tab);
				
			}
			
		});
		
		search_query_tab_list = new CTabFolder(this, SWT.BORDER);
		search_query_tab_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		search_query_tab_list.setLayout(new FillLayout());
		search_query_tab_list.setSimple(false);
		search_query_tab_list.setUnselectedImageVisible(false);
		search_query_tab_list.setUnselectedCloseVisible(false);
		
	}
	
	public int getTabType() {
		
		return TAB_SEARCH;
	}

	@Override
	public void lostFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void obtainFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disposeTab() {
		// TODO Auto-generated method stub
		
	}

}
