/* B80_ZK_2758Test.java

	Purpose:
		
	Description:
		
	History:
		Fri May 29 12:22:25 CST 2015, Created by jumperchen

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.*;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zktest.zats.ZATSTestCase;
/**
 * 
 * @author jumperchen
 *
 */
public class B80_ZK_2758Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		ComponentAgent main = desktop.query("#main");
		ComponentAgent button = main.query("button");
		List<ComponentAgent> groups = main.queryAll(".form-group");
		for (ComponentAgent group : groups) {
			assertFalse(group.as(HtmlBasedComponent.class).getSclass().contains("has-error"));
		}
		button.click();
		for (ComponentAgent group : groups) {
			assertTrue(group.as(HtmlBasedComponent.class).getSclass().contains("has-error"));
		}
		button.click();
		for (ComponentAgent group : groups) {
			assertFalse(group.as(HtmlBasedComponent.class).getSclass().contains("has-error"));
		}
		
	}
	@Test
	public void test1() {
		test();
	}
	@Test
	public void test2() {
		test();
	}
	@Test
	public void test3() {
		test();
	}
	@Test
	public void test4() {
		test();
	}
}
