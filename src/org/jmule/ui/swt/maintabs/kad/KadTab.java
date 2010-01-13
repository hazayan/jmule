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
package org.jmule.ui.swt.maintabs.kad;

import static org.jmule.ui.UIConstants.KAD_CLIENT_DISTANCE_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_CLIENT_ID_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_LOOKUP_HASH_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_LOOKUP_INFO_COLUMN_ID;
import static org.jmule.ui.UIConstants.KAD_TASK_TYPE_COLUMN_ID;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.jmule.core.JMuleCore;
import org.jmule.core.configmanager.ConfigurationAdapter;
import org.jmule.core.configmanager.ConfigurationManagerException;
import org.jmule.core.edonkey.packet.tag.StringTag;
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadConstants;
import org.jmule.core.jkad.JKadListener;
import org.jmule.core.jkad.JKadManager;
import org.jmule.core.jkad.JKadConstants.ContactType;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.lookup.LookupListener;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.publisher.PublishKeywordTask;
import org.jmule.core.jkad.publisher.PublishSourceTask;
import org.jmule.core.jkad.publisher.Publisher;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTableListener;
import org.jmule.core.jkad.search.KeywordSearchTask;
import org.jmule.core.jkad.search.NoteSearchTask;
import org.jmule.core.jkad.search.Search;
import org.jmule.core.jkad.search.SearchTask;
import org.jmule.core.jkad.utils.Utils;
import org.jmule.core.utils.Misc;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.tables.JMTable;

/**
 * Created on Jul 10, 2009
 * @author binary256
 * @version $Revision: 1.9 $
 * Last changed by $Author: binary255 $ on $Date: 2010/01/13 15:55:28 $
 */
public class KadTab extends AbstractTab {

	private JMuleCore _core;
	private JMTable<KadContact> contact_list;
	private JMTable<KadTask> kad_task_list;
	private Map<Int128,KadTask> task_map = new ConcurrentHashMap<Int128, KadTask>();
	private JKadManager _jkad;
	private Group routing_table_container;
	public KadTab(Composite shell, JMuleCore core) {		
		super(shell);
		_core = core;
		_jkad = _core.getJKadManager();
		setLayout(new GridLayout(1,false));
		
		final Composite tab_content = new Composite(this,SWT.NONE);
		tab_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab_content.setLayout(new FormLayout());
				
		routing_table_container = new Group(tab_content,SWT.NONE);
		Group kad_tasks_container = new Group(tab_content,SWT.NONE);
		routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes"));
		kad_tasks_container.setText(_._("mainwindow.kadtab.kad_tasks"));
		SashControl.createHorizontalSash(30, 80, tab_content, routing_table_container, kad_tasks_container);
		GridLayout grid_layout = new GridLayout(1,false);
		grid_layout.marginWidth = 0;
		grid_layout.marginHeight = 0;
		routing_table_container.setLayout(grid_layout);
		
		Composite buttons_composite = new Composite(routing_table_container, SWT.NONE);
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		buttons_composite.setLayoutData(g);
		buttons_composite.setLayout(new GridLayout(1,false));
		
		final Button setKadStatus = new Button(buttons_composite, SWT.NONE);
		try {
			setKadStatus.setEnabled(_core.getConfigurationManager().isJKadAutoconnectEnabled());
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			setKadStatus.setEnabled(false);
		}
		g = new GridData();
		g.widthHint = 150;
		setKadStatus.setLayoutData(g);
		setKadStatus.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (_core.getJKadManager().isConnected())
					_core.getJKadManager().disconnect();
				else
					if (_core.getJKadManager().isConnecting())
						_core.getJKadManager().disconnect();
					else
						if (_core.getJKadManager().isDisconnected()) {
							setKadStatus.setEnabled(false);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									_core.getJKadManager().connect();
								}
							});
						}
							
			}
		});
		
		if (_core.getJKadManager().isConnected())
			setKadStatus.setText(_._("mainwindow.kadtab.disconnect"));
		if (_core.getJKadManager().isConnecting())
			setKadStatus.setText(_._("mainwindow.kadtab.stop_connecting"));
		if (_core.getJKadManager().isDisconnected())
			setKadStatus.setText(_._("mainwindow.kadtab.connect"));
		
		_core.getJKadManager().addJKadListener(new JKadListener() {
			public void JKadIsConnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText(_._("mainwindow.kadtab.disconnect"));
					}
				});
			}

			public void JKadIsConnecting() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText(_._("mainwindow.kadtab.stop_connecting"));
					}
				});
			}

			public void JKadIsDisconnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes"));
						setKadStatus.setText(_._("mainwindow.kadtab.connect"));
						
					}
				});
			}
			
		});
		
		contact_list = new JMTable<KadContact>(routing_table_container,SWT.NONE){
			protected int compareObjects(KadContact object1,
					KadContact object2, int columnID, boolean order) {
				int result = 0;
				if (columnID == KAD_CLIENT_ID_COLUMN_ID)
					result = object1.getContactID().toHexString().compareTo(object2.getContactID().toHexString());
				
				if (columnID == KAD_CLIENT_DISTANCE_COLUMN_ID)
					result = object1.getContactDistance().toBinaryString().compareTo(object2.getContactDistance().toBinaryString());
				
				if (!order)
					result = Misc.reverse(result);
				return result;
			}

			protected Menu getPopUpMenu() {
				
				return null;
			}


			public void updateRow(KadContact object) {
				String imageName = "";
				if (object.getContactType() == ContactType.Active2MoreHours) imageName="contact0.png";
				if (object.getContactType() == ContactType.Active1Hour) imageName="contact1.png";
				if (object.getContactType() == ContactType.Active) imageName="contact2.png";
				if (object.getContactType() == ContactType.JustAdded) imageName="contact3.png";
				if (object.getContactType() == ContactType.ScheduledForRemoval) imageName="contact4.png";
				setRowImage(object, KAD_CLIENT_ID_COLUMN_ID, SWTImageRepository.getImage(imageName));
				setRowText(object, KAD_CLIENT_ID_COLUMN_ID, object.getContactID().toHexString());
				setRowText(object, KAD_CLIENT_DISTANCE_COLUMN_ID, object.getContactDistance().toBinaryString());
			}
			
		};
		contact_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		contact_list.addColumn(SWT.LEFT, KAD_CLIENT_ID_COLUMN_ID, _._("mainwindow.kadtab.contact_list.column.contact_id"), _._("mainwindow.kadtab.contact_list.column.contact_id.desc"), swtPreferences.getColumnWidth(KAD_CLIENT_ID_COLUMN_ID));
		contact_list.addColumn(SWT.LEFT, KAD_CLIENT_DISTANCE_COLUMN_ID, _._("mainwindow.kadtab.contact_list.column.contact_distance"), _._("mainwindow.kadtab.contact_list.column.contact_distance.desc"), swtPreferences.getColumnWidth(KAD_CLIENT_DISTANCE_COLUMN_ID));
		
		_core.getJKadManager().getRoutingTable().addListener(new RoutingTableListener() {
			public void contactAdded(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.addRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
				
			}

			public void contactRemoved(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.removeRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
			}

			public void contactUpdated(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.updateRow(contact);
						routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
					}
					
				});
			}

			public void allContactsRemoved() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.clear();
					}
					
				});
			}
			
		});
		
		for(KadContact contact : _core.getJKadManager().getRoutingTable().getContacts())
			contact_list.addRow(contact);
		
		routing_table_container.setText(_._("mainwindow.kadtab.kad_nodes_number",contact_list.getItemCount()+""));
		
		kad_tasks_container.setLayout(new FillLayout());
		kad_task_list = new JMTable<KadTask>(kad_tasks_container,SWT.NONE) {

			protected int compareObjects(KadTask object1, KadTask object2,
					int columnID, boolean order) {
				int result = 0;
				if (columnID == KAD_TASK_TYPE_COLUMN_ID)
					result = object1.task_type.compareTo(object2.task_type);
				if (columnID == KAD_TASK_LOOKUP_HASH_COLUMN_ID)
					result = object1.task_id.compareTo(object2.task_id);
				if (columnID == KAD_TASK_LOOKUP_INFO_COLUMN_ID)
					result = object1.task_info.compareTo(object2.task_info);
				if (!order)
					result = Misc.reverse(result);
				return result;
			}

			protected Menu getPopUpMenu() {
				return null;
			}

			public void updateRow(KadTask object) {
				setRowText(object, KAD_TASK_TYPE_COLUMN_ID, object.task_type);
				setRowText(object, KAD_TASK_LOOKUP_HASH_COLUMN_ID, object.task_id);
				setRowText(object, KAD_TASK_LOOKUP_INFO_COLUMN_ID, object.task_info);
			}
		};
		
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_TYPE_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_type"), _._("mainwindow.kadtab.task_list.column.task_type.desc"), swtPreferences.getColumnWidth(KAD_TASK_TYPE_COLUMN_ID));
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_LOOKUP_HASH_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_hash"), _._("mainwindow.kadtab.task_list.column.task_hash.desc"), swtPreferences.getColumnWidth(KAD_TASK_LOOKUP_HASH_COLUMN_ID));
		kad_task_list.addColumn(SWT.LEFT, KAD_TASK_LOOKUP_INFO_COLUMN_ID, _._("mainwindow.kadtab.task_list.column.task_info"), _._("mainwindow.kadtab.task_list.column.task_info.desc"), swtPreferences.getColumnWidth(KAD_TASK_LOOKUP_INFO_COLUMN_ID));
		kad_task_list.setLinesVisible(true);
		_core.getJKadManager().getLookup().addListener(new LookupListener() {
			public void taskAdded(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = lookupTaskToKadTask(task);
						if (kad_task != null)
							if (!kad_task_list.hasObject(kad_task)) {
								task_map.put(task.getTargetID(), kad_task);
								kad_task_list.addRow(kad_task);
							}
					}
					
				});
			}

			public void taskRemoved(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = task_map.get(task.getTargetID());
						if (kad_task == null)
							return;
						kad_task_list.removeRow(kad_task);
					}
					
				});
			}

			public void taskStarted(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = lookupTaskToKadTask(task);
						if (kad_task != null)
							if (!kad_task_list.hasObject(kad_task)) {
								task_map.remove(task.getTargetID());
								task_map.put(task.getTargetID(), kad_task);
								kad_task_list.updateRow(kad_task);
							}
					}
					
				});
			}
			
		});
		
		Map<Int128,LookupTask> list = _core.getJKadManager().getLookup().getLookupTasks();
		for(LookupTask task : list.values()) {
			KadTask kad_task = lookupTaskToKadTask(task);
			if (kad_task!=null)
				if (!kad_task_list.hasObject(kad_task)) {
					task_map.put(task.getTargetID(), kad_task);
					kad_task_list.addRow(kad_task);
				}
		}
		
		_core.getConfigurationManager().addConfigurationListener(new ConfigurationAdapter() {

			public void jkadStatusChanged(boolean newStatus) {
				if (newStatus == false) {
					setKadStatus.setEnabled(false);
				} else
					setKadStatus.setEnabled(true);
			}
			
		});
	}

	public void disposeTab() {
		
	}

	public JMULE_TABS getTabType() {
		
		return JMULE_TABS.KAD;
	}

	public void lostFocus() {
		
	}

	public void obtainFocus() {
		
	}
	
	private class KadTask {
		public String task_type;
		public String task_id;
		public String task_info;
		
		public int hashCode() {
			return (task_id + task_type).hashCode();
		}
		
		public boolean equals(Object object) {
			if (object == null) return false;
			if (!(object instanceof KadTask)) return false;
			KadTask kt = (KadTask)object;
			return task_id.equals(kt.task_id);
		}
	}

	private KadTask lookupTaskToKadTask(LookupTask task) {
		KadTask kad_task = new KadTask();
		if (task.getRequestType() == RequestType.FIND_NODE) {
			kad_task.task_type = _._("mainwindow.kadtab.node_lookup");
			
			Search search = _jkad.getSearch();
			SearchTask search_task = search.getSearchTask(task.getTargetID());
			if (search_task==null) return null; 
			kad_task.task_info = Utils.KadFileIDToFileName(search_task.getSearchID());
		}
		if (task.getRequestType() == RequestType.FIND_VALUE) {
			kad_task.task_type = _._("mainwindow.kadtab.keyword_search");
			Search search = _jkad.getSearch();
			SearchTask search_task = search.getSearchTask(task.getTargetID());
			if (search_task==null) return null; 
			if (search_task instanceof NoteSearchTask) {
				//TODO : Add note
				return null;
			}
			if (search_task instanceof KeywordSearchTask) {
				KeywordSearchTask keyword_search_task = (KeywordSearchTask) search_task;
				kad_task.task_info = keyword_search_task.getKeyword();
			}
		}
		if (task.getRequestType() == RequestType.STORE) {
			kad_task.task_type = _._("mainwindow.kadtab.store");
			Publisher publisher = _jkad.getPublisher();
			if (publisher.getPublishKeywordTask(task.getTargetID())!=null) {
				PublishKeywordTask publish_keyword = publisher.getPublishKeywordTask(task.getTargetID());
				StringTag file_name = (StringTag)publish_keyword.getTagList().getTag(JKadConstants.TAG_FILENAME);
				kad_task.task_info = (String)file_name.getValue();
			} else
			if (publisher.getPublishSourceTask(task.getTargetID())!=null) {
				PublishSourceTask publish_keyword = publisher.getPublishSourceTask(task.getTargetID());
				StringTag file_name = (StringTag)publish_keyword.getTagList().getTag(JKadConstants.TAG_FILENAME);
				kad_task.task_info = (String)file_name.getValue();
			}
		}
		
		kad_task.task_id = task.getTargetID().toHexString();
		return kad_task;
	}
	
}
