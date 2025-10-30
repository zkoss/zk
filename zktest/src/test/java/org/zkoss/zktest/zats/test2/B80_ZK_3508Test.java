/* B80_ZK_3519Test.java

	Purpose:

	Description:

	History:
		Wed Jan 11 15:11:40 CST 2017, Created by jameschu

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * 
 * @author jameschu
 */


public class B80_ZK_3508Test extends WebDriverTestCase {

	@Test
	public void test() throws IOException, ClassNotFoundException {
		connect();
		click(jq("@button").eq(0));
		waitResponse();
		System.out.println(jq("@window").eq(1).height());
		System.out.println(jq("@window").eq(1).html());
		assertNotSame("0px", jq("@window").eq(1).css("height"));
	}

	@Test
	public void test1() throws IOException, ClassNotFoundException {
		connect();
		click(jq("@button").eq(1));
		waitResponse();
		click(jq("@button").eq(2));
		waitResponse();
		assertNotSame(0, jq("@window").eq(1).height());
	}
}