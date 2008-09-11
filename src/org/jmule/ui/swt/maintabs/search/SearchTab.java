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
package org.jmule.ui.swt.maintabs.search;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.jmule.core.JMRunnable;
import org.jmule.core.JMuleCore;
import org.jmule.core.searchmanager.SearchManager;
import org.jmule.core.searchmanager.SearchRequest;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultListener;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.mainwindow.MainWindow;
/**
 * Created on Jul 31, 2008
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/11 18:29:16 $$
 */
public class SearchTab extends AbstractTab{

	private CTabFolder search_query_tab_list;
	
	private Text search_query;
	
	private JMuleCore _core;
	
	private List<SearchResultTab> search_tabs = new LinkedList<SearchResultTab>();
	
	public SearchTab(Composite parent, JMuleCore core) {
		super(parent);
	
		_core = core;
		
		_core.getSearchManager().addSeachResultListener(new SearchResultListener() {

			public void resultArrived(final SearchResult searchResult) {
				SWTThread.getDisplay().asyncExec(new JMRunnable() {
					public void JMRun() {
						SearchResultTab tab = getSearchResultTab(searchResult.getSearchRequest());
						if (tab != null) {
							tab.addSearchResult(searchResult);
							MainWindow.getLogger().fine(Localizer._("mainwindow.logtab.message_search_result_arrived",
									searchResult.getSearchRequest().getSearchQuery().getQuery(),searchResult.getSearchResultItemList().size()+""));
						}
					}
				}); 
			}
			
		});
		
		setLayout(new GridLayout(1,true));
		
		Composite search_bar_composite = new Composite(this,SWT.NONE);
		search_bar_composite.setLayout(new FillLayout());
		search_bar_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group search_bar = new Group(search_bar_composite,SWT.NONE);
		search_bar.setLayout(new GridLayout(1,false));
		
		Composite controls = new Composite(search_bar,SWT.NONE);
		controls.setLayout(new GridLayout(3,false));
		GridData layout_data = new GridData();
		layout_data.horizontalAlignment = GridData.CENTER;
		layout_data.grabExcessHorizontalSpace = true;
		controls.setLayoutData(layout_data);
		Label label = new Label(controls,SWT.NONE);
		label.setText(_._("mainwindow.searchtab.label.search") + " : ");
		search_query = new Text(controls,SWT.SINGLE | SWT.BORDER);
		layout_data = new GridData();
		layout_data.widthHint = 300;
		search_query.setLayoutData(layout_data);
		
		Button search_button = new Button(controls,SWT.PUSH);
		
		search_button.setText(Localizer._("mainwindow.searchtab.button.search"));
		search_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search();
			}
			
		});
		
		search_query.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == SWT.CR) {
					search();
				}
			}
		} );
		
		search_query_tab_list = new CTabFolder(this, SWT.BORDER);
		search_query_tab_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		search_query_tab_list.setLayout(new FillLayout());
		search_query_tab_list.setSimple(false);
		search_query_tab_list.setUnselectedImageVisible(true);
		search_query_tab_list.setUnselectedCloseVisible(false);
		
	}
	
	private SearchResultTab getSearchResultTab(SearchRequest searchRequest){
		SearchResultTab result = null;
		
		for(SearchResultTab tab : search_tabs) {
			if (tab.getSerchRequest().equals(searchRequest))
				if (!tab.hasResults())
					return tab;
		}
		
		
		return result;
	}
	
	public JMULE_TABS getTabType() {
		
		return JMULE_TABS.SEARCH;
	}

	public void lostFocus() {
		
	}

	public void obtainFocus() {
		
	}

	public void disposeTab() {
		for(SearchResultTab tab : search_tabs) {
			tab.getSearchTab().dispose();
		}
	}


	private void search() {
		if (!_core.getServerManager().isConnected()) {
			
			Utils.showWarningMessage(getShell(), Localizer._("mainwindow.searchtab.not_connected_to_server_title"), Localizer._("mainwindow.searchtab.not_connected_to_server"));
			
			return;
		}
			
		
		String query = search_query.getText();
		
		search_query.setText("");
		
		if (query.length()==0) return ;
		
		
		
		SearchRequest request = new SearchRequest(query);
		
		SearchManager manager = _core.getSearchManager();
		manager.search(request);
		
		final SearchResultTab tab = new SearchResultTab(search_query_tab_list,request,_core);
		
		search_query_tab_list.setSelection(tab.getSearchTab());
		
		search_tabs.add(tab);

		tab.getSearchTab().addListener(SWT.Close, new Listener() {
			public void handleEvent(Event arg0) {
				search_tabs.remove(tab);
			}		
		});
		
	}
	

}
