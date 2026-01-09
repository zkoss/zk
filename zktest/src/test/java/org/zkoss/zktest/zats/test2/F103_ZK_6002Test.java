package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * Test for ZK-6002: Modern XMLHttpRequest Event Handling for Comet Server Push
 *
 * Acceptance Criteria:
 * 1. Modern Event Handlers - Uses onload, onerror, ontimeout, onabort instead of onreadystatechange
 * 2. Extensibility - Can override _createXHR via zk.override()
 * 3. Event Classification - Dedicated handlers for different event types
 * 4. Backward Compatibility - Opt-in via "org.zkoss.zkex.ui.comet.modernEvent" property
 */
@ForkJVMTestOnly
public class F103_ZK_6002Test extends WebDriverTestCase {

	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F103-ZK-6002-zk.xml");

	@Test
	public void test() {
		connect();

		// Click Start button to initiate server push
		click(jq("@button:contains(Start)"));
		waitResponse();

		// Wait for server push to execute at least one XHR request
		// This ensures _createXHR is called and our override is triggered
		// Verify server push is working
		JQuery cnt = jq("$cnt");
		String t1 = cnt.text();
		sleep(2000);
		String t2 = cnt.text();
		assertTrue(Integer.parseInt(t2) > Integer.parseInt(t1),
			"Server push should increment counter");

		// Verify modern event handling is enabled (opt-in)
		String modernResult = (String) getEval(
			"zk.Desktop.$()._cmsp && zk.Desktop.$()._cmsp._modernEvent ? 'modern' : 'legacy'"
		);
		assertTrue("modern".equals(modernResult),
			"Should use modern event handlers when org.zkoss.zkex.ui.comet.modernEvent=true");

		// Verify the override was called
		// Note: getEval returns String "true" not Boolean true
		Object overrideResult = getEval("!!window._customXHRCalled");
		assertTrue("true".equals(String.valueOf(overrideResult)),
			"Custom _createXHR override should have been called, demonstrating extensibility");

		// Event Classification and Context
		// Verify that dedicated event handlers exist (not just onreadystatechange)
		Object hasModernHandlers = getEval(
			"(function() {" +
			"  var sp = zk.Desktop.$()._cmsp;" +
			"  return typeof sp._onLoad === 'function' && " +
			"         typeof sp._onError === 'function' && " +
			"         typeof sp._onTimeout === 'function' && " +
			"         typeof sp._onAbort === 'function';" +
			"})()"
		);
		assertTrue("true".equals(String.valueOf(hasModernHandlers)),
			"Should have dedicated handlers for load, error, timeout, and abort events");

		// Verify _createXHR is an independent, overridable function
		Object createXHRResult = getEval(
			"typeof zk.Desktop.$()._cmsp._createXHR === 'function'"
		);
		assertTrue("true".equals(String.valueOf(createXHRResult)),
			"Should have independent _createXHR function for extensibility");
	}
}
