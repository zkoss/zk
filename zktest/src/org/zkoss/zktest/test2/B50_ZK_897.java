package org.zkoss.zktest.test2;
import java.util.Comparator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.event.GroupsDataEvent;

public class B50_ZK_897 extends GenericForwardComposer {
	private static final long serialVersionUID = 7970338470714930637L;

	private Grid grid;
	
	private Listbox listbox;

	private Button button;

	private RowRenderer rowRenderer;

	private MyModel model;

	private class MyModel extends GroupsModelArray {

		public MyModel(String[] data, Comparator comp) {
			super(data, comp);
		}

		public void update(int group) {			
			fireEvent(GroupsDataEvent.GROUPS_CHANGED, 0, group, group);
		}

		protected Object createGroupHead(Object[] groupdata, int index, int col) {
			return "HEAD";
		}

		protected Object createGroupFoot(Object[] groupdata, int index, int col) {
			return "FOOT";
		}
	}


	public void doAfterCompose(final Component component) throws Exception {
		super.doAfterCompose(component);

		String[] data = new String[4];
		data[0] = "test0";
		data[1] = "test0";
		data[2] = "test1";
		data[3] = "test1";
		
		model = new MyModel(data, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((String)o1).compareTo((String)o2);
			}
		});
		
		if(grid != null)
			grid.setModel(model);
		
		if(listbox != null)
			listbox.setModel(model);
		

		button.addEventListener(Events.ON_CLICK, new EventListener() {
			public void onEvent(Event arg0) throws Exception {
				model.update(0);
			}
		});
	}

}