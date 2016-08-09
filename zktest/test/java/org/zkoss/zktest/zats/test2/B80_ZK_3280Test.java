package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * Created by wenninghsu on 8/2/16.
 */
public class B80_ZK_3280Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse(true);
		Assert.assertTrue(widget(jq("@tab").get(1)).is("selected"));
	}

}
