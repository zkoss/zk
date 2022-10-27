/* IPanelRichlet.java

	Purpose:

	Description:

	History:
		10:28 AM 2022/3/2, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.ICaption;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IHlayout;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IPanel;
import org.zkoss.stateless.zpr.IPanelchildren;
import org.zkoss.stateless.zpr.IToolbar;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IPanel} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Panel">IPanel</a>,
 * if any.
 *
 * @author jumperchen
 * @see IPanel
 */
@RichletMapping("/containers/ipanel")
public class IPanelRichlet implements StatelessRichlet {

	@RichletMapping("/molds")
	public IComponent molds() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofBorder(IPanel.Border.NONE).withTitle("Title").withPanelchildren(content),
				IPanel.ofBorder(IPanel.Border.NORMAL).withTitle("Title").withPanelchildren(content)
		);
	}

	@RichletMapping("/border")
	public IComponent border() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withBorder(IPanel.Border.NORMAL),
				IButton.of("change border").withAction(this::changeBorder)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBorder() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().border("none"));
	}

	@RichletMapping("/bottomToolbar")
	public IComponent bottomToolbar() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.of(content).withBottomToolbar(IToolbar.PANEL).withBorder(IPanel.Border.NORMAL),
				IPanel.of(content).withBorder(IPanel.Border.NORMAL)
		);
	}

	@RichletMapping("/caption")
	public IPanel caption() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IPanel.ofId("panel").withPanelchildren(content).withCaption(ICaption.of("panel caption"))
				.withBorder(IPanel.Border.NORMAL);
	}

	@RichletMapping("/closable")
	public IComponent closable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withTitle("panel").withPanelchildren(content)
						.withClosable(true).withCollapsible(true),
				IButton.of("change closable").withAction(this::changeClosable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeClosable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().closable(false));
	}

	@RichletMapping("/collapsible")
	public IComponent collapsible() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withTitle("panel").withPanelchildren(content).withCollapsible(true),
				IButton.of("change collapsible").withAction(this::changeCollapsible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCollapsible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().collapsible(false));
	}

	@RichletMapping("/floatable")
	public IComponent floatable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withFloatable(true).withTitle("panel"),
				IButton.of("change floatable").withAction(this::changeFloatable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeFloatable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().floatable(false));
	}

	@RichletMapping("/footToolbar")
	public IPanel footToolbar() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IPanel.ofId("panel").withPanelchildren(content)
				.withFootToolbar(IToolbar.PANEL).withBorder(IPanel.Border.NORMAL);
	}

	@RichletMapping("/maximizable")
	public IComponent maximizable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withMaximized(false).withMaximizable(true).withTitle("panel"),
				IButton.of("change maximizable").withAction(this::changeMaximizable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaximizable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().maximizable(false));
	}

	@RichletMapping("/maximized")
	public IComponent maximized() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withMaximizable(true).withMaximized(true).withTitle("panel"),
				IButton.of("change maximized").withAction(this::changeMaximized)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaximized() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().maximized(false));
	}

	@RichletMapping("/minheight")
	public IComponent minheight() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withSizable(true).withMinheight(200),
				IButton.of("change minheight").withAction(this::changeMinheight)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinheight() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().minheight(300));
	}

	@RichletMapping("/minimizable")
	public IComponent minimizable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withMinimized(false).withMinimizable(true).withTitle("panel"),
				IButton.of("change minimizable").withAction(this::changeMinimizable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinimizable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().minimizable(false));
	}

	@RichletMapping("/minimized")
	public IComponent minimized() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withMinimizable(true).withTitle("panel"),
				IButton.of("change minimized").withAction(this::changeMinimized)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinimized() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().minimized(true).minimizable(true));
	}

	@RichletMapping("/minwidth")
	public IComponent minwidth() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withSizable(true).withMinwidth(200),
				IButton.of("change minwidth").withAction(this::changeMinwidth)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinwidth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().minwidth(100));
	}

	@RichletMapping("/movable")
	public IComponent movable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withFloatable(true).withMovable(true),
				IButton.of("change movable").withAction(this::changeMovable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMovable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().movable(false));
	}

	@RichletMapping("/nativeScrollbar")
	public IComponent nativeScrollbar() {
		ILabel label = ILabel.of("Content1").withStyle("display: block;");
		IPanelchildren<ILabel> content = IPanelchildren.of(label, label, label).withStyle("overflow: scroll;");
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withNativeScrollbar(false).withHeight("50px"),
				IButton.of("change nativeScrollbar").withAction(this::changeNativeScrollbar)
		);
	}

	@RichletMapping("/nativeScrollbar")
	public IComponent nativeScrollbar2() {
		ILabel label = ILabel.of("Content1").withStyle("display: block;");
		IPanelchildren<ILabel> content = IPanelchildren.of(label, label, label).withStyle("overflow: scroll;");
		return IHlayout.of(
				IPanel.ofId("panel2").withPanelchildren(content).withNativeScrollbar(true).withHeight("50px")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeNativeScrollbar() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().nativeScrollbar(true));
	}

	@RichletMapping("/open")
	public IComponent open() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withOpen(false),
				IButton.of("change open").withAction(this::changeOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().open(true));
	}

	@RichletMapping("/panelchildren")
	public IPanel panelchildren() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content1"), ILabel.of("Content2"));
		return IPanel.ofId("panel").withPanelchildren(content);
	}

	@RichletMapping("/sizable")
	public IComponent sizable() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withSizable(true),
				IButton.of("change sizable").withAction(this::changeSizable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSizable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().sizable(false));
	}

	@RichletMapping("/title")
	public IComponent title() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IHlayout.of(
				IPanel.ofId("panel").withPanelchildren(content).withTitle("panel"),
				IButton.of("change title").withAction(this::changeTitle)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTitle() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("panel"), new IPanel.Updater().title("new panel"));
	}

	@RichletMapping("/topToolbar")
	public IComponent topToolbar() {
		IPanelchildren<ILabel> content = IPanelchildren.of(ILabel.of("Content"));
		return IPanel.ofId("panel").withPanelchildren(content).withTopToolbar(IToolbar.PANEL);
	}
}
