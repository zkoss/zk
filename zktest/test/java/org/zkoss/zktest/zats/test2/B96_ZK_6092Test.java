/* B96_ZK_6092Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Apr 23 15:38:26 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

public class B96_ZK_6092Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();

		String version = (String) getEval("window.DOMPurify && window.DOMPurify.version");
		Assert.assertEquals(
				"DOMPurify should be upgraded to 3.4.1 to fix CVE-2025-15599 and CVE-2026-0540",
				"3.4.1", version);

		String[] tags = {"textarea", "noscript", "xmp", "noembed", "noframes", "iframe"};
		for (String tag : tags) {
			String payload = "<a title=\"</" + tag + "><img src=x onerror=alert(1)>\">test</a>";
			String js = "(function(){var out = window.DOMPurify.sanitize("
					+ "'" + payload + "', {SAFE_FOR_XML: true});"
					+ "return /<\\/" + tag + "/i.test(out) || /onerror/i.test(out);})()";
			Object unsafe = getEval(js);
			Assert.assertEquals(
					"DOMPurify must strip closing </" + tag + "> and onerror payload from attribute values",
					"false", String.valueOf(unsafe));
		}
	}
}
