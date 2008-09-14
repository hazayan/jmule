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

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.3 $$
 * Last changed by $$Author: binary256_ $$ on $$Date: 2008/09/14 11:46:59 $$
 */
public class Utils {
	
	public static String getClipboardText() {
	    final Clipboard cb = new Clipboard(SWTThread.getDisplay());
	    final TextTransfer transfer = TextTransfer.getInstance();
	    
	    String data = (String)cb.getContents(transfer);
	    if (data==null) return "";
	    return data;
	}
	
	public static boolean showConfirmMessage(Shell shell, String title,String message) {
		MessageBox dialog = new MessageBox(shell,
                SWT.YES | SWT.NO | SWT.ICON_WARNING);
		dialog.setText(title);
		dialog.setMessage(message);
		int result = dialog.open();
		return result==SWT.YES;
	}
	
	public static void showWarningMessage(Shell shell,String title,String message) {
		MessageBox dialog = new MessageBox(shell,
                SWT.OK | SWT.ICON_WARNING);
		dialog.setText(title);
		dialog.setMessage(message);
		dialog.open();
	}
	
	public static void setClipBoardText(String text) {
		final Clipboard cb = new Clipboard(SWTThread.getDisplay());
		final TextTransfer transfer = TextTransfer.getInstance();
		
		cb.setContents(new String[]{text}, new Transfer[]{transfer});
	}
	
	/**
	 * Center shell
	 * @param shell shell which is needed to center
	 */
	public static void centreWindow(Shell shell) {
		Rectangle displayArea; // area to center in
		try {
			displayArea = shell.getMonitor().getClientArea();
		} catch (NoSuchMethodError e) {
			displayArea = shell.getDisplay().getClientArea();
		}

		Rectangle shellRect = shell.getBounds();

		if (shellRect.height > displayArea.height) {
			shellRect.height = displayArea.height;
		}
		if (shellRect.width > displayArea.width - 50) {
			shellRect.width = displayArea.width;
		}

		shellRect.x = displayArea.x + (displayArea.width - shellRect.width) / 2;
		shellRect.y = displayArea.y + (displayArea.height - shellRect.height) / 2;

		shell.setBounds(shellRect);
	}
	
	public static boolean drawImage(GC gc, Image image, Rectangle dstRect,
			Rectangle clipping, int hOffset, int vOffset, boolean clearArea)
	{
		return drawImage(gc, image, new Point(0, 0), dstRect, clipping, hOffset,
				vOffset, clearArea);
	}

	public static boolean drawImage(GC gc, Image image, Rectangle dstRect,
			Rectangle clipping, int hOffset, int vOffset)
	{
		return drawImage(gc, image, new Point(0, 0), dstRect, clipping, hOffset,
				vOffset, false);
	}

	public static boolean drawImage(GC gc, Image image, Point srcStart,
			Rectangle dstRect, Rectangle clipping, int hOffset, int vOffset,
			boolean clearArea)
	{
		Rectangle srcRect;
		Point dstAdj;

		if (clipping == null) {
			dstAdj = new Point(0, 0);
			srcRect = new Rectangle(srcStart.x, srcStart.y, dstRect.width,
					dstRect.height);
		} else {
			if (!dstRect.intersects(clipping)) {
				return false;
			}

			dstAdj = new Point(Math.max(0, clipping.x - dstRect.x), Math.max(0,
					clipping.y - dstRect.y));

			srcRect = new Rectangle(0, 0, 0, 0);
			srcRect.x = srcStart.x + dstAdj.x;
			srcRect.y = srcStart.y + dstAdj.y;
			srcRect.width = Math.min(dstRect.width - dstAdj.x, clipping.x
					+ clipping.width - dstRect.x);
			srcRect.height = Math.min(dstRect.height - dstAdj.y, clipping.y
					+ clipping.height - dstRect.y);
		}

		if (!srcRect.isEmpty()) {
			try {
				if (clearArea) {
					gc.fillRectangle(dstRect.x + dstAdj.x + hOffset, dstRect.y + dstAdj.y
							+ vOffset, srcRect.width, srcRect.height);
				}
				gc.drawImage(image, srcRect.x, srcRect.y, srcRect.width,
						srcRect.height, dstRect.x + dstAdj.x + hOffset, dstRect.y
								+ dstAdj.y + vOffset, srcRect.width, srcRect.height);
			} catch (Exception e) {
				System.out.println("drawImage: " + e.getMessage() + ": " + image + ", " + srcRect
						+ ", " + (dstRect.x + dstAdj.y + hOffset) + ","
						+ (dstRect.y + dstAdj.y + vOffset) + "," + srcRect.width + ","
						+ srcRect.height + "; imageBounds = " + image.getBounds());
			}
		}

		return true;
	}
	
	
	/**
	 * Bottom Index may be negative
	 */ 
	public static int getTableBottomIndex(Table table, int iTopIndex) {
		// on Linux, getItemHeight is slow AND WRONG. so is getItem(x).getBounds().y 
		// getItem(Point) is slow on OSX

		int itemCount = table.getItemCount();
		if (!table.isVisible() || iTopIndex >= itemCount)
			return -1;
		
//		if (Constants.isOSX) {
//			try {
//				TableItem item = table.getItem(iTopIndex);
//				Rectangle bounds = item.getBounds();
//				Rectangle clientArea = table.getClientArea();
//	
//				int itemHeight = table.getItemHeight();
//				int iBottomIndex = Math.min(iTopIndex
//						+ (clientArea.height + clientArea.y - bounds.y - 1) / itemHeight,
//						itemCount - 1);
//	
//	//			System.out.println(bounds + ";" + clientArea + ";" + itemHeight + ";bi="
//	//					+ iBottomIndex + ";ti=" + iTopIndex + ";"
//	//					+ (clientArea.height + clientArea.y - bounds.y - 1));
//				return iBottomIndex;
//			} catch (NoSuchMethodError e) {
//				// item.getBounds is 3.2
//				return Math.min(iTopIndex
//						+ ((table.getClientArea().height - table.getHeaderHeight() - 1) / 
//								table.getItemHeight()) + 1, table.getItemCount() - 1);
//			}
//		}

		// getItem will return null if clientArea's height is smaller than
		// header height.
		int areaHeight = table.getClientArea().height;
		if (areaHeight <= table.getHeaderHeight())
			return -1;

		// 2 offset to be on the safe side
		TableItem bottomItem = table.getItem(new Point(2,
				table.getClientArea().height - 1));
  	int iBottomIndex = (bottomItem != null) ? table.indexOf(bottomItem) :
			itemCount - 1;
  	return iBottomIndex;
	}
	
}
