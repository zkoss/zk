/* ICascaderRichlet.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:17:50 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyrex.state.ICascaderController;
import org.zkoss.zephyrex.zpr.ICascader;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.ICascader} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Cascader">ICascader</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ICascader
 */
@RichletMapping("/input/iCascader")
public class ICascaderRichlet implements StatelessRichlet {

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				initCascader().withOpen(false),
				IButton.of("change open").withAction(this::changeOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cascader"), new ICascader.Updater().open(true));
	}

	@RichletMapping("/placeholder")
	public List<IComponent> placeholder() {
		return Arrays.asList(
				initCascader().withPlaceholder("test"),
				IButton.of("change placeholder").withAction(this::changePlaceholder)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePlaceholder() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cascader"), new ICascader.Updater().placeholder(null));
	}

	@RichletMapping("/disabled")
	public List<IComponent> disabled() {
		return Arrays.asList(
				ICascader.ofId("cascader").withDisabled(true),
				IButton.of("change placeholder").withAction(this::changeDisabled)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cascader"), new ICascader.Updater().disabled(false));
	}

	@RichletMapping("/model")
	public IComponent model() {
		return initCascader();
	}

	public ICascader initCascader() {
		DefaultTreeNode root = new DefaultTreeNode("root", Arrays.asList(new DefaultTreeNode[] {
				new DefaultTreeNode("Node-1"),
				new DefaultTreeNode("Node-2", Arrays.asList(new DefaultTreeNode[] {
						new DefaultTreeNode("Node-2.2", Arrays.asList(new DefaultTreeNode[] {
								new DefaultTreeNode("Node-2.2.1"),
								new DefaultTreeNode("Node-2.2.2"),
								new DefaultTreeNode("Node-2.2.3"),
								new DefaultTreeNode("Node-2.2.4"),
								new DefaultTreeNode("Node-2.2.5")
						})),
						new DefaultTreeNode("Node-2.3")
				})),
				new DefaultTreeNode("Node-3"),
				new DefaultTreeNode("Node-4")
		}));
		DefaultTreeModel model = new DefaultTreeModel(root);

		return ICascaderController.of(ICascader.ofId("cascader"), model).build();
	}
}