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
	@Test
	public void test() {
		// getContentType shall return the type correctly
		Assertions.assertEquals("image/svg+xml", ContentTypes.getContentType("svg"));
		Assertions.assertEquals("font/ttf", ContentTypes.getContentType("ttf"));
		Assertions.assertEquals("font/otf", ContentTypes.getContentType("otf"));
		Assertions.assertEquals("font/woff", ContentTypes.getContentType("woff"));
		Assertions.assertEquals("font/woff2", ContentTypes.getContentType("woff2"));
		Assertions.assertEquals("application/vnd.ms-fontobject", ContentTypes.getContentType("eot"));
		Assertions.assertEquals("font/sfnt", ContentTypes.getContentType("sfnt"));
	}
}
