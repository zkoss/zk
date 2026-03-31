/* F104_ZK_6059Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Feb 06 16:39:14 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F104_ZK_6059Test extends WebDriverTestCase {
	private static final Pattern HTML_LANG_ATTRIBUTE_PATTERN = Pattern.compile("(?i)\\slang\\s*=\\s*['\"]");

    @Test
    public void testHtmlLangAttributePriority() {
        connect("/test2/F104-ZK-6059.zul");

        JQuery html = jq("html");
        String lang = html.attr("lang");

        assertNotNull(lang);
        assertFalse(lang.isEmpty());
        assertSingleLangAttribute();

        connect("/test2/F104-ZK-6059-2.zul");
        assertEquals("ja", jq("html").attr("lang"));
        assertSingleLangAttribute();

        connect("/test2/F104-ZK-6059-1.zul");
        click(jq("$setFrLocale"));
        waitResponse();
        assertEquals("fr-CA", jq("html").attr("lang"));
        assertSingleLangAttribute();

        connect("/test2/F104-ZK-6059-1.zul");
        click(jq("$setFrLocaleAndGotoRootLang"));
        waitResponse();
        assertEquals("ja", jq("html").attr("lang"));
        assertSingleLangAttribute();
    }

    private void assertSingleLangAttribute() {
		String pageSource = driver.getPageSource();
		int htmlStart = pageSource.toLowerCase().indexOf("<html");
		int htmlEnd = pageSource.indexOf('>', htmlStart);
		assertTrue(htmlStart >= 0 && htmlEnd > htmlStart);

		String htmlStartTag = pageSource.substring(htmlStart, htmlEnd);
		Matcher matcher = HTML_LANG_ATTRIBUTE_PATTERN.matcher(htmlStartTag);
		int langCount = 0;
		while (matcher.find())
			langCount++;
		assertEquals(1, langCount);
    }
}
