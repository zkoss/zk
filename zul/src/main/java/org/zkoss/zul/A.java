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
public class A extends LabelImageElement implements org.zkoss.zk.ui.ext.Disable {
	private AuxInfo _auxinf;

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
		return _auxinf != null && _auxinf.disabled;
	}

	/** Sets whether it is disabled.
	 */
	public void setDisabled(boolean disabled) {
		if ((_auxinf != null && _auxinf.disabled) != disabled) {
			initAuxInfo().disabled = disabled;
			smartUpdate("disabled", isDisabled());
		}
	}

	/** Returns a list of component IDs that shall be disabled when the user
	 * clicks this anchor.
	 * @since 7.0.2
	 */
	public String getAutodisable() {
		return _auxinf != null ? _auxinf.autodisable : null;
	}

	/** Sets a list of component IDs that shall be disabled when the user
	 * clicks this anchor.
	 *
	 * <p>To represent the anchor itself, the developer can specify <code>self</code>.
	 * For example, <code>&lt;a id="ok" autodisable="self,cancel"/></code>
	 * is the same as <code>&lt;a id="ok" autodisable="ok,cancel"/></code>
	 * that will disable
	 * both the ok and cancel anchor when an user clicks it.
	 *
	 * <p>The anchor being disabled will be enabled automatically
	 * once the client receives a response from the server.
	 * In other words, the server doesn't notice if a anchor is disabled
	 * with this method.
	 *
	 * <p>However, if you prefer to enable them later manually, you can
	 * prefix with '+'. For example,
	 * <code>&lt;a id="ok" autodisable="+self,+cancel"/></code>
	 *
	 * <p>Then, you have to enable them manually such as
	 * <pre><code>if (something_happened){
	 *  ok.setDisabled(false);
	 *  cancel.setDisabled(false);
	 *</code></pre>
	 *
	 * <p>Default: null.
	 * @since 7.0.2
	 */
	public void setAutodisable(String autodisable) {
		if (!Objects.equals(_auxinf != null ? _auxinf.autodisable : null, autodisable)) {
			initAuxInfo().autodisable = autodisable;
			smartUpdate("autodisable", getAutodisable());
		}
	}

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	public String getDir() {
		return _auxinf != null ? _auxinf.dir : "normal";
	}

	/** Sets the direction to layout with image.
	 * @param dir either "normal" or "reverse".
	 */
	public void setDir(String dir) throws WrongValueException {
		if (!"normal".equals(dir) && !"reverse".equals(dir))
			throw new WrongValueException(dir);

		if (!Objects.equals(_auxinf != null ? _auxinf.dir : "normal", dir)) {
			initAuxInfo().dir = dir;
			smartUpdate("dir", getDir());
		}
	}

	/** Returns the href that the browser shall jump to, if an user clicks
	 * this button.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick event listener.
	 * <p>If it is not null, the onClick event won't be sent.
	 */
	public String getHref() {
		return _auxinf != null ? _auxinf.href : null;
	}

	/** Sets the href.
	 */
	public void setHref(String href) {
		if (href != null && href.length() == 0)
			href = null;
		if (!Objects.equals(_auxinf != null ? _auxinf.href : null, href)) {
			initAuxInfo().href = href;
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
		return _auxinf != null ? _auxinf.target : null;
	}

	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	public void setTarget(String target) {
		if (target != null && target.length() == 0)
			target = null;

		if (!Objects.equals(_auxinf != null ? _auxinf.target : null, target)) {
			initAuxInfo().target = target;
			smartUpdate("target", getTarget());
		}
	}

	private String getEncodedHref() {
		final Desktop dt = getDesktop();
		return _auxinf != null && _auxinf.href != null && dt != null ? dt.getExecution().encodeURL(_auxinf.href) : null;
		//if desktop is null, it doesn't belong to any execution
	}

	//-- super --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		String s;
		if (!"normal".equals(s = getDir()))
			render(renderer, "dir", s);

		render(renderer, "disabled", isDisabled());
		render(renderer, "autodisable", getAutodisable());
		final String href;
		render(renderer, "href", href = getEncodedHref());
		render(renderer, "target", getTarget());

		org.zkoss.zul.impl.Utils.renderCrawlableA(href, getLabel());
	}

	protected void renderCrawlable(String label) throws java.io.IOException {
		//does nothing since generated in renderProperties
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	//Component//
	public String getZclass() {
		return _zclass != null ? _zclass : "z-a";
	}

	private class EncodedHref implements org.zkoss.zk.au.DeferredValue {
		public Object getValue() {
			return getEncodedHref();
		}
	}

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private String dir = "normal";
		private String href, target;
		private String autodisable;
		protected String upload;
		private boolean disabled;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
