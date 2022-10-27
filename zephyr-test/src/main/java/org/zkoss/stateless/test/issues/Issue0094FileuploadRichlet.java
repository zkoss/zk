/* Issue0094FileuploadRichlet.java

	Purpose:
		
	Description:
		
	History:
		2:42 PM 2022/1/13, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import java.util.List;

import org.zkoss.util.media.Media;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0094")
public class Issue0094FileuploadRichlet implements StatelessRichlet {

	@RichletMapping("")
	public IComponent index() {
		return IDiv.of(ILabel.ofId("msg"),
				IButton.ofId("upload").withLabel("Upload").withUpload("true"),
				IButton.of("Submit").withAction(this::doFileupload));
	}

	@Action(type = Events.ON_CLICK)
	public void doFileupload(@ActionVariable(targetId = "upload", field = "file") List<Media> data) {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("msg"), new ILabel.Updater().value(data.get(0).getName()));
	}
}
