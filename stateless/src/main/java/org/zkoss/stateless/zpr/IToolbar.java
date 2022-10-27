/* IToolbar.java

	Purpose:

	Description:

	History:
		Fri Oct 22 10:07:26 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.stateless.ui.util.IComponentChecker;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Toolbar;

/**
 * Immutable {@link Toolbar} component
 *
 * <p>A toolbar is used to place a series of buttons, such as toolbarbutton or button.
 * The toolbar buttons could be used without toolbars, so a toolbar could be used without
 * tool buttons. However, the tool buttons change their appearance if they are placed inside a toolbar.
 * <p> The toolbar has two orientation: horizontal and vertical. It controls how the buttons are placed.
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
 *          <td><img src="doc-files/IToolbar_mold_default.png"/></td>
 *       </tr>
 *       <tr>
 *          <td>{@code "panel"}</td>
 *          <td><img src="doc-files/IToolbar_mold_panel.png"/></td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * @author katherine
 * @see Toolbar
 */
@StatelessStyle
public interface IToolbar<I extends IAnyGroup> extends IXulElement<IToolbar<I>>, IChildable<IToolbar<I>, I>,
		IChildrenOfPanel<IToolbar<I>>, IAnyGroup<IToolbar<I>>, ITabboxComposite<IToolbar<I>> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IToolbar<IAnyGroup> DEFAULT = new IToolbar.Builder().build();

	/**
	 * Constant for pnael mold attributes of this immutable component.
	 */
	IToolbar<IAnyGroup> PANEL = new IToolbar.Builder().setMold("panel").build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Toolbar> getZKType() {
		return Toolbar.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Toolbar"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Toolbar";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkOrient() {
		IComponentChecker.checkOrient(getOrient());
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkAlign() {
		String align = getAlign();
		if (!"start".equals(align) && !"center".equals(align) && !"end".equals(align))
			throw new WrongValueException("align cannot be " + align);
	}

	/** Returns the orient.
	 * <p>Default: {@code "horizontal"}.
	 */
	default String getOrient() {
		return "horizontal";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient.
	 *
	 * @param orient Either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbar<I> withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient.
	 *
	 * @param orient Either {@code "horizontal"} or
	 * {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IToolbar<I> withOrient(Orient orient) {
		return withOrient(orient.value);
	}

	/**
	 * Returns the alignment of any children added to this toolbar. Valid values
	 * are {@code "start"}, {@code "end"} and {@code "center"}.
	 * <p>Default: {@code "start"}
	 */
	default String getAlign() {
		return "start";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code align}.
	 *
	 * <p> Sets the alignment of any children added to this toolbar.
	 *
	 * @param align Valid values are {@code "start"}, {@code "end"} and {@code "center"}
	 * <p>Default: {@code "start"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbar<I> withAlign(String align);

	/**
	 * Return whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 * <p>Default: {@code false}.
	 */
	default boolean isOverflowPopup() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code overflowPopup}.
	 *
	 * <p> Sets whether toolbar has a button that shows a popup
	 *
	 * @param overflowPopup Whether toolbar has a button that shows a popup
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbar<I> withOverflowPopup(boolean overflowPopup);

	/**
	 * Returns the overflow sclass name of overflow popup icon of this toolbar.
	 * Default: {@code "z-icon-ellipsis-h"}.
	 */
	default String getOverflowPopupIconSclass() {
		return "z-icon-ellipsis-h";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code overflowPopupIconSclass}.
	 *
	 * <p> Sets the overflow sclass name of overflow popup icon of this toolbar.
	 *
	 * @param overflowPopupIconSclass The overflow sclass name of overflow popup icon of this toolbar.
	 * <p>Default: {@code "z-icon-ellipsis-h"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IToolbar<I> withOverflowPopupIconSclass(String overflowPopupIconSclass);

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IToolbar<I> of(Iterable<? extends I> children) {
		return new IToolbar.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IToolbar<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IToolbar<I> ofId(String id) {
		return new IToolbar.Builder<I>().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		String _orient = getOrient();
		if (!"horizontal".equals(_orient))
			render(renderer, "orient", _orient);
		String _align = getAlign();
		if (!"start".equals(_align))
			render(renderer, "align", _align);
		render(renderer, "overflowPopup", isOverflowPopup());
		String _overflowPopupIconSclass = getOverflowPopupIconSclass();
		if (!"z-icon-ellipsis-h".equals(_overflowPopupIconSclass))
			render(renderer, "overflowPopupIconSclass", getOverflowPopupIconSclass());
	}

	/**
	 * Builds instances of type {@link IToolbar IToolbar}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIToolbar.Builder<I> {}

	/**
	 * Builds an updater of type {@link IToolbar} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IToolbarUpdater {}

	/**
	 * Specifies the orient with {@link #withOrient(Orient)}
	 */
	enum Orient {
		/**
		 * The horizontal orient.
		 */
		HORIZONTAL("horizontal"),

		/**
		 * The vertical orient.
		 */
		VERTICAL("vertical");
		final String value;

		Orient(String value) {
			this.value = value;
		}
	}
}