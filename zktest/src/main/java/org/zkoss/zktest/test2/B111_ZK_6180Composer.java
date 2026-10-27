/* B111_ZK_6180Composer.java

	Purpose:

	Description:

	History:
		Sat Oct 24 19:09:19 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

public class B111_ZK_6180Composer extends SelectorComposer<Component> {

	@Wire
	private Rows rows;
	@Wire
	private Rows prows;
	@Wire
	private Columns cols;
	@Wire
	private Foot foot;
	@Wire
	private Frozen frozen;
	@Wire
	private Grid pgrid;

	private int push;

	@Listen("onClick=#push")
	public void doPush() {
		push++;
		refill(rows, 2, true);
		refill(prows, 3, false);
	}

	@Listen("onClick=#others")
	public void doOthers() {
		cols.invalidate();
		foot.invalidate();
		frozen.invalidate();
		pgrid.getPagingChild().invalidate();
	}

	// replaces every row, then invalidates the rows so the client receives one outer of the rows
	private void refill(Rows target, int count, boolean twoCells) {
		target.getChildren().clear();
		for (int i = 1; i <= count; i++) {
			Row row = new Row();
			row.appendChild(new Label("push " + push + ", row " + i));
			if (twoCells)
				row.appendChild(new Label("x"));
			target.appendChild(row);
		}
		target.invalidate();
	}
}
