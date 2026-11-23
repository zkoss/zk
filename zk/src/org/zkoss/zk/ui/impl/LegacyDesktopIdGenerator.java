/* LegacyDesktopIdGenerator.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 10 10:55:47 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.impl;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * A legacy sequential desktop ID generator used in ZK before 9.6.0.
 * The result is like <code>z_1ym6</code> or <code>_ga6b</code>.
 *
 * It accepts {@link Configuration#isRepeatUuid()} for initial key value.
 *
 * <p>To use this Id Generator, add system-config in zk.xml.
 * <pre>
 * &lt;system-config&gt;
 *     &lt;id-generator-class&gt;org.zkoss.zk.ui.impl.LegacyDesktopIdGenerator&lt;/id-generator-class&gt;
 * &lt;/system-config&gt;
 * </pre>
 *
 * @author rudyhuang
 * @since 9.6.0
 */
public class LegacyDesktopIdGenerator implements IdGenerator, Serializable {
	private static final long serialVersionUID = 20210311125543L;
	private static final String DESKTOP_ID_PREFIX = "z_";
	private static final AtomicInteger _keyWithoutDC = new AtomicInteger();

	@Override
	public String nextDesktopId(Desktop desktop) {
		final Session session = desktop.getSession();
		final DesktopCache dc = session != null ? ((SessionCtrl) session).getDesktopCache() : null;
		if (dc != null)
			return ComponentsCtrl.encodeId(
					new StringBuffer(12).append(DESKTOP_ID_PREFIX),
					dc.getNextKey());

		return ComponentsCtrl.encodeId(
				new StringBuffer(12).append("_g"),
				_keyWithoutDC.getAndIncrement());
	}
}
