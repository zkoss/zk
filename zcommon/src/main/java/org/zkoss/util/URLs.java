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

import org.apache.hc.core5.net.URIBuilder;

/**
 * Utility class for URL operations.
 * @author jumperchen
 * @since 10.0.1
 */
public class URLs {

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

		// Handle JAR URLs specially - they have protocol "jar" and contain "!/" separator
		if ("jar".equals(url.getProtocol())) {
			// For JAR URLs, getFile() returns the full jar path with "!/" separator
			// e.g., "file:/path/to/lib.jar!/META-INF/resources/js/file.js"
			String jarSpec = url.getFile();
			if (jarSpec != null && jarSpec.contains("!/")) {
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

				// Reconstruct the JAR URL preserving the original jar path format
				return new URL("jar:" + jarFilePath + "!/" + internalPath);
			} else {
				throw new MalformedURLException("Invalid JAR URL format: missing !/ separator");
			}
		} else {
			// For regular URLs, prevent SSRF warning by reconstructing via URI
			return new URIBuilder().setScheme(url.getProtocol())
					.setHost(url.getHost()).setPort(url.getPort())
					.setPath(url.getPath())
					.setCustomQuery(url.getQuery()).build().toURL();
		}
	}
}
