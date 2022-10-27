/* IListboxRichlet.java

	Purpose:

	Description:

	History:
		3:33 PM 2022/3/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IAuxhead;
import org.zkoss.stateless.zpr.IAuxheader;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IFrozen;
import org.zkoss.stateless.zpr.IListbox;
import org.zkoss.stateless.zpr.IListcell;
import org.zkoss.stateless.zpr.IListfoot;
import org.zkoss.stateless.zpr.IListfooter;
import org.zkoss.stateless.zpr.IListheader;
import org.zkoss.stateless.zpr.IListitem;
import org.zkoss.statelessex.state.IListboxController;
import org.zkoss.statelessex.zpr.IListgroup;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.RowComparator;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link IListbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox">IListbox</a>,
 * if any.
 *
 * @author jumperchen
 * @see IListbox
 */
@RichletMapping("/data/ilistbox")
public class IListboxRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IListbox defaultMold() {
		return IListbox.ofListheaders(IListheader.of("Name"), IListheader.of("Gender"))
				.withChildren(
						IListitem.of(IListcell.of("Mary"), IListcell.of("FEMALE")),
						IListitem.of(IListcell.of("John"), IListcell.of("MALE")),
						IListitem.of(IListcell.of("Jane"),IListcell.of("FEMALE")),
						IListitem.of(IListcell.of("Henry"),IListcell.of("MALE"))
				).withListfoot(IListfoot.of(
						IListfooter.of("This is footer1"),
						IListfooter.of("This is footer2")
						)
				).withWidth("450px");
	}

	@RichletMapping("/mold/paging")
	public IListbox pagingMold() {
		IListbox iListbox = defaultMold().withMold("paging");
		return iListbox.withPagingChild(iListbox.getPagingChild().withPageSize(2));
	}

	@RichletMapping("/paging")
	public IListbox paging() {
		IListbox iListbox = defaultMold().withMold("paging");
		return iListbox.withPagingChild(iListbox.getPagingChild().withPageSize(3));
	}

	@RichletMapping("/children")
	public IComponent children() {
		return IListbox.ofListheaders(IListheader.of("Name"), IListheader.of("Gender"), IListheader.of("Account Number"))
				.withAuxhead(IAuxhead.of(
						IAuxheader.of("MEMBER").withColspan(2),
						IAuxheader.of("DETAIL"))
				).withChildren(
						IListitem.of(IListcell.of("Mary"), IListcell.of("FEMALE"), IListcell.of("001")),
						IListitem.of(IListcell.of("John"), IListcell.of("MALE"), IListcell.of("002")),
						IListitem.of(IListcell.of("Jane"),IListcell.of("FEMALE"), IListcell.of("003")),
						IListitem.of(IListcell.of("Henry"),IListcell.of("MALE"), IListcell.of("004"))
				).withListfoot(IListfoot.of(
								IListfooter.of("This is footer1"),
								IListfooter.of("This is footer2")
						)
				).withFrozen(IFrozen.ofColumns(2)).withWidth("450px");
	}

	@RichletMapping("/autosort")
	public IComponent autosort() {
		IListheader header = IListheader.of("head").withSortDirection(IListheader.SortDirection.ASCENDING)
				.withSortAscending(new RowComparator(1, true, true));
		IListbox listbox = IListbox.ofListheaders(header).withAutosort(IListbox.Autosort.ENABLE)
				.withHeight("500px").withId("listbox");
		return IListboxController.of(listbox, initGridModel()).build();
	}

	private SimpleListModel initGridModel() {
		String[] data = new String[3];
		data[0] = "2";
		data[1] = "1";
		data[2] = "3";
		return new SimpleListModel(data);
	}

	@RichletMapping("/checkmark")
	public List<IComponent> checkmark() {
		return Arrays.asList(
				IListbox.of(IListitem.of("item").withLabel("item").withLabel("item"))
						.withCheckmark(true).withId("listbox"),
				IButton.of("change checkmark").withAction(this::changeCheckmark)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCheckmark() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().checkmark(false));
	}

	@RichletMapping("/checkmarkDeselectOther")
	public List<IComponent> checkmarkDeselectOther() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item1"), IListitem.of("item2"))
						.withCheckmarkDeselectOther(true).withId("listbox")
						.withMultiple(true).withCheckmark(true),
				IButton.of("change checkmarkDeselectOther").withAction(this::changeCheckmarkDeselectOther)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCheckmarkDeselectOther() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().checkmarkDeselectOther(false));
	}

	@RichletMapping("/innerWidth")
	public List<IComponent> innerWidth() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item")).withInnerWidth("100px").withId("listbox"),
				IButton.of("change innerWidth").withAction(this::changeInnerWidth)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeInnerWidth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().innerWidth("200px"));
	}

	@RichletMapping("/listgroupSelectable")
	public List<IComponent> listgroupSelectable() {
		return Arrays.asList(
				IListbox.of(IListgroup.of(IListcell.of("group")), IListitem.ofId("listitem").withLabel("item"))
						.withListgroupSelectable(true).withId("listbox")
						.withMultiple(true).withCheckmark(true),
				IButton.of("change listgroupSelectable").withAction(this::changeListgroupSelectable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeListgroupSelectable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().listgroupSelectable(false));
	}

	@RichletMapping("/multiple")
	public List<IComponent> multiple() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item"))
						.withMultiple(true).withCheckmark(true).withId("listbox"),
				IButton.of("change multiple").withAction(this::changeMultiple)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMultiple() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().multiple(false));
	}

	@RichletMapping("/nonselectableTags")
	public List<IComponent> nonselectableTags() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item").withChildren(
								IListcell.of(IButton.DEFAULT),
								IListcell.of(IButton.DEFAULT)))
						.withCheckmark(true)
						.withNonselectableTags("*")
						.withId("listbox"),
				IButton.of("change nonselectableTags").withAction(this::changeNonselectableTags)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeNonselectableTags() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().nonselectableTags("input"));
	}

	@RichletMapping("/rightSelect")
	public List<IComponent> rightSelect() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item")).withRightSelect(false)
						.withCheckmark(true).withId("listbox"),
				IButton.of("change rightSelect").withAction(this::changeRightSelect)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRightSelect() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().rightSelect(true));
	}

	@RichletMapping("/selectOnHighlightDisabled")
	public List<IComponent> selectOnHighlightDisabled() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item"))
						.withSelectOnHighlightDisabled(true).withId("listbox"),
				IButton.of("change selectOnHighlightDisabled").withAction(this::changeSelectOnHighlightDisabled)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSelectOnHighlightDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().selectOnHighlightDisabled(false));
	}

	@RichletMapping("/rows")
	public List<IComponent> rows() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("listitem").withLabel("item1"), IListitem.of("item2")).withRows(1).withId("listbox"),
				IButton.of("change rows").withAction(this::changeRows)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRows() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listbox"), new IListbox.Updater().rows(2));
	}
}
