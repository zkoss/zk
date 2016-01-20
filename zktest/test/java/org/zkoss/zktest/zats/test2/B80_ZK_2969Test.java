/* B80_ZK_2969Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Jan 19, 2016  3:45:28 PM, Created by Christopher

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Iterator;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

import junit.framework.Assert;

/**
 * 
 * @author Christopher
 */
public class B80_ZK_2969Test extends WebDriverTestCase {
	
	@Test
	public void testNonPaging() {
		connect();
		// check to select all and compare messages
		click(jq(".z-listheader-checkable").eq(0));
		waitResponse(true);
		String msg = getZKLog();
		String onSelectMsg = msg.substring(msg.indexOf(":") + 1, msg.indexOf("onCheckSelectAll:")).trim();
		String onCheckSelectAllMsg = msg.substring(msg.lastIndexOf(":") + 1).trim();
		Assert.assertEquals(onSelectMsg, onCheckSelectAllMsg);
		
		// clears the message
		closeZKLog();
		
		// check again to un-select all and compare messages
		click(jq(".z-listheader-checkable").eq(0));
		waitResponse(true);
		msg = getZKLog();
		onSelectMsg = msg.substring(msg.indexOf(":") + 1, msg.indexOf("onCheckSelectAll:")).trim();
		onCheckSelectAllMsg = msg.substring(msg.lastIndexOf(":") + 1).trim();
		Assert.assertEquals(onSelectMsg, onCheckSelectAllMsg);
		
		// check to select all and un-select item 2, then compare selected items from both listbox
		click(jq(".z-listheader-checkable").eq(0));
		click(jq(".z-listbox").eq(0).find(".z-listitem-checkable").eq(1));
		waitResponse(true);
		Iterator<JQuery> iterNonPaging = jq(".z-listbox").eq(0).find(".z-listitem-selected").iterator();
		Iterator<JQuery> iterPaging = jq(".z-listbox").eq(1).find(".z-listitem-selected").iterator();
		for (JQuery np = iterNonPaging.next() ; iterNonPaging.hasNext(); np = iterNonPaging.next()) {
			if (!iterPaging.hasNext()) {
				click(jq(".z-paging-button.z-paging-next"));
				waitResponse(true);
				iterPaging = jq(".z-listbox").eq(1).find(".z-listitem-selected").iterator();
			}
			JQuery p = iterPaging.next();
			Assert.assertEquals(np.text(), p.text());
		}
	}
	
	@Test
	public void testPaging() {
		connect();
		
		// check to select all and compare messages
		click(jq(".z-listheader-checkable").eq(1));
		waitResponse(true);
		String msg = getZKLog();
		String onSelectMsg = msg.substring(msg.indexOf(":") + 1, msg.indexOf("onCheckSelectAll:")).trim();
		String onCheckSelectAllMsg = msg.substring(msg.lastIndexOf(":") + 1).trim();
		Assert.assertEquals(onSelectMsg, onCheckSelectAllMsg);
		
		// clears the message
		closeZKLog();
		
		// check again to un-select all and compare messages
		click(jq(".z-listheader-checkable").eq(1));
		waitResponse(true);
		msg = getZKLog();
		onSelectMsg = msg.substring(msg.indexOf(":") + 1, msg.indexOf("onCheckSelectAll:")).trim();
		onCheckSelectAllMsg = msg.substring(msg.lastIndexOf(":") + 1).trim();
		Assert.assertEquals(onSelectMsg, onCheckSelectAllMsg);
		
		// check to select all and un-select item 2, then compare selected items from both listbox
		click(jq(".z-listheader-checkable").eq(1));
		click(jq(".z-listbox").eq(1).find(".z-listitem-checkable").eq(1));
		waitResponse(true);
		Iterator<JQuery> iterNonPaging = jq(".z-listbox").eq(0).find(".z-listitem-selected").iterator();
		Iterator<JQuery> iterPaging = jq(".z-listbox").eq(1).find(".z-listitem-selected").iterator();
		for (JQuery np = iterNonPaging.next() ; iterNonPaging.hasNext(); np = iterNonPaging.next()) {
			if (!iterPaging.hasNext()) {
				click(jq(".z-paging-button.z-paging-next"));
				waitResponse(true);
				iterPaging = jq(".z-listbox").eq(1).find(".z-listitem-selected").iterator();
			}
			JQuery p = iterPaging.next();
			Assert.assertEquals(np.text(), p.text());
		}
	}
}
