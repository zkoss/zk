/* ILabel.java

	Purpose:

	Description:

	History:
		11:52 AM 2021/9/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.io.IOException;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Label;

/**
 * Immutable {@link Label} component
 * <p>A label component represents a piece of text.</p>
 *
 * @author jumperchen
 * @see Label
 */
@StatelessStyle
public interface ILabel extends IXulElement<ILabel>,
		IAnyGroup<ILabel>, IChildrenOfInputgroup<ILabel> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ILabel DEFAULT = new ILabel.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Label> getZKType() {
		return Label.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Label"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Label";
	}

	/** Returns the value.
	 * <p>Default: {@code ""}.
	 */
	default String getValue() {
		return "";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code value}.
	 *
	 * <p>Sets the value of the component
	 *
	 * @param value The value of the component
	 * <p>Default: {@code ""}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ILabel withValue(String value);

	/** Returns the maximal length of the label.
	 * <p>Default: {@code 0} (means no limitation)
	 */
	default int getMaxlength() {
		return 0;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code maxlength}.
	 *
	 * <p>Sets the maximal length of the label.
	 *
	 * @param maxlength The maximal length of the label.
	 * <p>Default: {@code 0}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ILabel withMaxlength(int maxlength);

	/** Returns whether to preserve the new line and the white spaces at the
	 * beginning of each line.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isMultiline() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code multiline}.
	 *
	 * <p>Sets whether to preserve the new line and the white spaces at the
	 * beginning of each line.
	 *
	 * @param multiline Whether to preserve the new line and the white spaces at the
	 * beginning of each line.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ILabel withMultiline(boolean multiline);

	/** Returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre".
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 */
	default boolean isPre() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code pre}.
	 *
	 * <p>Sets whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * @param pre Whether to preserve the white spaces, such as space, tab and new line.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ILabel withPre(boolean pre);

	/**
	 * Returns the instance with the given value.
	 * @param value The value of the component.
	 */
	static ILabel of(String value) {
		return new ILabel.Builder().setValue(value).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ILabel ofId(String id) {
		return new ILabel.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		int v = getMaxlength();
		if (v > 0)
			renderer.render("maxlength", v);
		render(renderer, "multiline", isMultiline());
		render(renderer, "pre", isPre());

		final String val = getValue();
		//allow deriving to override getValue()
		render(renderer, "value", val);
		org.zkoss.zul.impl.Utils.renderCrawlableText(val);
	}

	/**
	 * Builds instances of type {@link ILabel ILabel}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableILabel.Builder {}

	/**
	 * Builds an updater of type {@link ILabel} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ILabelUpdater {}
}

