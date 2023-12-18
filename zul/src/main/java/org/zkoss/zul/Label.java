/* Label.java

	Purpose:
		
	Description:
		
	History:
		Wed Jun  8 18:53:53     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zul;

import java.io.IOException;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.BooleanPropertyAccess;
import org.zkoss.zk.ui.sys.IntPropertyAccess;
import org.zkoss.zk.ui.sys.PropertyAccess;
import org.zkoss.zk.ui.sys.StringPropertyAccess;
import org.zkoss.zul.impl.XulElement;

/**
 * A label.
 * 
 * <p>Default {@link #getZclass}: z-label.
 *
 * @author tomyeh
 */
public class Label extends XulElement {
	private String _value = "";
	private AuxInfo _auxinf;

	public Label() {
	}

	public Label(String value) {
		setValue(value);
	}

	/** Returns the value.
	 * <p>Default: "".
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
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
			smartUpdate("value", getValue());
			//allow deriving to override getValue()
		}
	}

	/** Returns the maximal length of the label.
	 * <p>Default: 0 (means no limitation)
	 */
	public int getMaxlength() {
		return _auxinf != null ? _auxinf.maxlength : 0;
	}

	/** Sets the maximal length of the label.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0)
			maxlength = 0;
		if ((_auxinf != null ? _auxinf.maxlength : 0) != maxlength) {
			initAuxInfo().maxlength = maxlength;
			smartUpdate("maxlength", getMaxlength());
		}
	}

	/** Returns whether to preserve the new line and the white spaces at the
	 * beginning of each line.
	 */
	public boolean isMultiline() {
		return _auxinf != null && _auxinf.multiline;
	}

	/** Sets whether to preserve the new line and the white spaces at the
	 * beginning of each line.
	 */
	public void setMultiline(boolean multiline) {
		if ((_auxinf != null && _auxinf.multiline) != multiline) {
			initAuxInfo().multiline = multiline;
			smartUpdate("multiline", isMultiline());
		}
	}

	/** Returns whether to preserve the white spaces, such as space,
	 * tab and new line.
	 *
	 * <p>It is the same as style="white-space:pre". However, IE has a bug when
	 * handling such style if the content is updated dynamically.
	 * Refer to Bug 1455584.
	 *
	 * <p>Note: the new line is preserved either {@link #isPre} or
	 * {@link #isMultiline} returns true.
	 * In other words, <code>pre</code> implies <code>multiline</code>
	 */
	public boolean isPre() {
		return _auxinf != null && _auxinf.pre;
	}

	/** Sets whether to preserve the white spaces, such as space,
	 * tab and new line.
	 */
	public void setPre(boolean pre) {
		if ((_auxinf != null && _auxinf.pre) != pre) {
			initAuxInfo().pre = pre;
			smartUpdate("pre", isPre());
		}
	}

	//--ComponentCtrl--//
	private static HashMap<String, PropertyAccess> _properties = new HashMap<String, PropertyAccess>(3);

	static {
		_properties.put("value", new StringPropertyAccess() {
			public void setValue(Component cmp, String value) {
				((Label) cmp).setValue(value);
			}

			public String getValue(Component cmp) {
				return ((Label) cmp).getValue();
			}
		});
		_properties.put("multiline", new BooleanPropertyAccess() {
			public void setValue(Component cmp, Boolean multiline) {
				((Label) cmp).setMultiline(multiline);
			}

			public Boolean getValue(Component cmp) {
				return ((Label) cmp).isMultiline();
			}
		});
		_properties.put("maxlength", new IntPropertyAccess() {
			public void setValue(Component cmp, Integer maxlength) {
				((Label) cmp).setMaxlength(maxlength);
			}

			public Integer getValue(Component cmp) {
				return ((Label) cmp).getMaxlength();
			}
		});
	}

	public PropertyAccess getPropertyAccess(String prop) {
		PropertyAccess pa = _properties.get(prop);
		if (pa != null)
			return pa;
		return super.getPropertyAccess(prop);
	}

	//super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws IOException {
		super.renderProperties(renderer);

		int v = getMaxlength();
		if (v > 0)
			renderer.render("maxlength", v);
		render(renderer, "multiline", isMultiline());
		render(renderer, "pre", isPre());

		final String val = getValue();
		//allow deriving to override getValue()
		render(renderer, "value", val);
		org.zkoss.zul.impl.Utils.renderCrawlableText(val);
	}

	public String getZclass() {
		return _zclass == null ? "z-label" : _zclass;
	}

	//Cloneable//
	public Object clone() {
		final Label clone = (Label) super.clone();
		if (_auxinf != null)
			clone._auxinf = (AuxInfo) _auxinf.clone();
		return clone;
	}

	/** No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}

	private AuxInfo initAuxInfo() {
		if (_auxinf == null)
			_auxinf = new AuxInfo();
		return _auxinf;
	}

	private static class AuxInfo implements java.io.Serializable, Cloneable {
		private int maxlength;
		private boolean multiline;
		private boolean pre;

		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}
		}
	}
}
