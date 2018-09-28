/* F86_ZK_4028Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Sep 25 16:21:48 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zuti.zul.Navigation;
import org.zkoss.zuti.zul.NavigationLevel;
import org.zkoss.zuti.zul.NavigationModel;
import org.zkoss.zuti.zul.event.NavigationEvent;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Composer extends SelectorComposer<Div> implements EventListener<NavigationEvent<String>> {
	private NavigationModel<String> navModel = new NavigationModel<>();

	@Wire("::shadow#nav1") private Navigation nav1;
	@Wire private A s1;
	@Wire private A s2;
	@Wire private A s3;

	public F86_ZK_4028Composer() {
		navModel.put("Step 1", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 1/Step 1-1", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 2", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 2/Step 2-1", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 2/Step 2-2", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 2/Step 2-2/Step 2-2-1", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 2/Step 2-2/Step 2-2-2", "/test2/F86-ZK-4028-mvc-lvl.zul");
		navModel.put("Step 3", "/test2/F86-ZK-4028-mvc-lvl.zul");
	}

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		NavigationLevel<String> firstLevel = navModel.getChild();
		nav1.setLevel(firstLevel);
		updateLevel(nav1, firstLevel);
		navModel.addEventListener(this);
	}

	private void updateLevel(Navigation nav, NavigationLevel<String> level) {
		nav.setTemplateURI(level.getCurrent());
		nav.setDynamicProperty("data", level.getCurrent());
		nav.setDynamicProperty("context", level.getContext());
		nav.recreate();
	}

	@Listen("onClick = a#s1, a#s2, a#s3")
	public void nav(MouseEvent event) {
		navModel.navigateTo(((A) event.getTarget()).getLabel());
	}

	@Listen("onClick = #btn")
	public void directNavTo() {
		navModel.navigateToByPath("Step 1/Step 1-1");
	}

	@Override
	public void onEvent(NavigationEvent<String> event) throws Exception {
		Clients.log(event.toString());
		if (event.getType() == NavigationEvent.Type.NAVIGATE) {
			Navigation nav = getNavigation(event.getLevel().getLevel());
			if (nav != null)
				updateLevel(nav, event.getLevel());
		}
	}

	private Navigation getNavigation(int level) {
		Navigation base = nav1;
		while (level > 1) {
			base = (Navigation) Selectors.iterable(base.getDistributedChildren().get(0), "::shadow").iterator().next();
			if (base == null)
				return null;
			level--;
		}
		return base;
	}
}
