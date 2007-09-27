/* SplitLayoutRegion.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 8, 2007 11:23:11 AM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkforge.yuiext.layout;

import org.zkoss.xml.HTMLs;

/**
 * 
 * This class refer to Ext.SplitLayoutRegion by Ext JS at client side for adding
 * a splitbar and other (private) useful functionality to a LayoutRegion.
 * 
 * @author jumperchen
 * 
 */
public abstract class SplitLayoutRegion extends LayoutRegion {

	private boolean _split = false;

	private boolean _useShim = false;

	private int _minSize = 0;

	private int _maxSize = 2000;

	/**
	 * Returns the maximum size of the resizing element.
	 * <p>
	 * Default: 2000.
	 */
	public int getMaxSize() {
		return _maxSize;
	}

	/**
	 * Sets the maximum size of the resizing element.
	 */
	public void setMaxSize(int maxSize) {
		if (_maxSize != maxSize) {
			_maxSize = maxSize;
			smartUpdate("z.maxSize", _maxSize);
		}
	}

	/**
	 * Returns the minimum size of the resizing element.
	 * <p>
	 * Default: 0.
	 */
	public int getMinSize() {
		return _minSize;
	}

	/**
	 * Sets the minimum size of the resizing element.
	 */
	public void setMinSize(int minSize) {
		if (_minSize != minSize) {
			_minSize = minSize;
			smartUpdate("z.minSize", _minSize);
		}
	}

	/**
	 * Returns whether enable the split functionality.
	 * <p>
	 * Default: false.
	 */
	public boolean isSplit() {
		return _split;
	}

	/**
	 * Sets whether enable the split functionality.
	 */
	public void setSplit(boolean split) {
		if (_split != split) {
			_split = split;
			invalidate();
		}
	}

	/**
	 * Returns whether to create a transparent shim that overlays the page when
	 * dragging, enables dragging across iframes.
	 * <p>
	 * Default: false.
	 */
	public boolean isUseShim() {
		return _useShim;
	}

	/**
	 * Sets whether to create a transparent shim that overlays the page when
	 * dragging, enables dragging across iframes.
	 */
	public void setUseShim(boolean useShim) {
		if (_useShim != useShim) {
			_useShim = useShim;
			smartUpdate("z.useShim", _useShim);
		}
	}

	/**
	 * Sets margins for the element "{0,1,2,3}" that direction is
	 * "{top,left,right,bottom}" when collapsed.
	 */
	public void setCmargins(String cmargin) {
		if (cmargin != null) {
			cmargin = cmargin.trim();
			if (cmargin.startsWith("{") && cmargin.endsWith("}")) {
				final String[] margins = cmargin.substring(1,
						cmargin.length() - 1).split(",");
				setCmargins(Integer.parseInt(margins[0]), Integer
						.parseInt(margins[1]), Integer.parseInt(margins[2]),
						Integer.parseInt(margins[3]));
			}
		} else {
			throw new IllegalArgumentException(
					"margin must be a formation of {0,1,2,3}. The margin :"
							+ cmargin);
		}
	}

	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64).append(super
				.getOuterAttrs());
		final StringBuffer nm = new StringBuffer(32);
		if (isSplit()) {
			HTMLs.appendAttribute(sb, "z.split", _split);
			nm.append("split,");
		}
		if (getMaxSize() != 2000) {
			HTMLs.appendAttribute(sb, "z.maxSize", _maxSize);
			nm.append("maxSize,");
		}
		if (getMinSize() != 0) {
			HTMLs.appendAttribute(sb, "z.minSize", _minSize);
			nm.append("minSize,");
		}
		if (nm.length() > 0) {
			if (nm.lastIndexOf(",") + 1 == nm.length())
				nm.delete(nm.length() - 1, nm.length());
			appendInitAttr(sb, nm.toString());
		}
		return sb.toString();
	}

}
