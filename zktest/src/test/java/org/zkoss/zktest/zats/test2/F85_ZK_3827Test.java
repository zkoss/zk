package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author bob peng
 */
public class F85_ZK_3827Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery msg = jq(".z-combobox-emptySearchMessage");

		String typeString = "a";
		type(jq("@combobox .z-combobox-input"), typeString);
		waitResponse();
		assertFalse("Error: EmptySearchMessage should disappear", msg.isVisible());

		typeString = "g";
		type(jq("@combobox .z-combobox-input"), typeString);
		waitResponse();
		assertTrue("Error: EmptySearchMessage should appear", msg.isVisible());
	}
}