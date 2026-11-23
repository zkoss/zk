/* B80_ZK_3519Test.java

	Purpose:

	Description:

	History:
		Wed Jan 11 15:11:40 CST 2017, Created by jameschu

Copyright (C)  Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.io.*;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

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