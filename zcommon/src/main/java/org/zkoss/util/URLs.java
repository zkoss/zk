/* URLs.java

	Purpose:
		
	Description:
		
	History:
		4:11 PM 2024/6/7, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.util;

import java.net.MalformedURLException;
import java.net.URI;
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

		final String urlString = url.getPath();
		// avoid java.net.MalformedURLException: no !/ in spec
		if (urlString.contains("!/")) {
			String[] parts = urlString.split("!/");
			if (parts.length == 2) {
				String jarFilePath = parts[0];
				String internalPath = parts[1];

				// Ensure the jarFilePath is properly formed
				URL jarURL = new URL(jarFilePath);
				URI jarURI = new URIBuilder().setScheme(jarURL.getProtocol())
						.setHost(jarURL.getHost()).setPort(jarURL.getPort())
						.setPath(jarURL.getPath()).build();

				// Combine the jar URI with the internal path
				return new URL("jar:" + jarURI + "!/" + internalPath);
			} else {
				throw new MalformedURLException("Invalid JAR URL format");
			}
		} else {
			// prevent SSRF warning
			return new URIBuilder().setScheme(url.getProtocol())
					.setHost(url.getHost()).setPort(url.getPort())
					.setPath(url.getPath())
					.setCustomQuery(url.getQuery()).build().toURL();
		}
	}
}
