package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.ext.Selectable;

public class B96_ZK_4955Composer extends SelectorComposer<Component> {
	//Wire the component by it's ID to access it in the composer, more info in ZK MVC documentation
	@Wire
	private Combobox myCb;
	//List of my Data, from anywhere
	private List<MyEntityBean> myModel = new ArrayList();
	//Implementation of submodel (auto filtered model) to use in combobox
	private ListModel mySubModel;

	// This method is called after composing the page, and is suitable to intialize values and additional components
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		//call super (required, or the page will not finish composition)
		super.doAfterCompose(comp);
		//generate some data
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 5; j++) {
				myModel.add(new MyEntityBean(i + "-" + j + "-id", i + "-" + j + "-data"));
			}
		}

		//Comparator have many uses. Here, the comparator will return 0 is the item match the input string and should be included in the result)
		Comparator myComparator = new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// the value of the combobox input
				String input = (String) o1;
				// the bean from the model
				MyEntityBean bean = (MyEntityBean) o2;
				return bean.getData().contains(input) ? 0 : 1;
			}
		};

		//Generate a ListModel from the flat data, then a subModel from the listmodel
		//Comparator is used when filtering to identify which bean should be included in the result
		//This subModel will return the top 15 results from when filtering. Anything further will be ignored. You can implement your own submodel to have control over this process.
		mySubModel = ListModels.toListSubModel(new ListModelList(myModel), myComparator, 15);
		myCb.setModel(mySubModel);

		//Renderer will be called when creating the comboitem (each line in combobox) from the bean. You can implement your own renderer to control what is displayed
		ComboitemRenderer<MyEntityBean> myRenderer = new ComboitemRenderer<MyEntityBean>() {

			@Override
			public void render(Comboitem item, MyEntityBean bean, int index) throws Exception {
				//set the text of the row
				item.setLabel(bean.getData());
				//set the value of the row (available when receiving events from Combobox)
				item.setValue(bean);
			}
		};

		myCb.setItemRenderer(myRenderer);

	}

	@Listen("onChange=#myCb")
	public void handleOnchange(InputEvent event) {
		Clients.log("Onchange: selection -> " + ((Selectable) mySubModel).getSelection().size());
	}

	@Listen("onClick=#btn")
	public void showSelection() {
		Clients.log("selection count: " + ((Selectable) mySubModel).getSelection().size());
	}

	// Demo bean with 2 fields. data is the public label, ID is the internal key (not displayed to users)
	public class MyEntityBean {
		private String id;
		private String data;

		public MyEntityBean(String id, String data) {
			super();
			this.id = id;
			this.data = data;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}
}