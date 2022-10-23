/* ICheckboxTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:22:28 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Checkbox;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ICheckbox}
 *
 * @author katherine
 */
public class ICheckboxTest extends ZephyrTestBase {
	@Test
	public void withCheckbox() {
		// check Richlet API case
		assertEquals(richlet(() -> ICheckbox.of("abc")), zul(ICheckboxTest::newCheckbox));

		// check Zephyr file case
		assertEquals(composer(ICheckboxTest::newCheckbox), zul(ICheckboxTest::newCheckbox));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Checkbox("123"), (ICheckbox iCheckbox) -> iCheckbox.withLabel("abc")),
				zul(ICheckboxTest::newCheckbox));
	}

	@Test
	public void withChild() {
		try {
			ICheckbox.of("abc").withIndeterminate(true).withMold("switch");
		} catch (UiException ex) {
			assertEquals("Checkbox switch/toggle mold does not support indeterminate yet.", ex.getMessage());
		}
	}

	private static Checkbox newCheckbox() {
		return new Checkbox("abc");
	}
}