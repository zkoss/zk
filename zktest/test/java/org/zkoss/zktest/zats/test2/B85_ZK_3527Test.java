package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author bob peng
 */
public class B85_ZK_3527Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		for (int i = 0; i < 4; i++) {
			String str = jq(".z-datebox-input:eq(" + i + ")").val();
			if (i == 2) {
				assertTrue("Wrong hours or minutes : " + str, str.contains("00:36"));
			} else {
				assertTrue("Wrong hours or minutes : " + str, str.contains("00:00"));
			}
		}
	}
}