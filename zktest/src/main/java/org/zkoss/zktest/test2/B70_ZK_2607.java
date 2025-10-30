/**
 * 
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ItemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Selectbox;
import org.zkoss.zul.Window;

/**
 * @author jumperchen
 * 
 */
public class B70_ZK_2607 extends GenericForwardComposer<Window> {
	Selectbox mySelectbox;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList model = new ListModelList();
		model.add("value1");
		model.add("value2");
		mySelectbox.setModel(model);
		mySelectbox.setItemRenderer(new ItemRenderer<String>() {
			public String render(Component component, String option, int i)
					throws Exception {
				return "renderer1";
			}
		});
	}

	public void onClick$btn(Event e) throws InterruptedException {
		mySelectbox.setItemRenderer(new ItemRenderer<String>() {
			public String render(Component component, String option, int i)
					throws Exception {
				return "renderer2";
			}
		});
	}
}