/* F100_ZK_5502Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jul 26 14:32:09 CST 2023, Created by jamsonchan

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F100_ZK_5502Test extends WebDriverTestCase {
    @Test
    public void test() {
        String[] names = new String[] {"a", "auxheader", "column", "footer", "button", "caption", "checkbox", "combobutton", "comboitem", "fileupload", "listheader", "listcell", "listfooter", "menu", "menuitem", "nav", "navitem", "orgnode", "radio", "tab", "toolbarbutton", "treecol", "treecell", "treefooter"};
        connect();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName +" > .z-icon-home").attr("title"), widgetName);
        }
    }
}
