/* Label.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 18:53:53     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;

import org.zkoss.zul.impl.XulElement;

/**
 * A label.
 * 
 * @author tomyeh
 */
public class Label extends XulElement {
	private String _value = "";
	private int _maxlength;
	private boolean _pre, _hyphen, _multiline;

	public Label() {
	}
	public Label(String value) {
		setValue(value);
	}

	/** Returns the value.
	 * <p>Default: "".
	 */
	public String getValue() {
		return _value;
	}
	/** Sets the value.
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!Objects.equals(_value, value)) {
			_value = value;
			invalidate();
		}
	}

	/** Returns the maximal length of the label.
	 *
	 * <p>Noteice:
	 * <dl>
	 * <dt>hyphen="false" and pre="false"</dt>
	 * <dd>maxlength is the maximal length to display. Exceeding part is truncated.</dd>
	 * <dt>hyphen="true"</dt>
	 * <dd>maxlength is the maximal length of each line, and hyphenation is added
	 * if a line exceeds maxlength.</dd>
	 * <dt>hyphen="false" and pre="true"</dt>
	 * <dd>maxlength has no effect.</dd>
	 * <dt>maxlength=0</dt>
	 * <dd>hyphen has no effect</dd>
	 * </dl>
	 *
	 * <p>Since 3.0.4, you can set the style class (@{link #setSclass})
	 * to "word-wrap" to wrap a long word instead of using the hyphen
	 * and maxlength property. However, word-wrap is not applicable to
	 * Opera (it works fine with FF, IE and Safari).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of the label.
	 *
	 * <p>See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			invalidate();
		}
	}
	/** Returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically.
	 * Refer to Bug 1455584.
	 *
	 * <p>See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 * In other words, <code>pre</code> implies <code>multiline</code>
	 */
	public boolean isPre() {
		return _pre;
	}
	/** Sets whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setPre(boolean pre) {
		if (_pre != pre) {
			_pre = pre;
			invalidate();
		}
	}
	/** Returns whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 * In other words, <code>pre</code> implies <code>multiline</code>
	 */
	public boolean isMultiline() {
		return _multiline;
	}
	/** Sets whether to preserve the new line and the white spaces at the
	 * begining of each line.
	 */
	public void setMultiline(boolean multiline) {
		if (_multiline != multiline) {
			_multiline = multiline;
			invalidate();
		}
	}
	/** Returns whether to hyphenate a long word if maxlength is specified.
	 *
	 * <p>Since 3.0.4, you can set the style class (@{link #setSclass})
	 * to "word-wrap" to wrap a long word instead of using the hyphen
	 * and maxlength property. However, word-wrap is not applicable to
	 * Opera (it works fine with FF, IE and Safari).
	 *
	 * <p>See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public boolean isHyphen() {
		return _hyphen;
	}
	/** Sets whether to hyphen a long word if maxlength is specified.
	 *
	 * <p>See {@link #getMaxlength} for the relationship among pre, hyphen and
	 * maxlength.
	 */
	public void setHyphen(boolean hyphen) {
		if (_hyphen != hyphen) {
			_hyphen = hyphen;
			invalidate();
		}
	}

	/** Whether to generate the value directly without ID.
	 * <p>Used only for component generated. Not for applications.
	 * @since 3.0.0
	 */
	public boolean isIdRequired() {
		final Component p = getParent();
		return p == null || !isVisible() 
			|| !isRawLabel(p) || !Components.isAutoId(getId())
			|| isAsapRequired(Events.ON_CLICK)
			|| !isEmpty(getStyle()) || !isEmpty(getSclass())
			|| !isEmpty(getContext()) || !isEmpty(getTooltip())
			|| !isEmpty(getTooltiptext()) || !isEmpty(getPopup())
			|| !"false".equals(getDraggable())
			|| !"false".equals(getDroppable())
			|| isAsapRequired(Events.ON_RIGHT_CLICK)
			|| !isEmpty(getAction())
			|| !isEmpty(getLeft()) || !isEmpty(getTop())
			|| !isEmpty(getWidth()) || !isEmpty(getHeight())
			|| isAsapRequired(Events.ON_DOUBLE_CLICK);
	}
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	private static boolean isRawLabel(Component comp) {
		final LanguageDefinition langdef =
			comp.getDefinition().getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	/** Returns the text for generating HTML tags (Internal Use Only).
	 *
	 * <p>Used only for component generation. Not for applications.
	 */
	public String getEncodedText() {
		StringBuffer sb = null;
		final int len = _value.length();
		if (_pre || _multiline) {
			for (int j = 0, k;; j = k + 1) {
				k = _value.indexOf('\n', j);
				if (k < 0) {
					sb = encodeLine(sb, j, len);
					break;
				}

				if (sb == null) {
					assert j == 0;
					sb = new StringBuffer(_value.length() + 10);
				}
				sb = encodeLine(sb, j,
					k > j && _value.charAt(k - 1) == '\r' ? k - 1: k);
				sb.append("<br/>");
			}
		} else {
			sb = encodeLine(null, 0, len);
		}
		return sb != null ? sb.toString(): _value;
	}
	/*
	 * @param k excluded
	 */
	private StringBuffer encodeLine(StringBuffer sb, int b, int e) {
		boolean prews = _pre || _multiline;
		int linesz = 0;
		if (_maxlength > 0) {
			int deta = e - b;
			if (deta > _maxlength) {
				if (_hyphen) {
					linesz = _maxlength;
				} else if (!prews) {
					assert b == 0;
					int j = _maxlength;
					while (j > 0 && Character.isWhitespace(_value.charAt(j - 1)))
						--j;
					return new StringBuffer(j + 3)
						.append(_value.substring(0, j)).append("...");
				}
			}
		}

		l_linebreak:
		for (int cnt = 0, j = b; j < e; ++j) {
			final char cc = _value.charAt(j);
			String val = null;
			if (linesz > 0  && ++cnt > linesz && j + 1 < e) {
				sb = alloc(sb, j);
				if (Character.isLetterOrDigit(cc)
				&& Character.isLetterOrDigit(_value.charAt(j+1))) {
					cnt = 0;
					for (int k = sb.length(); cnt < 3; ++cnt) {
						if (!Character.isLetterOrDigit(sb.charAt(--k))) {
							sb.insert(k + 1, "<br/>");
							--j;
							continue l_linebreak;
						}
					}
					sb.append('-').append("<br/>").append(cc);
					cnt = 1;
					continue;
				} else if (!Character.isWhitespace(cc)) {
					sb.append(cc);
				}
				sb.append("<br/>");
				cnt = 0;
				continue;
			}

			if (cc == '\t') {
				if (prews) val = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			} else if (cc == ' ' || cc == '\f') {
				if (prews) val = "&nbsp;";
			} else {
				if (_multiline) prews = false;

				switch (cc) {
				case '<': val = "&lt;"; break;
				case '>': val = "&gt;"; break;
				case '&': val = "&amp;"; break;
				}
			}

			if (val != null) sb = alloc(sb, j).append(val);
			else if (sb != null) sb.append(cc);
		}
		return sb;
	}
	private StringBuffer alloc(StringBuffer sb, int e) {
		if (sb == null) {
			sb = new StringBuffer(_value.length() + 10);
			sb.append(_value.substring(0, e));
		}
		return sb;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs();
		return clkattrs == null ? attrs: attrs + clkattrs;
	}

	//-- Component --//
	public void invalidate() {
		if (isIdRequired()) super.invalidate();
		else getParent().invalidate();
	}
	public void redraw(Writer out) throws IOException {
		if (isIdRequired()) super.redraw(out);
		else out.write(getEncodedText());
			//no processing; direct output if not ZUL
	}
	/** No child is allowed.
	 */
	public boolean isChildable() {
		return false;
	}
}
