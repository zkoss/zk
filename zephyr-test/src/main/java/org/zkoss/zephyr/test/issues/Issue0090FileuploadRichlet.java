/* Issue0090FileuploadRichlet.java

	Purpose:
		
	Description:
		
	History:
		2:42 PM 2022/1/13, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import org.zkoss.zephyr.action.data.FileData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0090")
public class Issue0090FileuploadRichlet implements StatelessRichlet {

	@RichletMapping("")
	public IComponent index() {
		return IDiv.of(ILabel.ofId("msg"),
				IButton.of("Upload").withUpload("true").withAction(this::doFileupload));
	}

	@Action(type = Events.ON_UPLOAD)
	public void doFileupload(FileData data) {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("msg"), new ILabel.Updater().value(data.getMedia().getName()));
	}
}
