/* Breadcrumbitem.java

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
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.Disable;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * A single entry in a {@link Breadcrumb}.
 *
 * <p>Default {@link #getZclass}: "z-breadcrumbitem".
 *
 * @author yuehfeng
 * @since 10.4.0
 */
public class Breadcrumbitem extends LabelImageElement implements Disable {
	private static final long serialVersionUID = 1674508024320177268L;
	private String _href;
	private String _target;
	private boolean _disabled;

	public Breadcrumbitem() {
	}

	public Breadcrumbitem(String label) {
		super(label);
	}

	public Breadcrumbitem(String label, String href) {
		super(label);
		setHref(href);
	}

	/** Returns the navigation target URL.
	 * <p>Default: null. When null, the item renders as plain text (typically the last item).
	 */
	public String getHref() {
		return _href;
	}

	/** Sets the navigation target URL.
	 * @param href the URL the item links to; an empty string is treated as null,
	 *     in which case the item renders as plain text. Calls smartUpdate when changed.
	 * @throws WrongValueException if {@code href} uses an unsafe scheme
	 *     (javascript:, vbscript:, blob:, filesystem:, or a non-image data: URI).
	 */
	public void setHref(String href) {
		if (href != null && href.isEmpty())
			href = null;
		if (href != null) {
			// Reject schemes that turn an <a href="..."> into a script-execution
			// or resource-exfiltration vector. Strip ASCII control characters
			// (0x00–0x1F, 0x7F) before the prefix test because browsers strip
			// 0x09 / 0x0A / 0x0D from URL schemes before parsing — without
			// this, "java\tscript:..." would slip past startsWith("javascript:")
			// but still execute in the resulting <a>.
			//
			// Blocked schemes:
			//  - javascript:, vbscript: — inline script execution.
			//  - data:image/svg+xml — SVG permits inline <script> / onload=…,
			//    so the "image-only" allow rule below isn't enough.
			//  - blob:, filesystem: — used in some XSS chains to host
			//    attacker-controlled content with same-origin-ish behaviour.
			//  - data: other than data:image/* — arbitrary MIME/payload.
			String t = href.replaceAll("[\\x00-\\x1F\\x7F]", "")
					.trim().toLowerCase(java.util.Locale.ROOT);
			if (t.startsWith("javascript:") || t.startsWith("vbscript:")
					|| t.startsWith("blob:") || t.startsWith("filesystem:")
					|| t.startsWith("data:image/svg+xml")
					|| (t.startsWith("data:") && !t.startsWith("data:image/")))
				throw new WrongValueException("Unsafe href scheme: " + href);
		}
		if (!Objects.equals(_href, href)) {
			_href = href;
			// Push the encoded href (deferred so it resolves against the
			// execution at flush time) — same as A/Button/Menuitem. Sending the
			// raw value would skip context-path prefixing, cookieless-session
			// rewriting and "~./" resource resolution.
			smartUpdate("href", new EncodedHref());
		}
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _href != null && dt != null ? dt.getExecution().encodeURL(_href) : null;
		//if desktop is null, it doesn't belong to any execution
	}

	/** Returns the browsing context target (e.g., "_blank"). */
	public String getTarget() {
		return _target;
	}

	/** Sets the browsing context target (e.g., "_blank").
	 * @param target the target browsing context; an empty string is treated as null. Calls smartUpdate when changed.
	 */
	public void setTarget(String target) {
		if (target != null && target.isEmpty())
			target = null;
		if (!Objects.equals(_target, target)) {
			_target = target;
			smartUpdate("target", _target);
		}
	}

	/** Whether this item is disabled (non-clickable). */
	public boolean isDisabled() {
		return _disabled;
	}

	/** Sets whether this item is disabled (non-clickable).
	 * @param disabled whether the item is disabled. Calls smartUpdate when changed.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	//super//
	@Override
	public String getZclass() {
		return _zclass == null ? "z-breadcrumbitem" : _zclass;
	}

	@Override
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		render(renderer, "href", getEncodedHref());
		render(renderer, "target", _target);
		render(renderer, "disabled", _disabled);
	}

	private class EncodedHref implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}

	@Override
	public boolean isChildable() {
		return false;
	}

	@Override
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Breadcrumb))
			throw new UiException("Unsupported parent for breadcrumbitem: " + parent);
		super.beforeParentChanged(parent);
	}
}
