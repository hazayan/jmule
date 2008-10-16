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
package org.jmule.ui.swt.settingswindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.jmule.core.JMuleCore;
import org.jmule.ui.JMuleUIManager;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * Created on Aug 19, 2008
 * @author binary256
 * @version $Revision: 1.2 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/16 18:20:02 $
 */
public abstract class AbstractTab extends Composite {

	protected JMuleCore _core;
	protected Group content;
	protected SWTSkin skin;
	
	public AbstractTab(Composite parent, JMuleCore core) {
		super(parent, SWT.NONE);

		_core = core;
		setLayout(new FillLayout());
		
		content = new Group(this,SWT.NONE);
		
		skin = null;
		try {
		    skin = (SWTSkin)JMuleUIManager.getJMuleUI().getSkin();
		}catch(Throwable t) {
		}
		
		
	}
	
	public abstract Image getImage();
	
	public abstract String getTabName();
	
	public abstract boolean checkFields();

	public abstract void save();
	
	protected void checkSubclass() { }


}
