/* ICenterRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:21:49 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.ICenter;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ICenter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/Center">ICenter</a>,
 * if any.
 *
 * @author katherine
 * @see ICenter
 */
@RichletMapping("/data/iCenter")
public class ICenterRichlet implements StatelessRichlet {
	@RichletMapping("/collapsible")
	public IComponent collapsible() {
		return IDiv.of(IButton.of("check not allow to set collapsible").withAction(this::doCollapsibleError));
	}

	@Action(type = Events.ON_CLICK)
	public void doCollapsibleError() {
		ICenter.DEFAULT.withCollapsible(true);
	}

	@RichletMapping("/height")
	public IComponent height() {
		return IDiv.of(IButton.of("check not allow to set height").withAction(this::doHeightError));
	}

	@Action(type = Events.ON_CLICK)
	public void doHeightError() {
		ICenter.DEFAULT.withHeight("100px");
	}

	@RichletMapping("/maxsize")
	public IComponent maxsize() {
		return IDiv.of(IButton.of("check not allow to set maxsize").withAction(this::doMaxsizeError));
	}

	@Action(type = Events.ON_CLICK)
	public void doMaxsizeError() {
		ICenter.DEFAULT.withMaxsize(1);
	}

	@RichletMapping("/minsize")
	public IComponent minsize() {
		return IDiv.of(IButton.of("check not allow to set minsize").withAction(this::doMinsizeError));
	}

	@Action(type = Events.ON_CLICK)
	public void doMinsizeError() {
		ICenter.DEFAULT.withMinsize(1);
	}

	@RichletMapping("/open")
	public IComponent open() {
		return IDiv.of(IButton.of("check not allow to set open").withAction(this::doOpenError));
	}

	@Action(type = Events.ON_CLICK)
	public void doOpenError() {
		ICenter.DEFAULT.withOpen(true);
	}

	@RichletMapping("/slidable")
	public IComponent slidable() {
		return IDiv.of(IButton.of("check not allow to set slidable").withAction(this::doSlidableError));
	}

	@Action(type = Events.ON_CLICK)
	public void doSlidableError() {
		ICenter.DEFAULT.withSlidable(true);
	}

	@RichletMapping("/slide")
	public IComponent slide() {
		return IDiv.of(IButton.of("check not allow to set slide").withAction(this::doSlideError));
	}

	@Action(type = Events.ON_CLICK)
	public void doSlideError() {
		ICenter.DEFAULT.withSlide(true);
	}

	@RichletMapping("/splittable")
	public IComponent splittable() {
		return IDiv.of(IButton.of("check not allow to set splittable").withAction(this::doSplittableError));
	}

	@Action(type = Events.ON_CLICK)
	public void doSplittableError() {
		ICenter.DEFAULT.withSplittable(true);
	}

	@RichletMapping("/visible")
	public IComponent visible() {
		return IDiv.of(IButton.of("check not allow to set visible").withAction(this::doVisibleError));
	}

	@Action(type = Events.ON_CLICK)
	public void doVisibleError() {
		ICenter.DEFAULT.withVisible(true);
	}

	@RichletMapping("/width")
	public IComponent width() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		ICenter.DEFAULT.withWidth("100px");
	}

}