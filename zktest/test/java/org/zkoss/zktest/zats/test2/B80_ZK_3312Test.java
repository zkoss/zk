/* B80_ZK_3312Test.java

	Purpose:

	Description:

	History:
		Thu Jan 12 17:00:31 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import junit.framework.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author jameschu
 */
public class B80_ZK_3312Test extends ZATSTestCase{
    @Test
    public void test(){
		DesktopAgent desktop = connect();
		ComponentAgent info_origin = desktop.query("#info_origin");
		ComponentAgent info_selList = desktop.query("#info_selList");
		ComponentAgent info_selSet = desktop.query("#info_selSet");
		List<ComponentAgent> btns = desktop.queryAll("button");
		Label label_origin1 = info_origin.getChild(1).as(Label.class);
		Label label_origin2 = info_origin.getChild(2).as(Label.class);
		Label label_list1 = info_selList.getChild(1).as(Label.class);
		Label label_list2 = info_selList.getChild(2).as(Label.class);
		Label label_list3 = info_selList.getChild(3).getChild(1).as(Label.class);
		Label label_set1 = info_selSet.getChild(1).as(Label.class);
		Label label_set2 = info_selSet.getChild(2).as(Label.class);
		Label label_set3 = info_selSet.getChild(3).getChild(1).as(Label.class);
		assertEquals("[a, b, c]", label_origin1.getValue().trim());
		assertEquals("3", label_origin2.getValue().trim());
		btns.get(0).click();
		assertEquals("[a]", label_list1.getValue().trim());
		assertEquals("1", label_list2.getValue().trim());
		assertEquals("false", label_list3.getValue().trim());
		assertEquals("[a]", label_set1.getValue().trim());
		assertEquals("1", label_set2.getValue().trim());
		assertEquals("false", label_set3.getValue().trim());
		btns.get(1).click();
		assertEquals("[a, b]", label_list1.getValue().trim());
		assertEquals("2", label_list2.getValue().trim());
		assertEquals("false", label_list3.getValue().trim());
		assertEquals("[a, b]", label_set1.getValue().trim());
		assertEquals("2", label_set2.getValue().trim());
		assertEquals("false", label_set3.getValue().trim());
		btns.get(2).click();
		assertEquals("[a, b, c]", label_list1.getValue().trim());
		assertEquals("3", label_list2.getValue().trim());
		assertEquals("true", label_list3.getValue().trim());
		assertEquals("[a, b, c]", label_set1.getValue().trim());
		assertEquals("3", label_set2.getValue().trim());
		assertEquals("false", label_set3.getValue().trim());


		//another test
		ComponentAgent lb = desktop.query("listbox");
		assertEquals(3, lb.getChildren().size());
		btns.get(3).click();
		btns.get(4).click();
		assertEquals("[b, c]", label_origin1.getValue().trim());
		assertEquals("2", label_origin2.getValue().trim());
		assertEquals(2, lb.getChildren().size());
    }
}
