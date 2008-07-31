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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jmule.ui.JMuleUI;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.swt.Utils;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:11 $$
 */
public class ServerMessages extends Text {

	public ServerMessages(Composite composite) {
		super(composite,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		JMuleUI ui_instance = null;
		try {
			
		    ui_instance = JMuleUIManager.getJMuleUI();
		
		}catch(Throwable t) {
		}
		
		SWTSkin skin = (SWTSkin)ui_instance.getSkin();
		
		setFont(skin.getDefaultFont());
		setEditable(false);
		setBackground(Utils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		SWTServerListWrapper.getInstance().setServerMessages(this);
		
	}
	
	public void addText(String message) {
		String str = getText()+"\n"+message;
		setText(str);
	}
	
	protected void checkSubclass() {

    }
	
}
