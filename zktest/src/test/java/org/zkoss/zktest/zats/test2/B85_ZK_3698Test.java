/* B85_ZK_3698Test.java

	Purpose:
		
	Description:
		
	History:
		Tue May 22 15:23:40 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Decimalbox;


/**
 * @author rudyhuang
 */
public class B85_ZK_3698Test extends ZATSTestCase {
	@Test
	public void testDecimalBoxPropertyAccessString() {
		Decimalbox d = new Decimalbox(new BigDecimal("2.0"));
		String dd = "1.0";
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(new BigDecimal(dd), d.getValue());
		assertEquals(new BigDecimal(dd), d.getPropertyAccess("value").getValue(d));
	}

	@Test
	public void testDecimalBoxPropertyAccessBigDecimal() {
		Decimalbox d = new Decimalbox(new BigDecimal("2.0"));
		BigDecimal dd = new BigDecimal(100);
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(dd, d.getValue());
		assertEquals(dd, d.getPropertyAccess("value").getValue(d));
	}

	@Test
	public void testDecimalBoxPropertyAccessOthers() {
		BigDecimal origVal = new BigDecimal("2.0");
		Decimalbox d = new Decimalbox(origVal);
		Date dd = new Date();
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(origVal, d.getValue());
		assertEquals(origVal, d.getPropertyAccess("value").getValue(d));
	}

	@Test
	public void testDecimalBoxPropertyAccessNull() {
		Decimalbox d = new Decimalbox(new BigDecimal("2.0"));
		String dd = null;
		// test value
		d.getPropertyAccess("value").setValue(d, dd);
		assertEquals(dd, d.getValue());
		assertEquals(dd, d.getPropertyAccess("value").getValue(d));
	}

	@Test
	public void testDecimalBoxSetNull() {
		Decimalbox d = new Decimalbox(new BigDecimal("2.0"));
		try {
			d.setValue((String) null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> buttons = desktop.queryAll("button");

		try {
			buttons.get(0).click();
			buttons.get(1).click();
			buttons.get(2).click();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
