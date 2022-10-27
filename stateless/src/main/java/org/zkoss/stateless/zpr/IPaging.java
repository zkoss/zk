/* IPaging.java

	Purpose:

	Description:

	History:
		5:56 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.immutables.value.Value;

import org.zkoss.stateless.action.data.PagingData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Paging;

/**
 * Immutable {@link org.zkoss.zul.Paging} component
 *
 * <p>A paging component is used with another component to separate long content
 * into multiple pages. If a component has long content to display,
 * you could separate them into pages, and then use a paging component as a controller
 * to allow the user decide which page to display.</p>
 *
 * <p> The {@link IListbox listbox}, {@link IGrid grid} and {@link ITree tree}
 * components support the {@code paging} intrinsically, so you don't need
 * to specify a {@code paging} component explicitly. In other words, they will instantiate
 * and manage a {@code paging} component automatically if the {@code paging} mold
 * is specified. Of course, you could specify an external paging component,
 * if you want to have different visual layout, or to control multiple {@code listboxes},
 * {@code grids} and/or {@code trees} with one single {@code paging} component.</p>
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onPaging</td>
 *          <td><strong>ActionData</strong>: {@link PagingData}
 *          <br> Represents an action triggered by the user navigates a new page.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Molds</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "default"}</td>
 *          <td><img src="doc-files/IPaging_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "os"}</td>
 *          <td><img src="doc-files/IPaging_mold_os.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <br>
 * For example, suppose you have 100 items and prefer to show 20 items at a time,
 * then you can use the paging components as follows.
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("")
 * public IComponent index() {
 *     return IVlayout.of(IPaging.ofTotalSize(100));
 * }
 * </code>
 * </pre>
 * @author jumperchen
 * @see Paging
 */
@StatelessStyle
public interface IPaging extends IXulElement<IPaging>,
		IDisable<IPaging>, IMeshComposite<IPaging>, IAnyGroup<IPaging> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IPaging DEFAULT = new IPaging.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Paging> getZKType() {
		return Paging.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.mess.Paging"}</p>
	 */
	default String getWidgetClass() {
		return "zul.mesh.Paging";
	}

	/** Returns the number of items per page.
	 * <p>Default: {@code 20}.</p>
	 */
	default int getPageSize() {
		return IPagingCtrl.PAGE_SIZE;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pageSize}.
	 *
	 * <p>Sets the items to show in each page</p>
	 * @param pageSize The number of items per page. (positive only)
	 * <p>Default: {@code 20}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withPageSize(int pageSize);

	/** Returns the total number of items.
	 * <p>Default: {@code 0}</p>
	 */
	default int getTotalSize() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code totalSize}.
	 *
	 * <p>To specify the total size of the {@code paging} component</p>
	 * @param totalSize The total size of the {@code paging} component. (non-negative only)
	 * <p>Default: {@code 0}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withTotalSize(int totalSize);

	/** Returns the number of pages.
	 * <p><b>Note:</b> there is at least one page even no item at all.</p>
	 * <p>Default: {@code 1}</p>
	 */
	default int getPageCount() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pageCount}.
	 *
	 * <p>Sets the number of pages</p>
	 *
	 * <p><b>Note:</b> there is at least one page even no item at all.</p>
	 *
	 * @param pageCount The number of pages.
	 * <p>Default: {@code 1}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withPageCount(int pageCount);

	/**
	 * Returns whether to show the detailed info, such as {@link #getTotalSize()}
	 * <p>Default: {@code false}</p>
	 */
	default boolean isDetailed() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code detailed}.
	 *
	 * <p>Sets whether to show total size and index of items in current page</p>
	 * @param detailed {@code true} if enables the detail information.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withDetailed(boolean detailed);

	/**
	 * Returns whether to automatically hide the paging if
	 * there is only one page available.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isAutohide() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code autohide}.
	 *
	 * <p>Sets whether to automatically hide the {@code paging} component at client
	 * if there is only one page available.</p>
	 *
	 * @param autohide {@code true} if enables the auto-hide feature.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withAutohide(boolean autohide);

	/** Returns the active page (starting from 0).
	 * <p>Default: {@code 0}</p>
	 */
	default int getActivePage() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code activePage}.
	 *
	 * <p>Sets the active page (starting from 0)</p>
	 *
	 * <p><b>Note:</b> In server side, the active page starts from {@code 0}.
	 * But in browser UI, it starts from {@code 1} for human to see.
	 * @param activePage The active page to display at client.
	 * <p>Default: {@code 0}</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withActivePage(int activePage);

	/** Returns the number of page anchors shall appear at the client.
	 *
	 * <p>Default: {@code 10}.</p>
	 */
	default int getPageIncrement() {
		return 10;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code pageIncrement}.
	 *
	 * <p>Sets the number of page list icon when the {@code mold} is {@code "os"}</p>
	 * @param pageIncrement The number of page list icon when the {@code mold} is {@code "os"} (Non-positive only)
	 * <p>Default: {@code 10}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IPaging withPageIncrement(int pageIncrement);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkPageIncrement() {
		int pginc = getPageIncrement();
		if (pginc <= 0) {
			throw new WrongValueException("Nonpositive is not allowed: " + pginc);
		}
		if (10 != getPageIncrement() && !"os".equals(getMold())) {
			throw new WrongValueException("Only \"os\" mold is allowed for pageIncrement");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkPageSize() {
		if (getPageSize() <= 0) {
			throw new WrongValueException("positive only");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkTotalSize() {
		if (getTotalSize() < 0) {
			throw new WrongValueException("non-negative only");
		}
	}

	/**
	 * Returns the instance with the given total size.
	 * @param totalSize The total size of this paging component.
	 */
	static IPaging ofTotalSize(int totalSize) {
		return new IPaging.Builder().setTotalSize(totalSize).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IPaging ofId(String id) {
		return new IPaging.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	// super
	default void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		IXulElement.super.renderProperties(renderer);

		int _ttsz = getTotalSize();
		if (_ttsz != 0)
			renderer.render("totalSize", _ttsz);

		int _pgsz = getPageSize();
		if (_pgsz != 20)
			renderer.render("pageSize", _pgsz);

		int _actpg = getActivePage();
		if (_actpg != 0)
			renderer.render("activePage", _actpg);

		int _npg = getPageCount();
		if (_npg != 1)
			renderer.render("pageCount", _npg);

		int _pginc = getPageIncrement();
		if (_pginc != 10)
			renderer.render("pageIncrement", _pginc);

		render(renderer, "detailed", isDetailed());
		render(renderer, "autohide", isAutohide());
		render(renderer, "disabled", isDisabled());
	}

	/**
	 * Builds instances of type {@link IPaging IPaging}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em></p>
	 */
	class Builder extends ImmutableIPaging.Builder {

	}

	/**
	 * Builds an updater of type {@link IPaging} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IPagingUpdater {}
}
