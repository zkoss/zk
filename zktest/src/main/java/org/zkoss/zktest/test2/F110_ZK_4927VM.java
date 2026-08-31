/* F110_ZK_4927VM.java

	Purpose:

	Description:

	History:
		Mon Jun 22 14:25:23 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;

public class F110_ZK_4927VM {
	private String source = "# Bound\n\nEdited text with **bold**.";

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		// Echo the value back so the bound mdeditor reloads: without it the onChange two-way save is unobservable.
		BindUtils.postNotifyChange(null, null, this, "source");
	}
}
