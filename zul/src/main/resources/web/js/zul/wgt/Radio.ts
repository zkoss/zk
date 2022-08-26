/* Radio.ts

	Purpose:

	Description:

	History:
		Tue Dec 16 11:17:26     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A radio button.
 *
 * <p>Radio buttons without an ancestor {@link Radiogroup} is considered
 * as the same group.
 * The nearest ancestor {@link Radiogroup} is the group that the radio
 * belongs to. See also {@link #getRadiogroup}.
 *
 * <p>Event:
 * <ol>
 * <li>onCheck event is sent when a checkbox
 * is checked or unchecked by user.</li>
 * </ol>
 *
 */
@zk.WrapClass('zul.wgt.Radio')
export class Radio extends zul.wgt.Checkbox {
	_attachExternal?: boolean = undefined;
	_group?: zul.wgt.Radiogroup;

	/** Returns {@link Radiogroup} that this radio button belongs to.
	 * It is the nearest ancestor {@link Radiogroup}.
	 * In other words, it searches up the parent, parent's parent
	 * and so on for any {@link Radiogroup} instance.
	 * If found this radio belongs the found radiogroup.
	 * If not, this radio itself is a group.
	 * @return Radiogroup
	 */
	getRadiogroup(parent?: zk.Widget): zul.wgt.Radiogroup | undefined {
		if (!parent && this._group)
			return this._group;
		var wgt = parent ?? this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt instanceof zul.wgt.Radiogroup) return wgt;
		return undefined;
	}

	/** Sets {@link Radiogroup} that this radio button belongs to.
	 * The radio automatically belongs to the nearest ancestral radiogroup.
	 * Use this method only if the radio group is not one of its ancestors.
	 * @param Radiogroup group the radio group, or null to dis-associate
	 * @since 5.0.4
	 */
	setRadiogroup(radiogroup?: zul.wgt.Radiogroup | string): this {
		var old = this._group;
		// for zephyr to support set radiogroup by id
		if (typeof radiogroup == 'string')
			radiogroup = zk.$<zul.wgt.Radiogroup>('$' + radiogroup);
		if (old !== radiogroup) {
			if (old && this._attachExternal) old._rmExtern(this);
			this._group = radiogroup;
			if (radiogroup && this.desktop) {
				radiogroup._addExtern(this);
				this._attachExternal = true;
			}
			this._fixName();
		}
		return this;
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this._group && this.desktop && !this._attachExternal) {
			this._group._addExtern(this);
			this._attachExternal = true;
		}
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._group && this._attachExternal) {
			this._group._rmExtern(this);
			this._attachExternal = false;
		}
		super.unbind_(skipper, after, keepRod);
	}

	/** Sets the radio is checked and unchecked the others in the same radio
	 * group ({@link Radiogroup}
	 * @param boolean checked
	 * @return Radio
	 * @disable(zkgwt)
	 */
	override setChecked(checked: boolean, opts?: Record<string, boolean>): this {
		if (checked != this._checked) {
			this._checked = checked;
			var n = this.$n<HTMLInputElement>('real');
			if (n) {
				n.checked = checked || false;
				checked ? jq(n).attr('checked', 'checked') : jq(n).removeAttr('checked');
				this.clearStateClassName_();
				jq(this.$n_()).addClass(this.getClassNameByState_());
				// Bug ZK-622
				if (!n.checked)
					jq(n).removeAttr('checked');

				var group = this.getRadiogroup();
				if (group) {
					// bug #1893575 : we have to clean all of the radio at the same group.
					if (checked) {
						for (var items = group.getItems(), i = items.length; i--;) {
							if (items[i] != this) {
								var item = items[i].$n<HTMLInputElement>('real');
								if (item) {
									item.checked = false;
									jq(item).removeAttr('checked');
								}
								items[i].clearStateClassName_();
								jq(items[i].$n_()).addClass(this.$s(this.getMoldPrefix_() + 'off'));
								items[i]._checked = false;
							}
						}
					}
					group._fixSelectedIndex();
				}
			}
		}
		return this;
	}

	/** Sets whether it is selected.
	 * <p>Don't override this. Override {@link #setChecked} instead.
	 * <p>The same as {@link #setChecked}.
	 * @param boolean selected
	 * @return Radio
	 */
	setSelected(selected: boolean, opts?: Record<string, boolean>): this {
		return this.setChecked(selected, opts);
	}

	/** Returns whether it is selected.
	 * <p>Default: false.
	 * <p>Don't override this. Override {@link #isChecked} instead.
	 * @return boolean
	 */
	isSelected(): boolean {
		return this.isChecked();
	}

	/** Returns the name of this radio button.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * <p>It is readonly, and it is generated automatically
	 * to be the same as its parent's name ({@link Radiogroup#getName}).
	 * @return String
	 */
	override getName(): string | undefined {
		var group = this.getRadiogroup();
		return group != null ? group.getName() : this.uuid;
	}

	_fixName(): void {
		var n = this.$n<HTMLInputElement>('real');
		if (n)
			n.name = this.getName()!;
	}

	override beforeParentChanged_(newParent?: zk.Widget): void {
		var oldGroup = this.getRadiogroup(),
			newGroup = newParent ? this.getRadiogroup(newParent) : undefined;
		if (oldGroup != newGroup || !newParent) {
			if (oldGroup && oldGroup instanceof zul.wgt.Radiogroup) {
				oldGroup._fixOnRemove(this);
				if (this._attachExternal) {
					oldGroup._rmExtern(this);
					this._attachExternal = false;
				}
			}
			if (newGroup && newGroup instanceof zul.wgt.Radiogroup) {
				if (!this._attachExternal && newGroup == this._group) {
					newGroup._addExtern(this);
					this._attachExternal = true;
				}
				newGroup._fixOnAdd(this);
			}
		}
		super.beforeParentChanged_(newParent);
	}

	override fireOnCheck_(checked: boolean): void {
		// if Radiogroup listens to onCheck, we shall fire the event too.
		var group = this.getRadiogroup();
		this.fire('onCheck', checked);

		// send event to group at client instead of server side. in ZK 10 for zephyr
		if (group) {
			group.fire('onCheck', checked);
		}
	}
}