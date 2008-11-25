/* LiveGroupRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 18, 2008 11:37:53 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.userguide;

import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * @author jumperchen
 *
 */
public class LiveGroupRenderer implements RowRenderer {

	public void render(Row row, java.lang.Object data) {
		if(data instanceof String[]) {
			String[] ary = (String[]) data;
      Div div = new Div();
      Image icon = new Image();
      icon.setStyle("padding: 0px 10px");
      icon.setSrc("/img/EnvelopeOpen-16x16.png");
      div.appendChild(icon);
      new Label(ary[0]).setParent(div);
      row.appendChild(div);
      new Label(ary[1]).setParent(row);
      new Label(ary[2]).setParent(row);
      new Label(ary[3]).setParent(row);
		} else {
			new Label(data.toString()).setParent(row);
		}
        
	}
}
