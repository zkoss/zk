package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class F02545ChildrenBindingSupportListModelTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent list_init = desktop.query("#w #list #cbmi");
		ComponentAgent list_load = desktop.query("#w #list #cbml");
		ComponentAgent array_1d = desktop.query("#w #array #cbml_a1d");
		ComponentAgent array_2d = desktop.query("#w #array #cbml_a2d");
		ComponentAgent map = desktop.query("#w #map_set #cbml_m");
		ComponentAgent set = desktop.query("#w #map_set #cbml_s");
		ComponentAgent btn_add = desktop.query("#w #add");
		ComponentAgent btn_update = desktop.query("#w #update");
		ComponentAgent btn_remove = desktop.query("#w #remove");
		ComponentAgent btn_change_model = desktop.query("#w #change_model");
		ComponentAgent btn_serialization = desktop.query("#serialization");
		
		/* initial test */
		assertEquals(list_init.getChildren().size(), 5);
		assertEquals(list_load.getChildren().size(), 5);
		assertEquals(array_1d.getChildren().size(), 3);
		assertEquals(array_2d.getChildren().size(), 3);
		assertEquals(map.getChildren().size(), 5);
		assertEquals(set.getChildren().size(), 5);
		
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
	}
	
	
	private void checkList(DesktopAgent desktop) {
		List<ComponentAgent> lb_init = desktop.query("#w #list #omi").queryAll("listitem");
		ComponentAgent list_init = desktop.query("#w #list #cbmi");
		List<ComponentAgent> lb_load = desktop.query("#w #list #oml").queryAll("listitem");
		ComponentAgent list_load = desktop.query("#w #list #cbml");
		
		//size
		assertEquals(lb_init.size(), list_init.getChildren().size());
		assertEquals(lb_load.size(), list_load.getChildren().size());
		
		for (int i = 0; i < lb_init.size(); i++) {
			String id_lb = lb_init.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_init.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_cbm = list_init.getChild(i).getChild(0).as(Label.class).getValue();
			String name_cbm = list_init.getChild(i).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
		
		for (int i = 0; i < lb_load.size(); i++) {
			String id_lb = lb_load.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_load.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_cbm = list_load.getChild(i).getChild(0).as(Label.class).getValue();
			String name_cbm = list_load.getChild(i).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}

	private void checkArray(DesktopAgent desktop) {
		List<ComponentAgent> lb_1d = desktop.query("#w #array #oml_a1d").queryAll("listitem");
		List<ComponentAgent> lb_2d = desktop.query("#w #array #oml_a2d").queryAll("listitem");
		ComponentAgent array_1d = desktop.query("#w #array #cbml_a1d");
		ComponentAgent array_2d = desktop.query("#w #array #cbml_a2d");
		
		//size
		assertEquals(lb_1d.size(), array_1d.getChildren().size());
		assertEquals(lb_2d.size(), array_2d.getChildren().size());
		
		for (int i = 0; i < lb_1d.size(); i++) {
			String val_lb = lb_1d.get(i).as(Listitem.class).getLabel();
			String val_cbm = array_1d.getChild(i).as(Label.class).getValue();
			assertEquals(val_lb, val_cbm);
		}
		
		for (int i = 0; i < lb_2d.size(); i++) {
			String val_lb = lb_2d.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String desc_lb = lb_2d.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String val_cbm = array_2d.getChild(i).getChild(0).as(Label.class).getValue();
			String desc_cbm = array_2d.getChild(i).getChild(2).as(Label.class).getValue();
			assertEquals(val_lb, val_cbm);
			assertEquals(desc_lb, desc_cbm);
		}
	}
	
	private void checkMap(DesktopAgent desktop) {
		List<ComponentAgent> lb_map = desktop.query("#w #map_set #oml_m").queryAll("listitem");
		ComponentAgent map = desktop.query("#w #map_set #cbml_m");
		
		//size
		assertEquals(lb_map.size(), map.getChildren().size());
		
		for (int i = 0; i < lb_map.size(); i++) {
			String id_lb = lb_map.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_map.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_cbm = map.getChild(i).getChild(0).as(Label.class).getValue();
			String name_cbm = map.getChild(i).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}
	
	private void checkSet(DesktopAgent desktop) {
		List<ComponentAgent> lb_set = desktop.query("#w #map_set #oml_s").queryAll("listitem");
		ComponentAgent set = desktop.query("#w #map_set #cbml_s");
		
		//size
		assertEquals(lb_set.size(), set.getChildren().size());
		
		for (int i = 0; i < lb_set.size(); i++) {
			String id_lb = lb_set.get(i).queryAll("listcell").get(0).as(Listcell.class).getLabel();
			String name_lb = lb_set.get(i).queryAll("listcell").get(1).as(Listcell.class).getLabel();
			String id_cbm = set.getChild(i).getChild(0).as(Label.class).getValue();
			String name_cbm = set.getChild(i).getChild(2).as(Label.class).getValue();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}
}
