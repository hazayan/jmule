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
package org.jmule.ui.swt.maintabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:37 $$
 */
public class SharedTab extends AbstractTab {

	public SharedTab(Composite shell) {
		super(shell);
		
		setLayout(new FillLayout());
		
		Label l = new Label(this,SWT.NONE);
		
		l.setText("shared");
		
	}

	public int getTabType() {

		return TAB_SHARED;
	}

	@Override
	public void lostFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void obtainFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disposeTab() {
		// TODO Auto-generated method stub
		
	}

}
