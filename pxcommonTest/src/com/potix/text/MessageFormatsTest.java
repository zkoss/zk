/* MessageFormatsTest.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Apr  1 13:50:43     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2002 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.text;

import junit.framework.*;

/**
 * Test of {@link MessageFormats}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/05/29 04:27:26 $
 */
public class MessageFormatsTest extends TestCase {
	public MessageFormatsTest(String name) {
		super(name);
	}
	public static Test suite() {
		return new TestSuite(MessageFormatsTest.class);
	}
	protected void setUp() {
	}
	protected void tearDown() {
	}

	public void assertEquals
	(MessageFormats.NameInfo ni1, MessageFormats.NameInfo ni2) {
		assertEquals(ni1.pattern, ni2.pattern);
		assertEquals(ni1.names.length, ni2.names.length);
		for (int j = 0; j < ni1.names.length; ++j)
			assertEquals(ni1.names[j], ni2.names[j]);
	}
	public void testParseByName() throws Exception {
		final String[] patterns = {
			"{ab}, {c}, {de,number,#,##0}",
			"{ab,number}, {cde,date}, {ab,date}, '{cde}', {fg}, {cde,ok}, {xy}"
		};
		final MessageFormats.NameInfo nis[] = {
			new MessageFormats.NameInfo(
				"{0}, {1}, {2,number,#,##0}",
				new String[] {"ab", "c", "de"}),
			new MessageFormats.NameInfo(
				"{0,number}, {1,date}, {0,date}, '{cde}', {2}, {1,ok}, {3}",
				new String[] {"ab", "cde", "fg", "xy"}),
		};

		for (int j = 0; j < patterns.length;++j)
			assertEquals(nis[j],
				MessageFormats.parseByName(patterns[j]));
	}
}
