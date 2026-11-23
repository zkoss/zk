/* Va05Test.java

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

/**
 * @author jameschu
 */
public class Va05Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktopAgent = connect();
		String UNDER_AGE = "Under Age";
		String OVER_AGE = "Over Age";

		ComponentAgent ageBoxAgent = desktopAgent.query("#ageBox");
		ComponentAgent submitButton = desktopAgent.query("#submitButton");
		ComponentAgent limitBoxAgent = desktopAgent.query("#limitBox");
		Intbox limitBox = limitBoxAgent.as(Intbox.class);
		Label messageLabel = desktopAgent.query("#messageLabel").as(Label.class);

		ageBoxAgent.input(-1);
		submitButton.click();
		assertEquals(UNDER_AGE + " " + limitBox.getValue(), messageLabel.getValue());

		ageBoxAgent.input(11);
		submitButton.click();
		assertEquals(OVER_AGE + " " + limitBox.getValue(), messageLabel.getValue());

		limitBoxAgent.input(20);
		submitButton.click();
		assertEquals(UNDER_AGE + " " + limitBox.getValue(), messageLabel.getValue());

		ageBoxAgent.input(22);
		submitButton.click();
		assertEquals(OVER_AGE + " " + limitBox.getValue(), messageLabel.getValue());
	}
}
