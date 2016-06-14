/* F80_ZK_2707Test.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun  4 14:00:31 CST 2016, Created by Chris

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 *
 * @author Chris
 */
public class F80_ZK_2707Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		checkEquals(); //check init values are the same
		
		click(jq(".z-timebox-icon.z-timebox-up")); //change the value in the timebox
		waitResponse(true);
		click(jq(".z-label")); //click on whatever to blur and force onChange on the timebox
		waitResponse(true);
		
		checkEquals(); //check the values are still the same after changing the value in timebox
	}
	
	private void checkEquals() {
		int timeboxVal = Integer.valueOf(jq(".z-timebox > input").val().substring(0, 2));
		JQuery initTimepicker = jq(".z-timepicker > input");
		int tp1 = Integer.valueOf(initTimepicker.eq(0).val().substring(0, 2));
		assertTrue("Expecting: " + timeboxVal + " in timepicker #1, got: " + tp1, timeboxVal == tp1);
		int tp2 = Integer.valueOf(initTimepicker.eq(1).val().substring(0, 2));
		assertTrue("Expecting: " + timeboxVal + " in timepicker #2, got: " + tp2, timeboxVal == tp2);
		int tp3 = Integer.valueOf(initTimepicker.eq(2).val().substring(0, 2));
		assertTrue("Expecting: " + timeboxVal + " in timepicker #3, got: " + tp3, timeboxVal == tp3);
		int tp4 = Integer.valueOf(initTimepicker.eq(3).val().substring(0, 2));
		assertTrue("Expecting: " + timeboxVal + " in timepicker #4, got: " + tp4, timeboxVal == tp4);
		int tp5 = Integer.valueOf(initTimepicker.eq(4).val().substring(0, 2));
		assertTrue("Expecting: " + timeboxVal + " in timepicker #5, got: " + tp5, (timeboxVal > 12 || timeboxVal == 0 ? timeboxVal -= 12 : timeboxVal) == tp5);
	}
	
	@Test
	public void testApi() {
		connect();
		// check init options in the dropdown list
		click(jq(".z-timepicker-button").eq(2));
		waitResponse(true);
		assertEquals(2, jq(".z-timepicker-popup.z-timepicker-open > .z-timepicker-content").children().length());
		click(jq(".z-button:contains(\"min\")"));
		waitResponse(true);
		// check dropdown list options after triggering event
		click(jq(".z-timepicker-button").eq(2));
		waitResponse(true);
		assertEquals(1, jq(".z-timepicker-popup.z-timepicker-open > .z-timepicker-content").children().length());
		click(jq(".z-button:contains(\"max\")"));
		waitResponse(true);
		// check dropdown list options after triggering event
		click(jq(".z-timepicker-button").eq(2));
		waitResponse(true);
		assertEquals(2, jq(".z-timepicker-popup.z-timepicker-open > .z-timepicker-content").children().length());
		click(jq(".z-button:contains(\"interval\")"));
		waitResponse(true);
		// check dropdown list options after triggering event
		click(jq(".z-timepicker-button").eq(2));
		waitResponse(true);
		assertEquals(120, jq(".z-timepicker-popup.z-timepicker-open > .z-timepicker-content").children().length());
	}
	
	@Test
	public void testIndependence() {
		connect();
		//cache the init value
		String tp1 = jq(".z-timepicker > input").eq(0).val();
		String tp2 = jq(".z-timepicker > input").eq(1).val();
		String tp3 = jq(".z-timepicker > input").eq(2).val();
		String tp4 = jq(".z-timepicker > input").eq(3).val();
		String tp5 = jq(".z-timepicker > input").eq(4).val();
		// change the datebox value
		click(jq(".z-datebox-button"));
		waitResponse(true);
		if(jq(".z-calendar-selected").prev().exists()) {
			click(jq(".z-calendar-selected").prev());
		} else {
			click(jq(".z-calendar-selected").next());
		}
		waitResponse(true);
		//check the value after datebox changes date is still the same as the previously cached value
		assertEquals(tp1, jq(".z-timepicker > input").eq(0).val());
		assertEquals(tp2, jq(".z-timepicker > input").eq(1).val());
		assertEquals(tp3, jq(".z-timepicker > input").eq(2).val());
		assertEquals(tp4, jq(".z-timepicker > input").eq(3).val());
		assertEquals(tp5, jq(".z-timepicker > input").eq(4).val());
	}
}
