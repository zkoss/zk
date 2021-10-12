package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;

import static org.junit.Assert.fail;

/**
 * Created by wenning on 6/17/16.
 */
public class F80_ZK_3144Test extends ZATSTestCase {

    @Test
    public void testWidth2FlexViaNull() {
        try {
            Div div = new Div();
            div.setWidth("200px");
            Assert.assertEquals("200px", div.getWidth());
            div.setWidth(null);
            div.setHflex("1");
            div.setWidth(null);
            div.setHflex("1");
            Assert.assertEquals("1", div.getHflex());
        } catch (UiException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test(expected = UiException.class)
    public void testWidth2FlexFail() {
        Div div = new Div();
        div.setWidth("200px");
        div.setHflex("1");
    }

}
