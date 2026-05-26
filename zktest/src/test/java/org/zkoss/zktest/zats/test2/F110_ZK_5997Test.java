/* F110_ZK_5997Test.java

        Purpose:
                
        Description:
                
        History:
                Tue Apr 14 12:48:13 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.zkoss.test.webdriver.ExternalZkXml;
import org.zkoss.test.webdriver.ForkJVMTestOnly;
import org.zkoss.test.webdriver.WebDriverTestCase;

@ForkJVMTestOnly
public class F110_ZK_5997Test extends WebDriverTestCase {
	@RegisterExtension
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F110-ZK-5997.xml");
	private static final String TEST_PAGE = "/test2/F110-ZK-5997.zul";
	private static final String INJECTION_PAGE = "/test2/F110-ZK-5997Injection.zul";
	private static final String NATIVE_PAGE = "/test2/F110-ZK-5997Native.zul";
	private static final String ZHTML_PAGE = "/test2/F110-ZK-5997.zhtml";
	private static final String AUTHOR_NONCE_PAGE = "/test2/F110-ZK-5997AuthorNonce.zul";
	private static final String AUTHOR_NONCE_ZHTML_PAGE = "/test2/F110-ZK-5997AuthorNonce.zhtml";

	/** A {@code <script>}/{@code <style>} open tag as it left the server. */
	private static final Pattern SCRIPT_OR_STYLE_OPEN_TAG = Pattern.compile("(?i)<(?:script|style)\\b[^>]*>");
	private static final Pattern NONCE_ATTR = Pattern.compile("(?i)\\snonce\\s*=");

	/** Pins the acceptance criterion: the emitted script-src must not fall back on
	 * {@code 'unsafe-inline'}.
	 */
	@Test
	public void testHeaderHasNoUnsafeInline() throws IOException {
		String cspHeader = cspHeaderOf(TEST_PAGE);
		assertNotNull(cspHeader, "ZK should emit a Content-Security-Policy header");
		String scriptSrc = scriptSrcOf(cspHeader);
		assertNotNull(scriptSrc, cspHeader);
		assertTrue(scriptSrc.contains("'strict-dynamic'"), scriptSrc);
		assertTrue(scriptSrc.contains("'nonce-"), scriptSrc);
		assertFalse(scriptSrc.contains("'unsafe-inline'"), scriptSrc);
		// 'unsafe-eval' still ships (EE spel2js); pin it so removing it stays deliberate
		assertTrue(scriptSrc.contains("'unsafe-eval'"), scriptSrc);
	}

	/** A page taken by renderComplete -- a native-root ZUL or a .zhtml page -- must still
	 * carry the CSP header.
	 */
	@Test
	public void testCompletePagesCarryTheCspHeader() throws IOException {
		for (String page : new String[] { NATIVE_PAGE, ZHTML_PAGE }) {
			String cspHeader = cspHeaderOf(page);
			assertNotNull(cspHeader, page + " should carry a Content-Security-Policy header");
			String scriptSrc = scriptSrcOf(cspHeader);
			assertNotNull(scriptSrc, page + ": " + cspHeader);
			assertTrue(scriptSrc.contains("'nonce-"), page + ": " + scriptSrc);
			assertFalse(scriptSrc.contains("'unsafe-inline'"), page + ": " + scriptSrc);
		}
	}

	/** A native {@code <script>} bypasses every buffer the nonce is stamped on: it must
	 * carry the nonce and actually run.
	 */
	@Test
	public void testNativeScriptTagCarriesNonceAndRuns() {
		connect(NATIVE_PAGE);
		waitResponse();
		assertEveryInlineScriptHasNonce();
		assertTrue(Boolean.parseBoolean(getEval("window.zkNativeScriptLoaded === true")),
				"A native <script> must run under strict-dynamic, i.e. carry the nonce");
		assertNoAnyError();
	}

	/** The ZHTML counterpart: {@code org.zkoss.zhtml.Script} builds its own open tag. */
	@Test
	public void testZhtmlScriptTagCarriesNonceAndRuns() {
		connect(ZHTML_PAGE);
		waitResponse();
		assertEveryInlineScriptHasNonce();
		assertTrue(Boolean.parseBoolean(getEval("window.zkZhtmlScriptLoaded === true")),
				"A ZHTML <script> must run under strict-dynamic, i.e. carry the nonce");
		assertNoAnyError();
	}

	/** A page that writes the nonce itself must not get a second one stamped; only the
	 * response text shows the duplicate, the parser collapses it.
	 */
	@Test
	public void testAuthorSuppliedNonceIsNotDuplicated() throws IOException {
		for (String page : new String[] { AUTHOR_NONCE_PAGE, AUTHOR_NONCE_ZHTML_PAGE }) {
			int nonced = 0;
			Matcher tags = SCRIPT_OR_STYLE_OPEN_TAG.matcher(bodyOf(page));
			while (tags.find()) {
				final String tag = tags.group();
				int attrs = 0;
				for (Matcher n = NONCE_ATTR.matcher(tag); n.find();)
					++attrs;
				assertTrue(attrs <= 1,
						page + ": a tag must carry at most one nonce attribute, found " + attrs + ": " + tag);
				if (attrs == 1)
					++nonced;
			}
			assertNotEquals(0, nonced, page + ": no tag carried a nonce at all");
		}
	}

	/** The other half: skipping the stamp must not cost the page its nonce -- both pages
	 * write {@code ${cspNonce}}, so the script is admitted and runs.
	 */
	@Test
	public void testAuthorSuppliedNonceStillAdmitsTheScript() {
		connect(AUTHOR_NONCE_PAGE);
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("window.zkAuthorNonceNativeLoaded === true")),
				"A native <script> with an author-written nonce must still run");

		connect(AUTHOR_NONCE_ZHTML_PAGE);
		waitResponse();
		assertTrue(Boolean.parseBoolean(getEval("window.zkAuthorNonceZhtmlLoaded === true")),
				"A ZHTML <script> with an author-written nonce must still run");
	}

	/** Returns the response body of the given page as the server wrote it. */
	private String bodyOf(String page) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(getAddress() + page).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
			final StringBuilder sb = new StringBuilder();
			for (String line; (line = in.readLine()) != null;)
				sb.append(line).append('\n');
			return sb.toString();
		} finally {
			conn.disconnect();
		}
	}

	/** Returns the Content-Security-Policy header of the given page, or null. */
	private String cspHeaderOf(String page) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(getAddress() + page).openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		try {
			assertNull(conn.getHeaderField("Content-Security-Policy-Report-Only"));
			return conn.getHeaderField("Content-Security-Policy");
		} finally {
			conn.disconnect();
		}
	}

	/** Returns the script-src directive alone; style-src legitimately keeps
	 * 'unsafe-inline'.
	 */
	private static String scriptSrcOf(String cspHeader) {
		for (String directive : cspHeader.split(";")) {
			String d = directive.trim();
			if (d.startsWith("script-src"))
				return d;
		}
		return null;
	}

	/** Every inline script tag on the page carries a nonce, and EL exposes that nonce. */
	@Test
	public void testAllScriptTagsHaveNonce() {
		connect(TEST_PAGE);
		waitResponse();

		String elNonceValue = jq("$elNonce").text().trim();
		assertNotNull(elNonceValue, "CSP nonce should be available via EL");
		assertTrue(elNonceValue.matches("^[A-Za-z0-9+/]+=*$"),
				"Nonce should be a valid base64 string: " + elNonceValue);

		assertEveryInlineScriptHasNonce();
	}

	/** Asserts every inline {@code <script>} carries the one live nonce. Read it through the
	 * {@code .nonce} IDL property: a document with a CSP blanks the content attribute.
	 */
	private void assertEveryInlineScriptHasNonce() {
		int inlineScriptCount = Integer.parseInt(
				getEval("document.querySelectorAll('script:not([src])').length"));
		assertNotEquals(0, inlineScriptCount, "Page should contain inline script tags");

		String expected = getEval("document.querySelector('script[nonce]').nonce");
		assertNotEquals("", expected, "The document should carry a non-empty nonce");

		// distinct nonces across every inline script; "" shows up for an unstamped tag
		String distinct = getEval("(function(){"
				+ "var all = document.querySelectorAll('script:not([src])'), seen = {}, out = [];"
				+ "for (var i = 0; i < all.length; ++i) {"
				+ " var n = all[i].nonce || '';"
				+ " if (!seen[n]) { seen[n] = 1; out.push(n); }"
				+ "}"
				+ "return out.sort().join(',');})()");
		assertEquals(expected, distinct,
				"Every one of the " + inlineScriptCount
						+ " inline <script> tags must carry the live nonce; distinct values were: "
						+ distinct);
	}

	/** A component value that literally contains {@code <script} must reach the client
	 * unchanged, and must never have the live nonce spliced into it.
	 */
	@Test
	public void testComponentValueContainingScriptTagIsNotRewritten() {
		connect(TEST_PAGE);
		waitResponse();

		String victim = jq("$victim").text();
		assertEquals("Paste <script src=x> here", victim,
				"A literal \"<script\" in a component value must not be rewritten");

		String nonce = (String) getEval("document.querySelector('script[nonce]').nonce");
		assertNotNull(nonce);
		assertFalse(victim.contains(nonce),
				"The CSP nonce must never appear inside component data");
		assertNoAnyError();
	}

	/** A rendered script's nonce must equal the CSP nonce the page exposes. */
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

	/** Scripts of every flavour on the page must execute under strict CSP, client-side
	 * event listeners included.
	 */
	@Test
	public void testNoScriptBlockedByCSP() {
		connect(TEST_PAGE);
		waitResponse();

		assertTrue(Boolean.parseBoolean(getEval("typeof zk !== 'undefined'")),
				"ZK framework should be loaded (scripts not blocked by CSP)");

		assertTrue(jq(".z-window-header").exists(), "Window should be rendered");

		assertTrue(Boolean.parseBoolean(getEval("window.zkDirectiveScriptLoaded === true")),
				"Processing instruction script should have executed");

		assertTrue(Boolean.parseBoolean(getEval("window.zkScriptComponentLoaded === true")),
				"ZK <script> component should have executed");

		assertTrue(Boolean.parseBoolean(getEval("window.zkExternalScriptLoaded === true")),
				"External <script src> should have executed under strict CSP");

		type(jq("$eventListener"), "test");
		waitResponse();
		assertEquals("listener-ok", jq("$result").text(),
				"Client-side event listener (w:onChange) should work under strict CSP");

		assertNoAnyError();
	}

	/** External {@code <script src>} tags must carry the live nonce too; read through
	 * {@code .nonce} -- see {@link #assertEveryInlineScriptHasNonce()}.
	 */
	@Test
	public void testExternalScriptTagHasNonce() {
		connect(TEST_PAGE);
		waitResponse();

		int extScriptCount = Integer.parseInt(getEval(
				"document.querySelectorAll('script[src*=\"F110-ZK-5997-external.js\"]').length"));
		assertTrue(extScriptCount > 0,
				"Page should contain the external script tag for F110-ZK-5997-external.js");

		String expected = getEval("document.querySelector('script[nonce]').nonce");
		String distinct = getEval("(function(){"
				+ "var all = document.querySelectorAll('script[src*=\"F110-ZK-5997-external.js\"]'),"
				+ " seen = {}, out = [];"
				+ "for (var i = 0; i < all.length; ++i) {"
				+ " var n = all[i].nonce || '';"
				+ " if (!seen[n]) { seen[n] = 1; out.push(n); }"
				+ "}"
				+ "return out.sort().join(',');})()");
		assertEquals(expected, distinct,
				"External <script src> tags must carry the live nonce; distinct values were: "
						+ distinct);
	}

	/** A quote in {@code src}/{@code charset} must never close the hand-built open tag and
	 * start a {@code <script>} of its own; encoding, not CSP, is what prevents it.
	 */
	@Test
	public void testInjectedScriptTagIsNeverParsed() {
		connect(INJECTION_PAGE);
		waitResponse();

		assertEquals("no-injected-tag", getEval("(function(){"
				+ "var all = document.querySelectorAll('script');"
				+ "for (var i = 0; i < all.length; ++i)"
				+ " if (!all[i].src && all[i].textContent.indexOf('zkCspInjected') >= 0)"
				+ "  return 'injected-tag-parsed';"
				+ "return 'no-injected-tag';})()"),
				"An encoded charset must not produce a second <script> element at all");
		assertEquals("undefined", getEval("typeof window.zkCspInjected"),
				"The injected payload must never execute");

		// every framework-built <script src> here still carries the live nonce
		String expected = getEval("document.querySelector('script[nonce]').nonce");
		assertEquals(expected, getEval("(function(){"
				+ "var all = document.querySelectorAll('script[src*=\"F110-ZK-5997-external.js\"]'),"
				+ " seen = {}, out = [];"
				+ "for (var i = 0; i < all.length; ++i) {"
				+ " var n = all[i].nonce || '';"
				+ " if (!seen[n]) { seen[n] = 1; out.push(n); }"
				+ "}"
				+ "return out.sort().join(',');})()"),
				"Both framework-built <script src> tags must carry the live nonce");

		// the sane-charset sibling proves the nonce actually admits the script
		assertTrue(Boolean.parseBoolean(getEval("window.zkExternalScriptLoaded === true")),
				"A framework <script src> with a valid charset must still execute under strict CSP");
	}

	/** A server-pushed AuScript must execute on the client under strict CSP. */
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

	/** A Selectbox must render its items from the server-emitted payload under strict CSP. */
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
