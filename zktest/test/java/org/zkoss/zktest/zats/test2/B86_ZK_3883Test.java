/* B86_ZK_3883Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Aug 31 10:49:17 CST 2018, Created by leon

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_3883Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        checkAlign();
        click(jq("$fireResize"));
        waitResponse();
        checkAlign();
    }

    public void checkAlign() {
        int treecolA = jq("$myTreeA .z-treecol").width();
        int treecellA = jq("$myTreeA .z-treecell").width();
        int treefooterA = jq("$myTreeA .z-treefooter").width();
        assertTrue((treecolA == treecellA && treecolA == treefooterA));

        int listheaderA = jq("$myListboxA .z-listheader").width();
        int listcellA = jq("$myListboxA .z-listcell").width();
        int listfooterA = jq("$myListboxA .z-listfooter").width();
        assertTrue((listheaderA == listcellA && listheaderA == listfooterA));

        int columnA = jq("$myGridA .z-column").width();
        int rowinnerA = jq("$myGridA .z-row-inner").width();
        int footerA = jq("$myGridA .z-footer").width();
        assertTrue((columnA == rowinnerA && columnA == footerA));

        int treecolB = jq("$myTreeB .z-treecol").width();
        int treecellB = jq("$myTreeB .z-treecell").width();
        int treefooterB = jq("$myTreeB .z-treefooter").width();
        assertTrue((treecolB == treecellB && treecolB == treefooterB));

        int listheaderB = jq("$myListboxB .z-listheader").width();
        int listcellB = jq("$myListboxB .z-listcell").width();
        int listfooterB = jq("$myListboxB .z-listfooter").width();
        assertTrue((listheaderB == listcellB && listheaderB == listfooterB));

        int columnB = jq("$myGridB .z-column").width();
        int rowinnerB = jq("$myGridB .z-row-inner").width();
        int footerB = jq("$myGridB .z-footer").width();
        assertTrue((columnB == rowinnerB && columnB == footerB));
    }
}
