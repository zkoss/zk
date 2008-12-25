/* Radio.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:52     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 * A radio button.
 *
 * <p>Radio buttons without a ancestor {@link Radiogroup} is considered
 * as the same group.
 * The nearest ancestor {@link Radiogroup} is the group that the radio
 * belongs to. See also {@link #getRadiogroup}.
 * 
 * <p>Event:
 * <ol>
 * <li>{@link org.zkoss.zk.ui.event.CheckEvent} is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 * @author tomyeh
 */
public class Radio extends Checkbox implements org.zkoss.zul.api.Radio {
	private String _value = "";

	public Radio() {
	}
	public Radio(String label) {
		super(label);
	}
	public Radio(String label, String image) {
		super(label, image);
	}

	/** Returns {@link Radiogroup} that this radio button belongs to.
	 * It is the nearest ancestor {@link Radiogroup}.
	 * In other words, it searches up the parent, parent's parent
	 * and so on for any {@link Radiogroup} instance.
	 * If found this radio belongs the found radiogroup.
	 * If not, this radio itself is a group.
	 */
	public Radiogroup getRadiogroup() {
		for (Component p = this;;) {
			Component q = p.getParent();
			if ((q instanceof Radiogroup) || q == null)
				return (Radiogroup)q;
			p = q;
		}
	}
	/** Returns {@link Radiogroup} that this radio button belongs to.
	 * It is the nearest ancestor {@link Radiogroup}.
	 * In other words, it searches up the parent, parent's parent
	 * and so on for any {@link Radiogroup} instance.
	 * If found this radio belongs the found radiogroup.
	 * If not, this radio itself is a group.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Radiogroup getRadiogroupApi() {
		return getRadiogroup();
	}

	/** Returns whether it is selected.
	 * <p>Default: false.
	 * <p>Don't override this. Override {@link #isChecked} instead.
	 */
	public final boolean isSelected() {
		return isChecked();
	}
	/** Sets whether it is selected.
	 * <p>Don't override this. Override {@link #setChecked} instead.
	 * <p>The same as {@link #setChecked}.
	 */
	public final void setSelected(boolean selected) {
		setChecked(selected);
	}
	/** Sets the radio is checked and unchecked the others in the same radio
	 * group ({@link Radiogroup}.
	 */
	public void setChecked(boolean checked) {
		if (checked != isChecked()) {
			super.setChecked(checked);
			fixSiblings(checked, false);
		}
	}
	/** Make sure only one of them is checked. */
	private void fixSiblings(boolean checked, boolean byclient) {
		final Radiogroup group = getRadiogroup();
		if (group != null) {
			if (checked) {
				final Radio sib = group.getSelectedItem();
				if (sib != null && sib != this) {
					if (byclient)
						((ExtraCtrl)sib.getExtraCtrl()).setCheckedByClient(false);
					else
						sib.setChecked(false); //and fixSelectedIndex
					return;
				}
			}
			group.fixSelectedIndex();
		}
	}

	/** Returns the value.
	 * <p>Default: "".
	 */
	public String getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value; If null, it is considered as empty.
	 */
	public void setValue(String value) {
		if (value == null)
			value = "";
		if (!Objects.equals(_value, value)) {
			_value = value;
			smartUpdate("value", _value);
		}
	}

	/** Returns the name of this radio button.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>It is readonly, and it is generated automatically
	 * to be the same as its parent's name ({@link Radiogroup#getName}).
	 */
	public final String getName() {
		final Radiogroup group = getRadiogroup();
		return group != null ? group.getName(): getUuid();
	}

	/** Returns the inner attributes for generating the HTML radio tag
	 * (the name and value attribute).
	 * <p>Used only by component developers.
	 */
	public String getInnerAttrs() {
		final StringBuffer sb =
			new StringBuffer(64).append(super.getInnerAttrs());
		HTMLs.appendAttribute(sb, "value",  getValue());
		return sb.toString();
	}
	/** Returns the Style of radio label
	 *
	 * <p>Default: "z-radio"
	 * <p>Since 3.5.1
	 * 
	 */
	public String getZclass() {
		return _zclass == null ? "z-radio" : _zclass;
	}
	
	/** Process the onCheck event sent when the radio is checked.
	 * @since 3.5.3
	 */
	public void onCheck(Event event) {
		final Radiogroup rg = getRadiogroup();
		if (rg != null)
			Events.sendEvent(rg, event);
	}
	
	//-- Component --//
	public void setParent(Component parent) {
		final Radiogroup oldgp = getRadiogroup();
		super.setParent(parent);

		final Radiogroup newgp = getRadiogroup();
		if (oldgp != newgp) {
			if (oldgp != null) oldgp.fixOnRemove(this);
			if (newgp != null) newgp.fixOnAdd(this);
		}
	}

	//-- ComponentCtrl --//
	protected Object newExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends Checkbox.ExtraCtrl {
		//-- Checkable --//
		public void setCheckedByClient(boolean checked) {
			super.setCheckedByClient(checked);
			fixSiblings(checked, true);
		}
	}
}
