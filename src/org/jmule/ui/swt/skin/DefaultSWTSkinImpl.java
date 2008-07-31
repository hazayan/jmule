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
package org.jmule.ui.swt.skin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.skin.SkinConstants;
import org.jmule.ui.swt.Utils;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:42:49 $$
 */
public class DefaultSWTSkinImpl implements DefaultSWTSkin {

	public Font getButtonFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Color getDefaultColor() {
		return Utils.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	public Font getDefaultFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Font getLabelFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Font getMenuBarFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Font getMenuFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Font getPopupMenuFont() {
		return Utils.getDisplay().getSystemFont();
	}

	public Image getButtonImage(int imageID) {
		
		switch (imageID) {
		
			case SkinConstants.OK_BUTTON_IMAGE  : {
				return new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("ok.png"));
			}
		
			case SkinConstants.CANCEL_BUTTON_IMAGE  : {
				return new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("cancel.png"));
			}
			
			case SkinConstants.FINISH_BUTTON_IMAGE : {
				return new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("accept.png"));
			}
		}
		
		return null;
	}

}
