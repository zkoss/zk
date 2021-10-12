/* Source.java

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
 * The SOURCE tag.
 * 
 * @author jameschu
 */
public class Source extends AbstractTag {
	public Source() {
		super("source");
	}

	/**
	 * Returns the src of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSrc() {
		return (String) getDynamicProperty("src");
	}

	/**
	 * Sets the src of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSrc(String src) throws WrongValueException {
		setDynamicProperty("src", src);
	}

	/**
	 * Returns the srcset of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSrcset() {
		return (String) getDynamicProperty("srcset");
	}

	/**
	 * Sets the srcset of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSrcset(String srcset) throws WrongValueException {
		setDynamicProperty("srcset", srcset);
	}

	/**
	 * Returns the media of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getMedia() {
		return (String) getDynamicProperty("media");
	}

	/**
	 * Sets the media of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setMedia(String media) throws WrongValueException {
		setDynamicProperty("media", media);
	}

	/**
	 * Returns the sizes of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getSizes() {
		return (String) getDynamicProperty("sizes");
	}

	/**
	 * Sets the sizes of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setSizes(String sizes) throws WrongValueException {
		setDynamicProperty("sizes", sizes);
	}

	/**
	 * Returns the type of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public String getType() {
		return (String) getDynamicProperty("type");
	}

	/**
	 * Sets the type of this source tag.
	 * <p>Notice that this attribute refers to the corresponding attribute of the HTML5 specification.
	 * Hence, it would still be rendered to client-side as a DOM attribute even if the browser doesn’t support it.
	 * @since 8.5.1
	 */
	public void setType(String type) throws WrongValueException {
		setDynamicProperty("type", type);
	}
}
