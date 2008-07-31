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
package org.jmule.ui.swt.maintabs.serverlist;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMException;
import org.jmule.core.edonkey.ServerManager;
import org.jmule.core.edonkey.impl.ED2KServerLink;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.swt.tables.TableStructureModificationListener;
import org.jmule.util.Misc;
/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:12 $$
 */
public class ServerList extends JMTable<Server> implements TableStructureModificationListener {
	
	private ServerManager servers_manager;
	private Color server_down_color;
	private Color server_connected_color;
	private Color server_default_color;
	private ServerListUpdater updater;
	
	private Image server_error = new Image(getDisplay(),UIImageRepository.getImageAsStream("server_error.png"));
	private Image server_default = new Image(getDisplay(),UIImageRepository.getImageAsStream("server.png"));
	private Image server_connected = new Image(getDisplay(),UIImageRepository.getImageAsStream("server_connected.png"));
	
	public ServerList(Composite composite, final ServerManager server_manager) {
		super(composite,true);
		
		SWTServerListWrapper.getInstance().setServerList(this);
		
		server_down_color = new Color(getDisplay(),178,178,178);
		server_connected_color = new Color(getDisplay(),25,81,225);
		server_default_color = new Color(getDisplay(),0,0,0);
		
		this.servers_manager = server_manager;
		int width;
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_NAME_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_NAME_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.name"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_IP_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_IP_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.address"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_DESCRIPTION_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.description"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_PING_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_PING_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.ping"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_USERS_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_USERS_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.users"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_MAX_USERS_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_MAX_USERS_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.max_users"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_FILES_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_FILES_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.files"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_SOFT_LIMIT_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.soft_limit"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_HARD_LIMIT_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.hard_limit"),width);
		
		width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.SERVER_LIST_SOFTWARE_COLUMN);
		addColumn(SWTConstants.SERVER_LIST_SOFTWARE_COLUMN,Localizer._("mainwindow.serverlisttab.serverlist.column.software"),width);
		
		updateColumnOrder();
		updateColumnVisibility();

		final Menu single_select_popup_menu =  new Menu(this);
		
		final MenuItem server_connect_status = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_connect_status.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.connect_to"));
		server_connect_status.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_connect.png")));
		server_connect_status.addSelectionListener(new SelectionAdapter() {
			
		public void widgetSelected(final SelectionEvent e) {
			Server selected_server = (Server) getSelectedObject();
			if (selected_server!=null)
				if (!selected_server.isConnected())
					SWTServerListWrapper.getInstance().connectTo(selected_server);
				else
					selected_server.disconnect();
		}} );
		new MenuItem (single_select_popup_menu, SWT.SEPARATOR);
		MenuItem server_add = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_add.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_server"));
		server_add.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_add.png")));
		server_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showServerAddWindow();
			}
		});
		final MenuItem server_remove = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_remove.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_server"));
		server_remove.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_delete.png")));
		server_remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Server selected_server = (Server)getSelectedObject();
				if (selected_server == null) return ;
				int result = Utils.showConfirmMessageBox(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.server_delete_confirm"));
				if (result==SWT.YES)
					SWTServerListWrapper.getInstance().removeServer(selected_server);
			}
		});
		final MenuItem server_remove_all = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_remove_all.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_all"));
		server_remove_all.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("remove_all.png")));
		server_remove_all.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (getItems().length == 0) return ;
				int returnvalue = Utils.showConfirmMessageBox(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.clear_confirm"));
				if (returnvalue == SWT.YES) {
					SWTServerListWrapper.getInstance().clearServerList();
			} }
		});
		new MenuItem (single_select_popup_menu, SWT.SEPARATOR);
		final MenuItem server_copy_ed2k_link = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_copy_ed2k_link.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.copy_ed2k_link"));
		server_copy_ed2k_link.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("ed2k_link.png")));
		server_copy_ed2k_link.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				Utils.setClipBoardText(getSelectedObject().getServerLink().getStringLink());
			}
		});
		
		MenuItem server_paste_ed2k_links = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_paste_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		server_paste_ed2k_links.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("ed2k_link_paste.png")));
		server_paste_ed2k_links.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				List<ED2KServerLink> server_links = extractServerLinks(Utils.getClipboardText());
				SWTServerListWrapper wrapper = SWTServerListWrapper.getInstance();
				for(ED2KServerLink link : server_links) {
					Server server = new Server(link);
					wrapper.addServer(server);
				}
			}
			
		});
		new MenuItem (single_select_popup_menu, SWT.SEPARATOR);
		final MenuItem server_properties = new MenuItem (single_select_popup_menu, SWT.PUSH);
		server_properties.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.server_properties"));
		server_properties.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_properties.png")));
		server_properties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				ServerPropertiesWindow properties_window = new ServerPropertiesWindow(getSelectedObject());
				properties_window.getCoreComponents();
				properties_window.initUIComponents();
			}
		});
		new MenuItem (single_select_popup_menu, SWT.SEPARATOR);
		MenuItem column_setup = new MenuItem (single_select_popup_menu, SWT.PUSH);
		column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		column_setup.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("columns_setup.png")));
		final TableStructureModificationListener listener = this;
		column_setup.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				showColumnEditorWindow(listener);
			}
		});
		
		// Multiple servers selected
		final Menu multiple_select_popup_menu = new Menu (this);
				
		MenuItem server_remove_selected = new MenuItem (multiple_select_popup_menu, SWT.PUSH);
		server_remove_selected.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_selected"));
		server_remove_selected.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_delete.png")));
		server_remove_selected.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				Server[] server_list = new Server[getSelectionCount()];
				
				for(int i = 0;i<getSelectionCount();i++){
					server_list[i] = getSelectedObjects().get(i);
				}
				
				removeServer(server_list);
			}
		});
		
		new MenuItem (multiple_select_popup_menu, SWT.SEPARATOR);
		
		MenuItem server_remove_all_multiselect = new MenuItem (multiple_select_popup_menu, SWT.PUSH);
		server_remove_all_multiselect.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.remove_all"));
		server_remove_all_multiselect.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("remove_all.png")));
		server_remove_all_multiselect.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				if (getItems().length == 0) return ;
				int returnvalue = Utils.showConfirmMessageBox(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.clear_confirm"));
				if (returnvalue == SWT.YES) {
					SWTServerListWrapper.getInstance().clearServerList();
				}
			}
			
		});
		
		new MenuItem (multiple_select_popup_menu, SWT.SEPARATOR);
		
		MenuItem server_copy_ed2k_links = new MenuItem (multiple_select_popup_menu, SWT.PUSH);
		server_copy_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.copy_ed2k_links"));
		server_copy_ed2k_links.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("ed2k_link.png")));
		server_copy_ed2k_links.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				String str = "";
				List<Server> selected_servers = getSelectedObjects();
				for(Server server : selected_servers) {
					str+=server.getServerLink().getStringLink()+"\n";
				}
				Utils.setClipBoardText(str);
			}
			
		});
		
		new MenuItem (multiple_select_popup_menu, SWT.SEPARATOR);
		
		column_setup = new MenuItem (multiple_select_popup_menu, SWT.PUSH);
		column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		column_setup.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("columns_setup.png")));
		final TableStructureModificationListener table_listener = this;
		column_setup.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {}

			public void widgetSelected(SelectionEvent arg0) {
				showColumnEditorWindow(table_listener);
			}
		});

		// No servers
		final Menu no_servers_menu = new Menu(this);
		
		MenuItem no_servers_server_add = new MenuItem (no_servers_menu, SWT.PUSH);
		no_servers_server_add.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.add_server"));
		no_servers_server_add.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("server_add.png")));
		no_servers_server_add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				showServerAddWindow();
			}
		});
		new MenuItem (no_servers_menu, SWT.SEPARATOR);
		MenuItem no_server_paste_ed2k_links = new MenuItem (no_servers_menu, SWT.PUSH);
		no_server_paste_ed2k_links.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.paste_ed2k_links"));
		no_server_paste_ed2k_links.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("ed2k_link_paste.png")));
		no_server_paste_ed2k_links.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				List<ED2KServerLink> server_links = extractServerLinks(Utils.getClipboardText());
				SWTServerListWrapper wrapper = SWTServerListWrapper.getInstance();
				for(ED2KServerLink link : server_links) {
					Server server = new Server(link);
					wrapper.addServer(server);
				}
			}
			
		});
		
		new MenuItem (no_servers_menu, SWT.SEPARATOR);
		MenuItem no_server_column_setup = new MenuItem (no_servers_menu, SWT.PUSH);
		no_server_column_setup.setText (Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.column_setup"));
		no_server_column_setup.setImage(new Image(getDisplay(),UIImageRepository.getImageAsStream("columns_setup.png")));
		no_server_column_setup.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {}
			public void widgetSelected(SelectionEvent arg0) {
				showColumnEditorWindow(listener);
			}
		});
		
		this.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				if (getItemCount()==0) {
					setMenu(no_servers_menu);
					return ;
				}
				if (getSelectionCount()==1) {
					Server server = getSelectedObject();
					server_connect_status.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.connect_to"));
					server_connect_status.setImage(new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("server_connect.png")));
					if (server!=null) 
						if (server.isConnected()) {
							server_connect_status.setText(Localizer._("mainwindow.serverlisttab.serverlist.popupmenu.disconnect_from"));
							server_connect_status.setImage(new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("server_disconnect.png")));
					}
					setMenu(single_select_popup_menu);
				}
				else
					setMenu(multiple_select_popup_menu);
			}
		});

		
//		this.addPaintListener(new PaintListener() {
//			private long lastCall = System.currentTimeMillis();
//			public void paintControl(PaintEvent arg0) {
//				if ((System.currentTimeMillis() - lastCall)<500) return;
//				lastCall = System.currentTimeMillis();
//				System.out.println("Refresh");
//				for(int i = 0;i<line_list.size();i++) {
//					
//					if (line_list.get(i).isVisible()) {
//						updateLine(line_object_list.get(i));
//					}
//					
//				}
//			}
//			
//		});
		
		// Add servers
	    for(Server server : server_manager.getServers()) {
        		addServer(server);
	    }
	    
	    updater = new ServerListUpdater();
	}

	protected int compareObjects(Server object1, Server object2,
			String columnID, boolean order) {
		
		if (columnID == SWTPreferences.SERVER_LIST_NAME_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getName", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_DESCRIPTION_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getDesc", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_IP_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getAddressAsInt", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_PING_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getPing", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_USERS_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getNumUsers", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_MAX_USERS_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getMaxUsers", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_FILES_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getNumFiles", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_SOFT_LIMIT_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getSoftLimit", order);
		}
	
		if (columnID == SWTPreferences.SERVER_LIST_HARD_LIMIT_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getHardLimit", order);
		}
		
		if (columnID == SWTPreferences.SERVER_LIST_SOFTWARE_COLUMN) {
			return Misc.compareAllObjects(object1, object2, "getVersion", order);
		}
		
		return 0;
	}



	
	public void tableStructureChanged() {
		updateColumnOrder();
		updateColumnVisibility();
		//saveColumnSettings();
		this.update();
		this.redraw();
	}
	
	private void removeServer(Server... servers) {
		if (servers.length==0) return ;
		int result = 0;
		if (servers.length==1)
			result = Utils.showConfirmMessageBox(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.server_delete_confirm"));
		else
			result = Utils.showConfirmMessageBox(getShell(), Localizer._("mainwindow.serverlisttab.serverlist.servers_delete_confirm"));
		if (result == SWT.YES) {
			for(int i = 0;i<servers.length;i++){
				SWTServerListWrapper.getInstance().removeServer(servers[i]);
			}
		}
	}
	
	private List<ED2KServerLink> extractServerLinks(String links) {
		List<ED2KServerLink> result = new LinkedList<ED2KServerLink>();
		int i = 0;
		String link = "";
		while(i<links.length()){
		
			if (links.charAt(i)== '\n') {
				try {
					ED2KServerLink server_link = new ED2KServerLink(link);
					result.add(server_link);
					link = "";
				} catch (JMException e) {
					break;
				}
			} else 
				link += links.charAt(i);
			i++;
		}
		
		if (links.length()!=0) {
			try {
				ED2KServerLink server_link = new ED2KServerLink(link);
				result.add(server_link);
				link = "";
			} catch (JMException e) {
			}
		}
		return result;
	}
	
	private void showServerAddWindow() {
		ServerAddWindow add_server_window = new ServerAddWindow(Localizer._("serveraddwindow.title"));
		add_server_window.getCoreComponents();
		add_server_window.initUIComponents();
	}
	
	public void addServer(final Server server) {
		addLine(server);
		updateLine(server);		
	}
	
	public void removeServer(Server server) {
		removeRow(server);
	}
	
	protected void updateLine(Server server) {
		int id = 0;
		
		if (server.isDown()) {
			setLineImage(server,id,server_error);
			setForegroundColor(server, server_down_color);
		} else
		
			if (server.isConnected()) {
				setLineImage(server,id,server_connected);
				setForegroundColor(server,server_connected_color);
			}
			else {
				setForegroundColor(server,server_default_color);
				setLineImage(server,id,server_default);
			}

		setLineText(server, SWTPreferences.SERVER_LIST_IP_ORDER_DEFAULT, server.getAddress()+":"+server.getPort());
		setLineText(server, SWTPreferences.SERVER_LIST_NAME_ORDER_DEFAULT, server.getName());
		setLineText(server, SWTPreferences.SERVER_LIST_DESCRIPTION_ORDER_DEFAULT,server.getDesc());
		setLineText(server, SWTPreferences.SERVER_LIST_PING_ORDER_DEFAULT,server.getPing()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_USERS_ORDER_DEFAULT,server.getNumUsers()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_MAX_USERS_ORDER_DEFAULT,server.getMaxUsers()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_FILES_ORDER_DEFAULT,server.getNumFiles()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_SOFT_LIMIT_ORDER_DEFAULT,server.getSoftLimit()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_HARD_LIMIT_ORDER_DEFAULT,server.getHardLimit()+"");
		setLineText(server, SWTPreferences.SERVER_LIST_SOFTWARE_ORDER_DEFAULT,server.getVersion()+"");

	}
	
	public void lostFocus() {
		GUIUpdater.getInstance().removeRefreshable(updater);
	}
	
	public void obtainFocus() {
		GUIUpdater.getInstance().addRefreshable(updater);
	}

	
	private class ServerListUpdater implements Refreshable {

		public void refresh() {
			long start = System.currentTimeMillis();
			int i=0;
			for(Server server : servers_manager.getServers()) {
				if (!getRow(server).isVisible()) continue ;
				if (is_sorted)
					sortColumn(last_sort_column,last_sort_dir);
				updateLine(server);
				i++;
			}
			long end = System.currentTimeMillis();
		//	System.out.println("Processed items : "+i);
		//	System.out.println("Refresh time : "+(end-start));
		}
		
		
		
	}
	
}
