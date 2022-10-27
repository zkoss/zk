/* ISliderTest.java

	Purpose:

	Description:

	History:
		Wed Dec 01 17:54:21 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Slider;

/**
 * Test for {@link ISlider}
 *
 * @author katherine
 */
public class ISliderTest extends StatelessTestBase {
	@Test
	public void withSlider() {
		// check Richlet API case
		assertEquals(richlet(() -> ISlider.DEFAULT.withMaxpos(10).withCurposInDouble(1.5)), zul(ISliderTest::newSlider));

		// check Stateless file case
		assertEquals(composer(ISliderTest::newSlider), zul(ISliderTest::newSlider));
		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Slider slider = new Slider();
					slider.setMaxpos(10);
					slider.setCurpos(1);
					return slider;
				}, (ISlider iSlider) -> iSlider.withMaxpos(10).withCurposInDouble(1.5)),
				zul(ISliderTest::newSlider));
	}

	@Test
	public void withCrops() {
		assertEquals(richlet(() -> ISlider.DEFAULT.withMaxpos(10).withCurpos(20)), zul(ISliderTest::newSlider2));
	}

	private static Slider newSlider() {
		Slider slider = new Slider();
		slider.setMaxpos(10);
		slider.setCurpos(1.5);
		return slider;
	}

	private static Slider newSlider2() {
		Slider slider = new Slider();
		slider.setMaxpos(10);
		slider.setCurpos(20);
		return slider;
	}
}