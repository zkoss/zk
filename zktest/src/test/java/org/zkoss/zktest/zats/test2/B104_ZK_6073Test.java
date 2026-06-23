/* B104_ZK_6073Test.java

        Purpose:
                
        Description:
                
        History:
                Wed May 06 20:22:03 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * Verifies ZK-6073: a combobox with {@code instantSelect="false"} must only
 * fire {@code onChanging} events with {@code isChangingBySelectBack=true} while
 * the user navigates the popup with arrow keys. Before the fix, {@code _hilite2}
 * skipped updating {@code valueEnter_} on arrow nav (because {@code sendOnSelect}
 * is false), and a stale {@code valueEnter_} could differ from {@code valueSel_}
 * when {@code _onChanging} fired, dropping the bySelectBack flag.
 *
 * @author peakerlee
 */
public class B104_ZK_6073Test extends WebDriverTestCase {

	@Test
	public void testArrowNavOnlyFiresSelectBackOnChanging() {
		connect();
		waitResponse();

		// Open the popup so it has children rendered, then type a character to
		// trigger server-side filtering (the composer rebuilds the model).
		click(jq("$cb .z-combobox-button"));
		waitResponse(true);
		click(jq("$cb input"));
		sendKeys(jq("$cb input"), "A");
		waitResponse();
		waitResponse(true);
		// Filter onChanging fired during typing should have bySelectBack=false
		// (intentional). Capture baselines to measure only arrow-nav events.
		int baselineNonSelectBack = readNonSelectBackCount();
		int baselineTotal = readTotalCount();

		// Simulate the race deterministically by directly exercising the
		// keydown-only code path (which does not set valueEnter_ in _hilite2
		// before the fix), then forcing _onChanging to evaluate.
		String script
				= "(function(){"
				+ "var w=zk.Widget.$('$cb');"
				+ "if(!w)return 'no widget';"
				+ "var first=w.firstChild;"
				+ "var second=first&&first.nextSibling;"
				+ "if(!first||!second)return 'no items;n='+w.nChildren+' open='+w._open;"
				+ "for (var i=0;i<20;i++){"
				+ "  var item=i%2==0?first:second;"
				// Pretend a previous typeahead/_hilite left valueEnter_ stale.
				+ "  w.valueEnter_='STALE_'+i;"
				// Mimic what dnPressed_->_updnSel does for instantSelect=false:
				// it calls _select(sel, {}) (highlightOnly=true), which sets
				// inp.value, valueSel_ and calls _hilite2(sel, {}).
				+ "  w._select(item,{});"
				// Force the debounced onChanging to evaluate immediately.
				+ "  zul.inp.InputWidget._onChanging.call(w,-1);"
				+ "}"
				+ "return 'ok';"
				+ "})()";
		String result = getEval(script);
		waitResponse();
		// Sanity: ensure the popup actually had items to navigate.
		org.junit.jupiter.api.Assertions.assertEquals("ok", result,
				"combobox should have at least two filtered items, got: " + result);

		int nonSelectBack = readNonSelectBackCount() - baselineNonSelectBack;
		int total = readTotalCount() - baselineTotal;
		assertEquals(0, nonSelectBack,
				"Arrow-key navigation must only fire onChanging with"
						+ " isChangingBySelectBack=true. Total events during nav="
						+ total);
	}

	private int readNonSelectBackCount() {
		return Integer.parseInt(jq("$nonSelectBackCount").text().trim());
	}

	private int readTotalCount() {
		return Integer.parseInt(jq("$totalCount").text().trim());
	}
}
