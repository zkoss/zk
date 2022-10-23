/* IListitemBase.java

	Purpose:

	Description:

	History:
		Wed Jan 12 09:26:02 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Listitem;

/**
 * Immutable {@link Listitem} base component
 * @author katherine
 */
public interface IListitemBase<I extends IListitemBase>
		extends IXulElement<I>, IListboxComposite<I> {

	@Nullable
	String getImage();
	I withImage(@Nullable String image);

	default int getIndex() {
		return -1;
	}
	I withIndex(int index);
	default boolean isLoaded() {
		return false;
	}
	I withLoaded(boolean loaded);
	default boolean isSelected() {
		return false;
	}
	I withSelected(boolean selected);
	default boolean isSelectable() {
		return true;
	}
	I withSelectable(boolean selectable);

	default boolean isDisabled() {
		return false;
	}
	I withDisabled(boolean disabled);

	@Value.Check
	default void checkWidth() {
		if (getWidth() != null) {
			throw new UnsupportedOperationException("Set listheader's width instead");
		}
	}

	@Value.Check
	default void checkHflex() {
		if (getHflex() != null) {
			throw new UnsupportedOperationException("Set listheader's hflex instead");
		}
	}

	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);

		render(renderer, "selected", isSelected());
		render(renderer, "disabled", isDisabled());
		render(renderer, "_loaded", isLoaded());
		renderer.render("_index", getIndex());
	}
}
