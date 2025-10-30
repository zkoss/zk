package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Button;

public class B2241309Composer extends GenericAutowireComposer {
	public void onClick() {
		((Button)self).setLabel("NewLabel");
	}
}
