/* IAuxheader.java

	Purpose:

	Description:

	History:
		Thu Oct 07 16:28:51 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Auxheader;

/**
 * Immutable {@link Auxheader} component
 * <p>The auxiliary headers support the colspan and rowspan attributes which allows
 * itself to be spanned across several columns/rows.
 * <p>Auxiliary headers should be accompanied with {@link IColumns columns}/{@link IListhead listhead}/{@link ITreecols treecols}
 * when used with {@link IGrid grid}/{@link IListbox listbox}/{@link ITree tree}.</p>
 *
 * <h2>Example</h2>
 * <img src="doc-files/IAuxheader_example.png"/>
 * <pre>
 * <code>{@literal @}{@code RichletMapping}("/example")
 * public IComponent example() {
 *     return IGrid.DEFAULT.withAuxhead(
 *             IAuxhead.of(
 *                 IAuxheader.of("H1'21").withColspan(6),
 *                 IAuxheader.of("H2'21").withColspan(6)),
 *             IAuxhead.of(
 *                 IAuxheader.of("Q1'21").withColspan(3),
 *                 IAuxheader.of("Q2'21").withColspan(3),
 *                 IAuxheader.of("Q3'21").withColspan(3),
 *                 IAuxheader.of("Q4'21").withColspan(3))
 *         ).withColumns(
 *             IColumns.of(
 *                 IColumn.of("Jan"),
 *                 IColumn.of("Feb"),
 *                 IColumn.of("Mar"),
 *                 IColumn.of("Apr"),
 *                 IColumn.of("May"),
 *                 IColumn.of("Jun"),
 *                 IColumn.of("Jul"),
 *                 IColumn.of("Aug"),
 *                 IColumn.of("Sep"),
 *                 IColumn.of("Oct"),
 *                 IColumn.of("Nov"),
 *                 IColumn.of("Dec")
 *             )
 *         ).withRows(
 *             IRows.of(IRow.of(
 *                 ILabel.of("1,000"),
 *                 ILabel.of("1,100"),
 *                 ILabel.of("1,200"),
 *                 ILabel.of("1,300"),
 *                 ILabel.of("1,400"),
 *                 ILabel.of("1,500"),
 *                 ILabel.of("1,600"),
 *                 ILabel.of("1,700"),
 *                 ILabel.of("1,800"),
 *                 ILabel.of("1,900"),
 *                 ILabel.of("2,000"),
 *                 ILabel.of("2,100")
 *             )
 *         ));
 *     }
 * }
 * </code>
 * </pre>
 * @author katherine
 * @see Auxheader
 */
@ZephyrStyle
public interface IAuxheader<I extends IAnyGroup>
		extends IHeaderElement<IAuxheader<I>>, IChildable<IAuxheader<I>, I> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IAuxheader<IAnyGroup> DEFAULT = new IAuxheader.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Auxheader> getZKType() {
		return Auxheader.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.mesh.Auxheader"}</p>
	 */
	default String getWidgetClass() {
		return "zul.mesh.Auxheader";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkColspanAndRowSpan() {
		if (getColspan() <= 0 || getRowspan() <= 0) {
			throw new UiException("Positive only.");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkHeight() {
		if (getHeight() != null) {
			throw new UiException("Not allowed to set height in auxhead.");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkWidth() {
		if (getWidth() != null) {
			throw new UiException("Not allowed to set width in auxhead.");
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkFlex() {
		if(getVflex() != null) {
			throw new UiException("Not allowed to set vflex in auxhead.");
		}
		if(getHflex() != null) {
			throw new UiException("Not allowed to set hflex in auxhead.");
		}
	}

	/** Returns number of columns to span this header.
	 * Default: {@code 1}.
	 */
	default int getColspan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code colspan}.
	 *
	 * <p> Sets the number of columns to span this header.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 *
	 * @param colspan The number of columns to span this header.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAuxheader<I> withColspan(int colspan);

	/** Returns number of rows to span this header.
	 * Default: {@code 1}.
	 */
	default int getRowspan() {
		return 1;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code rowspan}.
	 *
	 * <p> Sets the number of rows to span this header.
	 * <p>It is the same as the rowspan attribute of HTML TD tag.
	 *
	 * @param rowspan The number of rows to span this header.
	 * <p>Default: {@code 1}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IAuxheader<I> withRowspan(int rowspan);

	/**
	 * Returns the instance with the given label.
	 * @param label The label that the header holds.
	 */
	static <I extends IAnyGroup> IAuxheader<I> of(String label) {
		return new IAuxheader.Builder().setLabel(label).build();
	}

	/**
	 * Returns the instance with the given label and image.
	 * @param label The label that the header holds.
	 * @param image The image that the header holds.
	 */
	static <I extends IAnyGroup> IAuxheader<I> of(String label, String image) {
		return new IAuxheader.Builder().setLabel(label).setImage(image).build();
	}

	/**
	 * Returns the instance with the given image.
	 * @param image The image that the header holds.
	 */
	static <I extends IAnyGroup> IAuxheader<I> ofImage(String image) {
		return new IAuxheader.Builder().setImage(image).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static <I extends IAnyGroup> IAuxheader<I> ofId(String id) {
		return new IAuxheader.Builder().setId(id).build();
	}

	/**
	 *
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IAuxheader<I> of(Iterable<? extends I> children) {
		return new IAuxheader.Builder<I>().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given any group children.
	 * @param children The children belong to any group
	 * @see IAnyGroup
	 */
	static <I extends IAnyGroup> IAuxheader<I> of(I... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws java.io.IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IHeaderElement.super.renderProperties(renderer);

		int _colspan = getColspan();
		int _rowspan = getRowspan();
		if (_colspan != 1)
			renderer.render("colspan", _colspan);
		if (_rowspan != 1)
			renderer.render("rowspan", _rowspan);
	}

	/**
	 * Builds instances of type {@link IAuxheader IAuxheader}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder<I extends IAnyGroup> extends ImmutableIAuxheader.Builder {}

	/**
	 * Builds an updater of type {@link IAuxheader} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IAuxheaderUpdater {}
}