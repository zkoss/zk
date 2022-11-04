package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F02545ChildrenBindingSupportListModelTest extends WebDriverTestCase {
	private JQuery list_init;
	private JQuery list_load;
	private JQuery array_1d;
	private JQuery array_2d;
	private JQuery map;
	private JQuery set;
	private JQuery btn_add;
	private JQuery btn_update;
	private JQuery btn_remove;
	private JQuery btn_change_model;
	private JQuery btn_serialization;

	@Test
	public void test() {
		connect();
		queryComponents();

		/* initial test */
		assertEquals(list_init.toWidget().nChildren(), 5);
		assertEquals(list_load.toWidget().nChildren(), 5);
		assertEquals(array_1d.toWidget().nChildren(), 3);
		assertEquals(array_2d.toWidget().nChildren(), 3);
		assertEquals(map.toWidget().nChildren(), 5);
		assertEquals(set.toWidget().nChildren(), 5);

		checkList();
		checkArray();
		checkMap();
		checkSet();
		/* event - add */
		click(btn_add);
		waitResponse();
		checkList();
		checkMap();
		checkSet();
		/* event - update */
		click(btn_update);
		waitResponse();
		checkList();
		checkArray();
		checkMap();
		/* event - remove */
		click(btn_remove);
		waitResponse();
		checkList();
		checkMap();
		checkSet();

		/* event - serialization */
		click(btn_serialization);
		waitResponse();
		queryComponents();
		/* event - add */
		click(btn_add);
		waitResponse();
		checkList();
		checkMap();
		checkSet();
		/* event - update */
		click(btn_update);
		waitResponse();
		checkList();
		checkArray();
		checkMap();
		/* event - remove */
		click(btn_remove);
		waitResponse();
		checkList();
		checkMap();
		checkSet();

		/* event - change model */
		click(btn_change_model);
		waitResponse();
		/* event - add */
		click(btn_add);
		waitResponse();
		checkList();
		checkMap();
		checkSet();
		/* event - update */
		click(btn_update);
		waitResponse();
		checkList();
		checkArray();
		checkMap();
		/* event - remove */
		click(btn_remove);
		waitResponse();
		checkList();
		checkMap();
		checkSet();

		/* event - serialization */
		click(btn_serialization);
		waitResponse();
		queryComponents();
		/* event - add */
		click(btn_add);
		waitResponse();
		checkList();
		checkMap();
		checkSet();
		/* event - update */
		click(btn_update);
		waitResponse();
		checkList();
		checkArray();
		checkMap();
		/* event - remove */
		click(btn_remove);
		waitResponse();
		checkList();
		checkMap();
		checkSet();
	}

	private void checkList() {
		JQuery lb_init = jq("$w $list $omi").find("@listitem");
		JQuery list_init = jq("$w $list $cbmi");
		JQuery lb_load = jq("$w $list $oml").find("@listitem");
		JQuery list_load = jq("$w $list $cbml");

		//size
		assertEquals(lb_init.length(), list_init.toWidget().nChildren());
		assertEquals(lb_load.length(), list_load.toWidget().nChildren());

		for (int i = 0; i < lb_init.length(); i++) {
			String id_lb = lb_init.eq(i).find("@listcell").eq(0).toWidget().get("label");
			String name_lb = lb_init.eq(i).find("@listcell").eq(1).toWidget().get("label");
			String id_cbm = list_init.find("@hlayout").eq(i).find("@label").eq(0).text();
			String name_cbm = list_init.find("@hlayout").eq(i).find("@label").eq(2).text();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}

		for (int i = 0; i < lb_load.length(); i++) {
			String id_lb = lb_load.eq(i).find("@listcell").eq(0).toWidget().get("label");
			String name_lb = lb_load.eq(i).find("@listcell").eq(1).toWidget().get("label");
			String id_cbm = list_load.find("@hlayout").eq(i).find("@label").eq(0).text();
			String name_cbm = list_load.find("@hlayout").eq(i).find("@label").eq(2).text();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}

	private void checkArray() {
		JQuery lb_1d = jq("$w $array $oml_a1d").find("@listitem");
		JQuery lb_2d = jq("$w $array $oml_a2d").find("@listitem");
		JQuery array_1d = jq("$w $array $cbml_a1d");
		JQuery array_2d = jq("$w $array $cbml_a2d");

		//size
		assertEquals(lb_1d.length(), array_1d.toWidget().nChildren());
		assertEquals(lb_2d.length(), array_2d.toWidget().nChildren());

		for (int i = 0; i < lb_1d.length(); i++) {
			String val_lb = lb_1d.eq(i).toWidget().get("label");
			String val_cbm = array_1d.find("@label").eq(i).text();
			assertEquals(val_lb, val_cbm);
		}

		for (int i = 0; i < lb_2d.length(); i++) {
			String val_lb = lb_2d.eq(i).find("@listcell").eq(0).toWidget().get("label");
			String desc_lb = lb_2d.eq(i).find("@listcell").eq(1).toWidget().get("label");
			String val_cbm = array_2d.find("@hlayout").eq(i).find("@label").eq(0).text();
			String desc_cbm = array_2d.find("@hlayout").eq(i).find("@label").eq(2).text();
			assertEquals(val_lb, val_cbm);
			assertEquals(desc_lb, desc_cbm);
		}
	}

	private void checkMap() {
		JQuery lb_map = jq("$w $map_set $oml_m").find("@listitem");
		JQuery map = jq("$w $map_set $cbml_m");

		//size
		assertEquals(lb_map.length(), map.toWidget().nChildren());

		for (int i = 0; i < lb_map.length(); i++) {
			String id_lb = lb_map.eq(i).find("@listcell").eq(0).toWidget().get("label");
			String name_lb = lb_map.eq(i).find("@listcell").eq(1).toWidget().get("label");
			String id_cbm = map.find("@hlayout").eq(i).find("@label").eq(0).text();
			String name_cbm = map.find("@hlayout").eq(i).find("@label").eq(2).text();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}

	private void checkSet() {
		JQuery lb_set = jq("$w $map_set $oml_s").find("@listitem");
		JQuery set = jq("$w $map_set $cbml_s");

		//size
		assertEquals(lb_set.length(), set.toWidget().nChildren());

		for (int i = 0; i < lb_set.length(); i++) {
			String id_lb = lb_set.eq(i).find("@listcell").eq(0).toWidget().get("label");
			String name_lb = lb_set.eq(i).find("@listcell").eq(1).toWidget().get("label");
			String id_cbm = set.find("@hlayout").eq(i).find("@label").eq(0).text();
			String name_cbm = set.find("@hlayout").eq(i).find("@label").eq(2).text();
			assertEquals(id_lb, id_cbm);
			assertEquals(name_lb, name_cbm);
		}
	}

	private void queryComponents() {
		list_init = jq("$w $list $cbmi");
		list_load = jq("$w $list $cbml");
		array_1d = jq("$w $array $cbml_a1d");
		array_2d = jq("$w $array $cbml_a2d");
		map = jq("$w $map_set $cbml_m");
		set = jq("$w $map_set $cbml_s");
		btn_add = jq("$w $add");
		btn_update = jq("$w $update");
		btn_remove = jq("$w $remove");
		btn_change_model = jq("$w $change_model");
		btn_serialization = jq("$serialization");
	}
}
