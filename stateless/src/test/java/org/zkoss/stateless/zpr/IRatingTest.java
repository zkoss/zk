/* IRatingTest.java

	Purpose:

	Description:

	History:
		Thu Nov 04 09:58:34 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.Rating;

/**
 * Test for {@link IRating}
 *
 * @author katherine
 */
public class IRatingTest extends StatelessTestBase {
	@Test
	public void withRating() {
		// check Richlet API case
		assertEquals(richlet(() -> IRating.ofMax(3).withRating(1)), zul(IRatingTest::newRating));

		// check Stateless file case
		assertEquals(composer(IRatingTest::newRating), zul(IRatingTest::newRating));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Rating rating = new Rating();
					rating.setMax(3);
					rating.setRating(2);
					return rating;
				}, (IRating iRating) -> iRating.withRating(1).withMax(3)),
				zul(IRatingTest::newRating));
	}

	@Test
	public void withChild() {
		try {
			IRating.ofMax(2).withRating(3);
		} catch (UiException ex) {
			assertEquals("max should be larger than rating", ex.getMessage());
		}
	}

	private static Rating newRating() {
		Rating rating = new Rating();
		rating.setMax(3);
		rating.setRating(1);
		return rating;
	}
}