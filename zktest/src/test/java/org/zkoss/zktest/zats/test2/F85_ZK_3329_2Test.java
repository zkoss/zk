/* F85_ZK_3329Test.java

	Purpose:
		
	Description:
		
	History:
		Wed May 16 16:11:42 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Center;
import org.zkoss.zul.LayoutRegion;

/**
 * @author rudyhuang
 */
public class F85_ZK_3329_2Test extends ZATSTestCase {
	private LayoutRegion center = new Center();

	@Test
	public void testSlidable() {
		Assertions.assertThrows(UnsupportedOperationException.class, () -> center.setSlidable(true));
	}

	@Test
	public void testSlide() {
		Assertions.assertThrows(UnsupportedOperationException.class, () -> center.setSlide(true));
	}

	@Test
	public void testClosable() {
		Assertions.assertThrows(UnsupportedOperationException.class, () -> center.setClosable(true));
	}
}
