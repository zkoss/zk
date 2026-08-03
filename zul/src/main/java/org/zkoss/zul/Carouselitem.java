/* Carouselitem.java

	Purpose:

	Description:

	History:
		Wed Apr 22 2026, Created by yuehfeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A single slide inside a {@link Carousel}.
 *
 * <p>Default {@link #getZclass}: "z-carouselitem".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Carouselitem extends LabelImageElement {
	private static final long serialVersionUID = 7943192457786925518L;
	public Carouselitem() {
	}

	public Carouselitem(String label) {
		super(label);
	}

	public Carouselitem(String label, String image) {
		super(label, image);
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-carouselitem" : _zclass;
	}

	@Override
	public boolean isChildable() {
		return true;
	}

	@Override
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Carousel))
			throw new UiException("Unsupported parent for carouselitem: " + parent);
		super.beforeParentChanged(parent);
	}
}
