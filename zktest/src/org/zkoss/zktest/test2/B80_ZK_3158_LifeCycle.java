/** B80_ZK_3143.java.

 Purpose:

 Description:

 History:
 		Wed Mar 16 17:12:46 CST 2016, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.lang.Exceptions;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.UiLifeCycle;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3158_LifeCycle implements UiLifeCycle{

	@Override
	public void afterShadowAttached(ShadowElement shadow, Component host) {
		if (Executions.getCurrent().getSession().getAttribute("ZK3158") != null)
			Clients.log("B80_ZK_3158_LifeCycle: afterShadowAttached");
	}

	@Override
	public void afterShadowDetached(ShadowElement shadow, Component prevhost) {
		if (Executions.getCurrent().getSession().getAttribute("ZK3158") != null)
			Clients.log("B80_ZK_3158_LifeCycle: afterShadowDetached");
	}

	@Override
	public void afterComponentAttached(Component comp, Page page) {

	}

	@Override
	public void afterComponentDetached(Component comp, Page prevpage) {

	}

	@Override
	public void afterComponentMoved(Component parent, Component child, Component prevparent) {

	}

	@Override
	public void afterPageAttached(Page page, Desktop desktop) {

	}

	@Override
	public void afterPageDetached(Page page, Desktop prevdesktop) {

	}
}
