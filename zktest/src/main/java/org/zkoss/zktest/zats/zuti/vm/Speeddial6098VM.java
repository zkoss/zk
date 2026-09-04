/* Speeddial6098VM.java

	Purpose:

	Description:

	History:
		Mon Jun 15 11:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.vm;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/** ViewModel for the ZK-6098 Speeddial MVVM fixture: two-way {@code open}
 * binding + an {@code onOpen} command counter. */
public class Speeddial6098VM {
	private boolean open = true;
	private int toggledCount = 0;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getToggledCount() {
		return toggledCount;
	}

	@Command
	@NotifyChange("toggledCount")
	public void toggled() {
		toggledCount++;
	}
}
