/* IAuxheadTest.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:54:49 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IAuxhead}
 *
 * @author katherine
 */
public class IAuxheadTest extends StatelessTestBase {
	@Test
	public void withAuxhead() {
		// check Richlet API case
		assertEquals(richlet(() -> IAuxhead.of(getIAuxheaderChildren())), zul(IAuxheadTest::newAuxhead));

		// check Stateless file case
		assertEquals(composer(IAuxheadTest::newAuxhead), zul(IAuxheadTest::newAuxhead));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Auxhead auxhead = new Auxhead();
					auxhead.appendChild(new Auxheader("abc"));
					return auxhead;
				}, (IAuxhead iAuxhead) -> iAuxhead),
				zul(IAuxheadTest::newAuxhead));
	}

	private static Auxhead newAuxhead() {
		Auxhead auxhead = new Auxhead();
		auxhead.appendChild(new Auxheader("abc"));
		return auxhead;
	}

	private static List getIAuxheaderChildren() {
		List children = new ArrayList();
		children.add(IAuxheader.of("abc"));
		return children;
	}
}