package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.Splitpane;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 */
public class F85_ZK_3683 extends SelectorComposer<Window> {
	@Wire
	private Splitpane sp1;

	private Splitpane lastSp;

	private int count = 0;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		lastSp = sp1;
	}

	@Listen("onClick = #btn")
	public void btnAddNestedSplitPane() {
		if (count > 2)
			return;
		Window win;
		Splitpane parentSp = lastSp;
		if (parentSp != null) {
			win = (Window) parentSp.getChildren().get(1);
			win.detach();
			if (count % 2 == 0) {
				appendSplitpane(parentSp, false);
			}
			else {
				appendSplitpane(parentSp, true);
			}
			count++;
		}
	}

	private void appendSplitpane(Splitpane parentSp, boolean isVertical) {
		String orient = isVertical ? "vertical" : "horizontal";
		Splitpane newSp = new Splitpane();
		newSp.setOrient(orient);
		newSp.setHeight("100%");
		newSp.setWidth("100%");
		Window newWin = new Window();
		newWin.setTitle("Window new" + (count + 1));
		newWin.setBorder("normal");
		newWin.setVflex("1");
		newWin.setHflex("1");
		Label l = new Label("Hello, World! new" + (count + 1));
		newWin.appendChild(l);
		newSp.appendChild(newWin);
		Window newWin1 = new Window();
		newWin1.setTitle("Window new" + (count + 2));
		newWin1.setBorder("normal");
		newWin1.setVflex("2");
		newWin1.setHflex("2");
		Label l1 = new Label("Hello, World! new" + (count + 2));
		newWin1.appendChild(l1);
		newSp.appendChild(newWin1);
		parentSp.appendChild(newSp);
		lastSp = newSp;
	}
}
