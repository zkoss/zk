/* ISignatureRichlet.java

	Purpose:

	Description:

	History:
		3:23 PM 2022/2/23, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.image.AImage;
import org.zkoss.zephyr.action.data.FileData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IImage;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zephyrex.zpr.ISignature;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.ISignature} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Radiogroup">IRadiogroup</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyrex.zpr.ISignature
 */
@RichletMapping("/input/isignature")
public class ISignatureRichlet implements StatelessRichlet {
	@RichletMapping("/example")
	public IComponent example() {
		return ISignature.ofSize("600px", "300px")
				.withPenColor("white").withBackgroundColor("#AED6F1")
				.withPenSize(6);
	}

	@RichletMapping("/example/upload")
	public IComponent uploadExample() {
		return IVlayout.of(
				ISignature.ofSize("600px", "300px").withAction(this::doUpload), IImage.ofId("image"));
	}

	@Action(type= org.zkoss.zkmax.ui.event.Events.ON_SAVE)
	public void doUpload(UiAgent uiAgent, FileData data) {
		uiAgent.smartUpdate(Locator.ofId("image"), new IImage.Updater().content((AImage) data.getMedia()));
	}

	@RichletMapping("/backgroundColor")
	public List<IComponent> backgroundColor() {
		return Arrays.asList(
				ISignature.ofSize("600px", "300px").withId("signature").withBackgroundColor("#b3e0ff"),
				IButton.of("change backgroundColor").withAction(this::changeBackgroundColor)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBackgroundColor() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater()
				.backgroundColor("#b3e000"));
	}

	@RichletMapping("/backgroundImage")
	public List<IComponent> backgroundImage() {
		return Arrays.asList(
				init().withBackgroundImage("/zephyr-test/zephyr/ZK-Logo-old.gif"),
				IButton.of("change backgroundImage").withAction(this::changeBackgroundImage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBackgroundImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater()
				.backgroundImage("/zephyr-test/zephyr/ZK-Logo.gif"));
	}

	@RichletMapping("/backgroundIncluded")
	public List<IComponent> backgroundIncluded() {
		return Arrays.asList(
				init().withBackgroundColor("AED6F1").withBackgroundIncluded(false)
						.withAction(this::doUpload), IImage.ofId("image"),
				IButton.of("change backgroundIncluded").withAction(this::changeBackgroundIncluded)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBackgroundIncluded() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().backgroundIncluded(true));
	}

	@RichletMapping("/clearLabel")
	public List<IComponent> clearLabel() {
		return Arrays.asList(
				init().withClearLabel("clear"),
				IButton.of("change clearLabel").withAction(this::changeClearLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeClearLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().clearLabel("clear2"));
	}

	@RichletMapping("/penColor")
	public List<IComponent> penColor() {
		return Arrays.asList(
				init().withPenColor("blue"),
				IButton.of("change penColor").withAction(this::changePenColor)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePenColor() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().penColor("black"));
	}

	@RichletMapping("/penSize")
	public List<IComponent> penSize() {
		return Arrays.asList(
				init().withPenSize(2),
				IButton.of("change penSize").withAction(this::changePenSize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePenSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().penSize(1));
	}

	@RichletMapping("/saveLabel")
	public List<IComponent> saveLabel() {
		return Arrays.asList(
				init().withSaveLabel("save"),
				IButton.of("change saveLabel").withAction(this::changeSaveLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSaveLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().saveLabel("save2"));
	}

	@RichletMapping("/saveType")
	public List<IComponent> saveType() {
		return Arrays.asList(
				init().withSaveType("image/jpg"),
				IButton.of("change saveType").withAction(this::changeSaveType)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSaveType() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().saveType("image/png"));
	}

	@RichletMapping("/toolbarVisible")
	public List<IComponent> toolbarVisiblee() {
		return Arrays.asList(
				init().withToolbarVisible(false),
				IButton.of("change toolbarVisible").withAction(this::changeToolbarVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeToolbarVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().toolbarVisible(true));
	}

	@RichletMapping("/undoLabel")
	public List<IComponent> undoLabel() {
		return Arrays.asList(
				init().withUndoLabel("Undo"),
				IButton.of("change undoLabel").withAction(this::changeUndoLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeUndoLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("signature"), new ISignature.Updater().undoLabel("Undo2"));
	}
	
	private ISignature init() {
		return ISignature.ofSize("600px", "300px").withId("signature");
	}
}
