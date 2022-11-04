package org.zkoss.zephyr.webdriver.mvvm.issue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B00877NPEInSaveOnlyBindingTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery msg = jq("$msg");
		JQuery tb = jq("$tb");

		type(tb, "abc");
		waitResponse();        //System.out.println("******" + tb.getErrorMessage());
		//System.out.println("******" + tb.val());

		type(tb, "Lin");
		waitResponse();        //System.out.println("******" + tb.getErrorMessage());
		//System.out.println("******" + tb.val());
		
/*		var msg = jq("$msg")
			      var tb = jq("$tb")

			      typeKeys(tb.toWidget(), "abc")
			      waitResponse()
			      sleep(500)
			      var errorPopup = jq(".z-errorbox")
			      verifyEquals("", msg.toWidget().get("value"))
			      verifyEquals(1, errorPopup.length())

			      tb.toElement().set("value", "")
			      typeKeys(tb.toWidget(), "Lin")
			      waitResponse()
			      sleep(500)
			      verifyEquals("Lin", msg.toWidget().get("value"))
			      errorPopup = jq(".z-errorbox")
			      verifyEquals(0, errorPopup.length())*/
	}
}
