/* Avatar.java

	Purpose:

	Description:

	History:
		Wed Apr 22 2026, Created by yuehfeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.LabelImageElement;
import org.zkoss.zul.impl.Utils;

/**
 * A user avatar. Supports image, fallback label initials, or an icon font.
 *
 * <p>Default {@link #getZclass}: "z-avatar".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Avatar extends LabelImageElement {
	private static final long serialVersionUID = 5340791258877187011L;
	private static final int DEFAULT_GAP = 4;
	private static final String DEFAULT_SHAPE = "circle";
	private static final String DEFAULT_SIZE = "medium";

	private String _shape = DEFAULT_SHAPE;
	private String _size = DEFAULT_SIZE;
	private int _gap = DEFAULT_GAP;

	public Avatar() {
	}

	public Avatar(String label) {
		super(label);
	}

	public Avatar(String label, String image) {
		super(label, image);
	}

	/** Returns the shape of this avatar.
	 * <p>Default: "circle".
	 */
	public String getShape() {
		return _shape;
	}

	/** Sets the shape of this avatar.
	 * @param shape either "circle" or "square". Pass null to reset to the default ("circle").
	 */
	public void setShape(String shape) throws WrongValueException {
		shape = Utils.checkEnum(shape, DEFAULT_SHAPE, "shape must be circle or square: ", "circle", "square");
		if (!Objects.equals(_shape, shape)) {
			_shape = shape;
			smartUpdate("shape", _shape);
		}
	}

	/** Returns the size of this avatar.
	 * <p>Default: "medium".
	 */
	public String getSize() {
		return _size;
	}

	/** Sets the size of this avatar.
	 * @param size "small", "medium" or "large". Pass null to reset to the default ("medium").
	 */
	public void setSize(String size) throws WrongValueException {
		size = Utils.checkEnum(size, DEFAULT_SIZE, "size must be small, medium or large: ", "small", "medium", "large");
		if (!Objects.equals(_size, size)) {
			_size = size;
			smartUpdate("size", _size);
		}
	}

	/** Returns the horizontal padding (in pixels) around the label text.
	 * Mirrors Ant Design Avatar's <code>gap</code> property — the safe inset
	 * the rendered initials keep from each side so they don't crowd the edge
	 * of a small avatar.
	 * <p>Default: 4.
	 */
	public int getGap() {
		return _gap;
	}

	/** Sets the horizontal padding (in pixels) around the label text.
	 * @param gap the inset, in pixels, that the rendered initials keep from each
	 *     side; must be in the range 0 to 24 inclusive. Calls smartUpdate when changed.
	 * @throws WrongValueException if {@code gap} is negative or greater than 24.
	 */
	public void setGap(int gap) throws WrongValueException {
		if (gap < 0)
			throw new WrongValueException("gap must be non-negative: " + gap);
		// Upper cap: a gap larger than the largest standard avatar's radius
		// produces text that overflows or collapses below baseline. 24px
		// matches the half-width of the `large` size (48px) — beyond that
		// the initials lose meaning, so reject at the API boundary instead
		// of relying on CSS clipping to mask the bug.
		if (gap > 24)
			throw new WrongValueException(
					"gap must be at most 24 (px): " + gap);
		if (_gap != gap) {
			_gap = gap;
			smartUpdate("gap", _gap);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-avatar" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		// Skip defaults: the widget already defaults to circle/medium, so an
		// unchanged shape/size would only bloat the initial AU payload (matches
		// the gap guard below).
		if (!DEFAULT_SHAPE.equals(_shape))
			render(renderer, "shape", _shape);
		if (!DEFAULT_SIZE.equals(_size))
			render(renderer, "size", _size);
		if (_gap != DEFAULT_GAP)
			render(renderer, "gap", _gap);
	}

	//-- Component --//
	/** Default: not childable.
	 */
	@Override
	public boolean isChildable() {
		return false;
	}
}
