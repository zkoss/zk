/* Group.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 10:15:04     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.grid.Group = zk.$extends(zul.grid.Row, {
	_open: true,

	$define: {
		open: function (open) {
			this._openItem(open, true);
			if (open)
				this.getGrid().stripe();
		}
	},

	getItems: function () {
		var item = [];
		for (var w = this.nextSibling; w && !w.$instanceof(zul.grid.Group); w = w.nextSibling)
			item.push(w);
		return item;
	},
	getItemCount: function () {
		return this.getItems().length;
	},
	getGroupfootIndex: function () {
	},
	getGroupfoot: function () {
	},
	getZclass: function () {
		return this._zclass != null ? this._zclass : "z-group";
	},
	getLabel: function () {
		return this.firstChild != null && this.firstChild.$instanceof(zul.wgt.Label) ?
			this.firstChild.getValue() : null;
	},
	domImage_: function () {
		var zcls = this.getZclass();
		return '<span id="' + this.uuid + '$img" class="' + zcls + '-img ' + zcls
			 + '-img-' + (this._open ? 'open' : 'close') + '"></span>';
	},
	/** A combination of image ([[#domImage_]]) and label ([[#domLabel_]]). */
	domContent_: function () {
		var label = this.getLabel(),
			img = this.domImage_();
		return label ? img + ' ': img;
	},
	isStripeable_: function () {
		return false;
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child;
		out.push('<td id="', child.uuid, '$chdextr"', this._childAttrs(child, opts.index),
				'>', '<div id="', child.uuid, '$cell" class="', opts.zclass, '-cnt ',
				opts.cls, '">');
		if (child == this.firstChild)
			out.push(this.domContent_());
		child.redraw(out);
		out.push('</div></td>');
		if (!opts.out) return out.join('');
	},
	domClick_: function (evt) {
		var toOpen = !this.isOpen(), grid = this.getGrid();
		this._openItem(toOpen);
		
		if (toOpen || grid.getModel()) {
			if (toOpen) this.parent.stripe();
			if (!grid.paging) grid.onSize(); 
				// group in paging will invalidate the whole rows.
		}
		zEvt.stop(evt);
	},
	_openItem: function (open, silent) {
		this._open = open;
		var img = this.getSubnode('img'),
			zcls = this.getZclass();
		if (img) {
			zDom[open ? "rmClass" : "addClass"](img, zcls + "-img-close");
			zDom[open ? "addClass" : "rmClass"](img, zcls + "-img-open");
		}
		this._openItemNow(open);
		if (!silent)
			this.fire('onOpen', {open: open});
				//always send since the client has to update Openable
	},
	_openItemNow: function (toOpen) {
		for (var r, w = this.nextSibling; w && (!w.$instanceof(zul.grid.Group) && !w.$instanceof(zul.grid.Groupfoot));
				w = w.nextSibling)
			if (w.isVisible())
				zDom[toOpen ? "show" : "hide"](w.getNode());
	},
	bind_: function () {		
		this.$supers('bind_', arguments);
		var n = this.getNode(),
			img = this.getSubnode("img");
		if (img)
			this.domListen_(img, "click");
		var table = n.parentNode.parentNode;
		if (table.tBodies.length > 1) {
			var span = 0;
			for (var row = table.rows[0], i = row.cells.length; --i >=0;)
				if(zDom.isVisible(row.cells[i])) span++;
			for (var cells = n.cells, i = cells.length; --i >= 0;)
				span -= cells[i].colSpan;
			if (span > 0 && n.cells.length) n.cells[n.cells.length - 1].colSpan += span;
		}
	},
	unbind_: function (skipper, after) {
		this.domUnlisten_(this.eimg, "click");
		var prev = this.getGroup();
		if (prev)
			after.push(function () {
				if (prev)
					prev._openItem(prev.isOpen(), true);
			});
		else this._openItemNow(true, true);
		this.$supers('unbind_', arguments);
	}
});
