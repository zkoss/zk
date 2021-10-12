/* F86_ZK_3540VM.java

	Purpose:

	Description:

	History:
		Wed Jan 23 11:22:11 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.sql.Time;
import java.util.Date;

import javassist.util.proxy.ProxyObject;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.zk.ui.util.Clients;

public class F86_ZK_3540VM {
	@Init
	public void init() {
		ProxyHelper.addIgnoredSuperProxyClass(F86_ZK_3540Foo.class);
		Bar proxyIfAny = ProxyHelper.createProxyIfAny(new Bar());
		if (proxyIfAny instanceof ProxyObject)
			Clients.log("error");
	}

	public static class Bar extends F86_ZK_3540Foo {
	}
}
