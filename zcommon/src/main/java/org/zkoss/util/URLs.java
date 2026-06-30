/* URLs.java

	Purpose:
		
	Description:
		
	History:
		4:11 PM 2024/6/7, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.hc.core5.net.URIBuilder;

/**
 * Utility class for URL operations.
 * @author jumperchen
 * @since 10.0.1
 */
public class URLs {

	// A JAR-family URL nests another URL in its file part, e.g.
	// "file:/path/lib.jar!/entry". Detect that nested URL by a leading
	// RFC-3986 scheme (length >= 2, so a Windows drive letter such as "C:" is
	// not mistaken for one). Regular http/https/file URLs have a file part that
	// starts with "/", so they do not match.
	private static final Pattern NESTED_URL_SCHEME =
			Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]+:.*");

	/**
	 * Sanitizes a URL to prevent MalformedURLException and SSRF warning.
	 *
	 * @param url The URL to be sanitized.
	 * @return The sanitized URL.
	 * @throws MalformedURLException If the URL is not in the correct format.
	 * @throws URISyntaxException If the URL is not formatted strictly according to RFC2396 and cannot be converted to a URI.
	 */
	public static URL sanitizeURL(URL url) throws MalformedURLException, URISyntaxException {
		if (url == null) return null;

		String protocol = url.getProtocol();
		// Handle JAR-family URLs specially. They follow the JarURLConnection form
		// "<protocol>:<innerURL>!/<entry>", so getFile() is itself a nested URL
		// (e.g. "file:/path/to/lib.jar!/META-INF/resources/js/file.js"). ZK-6075:
		// IBM WebSphere / Open Liberty emit this form as "wsjar:" instead of the
		// standard "jar:"; detecting by the nested-URL shape rather than the
		// protocol name handles both without a per-protocol name list.
		String jarSpec = url.getFile();
		if (jarSpec != null && NESTED_URL_SCHEME.matcher(jarSpec).matches()) {
			// For JAR URLs, getFile() returns the full jar path with "!/" separator
			// e.g., "file:/path/to/lib.jar!/META-INF/resources/js/file.js"
			if (!jarSpec.contains("!/")) {
				throw new MalformedURLException("Invalid JAR URL format: missing !/ separator");
			}
			int separatorIndex = jarSpec.indexOf("!/");
			String jarFilePath = jarSpec.substring(0, separatorIndex);
			String internalPath = jarSpec.substring(separatorIndex + 2);

			if (jarFilePath.isEmpty() || internalPath.isEmpty()) {
				throw new MalformedURLException("Invalid JAR URL format: empty jar path or internal path");
			}

			// Sanitize the jar file URL (e.g., "file:/path/to/lib.jar")
			// Validate by parsing through URL and URI, but preserve original format
			URL jarURL = new URL(jarFilePath);

			// Validate the URL can be converted to URI (SSRF check)
			jarURL.toURI();

			// Reconstruct the JAR URL preserving the original protocol (jar or wsjar)
			// and the inner jar path format.
			return new URL(protocol + ":" + jarFilePath + "!/" + internalPath);
		} else {
			// For regular URLs, prevent SSRF warning by reconstructing via URI
			return new URIBuilder().setScheme(url.getProtocol())
					.setHost(url.getHost()).setPort(url.getPort())
					.setPath(url.getPath())
					.setCustomQuery(url.getQuery()).build().toURL();
		}
	}
}
