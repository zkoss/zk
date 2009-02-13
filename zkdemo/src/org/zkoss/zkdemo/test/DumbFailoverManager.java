/* DumbFailoverManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 19 14:36:14     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test;

import java.util.Iterator;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.ui.sys.FailoverManager;
import org.zkoss.zk.ui.impl.PageImpl;

/**
 * A dumb failover manager useless but to test the failover mechanism.
 *
 * <p>To test, call {@link #dropDesktop} first to remove a desktop.
 * And, then clicks or enters something on the client to see
 * whether it behaves as usual.
 *
 * <p>It is usually tested with test/failover.zul.
 *
 * @author tomyeh
 */
public class DumbFailoverManager implements FailoverManager {
	private Desktop _killed;

	/** Drops the current desktop.
	 *
	 * @param recoverable whether the dropped desktop is recovable.
	 */
	public void dropDesktop(boolean recoverable) {
		final Desktop desktop = Executions.getCurrent().getDesktop();
		((WebAppCtrl)desktop.getWebApp())
			.getDesktopCache(desktop.getSession())
			.removeDesktop(desktop);
		if (recoverable)
			_killed = desktop;
	}

	//FailoverManager//
	public boolean isRecoverable(Session sess, String desktopId) {
		return _killed != null && desktopId.equals(_killed.getId());
	}
	public void recover(Session sess, Execution exec, Desktop desktop) {
		System.out.println("Recover "+_killed.getId());

		//recover desktop
		final DesktopCtrl dtc = (DesktopCtrl)desktop;
		dtc.setId(_killed.getId()); //required
		desktop.setCurrentDirectory(_killed.getCurrentDirectory()); //optional
		desktop.setDeviceType(_killed.getDeviceType()); //optional

		//recover pages
		for (Iterator it = _killed.getPages().iterator(); it.hasNext();)
			recover((Page)it.next());
		_killed = null;
	}
	private static void recover(final Page killed) {
		//recover page
		final Page page = new PageImpl(
			killed.getLanguageDefinition(), //required; never null
			killed.getComponentDefinitionMap(), //If unknown, just pass null
			killed.getRequestPath(), //If unknown, just pass null
			killed.getZScriptLanguage()); //If unkown, just pass null (Java assumed)

		final PageCtrl pageCtrl = (PageCtrl)page;
		final PageCtrl killedCtrl = (PageCtrl)killed;
		pageCtrl.init(
			new PageConfig() {
				public String getId() {return killed.getId();} //required; never null
				public String getUuid() {return killed.getUuid();} //required; never null
				public String getTitle() {return killed.getTitle();} //if unknown, just pass null
				public String getStyle() {return killed.getStyle();} //if unknown, just pass null
				public String getHeaders() {return killedCtrl.getHeaders();} //if unknown, just pass null
			});

		String s = killedCtrl.getRootAttributes();
		if (s != null) pageCtrl.setRootAttributes(s);

		s = killedCtrl.getContentType();
		if (s != null) pageCtrl.setContentType(s);

		s = killedCtrl.getDocType();
		if (s != null) pageCtrl.setDocType(s);

		s = killedCtrl.getFirstLine();
		if (s != null) pageCtrl.setFirstLine(s);

		if (killedCtrl.getCacheable() != null)
			pageCtrl.setCacheable(killedCtrl.getCacheable());

		if (killed.getExpressionFactoryClass() != null)
			page.setExpressionFactoryClass(killed.getExpressionFactoryClass());

		page.setComplete(killed.isComplete());
			//optional: copy killed's attrs to page

		for (Iterator it = killed.getRoots().iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			((Component)comp.clone()).setPage(page);
		}
	}
}
