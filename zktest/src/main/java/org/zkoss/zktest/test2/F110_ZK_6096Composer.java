/* F110_ZK_6096Composer.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;

public class F110_ZK_6096Composer extends SelectorComposer<Component> {
	@Wire
	private Label result;
	@Wire
	private Div g4live;
	@Wire
	private Hlayout g5live;
	@Wire
	private Grid g9grid;
	@Wire
	private Listbox g10listbox;
	@Wire
	private Grid g17grid;
	@Wire
	private Label g17result;
	@Wire
	private Div g18shield;
	@Wire
	private Div g18control;
	@Wire
	private Label g18result;
	private Div g18nested;
	private Div g18controlNested;
	private Label g18first;
	private Label g18second;
	@Wire
	private Hlayout g19live;
	@Wire
	private Label g19first;
	@Wire
	private Label g19result;
	private Label g19second;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		final ListModelList<String> model = new ListModelList<>();
		for (int i = 0; i < 120; i++) {
			final Row row = new Row();
			final Label gridLabel = new Label("rod-row-" + i);
			gridLabel.setId("g9label" + i);
			row.appendChild(gridLabel);
			g9grid.getRows().appendChild(row);

			final Listitem item = new Listitem();
			final Listcell cell = new Listcell();
			final Label listboxLabel = new Label("rod-item-" + i);
			listboxLabel.setId("g10label" + i);
			cell.appendChild(listboxLabel);
			item.appendChild(cell);
			g10listbox.appendChild(item);
			model.add("model-row-" + i);
		}
		g17grid.setRowRenderer((Row row, String data, int index) -> {
			final Label label = new Label(data);
			label.setId("g17label" + index);
			row.appendChild(label);
		});
		g17grid.setModel(model);
	}

	@Listen("onClick = #append")
	public void append() {
		final Label label = new Label("g4-new");
		label.setId("g4newLbl");
		g4live.appendChild(label);
		final Div div = new Div();
		div.setId("g4newDiv");
		g4live.appendChild(div);
		final Label label5 = new Label("g5-new");
		label5.setId("g5newLbl");
		g5live.appendChild(label5);
	}

	@Listen("onClick = #g1btn; onClick = #g3btn")
	public void clickLive(Event event) {
		result.setValue(event.getTarget().getId() + " clicked");
	}

	@Listen("onClick = #probe")
	public void probe() {
		final StringBuilder sb = new StringBuilder();
		appendStubIds(getSelf(), sb);
		result.setValue(sb.toString().trim());
	}

	@Listen("onClick = #g17update")
	public void updateModelRow() {
		final Component label = g17grid.getRows().getFirstChild().getFirstChild();
		final Cropper cropper = (Cropper) ((ComponentCtrl) g17grid.getRows()).getExtraCtrl();
		g17result.setValue("model: " + (g17grid.getModel() != null)
				+ "; cropper: " + cropper.isCropper() + "; row label: " + label.getClass().getSimpleName());
		if (label instanceof Label)
			((Label) label).setValue("updated-model-row-0");
	}

	@Listen("onClick = #g18appendContainer")
	public void appendNestedContainer() {
		g18nested = new Div();
		g18nested.setStubonlyDescendants("label");
		g18first = new Label("protected-first");
		g18first.setId("g18first");
		g18nested.appendChild(g18first);
		g18shield.appendChild(g18nested);

		g18controlNested = new Div();
		g18controlNested.setStubonlyDescendants("label");
		g18controlNested.appendChild(new Label("control-first"));
		g18control.appendChild(g18controlNested);
	}

	@Listen("onClick = #g18appendLabel")
	public void appendNestedLabel() {
		g18second = new Label("protected-second");
		g18second.setId("g18second");
		g18nested.appendChild(g18second);
		g18controlNested.appendChild(new Label("control-second"));
	}

	@Listen("onClick = #g18update")
	public void updateNestedLabels() {
		final boolean originals = g18nested.getFirstChild() == g18first
				&& g18nested.getLastChild() == g18second;
		final boolean controlStubs = g18controlNested.getFirstChild() instanceof StubComponent
				&& g18controlNested.getLastChild() instanceof StubComponent;
		g18result.setValue("protected originals: " + originals + "; control stubs: " + controlStubs);
		g18first.setValue("updated-first");
		g18second.setValue("updated-second");
	}

	@Listen("onClick = #g19append")
	public void appendNativeLabel() {
		g19second = new Label("native-second");
		g19second.setId("g19second");
		g19live.appendChild(g19second);
	}

	@Listen("onClick = #g19update")
	public void updateNativeLabels() {
		final boolean originals = g19live.getFirstChild() == g19first
				&& (g19second == null || g19live.getLastChild() == g19second);
		g19result.setValue("native stub: " + (g19live.getParent() instanceof StubComponent)
				+ "; originals: " + originals);
		g19first.setValue("updated-native-first");
		if (g19second != null)
			g19second.setValue("updated-native-second");
	}

	private static void appendStubIds(Component comp, StringBuilder sb) {
		if (comp instanceof StubComponent)
			sb.append(comp.getId()).append(' ');
		for (Component child = comp.getFirstChild(); child != null; child = child.getNextSibling())
			appendStubIds(child, sb);
	}
}
