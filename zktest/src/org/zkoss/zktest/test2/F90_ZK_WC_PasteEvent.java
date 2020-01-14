/* F90_ZK_WC_PasteEvent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jan 30 10:53:37 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.annotation.ClientEventParam;
import org.zkoss.zk.ui.event.Event;

/**
 * @author rudyhuang
 */
public class F90_ZK_WC_PasteEvent extends Event {
	private static final long serialVersionUID = 6337667047089957191L;
	private String _clipboardText;

	public F90_ZK_WC_PasteEvent(String name, Component target,
	                            @ClientEventParam("clipboardData.getData('text')") String clipboardText) {
		super(name, target);
		this._clipboardText = clipboardText;
	}

	public String getClipboardText() {
		return _clipboardText;
	}
}
