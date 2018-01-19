/* Radio.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:20:52     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zul.impl.Utils;

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
public class Radio extends Checkbox {

	/** At most one of _group and _groupId will be non-null. */
	private Radiogroup _group;
	/** At most one of _group and _groupId will be non-null. */
	private String _groupId;
	private boolean _attachExternal = false;
	private boolean _explictGroup = false;

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
		resolveGroup(false);
		if (_group != null)
			return _group;

		for (Component p = this;;) {
			Component q = p.getParent();
			if ((q instanceof Radiogroup) || q == null)
				return (Radiogroup) q;
			p = q;
		}
	}

	/** Associates the radiogroup to this radio component.
	 * The radio automatically belongs to the nearest ancestral radiogroup.
	 * Use this method only if the radio group is not one of its ancestors.
	 * @since 5.0.4
	 */
	public void setRadiogroup(Radiogroup radiogroup) {
		boolean inGroup = _groupId != null;
		_groupId = null;
		if (inGroup || radiogroup != _group) {
			_explictGroup = radiogroup != null;
			if (_group != null) {
				_group.removeExternal(this);
				_attachExternal = false;
			}
			_group = radiogroup;

			//ZK-1073 it's better not to add the external when component is not attached.
			if (_group != null && getDesktop() != null) {
				_group.addExternal(this);
				_attachExternal = true;
			}

			smartUpdate("radiogroup", _group);
		}
	}

	/** Associates the radiogroup to this radio component by giving ID.
	 * The radio automatically belongs to the nearest ancestral radiogroup.
	 * Use this method only if the radio group is not one of its ancestors.
	 * @param radiogroupId the ID of the radiogroup. To specify UUID,
	 * use the format: <code>uuid(comp_uuid)</code>.
	 * @since 5.0.4
	 */
	public void setRadiogroup(String radiogroupId) {
		if (radiogroupId == null) {
			setRadiogroup((Radiogroup) null);
			return;
		}

		_group = null;
		_groupId = radiogroupId;
		_explictGroup = true;
		if (resolveGroup(true)) //try to bind as soon as possible since they relate to each other
			smartUpdate("radiogroup", _group);
		else
			invalidate(); //delay the retrieval of _group to redraw
	}

	/** @param silent whether NOT to throw an exception if not found. */
	private boolean resolveGroup(boolean silent) {
		if (_groupId != null) {
			_group = (Radiogroup) Utils.getComponentById(this, _groupId);
			if (_group == null) {
				if (!silent)
					throw new WrongValueException("Radiogroup not found: " + _groupId);
				return false;
			}
			_groupId = null;
			_group.addExternal(this);
			_attachExternal = true;
		}
		return true;
	}

	/** Returns whether it is selected.
	 * <p>Default: false.
	 * <p>Don't override this. Override {@link #isChecked} instead.
	 */
	public boolean isSelected() {
		return isChecked();
	}

	/** Sets whether it is selected.
	 * <p>Don't override this. Override {@link #setChecked} instead.
	 * <p>The same as {@link #setChecked}.
	 */
	public void setSelected(boolean selected) {
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
					if (byclient) {
						sib._checked = false;
					} else {
						sib.setChecked(false); //and fixSelectedIndex
						return;
					}
				}
			}
			group.fixSelectedIndex();
			group.syncSelectionToModel();
		}
	}

	/** Returns the name of this radio button.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>It is readonly, and it is generated automatically
	 * to be the same as its parent's name ({@link Radiogroup#getName}).
	 */
	public String getName() {
		final Radiogroup group = getRadiogroup();
		return group != null ? group.getName() : getUuid();
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

	//-- Component --//
	public void setParent(Component parent) {
		Radiogroup oldgp = null;

		if (getParent() != null)
			oldgp = getRadiogroup();

		super.setParent(parent);

		Radiogroup newgp = null;

		if (parent != null)
			newgp = getRadiogroup();

		if (oldgp != newgp) {
			if (oldgp != null) { //removed from the component tree  
				if (_explictGroup && oldgp == _group) {
					_group.removeExternal(this);
					_attachExternal = false;
				}
			}
			if (newgp != null) {
				if (_explictGroup && !_attachExternal && newgp == _group) {
					_group.addExternal(this);
					_attachExternal = true;
				}
			}
		}
	}

	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);
		resolveGroup(false);
		render(renderer, "radiogroup", _group);
	}

	//-- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_CHECK)) {
			CheckEvent evt = CheckEvent.getCheckEvent(request);
			_checked = evt.isChecked();
			fixSiblings(_checked, true); //invoke syncSelectionToModel
			Events.postEvent(evt);
			// Bug: B50-3284663: Radio always sends onCheck event
			final Radiogroup rg = getRadiogroup();
			if (rg != null)
				Events.postEvent(rg, evt);
		} else
			super.service(request, everError);
	}

	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);
		//B65-ZK-1768 remove the radio from the radiogroup in case it is an external radio
		final Radiogroup rg = getRadiogroup();
		if (rg != null) {
			rg.removeExternal(this);
		}

		// ZK-3818: update selected index in the callback
		if (!_explictGroup) {
			_group = null;
		}
		Component rootParent = ComponentsCtrl.getRootParent();
		if (rg != null && rootParent != null && rootParent instanceof ComponentCtrl
				&& (!Components.isAncestor(rootParent, rg) || rootParent == rg)) {
			final Radio radio = this;
			((ComponentCtrl) rootParent).addCallback(AFTER_CHILD_REMOVED, new Callback<Component>() {
				public void call(Component rootParent) {
					rg.fixOnRemove(radio);
				}
			});
		}
	}

	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);
		Component rootParent = ComponentsCtrl.getRootParent();

		// ZK-3818: update selected index in the callback
		if (!_explictGroup) {
			_group = this.getRadiogroup();
		}
		if (_group != null && rootParent != null && rootParent instanceof ComponentCtrl
				&& (!Components.isAncestor(rootParent, _group) || rootParent == _group)) {
			final Radio radio = this;
			((ComponentCtrl) rootParent).addCallback(AFTER_CHILD_ADDED, new Callback<Component>() {
				public void call(Component rootParent) {
					_group.fixOnAdd(radio);
				}
			});
		}
	}
}
