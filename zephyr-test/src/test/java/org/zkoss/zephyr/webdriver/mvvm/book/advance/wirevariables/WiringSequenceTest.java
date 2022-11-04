/* WiringSequenceTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 14:40:44 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.wirevariables;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class WiringSequenceTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		assertEquals("The variable resolver defined in the ZUML document.", getZKLog());
	}

	@Test
	public void test2() {
		connect("/mvvm/book/advance/wirevariables/WiringSequence-2.zul");
		waitResponse();
		assertEquals("The variable resolver annotated registered with the VariableResolver annotation.", getZKLog());
	}
}
