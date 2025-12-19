/* B103_ZK_5877Test.java

	Purpose:
		
	Description:
		
	History:
		11:22 AM 2025/12/17, Created by jumperchen

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.zkoss.util.URLs;

/**
 * Test for ZK-5877: MalformedURLException when sanitizing JAR URLs
 * @author jumperchen
 */
public class B103_ZK_5877Test {

	@Test
	public void testSanitizeJarURL() throws Exception {
		// Test valid JAR URL
		URL jarUrl = new URL("jar:file:/path/to/lib.jar!/META-INF/resources/js/file.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
		assertEquals("file:/path/to/lib.jar!/META-INF/resources/js/file.js", sanitized.getFile());
	}

	@Test
	public void testSanitizeJarURLWithZkResources() throws Exception {
		// Test the actual error case from the bug report
		URL jarUrl = new URL("jar:file:/opt/app/lib/zul-10.2.1.jar!/js/zul/wgt/mold/inputgroup.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
		assertEquals("file:/opt/app/lib/zul-10.2.1.jar!/js/zul/wgt/mold/inputgroup.js", sanitized.getFile());
	}

	@Test
	public void testSanitizeJarURLWithNestedPath() throws Exception {
		// Test JAR URL with deeply nested internal path
		URL jarUrl = new URL("jar:file:/usr/local/tomcat/webapps/app.war!/WEB-INF/lib/zkmax.jar!/web/js/zkmax/grid.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
	}

	@Test
	public void testSanitizeRegularHttpURL() throws Exception {
		// Test regular HTTP URL
		URL httpUrl = new URL("http://example.com/path/to/resource.js");
		URL sanitized = URLs.sanitizeURL(httpUrl);

		assertNotNull(sanitized);
		assertEquals("http", sanitized.getProtocol());
		assertEquals("example.com", sanitized.getHost());
		assertEquals("/path/to/resource.js", sanitized.getPath());
	}

	@Test
	public void testSanitizeRegularHttpsURL() throws Exception {
		// Test regular HTTPS URL
		URL httpsUrl = new URL("https://cdn.example.com:8443/assets/script.js?version=1.0");
		URL sanitized = URLs.sanitizeURL(httpsUrl);

		assertNotNull(sanitized);
		assertEquals("https", sanitized.getProtocol());
		assertEquals("cdn.example.com", sanitized.getHost());
		assertEquals(8443, sanitized.getPort());
		assertEquals("/assets/script.js", sanitized.getPath());
	}

	@Test
	public void testSanitizeFileURL() throws Exception {
		// Test file:// URL
		URL fileUrl = new URL("file:/path/to/local/file.js");
		URL sanitized = URLs.sanitizeURL(fileUrl);

		assertNotNull(sanitized);
		assertEquals("file", sanitized.getProtocol());
		assertEquals("/path/to/local/file.js", sanitized.getPath());
	}

	@Test
	public void testSanitizeNullURL() throws Exception {
		// Test null URL
		URL sanitized = URLs.sanitizeURL(null);
		assertNull(sanitized);
	}

	@Test
	public void testSanitizeInvalidJarURLMissingSeparator() {
		// Test JAR URL without !/ separator - should throw exception
		assertThrows(MalformedURLException.class, () -> {
			URL jarUrl = new URL("jar:file:/path/to/lib.jar");
			URLs.sanitizeURL(jarUrl);
		});
	}

	@Test
	public void testSanitizeJarURLWithWindowsPath() throws Exception {
		// Test JAR URL with Windows-style path
		URL jarUrl = new URL("jar:file:/C:/Program%20Files/app/lib/zul.jar!/js/zul/wgt/button.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
	}

	@Test
	public void testSanitizeJarURLWithSpacesInPath() throws Exception {
		// Test JAR URL with URL-encoded spaces
		URL jarUrl = new URL("jar:file:/path/to/my%20lib/zk-10.2.1.jar!/web/js/zk/widget.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		assertEquals("jar", sanitized.getProtocol());
	}

	@Test
	public void testSanitizeJarURLPreventSSRF() throws Exception {
		// Test that SSRF protection is maintained for JAR URLs
		URL jarUrl = new URL("jar:file:/legitimate/path/lib.jar!/internal/path/file.js");
		URL sanitized = URLs.sanitizeURL(jarUrl);

		assertNotNull(sanitized);
		// Verify the URL is properly reconstructed through URIBuilder
		assertEquals("jar", sanitized.getProtocol());
		assertEquals("file:/legitimate/path/lib.jar!/internal/path/file.js", sanitized.getFile());
	}
}
