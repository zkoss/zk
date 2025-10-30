package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.proxy.ProxyTargetHandler;

public class B80_ZK_3149ProxyHandler implements ProxyTargetHandler{
	@Override
	public Object getOriginObject(Object origin) {
		if (origin instanceof B80_ZK_3149Object)
			((B80_ZK_3149Object)origin).setInfo("handled");
		return origin;
	}
}
