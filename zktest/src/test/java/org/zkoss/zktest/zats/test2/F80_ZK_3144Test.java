package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;

/**
 * Created by wenning on 6/17/16.
 */
public class F80_ZK_3144Test extends ZATSTestCase {

    @Test
    public void testWidth2FlexViaNull() {
        try {
            Div div = new Div();
            div.setWidth("200px");
            Assertions.assertEquals("200px", div.getWidth());
            div.setWidth(null);
            div.setHflex("1");
            div.setWidth(null);
            div.setHflex("1");
            Assertions.assertEquals("1", div.getHflex());
        } catch (UiException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testWidth2FlexFail() {
		Assertions.assertThrows(UiException.class, () -> {
			Div div = new Div();
			div.setWidth("200px");
			div.setHflex("1");
		});
    }

}
