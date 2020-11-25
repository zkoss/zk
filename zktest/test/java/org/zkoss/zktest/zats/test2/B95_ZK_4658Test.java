/* B95_ZK_4658Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 24 12:48:28 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zul.Decimalbox;

/**
 * @author rudyhuang
 */
public class B95_ZK_4658Test {
	@Test
	public void testEn() {
		testSetGetValue("locale:en");
	}

	@Test
	public void testEnUS() {
		testSetGetValue("locale:en-US");
	}

	private void testSetGetValue(String format) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setFormat(format);
		decimalbox.setConstraint("no negative");
		BigDecimal value = new BigDecimal("300000.00");
		decimalbox.setValue(value);
		Assert.assertEquals(value, decimalbox.getValue());
	}
}
