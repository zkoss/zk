/* WcagTestCase.java

	Purpose:
		
	Description:
		
	History:
		Fri May 22 15:45:52 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.wcag;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.deque.axe.AXE;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * A basic test case class for accessibility
 * @author rudyhuang
 */
public abstract class WcagTestCase extends WebDriverTestCase {
	private static final URL AXE_SCRIPT_URL = getAxeScriptUrl();

	private static URL getAxeScriptUrl() {
		URI uri = new File("src/archive/wcag/axe.min.js").toURI();
		try {
			return uri.toURL();
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	protected String getFileLocation() {
		String simple = getClass().getSimpleName();
		String name = getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/").replace(simple, "");
		String file = String.valueOf(simple.charAt(0)).toLowerCase() + simple.substring(1).replace("Test", "");
		return name + file + getFileExtension();
	}

	/**
	 * Verify accessibility issues.
	 * If there is any issue, test will fail.
	 */
	protected void verifyA11y() {
		// FIXME: Temporary disabled color-contrast
		AXE.Builder builder = new AXE.Builder(driver, AXE_SCRIPT_URL)
				.options("{ runOnly: ['wcag2a', 'wcag2aa'], rules: { 'color-contrast': { enabled: false } } }");
		JSONObject responseJSON = builder.analyze();
		JSONArray violations = responseJSON.getJSONArray("violations");

		if (violations.length() != 0) {
			Assert.fail(AXE.report(violations));
		}
	}
}
