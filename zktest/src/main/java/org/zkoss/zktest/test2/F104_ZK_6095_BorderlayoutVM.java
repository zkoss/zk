/* F104_ZK_6095_BorderlayoutVM.java

		Purpose:

		Description:

		History:
				Tue May 26 17:00:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F104_ZK_6095_BorderlayoutVM {

	private String shortcutKeys = "^k";
	private String result = "none";

	public String getShortcutKeys() { return shortcutKeys; }
	public String getResult() { return result; }

	@Command
	@NotifyChange("result")
	public void onShortcut(@BindingParam("code") int code) {
		result = "ctrlKey:" + code;
	}
}
