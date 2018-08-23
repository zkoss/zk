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
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;

public class B86_ZK_3588Composer extends SelectorComposer {
	@Wire
	private Listhead listhead;
	@Wire
	private Columns columns;

	@Listen("onClick = #button")
	public void addHeader() {
		listhead.appendChild(new Listheader("xxx"));
		columns.appendChild(new Column("xxx"));
	}
}
