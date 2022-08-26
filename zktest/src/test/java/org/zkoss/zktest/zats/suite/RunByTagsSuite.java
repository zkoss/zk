/* RunByTagsSuite.java

	Purpose:

	Description:

	History:
		Tue Dec 08 12:43:41 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.suite;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run test cases by tags (union/or).
 * Use {@code -Dtags=aaa,bbb,ccc} to specify tags.
 *
 * <p>Example:</p>
 * <code>mvn test -Dtest=RunByTagsSuite -Dtags="aaa,b b,ccc"</code>
 *
 * @author rudyhuang
 */
@ExtendWith(ReflectiveSuiteExtension.class)
public class RunByTagsSuite {
	private static final Logger LOG = LoggerFactory.getLogger(RunByTagsSuite.class);
	private static final Class<?>[] EMPTY = {};

	protected static Class<?>[] suite() {
		final String tagExpr = System.getProperty("tags", "");
		LOG.info("Use tags: {}", tagExpr);
		if (tagExpr.isEmpty()) {
			LOG.error("Tag is empty.");
			return EMPTY;
		}

		final Set<String> tags = newSetFromCommaString(tagExpr);
		try {
			Class[] classes = parseProperties().entrySet().parallelStream()
					.filter(e -> RunByTagsSuite.filterByTags(e, tags))
					.map(e -> RunByTagsSuite.toTestClass(e.getKey()))
					.filter(Objects::nonNull).toArray(Class[]::new);
			LOG.info("Test cases: {}", classes.length);
			return classes;
		} catch (Throwable e) {
			LOG.error("", e);
			return EMPTY;
		}
	}

	@Test
	public void dummy() {}

	private static Map<String, String> parseProperties() throws URISyntaxException, IOException {
		final URL config = RunByTagsSuite.class.getResource("/test2/config.properties");
		return Files.lines(Paths.get(config.toURI()))
				.filter(line -> line.startsWith("##zats##"))
				.map(line -> line.split("="))
				.collect(Collectors.toMap(e -> e[0], e -> e[1]));
	}

	private static Set<String> newSetFromCommaString(String commaString) {
		return new HashSet<>(Arrays.asList(commaString.toLowerCase().split(",")));
	}

	private static boolean filterByTags(Map.Entry<String, String> entry, Set<String> tags) {
		return !Collections.disjoint(newSetFromCommaString(entry.getValue()), tags);
	}

	private static Class<?> toTestClass(String zulName) {
		try {
			return Class.forName("org.zkoss.zktest.zats.test2." + convertToTestName(zulName));
		} catch (ClassNotFoundException e) {
			LOG.warn("Can't find the test class: {}", zulName);
			return null;
		}
	}

	private static String convertToTestName(String zulName) {
		return zulName
				.substring(8, zulName.indexOf(".z"))
				.replace('-', '_') + "Test";
	}
}
