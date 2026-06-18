/* B104_ZK_6071_VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 28 11:49:09 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B104_ZK_6071_VM {
	private Date date = new Date();
	private boolean visible;
	private int no = 0;

	@Command
	@NotifyChange({"visible", "no"})
	public void doCmd() {
		this.visible = !this.visible;
		this.no += 1;
	}

	public Date getDate() {
		return date;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getNo() {
		return no;
	}
}
