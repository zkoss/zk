/* B104_ZK_6075Test.java

        Purpose:

        Description:

        History:
                Wed Mar 25 17:24:36 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.zkoss.util.URLs;

public class B104_ZK_6075Test {

	@BeforeAll
	public static void setup() {
		try {
			// Register a wsjar URL handler that mirrors how IBM WebSphere / Open Liberty
			// parse "wsjar:file:/...jar!/inner" — i.e. the same way the JDK's built-in
			// jar handler does. Without overriding parseURL, the JDK default parser
			// mangles the inner "file:/...!/..." structure and the test wouldn't
			// reproduce the production behavior.
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				@Override
				public URLStreamHandler createURLStreamHandler(String protocol) {
					if ("wsjar".equals(protocol)) {
						return new URLStreamHandler() {
							@Override
							protected URLConnection openConnection(URL u) {
								return null;
							}
							@Override
							protected void parseURL(URL u, String spec, int start, int limit) {
								String file = spec.substring(start, limit);
								setURL(u, "wsjar", "", -1, null, null, file, null, null);
							}
						};
					}
					return null;
				}
			});
		} catch (Error e) {
			// URL.setURLStreamHandlerFactory may only be called once per JVM.
		}
	}

	@Test
	public void testSanitizeWsjarURL() throws Exception {
		// Simulate a wsjar URL as produced by IBM WebSphere / Open Liberty
		URL wsjarUrl = new URL("wsjar:file:/path/to/server/workarea/cache/WEB-INF/lib/zweb-10.3.0.1-jakarta.jar!/web/WEB-INF/tld/web/theme.dsp.tld");
		URL sanitized = URLs.sanitizeURL(wsjarUrl);

		assertNotNull(sanitized);
		assertEquals("wsjar", sanitized.getProtocol());
		assertEquals("file:/path/to/server/workarea/cache/WEB-INF/lib/zweb-10.3.0.1-jakarta.jar!/web/WEB-INF/tld/web/theme.dsp.tld", sanitized.getFile());
		// The actual ZK-6075 bug: without the fix, ":" and "!" inside the inner file: URL
		// got URL-encoded to "%3A" and "%21", causing "no protocol: file%3A/..." downstream.
		assertNoEncodedSeparators(sanitized);
	}

	@Test
	public void testSanitizeWsjarURLWithZkResources() throws Exception {
		// Path mirroring the one from the reported FFDC log
		URL wsjarUrl = new URL("wsjar:file:/Users/user/Downloads/openliberty/wlp/usr/servers/defaultServer/workarea/org.eclipse.osgi/64/data/cache/zk10301-liberty-repro/.cache/WEB-INF/lib/zweb-10.3.0.1-jakarta.jar!/web/WEB-INF/tld/web/theme.dsp.tld");
		URL sanitized = URLs.sanitizeURL(wsjarUrl);

		assertNotNull(sanitized);
		assertEquals("wsjar", sanitized.getProtocol());
		assertNoEncodedSeparators(sanitized);
	}

	@Test
	public void testSanitizeWsjarURLPreservesProtocol() throws Exception {
		// Verify the protocol stays "wsjar" — not silently rewritten to "jar"
		URL wsjarUrl = new URL("wsjar:file:/opt/ibm/wlp/lib/zul-10.3.0.1.jar!/js/zul/wgt/mold/inputgroup.js");
		URL sanitized = URLs.sanitizeURL(wsjarUrl);

		assertNotNull(sanitized);
		assertEquals("wsjar", sanitized.getProtocol());
		assertEquals("file:/opt/ibm/wlp/lib/zul-10.3.0.1.jar!/js/zul/wgt/mold/inputgroup.js", sanitized.getFile());
		assertNoEncodedSeparators(sanitized);
	}

	@Test
	public void testSanitizeJarURLStillWorks() throws Exception {
		// Ensure regular jar URLs still work after the change
		URL jarUrl = new URL("jar:file:/path/to/lib.jar!/META-INF/resources/js/file.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
		assertEquals("file:/path/to/lib.jar!/META-INF/resources/js/file.js", sanitized.getFile());
		assertNoEncodedSeparators(sanitized);
	}

	@Test
	public void testSanitizeWsjarURLWithoutSeparatorIsReturnedAsIs() throws Exception {
		// ZK-6075 loosening: a JAR-family URL whose file part lacks the "!/"
		// entry separator (e.g. a bare wsjar-wrapped jar) must no longer be
		// rejected with MalformedURLException; non-network URLs are returned
		// unchanged instead of being forced into a "<jar>!/<entry>" shape.
		URL wsjarUrl = new URL("wsjar:file:/opt/ibm/wlp/lib/zul-10.3.0.1.jar");
		URL sanitized = URLs.sanitizeURL(wsjarUrl);

		assertNotNull(sanitized);
		assertEquals("wsjar", sanitized.getProtocol());
		assertEquals("file:/opt/ibm/wlp/lib/zul-10.3.0.1.jar", sanitized.getFile());
		assertNoEncodedSeparators(sanitized);
	}

	@Test
	public void testSanitizeFileURLWithEncodedCharsNotDoubleEncoded() throws Exception {
		// A file URL whose path is already percent-encoded (e.g. an exploded
		// WAR under "Program Files") must be returned untouched: rebuilding it
		// via URIBuilder re-encoded "%20" to "%2520", which no longer resolves
		// (FileNotFoundException at openStream).
		URL fileUrl = new URL("file:/C:/Program%20Files/app/webroot/js/span.js");
		URL sanitized = URLs.sanitizeURL(fileUrl);

		assertNotNull(sanitized);
		assertEquals("file:/C:/Program%20Files/app/webroot/js/span.js", sanitized.toString());
		assertFalse(sanitized.toString().contains("%2520"),
				"already-encoded path must not be double-encoded: " + sanitized);
	}

	@Test
	public void testSanitizeFileURLWithRawSpaceDoesNotThrow() throws Exception {
		// Pre-10.0.1 behavior restored: no URI-strictness is imposed on
		// non-network URLs; whatever the container hands us is returned as-is
		// (Servlets.getResourceAsStream would otherwise turn a working
		// deployment into "resource not found").
		URL fileUrl = new URL("file:/path/my lib/file.js");
		URL sanitized = URLs.sanitizeURL(fileUrl);

		assertNotNull(sanitized);
		assertEquals("file:/path/my lib/file.js", sanitized.toString());
	}

	@Test
	public void testSanitizeFtpURLPreservesCredentials() throws Exception {
		// ftp is not in the http/https normalization list; it must pass through
		// untouched. The URIBuilder rebuild silently dropped the user-info
		// ("user:pass@") because it never called setUserInfo, breaking
		// authenticated ftp resources.
		URL ftpUrl = new URL("ftp://user:pass@host:2121/dir/file.bin");
		URL sanitized = URLs.sanitizeURL(ftpUrl);

		assertNotNull(sanitized);
		assertEquals("ftp://user:pass@host:2121/dir/file.bin", sanitized.toString());
		assertEquals("user:pass", sanitized.getUserInfo());
	}

	@Test
	public void testSanitizeFtpURLWithEncodedCharsNotDoubleEncoded() throws Exception {
		// ftp is a supported resource URI in Servlets.toURL(); an already-encoded
		// path must not be rebuilt through URIBuilder and re-encoded to "%2520".
		URL ftpUrl = new URL("ftp://host/my%20dir/file.bin");
		URL sanitized = URLs.sanitizeURL(ftpUrl);

		assertNotNull(sanitized);
		assertEquals("ftp://host/my%20dir/file.bin", sanitized.toString());
		assertFalse(sanitized.toString().contains("%2520"),
				"already-encoded ftp path must not be double-encoded: " + sanitized);
	}

	@Test
	public void testRegularHttpURLNotTreatedAsJar() throws Exception {
		// http/https stay on the URIBuilder
		// normalization path; a "!/" in an http path must not trigger any JAR
		// special-casing, and host/path must survive the rebuild.
		URL httpUrl = new URL("http://example.com/a/b!/c.js");
		URL sanitized = URLs.sanitizeURL(httpUrl);

		assertNotNull(sanitized);
		assertEquals("http", sanitized.getProtocol());
		assertEquals("example.com", sanitized.getHost());
	}

	private static void assertNoEncodedSeparators(URL url) {
		String s = url.toString();
		assertFalse(s.contains("%3A"), "URL should not contain encoded ':' (%3A): " + s);
		assertFalse(s.contains("%21"), "URL should not contain encoded '!' (%21): " + s);
	}
}
