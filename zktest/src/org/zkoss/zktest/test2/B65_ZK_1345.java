package org.zkoss.zktest.test2;

import java.io.IOException;

import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.metainfo.MessageLoader;

public class B65_ZK_1345 implements MessageLoader {
	@Override
	public void load(StringBuffer out, Execution exec) throws IOException {
		out.append("B65_ZK_1345 = \"load by B65_ZK_1345.java and " +
				"locale is " + Locales.getCurrent() +"\"");
	}
}