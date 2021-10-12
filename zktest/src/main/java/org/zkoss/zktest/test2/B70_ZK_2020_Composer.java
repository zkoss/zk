package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.ListDataEvent;

public class B70_ZK_2020_Composer extends GenericForwardComposer<Window> {
	private static final long serialVersionUID = 1L;
	List<String> list = new LinkedList<String>(Arrays.asList("aaa", "bbb",
			"ccc", "ddd"));
	private Textbox myFilter;
	private Grid myGrid;

	private static class MyModel extends AbstractListModel<Object> {
		List<String> data;
		int toDisplay;

		public MyModel(List<String> data) {
			super();
			this.data = data;
			this.toDisplay = this.data.size();
		}

		public int getSize() {
			return this.toDisplay;
		}

		public String getElementAt(int index) {
			return this.data.get(index);
		}

		public void refreshData() {
			this.toDisplay--;
			if (this.toDisplay == 0) {
				this.toDisplay = this.data.size();
			}
			fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
		}

	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		initListeners();
		initRenderer();
		initModel();
	}

	private void initRenderer() {
		myGrid.setRowRenderer(new RowRenderer<Object>() {

			public void render(Row row, Object data, int index)
					throws Exception {
				new Label(data.toString()).setParent(row);
			}
		});
	}

	private void initModel() {
		myGrid.setModel(new MyModel(this.list));
	}

	private void initListeners() {
		myFilter.addEventListener(Events.ON_OK, new EventListener<KeyEvent>() {
			public void onEvent(KeyEvent event) throws Exception {
				((MyModel) myGrid.getModel()).refreshData();
				myGrid.invalidate();
			}
		});
	}
}
