/* F102_ZK_5921Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Apr 14 17:07:08 CST 2025, Created by jamson

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class F102_ZK_5921Test extends WebDriverTestCase {
    
    @Test
    public void test() {
        connect();
        
        // listbox
        Assertions.assertNotEquals(
                jq(".z-listbox-header").css("background-color"), // listbox header background color (whole header)
                jq(".z-listheader").css("background-color") // listheader background color
        );
        Assertions.assertNotEquals(
                jq(".z-listbox").css("background-color"), // listbox background color
                jq(".z-listcell").css("background-color") // listcell background color
        );
        Assertions.assertNotEquals(
                jq(".z-listbox-footer").css("background-color"), // listbox footer background color (whole footer)
                jq(".z-listfooter").css("background-color") // listfooter background color
        );
        Assertions.assertEquals("none", jq(".z-listbox-footer").css("border-top").split(" ")[1]);  // listbox footer border-top style element changed
        Assertions.assertNotEquals("none", jq(".z-listfooter").css("border-top").split(" ")[1]);
        
        // grid
        Assertions.assertNotEquals(
                jq(".z-grid-header").css("background-color"), // grid header background color (whole header)
                jq(".z-column").css("background-color") // column background color
        );
        Assertions.assertNotEquals(
                jq(".z-grid").css("background-color"), // grid background color
                jq(".z-cell").css("background-color") // cell background color
        );
        Assertions.assertNotEquals(
                jq(".z-grid-footer").css("background-color"), // grid footer background color (whole footer)
                jq(".z-footer").css("background-color") // footer background color
        );
        Assertions.assertEquals("none", jq(".z-grid-footer").css("border-top").split(" ")[1]);  // grid footer border-top style element changed
        Assertions.assertNotEquals("none", jq(".z-footer").css("border-top").split(" ")[1]);
        
        // tree
        Assertions.assertNotEquals(
                jq(".z-tree-header").css("background-color"), // tree header background color (whole header)
                jq(".z-treecol").css("background-color") // treecol background color
        );
        Assertions.assertNotEquals(
                jq(".z-tree").css("background-color"), // tree background color
                jq(".z-treecell").css("background-color") // treecell background color
        );
        Assertions.assertNotEquals(
                jq(".z-tree-footer").css("background-color"), // tree footer background color (whole footer)
                jq(".z-treefooter").css("background-color") // treefooter background color
        );
        Assertions.assertEquals("none", jq(".z-tree-footer").css("border-top").split(" ")[1]); // tree footer border-top style element changed
        Assertions.assertNotEquals("none", jq(".z-treefooter").css("border-top").split(" ")[1]);
    }
}
