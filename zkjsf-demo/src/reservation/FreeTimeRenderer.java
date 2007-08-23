/* FreeTimeRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/8/22 2:08:47 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package reservation;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * @author Dennis.Chen
 *
 */
public class FreeTimeRenderer implements ListitemRenderer{
	public void render(Listitem item, java.lang.Object data){
		Object datas[] = (Object[])data;
		new Listcell((String)datas[0]).setParent(item);
		new Listcell((String)datas[1]).setParent(item);
		item.setValue(datas[0]);
	}

}
