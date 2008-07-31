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

import org.eclipse.swt.widgets.Display;
import org.jmule.core.JMThread;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:59 $$
 */
public class SWTThread {

	private static SWTThread instance = null;
	
	public static SWTThread getInstance() {
		if (instance==null)
			instance = new SWTThread();
		
		return instance;
	}
	
	private Display display;
	
	private JMSWTThread swt_thread;
	
	private boolean display_created = false;
	
	private SWTThread() {
		
		swt_thread = new JMSWTThread();
		
		
	}
	
	public void initialize() {
		
		swt_thread.start();
		
		while(!display_created) ;
		
	}
	
	public void start() {
		
		synchronized(swt_thread) {
			
			swt_thread.notify();
			
		}
		
	}
	
	public Display getDisplay() {
		return this.display;
	}
	
	private class JMSWTThread extends JMThread {
	
		public JMSWTThread() {
			
			super("SWT Thread");
			
		}
	
		public void run() {
		
			display_created = false;
			
			display = Display.getCurrent();
			if ( display == null )
				display = new Display();
		
			display_created = true;
			
			synchronized(this) {
				
				try {
					this.wait();
				} catch (InterruptedException e) {
					
				}
				
			}
			
			try {
		
			while (!display.isDisposed ()) {
				if (!display.readAndDispatch ()) 
					display.sleep ();
			}}catch(Throwable t) {
				t.printStackTrace();
			}
		
			// Dispose was moved to JMuleSWTUI
		}
	
	}
	
}
