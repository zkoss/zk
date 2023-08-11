/* F100_ZK_5503Test.java

        Purpose:
                
        Description:
                
        History:
                Wed Jul 26 14:32:38 CST 2023, Created by jamsonchan

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F100_ZK_5503Test  extends WebDriverTestCase {
    public String[] names = new String[] {"a", "auxheader", "column", "footer", "button", "caption", "checkbox", "combobutton", "comboitem", "fileupload", "listheader", "listcell", "listfooter", "menu", "menuitem", "nav", "navitem", "orgnode", "radio", "tab", "toolbarbutton", "treecol", "treecell", "treefooter"};
    @Test
    public void test() {
        connect();

        click(jq("@button:contains(test1)"));
        waitResponse();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName +" > .z-icon-plus").attr("title"), "1");
            assertEquals(jq("@" + widgetName +" > .z-icon-minus").attr("title"), "1");
        }

        click(jq("@button:contains(test2)"));
        waitResponse();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName +" > .z-icon-plus").attr("title"), "2");
            assertEquals(jq("@" + widgetName + " > .z-icon-minus").attr("title"), "null");
        }

        click(jq("@button:contains(test3)"));
        waitResponse();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName +" > .z-icon-plus").attr("title"), "3");
            assertEquals(jq("@" + widgetName + " > .z-icon-minus").attr("title"), "null");
        }

        click(jq("@button:contains(test4)"));
        waitResponse();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName + " > .z-icon-plus").attr("title"), "null");
            assertEquals(jq("@" + widgetName + " > .z-icon-minus").attr("title"), "null");
        }

        click(jq("@button:contains(test5)"));
        waitResponse();
        for (String widgetName : names) {
            if (widgetName.equals("comboitem")) {
                click(jq(".z-combobox-icon.z-icon-caret-down"));
            }
            assertEquals(jq("@" + widgetName + " > .z-icon-plus").attr("title"), "null");
            assertEquals(jq("@" + widgetName + " > .z-icon-minus").attr("title"), "null");
        }
    }
}
