/* F90_ZK_4380Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Sep 20 10:09:27 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Searchbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.ext.Selectable;

/**
 * @author rudyhuang
 */
public class F90_ZK_4380Composer extends SelectorComposer<Component> {
	@Wire
	private Searchbox<String> myComp;

	private ListModelList<String> model;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		model = new ListModelList<>();
		for (int i = 1; i <= 100; i++) {
			model.add("Item " + i);
		}
		((Selectable<String>) model).addToSelection("Item 99");
		myComp.setModel(model);
		myComp.setItemConverter(obj -> String.format("Selected %d items: %s", obj.size(), obj.toString()));
	}

	@Listen("onSearching=#myComp")
	public void handleOnSearching(InputEvent event) {
		Clients.log(String.format("You listen onSearching: %s prev: %s value: %s pos: %d",
				event.getTarget(), event.getPreviousValue(), event.getValue(), event.getStart()));
	}

	@Listen("onClick=#modelMultiple")
	public void handleModelMultiple() {
		Selectable m = (Selectable) myComp.getModel();
		m.setMultiple(!m.isMultiple());
	}

	@Listen("onClick=#addItem")
	public void handleAddItem() {
		model.add(0, LocalTime.now().toString());
	}

	@Listen("onClick=#removeItem")
	public void handleRemoveItem() {
		model.remove(model.getSize() - 1);
	}

	@Listen("onClick=#changeItem")
	public void handleChangeItem() {
		final ListIterator<String> listIterator = model.listIterator();
		if (listIterator.hasNext()) {
			listIterator.next();
			listIterator.set(LocalTime.now().toString());
		}
	}

	@Listen("onClick=#addSelItem")
	public void handleAddSelItem() {
		final int size = model.getSize();
		final SecureRandom rng = new SecureRandom();
		final Set<String> selection = new HashSet<>();
		for (int i = 0; i < 3; i ++) {
			selection.add(model.get(rng.nextInt(size)));
		}
		model.setMultiple(true);
		model.setSelection(selection);
	}

	@Listen("onClick=#setModel")
	public void handleSetModel() {
		model = new ListModelList<>(Arrays.asList("1", "2", "3", "4"));
		myComp.setModel(model);
		//((Selectable<String>) model).addToSelection("2");
	}

	@Listen("onClick=#setModelSet")
	public void handleSetModelSet() {
		Set<String> newset = new HashSet<>();
		ListModelSet<String> modelSet = new ListModelSet<>(newset, true);
		myComp.setModel(modelSet);
		modelSet.addAll(Arrays.asList("1", "2", "3", "4"));
	}

	@Listen("onClick=#setModelNull")
	public void handleSetModelNull() {
		myComp.setModel(null);
	}

	@Listen("onClick=#setListSubModel")
	public void handleSetListSubModel() {
		ListModel<String> subModel = ListModels.toListSubModel(model);
		handleAddSelItem();
		myComp.setModel(subModel);
	}

	byte[] _bytes;

	@Listen("onClick=#serDeser")
	public void doSerialize() {
		try {
			doSerialize0(myComp);
			doDeserialize0(myComp);
		} catch (Exception x) {
			x.printStackTrace();
			Clients.log("error :" + x.getClass() + "," + x.getMessage());
		}
	}

	public void doSerialize0(Component comp) throws Exception {
		((ComponentCtrl) comp).sessionWillPassivate(comp.getPage()); // simulate
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(comp);
		oos.close();
		baos.close();
		_bytes = baos.toByteArray();
	}

	public void doDeserialize0(Component comp) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(_bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);

		Component newComp = (Component) ois.readObject();
		Component parent = comp.getParent();
		Component ref = comp.getNextSibling();
		comp.detach();
		ois.close();
		bais.close();
		parent.insertBefore(newComp, ref);
		//for load component back.
		((ComponentCtrl) newComp).sessionDidActivate(newComp.getPage()); // simulate
		myComp = (Searchbox<String>) newComp; // replace the old one
		Clients.log("done deserialize: " + _bytes.length);
	}
}
