/* IFileuploadRichletTest.java

	Purpose:

	Description:

	History:
		Tue Apr 19 18:16:56 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.docs.essential_components;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.sul.IFileupload;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * A set of unit test for {@link IFileupload} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Fileupload">Fileupload</a>,
 * if any.
 *
 * @author katherine
 * @see IFileupload
 */
public class IFileuploadRichletTest extends WebDriverTestCase {
	@Test
	public void fileupload() {
		connect("/essential_components/iFileupload/fileupload");
		assertTrue(jq(".z-upload").exists());
		assertTrue(jq(".z-upload").prev().hasClass("z-button"));
	}
}