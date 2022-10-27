/* IDivTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:55:16 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * Test for {@link IDiv}
 *
 * @author katherine
 */
public class IDivTest extends StatelessTestBase {
	@Test
	public void withDiv() {
		// check Richlet API case
		assertEquals(richlet(() -> IDiv.of(IDiv.of(IDiv.of(ILabel.of("1"))))), zul(IDivTest::newDiv));

		// check Stateless file case
		assertEquals(composer(IDivTest::newDiv), zul(IDivTest::newDiv));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Div div = new Div();
					div.appendChild(new Label("2"));
					return div;
				}, (IDiv<IAnyGroup> iDiv) -> iDiv.withChildren(IDiv.of(IDiv.of(ILabel.of("1"))))),
				zul(IDivTest::newDiv));
	}

	private static Div newDiv() {
		Div div = new Div();
		Div div2 = new Div();
		Div div3 = new Div();
		div.appendChild(new Label("1"));
		div2.appendChild(div);
		div3.appendChild(div2);
		return div3;
	}
}