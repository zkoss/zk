/* IAuxheaderTest.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:54:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Auxheader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IAuxheader}
 *
 * @author katherine
 */
public class IAuxheaderTest extends StatelessTestBase {
	@Test
	public void withAuxheader() {
		// check Richlet API case
		assertEquals(richlet(() -> IAuxheader.of("abc")), zul(IAuxheaderTest::newAuxheader));

		// check Stateless file case
		assertEquals(composer(IAuxheaderTest::newAuxheader), zul(IAuxheaderTest::newAuxheader));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Auxheader("123"), (IAuxheader iAuxheader) -> iAuxheader.withLabel("abc")),
				zul(IAuxheaderTest::newAuxheader));
	}

	@Test
	public void withColspan() {
		try {
			IAuxheader.of("abc").withColspan(-1);
		} catch (UiException ex) {
			assertEquals("Positive only.", ex.getMessage());
		}
	}

	@Test
	public void withHeight() {
		try {
			IAuxheader.of("abc").withHeight("10px");
		} catch (UiException ex) {
			assertEquals("Not allowed to set height in auxhead.", ex.getMessage());
		}
	}

	private static Auxheader newAuxheader() {
		return new Auxheader("abc");
	}
}