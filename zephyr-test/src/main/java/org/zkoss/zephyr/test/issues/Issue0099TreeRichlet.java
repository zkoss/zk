/* Issue0099TreeRichlet.java

	Purpose:

	Description:

	History:
		3:38 PM 2022/2/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Self;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.stateless.zpr.ITreecell;
import org.zkoss.stateless.zpr.ITreechildren;
import org.zkoss.stateless.zpr.ITreecol;
import org.zkoss.stateless.zpr.ITreecols;
import org.zkoss.stateless.zpr.ITreeitem;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0099")
public class Issue0099TreeRichlet implements StatelessRichlet {
	@RichletMapping("/createOnOpen")
	public IComponent createOnOpen() {
		return ITree.DEFAULT.withWidth("400px")
				.withTreecols(
						ITreecols.of(
								ITreecol.of("Subject"),
								ITreecol.of("From")
						)
				).withTreechildren(
						ITreechildren.of(
								ITreeitem.ofTreecells(
										ITreecell.of("Intel Snares XML"),
										ITreecell.of("David Needle")
								).withTreechildren(
										ITreechildren.DEFAULT
								).withOpen(false).withAction(this::doCreateOnOpen)
						)
				);
	}
	@Action(type = Events.ON_OPEN)
	public void doCreateOnOpen(Self self, @ActionVariable(targetId = SELF, field = "empty") boolean isEmpty) {
		if (isEmpty) {
			UiAgent.getCurrent()
					.replaceChildren(self.findChild(ITreechildren.class),
							ITreeitem.of("New added"));
		}
	}
}
