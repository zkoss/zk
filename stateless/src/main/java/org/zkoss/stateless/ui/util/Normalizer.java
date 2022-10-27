/* Normalizer.java

	Purpose:
		
	Description:
		
	History:
		4:38 PM 2022/1/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.util;

/**
 * This class provides the method normalize which transforms CSS selector into
 * ZK widget selector.
 * @author jumperchen
 */
public final class Normalizer {

	/**
	 * Converts a CSS selector into ZK widget selector.
	 * i.e. converts from {@code #btn} to {@code $btn} and from {@code button} to
	 * {@literal @}{@code button}.
	 * @param selector The CSS selector
	 * @return a ZK widget selector.
	 */
	public static String normalize(String selector) {
		return selector.replace('#', '$').replaceAll("(^[a-zA-Z][^ ]*)", "@$1")
				.replaceAll(" ([a-zA-Z][^ ]*)", " @$1");
	}
}
