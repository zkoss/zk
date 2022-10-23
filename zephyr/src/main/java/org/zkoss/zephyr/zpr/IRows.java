/* IRows.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:55:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zephyr.immutable.ZephyrOnly;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Rows;

/**
 * Immutable {@link Rows} component
 * <p> Defines the rows of a grid.
 * Each child of a rows element should be a {@link IRow} component.
 * </p>
 * @author katherine
 * @see Rows
 */
@ZephyrStyle
public interface IRows extends IXulElement<IRows>, IChildable<IRows, IRowBase>, IGridComposite<IRows> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IRows DEFAULT = new IRows.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Rows> getZKType() {
		return Rows.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.grid.Rows"}</p>
	 */
	default String getWidgetClass() {
		return "zul.grid.Rows";
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getWidth() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IRows withWidth(@Nullable String width) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * @hidde for Javadoc
	 */
	@Value.Derived
	@Nullable
	default String getHflex() {
		return null;
	}

	/**
	 * To control the size of Foot related
	 * components, please refer to {@link IGrid} and {@link IColumn} instead.
	 */
	default IRows withHflex(@Nullable String hflex) {
		throw new UnsupportedOperationException("readonly");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkGroup() {
		List<IRowBase> children = getChildren();
		int token = -1; // -1: no group and groupfoot, 0: group, 1: groupfoot
		Iterator<IRowBase> iterator = children.iterator();
		while (iterator.hasNext()) {
			IRowBase next = iterator.next();
			if (next instanceof IGroupfootChild) {
				if (token < 0) {
					throw new UiException("Groupfoot cannot exist alone, you have to add a Group first");
				} else if (token > 0) {
					throw new UiException("Only one Goupfoot is allowed per Group");
				}
				token = 1;
			} else if (next instanceof IGroupChild) {
				token = 0;
			}
		}
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@ZephyrOnly
	default int getOffset() {
		return 0;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	IRows withOffset(int offset);

	/**
	 * Internal use for Model case
	 * @hidden for Javadoc
	 */
	@ZephyrOnly
	default Map<String, Object> getAuxInfo() {
		return Collections.emptyMap();
	}

	/**
	 * Returns the instance with the given {@link IRowBase} children.
	 * @param children The children of {@link IRowBase}
	 */
	static IRows of(Iterable<? extends IRowBase> children) {
		return new IRows.Builder().setChildren(children).build();
	}

	/**
	 * Returns the instance with the given {@link IRowBase} children.
	 * @param children The children of {@link IRowBase}
	 */
	static IRows of(IRowBase... children) {
		Objects.requireNonNull(children, "Children cannot be null");
		return of(Arrays.asList(children));
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IRows ofId(String id) {
		return new IRows.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		renderer.render("_offset", getOffset());

		//render "visibleItemCount"
		Map<String, Object> map = getAuxInfo();
		if (!map.isEmpty()) {
			for (Map.Entry<String, ?> entry: map.entrySet()) {
				renderer.render(entry.getKey(), entry.getValue());
			}
		}

	}

	/**
	 * Builds instances of type {@link IRows IRows}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIRows.Builder {
	}

	/**
	 * Builds an updater of type {@link IRows} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends IRowsUpdater {}
}