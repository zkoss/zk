/** ListmodelTest.java

	Purpose:
		
	Description:
		
	History:
		11:40:00 AM Feb 2, 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm._foreach;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

/**
 * @author jameschu
 */
public class ListmodelTest extends ZutiBasicTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent list_init = desktop.query("#w #list #fli");
		ComponentAgent list_load = desktop.query("#w #list #fll");
		ComponentAgent array_1d = desktop.query("#w #array #fa1d");
		ComponentAgent array_2d = desktop.query("#w #array #fa2d");
		ComponentAgent map = desktop.query("#w #map_set #fm");
		ComponentAgent set = desktop.query("#w #map_set #fs");
		ComponentAgent btn_add = desktop.query("#w #add");
		ComponentAgent btn_update = desktop.query("#w #update");
		ComponentAgent btn_remove = desktop.query("#w #remove");
		ComponentAgent btn_change_model = desktop.query("#w #change_model");
		ComponentAgent btn_serialization = desktop.query("#serialization");
		
		/* initial test */
		assertEquals(list_init.getChildren().size(), 5 + 2);
		assertEquals(list_load.getChildren().size(), 5 + 2);
		assertEquals(array_1d.getChildren().size(), 3 + 2);
		assertEquals(array_2d.getChildren().size(), 3 + 2);
		assertEquals(map.getChildren().size(), 5 + 2);
		assertEquals(set.getChildren().size(), 5 + 2);
		
		checkList(desktop);
		checkArray(desktop);
		checkMap(desktop);
		checkSet(desktop);
		/* event - add */
		btn_add.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
		/* event - update */
		btn_update.click();
		checkList(desktop);
		checkArray(desktop);
		checkMap(desktop);
		/* event - remove */
		btn_remove.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
		
		/* event - serialization */
		btn_serialization.click();
		/* event - add */
		btn_add.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
		/* event - update */
		btn_update.click();
		checkList(desktop);
		checkArray(desktop);
		checkMap(desktop);
		/* event - remove */
		btn_remove.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
		
		/* event - change model */
		btn_change_model.click();
		/* event - add */
		btn_add.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
		/* event - update */
		btn_update.click();
		checkList(desktop);
		checkArray(desktop);
		checkMap(desktop);
		/* event - remove */
		btn_remove.click();
		checkList(desktop);
		checkMap(desktop);
		checkSet(desktop);
	}
	
	
	private void checkList(DesktopAgent desktop) {
		List<ComponentAgent> lb_init = desktop.query("#w #list #lli").queryAll("listitem");
		List<ComponentAgent> lb_load = desktop.query("#w #list #lll").queryAll("listitem");
		ComponentAgent list_init = desktop.query("#w #list #fli");
		ComponentAgent list_load = desktop.query("#w #list #fll");
		
		//size
		assertEquals(lb_init.size(), list_init.getChildren().size() - 2);
		assertEquals(lb_load.size(), list_load.getChildren().size() - 2);
		
		for (int i = 0; i < lb_init.size(); i++) {
			String id_lb = lb_init.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_init.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_fm = list_init.getChild(i + 1).getChild(0).as(Label.class).getValue();
			String name_fm = list_init.getChild(i + 1).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_fm);
			assertEquals(name_lb, name_fm);
		}
		
		for (int i = 0; i < lb_load.size(); i++) {
			String id_lb = lb_load.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_load.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_fm = list_load.getChild(i + 1).getChild(0).as(Label.class).getValue();
			String name_fm = list_load.getChild(i + 1).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_fm);
			assertEquals(name_lb, name_fm);
		}
	}

	private void checkArray(DesktopAgent desktop) {
		List<ComponentAgent> lb_1d = desktop.query("#w #array #la1d").queryAll("listitem");
		List<ComponentAgent> lb_2d = desktop.query("#w #array #la2d").queryAll("listitem");
		ComponentAgent array_1d = desktop.query("#w #array #fa1d");
		ComponentAgent array_2d = desktop.query("#w #array #fa2d");
		
		//size
		assertEquals(lb_1d.size(), array_1d.getChildren().size() - 2);
		assertEquals(lb_2d.size(), array_2d.getChildren().size() - 2);
		
		for (int i = 0; i < lb_1d.size(); i++) {
			String val_lb = lb_1d.get(i).as(Listitem.class).getLabel();
			String val_fm = array_1d.getChild(i + 1).as(Label.class).getValue();
			assertEquals(val_lb, val_fm);
		}
		
		for (int i = 0; i < lb_2d.size(); i++) {
			String val_lb = lb_2d.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String desc_lb = lb_2d.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String val_fm = array_2d.getChild(i + 1).getChild(0).as(Label.class).getValue();
			String desc_fm = array_2d.getChild(i + 1).getChild(2).as(Label.class).getValue();
			assertEquals(val_lb, val_fm);
			assertEquals(desc_lb, desc_fm);
		}
	}
	
	private void checkMap(DesktopAgent desktop) {
		List<ComponentAgent> lb_map = desktop.query("#w #map_set #lm").queryAll("listitem");
		ComponentAgent map = desktop.query("#w #map_set #fm");
		
		
		//size
		assertEquals(lb_map.size(), map.getChildren().size() - 2);
		
		for (int i = 0; i < lb_map.size(); i++) {
			String id_lb = lb_map.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_map.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_fm = map.getChild(i + 1).getChild(0).as(Label.class).getValue();
			String name_fm = map.getChild(i + 1).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_fm);
			assertEquals(name_lb, name_fm);
		}
	}
	
	private void checkSet(DesktopAgent desktop) {
		List<ComponentAgent> lb_set = desktop.query("#w #map_set #ls").queryAll("listitem");
		ComponentAgent set = desktop.query("#w #map_set #fs");
		
		//size
		assertEquals(lb_set.size(), set.getChildren().size() - 2);
		
		for (int i = 0; i < lb_set.size(); i++) {
			String id_lb = lb_set.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_set.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_fm = set.getChild(i + 1).getChild(0).as(Label.class).getValue();
			String name_fm = set.getChild(i + 1).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_fm);
			assertEquals(name_lb, name_fm);
		}
	}
	
	
	
}
