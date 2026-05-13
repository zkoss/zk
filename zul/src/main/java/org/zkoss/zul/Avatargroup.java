/* Avatargroup.java

	Purpose:

	Description:

	History:
		Wed May 13 13:11:40 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.Utils;
import org.zkoss.zul.impl.XulElement;

/**
 * A group container that stacks multiple {@link Avatar} components with an
 * overlapping layout. When {@link #getMaxCount()} is set, avatars beyond the
 * limit are hidden and a "+N" overflow indicator is shown.
 *
 * <p>Setting {@link #setSize(String)} or {@link #setShape(String)} on the
 * group overrides the corresponding property on all child avatars via a CSS
 * cascade class on the group root element.
 *
 * <p>Default {@link #getZclass}: "z-avatargroup".
 *
 * @author peakerlee
 * @since 10.4.0
 */
public class Avatargroup extends XulElement {
	private static final long serialVersionUID = -856626047370581203L;
	private int _maxCount = 0;
	private String _size;
	private String _shape;

	/** Returns the maximum number of visible avatars.
	 * <p>Default: 0 (unlimited).
	 */
	public int getMaxCount() {
		return _maxCount;
	}

	/** Sets the maximum number of visible avatars. Excess avatars are hidden and
	 * replaced with a "+N" overflow indicator. 0 means unlimited.
	 */
	public void setMaxCount(int maxCount) {
		if (maxCount < 0)
			throw new WrongValueException("maxCount cannot be negative: " + maxCount);
		if (_maxCount != maxCount) {
			_maxCount = maxCount;
			smartUpdate("maxCount", _maxCount);
		}
	}

	/** Returns the size override applied to all child avatars, or {@code null}
	 * if not overriding.
	 */
	public String getSize() {
		return _size;
	}

	/** Sets a uniform size for all child avatars ("small", "medium" or "large").
	 * Applies via a CSS cascade class on the group element; the individual
	 * avatar {@link Avatar#setSize(String)} is not changed.
	 */
	public void setSize(String size) throws WrongValueException {
		size = Utils.checkEnum(size, null, "size must be small, medium or large: ", "small", "medium", "large");
		if (!Objects.equals(_size, size)) {
			_size = size;
			smartUpdate("size", _size);
		}
	}

	/** Returns the shape override applied to all child avatars, or {@code null}
	 * if not overriding.
	 */
	public String getShape() {
		return _shape;
	}

	/** Sets a uniform shape for all child avatars ("circle" or "square").
	 * Applies via a CSS cascade class on the group element.
	 */
	public void setShape(String shape) throws WrongValueException {
		shape = Utils.checkEnum(shape, null, "shape must be circle or square: ", "circle", "square");
		if (!Objects.equals(_shape, shape)) {
			_shape = shape;
			smartUpdate("shape", _shape);
		}
	}

	@Override
	public String getZclass() {
		return _zclass == null ? "z-avatargroup" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (_maxCount > 0)
			render(renderer, "maxCount", _maxCount);
		render(renderer, "size", _size);
		render(renderer, "shape", _shape);
	}

	@Override
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Avatar))
			throw new UiException(
					"Avatargroup only accepts Avatar children, got: "
					+ child.getClass().getSimpleName());
		super.beforeChildAdded(child, refChild);
	}

	@Override
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		// Force a re-render so the client widget re-runs _applyOverflow with
		// the new child count (otherwise the "+N" indicator is stale).
		invalidate();
	}

	@Override
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}
}
