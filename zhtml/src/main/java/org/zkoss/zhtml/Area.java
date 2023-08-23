/* Area.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:58:16     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The AREA tag.
 * 
 * @author tomyeh
 */
public class Area extends AbstractTag {
	public Area() {
		super("area");
	}
	/**
	 * Returns the alt of this area tag.
	 * @since 8.0.3
	 */
	public String getAlt() {
		return (String) getDynamicProperty("alt");
	}

	/**
	 * Sets the alt of this area tag.
	 * @since 8.0.3
	 */
	public void setAlt(String alt) throws WrongValueException {
		setDynamicProperty("alt", alt);
	}
	/**
	 * Returns the coords of this area tag.
	 * @since 8.0.3
	 */
	public String getCoords() {
		return (String) getDynamicProperty("coords");
	}

	/**
	 * Sets the coords of this area tag.
	 * @since 8.0.3
	 */
	public void setCoords(String coords) throws WrongValueException {
		setDynamicProperty("coords", coords);
	}
	/**
	 * Returns the download of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getDownload() {
		return (String) getDynamicProperty("download");
	}

	/**
	 * Sets the download of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setDownload(String download) throws WrongValueException {
		setDynamicProperty("download", download);
	}
	/**
	 * Returns the href of this area tag.
	 * @since 8.0.3
	 */
	public String getHref() {
		return (String) getDynamicProperty("href");
	}

	/**
	 * Sets the href of this area tag.
	 * @since 8.0.3
	 */
	public void setHref(String href) throws WrongValueException {
		setDynamicProperty("href", href);
	}
	/**
	 * Returns the hreflang of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 * @deprecated Not supported in HTML5.
	 */
	public String getHreflang() {
		return (String) getDynamicProperty("hreflang");
	}

	/**
	 * Sets the hreflang of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 * @deprecated Not supported in HTML5.
	 */
	public void setHreflang(String hreflang) throws WrongValueException {
		setDynamicProperty("hreflang", hreflang);
	}
	/**
	 * Returns the media of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}
	/**
	 * Returns the rel of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getRel() {
		return (String) getDynamicProperty("rel");
	}

	/**
	 * Sets the rel of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setRel(String rel) throws WrongValueException {
		setDynamicProperty("rel", rel);
	}
	/**
	 * Returns the shape of this area tag.
	 * @since 8.0.3
	 */
	public String getShape() {
		return (String) getDynamicProperty("shape");
	}

	/**
	 * Sets the shape of this area tag.
	 * @since 8.0.3
	 */
	public void setShape(String shape) throws WrongValueException {
		setDynamicProperty("shape", shape);
	}
	/**
	 * Returns the target of this area tag.
	 * @since 8.0.3
	 */
	public String getTarget() {
		return (String) getDynamicProperty("target");
	}

	/**
	 * Sets the target of this area tag.
	 * @since 8.0.3
	 */
	public void setTarget(String target) throws WrongValueException {
		setDynamicProperty("target", target);
	}
	/**
	 * Returns the type of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 * @deprecated Not supported in HTML5.
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this area tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 * @deprecated Not supported in HTML5.
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}


	/**
	 * Returns the ping of this a tag.
	 * @since 10.0.0
	 */
	public String getPing() {
		return (String) getDynamicProperty("ping");
	}

	/**
	 * Sets the ping of this a tag as a space-separated list of URLs.
	 * When the link is followed, the browser will send {@code POST} requests with the body
	 * {@code PING} to the URLs. Typically, it's for tracking.
	 * @since 10.0.0
	 */
	public void setPing(String ping) throws WrongValueException {
		setDynamicProperty("ping", ping);
	}

	/**
	 * Returns the referrerpolicy of this a tag.
	 * @since 10.0.0
	 */
	public String getReferrerpolicy() {
		return (String) getDynamicProperty("referrerpolicy");
	}

	/**
	 * Sets the referrerpolicy of this a tag.
	 * <p>How much of the {@code referrer} to send when following the link.
	 * <ul>
	 * <li>{@code no-referrer}: The Referer header will not be sent.</li>
	 * <li>{@code no-referrer-when-downgrade}: The Referer header will not be sent to origins without TLS (HTTPS).</li>
	 * <li>{@code origin}: The sent referrer will be limited to the origin of the referring page: its scheme, host, and port.</li>
	 * <li>{@code origin-when-cross-origin}: The referrer sent to other origins will be limited to the scheme, the host, and the port. Navigations on the same origin will still include the path.</li>
	 * <li>{@code same-origin}: A referrer will be sent for same origin, but cross-origin requests will contain no referrer information.</li>
	 * <li>{@code strict-origin}: Only send the origin of the document as the referrer when the protocol security level stays the same (HTTPS→HTTPS), but don't send it to a less secure destination (HTTPS→HTTP).</li>
	 * <li>{@code strict-origin-when-cross-origin} (default): Send a full URL when performing a same-origin request, only send the origin when the protocol security level stays the same (HTTPS→HTTPS), and send no header to a less secure destination (HTTPS→HTTP).</li>
	 * <li>{@code unsafe-url}: The referrer will include the origin and the path (but not the fragment, password, or username). This value is unsafe, because it leaks origins and paths from TLS-protected resources to insecure origins.</li>
	 * </ul>
	 * @since 10.0.0
	 */
	public void setReferrerpolicy(String referrerpolicy) throws WrongValueException {
		setDynamicProperty("referrerpolicy", referrerpolicy);
	}

	/**
	 * @deprecated Not supported in HTML5.
	 */
	public void setTabindex(Integer tabindex) throws WrongValueException {
		super.setTabindex(tabindex);
	}
}
