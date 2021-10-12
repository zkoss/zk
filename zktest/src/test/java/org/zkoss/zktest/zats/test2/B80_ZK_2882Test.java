package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class B80_ZK_2882Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		//click the 1st combobox button in the 1st grid
		click(jq(".z-grid:eq(0) .z-combobox-button").eq(0));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//click the 2nd combobox button in the 1st grid
		click(jq(".z-grid:eq(0) .z-combobox-button").eq(1));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//click the 3rd combobox button in the 1st grid
		click(jq(".z-grid:eq(0) .z-combobox-button").eq(2));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//check the all input should be 222
		JQuery inputs = jq("input");
		int index = 0;
		while (index < 12) {
			assertEquals("222", inputs.eq(index).val());
			index += 1;
		}

		//click the 1st combobox button in the 2nd grid
		click(jq(".z-grid:eq(1) .z-combobox-button").eq(0));
		waitResponse();
		//select the 1st item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(0));
		waitResponse();
		//click the 2nd combobox button in the 1st grid
		click(jq(".z-grid:eq(1) .z-combobox-button").eq(1));
		waitResponse();
		//select the 1st item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(0));
		waitResponse();
		//click the 3rd combobox button in the 1st grid
		click(jq(".z-grid:eq(1) .z-combobox-button").eq(2));
		waitResponse();
		//select the 1st item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(0));
		waitResponse();
		//check the all input should be 111
		index = 0;
		while (index < 12) {
			assertEquals("111", inputs.eq(index).val());
			index += 1;
		}

		//click the 1st combobox button in the 3rd grid
		click(jq(".z-grid:eq(2) .z-combobox-button").eq(0));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//click the 2nd combobox button in the 3rd grid
		click(jq(".z-grid:eq(2) .z-combobox-button").eq(1));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//click the 3rd combobox button in the 3rd grid
		click(jq(".z-grid:eq(2) .z-combobox-button").eq(2));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(1));
		waitResponse();
		//check the all input should be 222
		index = 0;
		while (index < 12) {
			assertEquals("222", inputs.eq(index).val());
			index += 1;
		}

		//click the 1st combobox button in the 4th grid
		click(jq(".z-grid:eq(3) .z-combobox-button").eq(0));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(2));
		waitResponse();
		//click the 2nd combobox button in the 4th grid
		click(jq(".z-grid:eq(3) .z-combobox-button").eq(1));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(2));
		waitResponse();
		//click the 3rd combobox button in the 4th grid
		click(jq(".z-grid:eq(3) .z-combobox-button").eq(2));
		waitResponse();
		//select the 2nd item
		click(jq(".z-combobox-popup.z-combobox-open .z-comboitem").eq(2));
		waitResponse();
		//check the all input should be 333
		index = 0;
		while (index < 12) {
			assertEquals("333", inputs.eq(index).val());
			index += 1;
		}
		//check there are no errors
		assertFalse(hasError());
	}
}