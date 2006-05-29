/* ImageTest.java

{{IS_NOTE

	Purpose: Test MultiValues
	Description:
	History:
	 2001/4/12, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.image;

import junit.framework.*;
import java.util.*;
import java.io.*;

public class ImageTest extends TestCase {
	private static final String FILE1 = "/metainfo/com/potix/image/sample.gif";

	public ImageTest(String name) {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(ImageTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void testImage() throws Exception {
		final Image img = new AImage(
			ImageTest.class.getResourceAsStream(FILE1));
		assertEquals("gif", img.getFormat());
		assertEquals(new Integer(15), new Integer(img.getWidth()));
		assertEquals(new Integer(13), new Integer(img.getHeight()));
	}
}
