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
package org.jmule.ui.swt.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.jmule.core.sharingmanager.Gap;
import org.jmule.core.sharingmanager.GapList;
import org.jmule.ui.swt.SWTThread;

/**
 * Created on Aug 02 2008
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/07 16:40:14 $$
 */
public class GapListPainter {
	
	private GapList gap_list;
	private long file_size;
	
    private static Color blue  = new Color(SWTThread.getDisplay(), new RGB(0, 128, 255));
    private static Color green = new Color(SWTThread.getDisplay(), new RGB(72, 179, 72));
    private static Color black = new Color(SWTThread.getDisplay(), new RGB(0, 0, 0));
    private static Color white = new Color(SWTThread.getDisplay(), new RGB(255, 255, 255));
	private static final int PROGRESS_BAR_HEIGHT = 3;
	private int margin_width = 3;
		
	public GapListPainter(GapList gapList,long fileSize) {
		gap_list = gapList;
		file_size = fileSize;
	}
	
	public void setData(GapList gapList, long fileSize) {
		gap_list = gapList;
		file_size = fileSize;
	}
	
	public GapList getGapList() {
		return gap_list;
	}
	
	public long getFileSize() {
		return file_size;
	}
	
	public void setMarginWidth(int marginWidth) {
		margin_width = marginWidth;
	}
	
	public void draw(GC gc, int x,int y, int width, int height) {
		width -=margin_width;
		height -= margin_width;
		x+=margin_width;
		y+=margin_width;
		float k = (float)(width)/(float)(file_size);
		//Gaps
		
		gc.setBackground(blue);
		gc.fillRectangle(new Rectangle(x,y,width,height));
		gc.setBackground(white);
		for(Gap gap : gap_list.getGaps()) {
			int startPos = (int)(gap.getStart()*k);
			int length = (int)((gap.getEnd() - gap.getStart()) * k);
			Rectangle rect = new Rectangle(x + startPos,y + 0,length,height);
			gc.fillRectangle(rect);
		}
		//Draw progress bar
		long downloaded = file_size - gap_list.byteCount();
		long progress = Math.round(((downloaded* 100f)/ (float) file_size));
		k = (float)(width)/100f;
		Rectangle progress_bar = new Rectangle(x, y,(int)( width), PROGRESS_BAR_HEIGHT);
		gc.setBackground(black);
		gc.fillRectangle(progress_bar);
		progress_bar = new Rectangle(x,y,(int)(progress* k),PROGRESS_BAR_HEIGHT);
		gc.setBackground(green);
		gc.fillRectangle(progress_bar);
	}
	
}
