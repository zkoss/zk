/* B96_ZK_4763Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 18 14:56:43 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

public class B96_ZK_4763Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent r1 = desktop.query("#r_1");
		ComponentAgent r2 = desktop.query("#r_2");
		ComponentAgent movebtn = desktop.query("#move");
		Radiogroup rg1 = desktop.query("#rg_t1").as(Radiogroup.class);
		Radiogroup rg2 = desktop.query("#rg_t2").as(Radiogroup.class);

		r1.check(true);
		Assertions.assertEquals(1, rg1.getSelectedIndex());
		Assertions.assertEquals(-1, rg2.getSelectedIndex());
		movebtn.click();
		Assertions.assertEquals(-1, rg1.getSelectedIndex());
		Assertions.assertEquals(1, rg2.getSelectedIndex());
		r2.check(true);
		r1.check(true);
		Assertions.assertTrue(r1.as(Radio.class).isSelected());
		Assertions.assertFalse(r2.as(Radio.class).isSelected());
	}
}
