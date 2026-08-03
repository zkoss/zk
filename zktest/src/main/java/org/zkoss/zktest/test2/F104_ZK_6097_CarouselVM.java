/* F104_ZK_6097_CarouselVM.java

	Purpose:

	Description:

	History:
		Wed May 13 13:05:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6097_CarouselVM {

	private int activeIndex = 0;
	private String selResult = "none";
	private String changingResult = "none";

	@Init
	public void init() {
	}

	public int getActiveIndex() { return activeIndex; }
	public String getSelResult() { return selResult; }
	public String getChangingResult() { return changingResult; }

	@Command
	@NotifyChange({"activeIndex", "selResult"})
	public void onSlideSelected(@BindingParam("idx") int idx) {
		activeIndex = idx;
		selResult = "i=" + idx;
	}

	@Command
	@NotifyChange("changingResult")
	public void onSlideChanging(@BindingParam("from") int from, @BindingParam("to") int to) {
		changingResult = "from=" + from + " to=" + to;
	}

	@Command
	@NotifyChange("activeIndex")
	public void goToSecond() {
		activeIndex = 1;
	}

	@Command
	@NotifyChange("activeIndex")
	public void goToFirst() {
		activeIndex = 0;
	}
}
