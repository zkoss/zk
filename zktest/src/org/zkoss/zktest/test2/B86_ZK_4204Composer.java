/* B86_ZK_4204Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar 06 18:26:51 CST 2019, Created by rudyhuang

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
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class B86_ZK_4204Composer extends SelectorComposer<Component> implements DesktopCleanup {
	private static Logger log = LoggerFactory.getLogger(B86_ZK_4204Composer.class);

	@WireVariable
	private Desktop desktop;

	@WireVariable
	private Session session;

	@Wire
	private Label rmDesktopIndicator;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		desktop.addListener(this);
		Object hasRmDesktop = session.getAttribute("B86_ZK_4204");
		if (hasRmDesktop != null)
			rmDesktopIndicator.setValue("rmDesktop received at " + new Date((long) hasRmDesktop));
	}

	@Override
	public void cleanup(Desktop dsk) throws Exception {
		session.setAttribute("B86_ZK_4204", System.currentTimeMillis());
		log.info("rmDesktop");
	}
}
