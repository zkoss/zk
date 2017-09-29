package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertFalse;

/**
 * @author bob peng
 */
public class B85_ZK_3732Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery btn = jq("$btn");
		click(btn);
		waitResponse();
		assertFalse("error popped", jq(".z-messagebox-error").exists());
	}
}