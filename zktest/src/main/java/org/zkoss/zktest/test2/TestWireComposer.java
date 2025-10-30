package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;

public class TestWireComposer extends GenericForwardComposer {


	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		if (self == null)
			throw new IllegalStateException("illegal");
	}
}
