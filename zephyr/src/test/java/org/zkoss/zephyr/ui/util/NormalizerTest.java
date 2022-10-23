/* NormalizerTest.java

	Purpose:
		
	Description:
		
	History:
		6:10 PM 2022/1/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author jumperchen
 */
public class NormalizerTest {
	@Test
	public void testSelector() {
		assertEquals("$abc", Normalizer.normalize("#abc"));
		assertEquals("$abc @button", Normalizer.normalize("#abc button"));
		assertEquals("@abc + @button", Normalizer.normalize("abc + button"));
		assertEquals(".abc + @button", Normalizer.normalize(".abc + button"));
		assertEquals("@abc > .button $abc @button:contain(abc)", Normalizer.normalize("abc > .button #abc button:contain(abc)"));
	}
}
