/* Va01Test.java

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;

public class Va01Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String UNDER_AGE = "Under Age";
		String ADULT = "Adult";

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		Intbox ageBox = ageBoxAgent.as(Intbox.class);
		ComponentAgent submitButton = desktopAgent.query("#submitButton");
		Label adultLabel = desktopAgent.query("#adultLabel").as(Label.class);

		assertEquals(new Integer(-1), ageBox.getValue());

		ageBoxAgent.input(22);
		submitButton.click();
		assertEquals(ADULT, adultLabel.getValue());

		ageBoxAgent.input(-1);
		submitButton.click();
		assertEquals(ADULT, adultLabel.getValue());

		ageBoxAgent.input(11);
		submitButton.click();
		assertEquals(UNDER_AGE, adultLabel.getValue());
	}
}
