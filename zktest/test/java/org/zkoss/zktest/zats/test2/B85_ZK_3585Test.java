/* B85_ZK_3585Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 13 19:04:30 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Textbox;

/**
 * @author rudyhuang
 */
public class B85_ZK_3585Test extends ZATSTestCase {
    @Test
    public void test() throws Exception {
        DesktopAgent desktop = connect();
        Textbox city = desktop.queryAll("textbox").get(2).as(Textbox.class);
        String cityValue = city.getValue();
        desktop.queryAll("button").get(0).click();
        Assert.assertNotEquals(cityValue, city.getValue());

        Hbox collectionHbox = desktop.queryAll("hbox").get(5).as(Hbox.class);
        int numOfCollectionLabel = collectionHbox.getLastChild().getChildren().size();
        desktop.queryAll("button").get(3).click();
        Assert.assertEquals(numOfCollectionLabel + 1, collectionHbox.getLastChild().getChildren().size());

        Hbox mapHbox = desktop.queryAll("hbox").get(6).as(Hbox.class);
        int numOfMapDiv = mapHbox.getLastChild().getChildren().size();
        desktop.queryAll("button").get(4).click();
        Assert.assertEquals(numOfMapDiv + 1, mapHbox.getLastChild().getChildren().size());

        Textbox childName = desktop.queryAll("textbox").get(3).as(Textbox.class);
        String name = childName.getValue();
        desktop.queryAll("button").get(5).click();
        Assert.assertNotEquals(name, childName.getValue());
    }
}
