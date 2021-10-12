/* B86_ZK_3588Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 23 15:25:08 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;

public class B86_ZK_3588Composer extends SelectorComposer {
	@Wire
	private Listhead listhead;
	@Wire
	private Listitem listitem;
	@Wire
	private Columns columns;
	@Wire
	private Row row;
	private int count = 4;

	@Listen("onClick = #add")
	public void add() {
		String content = String.valueOf(count++);
		listhead.appendChild(new Listheader(content));
		listitem.appendChild(new Listcell(content));
		columns.appendChild(new Column(content));
		row.appendChild(new Label(content));
	}
}
