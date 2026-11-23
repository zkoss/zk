package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B80_ZK_2861Test extends WebDriverTestCase {
	@Test public void test() {
		connect();
		waitResponse();
		assertEquals("{\"id1\":\"<value1>\",\"id2\":\"value2\",\"id3\":\"value2\"}", getZKLog());
	}
}