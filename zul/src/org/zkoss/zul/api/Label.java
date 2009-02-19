/* Label.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * A label.
 * 
 * @author tomyeh
 * @since 3.5.2
 */
public interface Label extends org.zkoss.zul.impl.api.XulElement {

	/**
	 * Returns the value.
	 * <p>
	 * Default: "".
	 */
	public String getValue();

	/**
	 * Sets the value.
	 */
	public void setValue(String value);

	/**
	 * Returns the maximal length of the label.
	 * 
	 * <p>
	 * Noteice:
	 * <dl>
	 * <dt>hyphen="false" and pre="false"</dt>
	 * <dd>maxlength is the maximal length to display. Exceeding part is
	 * truncated.</dd>
	 * <dt>hyphen="true"</dt>
	 * <dd>maxlength is the maximal length of each line, and hyphenation is
	 * added if a line exceeds maxlength.</dd>
	 * <dt>hyphen="false" and pre="true"</dt>
	 * <dd>maxlength has no effect.</dd>
	 * <dt>maxlength=0</dt>
	 * <dd>hyphen has no effect</dd>
	 * </dl>
	 * 
	 * <p>
	 * Since 3.0.4, you can set the style class (@{link #setSclass}) to
	 * "word-wrap" to wrap a long word instead of using the hyphen and maxlength
	 * property. However, word-wrap is not applicable to Opera (it works fine
	 * with FF, IE and Safari).
	 */
	public int getMaxlength();

	/**
	 * Sets the maximal length of the label.
	 * 
	 * <p>
	 * See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setMaxlength(int maxlength);

	/**
	 * Returns whether to preserve the white spaces, such as space, tab and new
	 * line.
	 * 
	 * <p>
	 * It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically. Refer to Bug
	 * 1455584.
	 * 
	 * <p>
	 * See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 * 
	 * <p>
	 * Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true. In other words, <code>pre</code>
	 * implies <code>multiline</code>
	 */
	public boolean isPre();

	/**
	 * Sets whether to preserve the white spaces, such as space, tab and new
	 * line.
	 * 
	 * <p>
	 * See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setPre(boolean pre);

	/**
	 * Returns whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 * 
	 * <p>
	 * Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true. In other words, <code>pre</code>
	 * implies <code>multiline</code>
	 */
	public boolean isMultiline();

	/**
	 * Sets whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	public void setMultiline(boolean multiline);

	/**
	 * Returns whether to hyphenate a long word if maxlength is specified.
	 * 
	 * <p>
	 * Since 3.0.4, you can set the style class (@{link #setSclass}) to
	 * "word-wrap" to wrap a long word instead of using the hyphen and maxlength
	 * property. However, word-wrap is not applicable to Opera (it works fine
	 * with FF, IE and Safari).
	 * 
	 * <p>
	 * See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public boolean isHyphen();

	/**
	 * Sets whether to hyphen a long word if maxlength is specified.
	 * 
	 * <p>
	 * See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setHyphen(boolean hyphen);
}
