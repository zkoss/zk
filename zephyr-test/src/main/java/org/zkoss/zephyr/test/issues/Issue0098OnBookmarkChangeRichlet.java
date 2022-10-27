/* Issue0098OnBookmarkChangeRichlet.java

	Purpose:
		
	Description:
		
	History:
		4:34 PM 2022/8/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import org.zkoss.stateless.action.data.BookmarkData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0098")
public class Issue0098OnBookmarkChangeRichlet implements StatelessRichlet {
	@RichletMapping("")
	public IComponent index() {
		return IDiv.of(ILabel.ofId("msg")).withId("main");
	}

	@Action(from = "#main", type = Events.ON_BOOKMARK_CHANGE)
	public void doBookmarkChange(BookmarkData data) {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("msg"), new ILabel.Updater().value(data.getBookmark()));
	}
}
