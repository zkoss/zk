/* F104_ZK_6097_BreadcrumbVM.java

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

public class F104_ZK_6097_BreadcrumbVM {

	private String separator = "/";
	private int maxItems = 0;
	private String currentPath = "Home";

	@Init
	public void init() {
	}

	public String getSeparator() { return separator; }
	public int getMaxItems() { return maxItems; }
	public String getCurrentPath() { return currentPath; }

	@Command
	@NotifyChange("separator")
	public void usePipeSeparator() {
		separator = "|";
	}

	@Command
	@NotifyChange("separator")
	public void useDefaultSeparator() {
		separator = "/";
	}

	@Command
	@NotifyChange("maxItems")
	public void enableCollapse() {
		maxItems = 3;
	}

	@Command
	@NotifyChange("maxItems")
	public void disableCollapse() {
		maxItems = 0;
	}
}
