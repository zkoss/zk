/* tablelayout.js

	Purpose:
		
	Description:
		
	History:
		Thu May 19 14:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var zcls = this.getZclass();
	var curRow=0, 
		curCol=0,
		rowspan,
		colspan,
		totalCol=this.getColumns(),
		colSpanList={};
	
	out.push('<table', this.domAttrs_(),'>');
		for(var child = this.firstChild; child; child = child.nextSibling){
			if(curCol == 0 || (colSpanList[curRow] && colSpanList[curRow] == curCol))
				out.push("<tr>");
				
			child.redraw(out);
			
			rowspan = child.getRowspan();
			colspan = child.getColspan();
			
			if(rowspan > 1){
				for(var row = curRow+1; row < curRow+rowspan; row++){
					if(!colSpanList[row])
						colSpanList[row] = colspan;
					else
						colSpanList[row] += colspan;
				}
			}
			
			if(curCol + colspan >= totalCol){
				curRow++;
				if(colSpanList[curRow])
					curCol = colSpanList[curRow];
				else
					curCol = 0;
				
				out.push("</tr>");
			}else{
				curCol += colspan;
			}
		}
		
		if(curCol!=0 && !(colSpanList[curRow] && colSpanList[curRow] == curCol) && curCol<totalCol)
			out.push("</tr>");
	out.push('</table>');
}