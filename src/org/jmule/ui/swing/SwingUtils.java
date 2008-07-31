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
package org.jmule.ui.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;

/**
 * 
 * @author javajox
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:43:03 $$
 */
public final class SwingUtils {

    /**
     * Centers a window on screen.
     * <p>
     * @param w     The window to center.
     */
    public static void centerOnScreen(Window w) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension splashSize = w.getPreferredSize();
        w.setLocation(screenSize.width / 2 - (splashSize.width / 2),
                      screenSize.height / 2 - (splashSize.height / 2));
    }
    
    // Center Window on screen
    public static void centerAndSizeWindow( Window win, int fraction, int base) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width  = screenSize.width * fraction / base;
        int height = screenSize.height * fraction / base;
    
        Rectangle rect = new Rectangle( (screenSize.width - width) / 2,
            (screenSize.height - height) / 2, width, height );
        win.setBounds(rect);
    }
    
    /**
     * Maximizes a JFrame, just like the 'maximize window' button does.
     * <p>
     * @param f     The frame to maximize.
     */
    public static void maximizeJFrame(JFrame f) {
    	
        f.setExtendedState( Frame.MAXIMIZED_BOTH );
    }
	
}
