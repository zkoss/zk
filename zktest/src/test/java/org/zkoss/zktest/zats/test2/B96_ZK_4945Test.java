/* B96_ZK_4945Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jul 09 10:52:22 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zkmax.bind.impl.AnnotateBinderEx;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B96_ZK_4945Test extends ZATSTestCase {
	private DesktopAgent desktopAgent;
	private ComponentAgent root() {
		return desktopAgent.query("#root");
	}
	private ComponentAgent typeCombobox() {
		return desktopAgent.query("combobox");
	}
	private ComponentAgent nameLabel(String type) {
		return desktopAgent.query("#name" + type);
	}
	
	@Test
	public void testMemoryLeak() {
		desktopAgent = connect();
		List<Integer> cacheSizeHistory = new ArrayList<>();
		cacheSizeHistory.add(getFormIdCache().size());

		Assertions.assertArrayEquals(new Component[]{}, findDetachedComponentsInCache(), "There should be no detached components in the cache");
		Assertions.assertFalse(getFormIdCache().values().contains(null),
				"There should be no NULL values in the cache");

		// update the UI dynamically
		for (int i = 0; i < 5; i++) {
			switchType("A", "B");
			cacheSizeHistory.add(getFormIdCache().size());
			switchType("B", "A");
			cacheSizeHistory.add(getFormIdCache().size());
		}

		System.out.println(cacheSizeHistory);
		Assertions.assertArrayEquals(new Component[]{}, findDetachedComponentsInCache(), "There should be no detached components in the cache");
		Assertions.assertFalse(getFormIdCache().values().contains(null),
				"There should be no NULL values in the cache");
	}

	private void switchType(String fromType, String toType) {
		Assertions.assertNotNull(nameLabel(fromType));
		Assertions.assertNull(nameLabel(toType));
		typeCombobox().input(toType);
		Assertions.assertNull(nameLabel(fromType));
		Assertions.assertNotNull(nameLabel(toType));
		System.out.println(getFormIdCache());
	}

	private Component[] findDetachedComponentsInCache() {
		return getFormIdCache().keySet().stream()
				.filter(component -> {
					if(component instanceof HtmlShadowElement) {
						HtmlShadowElement shadowElement = (HtmlShadowElement) component;
						return shadowElement.getShadowHostIfAny() != null ? shadowElement.getShadowHost().getDesktop() == null : true;
					}
					return component.getDesktop() == null;
				}).toArray(Component[]::new);
	}

	private Map<Component, String> getFormIdCache() {
		AnnotateBinderEx binder = (AnnotateBinderEx) root().as(Component.class).getAttribute(BinderImpl.BINDER);
		return binder.getLookupComponentFormIdCache();
	}
}