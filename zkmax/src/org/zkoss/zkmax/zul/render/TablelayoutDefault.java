/* TablelayoutDefault.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 14, 2008 4:44:01 PM , Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.zul.render;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.render.ComponentRenderer;
import org.zkoss.zk.ui.render.SmartWriter;
import org.zkoss.zkmax.zul.Tablechildren;
import org.zkoss.zkmax.zul.Tablelayout;

/**
 * {@link Tablelayout}'s default mold.
 * @author robbiecheng
 * @since 3.5.0
 *
 */
public class TablelayoutDefault implements ComponentRenderer{	
	private int _curRol, _curCol;
	private List _rowinfos;	
	
	private void init(){
		if (_rowinfos != null && _rowinfos.size() > 0 )
			_rowinfos.clear();
		else
			_rowinfos = new LinkedList();
		_curRol = 0;
		_curCol = 0;
	}
	
	public void render(Component comp, Writer out) throws IOException {
		init();
		final SmartWriter wh = new SmartWriter(out);
		final Tablelayout self = (Tablelayout) comp;
		final List childern = self.getChildren();
		
		for (int i = 0; i < childern.size(); i++)
			updateCellIndex((Tablechildren) childern.get(i));
		
		wh.write("<table id=\"").write(self.getUuid()).write("\"")
			.write(self.getOuterAttrs()).write(self.getInnerAttrs())
			.write("> <tbody>");			
		int from = 0, to = 0, count = 0;
		for (int i = 0; i < this.getRowCount(); i++) {
			count = getCellCount(i);
			to = from + count - 1;
			wh.write("<tr>");
			wh.writeChildren(self, from, to);
			from = (to + 1);
			wh.write("</tr>");
		}
		wh.write("</tbody></table>");
	}
	
	private int getRowCount(){
		return _rowinfos.size();
	}	
	
	private int getCellCount(int index){
		return ((int[])_rowinfos.get(index))[0];
	}
	
	private int[] getNextNonSpan(int columns, int colIndex, int rowIndex) {
		while((_rowinfos.size() > 0 && colIndex >= (columns - 1)) || (_rowinfos.size() >= (rowIndex + 1)
				&& ((int[])_rowinfos.get(rowIndex))[1] > (colIndex + 1)) ) {	
			if (colIndex >= (columns - 1)) {
				rowIndex++;
				colIndex = 0;
			}else{
				colIndex++;
			}
			if (columns == 1)
				break;
		}
		return new int[]{colIndex, rowIndex};
	}
	
	private void updateCellIndex(Tablechildren child) {
		final int columns = ((Tablelayout)child.getParent()).getColumns();
		final int[] index = getNextNonSpan(columns, _curCol, _curRol);
		_curCol = index[0];
		_curRol = index[1];		
		for(int rowIndex = _curRol; rowIndex < _curRol + (child.getRowspan() > 1 ? child.getRowspan() : 1); rowIndex++) {			
			if (_rowinfos.size() <= rowIndex) {
				_rowinfos.add(rowIndex, new int[]{0,0});
			}
			int[] rowinfo = (int[]) _rowinfos.get(rowIndex);
			for (int colIndex = _curCol; colIndex < _curCol + (child.getColspan() > 1 ? child.getColspan() : 1); colIndex++) {
				rowinfo[1] += 1;
			}
		}
		int[] rowinfo = (int[]) _rowinfos.get(_curRol);
		rowinfo[0]++;
	}
}
