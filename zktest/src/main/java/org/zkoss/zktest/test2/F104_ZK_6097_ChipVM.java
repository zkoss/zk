/* F104_ZK_6097_ChipVM.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6097_ChipVM {

	private String chipLabel = "MVVM Chip";
	private String severity = "info";
	private String result = "open";

	@Init
	public void init() {
	}

	public String getChipLabel() { return chipLabel; }
	public String getSeverity() { return severity; }
	public String getResult() { return result; }

	@Command
	@NotifyChange("result")
	public void onChipClose() {
		result = "closed";
	}

	@Command
	@NotifyChange("severity")
	public void changeSeverity(@BindingParam("sev") String sev) {
		severity = sev;
	}

	@Command
	@NotifyChange("chipLabel")
	public void changeLabel() {
		chipLabel = "Updated Label";
	}
}
