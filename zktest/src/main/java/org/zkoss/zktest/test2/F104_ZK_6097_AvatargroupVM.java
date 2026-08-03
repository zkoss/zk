/* F104_ZK_6097_AvatargroupVM.java

		Purpose:

		Description:

		History:
				Wed May 13 13:17:28 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6097_AvatargroupVM {

	private int maxCount = 3;
	private String size;
	private String shape;
	private String result = "initial: maxCount=3";

	@Init
	public void init() {}

	public int getMaxCount() { return maxCount; }
	public String getSize()   { return size; }
	public String getShape()  { return shape; }
	public String getResult() { return result; }

	@Command
	@NotifyChange({"maxCount", "result"})
	public void removeLimit() {
		maxCount = 0;
		result = "maxCount removed — all avatars visible";
	}

	@Command
	@NotifyChange({"maxCount", "result"})
	public void setLimit2() {
		maxCount = 2;
		result = "maxCount=2 — 3 avatars hidden (+3 overflow)";
	}

	@Command
	@NotifyChange({"size", "shape", "result"})
	public void setLargeSquare() {
		size = "large";
		shape = "square";
		result = "size=large shape=square applied";
	}

	@Command
	@NotifyChange({"maxCount", "size", "shape", "result"})
	public void reset() {
		maxCount = 3;
		size = null;
		shape = null;
		result = "reset: maxCount=3";
	}
}
