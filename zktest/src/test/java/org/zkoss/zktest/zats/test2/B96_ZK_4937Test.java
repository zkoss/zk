/* B96_ZK_4937Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 25 14:56:47 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.util.media.ContentTypes;

public class B96_ZK_4937Test {
	private String trimSemicolon(String str) {
		if (str.endsWith(";")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	@Test
	public void test() {
		// getContentType shall return the type correctly
		Assertions.assertEquals("image/svg+xml", trimSemicolon(ContentTypes.getContentType("svg")));
		Assertions.assertEquals("font/ttf", trimSemicolon(ContentTypes.getContentType("ttf")));
		Assertions.assertEquals("font/otf", trimSemicolon(ContentTypes.getContentType("otf")));
		Assertions.assertEquals("font/woff", trimSemicolon(ContentTypes.getContentType("woff")));
		Assertions.assertEquals("font/woff2", trimSemicolon(ContentTypes.getContentType("woff2")));
		Assertions.assertEquals("application/vnd.ms-fontobject", trimSemicolon(ContentTypes.getContentType("eot")));
		Assertions.assertEquals("font/sfnt", trimSemicolon(ContentTypes.getContentType("sfnt")));
	}
}
