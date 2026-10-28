/* B102_ZK_5616Test.java

        Purpose:
                
        Description:
                
        History:
                Fri Apr 18 19:28:23 CST 2025, Created by cherrylee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B102_ZK_5616Test extends WebDriverTestCase {

    @Test
    public void testDetailNotOverlappedAfterScroll() {
        connect("/test2/B102-ZK-5616.zul");
        int columnWidth = jq(".z-listheader").outerWidth();

        jq(".z-frozen-inner").scrollLeft(8 * columnWidth + 120);
        waitResponse();

        long detailLeft = Math.round(Double.parseDouble(getEval("$('.z-row:first-child .z-detail-outer')[0].getBoundingClientRect().left")));
        long detailTop = Math.round(Double.parseDouble(getEval("$('.z-row:first-child .z-detail-outer')[0].getBoundingClientRect().top")));

        // The detail cell may render its content in a child element, so check the hit lands inside the cell.
        String hitInDetail = getEval("document.elementFromPoint(" + detailLeft + ", " + detailTop + ")"
                + ".closest('.z-detail-outer') === $('.z-row:first-child .z-detail-outer')[0]");
        Assertions.assertEquals("true", hitInDetail);
        // A transparent detail cell still wins the hit test but lets the scrolled cells show through it.
        String detailBackground = jq(".z-row:first-child .z-detail-outer").css("background-color");
        Assertions.assertTrue(detailBackground.startsWith("rgb("), "detail cell must be opaque: " + detailBackground);
    }
}
