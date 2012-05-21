/* Notification.js

	Purpose:
		
	Description:
		
	History:
		Thu Mar 15 17:12:46     2012, Created by simon

Copyright (C) 2012 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	
/**
 * A notification widget.
 * @since 6.0.1
 */
zul.wgt.Notification = zk.$extends(zul.wgt.Popup, {
	
	$init: function (msg, type, ref) {
		this.$supers(zul.wgt.Notification, '$init', arguments);
		this._msg = msg;
		this._type = type;
		this._ref = ref;
	},
	redraw: function (out) {
		var uuid = this.uuid,
			zcls = this.getZclass();
		out.push('<div', this.domAttrs_(), '><div id=', uuid, '-p class="', 
				zcls, '-pointer"></div><div id="', uuid, '-body" class="', 
				zcls, '-cl"><div id="', uuid, '-cave" class="', zcls, 
				'-cnt">', this._msg, '</div></div></div>'); // not encoded to support HTML
	},
	domClass_: function (no) {
		var zcls = this.getZclass(),
			type = this._type,
			entype,
			ref = this._ref,
			s = this.$supers(zul.wgt.Notification, 'domClass_', arguments);
		if (type)
			s += ' ' + zcls + '-' + (entype = zUtl.encodeXML(type));
		if (ref)
			s += ' ' + zcls + '-ref';
		if (zk.ie < 8 && type && ref) // need to provide extra class name for IE 6/7
			s += ' ' + zcls + '-ref-' + entype;
		return s;
	},
	open: function (ref, offset, position, opts) {
		this.$supers(zul.wgt.Notification, 'open', arguments);
		this._fixarrow(); // TODO: better place for _fixarrow
	},
	position: function (ref, offset, position, opts) {
		this.$supers(zul.wgt.Notification, 'position', arguments);
		this._fixarrow(); // TODO: better place for _fixarrow
	},
	_posInfo: function (ref, offset, position, opts) {
		this._fixPadding(position);
		return this.$supers(zul.wgt.Notification, '_posInfo', arguments);
	},
	_fixPadding: function (position) {
		var p = this.$n('p');
		if (!p)
			return;
		var n = this.$n(),
			pw = 2 + (zk(p).borderWidth() / 2) | 0,
			ph = 2 + (zk(p).borderHeight() / 2) | 0;
		
		n.style.padding = '0';
		// cache arrow direction for _fixarrow() later
		switch (position) {
		case "before_start":
		case "before_center":
		case "before_end":
			this._dir = 'd';
			n.style.paddingBottom = ph + 'px';
			break;
		case "after_start":
		case "after_center":
		case "after_end":
			this._dir = 'u';
			n.style.paddingTop = ph + 'px';
			break;
		case "end_before":
		case "end_center":
		case "end_after":
			this._dir = 'l';
			n.style.paddingLeft = pw + 'px';
			break;
		case "start_before":
		case "start_center":
		case "start_after":
			this._dir = 'r';
			n.style.paddingRight = pw + 'px';
			break;
		case "top_left":
		case "top_center":
		case "top_right":
		case "middle_left":
		case "middle_center":
		case "middle_right":
		case "bottom_left":
		case "bottom_center":
		case "bottom_right":
		case "overlap":
		case "overlap_end":
		case "overlap_before":
		case "overlap_after":
			this._dir = 'n';
			n.style.padding = this._getPaddingSize(); // for better look & feel
			break;
		// at_pointer, after_pointer, etc.
		default:
			this._dir = 'n';
		}
	},
	_getPaddingSize: function () {
		return '10px';
	},
	_fixarrow: function () {
		if (zk.ie == 6)
			return; // CSS won't work in IE 6, fall back (not showing the triangle)
		var p = this.$n('p');
		if (!p)
			return;
		
		var pzcls = this.getZclass() + '-pointer',
			n = this.$n(),
			dir = this._dir,
			pw = zk(p).borderWidth(),
			ph = zk(p).borderHeight();
		
		if (dir != 'n') {
			// positioning
			if (dir == 'u' || dir == 'd') {
				var b = dir == 'u';
				p.style.left = ((n.offsetWidth - pw) / 2 | 0) + 'px';
				p.style[b ? 'top' : 'bottom'] = ((2 - ph / 2) | 0) + 'px';
				p.style[b ? 'bottom' : 'top'] = '';
			} else {
				var b = dir == 'l';
				p.style.top = ((n.offsetHeight - ph) / 2 | 0) + 'px';
				p.style[b ? 'left' : 'right'] = ((2 - pw / 2) | 0) + 'px';
				p.style[b ? 'right' : 'left'] = '';
			}
			
			p.className = pzcls + ' ' + pzcls + '-' + dir;
			jq(p).show();
			
		} else {
			p.className = pzcls;
			jq(p).hide();
			
		}
	},
	openAnima_: function (ref, offset, position, opts) {
		var self = this;
		jq(this.$n()).fadeIn(500, function () {
			self.afterOpenAnima_(ref, offset, position, opts);
		});
	},
	closeAnima_: function (opts) {
		var self = this;
		jq(this.$n()).fadeOut(500, function () {
			self.afterCloseAnima_(opts);
		});
	},
	afterCloseAnima_: function (opts) {
		this.detach();
	},
	getZclass: function () {
		return this._zclass || "z-notification";
	}
	
}, {
	
	/**
	 * Shows a notification.
	 * TODO
	 */
	show: function (msg, pid, opts) {
		if (!opts)
			opts = {};
		var parent = zk.Widget.$(pid),
			ref = opts.ref,
			pos = opts.pos,
			ntf = new zul.wgt.Notification(msg, opts.type, ref),
			off = opts.off;
		
		// TODO: allow user to specify arrow direction?
		
		if (!pos && !off)
			pos = ref ? "end_center" : "middle_center";
		
		if (!parent) {
			// bug ZK-1136: If target page is detached, append to current active page
			parent = zk.Desktop.$().firstChild;
		}
		parent.appendChild(ntf);
		ntf.open(ref, off, pos);
		
		// auto dismiss
		if (opts.dur > 0)
			setTimeout(function () {
				if (ntf.desktop)
					ntf.close();
			}, opts.dur);
	}
	
});

})();