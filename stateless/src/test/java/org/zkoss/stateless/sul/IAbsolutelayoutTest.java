/* IAbsolutelayoutTest.java

	Purpose:

	Description:

	History:
		Wed Oct 06 15:18:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Absolutechildren;
import org.zkoss.zul.Absolutelayout;

/**
 * Test for {@link IAbsolutechildren}
 *
 * @author katherine
 */
public class IAbsolutelayoutTest extends StatelessTestBase {
	@Test
	public void withAbsolutelayout() {
		// check Richlet API case
		assertEquals(richlet(() -> IAbsolutelayout.of(IAbsolutechildren.of(20, 20), IAbsolutechildren.of(10, 10))), zul(IAbsolutelayoutTest::newAbsolutelayout));

		// check Stateless file case
		assertEquals(composer(IAbsolutelayoutTest::newAbsolutelayout), zul(IAbsolutelayoutTest::newAbsolutelayout));

		// check Stateless file and then recreate another immutable case
		assertEquals(
			thenComposer(() -> new Absolutelayout(), (IAbsolutelayout iAbsolutelayout) -> iAbsolutelayout.withChildren(IAbsolutechildren.of(20, 20), IAbsolutechildren.of(10, 10))),
			zul(IAbsolutelayoutTest::newAbsolutelayout));
	}

	private static Absolutelayout newAbsolutelayout() {
		Absolutelayout absolutelayout = new Absolutelayout();
		Absolutechildren absolutechildren = new Absolutechildren();
		absolutechildren.setX(20);
		absolutechildren.setY(20);
		Absolutechildren absolutechildren2 = new Absolutechildren();
		absolutechildren2.setX(10);
		absolutechildren2.setY(10);
		absolutelayout.appendChild(absolutechildren);
		absolutelayout.appendChild(absolutechildren2);
		return absolutelayout;
	}
}