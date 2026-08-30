/* F110_ZK_6097_CarouselBindSaveVM.java

	Purpose:

	Description:

	History:
		Sat Aug 30 10:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.NotifyChange;

public class F110_ZK_6097_CarouselBindSaveVM {

	private int activeIndex = 0;

	public int getActiveIndex() { return activeIndex; }

	@NotifyChange("savedIndex")
	public void setActiveIndex(int activeIndex) { this.activeIndex = activeIndex; }

	public String getSavedIndex() { return "saved=" + activeIndex; }
}
