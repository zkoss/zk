/* B96_ZK_6083Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Apr 08 12:27:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Https;

public class B96_ZK_6083Test {

	// -- normalizePath tests --

	@Test
	public void testNormalizePath_normal() {
		Assert.assertEquals("/web/zk/js/zk.wpd", Https.normalizePath("/web/zk/js/zk.wpd"));
	}

	@Test
	public void testNormalizePath_root() {
		Assert.assertEquals("/", Https.normalizePath("/"));
	}

	@Test
	public void testNormalizePath_null() {
		Assert.assertNull(Https.normalizePath(null));
	}

	@Test
	public void testNormalizePath_safeDotDot() {
		// ../within a valid hierarchy should resolve
		Assert.assertEquals("/web/foo.js", Https.normalizePath("/web/zk/../foo.js"));
	}

	@Test
	public void testNormalizePath_traversalAboveRoot() {
		// Trying to go above root should return null
		Assert.assertNull(Https.normalizePath("/web/../../etc/passwd"));
	}

	@Test
	public void testNormalizePath_dotSegments() {
		Assert.assertEquals("/web/js/zk.wpd", Https.normalizePath("/web/./js/./zk.wpd"));
	}

	@Test
	public void testNormalizePath_trailingSlash() {
		Assert.assertEquals("/web/js/", Https.normalizePath("/web/js/"));
	}

	// -- isValidPath tests --

	@Test
	public void testIsValidPath_normal() {
		Assert.assertTrue(Https.isValidPath("/web/zk/js/zk.wpd"));
	}

	@Test
	public void testIsValidPath_null() {
		Assert.assertFalse(Https.isValidPath(null));
	}

	@Test
	public void testIsValidPath_doubleSlash() {
		// normalizePath collapses // into /, so //etc/passwd becomes /etc/passwd
		// which is a valid path. The real defense is that servlet containers
		// normalize // before it reaches ZK.
		Assert.assertTrue(Https.isValidPath("//etc/passwd"));
	}

	@Test
	public void testIsValidPath_dotDotAtRoot() {
		Assert.assertFalse(Https.isValidPath("/../etc/passwd"));
	}

	// -- sanitizePath tests --

	@Test
	public void testSanitizePath_normal() {
		Assert.assertEquals("/web/zk/js/zk.wpd", Https.sanitizePath("/web/zk/js/zk.wpd"));
	}

	@Test
	public void testSanitizePath_null() {
		Assert.assertNull(Https.sanitizePath(null));
	}

	@Test
	public void testSanitizePath_blocksTraversal() {
		// Classic path traversal attack — goes above root
		Assert.assertNull(Https.sanitizePath("/web/../../etc/passwd"));
	}

	@Test
	public void testSanitizePath_blocksWEBINFTraversal() {
		Assert.assertNull(Https.sanitizePath("/web/../../../WEB-INF/web.xml"));
	}

	@Test
	public void testSanitizePath_doubleSlashNormalized() {
		// //etc/passwd normalizes to /etc/passwd which is a valid path.
		// The real protection against // attacks comes from the servlet container
		// and the Path.of().normalize() checks in ClassWebResource.
		Assert.assertEquals("/etc/passwd", Https.sanitizePath("//etc/passwd"));
	}

	@Test
	public void testSanitizePath_allowsSafeRelativeTraversal() {
		// Safe traversal within valid hierarchy
		Assert.assertEquals("/web/foo.js", Https.sanitizePath("/web/zk/../foo.js"));
	}

	@Test
	public void testSanitizePath_fileProtocol() {
		String result = Https.sanitizePath("file:///var/www/index.html");
		Assert.assertNotNull("file:// URI should be preserved after sanitization", result);
		Assert.assertTrue("Should keep file scheme", result.startsWith("file:"));
		Assert.assertFalse("Should not contain '..'", result.contains(".."));
	}

	@Test
	public void testSanitizePath_fileProtocolTraversalAtRoot() {
		Assert.assertNull(Https.sanitizePath("file:///../etc/passwd"));
	}

	@Test
	public void testServletsGetResourceAsStream_fileProtocolTraversalAtRoot() throws Exception {
		Assert.assertNull(Servlets.getResourceAsStream(null, "file:///../etc/passwd"));
	}

	@Test
	public void testServletsGetResourceAsStream_fileProtocolValid() throws Exception {
		Path tempFile = Files.createTempFile("zk-6083-", ".txt");
		Files.writeString(tempFile, "ok", StandardCharsets.UTF_8);
		try (InputStream input = Servlets.getResourceAsStream(null, tempFile.toUri().toString())) {
			Assert.assertNotNull(input);
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}

	@Test
	public void testSanitizePath_encodedDotsNotDecoded() {
		// sanitizePath does NOT URL-decode; that's the servlet container's job.
		// %2e%2e is treated as literal characters, not as ".."
		String result = Https.sanitizePath("/web/%2e%2e/%2e%2e/etc/passwd");
		Assert.assertNotNull("Encoded dots are not decoded by sanitizePath", result);
	}

	// -- /web path checks used by servlet/class resource entrypoints --

	@Test
	public void testWebPathSanitize_blocksEscape() {
		Assert.assertFalse(isValidWebPath(Https.sanitizePath("/web/../../etc/passwd")));
	}

	@Test
	public void testWebPathSanitize_allowsValid() {
		Assert.assertTrue(isValidWebPath(Https.sanitizePath("/web/zk/js/zk.wpd")));
	}

	@Test
	public void testWebPathSanitize_prefixUriBlocksEscape() {
		String webPath = "/web/../../etc/passwd";
		Assert.assertFalse(isValidWebPath(Https.sanitizePath(webPath)));
	}

	@Test
	public void testWebPathSanitize_prefixUriAllowsValid() {
		String webPath = "/web/zk/js/zk.wpd";
		Assert.assertTrue(isValidWebPath(Https.sanitizePath(webPath)));
	}

	// -- CookieThemeResolver.isValidName equivalent logic --

	@Test
	public void testThemeNameValidation() {
		// Theme names with path traversal characters should be rejected
		Assert.assertTrue("Normal theme", isValidThemeName("iceblue_c"));
		Assert.assertTrue("Theme with hyphen", isValidThemeName("my-theme"));
		Assert.assertFalse("Slash", isValidThemeName("../../../etc"));
		Assert.assertFalse("Backslash", isValidThemeName("..\\..\\etc"));
		Assert.assertFalse("Dot", isValidThemeName("theme.name"));
		Assert.assertFalse("Percent encoding", isValidThemeName("theme%2F.."));
		Assert.assertFalse("Null", isValidThemeName(null));
		Assert.assertFalse("Empty", isValidThemeName(""));
		Assert.assertFalse("Blank", isValidThemeName("   "));
	}

	// -- WebSphere Liberty specific attack scenarios --

	@Test
	public void testWebSphereLibertyAttack_classWebResource() {
		// WebSphere Liberty may not normalize pathInfo before passing to servlet
		// Attacker sends: GET /zkau/web/../../../WEB-INF/web.xml
		String pi = "/web/../../../WEB-INF/web.xml";

		// Layer 1: sanitizePath check
		Assert.assertNull("Attack must be blocked by sanitizePath", Https.sanitizePath(pi));

		// Layer 2: /web-prefix guard used by servlet entrypoints
		Assert.assertFalse("Attack must be blocked by /web prefix guard",
				isValidWebPath(Https.sanitizePath(pi)));
	}

	@Test
	public void testWebSphereLibertyAttack_servletContext() {
		// Attacker targets SimpleWebApp.getResource() or ResourceCaches
		String path = "/../../WEB-INF/web.xml";
		Assert.assertNull("Must block servletContext traversal", Https.sanitizePath(path));
	}

	@Test
	public void testWebSphereLibertyAttack_cookieInjection() {
		// Attacker sets cookie: zktheme=../../WEB-INF/web.xml
		String maliciousTheme = "../../WEB-INF/web.xml";
		Assert.assertFalse("Must block cookie-based traversal", isValidThemeName(maliciousTheme));
	}

	// Replicate the validation logic from CookieThemeResolver
	private boolean isValidThemeName(String themeName) {
		if (themeName == null || themeName.trim().isEmpty()) return false;
		for (int j = 0, len = themeName.length(); j < len; ++j) {
			char cc = themeName.charAt(j);
			if (cc == '/' || cc == '\\' || cc == '.' || cc == ':' || cc == '?'
					|| cc == '&' || cc == '=' || cc == '%' || cc == '#' || cc == ' ')
				return false;
		}
		return true;
	}

	private boolean isValidWebPath(String path) {
		return path != null && ("/web".equals(path) || path.startsWith("/web/"));
	}
}
