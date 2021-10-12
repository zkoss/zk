/* Video.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 14 10:19:42 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.WrongValueException;

/**
 * The VIDEO tag.
 * 
 * @author jameschu
 */
public class Video extends AbstractTag {
	public Video() {
		super("video");
	}

	/**
	 * Returns the autoplay of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	*/
	public boolean getAutoplay() {
		return (Boolean) getDynamicProperty("autoplay");
	}

	/**
	 * Sets the autoplay of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setAutoplay(boolean autoplay) throws WrongValueException {
		setDynamicProperty("autoplay", autoplay);
	}

	/**
	 * Returns the controls of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getControls() {
		return (String) getDynamicProperty("controls");
	}

	/**
	 * Sets the controls of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setControls(String controls) throws WrongValueException {
		setDynamicProperty("controls", controls);
	}

	/**
	 * Returns the crossorigin of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getCrossorigin() {
		return (String) getDynamicProperty("crossorigin");
	}

	/**
	 * Sets the crossorigin of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setCrossorigin(String crossorigin) throws WrongValueException {
		setDynamicProperty("crossorigin", crossorigin);
	}

	/**
	 * Returns the height of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getHeight() {
		return (String) getDynamicProperty("height");
	}

	/**
	 * Sets the height of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setHeight(String height) throws WrongValueException {
		setDynamicProperty("height", height);
	}

	/**
	 * Returns the loop of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public boolean getLoop() {
		return (Boolean) getDynamicProperty("loop");
	}

	/**
	 * Sets the loop of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setLoop(boolean loop) throws WrongValueException {
		setDynamicProperty("loop", loop);
	}

	/**
	 * Returns the muted of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public boolean getMuted() {
		return (Boolean) getDynamicProperty("muted");
	}

	/**
	 * Sets the muted of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setMuted(boolean muted) throws WrongValueException {
		setDynamicProperty("muted", muted);
	}

	/**
	 * Returns the preload of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getPreload() {
		return (String) getDynamicProperty("preload");
	}

	/**
	 * Sets the preload of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setPreload(String preload) throws WrongValueException {
		setDynamicProperty("preload", preload);
	}

	/**
	 * Returns the poster of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getPoster() {
		return (String) getDynamicProperty("poster");
	}

	/**
	 * Sets the poster of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setPoster(String poster) throws WrongValueException {
		setDynamicProperty("poster", poster);
	}

	/**
	 * Returns the src of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}

	/**
	 * Returns the width of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getWidth() {
		return (String) getDynamicProperty("width");
	}

	/**
	 * Sets the width of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setWidth(String width) throws WrongValueException {
		setDynamicProperty("width", width);
	}

	/**
	 * Returns the playsinline of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public boolean getPlaysinline() {
		return (Boolean) getDynamicProperty("playsinline");
	}

	/**
	 * Sets the playsinline of this video tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setPlaysinline(boolean playsinline) throws WrongValueException {
		setDynamicProperty("playsinline", playsinline);
	}
}
