/* A.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 14:56:22     2005, Created by tomyeh

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
 * The A tag.
 * 
 * @author tomyeh
 */
public class A extends AbstractTag {
	public A() {
		super("a");
	}

	/**
	 * Returns the download of this a tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getDownload() {
		return (String) getDynamicProperty("download");
	}

	/**
	 * Sets the download of this a tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setDownload(String download) throws WrongValueException {
		setDynamicProperty("download", download);
	}
	/**
	 * Returns the href of this a tag.
	 * @since 8.0.3
	 */
	public String getHref() {
		return (String) getDynamicProperty("href");
	}

	/**
	 * Sets the href of this a tag.
	 * @since 8.0.3
	 */
	public void setHref(String href) throws WrongValueException {
		setDynamicProperty("href", href);
	}
	/**
	 * Returns the hreflang of this a tag.
	 * @since 8.0.3
	 */
	public String getHreflang() {
		return (String) getDynamicProperty("hreflang");
	}

	/**
	 * Sets the hreflang of this a tag.
	 * @since 8.0.3
	 */
	public void setHreflang(String hreflang) throws WrongValueException {
		setDynamicProperty("hreflang", hreflang);
	}
	/**
	 * Returns the media of this a tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this a tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.0.3
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}
	/**
	 * Returns the rel of this a tag.
	 * @since 8.0.3
	 */
	public String getRel() {
		return (String) getDynamicProperty("rel");
	}

	/**
	 * Sets the rel of this a tag.
	 * @since 8.0.3
	 */
	public void setRel(String rel) throws WrongValueException {
		setDynamicProperty("rel", rel);
	}
	/**
	 * Returns the target of this a tag.
	 * @since 8.0.3
	 */
	public String getTarget() {
		return (String) getDynamicProperty("target");
	}

	/**
	 * Sets the target of this a tag.
	 * @since 8.0.3
	 */
	public void setTarget(String target) throws WrongValueException {
		setDynamicProperty("target", target);
	}
	/**
	 * Returns the type of this a tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this a tag
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
