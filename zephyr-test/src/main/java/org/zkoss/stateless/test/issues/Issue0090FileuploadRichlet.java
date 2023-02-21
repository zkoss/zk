/* Issue0090FileuploadRichlet.java

	Purpose:
		
	Description:
		
	History:
		2:42 PM 2022/1/13, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import org.zkoss.stateless.action.data.FileData;
import org.zkoss.stateless.annotation.Action;
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
@RichletMapping("/stateless/issue0090")
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
