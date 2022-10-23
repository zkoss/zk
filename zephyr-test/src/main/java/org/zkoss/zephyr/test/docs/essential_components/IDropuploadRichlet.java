/* IDropuploadRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 18:13:48 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyrex.zpr.IDropupload;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.IDropupload} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Dropupload">IDropupload</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IDropupload
 */
@RichletMapping("/essential_components/iDropupload")
public class IDropuploadRichlet implements StatelessRichlet {
	@RichletMapping("/anchorId")
	public List<IComponent> anchorId() {
		return Arrays.asList(
				IDiv.ofSize("100px", "100px").withStyle("border: 1px solid black").withId("div"),
				IDropupload.ofId("upload").withAnchorId("div"),
				IButton.of("change anchorId").withAction(this::changeAnchorId)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAnchorId() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().anchorId(null));
	}

	@RichletMapping("/content")
	public List<IComponent> content() {
		return Arrays.asList(
				IDropupload.ofId("upload").withContent("upload"),
				IButton.of("change content").withAction(this::changeContent)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContent() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().content("upload2"));
	}

	@RichletMapping("/detection")
	public List<IComponent> detection() {
		return Arrays.asList(
				IDropupload.ofId("upload").withDetection(IDropupload.Detection.SELF),
				IButton.of("change detection").withAction(this::changeDetection)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDetection() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().detection("browser"));
	}

	@RichletMapping("/maxFileCount")
	public List<IComponent> maxFileCount() {
		return Arrays.asList(
				IDropupload.ofId("upload").withMaxFileCount(2),
				IButton.of("change maxFileCount").withAction(this::changeMaxFileCount)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaxFileCount() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().maxFileCount(1));
	}

	@RichletMapping("/maxsize")
	public List<IComponent> maxsize() {
		return Arrays.asList(
				IDropupload.ofId("upload").withMaxsize(5000),
				IButton.of("change maxsize").withAction(this::changeMaxsize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaxsize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().maxsize(1000));
	}

	@RichletMapping("/suppressedErrors")
	public List<IComponent> suppressedErrors() {
		return Arrays.asList(
				IDropupload.ofId("upload").withSuppressedErrors("error"),
				IButton.of("change suppressedErrors").withAction(this::changeSuppressedErrors)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSuppressedErrors() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().suppressedErrors(null));
	}

	@RichletMapping("/viewerClass")
	public List<IComponent> viewerClass() {
		return Arrays.asList(
				IDropupload.ofId("upload").withViewerClass("class"),
				IButton.of("change viewerClass").withAction(this::changeViewerClass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeViewerClass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("upload"), new IDropupload.Updater().viewerClass(null));
	}
}