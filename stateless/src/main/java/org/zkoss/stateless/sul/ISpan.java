/* ISpan.java

	Purpose:

	Description:

	History:
		4:17 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zul.Span;

/**
 * Immutable {@link Span} component.
 *
 * <p>The same as HTML SPAN tag.
 * It is one of the most lightweight containers to group child component for, say,
 * assigning CSS or making more sophisticated layout. Span is displayed inline with
 * other siblings; as if there is no line breaks between them.
 *
 * @author jumperchen
 * @see Span
 */
@StatelessStyle
public interface ISpan<I extends IAnyGroup> extends IXulElement<ISpan<I>>,
		IAnyGroup<ISpan<I>>, IChildable<ISpan<I>, I> {
	/**
	 * Constant for default attributes of this immutable component.
	 */
	ISpan DEFAULT = new ISpan.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Span> getZKType() {
		return Span.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Span"}</p>
	 */
	default String getWidgetClass() {
		return "zul.wgt.Span";
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ISpan<I> of(Iterable<? extends I> children) {
		return new ISpan.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> ISpan<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> ISpan<I> ofId(String id) {
		return new ISpan.Builder<I>().setId(id).build();
	}

	/**
	 * Builds instances of type {@link ISpan ISpan}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableISpan.Builder<I> {}

	/**
	 * Builds an updater of type {@link ISpan} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ISpanUpdater {}
}
