/* B96_ZK_5382Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 03 17:13:22 CST 2023, Created by jameschu

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jameschu
 */
public class B96_ZK_5382Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();

		AbstractComponent host = desktop.query("#host").as(AbstractComponent.class);
		List<ShadowElement> shadowRoots = host.getShadowRoots();
		assertEquals(3, shadowRoots.size());
		HtmlShadowElement apply1 = (HtmlShadowElement) shadowRoots.get(0);
		HtmlShadowElement apply2 = (HtmlShadowElement) shadowRoots.get(1);
		assertEquals(apply2, apply1.getNextInsertion());
		assertEquals(apply1, apply2.getPreviousInsertion());
		HtmlShadowElement apply3 = (HtmlShadowElement) shadowRoots.get(2);
		assertEquals(apply3, apply2.getNextInsertion());
		assertEquals(apply2, apply3.getPreviousInsertion());
	}
}