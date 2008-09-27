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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMuleCore;
import org.jmule.core.downloadmanager.DownloadManager;
import org.jmule.core.edonkey.impl.FileHash;
import org.jmule.core.searchmanager.SearchRequest;
import org.jmule.core.searchmanager.SearchResult;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.core.sharingmanager.SharingManager;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.util.Misc;


/**
 * Created on Aug 15, 2008
 * @author binary256
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/27 17:08:24 $
 */
public class SearchResultTab {

	private boolean hasResults = false;
	
	private CTabItem search_tab;
	
	private SearchRequest request;

	private JMTable<SearchResultItem> search_results;
	
	private Menu no_items_menu, pop_up_menu;
	private MenuItem download_item;
	private MenuItem properties_item;
	private JMuleCore _core;
	
	private SearchResult search_result;
	
	private Color color_red = SWTThread.getDisplay().getSystemColor(SWT.COLOR_RED);
	private Color color_green = new Color(SWTThread.getDisplay(),0,139,0);
	
	public SearchResultTab(CTabFolder parent,SearchRequest searchRequest,JMuleCore core) {
		search_tab = new CTabItem(parent,SWT.CLOSE);
		_core = core;
		request = searchRequest;
		search_tab.setText(getTabTitle(request.getSearchQuery().getQuery()));
		search_tab.setToolTipText(request.getSearchQuery().getQuery());
		search_tab.setImage(SWTImageRepository.getImage("search_loading.png"));
		
		Composite content = new Composite(parent,SWT.NONE);
		
		search_tab.setControl(content);
		content.setLayout(new FillLayout());
		
		search_results = new JMTable<SearchResultItem>(content, SWT.NONE) {

			protected int compareObjects(SearchResultItem object1,
					SearchResultItem object2, int columnID, boolean order) {
				
				if (columnID == SWTPreferences.SEARCH_FILENAME_COLUMN_ID) {
					return Misc.compareAllObjects(object1, object2, "getFileName", order);
				}
				
				if (columnID == SWTPreferences.SEARCH_FILESIZE_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2, "getFileSize", order);
				
				if (columnID == SWTPreferences.SEARCH_AVAILABILITY_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2, "getFileAviability", order);
				
				if (columnID == SWTPreferences.SEARCH_COMPLETESRC_COLUMN_ID)
					return Misc.compareAllObjects(object1, object2, "getFileCompleteSrc", order);
				
				if (columnID == SWTPreferences.SEARCH_FILE_TYPE_COLUMN_ID) {
					String type1 = new String(object1.getMimeType());
					String type2 = new String(object2.getMimeType());
					
					int result = type1.compareTo(type2);
					
					if (order) return result;
					
					return Misc.reverse(result);
				}
				
				
				if (columnID == SWTPreferences.SEARCH_FILE_ID_COLUMN_ID) {
					String id1 = object1.getFileHash().getAsString();
					String id2 = object2.getFileHash().getAsString();
					int result = id1.compareTo(id2);
					if (order) return result;
					return Misc.reverse(result);
				}
				
				return 0;
			}

			protected Menu getPopUpMenu() {

				if (getItemCount()==0) return no_items_menu;
				boolean contain_unshared = false;
				
				SharingManager manager = _core.getSharingManager();
				
				for(SearchResultItem item : getSelectedObjects())
					if (!manager.hasFile(item.getFileHash()))
							contain_unshared = true;
				if (contain_unshared)
					download_item.setEnabled(true);
				else
					download_item.setEnabled(false);
				
				if (getSelectionCount()>1)
					properties_item.setEnabled(false);
				else
					properties_item.setEnabled(true);
				
				return pop_up_menu;
			}


			public void updateRow(SearchResultItem object) {
				
				FileHash fileHash = object.getFileHash();
				if (_core.getDownloadManager().hasDownload(fileHash)) {
					setForegroundColor(object, color_red);
				}
				else
					if (_core.getSharingManager().hasFile(fileHash))
						setForegroundColor(object, color_green);
					
				
				setRowText(object, SWTConstants.SEARCH_FILENAME_COLUMN_ID, object.getFileName());
				Image file_icon = SWTImageRepository.getIconByExtension(object.getFileName());
				setRowImage(object, SWTPreferences.SEARCH_FILENAME_COLUMN_ID, file_icon);
				
				setRowText(object, SWTPreferences.SEARCH_FILESIZE_COLUMN_ID, FileFormatter.formatFileSize(object.getFileSize()));
				setRowText(object, SWTPreferences.SEARCH_AVAILABILITY_COLUMN_ID, object.getFileAviability()+"");
				setRowText(object, SWTPreferences.SEARCH_COMPLETESRC_COLUMN_ID, object.getFileCompleteSrc()+"");
				byte[] fileType = object.getMimeType();
				
				setRowText(object, SWTPreferences.SEARCH_FILE_TYPE_COLUMN_ID, FileFormatter.formatMimeType(fileType));
				setRowText(object, SWTPreferences.SEARCH_FILE_ID_COLUMN_ID, object.getFileHash().getAsString());
				
			}
			
		};
		int width;
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_FILENAME_COLUMN_ID); 
		search_results.addColumn(SWT.LEFT,  SWTConstants.SEARCH_FILENAME_COLUMN_ID, _._("mainwindow.searchtab.column.filename"), "", width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_FILESIZE_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT, SWTConstants.SEARCH_FILESIZE_COLUMN_ID, _._("mainwindow.searchtab.column.filesize"), "", width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_AVAILABILITY_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT,  SWTConstants.SEARCH_AVAILABILITY_COLUMN_ID, _._("mainwindow.searchtab.column.availability"), "" , width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_COMPLETESRC_COLUMN_ID);
		search_results.addColumn(SWT.RIGHT,  SWTConstants.SEARCH_COMPLETESRC_COLUMN_ID, _._("mainwindow.searchtab.column.completesrcs"), "", width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_FILE_TYPE_COLUMN_ID);
		search_results.addColumn(SWT.LEFT,  SWTConstants.SEARCH_FILE_TYPE_COLUMN_ID, _._("mainwindow.searchtab.column.filetype"), "", width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SEARCH_FILE_ID_COLUMN_ID);
		search_results.addColumn(SWT.LEFT,  SWTConstants.SEARCH_FILE_ID_COLUMN_ID, _._("mainwindow.searchtab.column.fileid"), "", width);
		
		search_results.updateColumnSettings();
		
		no_items_menu = new Menu(search_results);
		
		MenuItem no_items_column_setup_item = new MenuItem(no_items_menu,SWT.PUSH);
		no_items_column_setup_item.setText(_._("mainwindow.searchtab.popupmenu.column_setup"));
		no_items_column_setup_item.setImage(SWTImageRepository.getImage("columns_setup.png"));
		no_items_column_setup_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_results.showColumnEditorWindow();
			}
			
		});
		
		pop_up_menu = new Menu(search_results);
		
		download_item = new MenuItem(pop_up_menu,SWT.PUSH);
		download_item.setText(_._("mainwindow.searchtab.popupmenu.download"));
		download_item.setImage(SWTImageRepository.getImage("start_download.png"));
		download_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				start_download();
			}
		});
		
		
		MenuItem try_again = new MenuItem(pop_up_menu,SWT.PUSH);
		try_again.setText(_._("mainwindow.searchtab.popupmenu.try_again"));
		try_again.setImage(SWTImageRepository.getImage("refresh.png"));
		try_again.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				retry();
			}
		});
		

		
		MenuItem copy_ed2k_item = new MenuItem(pop_up_menu,SWT.PUSH);
		copy_ed2k_item.setText(_._("mainwindow.searchtab.popupmenu.copy_ed2k_link"));
		copy_ed2k_item.setImage(SWTImageRepository.getImage("ed2k_link.png"));
		copy_ed2k_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				copyED2KLinks();
			}
		});
		
		
		MenuItem close_item = new MenuItem(pop_up_menu,SWT.PUSH);
		close_item.setText(_._("mainwindow.searchtab.popupmenu.close"));
		close_item.setImage(SWTImageRepository.getImage("cancel.png"));
		close_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_tab.dispose();
			}
			
		});

		
		MenuItem column_setup_item = new MenuItem(pop_up_menu,SWT.PUSH);
		column_setup_item.setText(_._("mainwindow.searchtab.popupmenu.column_setup"));
		column_setup_item.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				search_results.showColumnEditorWindow();
			}
			
		});
		
		new MenuItem(pop_up_menu,SWT.SEPARATOR);
		
		properties_item = new MenuItem(pop_up_menu,SWT.PUSH);
		properties_item.setText(_._("mainwindow.searchtab.popupmenu.properties"));
		properties_item.setImage(SWTImageRepository.getImage("info.png"));
		properties_item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				SearchPropertiesWindow window = new SearchPropertiesWindow(search_results.getSelectedObject());
				window.getCoreComponents();
				window.initUIComponents();
			}
			
		});
		
		search_tab.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				_core.getSearchManager().removeSearch(request);
				if (search_result!=null)
					_core.getSearchManager().removeResult(search_result);
			}
			
		});

	}
	
	public SearchRequest getSerchRequest() {
		return request;
	}
	
	public CTabItem getSearchTab() {
		return search_tab;
	}
	
	public void addSearchResult(SearchResult searchResult) {
		hasResults = true;
		search_result = searchResult;
		for(SearchResultItem item : searchResult.getSearchResultItemList()) {
			search_results.addRow(item);
		}
		String query = searchResult.getSearchRequest().getSearchQuery().getQuery();
		String title = getTabTitle(query)+" ("+searchResult.getSearchResultItemList().size()+")";
		search_tab.setText(title);
		search_tab.setImage(null);
		
	}

	private void start_download() {
		List<SearchResultItem> list = search_results.getSelectedObjects();
		DownloadManager download_manager = _core.getDownloadManager();
		for(SearchResultItem item : list) {
			download_manager.addDownload(item);
			download_manager.getDownload(item.getFileHash()).startDownload();
			search_results.updateRow(item);
		}
	}
	
	private void retry() {
		hasResults = false;
		_core.getSearchManager().removeSearch(request);
		search_results.clear();
		_core.getSearchManager().search(request);
		search_tab.setText(getTabTitle(request.getSearchQuery().getQuery()));
		search_tab.setImage(SWTImageRepository.getImage("search_loading.png"));
	}
	
	private void copyED2KLinks() {
		List<SearchResultItem> list = search_results.getSelectedObjects();
		String str = "";
		String separator = System.getProperty("line.separator");
		for(SearchResultItem item : list)
			str+=item.getAsED2KLink().getAsString() + separator;
		Utils.setClipBoardText(str);
	}
	
	private String getTabTitle(String query) {
		String result = query;
		
		if (result.length()>10)
			result = result.substring(0, 10)+"...";
		
		return result;
	}

	public boolean hasResults() {
		return hasResults;
	}
	
}
