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
package org.jmule.ui.swt.maintabs.transfers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jmule.core.JMIterable;
import org.jmule.core.edonkey.impl.Peer;
import org.jmule.core.uploadmanager.UploadSession;
import org.jmule.countrylocator.CountryLocator;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.UICommon;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.common.CountryFlagPainter;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.swt.tables.JMTable;
import org.jmule.ui.swt.tables.TableItemCountryFlag;
import org.jmule.ui.utils.PeerInfoFormatter;
import org.jmule.ui.utils.SpeedFormatter;
import org.jmule.util.Misc;

/**
 * Created on Aug 11, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/18 07:03:27 $
 */
public class UploadPeerListTab extends CTabItem implements Refreshable{

	private UploadSession upload_session;
	private UploadPeerList peers_table;
	private List<Peer> shown_peers = new LinkedList<Peer>();
	private Menu popup_menu;
	
	public UploadPeerListTab(CTabFolder tabFolder, UploadSession uploadSession) {
		super(tabFolder, SWT.NONE);

		upload_session = uploadSession;
		
		setText(Localizer._("uploadinfowindow.tab.peerlist.title"));
		JMuleUI ui_instance = null;
		try {
		    ui_instance = JMuleUIManager.getJMuleUI();
		}catch(Throwable t) {
		}
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		Composite content = new Composite(tabFolder,SWT.BORDER);
		setControl(content);
		content.setLayout(new GridLayout(1,true));
		peers_table =  new UploadPeerList(content);
		peers_table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		popup_menu = new Menu(peers_table);
		
		MenuItem column_setup = new MenuItem(popup_menu,SWT.NONE);
		column_setup.setText(_._("uploadinfowindow.tab.peerlist.menu.column_setup"));
		column_setup.setImage(SWTImageRepository.getImage("columns_setup.png"));
		column_setup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				peers_table.showColumnEditorWindow();
			}
		});
		
	}
	
	public void refresh() {
		if (isDisposed()) return ;
		JMIterable<Peer> peers = upload_session.getPeers();

		for(Peer peer : shown_peers) {
			if (!upload_session.hasPeer(peer))
				peers_table.removeRow(peer);
		}

		for(Peer peer : peers) {
			if (!shown_peers.contains(peer)){
				shown_peers.add(peer);
				peers_table.addRow(peer);
			}
			peers_table.updateRow(peer);
		}
	}

	private class UploadPeerList extends JMTable<Peer> {

		public UploadPeerList(Composite composite) {
			super(composite, SWT.NONE);
			int width;
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.nickname"), _._("uploadinfowindow.tab.peerlist.column.nickname.desc"),	SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID));
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.DOWNLOAD_PEER_LIST_CC_COLUMN_ID);
			addColumn(SWT.CENTER, SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID, 		_._("uploadinfowindow.tab.peerlist.column.country_code"), _._("uploadinfowindow.tab.peerlist.column.country_code.desc"), 		width );
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID);
			
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID, 		_._("uploadinfowindow.tab.peerlist.column.flag"), _._("uploadinfowindow.tab.peerlist.column.flag.desc"), 		width );
			if (CountryLocator.getInstance().isServiceDown())
				disableColumn(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID);
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID, 			_._("uploadinfowindow.tab.peerlist.column.address"), _._("uploadinfowindow.tab.peerlist.column.address.desc"), width);
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.up_speed"),_._("uploadinfowindow.tab.peerlist.column.up_speed.desc"), width);
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.software"), _._("uploadinfowindow.tab.peerlist.column.software.desc"), 	width);
			
			width = SWTPreferences.getInstance().getColumnWidth(SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID);
			addColumn(SWT.LEFT, SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID, _._("uploadinfowindow.tab.peerlist.column.status"), _._("uploadinfowindow.tab.peerlist.column.status.desc"), width);

			updateColumnVisibility();
			updateColumnOrder();
			
		}
		
		public void addRow(Peer object) {
			super.addRow(object);
			if (!CountryLocator.getInstance().isServiceDown()) {
				Image image = new Image(SWTThread.getDisplay(),UICommon.getCountryFlag(object.getAddress()));
				
				CountryFlagPainter painter = new CountryFlagPainter(image);
				TableItemCountryFlag table_item_painter = new TableItemCountryFlag(SWTPreferences.getDefaultColumnOrder(SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID),painter);
				addCustumControl(getItemCount()-1, table_item_painter);
			}
		}

		protected int compareObjects(Peer object1, Peer object2,
				int columnID, boolean order) {
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getUploadSpeed",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getAddress",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getNickName",order);

			if (columnID == SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID)
				return Misc.compareAllObjects(object1,object2,"getClientSoftware",order);
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID) {
				String country1 = CountryLocator.getInstance().getCountryCode(object1.getAddress());
				String country2 = CountryLocator.getInstance().getCountryCode(object2.getAddress());
				int result = country1.compareTo(country2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_FLAG_COLUMN_ID) {
				String country1 = CountryLocator.getInstance().getCountryName(object1.getAddress());
				String country2 = CountryLocator.getInstance().getCountryName(object2.getAddress());
				int result = country1.compareTo(country2);
				if (order)
					return result;
				else
					return Misc.reverse(result);
			}
			
			if (columnID == SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID) {
				int id1 = upload_session.getPeerPosition(object1);
				int id2 = upload_session.getPeerPosition(object2);
				
				int result = 0;
				if (id1>id2) result = 1;
				if (id1<id2) result = -1;
				if (!order) return Misc.reverse(result);
				return result;
				
			}
			
			return 0;
		}

		protected Menu getPopUpMenu() {
			return popup_menu;
		}

		public void updateRow(Peer object) {
			
			String country_code = CountryLocator.getInstance().getCountryCode(object.getAddress());
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_CC_COLUMN_ID, country_code);
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_IP_COLUMN_ID, 		object.getAddress()+":"+object.getPort());
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_NICKNAME_COLUMN_ID, object.getNickName());
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_SOFTWARE_COLUMN_ID, PeerInfoFormatter.formatPeerSoftware(object));
			
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_UPLOAD_SPEED_COLUMN_ID, SpeedFormatter.formatSpeed(object.getUploadSpeed()));
			
			int rank = upload_session.getPeerPosition(object);
			String str="";
			if (rank!=0) {
				str = Localizer.getString("uploadinfowindow.tab.peerlist.column.status.queue",rank+"");
			} else {
				str = _._("uploadinfowindow.tab.peerlist.column.status.uploading");
			}
			setRowText(object, SWTConstants.UPLOAD_PEER_LIST_STATUS_COLUMN_ID, 	str);

		}
		
	}
	
}
