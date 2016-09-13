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
	 * @since 8.0.3
	 */
	public String getDownload() {
		return (String) getDynamicProperty("download");
	}

	/**
	 * Sets the download of this area tag.
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
	 * @since 8.0.3
	 */
	public String getHreflang() {
		return (String) getDynamicProperty("hreflang");
	}

	/**
	 * Sets the hreflang of this area tag.
	 * @since 8.0.3
	 */
	public void setHreflang(String hreflang) throws WrongValueException {
		setDynamicProperty("hreflang", hreflang);
	}
	/**
	 * Returns the media of this area tag.
	 * @since 8.0.3
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this area tag.
	 * @since 8.0.3
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}
	/**
	 * Returns the rel of this area tag.
	 * @since 8.0.3
	 */
	public String getRel() {
		return (String) getDynamicProperty("rel");
	}

	/**
	 * Sets the rel of this area tag.
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
	 * @since 8.0.3
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this area tag.
	 * @since 8.0.3
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
