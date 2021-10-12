/* Track.java

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
 * The TRACK tag.
 * 
 * @author jameschu
 */
public class Track extends AbstractTag {
	public Track() {
		super("track");
	}

	/**
	 * Returns the default of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getDefault() {
		return (String) getDynamicProperty("default");
	}

	/**
	 * Sets the default of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setDefault(String propDefault) throws WrongValueException {
		setDynamicProperty("default", propDefault);
	}
	/**
	 * Returns the kind of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getKind() {
		return (String) getDynamicProperty("kind");
	}

	/**
	 * Sets the kind of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setKind(String kind) throws WrongValueException {
		setDynamicProperty("kind", kind);
	}
	/**
	 * Returns the label of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getLabel() {
		return (String) getDynamicProperty("label");
	}

	/**
	 * Sets the label of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setLabel(String label) throws WrongValueException {
		setDynamicProperty("label", label);
	}
	/**
	 * Returns the src of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}
	/**
	 * Returns the srclang of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSrclang() {
		return (String) getDynamicProperty("srclang");
	}

	/**
	 * Sets the srclang of this track tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSrclang(String srclang) throws WrongValueException {
		setDynamicProperty("srclang", srclang);
	}
}
