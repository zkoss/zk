/* Breadcrumb.java

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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.XulElement;

/**
 * A breadcrumb navigation. Contains an ordered list of {@link Breadcrumbitem}.
 *
 * <p>Default {@link #getZclass}: "z-breadcrumb".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Breadcrumb extends XulElement {
	private static final long serialVersionUID = 6240578539271280834L;
	private String _separator = "/";
	private int _maxItems = 0;

	public Breadcrumb() {
	}

	/** Returns the separator between items.
	 * <p>Default: "/".
	 */
	public String getSeparator() {
		return _separator;
	}

	/** Sets the separator. Also accepts icon form: "icon:z-icon-chevron-right".
	 */
	public void setSeparator(String separator) {
		if (separator == null || separator.isEmpty())
			separator = "/";
		if (!Objects.equals(_separator, separator)) {
			_separator = separator;
			smartUpdate("separator", _separator);
		}
	}

	/** Returns the maximum number of items to display before collapsing.
	 * <p>Default: 0 (no limit).
	 */
	public int getMaxItems() {
		return _maxItems;
	}

	/** Sets the maximum number of items to display before collapsing.
	 * <p>Use {@code 0} to disable collapse (default). A non-zero value must
	 * be {@code >= 2} — the collapse always keeps the first and last item, so
	 * {@code maxItems = 1} cannot actually hide anything and is rejected.
	 * @throws WrongValueException if {@code maxItems} is negative or equal to 1.
	 */
	public void setMaxItems(int maxItems) {
		if (maxItems < 0)
			throw new WrongValueException("maxItems cannot be negative: " + maxItems);
		if (maxItems == 1)
			throw new WrongValueException(
					"maxItems must be 0 (unlimited) or >= 2 — got 1, "
					+ "which cannot collapse below the first+last pair");
		if (_maxItems != maxItems) {
			_maxItems = maxItems;
			smartUpdate("maxItems", _maxItems);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-breadcrumb" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		if (!"/".equals(_separator))
			render(renderer, "separator", _separator);
		if (_maxItems != 0)
			render(renderer, "maxItems", _maxItems);
	}

	//-- Component --//
	@Override
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Breadcrumbitem))
			throw new UiException("Unsupported child for breadcrumb: " + child);
		super.beforeChildAdded(child, refChild);
	}

	@Override
	public void onChildAdded(Component child) {
		super.onChildAdded(child);
		// Re-render so the client _applyCollapse re-evaluates ellipsis placement
		// against the new item count.
		invalidate();
	}

	@Override
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		invalidate();
	}
}
