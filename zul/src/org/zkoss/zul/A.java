/* A.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 22, 2009 4:05:38 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * The same as HTML A tag.
 * <p>Default {@link #getZclass}: z-a.
 * @author jumperchen
 * @since 5.0.0
 */
public class A extends LabelImageElement implements org.zkoss.zul.api.A {
	private String _dir = "normal";
	private String _href, _target;
	private int _tabindex = -1;
	private boolean _disabled;

	static {
		addClientEvent(A.class, Events.ON_FOCUS, CE_DUPLICATE_IGNORE);
		addClientEvent(A.class, Events.ON_BLUR, CE_DUPLICATE_IGNORE);
	}

	public A() {
	}
	public A(String label) {
		super(label);
	}
	public A(String label, String image) {
		super(label, image);
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}
	
	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _dir;
	}
	/** Sets the direction.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_dir, dir)) {
			_dir = dir;
			smartUpdate("dir", _dir);
		}
	}

	/** Returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	public String getHref() {
		return _href;
	}
	/** Sets the href.
	 */
	public void setHref(String href) {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_href, href)) {
			_href = href;
			smartUpdate("href", new EncodedHref()); //Bug 1850895
		}
	}

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 */
	public String getTarget() {
		return _target;
	}
	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_target, target)) {
			_target = target;
			smartUpdate("target", _target);
		}
	}

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	public int getTabindex() {
		return _tabindex;
	}
	/** Sets the tab order of this component.
	 */
	public void setTabindex(int tabindex) throws WrongValueException {
		if (_tabindex != tabindex) {
			_tabindex = tabindex;
			smartUpdate("tabindex", _tabindex);
		}
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _href != null && dt != null ? dt.getExecution().encodeURL(_href): null;
			//if desktop is null, it doesn't belong to any execution
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		if (_tabindex >= 0)
			renderer.render("tabindex", _tabindex);
		if (!"normal".equals(_dir)) render(renderer, "dir", _dir);

		render(renderer, "disabled", _disabled);
		final String href;
		render(renderer, "href", href = getEncodedHref());
		render(renderer, "target", _target);

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}
	
	//Component//
	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}
	public String getZclass() {
		return _zclass != null ? _zclass: "z-a";
	}

	private class EncodedHref implements org.zkoss.zk.ui.util.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}
}
