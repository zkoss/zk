package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;
import org.zkoss.test.webdriver.ztl.Widget;

/**
 * 
 * @author Sefi
 */
public class B80_ZK_3169Test extends WebDriverTestCase {
    @Test
    public void test() {
        connect();
        Widget widget = widget(jq("@chosenbox"));
        sendKeys(widget.$n("inp"), "i");
        JQuery options = jq(".z-chosenbox-option");
        waitResponse();

        JQuery element = options.eq(1);
        String text = element.text();
        click(element);
        waitResponse();
        JQuery chosenItem = jq(".z-chosenbox-item");
        assertEquals(1, chosenItem.length());
        assertEquals(text, chosenItem.eq(0).text());

        JQuery listItem = jq(".z-listitem");
        assertEquals(1, listItem.length());
        assertEquals(text, listItem.eq(0).text());

        click(jq("@button"));
        waitResponse();

        assertEquals(1, chosenItem.length());
        assertEquals(text, chosenItem.eq(0).text());
        assertEquals(1, listItem.length());
        assertEquals(text, listItem.eq(0).text());
    }
}
