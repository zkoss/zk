package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertFalse;

/**
 * @author bob peng
 */
public class B85_ZK_3715Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq(".z-button"));
		waitResponse();
		assertFalse("error popped", jq(".z-messagebox-error").exists());
	}
}