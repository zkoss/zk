/* B30_2208873Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 14 09:46:24 CST 2026, Created by rebeccalai

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B30_2208873Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals(4, jq(".z-listcell").length());
		String[] expected = {"one", "two", "three", "four"};
		IntStream.range(0, 4).forEach(i -> assertEquals(expected[i], jq(".z-listcell").eq(i).text()));
		assertNoAnyError();
	}
}
