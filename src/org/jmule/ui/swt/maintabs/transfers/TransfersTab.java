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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jmule.core.JMuleCore;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.GUIUpdater;
import org.jmule.ui.swt.common.SashControl;
import org.jmule.ui.swt.maintabs.AbstractTab;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:51:24 $$
 */
public class TransfersTab extends AbstractTab{

	private Composite download_panel;
	
	private Composite upload_panel;
	
	private JMuleCore _core;
	
	private DownloadList download_list;
	private UploadList 	 upload_list;
	
	public TransfersTab(Composite shell,JMuleCore core) {
		super(shell);

		_core = core;
		
		setLayout(new FormLayout());
		
		download_panel = new Composite(this,SWT.NONE);
		upload_panel = new Composite(this,SWT.NONE);
		
		SashControl.createHorizontalSash(50, 50, this, download_panel, upload_panel);
		
		download_panel.setLayout(new FillLayout());
		upload_panel.setLayout(new FillLayout());
		
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		Group downloads = new Group(download_panel,SWT.NONE);
		downloads.setFont(skin.getDefaultFont());
		downloads.setText(Localizer._("mainwindow.transferstab.downloads"));
		downloads.setLayout(new FillLayout());
		download_list = new DownloadList(downloads,_core);
		
		Group uploads = new Group(upload_panel,SWT.NONE);
		uploads.setFont(skin.getDefaultFont());
		uploads.setText(Localizer._("mainwindow.transferstab.uploads"));
		uploads.setLayout(new FillLayout());
		upload_list = new UploadList(uploads,_core);
		
		
	}

	public JMULE_TABS getTabType() {
		return JMULE_TABS.TRANSFERS;
	}

	public void lostFocus() {
		GUIUpdater.getInstance().removeRefreshable(download_list);
		GUIUpdater.getInstance().removeRefreshable(upload_list);
	}

	public void obtainFocus() {
		GUIUpdater.getInstance().addRefreshable(download_list);
		GUIUpdater.getInstance().addRefreshable(upload_list);
	}

	public void disposeTab() {

	}

}
