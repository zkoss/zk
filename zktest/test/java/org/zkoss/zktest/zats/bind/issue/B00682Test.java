/* B00682Test.java
	Purpose:

	Description:

	History:
		Tue Apr 27 16:12:24 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.impl.InputElement;

/**
 * @author jameschu
 */
public class B00682Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		assertEquals(null, desktop.query("#inp11").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp21").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp12").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp22").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp13").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp23").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp14").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp24").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp15").as(InputElement.class).getRawValue());
		assertEquals(null, desktop.query("#inp25").as(InputElement.class).getRawValue());
		assertEquals("-1.0", desktop.query("#inp16").as(InputElement.class).getRawValue().toString());
		assertEquals("-1.0", desktop.query("#inp26").as(InputElement.class).getRawValue().toString());
		assertEquals("-2.0", desktop.query("#inp17").as(InputElement.class).getRawValue().toString());
		assertEquals("-2.0", desktop.query("#inp27").as(InputElement.class).getRawValue().toString());
		assertEquals("-2.0", desktop.query("#inp18").as(InputElement.class).getRawValue().toString());
		assertEquals("-2.0", desktop.query("#inp28").as(InputElement.class).getRawValue().toString());
		assertEquals("-3", desktop.query("#inp19").as(InputElement.class).getRawValue().toString());
		assertEquals("-3", desktop.query("#inp29").as(InputElement.class).getRawValue().toString());
		assertEquals("-3", desktop.query("#inp1a").as(InputElement.class).getRawValue().toString());
		assertEquals("-3", desktop.query("#inp2a").as(InputElement.class).getRawValue().toString());
		assertEquals("-4", desktop.query("#inp1b").as(InputElement.class).getRawValue().toString());
		assertEquals("-4", desktop.query("#inp2b").as(InputElement.class).getRawValue().toString());
	}
}