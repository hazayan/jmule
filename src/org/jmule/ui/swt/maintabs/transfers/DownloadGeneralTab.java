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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.jmule.core.downloadmanager.DownloadSession;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.Refreshable;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.common.GapListPainter;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.utils.FileFormatter;
import org.jmule.ui.utils.SpeedFormatter;

/**
 * Created on Aug 7, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/28 16:26:31 $
 */
public class DownloadGeneralTab extends CTabItem implements Refreshable {

	private DownloadSession download_session;
	private Label part_status,download_status,sources_count,download_size,remaining_label,speed;
	private GapListPainter gap_list_painter;
	private Canvas total_progress;
	public DownloadGeneralTab(CTabFolder tabFolder, final DownloadSession downloadSession) {
		super(tabFolder, SWT.NONE);
		
		download_session = downloadSession;
		setText(Localizer._("downloadinfowindow.tab.general.title"));
		JMuleUI ui_instance = null;
		try {
		    ui_instance = JMuleUIManager.getJMuleUI();
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		Composite content = new Composite(tabFolder,SWT.NONE);
		setControl(content);
		content.setLayout(new GridLayout(1,false));
		
		Label label;
		
		
		Group transfer = new Group(content,SWT.NONE);
		transfer.setLayout(new GridLayout(2,false));
		transfer.setText(Localizer._("downloadinfowindow.tab.general.group.transfer"));
		transfer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.totalprogress")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		total_progress = new Canvas(transfer,SWT.NONE);
		GridData grid_data = new GridData(GridData.FILL_HORIZONTAL);
		grid_data.heightHint = 20;
		total_progress.setLayoutData(grid_data);
		gap_list_painter = new GapListPainter(downloadSession.getGapList(),downloadSession.getFileSize());
		gap_list_painter.setMarginWidth(0);
		total_progress.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				gap_list_painter.draw(arg0.gc,0,0,arg0.width,arg0.height);
			}
		});
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.sources")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		sources_count = new Label(transfer,SWT.NONE);
		sources_count.setFont(skin.getLabelFont());
		sources_count.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sources_count.setText(downloadSession.getPeersCount()+"");
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.transferred")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		download_size = new Label(transfer,SWT.NONE);
		download_size.setFont(skin.getLabelFont());
		download_size.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		download_size.setText(FileFormatter.formatFileSize(downloadSession.getTransferredBytes()));
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.remaining")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		remaining_label = new Label(transfer,SWT.NONE);
		remaining_label.setFont(skin.getLabelFont());
		remaining_label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		long remaining = (downloadSession.getFileSize() - downloadSession.getTransferredBytes());
		remaining_label.setText(FileFormatter.formatFileSize(remaining));
		
		label = new Label(transfer,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.download_speed")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		speed = new Label(transfer,SWT.NONE);
		speed.setFont(skin.getLabelFont());
		speed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String download_speed = SpeedFormatter.formatSpeed(downloadSession.getSpeed());
		speed.setText(download_speed);
		
		Group general = new Group(content,SWT.NONE);
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth = 10;
		general.setLayout(layout);
		general.setText(Localizer._("downloadinfowindow.group.general"));
		general.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.filename")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(downloadSession.getSharingName());
		label.setToolTipText(downloadSession.getSharingName());
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.partmet")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(downloadSession.getMetFilePath());
		label.setToolTipText(downloadSession.getMetFilePath());
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.filehash")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(downloadSession.getFileHash()+"");
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.filesize")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(general,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(FileFormatter.formatFileSize(downloadSession.getFileSize()));
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.parts")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label.setFont(skin.getLabelFont());
		part_status = new Label(general,SWT.NONE);
		part_status.setFont(skin.getLabelFont());
		part_status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		part_status.setText(downloadSession.getPartCount()+"; "+Localizer._("downloadinfowindow.tab.general.label.parts_available")+" : "+downloadSession.getAvailablePartCount());
		
		label = new Label(general,SWT.NONE);
		label.setText(Localizer._("downloadinfowindow.tab.general.label.status")+" : ");
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		download_status = new Label(general,SWT.NONE);
		download_status.setFont(skin.getLabelFont());
		download_status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		download_status.setText(downloadSession.getStatus()==DownloadSession.STATUS_STARTED ? Localizer._("mainwindow.transferstab.downloads.column.status.started") : Localizer._("mainwindow.transferstab.downloads.column.status.stopped") );
		
		label = new Label(general,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("downloadinfowindow.tab.general.label.ed2k_link") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label = new Label(general,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(downloadSession.getED2KLink().getAsString());
		label.setToolTipText(_._("downloadinfowindow.tab.general.label.ed2k_link.tooltip"));
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		label.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		label.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event arg0) {
				Utils.setClipBoardText(downloadSession.getED2KLink().getAsString());
			}
		});
		
	}
	
	public void refresh() {
		if (isDisposed()) return ;
		if (!getParent().getSelection().equals(this)) return ;
		part_status.setText(download_session.getPartCount()+"; "+Localizer._("downloadinfowindow.tab.general.label.parts_available")+" : "+download_session.getAvailablePartCount());
		download_status.setText(download_session.getStatus()==DownloadSession.STATUS_STARTED ? Localizer._("mainwindow.transferstab.downloads.column.status.started") : Localizer._("mainwindow.transferstab.downloads.column.status.stopped") );
		
		total_progress.redraw();
		
		sources_count.setText(download_session.getPeersCount()+"");
		download_size.setText(FileFormatter.formatFileSize(download_session.getTransferredBytes()));
		long remaining = (download_session.getFileSize() - download_session.getTransferredBytes());
		remaining_label.setText(FileFormatter.formatFileSize(remaining));
		String download_speed = SpeedFormatter.formatSpeed(download_session.getSpeed());
		speed.setText(download_speed);
	}

	protected void checkSubclass() {}
	
}
