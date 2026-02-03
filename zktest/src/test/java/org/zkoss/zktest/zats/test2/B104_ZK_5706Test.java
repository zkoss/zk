package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B104_ZK_5706Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        waitResponse();

        JQuery container = jq(".z-pdfviewer-container");

        int retries = 0;
        while (!container.find("canvas").exists() && retries < 20) {
            sleep(500);
            retries++;
        }

        eval("document.body.style.zoom = '1.1'");
        waitResponse();

        click(container);
        waitResponse();

        eval("jq('.z-pdfviewer-container')[0].scrollTop = jq('.z-pdfviewer-container')[0].scrollHeight");
        waitResponse();
        sleep(500);

        eval("jq('.z-pdfviewer-container').trigger({ type: 'mousewheel', deltaY: -100 }, [-100])");
        waitResponse();

        String pageNum = jq(".z-pdfviewer-toolbar-page-active").val();
        assertEquals("2", pageNum);
    }
}
