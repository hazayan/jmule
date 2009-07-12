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

import java.util.Map;

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
import org.jmule.core.jkad.Int128;
import org.jmule.core.jkad.JKadListener;
import org.jmule.core.jkad.JKadConstants.ContactType;
import org.jmule.core.jkad.JKadConstants.RequestType;
import org.jmule.core.jkad.lookup.LookupListener;
import org.jmule.core.jkad.lookup.LookupTask;
import org.jmule.core.jkad.publisher.PublishKeywordTask;
import org.jmule.core.jkad.publisher.PublishNoteTask;
import org.jmule.core.jkad.publisher.PublishSourceTask;
import org.jmule.core.jkad.publisher.PublishTask;
import org.jmule.core.jkad.publisher.PublisherListener;
import org.jmule.core.jkad.routingtable.KadContact;
import org.jmule.core.jkad.routingtable.RoutingTableListener;
import org.jmule.core.utils.Misc;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.tables.JMTable;

/**
 * Created on Jul 10, 2009
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary255 $ on $Date: 2009/07/12 09:14:50 $
 */
public class KadTab extends AbstractTab {

	public static int CLIENT_ID_COLUMN = 1;
	public static int CLIENT_DISTANCE_COLUMN = 2;
	
	public static int TASK_LOOKUP_TYPE = 1;
	public static int TASK_LOOKUP_HASH = 2;
	
	private JMuleCore _core;
	private JMTable<KadContact> contact_list;
	private JMTable<KadTask> kad_task_list;
	
	private Group routing_table_container;
	public KadTab(Composite shell, JMuleCore core) {
		
		super(shell);
		_core = core;
		
		/*if (!_core.getConfigurationManager().isJKadEnabled()) {
			setLayout(new FillLayout());
			Label label = new Label(this,SWT.CENTER);
			label.setText("Kad is disabled");
			return ;
		}*/
		setLayout(new GridLayout(1,false));
		
		final Composite tab_content = new Composite(this,SWT.NONE);
		tab_content.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab_content.setLayout(new FormLayout());
		
		routing_table_container = new Group(tab_content,SWT.NONE);
		Group kad_tasks_container = new Group(tab_content,SWT.NONE);
		routing_table_container.setText("Kad nodes");
		kad_tasks_container.setText("Kad tasks");
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
		setKadStatus.setEnabled(_core.getConfigurationManager().isJKadEnabled());
		g = new GridData();
		g.widthHint = 150;
		setKadStatus.setLayoutData(g);
		setKadStatus.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (_core.getJKad().isConnected())
					_core.getJKad().disconnect();
				else
					if (_core.getJKad().isConnecting())
						_core.getJKad().disconnect();
					else
						if (_core.getJKad().isDisconnected()) {
							setKadStatus.setEnabled(false);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									_core.getJKad().connect();
								}
							});
						}
							
			}
		});
		
		if (_core.getJKad().isConnected())
			setKadStatus.setText("Disconnect");
		if (_core.getJKad().isConnecting())
			setKadStatus.setText("Stop");
		if (_core.getJKad().isDisconnected())
			setKadStatus.setText("Connect");
		
		_core.getJKad().addListener(new JKadListener() {
			public void JKadIsConnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText("Disconnect");
					}
				});
			}

			public void JKadIsConnecting() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						setKadStatus.setText("Stop");
					}
				});
			}

			public void JKadIsDisconnected() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						setKadStatus.setEnabled(true);
						routing_table_container.setText("Kad nodes");
						setKadStatus.setText("Connect");
						
					}
				});
			}
			
		});
		
		contact_list = new JMTable<KadContact>(routing_table_container,SWT.NONE){
			protected int compareObjects(KadContact object1,
					KadContact object2, int columnID, boolean order) {
				int result = 0;
				if (columnID == CLIENT_ID_COLUMN)
					result = object1.getContactID().toHexString().compareTo(object2.getContactID().toHexString());
				
				if (columnID == CLIENT_DISTANCE_COLUMN)
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
				setRowImage(object, CLIENT_ID_COLUMN, SWTImageRepository.getImage(imageName));
				setRowText(object, CLIENT_ID_COLUMN, object.getContactID().toHexString());
				setRowText(object, CLIENT_DISTANCE_COLUMN, object.getContactDistance().toBinaryString());
			}
			
		};
		contact_list.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		contact_list.addColumn(SWT.LEFT, CLIENT_ID_COLUMN, "Contact ID", "128 bit client identifier", 300);
		contact_list.addColumn(SWT.LEFT, CLIENT_DISTANCE_COLUMN, "Contact distance", "Distance from JMule to client", 400);
		
		_core.getJKad().getRoutingTable().addListener(new RoutingTableListener() {
			public void contactAdded(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.addRow(contact);
						routing_table_container.setText("Kad nodes (" + contact_list.getItemCount() +")");
					}
					
				});
				
			}

			public void contactRemoved(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.removeRow(contact);
						routing_table_container.setText("Kad nodes (" + contact_list.getItemCount() +")");
					}
					
				});
			}

			public void contactUpdated(final KadContact contact) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						contact_list.updateRow(contact);
						routing_table_container.setText("Kad nodes (" + contact_list.getItemCount() +")");
					}
					
				});
			}

			public void allContactsRemoved() {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						contact_list.clear();
					}
					
				});
			}
			
		});
		
		for(KadContact contact : _core.getJKad().getRoutingTable().getContacts())
			contact_list.addRow(contact);
		
		routing_table_container.setText("Kad nodes (" + contact_list.getItemCount() +")");
		
		kad_tasks_container.setLayout(new FillLayout());
		kad_task_list = new JMTable<KadTask>(kad_tasks_container,SWT.NONE) {

			protected int compareObjects(KadTask object1, KadTask object2,
					int columnID, boolean order) {
				int result = 0;
				if (columnID == TASK_LOOKUP_TYPE)
					result = object1.task_type.compareTo(object2.task_type);
				if (columnID == TASK_LOOKUP_HASH)
					result = object1.task_id.compareTo(object2.task_id);
				if (!order)
					result = Misc.reverse(result);
				return result;
			}

			protected Menu getPopUpMenu() {
				return null;
			}

			public void updateRow(KadTask object) {
				setRowText(object, TASK_LOOKUP_TYPE, object.task_type);
				setRowText(object, TASK_LOOKUP_HASH, object.task_id);
			}
		};
		
		kad_task_list.addColumn(SWT.LEFT, TASK_LOOKUP_TYPE, "Type", "Kad lookup type", 300);
		kad_task_list.addColumn(SWT.LEFT, TASK_LOOKUP_HASH, "Lookup hash", "Hash value which is used in lookup", 400);
		_core.getJKad().getLookup().addListener(new LookupListener() {
			public void taskAdded(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = lookupTaskToKadTask(task);
						kad_task_list.addRow(kad_task);
					}
					
				});
			}

			public void taskRemoved(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task =  lookupTaskToKadTask(task);
						kad_task_list.removeRow(kad_task);
					}
					
				});
			}

			public void taskStarted(final LookupTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = lookupTaskToKadTask(task);
						kad_task_list.updateRow(kad_task);
					}
					
				});
			}
			
		});
		
		Map<Int128,LookupTask> list = _core.getJKad().getLookup().getLookupTasks();
		for(LookupTask task : list.values()) {
			KadTask kad_task = lookupTaskToKadTask(task);
			kad_task_list.addRow(kad_task);
		}
		
		
		/*_core.getJKad().getPublisher().addListener(new PublisherListener() {

			public void publishTaskAdded(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = publishTaskToKadTask(task);
						kad_task_list.addRow(kad_task);
					}
				});
			}

			public void publishTaskRemoved(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = publishTaskToKadTask(task);
						kad_task_list.removeRow(kad_task);
					}
				});
			}

			public void publishTaskStarted(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = publishTaskToKadTask(task);
						kad_task_list.updateRow(kad_task);
					}
				});
			}

			public void publishTaskStopped(final PublishTask task) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed()) return;
						KadTask kad_task = publishTaskToKadTask(task);
						kad_task_list.removeRow(kad_task);
					}
				});
			}
			
		});*/
		
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
		
		public int hashCode() {
			return task_id.hashCode() + task_type.hashCode();
		}
		
		public boolean equals(Object object) {
			if (object == null) return false;
			if (!(object instanceof KadTask)) return false;
			return object.hashCode()==hashCode();
		}
	}
	
	private KadTask publishTaskToKadTask(PublishTask task) {
		KadTask result = new KadTask();
		result.task_id = task.getPublishID().toHexString();
		if (task instanceof PublishKeywordTask)
			result.task_type = "Keyword publish";
		
		if (task instanceof PublishSourceTask)
			result.task_type = "Source publish";
		
		if (task instanceof PublishNoteTask)
			result.task_type = "Note publish";
		
		return result;
	}

	private KadTask lookupTaskToKadTask(LookupTask task) {
		KadTask kad_task = new KadTask();
		if (task.getRequestType() == RequestType.FIND_NODE)
			kad_task.task_type = "Node lookup";
		if (task.getRequestType() == RequestType.FIND_VALUE)
			kad_task.task_type = "Keyword search";
		if (task.getRequestType() == RequestType.STORE)
			kad_task.task_type = "Store";
		
		kad_task.task_id = task.getTargetID().toHexString();
		return kad_task;
	}
	
}
