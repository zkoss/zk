package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_5156Test extends WebDriverTestCase {

	@Test
	public void test() {
		connect();
		Assert.assertEquals("123456789.02", jq("$decimal-1").toElement().get("value"));
		Assert.assertEquals("12,34,56,789.02", jq("$decimal-2").toElement().get("value"));
		Assert.assertEquals("12,34,56,789.02", jq("$decimal-3").toElement().get("value"));
	}
}
