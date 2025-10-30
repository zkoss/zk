/* TimeZoneDataBaseVersionChecker.java

		Purpose:
				
		Description:
				
		History:
				Mon Jun 30 15:47:16 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.zone.ZoneRulesProvider;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.io.Files;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.lang.Library;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * Utilities to check whether JDK and moment.js's time zone database version matched.
 *
 * @author Jamson Chan
 * @since 10.3.0
 */
public class TimeZoneDataBaseVersionChecker implements WebAppInit {

	private static final Logger log = LoggerFactory.getLogger(TimeZoneDataBaseVersionChecker.class);
	private static final String UNKNOWN_TZDB_VERSION = "unknown";
	private static final String _jarBuildInClientTZDBVersion;
	private static String _currentClientTZDBVersion;

	static {
		_jarBuildInClientTZDBVersion = _currentClientTZDBVersion = extractJarBuildInClientTZDBVersion();
	}

	@Override
	public void init(WebApp wapp) throws Exception {
		String path = Library.getProperty("org.zkoss.zk.moment.timezone.path");
		_currentClientTZDBVersion = _jarBuildInClientTZDBVersion;
		if (path != null) {
			InputStream is = null;
			try {
				is = WebApps.getCurrent().getResourceAsStream(path);
				if (is != null) {
					byte[] bytes = Files.readAll(is);
					if (bytes.length > 0) {
						// ZK-5640
						String jsonFileContent = new String(bytes, StandardCharsets.UTF_8);
						_currentClientTZDBVersion = getClientTZDBVersionFromJSONString(jsonFileContent);
					}
				}
			} finally {
				Files.close(is);
			}
		}
		// ZK-5640
		String serverTZDBVersion = ZoneRulesProvider.getVersions("UTC").lastEntry().getKey();
		if (!Objects.equals(_currentClientTZDBVersion, serverTZDBVersion)) {
			log.warn("Time zone data version mismatch detected:\n"
					+ " - Client (moment.js) tzdb version: " + _currentClientTZDBVersion + "\n"
					+ " - Server (JDK) tzdb version: " + serverTZDBVersion + "\n"
					+ "Date and time values may be incorrect if time zone rules differ.\n"
					+ "To resolve, update the moment-timezone data on the client and/or the JDK time zone data (TZUpdater or Java update) on the server so both use the same version.");
		}
	}

	/**
	 * Returns client timezone database version.
	 * <p>
	 * If didn't provide a customized tzdb data, returns the jar build-in timezone data version.
	 */
	public static String getClientTimeZoneDataBaseVersion() {
		return _currentClientTZDBVersion;
	}

	/**
	 * Extract the timezone database version from the JSON string.
	 * @param input the JSON string
	 * @return the timezone database version
	 */
	private static String getClientTZDBVersionFromJSONString(String input) {
		JSONObject json = (JSONObject) new JSONParser().parse(input);
		return json.get("version") != null ? json.get("version").toString() : UNKNOWN_TZDB_VERSION;
	}

	/**
	 * Extract the timezone database version from the jar build-in moment-timezone-with-data.src.js file.
	 * @return the timezone database version
	 */
	private static String extractJarBuildInClientTZDBVersion() {
		try (InputStream is = TimeZoneDataBaseVersionChecker.class.getClassLoader().getResourceAsStream("web/js/zk/ext/moment-timezone-with-data.src.js")) {
			if (is != null) {
				byte[] bytes = Files.readAll(is);
				if (bytes.length > 0) {
					String jsFileContent = new String(bytes, StandardCharsets.UTF_8).replace(" ", "");
					int targetJsonStartIndex = jsFileContent.indexOf("loadData({") + 9,
							targetJsonEndIndex = -1,
							fileEndIndex = jsFileContent.length(),
							curlyBracketsStack = 0;
					for (int i = targetJsonStartIndex; i < fileEndIndex; i++) {
						if (jsFileContent.charAt(i) == '{')
							curlyBracketsStack++;
						else if (jsFileContent.charAt(i) == '}')
							curlyBracketsStack--;
						if (curlyBracketsStack == 0) {
							targetJsonEndIndex = i;
							break;
						}
					}
					return getClientTZDBVersionFromJSONString(jsFileContent.substring(targetJsonStartIndex, targetJsonEndIndex + 1));
				}
			}
		} catch (IOException e) {
			log.warn("Cannot extracting moment-timezone-with-data.src.js timezone database version from zk jar.");
		}
		return UNKNOWN_TZDB_VERSION;
	}

}
