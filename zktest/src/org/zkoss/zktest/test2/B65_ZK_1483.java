package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

/**
 * Bug ZK-1483: Jumpy scrollbar for listbox with rod when items are selected
 * @link http://tracker.zkoss.org/browse/ZK-1483
 */
public class B65_ZK_1483 extends SelectorComposer<Window> {

	@Wire
	private Listbox listbox;

	private List<String> elements = getRandomDummyElements(1000);
	private ListModelList<String> lmdList;

	@Override
	public final void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		lmdList = new ListModelList<String>(elements);
		listbox.setModel(lmdList);
		listbox.setItemRenderer(new ListitemRenderer<String>() {
			@Override
			public void render(Listitem item, String data, int index) throws Exception {
				item.setLabel(data);
			}
		});
		listbox.setMultiple(true);
		listbox.setCheckmark(true);
		listbox.setAttribute("org.zkoss.zul.listbox.rod", true);
	}

	private static final List<String> getRandomDummyElements(int nbItems) {

		List<String> dummys = new ArrayList<String>(nbItems);

		for (int i = 0; i < nbItems; i++) {
			dummys.add(String.format("%4d: %s", i, randomAlphabetic(15)));
		}

		return dummys;
	}
	
	private static final String randomAlphabetic(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i=0; i < length; i++) {
			sb.append((char) ('A' + (int) (('Z'-'A') * Math.random())));
		}
		return sb.toString();
	}

	@Listen(Events.ON_CLICK + " = #btnSelectAll")
	public void handlerSelectAllListBox(Event evt) {
		lmdList.clearSelection();
		lmdList.setSelection(elements);
	}

}
