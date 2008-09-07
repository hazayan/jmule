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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jmule.core.edonkey.impl.ED2KFileLink;
import org.jmule.core.searchmanager.SearchResultItem;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIComponent;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.localizer._;
import org.jmule.ui.swt.SWTImageRepository;
import org.jmule.ui.swt.SWTThread;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;
import org.jmule.ui.utils.FileFormatter;

/**
 * Created on Aug 16, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/09/07 15:23:25 $
 */
public class SearchPropertiesWindow implements JMuleUIComponent {

	private Shell shell;
	private SearchResultItem search_result;
	
	public SearchPropertiesWindow(SearchResultItem item) {
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		search_result = item;
		
		final Shell shell1=new Shell(SWTThread.getDisplay(),SWT.ON_TOP);
		shell=new Shell(shell1,SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		shell.setImage(SWTImageRepository.getImage("info.png"));
		shell.setText(_._("searchpropertieswindow.title"));
		shell.setSize(500, 310);
		
		Utils.centreWindow(shell);
		shell.setLayout(new GridLayout(1,false));
		Composite search_fields = new Composite(shell,SWT.BORDER);
		search_fields.setLayout(new GridLayout(2,false));
		search_fields.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_BOTH));

		Label label;
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.filename")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(search_result.getFileName());
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.filesize")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(FileFormatter.formatFileSize(search_result.getFileSize()));

		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.availability")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText((search_result.getFileAviability())+"");
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.completesrc")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText((search_result.getFileCompleteSrc())+"");
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.type")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		byte[] fileType = search_result.getMimeType();
		label.setText(FileFormatter.formatMimeType(fileType));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(_._("searchpropertieswindow.label.filehash")+" : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(search_result.getFileHash().getAsString());

		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getLabelFont());
		label.setText(Localizer._("sharedfilepropertieswindow.label.ed2k_link") + " : ");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		label = new Label(search_fields,SWT.NONE);
		label.setFont(skin.getDefaultFont());
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(search_result.getAsED2KLink().getAsString());
		label.setToolTipText(_._("searchpropertieswindow.label.ed2k_link.tooltip"));
		label.setForeground(SWTThread.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		label.setCursor(new Cursor(SWTThread.getDisplay(),SWT.CURSOR_HAND));
		label.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event arg0) {
				Utils.setClipBoardText(search_result.getAsED2KLink().getAsString());
			}
		});
		
		Composite button_bar = new Composite(shell,SWT.NONE);
		button_bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout = new GridLayout(2,false);
		button_bar.setLayout(layout);
	
		Button button = new Button(button_bar,SWT.NONE);
		button.setFont(skin.getButtonFont());
		button.setText(_._("searchpropertieswindow.button.close"));
		
		GridData grid_data = new GridData();
		grid_data.horizontalAlignment = GridData.END;
		grid_data.widthHint = 60;
		grid_data.grabExcessHorizontalSpace = true;
		button.setLayoutData(grid_data);
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.close();
			}
		});
	}
	
	public void getCoreComponents() {
		
	}

	public void initUIComponents() {
		shell.open();
	}

}