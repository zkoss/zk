package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F100_ZK_BindComposer extends org.zkoss.bind.BindComposer {
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Clients.log("custom bind composer");
	}
}
