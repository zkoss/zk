/* ILabelImageElementRichlet.java

	Purpose:

	Description:

	History:
		Thu Mar 30 09:17:36 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.zpr.ILabelImageElement;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.ICaption;
import org.zkoss.stateless.zpr.ICheckbox;
import org.zkoss.stateless.zpr.ICombobox;
import org.zkoss.stateless.zpr.IComboitem;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ILabelImageElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/LabelImageElement">ILabelImageElement</a>,
 * if any.
 *
 * @author katherine
 * @see ILabelImageElement
 */
@RichletMapping("/base_components/iLabelImageElement")
public class ILabelImageElementRichlet implements StatelessRichlet {
	@RichletMapping("/hoverImage")
	public List<IComponent> hoverImage() {
		return Arrays.asList(
				IButton.ofId("btn").withHoverImage("/zephyr/ZK-Logo.gif").withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICaption.ofId("caption").withHoverImage("/zephyr/ZK-Logo.gif").withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICheckbox.ofId("cb").withLabel("item A").withHoverImage("/zephyr/ZK-Logo.gif").withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICombobox.of(IComboitem.ofId("ci").withLabel("item A").withHoverImage("/zephyr/ZK-Logo.gif").withImage("/zephyr/ZK-Logo_en_US.gif")).withOpen(true),
				IButton.of("change hoverImage").withAction(this::changeHoverImage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHoverImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().hoverImage("/zephyr-test/zephyr/ZK-Logo-old.gif"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("caption"), new ICaption.Updater().hoverImage("/zephyr-test/zephyr/ZK-Logo-old.gif"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new ICheckbox.Updater().hoverImage("/zephyr-test/zephyr/ZK-Logo-old.gif"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ci"), new IComboitem.Updater().hoverImage("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

	@RichletMapping("/iconSclass")
	public List<IComponent> iconSclass() {
		return Arrays.asList(
				IButton.ofId("btn").withIconSclass("z-icon-home"),
				ICaption.ofId("caption").withIconSclass("z-icon-home"),
				ICheckbox.ofId("cb").withLabel("item A").withIconSclass("z-icon-home"),
				ICombobox.of(IComboitem.ofId("ci").withLabel("item A").withIconSclass("z-icon-home")).withOpen(true),
				IButton.of("change iconSclass").withAction(this::changeIconSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeIconSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().iconSclass("z-icon-user"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("caption"), new ICaption.Updater().iconSclass("z-icon-user"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new ICheckbox.Updater().iconSclass("z-icon-user"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ci"), new IComboitem.Updater().iconSclass("z-icon-user"));
	}

	@RichletMapping("/preloadImage")
	public List<IComponent> preloadImage() {
		return Arrays.asList(
				IButton.ofId("btn").withPreloadImage(true).withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICaption.ofId("caption").withPreloadImage(true).withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICheckbox.ofId("cb").withLabel("item A").withPreloadImage(true).withImage("/zephyr/ZK-Logo_en_US.gif"),
				ICombobox.of(IComboitem.ofId("ci").withLabel("item A").withPreloadImage(true).withImage("/zephyr/ZK-Logo_en_US.gif"))
		);
	}
}