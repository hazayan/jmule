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
package org.jmule.ui.swt;

import org.jmule.ui.JMuleUI;
import org.jmule.ui.localizer.Localizer;
import org.jmule.ui.swt.mainwindow.MainWindow;
import org.jmule.ui.swt.skin.DefaultSWTSkinImpl;
import org.jmule.ui.swt.skin.SWTSkin;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.2 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:37:40 $$
 */
public class JMuleSWTUI implements JMuleUI<SWTSkin> {
	
	private SWTSkin default_skin;
	private MainWindow main_window;
	
	public void initialize() {
		
		Localizer.initialize();
		
		SWTThread.getInstance().initialize();

		default_skin = new DefaultSWTSkinImpl();
		
		main_window = new MainWindow();
		main_window.getCoreComponents();
	}
	
	public void shutdown() {
		GUIUpdater.getInstance().JMStop();
		SWTThread.getInstance().start();
	}

	public void start() {

		GUIUpdater.getInstance();
		
		main_window.initUIComponents();
		
		SWTThread.getInstance().start();
		
		GUIUpdater.getInstance().start();
	}

	public SWTSkin getSkin() {

		return default_skin;
	}

}
