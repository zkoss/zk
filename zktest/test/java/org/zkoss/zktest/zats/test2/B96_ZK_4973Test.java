package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_4973Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		assertDateboxPopupShowsAfterClick();

		click(jq("$btnYellow"));
		waitResponse();

		click(jq("$btnInvalidate"));
		waitResponse();

		final String backgroundColor = jq("$myTab1").css("background-color").replaceAll("\\s+","");
		Assert.assertEquals("rgb(255,255,0)", backgroundColor);
		assertDateboxPopupShowsAfterClick();

		click(jq("$myTab2"));
		waitResponse();
		assertTab2IsSelectedAfterClick("btnInvalidateTabs");
		assertTab2IsSelectedAfterClick("btnInvalidateTabbox");
		assertTab2IsSelectedAfterClick("btnInvalidate2");
	}

	void assertDateboxPopupShowsAfterClick() {
		click(jq("@datebox").toWidget().$n("btn"));
		waitResponse();
		Assert.assertTrue(jq(".z-datebox-popup.z-datebox-open").exists());
	}

	void assertTab2IsSelectedAfterClick(String buttonId) {
		click(jq("$" + buttonId));
		waitResponse();
		Assert.assertTrue(jq("$myTab2").toWidget().is("selected"));
	}
}
