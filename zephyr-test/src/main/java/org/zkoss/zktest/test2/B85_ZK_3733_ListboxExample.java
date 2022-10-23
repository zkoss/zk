package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.zul.*;

import java.io.Serializable;
import java.util.Date;

public class B85_ZK_3733_ListboxExample implements Serializable {
	private static final long serialVersionUID = 1L;
	private B85_ZK_3733_FoodGroupsModel groupModel;


	@Init
	public void init() {
		groupModel = new B85_ZK_3733_FoodGroupsModel(B85_ZK_3733_FoodData.getAllFoodsArray(), new B85_ZK_3733_FoodComparator());
		groupModel.setMultiple(true);
	}

	public B85_ZK_3733_FoodGroupsModel getGroupModel() {
		return groupModel;
	}

	public ListitemRenderer<Object> getItemRenderer() {

		return new ListitemRenderer<Object>() {
			@Override
			public void render(Listitem listitem, Object obj, int index) {

				if (listitem instanceof Listgroup) {
					B85_ZK_3733_FoodGroupsModel.FoodGroupInfo groupInfo = (B85_ZK_3733_FoodGroupsModel.FoodGroupInfo) obj;
					listitem.appendChild(new Listcell(groupInfo.getFirstChild().getCategory()));
					listitem.setValue(obj);
				} else if (listitem instanceof Listgroupfoot) {
					Listcell cell = new Listcell();
					cell.setSpan(4);
					cell.appendChild(new Label("Total " + obj + " Items"));
					listitem.appendChild(cell);
				} else {
					B85_ZK_3733_Food data = (B85_ZK_3733_Food) obj;
					Listcell cell = new Listcell();
					cell.setLabel(data.getCategory());
					listitem.appendChild(cell);

					cell = new Listcell();
					Textbox textbox = new Textbox(data.getName());
					textbox.setWidth("300px");
					cell.appendChild(textbox);
					listitem.appendChild(cell);

					cell = new Listcell();
					Combobox combobox = new Combobox(data.getTopNutrients());
					combobox.setWidth("300px");
					for (int i = 0; i < 20; i++) {
						combobox.appendItem("Option" + (i + 1));
					}
					cell.appendChild(combobox);
					listitem.appendChild(cell);

					cell = new Listcell();
					Datebox datebox = new Datebox(new Date());
					datebox.setWidth("300px");
					cell.appendChild(datebox);
					listitem.appendChild(cell);

					listitem.setValue(data);
				}
			}
		};
	}
}