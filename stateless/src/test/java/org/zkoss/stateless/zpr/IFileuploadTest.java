/* IFileuploadTest.java

	Purpose:

	Description:

	History:
		Tue Oct 28 15:24:39 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Fileupload;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link IFileupload}
 *
 * @author katherine
 */
public class IFileuploadTest extends StatelessTestBase {
	@Test
	public void withFileupload() {
		// check Richlet API case
		assertEquals(richlet(() -> IFileupload.of("abc")), zul(IFileuploadTest::newFileupload));

		// check Stateless file case
		assertEquals(composer(IFileuploadTest::newFileupload), zul(IFileuploadTest::newFileupload));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Fileupload Fileupload = new Fileupload("123");
					return Fileupload;
				}, (IFileupload iFileupload) -> iFileupload.withLabel("abc")),
				zul(IFileuploadTest::newFileupload));
	}

	private static Fileupload newFileupload() {
		Fileupload Fileupload = new Fileupload("abc");
		return Fileupload;
	}
}