/* F60_ZK_1047Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Apr 15 16:32:38 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F60_ZK_1047Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assert.assertEquals("12,345", desktop.query("#i1").as(Label.class).getValue());
		Assert.assertEquals("12,345,678,901,234.35", desktop.query("#i2").as(Label.class).getValue());
		Assert.assertEquals("12,345.23", desktop.query("#i3").as(Label.class).getValue());
		Assert.assertEquals("12,345.235", desktop.query("#i4").as(Label.class).getValue());
		Assert.assertEquals("12,345,234.5678 ‰", desktop.query("#i5").as(Label.class).getValue());
		Assert.assertEquals("$ 12,345.234568", desktop.query("#i6").as(Label.class).getValue());
		Assert.assertEquals("￥ 12,345.234568", desktop.query("#i7").as(Label.class).getValue());
		Assert.assertEquals(12345, (int) desktop.query("#i8").as(Intbox.class).getValue());
		Assert.assertEquals(12345, (int) desktop.query("#i9").as(Intbox.class).getValue());
		Assert.assertEquals(12345.876543, desktop.query("#i10").as(Doublebox.class).getValue(), 0.000001);

		Assert.assertEquals("2012-12-21", desktop.query("#i11").as(Label.class).getValue());
		Assert.assertThat(desktop.query("#i12").as(Label.class).getValue(), anyOf(
				equalTo("12/21/12 2:00 AM"),
				equalTo("12/21/12, 2:00 AM") // since Java 9 uses Unicode CLDR
		));
		Assert.assertThat(desktop.query("#i13").as(Label.class).getValue(), allOf(
				startsWith("20 décembre 2012"),
				endsWith("22:00:00 GMT-02:00")
		));
		Assert.assertThat(desktop.query("#i14").as(Label.class).getValue(), startsWith("venerdì 21 dicembre 2012"));
		Assert.assertEquals("Feb 3, 2010", desktop.query("#i15").as(Datebox.class).getText());
	}
}
