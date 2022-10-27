/* ITabbox.java

	Purpose:

	Description:

	History:
		Fri Oct 22 12:11:46 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.stateless.action.data.SelectData;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Tabbox;

/**
 * Immutable {@link Tabbox} component
 *
 * <p>A tabbox is a container used to display a set of tabbed groups of components.
 * A row of tabs is displayed at the top (or left or other location) of tabbox
 * which may be used to switch between each group. It allows developers to separate
 * a large number of components into several groups (each group is contained in a tabpanel).
 * Only one group is visible at the time, such that the user interface won't be too complicate
 * to read. Once the tab of an invisible group is clicked, it becomes visible and
 * the previous visible group becomes invisible.</p>
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
 *          <td>onSelect</td>
 *          <td><strong>ActionData</strong>: {@link SelectData}
 *          <br>Denotes user has selected a tab. onSelect is sent to both tab and tabbox.</td>
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
 *          <td><img src="doc-files/ITabbox_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "accordion"}</td>
 *          <td><img src="doc-files/ITabbox_mold_accordion.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h3>Support Orients</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Snapshot</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>{@code "top"}</td>
 *          <td><img src="doc-files/ITabbox_orient_top.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "left"}</td>
 *          <td><img src="doc-files/ITabbox_orient_left.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "right"}</td>
 *          <td><img src="doc-files/ITabbox_orient_right.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "bottom"}</td>
 *          <td><img src="doc-files/ITabbox_orient_bottom.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Tabbox
 */
@StatelessStyle
public interface ITabbox extends IXulElement<ITabbox>, IAnyGroup<ITabbox>,
		IComposite<ITabboxComposite> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITabbox DEFAULT = new ITabbox.Builder().build();

	/**
	 * Constant for accordion mold attributes of this immutable component.
	 */
	ITabbox ACCORDION = new ITabbox.Builder().setMold("accordion").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Tabbox> getZKType() {
		return Tabbox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.tab.Tabbox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.tab.Tabbox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkMold() {
		String orient = getOrient();
		String mold = getMold();
		if (("vertical".equals(orient) || "right".equals(orient)) && mold.startsWith("accordion")) {
			throw new WrongValueException("Unsupported vertical orient in mold : " + mold);
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkOrient() {
		String orient = getOrient();
		IComponentChecker.checkOrient(orient, "horizontal", "top", "bottom", "vertical", "right", "left");
		if (("vertical".equals(orient) || "right".equals(orient) || "left".equals(orient)))
			if (getToolbar() != null)
				throw new WrongValueException("Unsupported vertical orient when there is a toolbar");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITabbox updateChildren() {
		ITabs tabs = getTabs();
		ITabpanels tabpanels = getTabpanels();
		if (tabs != null && tabpanels != null) {
			List<ITab> tabList = tabs.getChildren();
			Builder builder = new Builder().from(this);
			boolean hasChanged = false;
			if (!tabList.isEmpty()) {
				ITab lastSelectedTab = tabList.stream().filter(ITab::isSelected).reduce((first, second) -> second).orElse(null);
				List<ITab> newTabs = new ArrayList<>(tabList);

				// by default, select the first one tab.
				if (lastSelectedTab == null) {
					hasChanged = true;
					newTabs.set(0, newTabs.get(0).withSelected(true));
				} else {
					// reset from the first tab to the one before the last selected tab;
					for (int i = 0, j = newTabs.size(); i < j; i++) {
						ITab currectTab = newTabs.get(i);
						if (currectTab == lastSelectedTab) {
							break;
						}
						ITab newTab = currectTab.withSelected(false); // reset
						if (newTab != currectTab) {
							newTabs.set(i, newTab);
							hasChanged = true;
						}
					}
				}
				builder.setTabs(tabs.withChildren(newTabs));
			}
			if (hasChanged) return builder.build();
		}
		return this;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default List<ITabboxComposite> getAllComponents() {
		ArrayList<ITabboxComposite> components = new ArrayList<>();
		ITabs iTabs = getTabs();
		if (iTabs != null) {
			components.add(iTabs);
		}
		ITabpanels iTabpanels = getTabpanels();
		if (iTabpanels != null) {
			components.add(iTabpanels);
		}
		IToolbar iToolbar = getToolbar();
		if (iToolbar != null) {
			components.add(iToolbar);
		}
		return components;
	}

	/**
	 * Returns the tabs that this tabbox owns.
	 */
	@Nullable
	ITabs getTabs();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code tabs}.
	 *
	 * <p> Sets the tabs that this tabbox owns.
	 * @param tabs The tabs child
	 *
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withTabs(@Nullable ITabs tabs);

	/**
	 * Returns the tabpanels that this tabbox owns.
	 */
	@Nullable
	ITabpanels getTabpanels();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code tabpanels}.
	 *
	 * <p> Sets the tabpanels that this tabbox owns.
	 * @param tabpanels The tabpanels child
	 *
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withTabpanels(@Nullable ITabpanels tabpanels);

	/**
	 * Returns the auxiliary toolbar that this tabbox owns.
	 */
	@Nullable
	IToolbar getToolbar();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code toolbar}.
	 *
	 * <p> Sets the auxiliary toolbar that this tabbox owns.
	 * @param toolbar The auxiliary toolbar child
	 *
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withToolbar(@Nullable IToolbar toolbar);

	/**
	 * Returns the spacing between {@link ITabpanel}. This is used by certain
	 * molds, such as {@code accordion}.
	 * <p>
	 * Default: {@code null} (no spacing).
	 */
	@Nullable
	String getPanelSpacing();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code panelSpacing}.
	 *
	 * <p> Sets the spacing between {@link ITabpanel}. This is used by certain
	 * molds, such as {@code accordion}.
	 *
	 * @param panelSpacing The spacing between {@link ITabpanel}
	 * <p>Default: {@code null} (no spacing)</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withPanelSpacing(@Nullable String panelSpacing);

	/**
	 * Returns the orient.
	 *
	 * <p>
	 * Default: {@code "top"}.
	 *
	 * <p>
	 * Note: only the {@code "default"} mold supports it (not supported if {@code "accordion"} mold).
	 */
	default String getOrient() {
		return "top";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of the tabs' position. Either {@code "top"}, {@code "left"}, {@code "bottom}
	 * or {@code "right"}.
	 *
	 * @param orient The orient of the tabs' position.
	 * <p>Default: {@code "top"}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of the tabs' position.
	 *
	 * @param orient The orient of the tabs' position.
	 * <p>Default: {@code "top"}</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ITabbox withOrient(Orient orient) {
		Objects.requireNonNull(orient);
		return withOrient(orient.value);
	}

	/**
	 * Returns whether the tab scrolling is enabled.
	 * Default: {@code true}.
	 */
	default boolean isTabscroll() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code tabscroll}.
	 *
	 * <p> Sets whether to enable the tab scrolling.
	 * When enabled, if tab list is wider than tab bar, left, right arrow will appear.
	 *
	 * @param tabscroll Whether to enable the tab scrolling.
	 * <p>Default: {@code true}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withTabscroll(boolean tabscroll);

	/**
	 * Returns whether to use maximum height of all tabpanel in initial phase.
	 * <p>
	 * Default: {@code false}.
	 */
	default boolean isMaximalHeight() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maximalHeight}.
	 *
	 * <p> Sets whether to use maximum height of all tabpanel in initial phase.
	 *
	 * @param maximalHeight Whether to use maximum height of all tabpanel in initial phase.
	 * <p>Default: {@code false}</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withMaximalHeight(boolean maximalHeight);

	/**
	 * Returns whether to use Browser's scrollbar or a floating scrollbar (if with {@code false}).
	 * <p>Default: {@code true} to use Browser's scrollbar, if the {@code "org.zkoss.zul.nativebar"}
	 * library property is not set in zk.xml.</p>
	 */
	default boolean isNativeScrollbar() {
		return Boolean.parseBoolean(
				Library.getProperty("org.zkoss.zul.nativebar", "true"));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code nativeScrollbar}.
	 *
	 * <p>Sets to use Browser's scrollbar or a floating scrollbar</p>
	 * @param nativeScrollbar {@code true} to use Browser's scrollbar, or {@code false} to
	 * use a floating scrollbar.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITabbox withNativeScrollbar(boolean nativeScrollbar);

	/**
	 * Returns the instance with the given tabs and tappanels
	 * @param tabs The tabs child
	 * @param tabpanels The tabpanels child.
	 */
	static ITabbox of(ITabs tabs, ITabpanels tabpanels) {
		return new ITabbox.Builder().setTabs(tabs).setTabpanels(tabpanels).build();
	}

	/**
	 * Returns the instance with the given tabs, tappanels, and toolbar
	 * @param tabs The tabs child
	 * @param tabpanels The tabpanels child.
	 * @param toolbar The auxiliary toolbar
	 */
	static ITabbox of(ITabs tabs, ITabpanels tabpanels, IToolbar toolbar) {
		return new ITabbox.Builder().setTabs(tabs).setTabpanels(tabpanels).setToolbar(toolbar).build();
	}

	/**
	 * Returns the instance with the given width and height
	 * @param width The width of the component
	 * @param height The height of the component
	 */
	static ITabbox ofSize(String width, String height) {
		return new ITabbox.Builder().setWidth(width).setHeight(height).build();
	}

	/**
	 * Returns the instance with the given {@link Orient orient}
	 * @param orient The orient of the tabs' position.
	 */
	static ITabbox ofOrient(Orient orient) {
		return new ITabbox.Builder().setOrient(orient.value).build();
	}

	/**
	 * Returns the instance with the given vflex
	 * @param vflex The vertical flex hint.
	 */
	static ITabbox ofVFlex(String vflex) {
		return new ITabbox.Builder().setVflex(vflex).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITabbox ofId(String id) {
		return new ITabbox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		String _panelSpacing = getPanelSpacing();
		if (_panelSpacing != null)
			render(renderer, "panelSpacing", _panelSpacing);
		String orient = getOrient();
		if (!("horizontal".equals(orient) || "top".equals(orient)))
			render(renderer, "orient", orient);
		boolean _tabscroll = isTabscroll();
		if (!_tabscroll)
			renderer.render("tabscroll", _tabscroll);
		boolean _maximalHeight = isMaximalHeight();
		if (_maximalHeight) {
			renderer.render("z$rod", false);
			renderer.render("maximalHeight", _maximalHeight);
		}
		//ZK-3678: Provide a switch to enable/disable iscroll
		if (!isNativeScrollbar())
			renderer.render("_nativebar", false);
	}

	/**
	 * Builds instances of type {@link ITabbox ITabbox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITabbox.Builder {}

	/**
	 * Builds an updater of type {@link ITabbox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITabboxUpdater {}

	/**
	 * Specifies the orient of tabs' position to this tabbox.
	 */
	enum Orient {
		/**
		 * Left orient.
		 */
		LEFT("left"),

		/**
		 * Top orient.
		 */
		TOP("top"),

		/**
		 * Right orient.
		 */
		RIGHT("right"),

		/**
		 * Bottom orient.
		 */
		BOTTOM("bottom");

		final String value;
		Orient(String value) {
			this.value = value;
		}
	}
}