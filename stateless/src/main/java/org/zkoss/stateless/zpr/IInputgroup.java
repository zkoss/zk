/* IInputgroup.java

	Purpose:

	Description:

	History:
		Fri Oct 29 12:59:25 CST 2021, Created by katherine

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
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Inputgroup;

/**
 * Immutable {@link Inputgroup} component
 *
 * <p> An inputgroup.
 * <p>
 * Inspired by Bootstrap’s Input group and Button group.
 * By prepending or appending some components to the input component,
 * you can merge them like a new form-input component.
 *
 * <h2>Example</h2>
 * <img src="doc-files/IInputgroup_example.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IVlayout.of(
 *         IInputgroup.of(ILabel.of("{@literal @}"), ITextbox.DEFAULT),
 *         IInputgroup.of(ITextbox.DEFAULT.withPlaceholder("Recipient's username"), ILabel.of("{@literal @}example.com")),
 *         IInputgroup.of(ILabel.of("$"), ITextbox.DEFAULT, ILabel.of(".00")),
 *         IInputgroup.of(ILabel.of("With textarea"), ITextbox.ofCols(30).withRows(5).withMultiline(true))
 *     );
 * }
 * </code></pre>
 * @author katherine
 * @see Inputgroup
 */
@StatelessStyle
public interface IInputgroup extends IXulElement<IInputgroup>,
		IAnyGroup<IInputgroup>, IChildable<IInputgroup, IChildrenOfInputgroup> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IInputgroup DEFAULT = new IInputgroup.Builder().build();

	/**
	 * Internal use
	 *
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Inputgroup> getZKType() {
		return Inputgroup.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.wgt.Inputgroup"}</p>
	 *
	 * @return
	 */
	default String getWidgetClass() {
		return "zul.wgt.Inputgroup";
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
	 * <p> Sets the orient of component
	 *
	 * @param orient Either {@code "horizontal"} or {@code "vertical"}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IInputgroup withOrient(String orient);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code orient}.
	 *
	 * <p> Sets the orient of component
	 *
	 * @param orient The {@link Orient orient}
	 * <p>Default: {@code "horizontal"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IInputgroup withOrient(Orient orient) {
		Objects.requireNonNull(orient);
		return withOrient(orient.value);
	}

	/**
	 *
	 * Returns the instance with the given {@link IChildrenOfInputgroup} children.
	 * @param children The children of {@link IChildrenOfInputgroup}
	 */
	static IInputgroup of(Iterable<? extends IChildrenOfInputgroup> children) {
		return new IInputgroup.Builder().setChildren(children).build();
	}

	/**
	 *
	 * Returns the instance with the given {@link IChildrenOfInputgroup} children.
	 * @param children The children of {@link IChildrenOfInputgroup}
	 */
	static IInputgroup of(IChildrenOfInputgroup... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component.
	 */
	static IInputgroup ofId(String id) {
		return new IInputgroup.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		render(renderer, "vertical", "vertical".equals(getOrient()));
	}

	/**
	 * Builds instances of type {@link IInputgroup IInputgroup}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIInputgroup.Builder {
	}

	/**
	 * Builds an updater of type {@link IInputgroup} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IInputgroupUpdater {}

	/**
	 * Specifies the orient of {@link IInputgroup} component
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