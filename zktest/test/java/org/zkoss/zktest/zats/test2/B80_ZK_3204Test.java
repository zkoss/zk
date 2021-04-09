/* B80_ZK_3204Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Apr 09 11:59:19 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.lessThan;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_3204Test extends WebDriverTestCase {
	@Test
	public void test() {
		Actions act = new Actions(connect());
		jq("html").scrollTop(2000);
		JQuery bottomCombobox = jq("@combobox");
		JQuery comboboxButton = jq(".z-combobox-button");

		click(comboboxButton);
		waitResponse();
		JQuery comboboxPopup = jq(".z-combobox-popup.z-combobox-open");
		MatcherAssert.assertThat("you should see popup was shown above of comboBox",
			comboboxPopup.offsetTop() + comboboxPopup.height(), lessThan(bottomCombobox.offsetTop()));

		act.sendKeys("a").perform();
		waitResponse();
		JQuery comboitems = jq(".z-combobox-content > .z-comboitem:visible");
		for (int i = 0; i < comboitems.length(); i++) {
			Assert.assertTrue("the popup should filter items that start with 'a'", comboitems.eq(i).text().startsWith("a"));
		}
		MatcherAssert.assertThat("you should see popup's bottom attach the comboBox's top",
			comboboxPopup.offsetTop() + comboboxPopup.height(), lessThan(bottomCombobox.offsetTop()));

		act.sendKeys(Keys.BACK_SPACE).perform();
		waitResponse();
		MatcherAssert.assertThat("popup should not cover comboBox",
			comboboxPopup.offsetTop() + comboboxPopup.height(), lessThan(bottomCombobox.offsetTop()));
	}
}
