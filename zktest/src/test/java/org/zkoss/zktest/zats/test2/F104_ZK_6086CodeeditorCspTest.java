/* F104_ZK_6086CodeeditorCspTest.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 18 18:05:20 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

/*
 * D4 (decided 2026-06-17): strict-CSP compatibility is in the CE-basic first
 * release. ZK's strict-dynamic mode nonces only script-src and keeps style-src
 * 'unsafe-inline', so CodeMirror's JS-injected highlight styles are permitted
 * and the editor must mount and highlight with no CSP violation. The widget
 * still feeds EditorView.cspNonce (read in bind_) for externally-imposed strict
 * style-src policies that ZK config cannot itself produce.
 */
@ForkJVMTestOnly
public class F104_ZK_6086CodeeditorCspTest extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F104-ZK-6086-Codeeditor-csp.xml");
	private static final String PAGE = "/test2/F104-ZK-6086-Codeeditor-csp.zul";

	@Test
	public void testEditorMountsUnderCsp() {
		connect(PAGE);
		waitResponse();
		// CSP is actually on: ZK emitted a nonce on its bootstrap script
		assertNotNull(getEval("document.getElementsByTagName('script')[0].nonce"),
				"strict CSP active (nonced script present)");
		// the editor mounted despite runtime JS style injection
		assertTrue(jq("$ceCsp .cm-editor").exists(), "CodeMirror mounted under CSP");
		// its content loaded
		assertTrue(jq("$ceCsp .cm-content").text().contains("Hello"), "content rendered under CSP");
		// the crux of D4: CodeMirror injects its editor CSS from JS at runtime
		// (StyleModule -> <style>). Under ZK strict-dynamic CSP it must not be
		// blocked, so a <style> carrying CM rules must be present and active.
		String cmStyles = getEval(
				"String(Array.from(document.querySelectorAll('style'))"
						+ ".filter(function(s){return s.textContent.indexOf('.cm-')>=0;}).length)");
		assertTrue(Integer.parseInt(cmStyles) > 0, "CodeMirror injected styles survived CSP");
		// no client-side error / CSP violation surfaced
		assertNoAnyError();
	}
}
