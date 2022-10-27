/* IComboitem.java

	Purpose:

	Description:

	History:
		Fri Oct 15 12:01:42 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Comboitem;

/**
 * Immutable {@link Comboitem} component
 *
 * <p>An item of a combo box.</p>
 *
 * @author katherine
 * @see Comboitem
 */
@StatelessStyle
public interface IComboitem extends ILabelImageElement<IComboitem>, IDisable<IComboitem> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IComboitem DEFAULT = new IComboitem.Builder().build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Comboitem> getZKType() {
		return Comboitem.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.inp.Comboitem"}</p>
	 */
	default String getWidgetClass() {
		return "zul.inp.Comboitem";
	}

	/** Returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * <p>Default: {@code ""}.
	 */
	default String getDescription() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code description}.
	 *
	 * <p>Sets the description.
	 *
	 * @param description The description of the comboitem.
	 * <p>Default: {@code ""}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IComboitem withDescription(String description);


	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: {@code ""}</p>
	 * @see #getDescription
	 */
	default String getContent() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code content}.
	 *
	 * <p> Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: {@code ""}.
	 *
	 * <h4>Security Note</h4>
	 * <p>Unlike other methods, the content assigned to this method
	 * is generated directly to the browser without escaping.
	 * Thus, it is better not to have something input by the user to avoid
	 * any <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/Security_Tips/Cross-site_scripting">XSS</a>
	 * attach.
	 *
	 * @param content The embedded content of the comboitem to display
	 * <p>Default: {@code ""}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IComboitem withContent(String content);

	/**
	 * Returns the instance with the given label
	 * @param label The label of the comboitem
	 */
	static IComboitem of(String label) {
		return new IComboitem.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image
	 * @param label The label of the comboitem
	 * @param image The image of the comboitem
	 */
	static IComboitem of(String label, String image) {
		return new IComboitem.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image
	 * @param image The image of the comboitem
	 */
	static IComboitem ofImage(String image) {
		return new IComboitem.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IComboitem ofId(String id) {
		return new IComboitem.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		ILabelImageElement.super.renderProperties(renderer);
		this.render(renderer, "disabled", this.isDisabled());
		this.render(renderer, "description", getDescription());
		this.render(renderer, "content", getContent());
	}

	/**
	 * Builds instances of type {@link IComboitem IComboitem}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIComboitem.Builder {}

	/**
	 * Builds an updater of type {@link IComboitem} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IComboitemUpdater {}
}