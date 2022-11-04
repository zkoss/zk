/* Errorbox.ts

	Purpose:

	Description:

	History:
		Sun Jan 11 21:17:56     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

var _dirMap = {
	'u': 'up',
	'd': 'down',
	'l': 'left',
	'r': 'right'
};
/**
 * A error message box that is displayed as a popup.
 */
@zk.WrapClass('zul.inp.Errorbox')
export class Errorbox extends zul.wgt.Notification {
	override parent: zul.inp.InputWidget | undefined;
	/** @internal */
	_defaultPos = 'end_before';
	msg: string;
	sclass?: string;
	iconSclass: string;
	/** @internal */
	__ebox?: Errorbox;

	constructor(owner: zul.inp.InputWidget, msg: string) {
		super(msg, { ref: owner });
		this.parent = owner;
		this.parent.__ebox = this;
		this.msg = msg;
		this.sclass = owner._errorboxSclass;
		this.iconSclass = owner._errorboxIconSclass || 'z-icon-exclamation-triangle';
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var sclass = this.sclass,
			s = super.domClass_(no);
		if (sclass)
			s += ' ' + sclass;
		return s;
	}

	/**
	 * Opens the popup.
	 * @see {@link zul.wgt.Popup.open}
	 */
	override show(): this {
		if (!this.$n())
			jq(document.body).append(this);

		// Fixed IE6/7 issue in B50-2941554.zul
		var cstp = this.parent!._cst && (this.parent!._cst as zul.inp.SimpleConstraint)._pos;
		// ZK-2069: show only if is in view //B85-ZK-3321
		if (this.parent!.isRealVisible()) {
			setTimeout(() => {
				if (this.parent && zul.inp.InputWidget._isInView(this)) //Bug #3067998: if
					this.open(this.parent, undefined, cstp || this._defaultPos, { dodgeRef: !cstp });
			}, 50); // B36-2935398: add time
		}
		zWatch.listen({ onHide: [this.parent, this.onParentHide] });
		return this;
	}

	/**
	 * Destroys the errorbox
	 */
	destroy(): void {
		if (this.parent) {
			zWatch.unlisten({ onHide: [this.parent, this.onParentHide] });
			delete this.parent.__ebox;
		}
		this.close();
		this.unbind();
		jq(this).remove();
		this.parent = undefined;
	}

	onParentHide(): void {
		if (this.__ebox) {
			this.__ebox.setFloating_(false);
			this.__ebox.close();
		}
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		this._drag = new zk.Draggable(this, undefined, {
			starteffect: zk.$void,
			endeffect: Errorbox._enddrag,
			ignoredrag: Errorbox._ignoredrag,
			change: Errorbox._change
		});
		zWatch.listen({ onMove: this });
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		// bug ZK-1143
		var drag = this._drag;
		this._drag = undefined;
		if (drag)
			drag.destroy();
		zWatch.unlisten({ onMove: this });

		// just in case
		if (this.parent)
			zWatch.unlisten({ onHide: [this.parent, this.onParentHide] });

		super.unbind_(skipper, after, keepRod);
	}

	override getInputNode(): HTMLInputElement | undefined {
		return this.parent ? this.parent.$n() : undefined;
	}

	onMove(): void {
		if (this.isOpen()) {
			this.reposition(); //call reposition in super
			this._fixarrow();
		}
	}

	override onSize(): void {
		super.onSize();
		if (this.isOpen())
			this._fixarrow();
	}

	/** @internal */
	override setDomVisible_(domVisible: HTMLElement, visible: boolean, opts?: zk.DomVisibleOptions): void {
		super.setDomVisible_(domVisible, visible, opts);
		const stackup = this._stackup;
		if (stackup) stackup.style.display = visible ? '' : 'none';
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		interface WidgetErrorMessage extends zk.Widget {
			clearErrorMessage?(revalidate: boolean, remainError?: boolean): void;
		}
		var p: HTMLElement | WidgetErrorMessage | undefined = evt.domTarget;
		if (p == this.$n('cls') || p == this.$n('clsIcon')) { //may click on font-icon
			if ((p = this.parent) && p.clearErrorMessage) {
				p.clearErrorMessage(true, true);
				p.focus(0); // Bug #3159848
			} else
				zAu.wrongValue_(p!, false);
		} else {
			super.doClick_(evt, popupOnly);
			this.parent!.focus(0);
		}
	}

	override open(ref: zk.Widget, offset?: zk.Offset, position?: string, opts?: zul.wgt.PopupOptions): void {
		super.open(ref, offset, position, opts);
		this.setTopmost();
		this._fixarrow();
	}

	/** @internal */
	override afterCloseAnima_(opts?: zul.wgt.PopupOptions): void {
		opts = zk.copy(opts, { keepVisible: true });
		super.afterCloseAnima_(opts);
	}

	override redraw(out: string[]): void {
		var uuid = this.uuid,
			icon = this.$s('icon'),
			iconSclass = this.iconSclass;
		out.push('<div', this.domAttrs_(), '><div id="', uuid, '-p" class="',
			this.$s('pointer'), '"></div><i id="', uuid, '-icon" class="',
			//ZK-2677 use either default or self-defined icon, do not rely on CSS overwrite
			icon, ' ', iconSclass, '"></i><div id="', uuid,
			'-cave" class="', this.$s('content'), '" title="',
			(zUtl.encodeXML(msgzk.GOTO_ERROR_FIELD)), '">',
			zUtl.encodeXML(this.msg, { multiline: true }),
			'</div><div id="', uuid, '-cls" class="',
			// Bug ZK-2952: added missing id for the "x" icon
			this.$s('close'), '"><i id="', uuid, '-clsIcon" class="', icon,
			' z-icon-times"></i></div></div>');
	}

	override onFloatUp(ctl: zk.ZWatchController): void {
		var wgt = ctl.origin as zk.Widget | undefined;
		if (wgt == this) {
			this.setTopmost();
			return;
		}
		if (!wgt || wgt == this.parent || !this.isVisible())
			return;

		// eslint-disable-next-line @typescript-eslint/no-this-alias
		var top1: zk.Widget | undefined = this,
			top2: zk.Widget | undefined = wgt;
		while ((top1 = top1.parent) && !top1.isFloating_())
			if (top1 == wgt) //wgt is parent
				return;
		for (; top2 && !top2.isFloating_(); top2 = top2.parent)
			;
		if (top1 == top2) { //uncover if sibling
			var n = wgt.$n();
			if (n) this._uncover(n);
		}
	}

	/** @internal */
	_uncover(el: HTMLElement): void {
		var elofs = zk(el).revisedOffset(),
			node = this.$n()!,
			nodeofs = zk(node).cmOffset();

		if (jq.isOverlapped(
			elofs, [el.offsetWidth, el.offsetHeight],
			nodeofs, [node.offsetWidth, node.offsetHeight])) {
			var parent = this.parent!.$n_(),
				ptofs = zk(parent).cmOffset(),
				pthgh = parent.offsetHeight,
				ptbtm = ptofs[1] + pthgh,
				y = elofs[1] + el.offsetHeight <= ptbtm ? ptbtm : ptofs[1] - node.offsetHeight,
				/* we compare bottom because default is located below */
				ofs = zk(node).toStyleOffset(0, y);
			node.style.top = `${ofs[1]}px`;
			this._fixarrow();
		}
	}

	/** @internal */
	override _fixarrow(): void {
		var parent = this.parent!.$n()!,
			node = this.$n()!,
			pointer = this.$n('p')!,
			ptofs = zk(parent).revisedOffset(),
			nodeofs = zk(node).revisedOffset(),
			dx = nodeofs[0] - ptofs[0],
			dy = nodeofs[1] - ptofs[1],
			dir: string,
			s = node.style,
			pw = 2 + (zk(pointer).borderWidth() / 2) || 0,
			ph = 2 + (zk(pointer).borderHeight() / 2) || 0;

		// conditions of direction
		if (dx >= parent.offsetWidth - pw)
			dir = dy < ph - node.offsetHeight ? 'ld' : dy >= parent.offsetHeight - ph ? 'lu' : 'l';
		else if (dx < pw - node.offsetWidth)
			dir = dy < ph - node.offsetHeight ? 'rd' : dy >= parent.offsetHeight - ph ? 'ru' : 'r';
		else
			dir = dy < 0 ? 'd' : 'u';

		node.style.padding = '0';
		// for setting the pointer position
		if (dir == 'd' || dir == 'u') {
			var md = (Math.max(dx, 0) + Math.min(node.offsetWidth + dx, parent.offsetWidth)) / 2 - dx - 6,
				mx = node.offsetWidth - 11;
			pointer.style.left = `${md > mx ? mx : md < 1 ? 1 : md}px`;
			if (dir == 'd') {
				pointer.style.top = undefined as unknown as string;
				pointer.style.bottom = '-4px';
				s.paddingBottom = `${ph}px`;
			} else {
				pointer.style.top = '-4px';
				s.paddingTop = `${ph}px`;
			}

		} else if (dir == 'l' || dir == 'r') {
			var md = (Math.max(dy, 0) + Math.min(node.offsetHeight + dy, parent.offsetHeight)) / 2 - dy - 6,
				mx = node.offsetHeight - 11;
			pointer.style.top = `${md > mx ? mx : md < 1 ? 1 : md}px`;
			if (dir == 'r') {
				pointer.style.left = undefined as unknown as string;
				pointer.style.right = '-4px';
				s.paddingRight = `${pw}px`;
			} else {
				pointer.style.left = '-4px';
				s.paddingLeft = `${pw}px`;
			}

		} else {
			var ps = pointer.style;
			ps.left = ps.top = ps.right = ps.bottom = undefined as unknown as string;
			switch (dir) {
				case 'lu':
					ps.left = '0px';
					ps.top = '-4px';
					s.paddingTop = `${ph}px`;
					break;
				case 'ld':
					ps.left = '0px';
					ps.bottom = '-4px';
					s.paddingBottom = `${ph}px`;
					break;
				case 'ru':
					ps.right = '0px';
					ps.top = '-4px';
					s.paddingTop = `${ph}px`;
					break;
				case 'rd':
					ps.right = '0px';
					ps.bottom = '-4px';
					s.paddingBottom = `${ph}px`;
					break;
			}
			dir = dir == 'ru' || dir == 'lu' ? 'u' : 'd';
		}

		pointer.className = this.$s('pointer') + (_dirMap[dir] ? ' ' + this.$s(_dirMap[dir as keyof typeof _dirMap]) : '');
		jq(pointer).show();
	}

	/** @internal */
	override isInView_(): boolean {
		return zul.inp.InputWidget._isInView(this);
	}

	/** @internal */
	override getPositionArgs_(): zul.wgt.PositionArgs {
		var p = this.parent, cstp = p ? p._cst && (p._cst as zul.inp.SimpleConstraint)._pos : false;
		return [p, undefined, cstp || 'end_before', { dodgeRef: !cstp }];
	}

	/** @internal */
	static _enddrag(dg: zk.Draggable): void {
		var errbox = dg.control as Errorbox;
		errbox.setTopmost();
		errbox._fixarrow();
	}

	/** @internal */
	static _ignoredrag(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		return zul.inp.InputCtrl.isIgnoredDragForErrorbox(dg, pointer, evt);
	}

	/** @internal */
	static _change(dg: zk.Draggable): void {
		var errbox = dg.control as Errorbox,
			stackup = errbox._stackup,
			el = errbox.$n()!;
		if (stackup) {
			stackup.style.top = el.style.top;
			stackup.style.left = el.style.left;
		}
		errbox._fixarrow();
		if (zk.mobile)
			zk(el).redoCSS();
	}
}