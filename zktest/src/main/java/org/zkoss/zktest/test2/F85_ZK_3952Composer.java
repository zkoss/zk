/* F85_ZK_3952VM.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 13 18:05:19 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;

public class F85_ZK_3952Composer extends SelectorComposer {
	
	@Wire
	private Grid grid;
	@Wire
	private Listbox listbox;
	@Wire
	private Intbox intbox;
	
	@Listen("onBlur = #intbox")
	public void scroll() {
		Integer value = intbox.getValue();
		if (value != null) {
			grid.scrollToIndex(value);
			listbox.scrollToIndex(value);
		}
	}
}
