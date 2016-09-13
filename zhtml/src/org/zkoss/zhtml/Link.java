/* Link.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:22:15     2005, Created by tomyeh

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
 * The LINK tag.
 * 
 * @author tomyeh
 */
public class Link extends AbstractTag {
	public Link() {
		super("link");
	}
	/**
	 * Returns the crossorigin of this link tag.
	 * @since 8.0.3
	 */
	public String getCrossorigin() {
		return (String) getDynamicProperty("crossorigin");
	}

	/**
	 * Sets the crossorigin of this link tag.
	 * @since 8.0.3
	 */
	public void setCrossorigin(String crossorigin) throws WrongValueException {
		setDynamicProperty("crossorigin", crossorigin);
	}
	/**
	 * Returns the href of this link tag.
	 * @since 8.0.3
	 */
	public String getHref() {
		return (String) getDynamicProperty("href");
	}

	/**
	 * Sets the href of this link tag.
	 * @since 8.0.3
	 */
	public void setHref(String href) throws WrongValueException {
		setDynamicProperty("href", href);
	}
	/**
	 * Returns the hreflang of this link tag.
	 * @since 8.0.3
	 */
	public String getHreflang() {
		return (String) getDynamicProperty("hreflang");
	}

	/**
	 * Sets the hreflang of this link tag.
	 * @since 8.0.3
	 */
	public void setHreflang(String hreflang) throws WrongValueException {
		setDynamicProperty("hreflang", hreflang);
	}
	/**
	 * Returns the media of this link tag.
	 * @since 8.0.3
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this link tag.
	 * @since 8.0.3
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}
	/**
	 * Returns the rel of this link tag.
	 * @since 8.0.3
	 */
	public String getRel() {
		return (String) getDynamicProperty("rel");
	}

	/**
	 * Sets the rel of this link tag.
	 * @since 8.0.3
	 */
	public void setRel(String rel) throws WrongValueException {
		setDynamicProperty("rel", rel);
	}
	/**
	 * Returns the sizes of this link tag.
	 * @since 8.0.3
	 */
	public String getSizes() {
		return (String) getDynamicProperty("sizes");
	}

	/**
	 * Sets the sizes of this link tag.
	 * @since 8.0.3
	 */
	public void setSizes(String sizes) throws WrongValueException {
		setDynamicProperty("sizes", sizes);
	}
	/**
	 * Returns the type of this link tag.
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this link tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
