/* B96_ZK_5058Test.java

	Purpose:
		
	Description:
		
	History:
		3:08 PM 2021/11/19, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class B96_ZK_5058Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		Iterator<JQuery> iterator = jq(".z-groupbox .z-caption").iterator();
		while (iterator.hasNext()) {
			JQuery next = iterator.next();
			assertNotEquals("0px", next.css("top"));
		}
	}

}