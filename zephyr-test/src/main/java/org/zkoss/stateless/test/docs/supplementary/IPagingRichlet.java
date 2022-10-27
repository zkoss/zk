/* IPagingRichlet.java

	Purpose:

	Description:

	History:
		3:37 PM 2021/12/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.supplementary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.stateless.action.ActionType;
import org.zkoss.stateless.action.data.PagingData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IPaging;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.event.ZulEvents;

/**
 * A set of examples for {@link IPaging} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Paging">IPaging</a>,
 * if any.
 * @author jumperchen
 * @see IPaging
 */
@RichletMapping("/supplementary/ipaging")
public class IPagingRichlet implements StatelessRichlet {

	@RichletMapping("")
	public IComponent index() {
		return IVlayout.of(IPaging.ofTotalSize(100));
	}

	@RichletMapping("/detailed")
	public IComponent detailed() {
		return IPaging.ofTotalSize(100).withDetailed(true);
	}

	@RichletMapping("/disabled")
	public List<IComponent> disabled() {
		return Arrays.asList(
				IButton.of("change disabled").withAction(this::changeDisabled),
				IPaging.DEFAULT.withPageSize(2).withDisabled(true).withId("paging").withTotalSize(100)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().disabled(false));
	}

	@RichletMapping("/pagingEvent")
	public IComponent pagingEvent() {
		int pageSize = 20; // default
		Iterable<ILabel> labels = IntStream.range(0, pageSize)
				.mapToObj(index -> ILabel.of(String.valueOf(index))).collect(
						Collectors.toList());
		return IVlayout.of(IVlayout.ofId("context").withChildren(labels),
				IPaging.ofId("paging").withTotalSize(100).withActions(
						ActionType.onPaging(this::onPaging)));
	}

	@Action(type = ZulEvents.ON_PAGING)
	public void onPaging(UiAgent uiAgent, PagingData pagingData) {
		int pageSize = pagingData.getPageSize();
		int activePage = pagingData.getActivePage();
		List<ILabel> labels = IntStream.range(activePage * pageSize,
						(activePage + 1) * pageSize)
				.mapToObj(index -> ILabel.of(String.valueOf(index))).collect(Collectors.toList());
		uiAgent.replaceChildren(Locator.ofId("context"), labels);
	}

	@RichletMapping("/os")
	public List<IComponent> osMold() {
		return Arrays.asList(
				IButton.of("change mold").withAction(this::changeMold),
				IPaging.ofTotalSize(100).withMold("os").withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMold() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().mold("default"));
	}

	@RichletMapping("/id")
	public List<IComponent> id() {
		return Arrays.asList(
				IButton.of("change id").withAction(this::changeId),
				IPaging.ofId("mypaging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeId() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("mypaging"), new IPaging.Updater().id("mypaging2"));
	}

	@RichletMapping("/totalSize")
	public List<IComponent> totalSize() {
		return Arrays.asList(
				IButton.of("change totalSize").withAction(this::changeTotalSize),
				IPaging.ofTotalSize(100).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTotalSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().totalSize(50));
	}

	@RichletMapping("/pageCount")
	public List<IComponent> pageCount() {
		return Arrays.asList(
				IButton.of("change pageCount").withAction(this::changePageCount),
				IPaging.DEFAULT.withPageCount(10).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePageCount() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().pageCount(20));
	}

	@RichletMapping("/autohide")
	public List<IComponent> autohide() {
		return Arrays.asList(
				IButton.of("change autohide").withAction(this::changeAutohide),
				IPaging.DEFAULT.withAutohide(true).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutohide() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().autohide(false));
	}

	@RichletMapping("/pageIncrement")
	public List<IComponent> pageIncrement() {
		return Arrays.asList(
				IButton.of("change pageIncrement").withAction(this::changePageIncrement),
				IPaging.ofTotalSize(100).withMold("os").withPageIncrement(5).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePageIncrement() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().mold("os").pageIncrement(3));
	}

	@RichletMapping("/activePage")
	public List<IComponent> activePage() {
		return Arrays.asList(
				IButton.of("change activePage").withAction(this::changeActivePage),
				IPaging.ofTotalSize(100).withActivePage(2).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeActivePage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().activePage(3));
	}

	@RichletMapping("/pageSize")
	public List<IComponent> pageSize() {
		return Arrays.asList(
				IButton.of("change pageSize").withAction(this::changePageSize),
				IPaging.ofTotalSize(100).withPageSize(5).withId("paging")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePageSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("paging"), new IPaging.Updater().pageSize(10));
	}

}
