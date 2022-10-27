/* IARichlet.java

		Purpose:

		Description:

		History:
				Tue Jan 04 11:40:47 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IA;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link IA} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/A">IA</a>,
 * if any.
 * @author leon
 * @see IA
 */
@RichletMapping("/essential_components/ia")
public class IARichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IHlayout.of(
				IA.of("Visit ZK!").withHref("https://www.zkoss.org"),
				IA.of("jump to uri").withHref("uri")
		);
	}

	@RichletMapping("/uri")
	public IComponent uri() {
		return IHlayout.of(
				IButton.of("change uri").withAction(this::changeHref),
				IA.of("jump to example (slash)").withHref("/essential_components/ia/example"),
				IA.of("jump to example").withHref("example").withId("ia")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHref() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ia"), new IA.Updater().href("href"));
	}

	@RichletMapping("/autodisable")
	public IComponent autodisable() {
		return IHlayout.of(
				IButton.of("change autodisable").withAction(this::changeAutodisable),
				IA.ofId("ok").withLabel("OK").withAutodisable("ok,cancel"),
				IA.ofId("ok2").withLabel("OK(self)").withAutodisable("self,cancel"),
				IA.ofId("ok3").withLabel("OK(action)").withAutodisable("self,+cancel").withAction(this::control),
				IA.ofId("cancel").withLabel("CANCEL")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodisable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ok"), new IA.Updater().autodisable(null));
	}

	@Action(type = Events.ON_BLUR)
	public void control() {
		Clients.log("control by action ON_BLUR");
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cancel"), new IA.Updater().disabled(false));
	}

	@RichletMapping("/dir")
	public IComponent dir() {
		return IVlayout.of(
				IButton.of("change dir").withAction(this::changeDir),
				IA.of("normal").withImage("/stateless/ZK-Logo.gif").withDir("normal").withId("ia"),
				IA.of("reverse").withImage("/stateless/ZK-Logo.gif").withDir("reverse")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDir() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ia"), new IA.Updater().dir("reverse"));
	}

	@RichletMapping("/dir2")
	public IComponent dir2() {
		return IVlayout.of(
				IA.of("normal").withImage("/stateless/ZK-Logo.gif").withDir(IA.Direction.NORMAL),
				IA.of("reverse").withImage("/stateless/ZK-Logo.gif").withDir(IA.Direction.REVERSE)
		);
	}

	@RichletMapping("/disabled")
	public IComponent disabled() {
		return IVlayout.of(
				IButton.of("change disabled").withAction(this::changeDisabled),
				IA.of("disabled").withDisabled(true).withAction(this::doClick).withId("ia")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ia"), new IA.Updater().disabled(false));
	}

	@Action(type = Events.ON_CLICK)
	public void doClick() {
		Clients.log("doClick");
	}

	@RichletMapping("/target")
	public IComponent target() {
		return IVlayout.of(
				IButton.of("change target").withAction(this::changeTarget),
				IA.of("target").withHref("/stateless/ZK-Logo.gif").withTarget("_blank").withId("ia")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTarget() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ia"), new IA.Updater().target("_parent"));
	}

	@RichletMapping("/target2")
	public IComponent target2() {
		return IA.of("target").withHref("/stateless/ZK-Logo.gif").withTarget(IA.Target.BLANK);
	}

	@RichletMapping("/focus")
	public IComponent focus() {
		return IA.ofId("focus").withLabel("doFocus").withAction(this::doFocus);
	}

	@Action(type = Events.ON_FOCUS)
	public void doFocus() {
		Clients.log("doFocus");
	}

	@RichletMapping("/blur")
	public IComponent blur() {
		return IA.ofId("blur").withLabel("doBlur").withAction(this::doBlur);
	}

	@Action(type = Events.ON_BLUR)
	public void doBlur() {
		Clients.log("doBlur");
	}
}
