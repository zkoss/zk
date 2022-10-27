/* DropData.java

	Purpose:
		
	Description:
		
	History:
		10:05 AM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import org.zkoss.lang.Library;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.Self;

/**
 * Represents an action cause by user's dragging and dropping a component.
 *
 * <p>The component being dragged can be retrieved by {@link #getDragged}.
 * The component that received the dragged component is {@link Self}.
 * @author jumperchen
 */
public class DropData extends MouseData {
	private String dragged;
	private Integer draggedIndex;

	@ActionVariable(targetId = SELF, field = "index|childIndex")
	private Integer droppedIndex;

	/**
	 * Returns the component locator being dragged and drop to {@link Self}.
	 * If the library property of {@code "org.zkoss.zul.drop.allowNullDragged"}
	 * is specified with {@code "true"}, then the returned value may be null if
	 * the {@code dragged} locator is null, otherwise, the {@link NullPointerException}
	 * is raised.
	 */
	public Locator getDragged() {
		return "true".equals(
				Library.getProperty("org.zkoss.zul.drop.allowNullDragged")) ?
				dragged == null ? null : Locator.of(dragged) :
				Locator.of(dragged);

	}

	/**
	 * Returns its {@code getIndex()} (if any) or the child index of
	 * the dragged component from its parent.
	 * If the library property of {@code "org.zkoss.zul.drop.allowNullDragged"}
	 * is specified with {@code "true"}, then the returned value may be {@code -1} if
	 * the {@link #getDragged()} locator is null.
	 */
	public int getDraggedIndex() {
		return "true".equals(
				Library.getProperty("org.zkoss.zul.drop.allowNullDragged")) ?
				draggedIndex == null ? -1 : draggedIndex :
				draggedIndex;
	}


	/**
	 * Returns its {@code getIndex()} (if any) or the child index of
	 * the dropped component from its parent.
	 */
	public int getDroppedIndex() {
		return droppedIndex;
	}

	/** Inherited from {@link MouseData}, but not applicable to {@link DropData}.
	 * It always returns null.
	 */
	public String getArea() {
		return null;
	}
}
