/* F90_ZK_4386Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct 2 10:09:27 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zkmax.zul.Anchornav;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 */
public class F90_ZK_4386Composer extends SelectorComposer<Component> {
	List<Component> newWindows = new ArrayList();

	public void addWindow(boolean before, Component ref) {
		Component parent = ref.getParent();
		Window win = new Window();
		win.setClientDataAttribute("anchornav", "a1");
		win.setClientDataAttribute("anchornavtitle", "Brand New");
		win.setHeight("200px");
		win.setWidth("200px");
		win.setTitle("Brand New");
		win.setBorder("normal");
		if (before) {
			parent.insertBefore(win, ref);
		} else {
			parent.appendChild(win);
		}
		newWindows.add(win);
	}

	public void detachNewWindow() {
		newWindows.get(newWindows.size() - 1).detach();
	}

	public void addAnchor(Component p) {
		Anchornav a = new Anchornav();
		a.setName("a1");
		a.setParent(p);
	}
}
