/* IImageTest.java

	Purpose:

	Description:

	History:
		Wed Oct 27 14:15:41 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.junit.jupiter.api.Test;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zul.Image;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IImage}
 *
 * @author katherine
 */
public class IImageTest extends ZephyrTestBase {
	@Test
	public void withImage() {
		// check Richlet API case
		assertEquals(richlet(() -> IImage.of("abc")), zul(IImageTest::newImage));

		// check Zephyr file case
		assertEquals(composer(IImageTest::newImage), zul(IImageTest::newImage));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Image Image = new Image("123");
					return Image;
				}, (IImage iImage) -> iImage.withSrc("abc")),
				zul(IImageTest::newImage));
	}

	private static Image newImage() {
		Image image = new Image("abc");
		return image;
	}
}