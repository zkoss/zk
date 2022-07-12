/* B96_ZK_4937Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 25 14:56:47 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;

import org.zkoss.util.media.ContentTypes;

public class B96_ZK_4937Test {
	@Test
	public void test() {
		// getContentType shall return the type correctly
		MatcherAssert.assertThat(ContentTypes.getContentType("svg"),
				new StringStartsWith(true, "image/svg+xml"));
		MatcherAssert.assertThat(ContentTypes.getContentType("ttf"),
				new StringStartsWith(true,  "font/ttf"));
		MatcherAssert.assertThat(ContentTypes.getContentType("otf"),
				new StringStartsWith(true, "font/otf"));
		MatcherAssert.assertThat(ContentTypes.getContentType("woff"),
				new StringStartsWith(true, "font/woff"));
		MatcherAssert.assertThat(ContentTypes.getContentType("woff2"),
				new StringStartsWith(true, "font/woff2"));
		MatcherAssert.assertThat(ContentTypes.getContentType("eot"),
				new StringStartsWith(true, "application/vnd.ms-fontobject"));
		MatcherAssert.assertThat(ContentTypes.getContentType("sfnt"),
				new StringStartsWith(true, "font/sfnt"));
	}
}
