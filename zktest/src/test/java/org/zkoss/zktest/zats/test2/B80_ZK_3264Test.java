package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * Created by wenninghsu on 8/9/16.
 */
public class B80_ZK_3264Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse(true);
		Assert.assertEquals("true", jq("@label").eq(1).text());
	}

}
