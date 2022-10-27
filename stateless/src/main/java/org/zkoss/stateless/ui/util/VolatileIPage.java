/* VolatileIPage.java

	Purpose:
		
	Description:
		
	History:
		3:08 PM 2022/8/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.ui.IStubComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.impl.VolatilePage;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A page is alive temporarily during UiEngine to create IComponents
 * from a ZUML page that eventually shall not be attached to the desktop.
 * @author jumperchen
 */
public class VolatileIPage extends VolatilePage {
	private static String ACTIONS = VolatileIPage.class.getName() + ".ACTIONS";
	private Map<IComponent, StatelessEventListenerMap> zevts;
	public VolatileIPage(PageDefinition pgdef) {
		super(pgdef);
	}

	public VolatileIPage(Page ref) {
		super(ref);
	}

	public void addAction(IComponent owner, String name, ActionHandler handler) {
		// lazy init
		if (zevts == null) {
			 zevts = new HashMap<>(4);
		}
		zevts.computeIfAbsent(owner, (__) -> new StatelessEventListenerMap(
				owner.getEventListenerMap())).addAction(name, handler);
	}

	public void mergeActionsTo(Page page) {
		if (zevts == null || zevts.isEmpty()) return;

		if (page instanceof VolatileIPage) {
			if (((VolatileIPage) page).zevts == null) {
				((VolatileIPage) page).zevts = zevts;
			} else {
				((VolatileIPage) page).zevts.putAll(zevts);
			}
		} else {
			if (page.hasAttribute(ACTIONS)) {
				Map map = (Map) page.getAttribute(ACTIONS);
				if (map != null) {
					map.putAll(zevts);
				} else {
					page.setAttribute(ACTIONS, zevts);
				}
			} else {
				page.setAttribute(ACTIONS, zevts);
			}
		}
	}

	public <T extends IComponent> List<T> getAllIComponents() {
		return getRoots().stream().map(component -> {
			component.setPage(null); // detach the component, which is not belonged to any desktop
			return (T) (component instanceof IStubComponent ?
					((IStubComponent) component).getOwner() :
					Immutables.proxyIComponent(component));
		}).collect(Collectors.toList());
	}

	public static StatelessEventListenerMap removeVolatileEventListenerMap(Page page, IComponent icmp) {
		if (page.hasAttribute(ACTIONS)) {
			Map<IComponent, StatelessEventListenerMap> zevts = (Map) page.getAttribute(ACTIONS);
			try {
				if (zevts != null) {
					return zevts.remove(icmp);
				}
			} finally {
				if (zevts == null || zevts.isEmpty()) {
					page.removeAttribute(ACTIONS);
				}
			}
		}
		return null;
	}
}
