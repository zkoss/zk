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
	 * Sanitizes a URL for resource loading.
	 *
	 * <p>Only {@code http}/{@code https} URLs keep the legacy URIBuilder rebuild;
	 * a URL of any other protocol is returned unchanged, so the declared
	 * exceptions can only be thrown for {@code http}/{@code https} URLs.
	 *
	 * @param url The URL to be sanitized.
	 * @return The sanitized URL.
	 * @throws MalformedURLException If an http/https URL cannot be reconstructed.
	 * @throws URISyntaxException If an http/https URL is not formatted strictly according to RFC2396 and cannot be converted to a URI.
	 */
	public static URL sanitizeURL(URL url) throws MalformedURLException, URISyntaxException {
		if (url == null) return null;

		String protocol = url.getProtocol();
		// Preserve the existing http/https behavior. Any other protocol
		// (file, jar, wsjar, vfs, ftp, ...) is returned untouched - its format is owned by its
		// URLStreamHandler, and rebuilding may corrupt it (e.g. percent-encode
		// the separators of a nested JAR URL, or drop the user-info).
		if ("http".equals(protocol) || "https".equals(protocol)) {
			return new URIBuilder().setScheme(protocol)
					.setHost(url.getHost()).setPort(url.getPort())
					.setPath(url.getPath())
					.setCustomQuery(url.getQuery()).build().toURL();
		}
		return url;
	}
}
