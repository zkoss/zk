/* Inputgroup.js

		Purpose:

		Description:

		History:
				Thu Mar 07 16:51:36 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.

*/
(function () {
/**
 * An inputgroup.
 *
 * Inspired by Bootstrap’s Input group and Button group.
 * By prepending or appending some components to the input component,
 * you can merge them like a new form-input component.
 *
 * <h3>Accepted child components</h3>
 * <ul>
 *     <li>Label</li>
 *     <li>InputElement</li>
 *     <li>LabelImageElement</li>
 * </ul>
 *
 * <p>Default {@link #getZclass}: z-inputgroup.
 *
 * @since 9.0.0
 * @author charlesqiu, rudyhuang
 */
zul.wgt.Inputgroup = zk.$extends(zul.Widget, {
	_vertical: false,

	$define: {
		/**
		 * Returns whether it is a vertical orientation.
		 * <p>Default: false
		 *
		 * @return boolean whether it is a vertical orientation
		 */
		/**
		 * Sets whether it is a vertical orientation.
		 * @param boolean vertical whether it is a vertical orientation
		 */
		vertical(v: boolean) {
			if (this.desktop) {
				jq(this.$n()).toggleClass(this.$s('vertical'), v);
			}
		}
	},

	// treat this as setVertical(boolean) for zephyr
	setOrient(orient: string) {
		this.setVertical(orient == 'vertical');
	},

	domClass_(): string {
		let classes = this.$supers('domClass_', arguments);
		return classes + (this._vertical ? ' ' + this.$s('vertical') : '');
	},
	insertChildHTML_(child: zk.Widget, before: zk.Widget, desktop: zk.Desktop) {
		if (before)
			jq(before.$n('chdex') || before.$n()).before(
				this.encloseChildHTML_({child: child}));
		else
			jq(this.getCaveNode()).append(
				this.encloseChildHTML_({child: child}));

		child.bind(desktop);
	},
	removeChildHTML_(child: zk.Widget) {
		this.$supers('removeChildHTML_', arguments);
		jq(child.uuid + '-chdex', zk).remove();
	},
	encloseChildHTML_(opts) {
		let out = opts.out || new zk.Buffer(),
			w = opts.child;
		if (!w.$instanceof(zul.wgt.Button) && !w.$instanceof(zul.wgt.Toolbarbutton)
				&& (!zul.inp || !w.$instanceof(zul.inp.InputWidget))) {
			out.push('<div id="', w.uuid, '-chdex" class="', this.$s('text'), '">');
			w.redraw(out);
			out.push('</div>');
		} else {
			w.redraw(out);
		}
		if (!opts.out) return out.join('');
	},
	beforeChildAdded_(child, insertBefore) {
		if (!child.$instanceof(zul.wgt.Label) && !child.$instanceof(zul.inp.InputWidget) && !child.$instanceof(zul.LabelImageWidget)) {
			zk.error('Unsupported child for Inputgroup: ' + child.className);
			return false;
		}
		return true;
	}
});
})();