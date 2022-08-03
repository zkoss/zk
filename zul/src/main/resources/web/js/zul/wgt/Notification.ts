/* Notification.ts

	Purpose:

	Description:

	History:
		Thu Mar 15 17:12:46     2012, Created by simon

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
var _iconMap = {
		'warning': 'z-icon-exclamation-circle',
		'info': 'z-icon-info-circle',
		'error': 'z-icon-times-circle'
	} as const,
	_dirMap = {
		'u': 'up',
		'd': 'down',
		'l': 'left',
		'r': 'right'
	} as const;
export type NotificationType = keyof typeof _iconMap;

export interface NotificationOptions {
	ref?: zk.Widget;
	pos?: string;
	off?: zk.Offset;
	dur?: number;
	type?: zul.wgt.NotificationType;
	closable?: boolean;
}
/**
 * A notification widget.
 * @since 6.0.1
 */
@zk.WrapClass('zul.wgt.Notification')
export class Notification extends zul.wgt.Popup {
	override _keepVisible = true;
	_closable?: boolean;
	_dur?: number;
	_nftPos?: string;
	_dir?: keyof typeof _dirMap | 'n';
	_ref?: zk.Widget;
	_msg: string;
	_type: NotificationOptions['type'];

	constructor(msg: string, opts: NotificationOptions) {
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-expect-error
		super(msg, opts); // FIXME: doesn't match zk.Widget.prototype.constructor
		this._msg = msg;
		this._type = opts.type;
		this._ref = opts.ref;
		this._dur = opts.dur;
		this._closable = opts.closable;
	}

	override redraw(out: string[]): void {
		var uuid = this.uuid,
			icon = this.$s('icon');
		out.push('<div', this.domAttrs_(), '>');
		if (this._ref) //output arrow if reference exist
			out.push('<div id="', uuid, '-p" class="', this.$s('pointer'), '"></div>');
		out.push('<i id="', uuid, '-icon" class="', icon, ' ', (_iconMap[this._type!]), '"></i>');
		out.push('<div id="', uuid, '-cave" class="', this.$s('content'), '">',
				this._msg, '</div>');
		if (this._closable)
			out.push('<div id="', uuid, '-cls" class="', this.$s('close'),
					'"><i id="', uuid, '-clsIcon" class="', icon, ' z-icon-times"></i></div>');
		out.push('</div>'); // not encoded to support HTML
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var type = this._type,
			s = super.domClass_(no);
		if (type)
			s += ' ' + this.$s(zUtl.encodeXML(type));
		return s;
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var p = evt.domTarget;
		if (p == this.$n('cls') || p == this.$n('clsIcon')) //may click on font-icon
			this.close();
		else
			super.doClick_(evt, popupOnly);
	}

	override onFloatUp(ctl: zk.ZWatchController, opts: zk.FireOptions): void {
		if (opts && opts.triggerByFocus) //only mouse click should close notification
			return;
		if (!this.isVisible())
			return;
		var wgt: zk.Widget | undefined = ctl.origin as zk.Widget | undefined;
		for (var floatFound = false; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			if (wgt == this.parent && wgt.ignoreDescendantFloatUp_(this))
				return;
			floatFound = floatFound ?? wgt.isFloating_();
		}
		if (!this._closable && this._dur! <= 0)
			this.close({sendOnOpen: true});
	}

	override open(ref: zk.Widget, offset?: zk.Offset, position?: string, opts?: zul.wgt.PopupOptions): void {
		super.open(ref, offset, position, opts);
		this._fixarrow(ref); //ZK-1583: modify arrow position based on reference component
		zk(this).redoCSS(-1, {'fixFontIcon': true});
	}

	override position(ref: zk.Widget, offset?: zk.Offset, position?: string, opts?: zk.PositionOptions): void {
		if (ref && !ref.$n())
			return;
		super.position(ref, offset, position, opts);
		this._fixarrow(ref); //ZK-1583: modify arrow position based on reference component
	}

	override _posInfo(ref?: zul.wgt.Ref, offset?: zk.Offset, position?: string, opts?: zul.wgt.PopupOptions): zul.wgt.PositionInfo | undefined {
		this._fixPadding(position);
		return super._posInfo(ref, offset, position, opts);
	}

	_fixPadding(position?: string): void {
		var p = this.$n('p');
		if (!p)
			return;
		var n = this.$n()!,
			pw = 2 + (zk(p).borderWidth() / 2) || 0,
			ph = 2 + (zk(p).borderHeight() / 2) || 0;

		n.style.padding = '0';
		// cache arrow direction for _fixarrow() later
		switch (position) {
		case 'before_start':
		case 'before_center':
		case 'before_end':
			this._dir = 'd';
			n.style.paddingBottom = ph + 'px';
			break;
		case 'after_start':
		case 'after_center':
		case 'after_end':
			this._dir = 'u';
			n.style.paddingTop = ph + 'px';
			break;
		case 'end_before':
		case 'end_center':
		case 'end_after':
			this._dir = 'l';
			n.style.paddingLeft = pw + 'px';
			break;
		case 'start_before':
		case 'start_center':
		case 'start_after':
			this._dir = 'r';
			n.style.paddingRight = pw + 'px';
			break;
		case 'top_left':
		case 'top_center':
		case 'top_right':
		case 'middle_left':
		case 'middle_center':
		case 'middle_right':
		case 'bottom_left':
		case 'bottom_center':
		case 'bottom_right':
		case 'overlap':
		case 'overlap_end':
		case 'overlap_before':
		case 'overlap_after':
			this._dir = 'n';
			break;
		// at_pointer, after_pointer, etc.
		default:
			this._dir = 'n';
		}
	}

	_fixarrow(ref: zk.Widget): void {
		var p = this.$n('p');
		if (!p)
			return;

		var pzcls = this.$s('pointer'),
			n = this.$n()!,
			refn = ref.$n()!,
			dir = this._dir,
			zkp = zk(p),
			pw = zkp.borderWidth(),
			ph = zkp.borderHeight(),
			nOffset = zk(n).revisedOffset(),
			refOffset = zk(refn).revisedOffset(),
			arrXOffset = (refn.offsetWidth - pw) / 2,
			arrYOffset = (refn.offsetHeight - ph) / 2;
		if (dir != 'n') {
			// positioning
			if (dir == 'u' || dir == 'd') {
				var b = dir == 'u',
					l1 = (n.offsetWidth - pw) / 2 || 0,
					l2 = refOffset[0] - nOffset[0] + arrXOffset || 0;
				p.style.left = (refn.offsetWidth >= n.offsetWidth ? l1 : l2) + 'px'; //ZK-1583: assign arrow position to reference widget if it is smaller than notification
				p.style[b ? 'top' : 'bottom'] = ((2 - ph / 2) || 0) + 'px';
				p.style[b ? 'bottom' : 'top'] = '';
			} else {
				var b = dir == 'l',
					t1 = (n.offsetHeight - ph) / 2 || 0,
					t2 = refOffset[1] - nOffset[1] + arrYOffset || 0;
				p.style.top = (refn.offsetHeight >= n.offsetHeight ? t1 : t2) + 'px'; //ZK-1583: assign arrow position to reference widget if it is smaller than notification
				p.style[b ? 'left' : 'right'] = ((2 - pw / 2) || 0) + 'px';
				p.style[b ? 'right' : 'left'] = '';
			}

			p.className = pzcls + (_dirMap[dir!] ? ' ' + this.$s(_dirMap[dir!]) : '');
			jq(p).show();

		} else {
			p.className = pzcls;
			jq(p).hide();
		}
	}

	override openAnima_(ref?: zul.wgt.Ref, offset?: zk.Offset, position?: string, opts?: zul.wgt.PopupOptions): void {
		var self = this;
		jq(this.$n()).fadeIn(500, function () {
			self.afterOpenAnima_(ref, offset, position, opts);
		});
	}

	override closeAnima_(opts?: zul.wgt.PopupOptions): void {
		var self = this;
		jq(this.$n()).fadeOut(500, function () {
			self.afterCloseAnima_(opts);
		});
	}

	override afterCloseAnima_(opts?: zul.wgt.PopupOptions): void {
		if (opts && opts.keepVisible) {
			this.setVisible(false);
			this.setFloating_(false);
			if (opts.sendOnOpen)
				this.fire('onOpen', {open: false});
		} else {
			this.detach();
		}
	}

	override getPositionArgs_(): zul.wgt.PositionArgs {
		return [this._fakeParent, undefined, this._nftPos, undefined];
	}

	override reposition(): void {
		super.reposition();
		if (this._ref) this._fixarrow(this._ref);
	}

	/**
	 * Shows a notification.
	 * TODO
	 */
	static show(msg: string, pid: string, opts: NotificationOptions): void {
		if (!opts)
			opts = {};
		var ref = opts.ref,
			pos = opts.pos,
			dur = opts.dur,
			ntf = new zul.wgt.Notification(msg, opts),
			off = opts.off,
			n: HTMLElement | undefined,
			isInView = true;

		if (ref) {
			n = ref.$n('real') || ref.$n();
			isInView = zk(n).isRealScrollIntoView();
		}

		// TODO: allow user to specify arrow direction?

		//ZK-2687, don't show notification if wgt is not in view
		if (!isInView)
			return;

		if (!pos && !off)
			pos = ref ? 'end_center' : 'middle_center';

		jq(document.body).append(ntf);
		ntf._nftPos = pos;
		ntf.open(ref!, off, pos);

		// auto dismiss
		if (dur! > 0)
			setTimeout(function () {
				if (ntf.desktop)
					ntf.close();
			}, dur);
	}
}