/* Va03Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.databinding.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class Va03Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		ComponentAgent minusButton = desktopAgent.query("#minusButton");
		Label adultLabel = desktopAgent.query("#adultLabel").as(Label.class);
		Label ageLabel = desktopAgent.query("#ageLabel").as(Label.class);

		ageBoxAgent.input(1);
		minusButton.click();
		assertEquals(UNDER_AGE, adultLabel.getValue());

		ageBoxAgent.input(28);
		minusButton.click();
		assertEquals("18", ageLabel.getValue());
		assertEquals(ADULT, adultLabel.getValue());
	}
}
