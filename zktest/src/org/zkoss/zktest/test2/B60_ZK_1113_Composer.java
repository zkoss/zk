package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

public class B60_ZK_1113_Composer extends GenericForwardComposer {
	private transient Window win;
	private transient Chosenbox cbx;

	List<String> lst = Arrays.asList(new String[] { "first", "second", "third" });

	/*
	 * Binding definition
	 */
	public ListModelList<String> getModel() {
		return new ListModelList<String>(lst);
	}

	public void onCancel$win(ForwardEvent event) {
		Events.postEvent(new Event(Events.ON_CLOSE, win, null));
	}
}
