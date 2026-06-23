/* F104_ZK_5997Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 14 12:48:13 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class F104_ZK_5997Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F104-ZK-5997.xml");
	private static final String TEST_PAGE = "/test2/F104-ZK-5997.zul";

	/**
	 * Verify that every inline script tag in the page source has a nonce attribute.
	 */
	@Test
	public void testAllScriptTagsHaveNonce() {
		connect(TEST_PAGE);
		waitResponse();

		// Get the nonce value from the ZUL EL expression
		String elNonceValue = jq("$elNonce").text().trim();
		assertNotNull(elNonceValue, "CSP nonce should be available via EL");
		assertTrue(elNonceValue.matches("^[A-Za-z0-9+/]+=*$"),
				"Nonce should be a valid base64 string: " + elNonceValue);

		// Get the full page source and check all <script> tags
		String pageSource = driver.getPageSource();

		// Find all <script> tags and verify each has nonce
		Pattern scriptPattern = Pattern.compile("<script(?![^>]*\\bsrc\\s*=)[^>]*>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = scriptPattern.matcher(pageSource);
		int inlineScriptCount = 0;
		int nonceScriptCount = 0;

		while (matcher.find()) {
			String scriptTag = matcher.group();
			inlineScriptCount++;
			if (scriptTag.contains("nonce=")) {
				nonceScriptCount++;
			}
		}

		assertNotEquals(0, inlineScriptCount, "Page should contain inline script tags");
		assertEquals(inlineScriptCount, nonceScriptCount,
				"All inline <script> tags should have nonce attributes. " +
						"Found " + nonceScriptCount + "/" + inlineScriptCount + " with nonce.");
	}

	/**
	 * Verify that a script nonce on a rendered script element matches the CSP nonce.
	 */
	@Test
	public void testScriptNonceMatchesCspNonce() {
		connect(TEST_PAGE);
		waitResponse();

		String elNonceValue = jq("$elNonce").text().trim();
		String scriptNonce = (String) getEval("document.querySelector('script[nonce]').nonce");

		assertNotNull(elNonceValue);
		assertNotNull(scriptNonce);
		assertEquals(elNonceValue, scriptNonce,
				"Script nonce should match the CSP nonce from server");
	}

	/**
	 * Verify that scripts actually execute under strict CSP (no CSP violations).
	 * Checks that the ZK framework loaded, the page rendered, and client-side
	 * interactions work (event listener via w:onChange).
	 */
	@Test
	public void testNoScriptBlockedByCSP() {
		connect(TEST_PAGE);
		waitResponse();

		// Verify the ZK framework loaded (if scripts were blocked, zk would not exist)
		assertTrue(Boolean.parseBoolean(getEval("typeof zk !== 'undefined'")),
				"ZK framework should be loaded (scripts not blocked by CSP)");

		// Verify the page rendered properly (window title visible)
		assertTrue(jq(".z-window-header").exists(), "Window should be rendered");

		// Verify the <?script?> directive executed
		assertTrue(Boolean.parseBoolean(getEval("window.zkDirectiveScriptLoaded === true")),
				"Processing instruction script should have executed");

		// Verify the <script> component executed
		assertTrue(Boolean.parseBoolean(getEval("window.zkScriptComponentLoaded === true")),
				"ZK <script> component should have executed");

		// Verify external <script src="..."> loaded and executed
		assertTrue(Boolean.parseBoolean(getEval("window.zkExternalScriptLoaded === true")),
				"External <script src> should have executed under strict CSP");

		// Verify client-side event listener works (w:onChange)
		type(jq("$eventListener"), "test");
		waitResponse();
		assertEquals("listener-ok", jq("$result").text(),
				"Client-side event listener (w:onChange) should work under strict CSP");

		assertNoAnyError();
	}

	/**
	 * Verify that external script tags (with src attribute) also have nonce.
	 */
	@Test
	public void testExternalScriptTagHasNonce() {
		connect(TEST_PAGE);
		waitResponse();

		String pageSource = driver.getPageSource();

		// Find all <script> tags with src attribute
		Pattern extScriptPattern = Pattern.compile(
				"<script[^>]*\\bsrc\\s*=[^>]*>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = extScriptPattern.matcher(pageSource);
		int extScriptCount = 0;
		int extNonceCount = 0;

		while (matcher.find()) {
			String tag = matcher.group();
			// skip external CDN/framework scripts that are loaded via <link>
			if (tag.contains("F104-ZK-5997-external.js")) {
				extScriptCount++;
				if (tag.contains("nonce=")) {
					extNonceCount++;
				}
			}
		}

		assertTrue(extScriptCount > 0,
				"Page should contain the external script tag for F104-ZK-5997-external.js");
		assertEquals(extScriptCount, extNonceCount,
				"External <script src> tags should have nonce attributes. " +
						"Found " + extNonceCount + "/" + extScriptCount + " with nonce.");
	}

	/**
	 * Verify that a server-pushed AuScript executes on the client under strict CSP.
	 * The AuScript handler routes through jq.globalEval, which must forward the page
	 * nonce so the dynamically-created script element passes the script-src directive.
	 */
	@Test
	public void testAuScriptExecutesUnderCsp() {
		connect(TEST_PAGE);
		waitResponse();

		assertTrue(Boolean.parseBoolean(getEval("typeof window.zkAuScriptExecuted === 'undefined'")),
				"Flag should be unset before the button is clicked");

		click(jq("$evalJsBtn"));
		waitResponse();

		assertTrue(Boolean.parseBoolean(getEval("window.zkAuScriptExecuted === true")),
				"Clients.evalJavaScript payload should have executed under strict CSP");
		assertNoAnyError();
	}

	/**
	 * Verify that a Selectbox renders its items under strict CSP. The default mold
	 * deserialises this.items via JSON.parse (fallback to $eval); a regression that
	 * left $eval as the only path would render zero options because the new $eval
	 * implementation under CSP returns undefined.
	 */
	@Test
	public void testSelectboxItemsUnderCsp() {
		connect(TEST_PAGE);
		waitResponse();

		int optionCount = Integer.parseInt(getEval(
				"zk.Widget.$('$selectboxCsp').$n().querySelectorAll('option').length"));
		assertEquals(3, optionCount,
				"Selectbox should render all three items from the JSON-encoded payload");
		String firstOption = getEval(
				"zk.Widget.$('$selectboxCsp').$n().querySelectorAll('option')[0].textContent");
		assertEquals("alpha", firstOption,
				"First option text should match the server-emitted item");
		assertNoAnyError();
	}
}
