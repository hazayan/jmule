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
package org.jmule.ui.swt.tables;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jmule.core.edonkey.impl.Server;
import org.jmule.ui.UIImageRepository;
import org.jmule.ui.swt.SWTConstants;
import org.jmule.ui.swt.SWTPreferences;
import org.jmule.ui.swt.Utils;

/**
 * 
 * @author binary256
 * @version $$Revision: 1.1 $$
 * Last changed by $$Author: javajox $$ on $$Date: 2008/07/31 16:44:17 $$
 */
public abstract class JMTable<T> extends Table{
	public static final Color ROW_ALTERNATE_COLOR_1 = new Color(Utils.getDisplay(),238,238,238);
	public static final Color ROW_ALTERNATE_COLOR_2 = Utils.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	
	protected List<BufferedTableRow> line_list = new LinkedList<BufferedTableRow>();
	protected List<BufferedTableRow> default_line_list = new LinkedList<BufferedTableRow>();
	private Listener column_move_listener;
	private Listener column_resize_listener;
	private SWTPreferences swt_preferences = SWTPreferences.getInstance();
	protected boolean is_sorted = false;
	protected String last_sort_column = "";
	protected boolean last_sort_dir = true;
	
	public JMTable(Composite composite,boolean multiSelect) {
		super(composite, multiSelect?SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | 
				SWT.MULTI  : SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );		
		setHeaderVisible(true);
		setLinesVisible (true);
		
		column_move_listener = new Listener() {
			public void handleEvent(Event arg0) {
				saveColumnSettings();
			}
			
		};
		
		column_resize_listener = new Listener() {
			public void handleEvent(Event arg0) {
				saveColumnSettings();
			}
		};
		
//		addListener(SWT.SetData, new Listener(){
//			public void handleEvent(Event arg0) {
//				TableItem item = (TableItem) arg0.item;
//				int index = indexOf(item);
//				T object = (T)line_list.get(index).getData(SWTConstants.ROW_OBJECT_KEY);
//				updateLine(object);
//			}
//		});
		
	}
	
	protected abstract void updateLine(T object);
	
	protected abstract int compareObjects(T object1, T object2, String columnID,boolean order);
	
	public void addColumn(String ColumnID,String column,int width) {
		TableColumn table_column = new TableColumn(this, SWT.NONE);
		table_column.setData(SWTConstants.COLUMN_NAME_KEY, ColumnID);
		table_column.setWidth(width);
		table_column.setText(column);
		table_column.setMoveable(true);
		table_column.addListener(SWT.Move, column_move_listener);
		table_column.addListener(SWT.Resize, column_resize_listener);
		
		Listener sort_listener = new Listener() {
			private int sort_order = 0;
			public void handleEvent(Event e) {
				TableColumn column = (TableColumn)e.widget;
				String column_id = (String)column.getData(SWTConstants.COLUMN_NAME_KEY);
				if (is_sorted)
					if (!last_sort_column.equals(column_id))
						sort_order=0;
				clearColumnImages();
				if (sort_order == 0) {
					sort_order = SWT.UP;
				}
				else
					if (sort_order == SWT.UP)
						sort_order = SWT.DOWN;
					else
						if (sort_order == SWT.DOWN) {
							sort_order = 0;
							is_sorted = false;
						}
				
				if (sort_order == SWT.UP)
					sortColumn(column_id, true);
				
				if (sort_order == SWT.DOWN) {
					sortColumn(column_id, false);
				}
				
				if (sort_order == 0) ;

				if (sort_order == SWT.UP) {
					column.setImage(new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("sort-down.png")));
				}
				
				if (sort_order == SWT.DOWN) {
					column.setImage(new Image(Utils.getDisplay(),UIImageRepository.getImageAsStream("sort-up.png")));
				}
				
				if (sort_order == 0) {
					column.setImage(null);
					resetOrdering();
				}
			}
			
		};
		table_column.addListener(SWT.Selection, sort_listener);
	}
	
	public void addLine(T object) {
        BufferedTableRow table_row = new BufferedTableRow(this);
        line_list.add(table_row);
        default_line_list.add(table_row);
        table_row.createSWTRow();
        if (getItemCount()%2==0)
        	table_row.setBackgrounColor(ROW_ALTERNATE_COLOR_1);
        else
        	table_row.setBackgrounColor(ROW_ALTERNATE_COLOR_2);
        table_row.setData(SWTConstants.ROW_OBJECT_KEY, object);
	}
	
	public void setLineText(T object,int columnID,String text) {
		int line_id = getObjectID(object);
		if (line_id==-1) {
			return ;
		}
		BufferedTableRow row = line_list.get(line_id);
		row.setText(columnID, text);
	}
	
	public void setLineImage(T object,int columnID,Image image) {
		int line_id = getObjectID(object);
		if (line_id==-1) return;
		BufferedTableRow row = line_list.get(line_id);
		row.setImage(columnID, image);
	}
	
	private void clearColumnImages() {
		for(int i = 0;i<getColumnCount();i++) {
			getColumn(i).setImage(null);
		}
	}
	
	public void clear() {
        line_list.clear();
        removeAll();
    }
	
	public T getSelectedObject() {
		int i = getSelectionIndex();
		if (i==-1) return null;
		return (T)line_list.get(i).getData(SWTConstants.ROW_OBJECT_KEY);
	}
	
	public List<T> getSelectedObjects() {
		int indexes[] = getSelectionIndices();
		List<T> selected_objects = new LinkedList<T>();
		for(int i = 0;i<indexes.length;i++) {
			BufferedTableRow row = line_list.get(indexes[i]);
			selected_objects.add((T)row.getData(SWTConstants.ROW_OBJECT_KEY));
		}
		return selected_objects;
	}
	
	public BufferedTableRow getRow(T object) {
		int id = getObjectID(object);
		if (id!=-1) {
			return line_list.get(id);
		}
		return null;
	}
	
	public void setForegroundColor(T object,final Color color) {
		final BufferedTableRow item = getRow(object);
		if (item==null) return ;
	   	item.setForeground(color);
	}
	
	public void setImage(T object,final int id,final Image image) {
		int line_id = getObjectID(object);
		if (line_id==-1) return;
		line_list.get(line_id).setImage(id, image);
		
	}
	
	public void removeRow(T object) {
		int id = getObjectID(object);
		if (id==-1) return ;
		if (id!=getItemCount()) {
			
		}
		remove(id);
		line_list.remove(id);
		
		for(id = 0;id<getItemCount();id++) {
			BufferedTableRow row = line_list.get(id);
			if ((id)%2==0)
				row.setBackgrounColor(ROW_ALTERNATE_COLOR_2);
			else
				row.setBackgrounColor(ROW_ALTERNATE_COLOR_1);
		}
		
	}
	
	protected void updateColumnVisibility() {
		for(int i = 0;i<this.getColumnCount();i++) {
			TableColumn column = this.getColumn(i);
			
			String column_id = (String)column.getData(SWTConstants.COLUMN_NAME_KEY);

			boolean column_visibility = swt_preferences.isColumnVisible(column_id);
			if (!column_visibility) {
				if (column.getWidth()!=0) {
				column.setWidth(0);
				column.setResizable(false);
				}				
			} else {
				if (column.getWidth()==0) {
					column.setWidth(swt_preferences.getDefaultColumnWidth((String)column.getData(SWTConstants.COLUMN_NAME_KEY)));
					column.setResizable(true);
					}
			}
		}
	}
	
	protected void updateColumnOrder() {
		int column_order[] = new int[getColumnCount()];
		for(int i = 0; i < this.getColumnCount(); i++ ) {
			TableColumn column = getColumn(i);
			String column_id = (String)column.getData(SWTConstants.COLUMN_NAME_KEY);
			int pos = swt_preferences.getColumnOrder(column_id);
			column_order[pos] = i;
		}
		
		setColumnOrder(column_order);
	}
	
	protected void saveColumnSettings() {
		if (!isVisible()) return ;
		int column_order[];
		column_order = getColumnOrder();
		for(int i = 0; i < this.getColumnCount(); i++ ) {
			int column_id = column_order[i];
			TableColumn table_column = getColumn(column_id);
			
			String column_str_id = (String)table_column.getData(SWTConstants.COLUMN_NAME_KEY);
			swt_preferences.setColumnOrder(column_str_id, i);
			if (table_column.getWidth()==0)
				swt_preferences.setColumnVisibility(column_str_id, false);
			else 
				swt_preferences.setColumnWidth(column_str_id, table_column.getWidth());
		}
	}
	
	protected void showColumnEditorWindow(TableStructureModificationListener listener) {
		new TableColumnEditorWindow(this,this.getShell(),getColumns(),listener);
	}
	
	protected int getFirstColumn() {
		int columns[] = this.getColumnOrder();
		int id = 0;
		int first_column = columns[id];
		
		while((getColumn(first_column).getWidth()==0)&&(id<getColumnCount())) 
			first_column = columns[++id];
		
		if (id<getColumnCount()) return -1;
		return first_column;
	}
	
	private int getObjectID(T object) {
		int id = 0;
		boolean found = false;
		for(BufferedTableRow row : line_list) {
			T o = (T)row.getData(SWTConstants.ROW_OBJECT_KEY);
			if (o!=null)
			if (object.equals(o)) {
				found = true;
				break;
			}
			id++;
		}
		
		if (found) return id;
		return -1;
		
	}
	
	private boolean is_sorting = false;
	protected void sortColumn(final String columnID,final boolean sortOrder) {
		if (is_sorting) { return ;}
		is_sorting = true;
		is_sorted = true;
		last_sort_column = columnID;
		last_sort_dir = sortOrder;
		synchronized(this) {
			Collections.sort(line_list, new Comparator() {
					public int compare(Object arg0, Object arg1) {
						BufferedTableRow row1 = (BufferedTableRow)arg0;
						BufferedTableRow row2 = (BufferedTableRow)arg1;
						
						T object1 = (T) row1.getData(SWTConstants.ROW_OBJECT_KEY);
						T object2 = (T) row2.getData(SWTConstants.ROW_OBJECT_KEY);
						return compareObjects(object1,object2,columnID,sortOrder);
				} });
			updateIndexes();
			is_sorting = false;
		}
	}
	
	/**
	 * Set to default order
	 */
	private void resetOrdering() {
		synchronized(line_list) {
			line_list.clear();
			for(BufferedTableRow row : default_line_list) {
				line_list.add(row);
			}
			updateIndexes();
		}
		
		
	}
	
	private void updateIndexes() {
		
		for(int i = 0;i<line_list.size();i++) {
			line_list.get(i).setTableItem(i, false);
		}
		
	}
	
	
	protected void checkSubclass() {}

}
