/* B96_ZK_4937Test.java

		Purpose:
		
		Description:
		
		History:
				Fri Jun 25 14:56:47 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.util.media.ContentTypes;

public class B96_ZK_4937Test {
	@Test
	public void test() {
		// getContentType shall return the type correctly
		Assert.assertEquals("image/svg+xml", ContentTypes.getContentType("svg"));
		Assert.assertEquals("font/ttf", ContentTypes.getContentType("ttf"));
		Assert.assertEquals("font/otf", ContentTypes.getContentType("otf"));
		Assert.assertEquals("font/woff", ContentTypes.getContentType("woff"));
		Assert.assertEquals("font/woff2", ContentTypes.getContentType("woff2"));
		Assert.assertEquals("application/vnd.ms-fontobject", ContentTypes.getContentType("eot"));
		Assert.assertEquals("font/sfnt", ContentTypes.getContentType("sfnt"));
	}
}
