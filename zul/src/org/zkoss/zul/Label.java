/* Label.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 18:53:53     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
import org.zkoss.zk.ui.sys.ComponentCtrl;

import org.zkoss.zul.impl.XulElement;

/**
 * A label.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Label extends XulElement {
	private String _value = "";
	private int _maxlength;
	private boolean _pre, _hyphen;

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
	 * <dd>maxlength is the maximal length of each word, and hyphenation is added
	 * if a word exceeds maxlength.</dd>
	 * <dt>hyphen="false" and pre="true"</dt>
	 * <dd>maxlength has no effect.</dd>
	 * <dt>maxlength=0</dt>
	 * <dd>hypen has no effect</dd>
	 * </dl>
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
	/** Returns whether to hyphen a long word if maxlength is specified.
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

	/** Whether to generate the value directly without ID. */
	private boolean isIdRequired() {
		final Component p = getParent();
		return p == null || !isVisible() || !Components.isAutoId(getId())
			|| !isRawLabel(p) || isAsapRequired(Events.ON_CLICK)
			|| isAsapRequired(Events.ON_RIGHT_CLICK)
			|| isAsapRequired(Events.ON_DOUBLE_CLICK);
	}
	private static boolean isRawLabel(Component comp) {
		final LanguageDefinition langdef =
			((ComponentCtrl)comp).getMilieu().getLanguageDefinition();
		return langdef != null && langdef.isRawLabel();
	}

	/** Returns the text for generating HTML tags (Internal Use Only).
	 *
	 * <p>Used only for component generation. Not for applications.
	 */
	public String getEncodedText() {
		StringBuffer sb = null;
		final int len = _value.length();
		if (_pre) {
			for (int j = 0, k;; j = k + 1) {
				k = _value.indexOf('\n', j);
				if (k < 0) {
					sb = encodeText(sb, j, len);
					break;
				}

				if (sb == null)
					sb = new StringBuffer(_value.length() + 10);
				sb = encodeText(sb, j,
					k > j && _value.charAt(k - 1) == '\r' ? k - 1: k);
				sb.append("<br/>");
			}
		} else {
			sb = encodeText(null, 0, len);
		}
		return sb != null ? sb.toString(): _value;
	}
	/*
	 * @param k excluded
	 */
	private StringBuffer encodeText(StringBuffer sb, int j, int k) {
		int maxword = 0;
		if (_maxlength > 0) {
			int deta = k - j;
			if (deta > _maxlength) {
				if (_hyphen) {
					maxword = _maxlength;
				} else if (!_pre) {
					assert j == 0;
					j = _maxlength;
					while (j > 0 && Character.isWhitespace(_value.charAt(j - 1)))
						--j;
					return new StringBuffer(j + 3)
						.append(_value.substring(0, j)).append("...");
				}
			}
		}

		for (int cnt = 0, i = j; i < k; ++i) {
			final char cc = _value.charAt(i);
			String val = null;
			if (cc == '\t') {
				cnt = 0;
				if (_pre) val = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			} else if (cc == ' ') {
				cnt = 0;
				if (_pre) val = "&nbsp;";
			} else {
				if (maxword > 0  && ++cnt > maxword) {
					sb = alloc(sb, j, i).append("-<br/>");
					cnt = 1;
				}
				switch (cc) {
				case '<': val = "&lt;"; break;
				case '>': val = "&gt;"; break;
				case '&': val = "&amp;"; break;
				}
			}

			if (val != null) sb = alloc(sb, j, i).append(val);
			else if (sb != null) sb.append(cc);
		}
		return sb;
	}
	private StringBuffer alloc(StringBuffer sb, int j, int k) {
		if (sb == null) {
			sb = new StringBuffer(_value.length() + 10);
			sb.append(_value.substring(j, k));
		}
		return sb;
	}

	//-- super --//
	public String getOuterAttrs() {
		final String attrs = super.getOuterAttrs();
		final String clkattrs = getAllOnClickAttrs(false);
		return clkattrs == null ? attrs: attrs + clkattrs;
	}

	//-- Component --//
	public void invalidate() {
		if (isIdRequired()) super.invalidate();
		else getParent().invalidate();
	}
	public void redraw(Writer out) throws IOException {
		if (isIdRequired()) super.redraw(out);
		else out.write(getEncodedText()); //no processing; direct output if not ZUL
	}
	public boolean isChildable() {
		return false;
	}
}
