package org.zkoss.zktest.zats.bind.issue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.Zats;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Textbox;

public class B00877NPEInSaveOnlyBindingTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent msg = desktop.query("#msg");
		ComponentAgent tb = desktop.query("#tb");
		
		tb.type("abc");
		System.out.println("******" + tb.as(Textbox.class).getErrorMessage());
		System.out.println("******" + tb.as(Textbox.class).getValue());
		
		tb.type("Lin");
		System.out.println("******" + tb.as(Textbox.class).getErrorMessage());
		System.out.println("******" + tb.as(Textbox.class).getValue());
		
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
