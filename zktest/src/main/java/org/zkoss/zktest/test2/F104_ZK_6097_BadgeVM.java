/* F104_ZK_6097_BadgeVM.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6097_BadgeVM {

	private int count = 0;
	private String severity = "info";
	private boolean dot = false;

	@Init
	public void init() {
	}

	public int getCount() { return count; }
	public String getSeverity() { return severity; }
	public boolean isDot() { return dot; }

	@Command
	@NotifyChange("count")
	public void increment() {
		count++;
	}

	@Command
	@NotifyChange({"count", "severity"})
	public void reset() {
		count = 0;
		severity = "info";
	}

	@Command
	@NotifyChange("severity")
	public void setDangerSeverity() {
		severity = "danger";
	}

	@Command
	@NotifyChange("dot")
	public void toggleDot() {
		dot = !dot;
	}
}
