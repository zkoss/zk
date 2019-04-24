/* F50_3291332Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 14:47:51 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zul.Button;

/**
 * @author rudyhuang
 */
public class F50_3291332Composer extends SelectorComposer<Component> implements DesktopCleanup {
	private static Logger log = LoggerFactory.getLogger(F50_3291332Composer.class);

	@WireVariable
	private Desktop desktop;

	@WireVariable
	private Session session;

	@Wire
	private Button test;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		desktop.addListener(this);
		Object hasRmDesktop = session.getAttribute("F50_3291332");
		if (hasRmDesktop != null)
			test.setLabel("rmDesktop received at " + new Date((long) hasRmDesktop));
	}

	@Override
	public void cleanup(Desktop dsk) throws Exception {
		session.setAttribute("F50_3291332", System.currentTimeMillis());
		log.info("rmDesktop");
	}
}
